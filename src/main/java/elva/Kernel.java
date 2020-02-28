/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*****************************************************************************/
package elva;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import elva.ElvaLisp.ElvaRuntimeException;

/**
 * LISP処理系のスコープ付きの評価器の実装です。
 * 
 * 
 * @author Journal of Hamradio Informatics
 *
 * @since 2017/02/18
 */
public final class Kernel {
	/**
	 * この評価器に関連づけられたスコープです。
	 */
	public final Nested scope;

	/**
	 * 指定されたスコープに対する評価器を構築します。
	 *
	 * @param scope 評価器のスコープ
	 */
	public Kernel(Nested scope) {
		this.scope = scope;
	}

	/**
	 * 指定された式の値を求めて{@link Object}として返します。
	 *
	 * @param sexp 式
	 * @return 返り値 nullの場合は例外を発生させる
	 *
	 * @throws ElvaRuntimeException 評価により発生した例外
	 */
	public Object some(Object sexp) {
		return eval(sexp, Object.class);
	}

	/**
	 * 指定された式の値を求めて{@link Symbol}として返します。
	 *
	 * @param sexp 式
	 * @return 返り値
	 *
	 * @throws ElvaRuntimeException 評価により発生した例外
	 */
	public Symbol name(Object sexp) {
		return eval(sexp, Symbol.class);
	}

	/**
	 * 指定された式の値を求めて{@link String}として返します。
	 *
	 * @param sexp 式
	 * @return 返り値
	 *
	 * @throws ElvaRuntimeException 評価により発生した例外
	 */
	public String text(Object sexp) {
		return eval(sexp, String.class);
	}

	/**
	 * 指定された式の値を求めて{@link Struct}として返します。
	 *
	 * @param sexp 式
	 * @return 返り値
	 *
	 * @throws ElvaRuntimeException 評価により発生した例外
	 */
	public Struct list(Object sexp) {
		return eval(sexp, Struct.class);
	}

	/**
	 * 指定された式の値を求めて真偽値として返します。
	 *
	 * @param sexp 式
	 * @return 返り値
	 *
	 * @throws ElvaRuntimeException 評価により発生した例外
	 */
	public boolean bool(Object sexp) {
		return eval(sexp, Boolean.class);
	}

	/**
	 * 指定された式の値を求めて実数値として返します。
	 *
	 * @param sexp 式
	 * @return 返り値
	 *
	 * @throws ElvaRuntimeException 評価により発生した例外
	 */
	public BigDecimal real(Object sexp) {
		final var val = eval(sexp, Number.class);
		final var lg = val instanceof Long;
		if(val instanceof BigDecimal) return (BigDecimal) val;
		if(lg) return BigDecimal.valueOf(val.longValue());
		else return BigDecimal.valueOf(val.doubleValue());
	}

	/**
	 * 指定された式の値を求めます。
	 *
	 * @param sexp 式
	 * @return 返り値
	 *
	 * @throws ElvaRuntimeException 評価により発生した例外
	 */
	public Object eval(Object sexp) {
		if (Struct.NIL.equals(sexp)) return Struct.NIL;
		if (sexp instanceof Struct) return call((Struct) sexp);
		return (sexp instanceof Symbol)? scope.get(sexp): sexp;
	}

	/**
	 * 指定された式の値を求め、値が指定された型であるか検査します。
	 *
	 * @param sexp 式
	 * @param type 型
	 * @return 返り値
	 *
	 * @param <V> 返り値の総称型
	 *
	 * @throws ElvaRuntimeException 評価により発生した例外
	 */
	public <V> V eval(Object sexp, Class<V> type) {
		final Object value = eval(sexp);
		@SuppressWarnings("unchecked")
		final V valid = type.isInstance(value)? (V) value: null;
		final String temp = "%s instance required but %s found";
		if (valid != null) return valid;
		throw new ElvaRuntimeException(temp, type, value).add(sexp);
	}

	/**
	 * 指定された式を関数適用として評価した値を返します。
	 *
	 * @param sexp 式
	 * @return 返り値
	 *
	 * @throws ElvaRuntimeException 評価により発生した例外
	 */
	public final Object call(Struct sexp) {
		try {
			final Function func = eval(sexp.car(), Function.class);
			return valid(func, sexp.cdr()).apply(sexp.cdr(), this);
		} catch (ElvaRuntimeException ex) {
			throw ex.add(sexp);
		}
	}

	/**
	 * 準引用の部分解除を表現する識別子です。
	 */
	private final Symbol UQUOT = Quotes.UQUOT.toSymbol();

	/**
	 * 準引用の部分展開を表現する識別子です。
	 */
	private final Symbol UQSPL = Quotes.UQSPL.toSymbol();

	/**
	 * 準引用の被引用式にて引用の部分解除を示す内部オブジェクトです。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @sicne 2020/02/26
	 */
	static interface Unquote {
		public void addAll(List<Object> seq);
		public Object sexp();
	}

	/**
	 * 準引用の被引用式にて通常の引用解除を示す内部オブジェクトです。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @sicne 2020/02/26
	 */
	private static final class Normal implements Unquote {
		public final Object sexp;
		public Normal(Object sexp) {
			this.sexp = sexp;
		}
		@Override
		public void addAll(List<Object> seq) {
			seq.add(this.sexp);
		}
		@Override
		public Object sexp() {
			return sexp;
		}
	}

	/**
	 * 準引用の被引用式にてリストの継足しを示す内部オブジェクトです。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @sicne 2020/02/26
	 */
	private static final class Splice implements Unquote {
		public final List<Object> sexp;
		public Splice(List<Object> sexp) {
			this.sexp = sexp;
		}
		@Override
		public void addAll(List<Object> seq) {
			seq.addAll(this.sexp);
		}
		@Override
		public Object sexp() {
			return sexp;
		}
	}

	/**
	 * 指定された式を準引用の被引用式として評価した値を返します。
	 *
	 * @param quoted 式
	 * @return 返り値
	 *
	 * @throws ElvaRuntimeException 評価により発生した例外
	 */
	final Unquote uquote(Object quoted) {
		if (quoted instanceof Struct) {
			final Struct seq = (Struct) quoted;
			if (Struct.NIL.equals(seq)) return new Normal(Struct.NIL);
			if (UQUOT.equals(seq.car())) return new Normal(eval(seq));
			if (UQSPL.equals(seq.car())) return new Splice(list(seq));
			final ArrayList<Object> list = new ArrayList<>();
			for (Object sexp: seq) uquote(sexp).addAll(list);
			return new Normal(Struct.of(list));
		} else return new Normal(quoted);
	}

	/**
	 * 実引数の個数を検査して必要なら例外を発生させます。
	 *
	 * @param func 演算子
	 * @param list 引数の式
	 * @return 対象の演算子
	 *
	 * @throws ElvaRuntimeException 引数の個数が誤っている場合
	 */
	private final Function valid(Function func, Struct list) {
		String temp = "%s requires at-least %d and at-most %d arguments";
		final Params annon = func.getClass().getAnnotation(Params.class);
		final int len = list.size();
		final int min = annon.min() >= 0? annon.min(): Integer.MAX_VALUE;
		final int max = annon.max() >= 0? annon.max(): Integer.MAX_VALUE;
		if (min <= len && len <= max) return func;
		throw new ElvaRuntimeException(temp, func, min, max);
	}
}
