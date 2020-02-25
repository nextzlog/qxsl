/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*****************************************************************************/
package qxsl.ruler;

import java.io.Reader;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import javax.script.*;
import javax.xml.namespace.QName;

import elva.*;

import qxsl.extra.field.City;
import qxsl.extra.field.Time;
import qxsl.model.Exch;
import qxsl.model.Item;
import qxsl.model.Tuple;

import static javax.script.ScriptContext.ENGINE_SCOPE;

/**
 * {@link Contest}をLISPベースのドメイン特化言語で表現する仕組みです。
 * 
 * 
 * @author Journal of Hamradio Informatics
 *
 * @since 2017/02/27
 *
 * @see ElvaLisp 内部で使用されるLISP処理系
 */
public final class RuleKit {
	private final ElvaLisp elva;
	private final ScriptContext context;

	/**
	 * LISP処理系を構築します。
	 */
	public RuleKit() {
		this.elva = new ElvaLisp();
		context = new SimpleScriptContext();
		context.setBindings(createBindings(), ENGINE_SCOPE);
	}

	/**
	 * 指定された入力から文字列を読み取り評価します。
	 * 返り値はコンテストの定義である必要があります。
	 * 
	 * 
	 * @param reader 式を読み取るリーダ
	 * @return コンテストの定義
	 *
	 * @throws ClassCastException 返り値が不正な型の場合
	 * @throws ScriptException 式の評価時に発生する例外
	 */
	public Contest eval(Reader reader) throws ScriptException {
		return (Contest) this.elva.eval(reader, this.context);
	}

	/**
	 * 指定された入力から文字列を読み取り評価します。
	 * 返り値はコンテストの定義である必要があります。
	 * 
	 * 
	 * @param string 式を読み取る文字列
	 * @return コンテストの定義
	 *
	 * @throws ClassCastException 返り値が不正な型の場合
	 * @throws ScriptException 式の評価時に発生する例外
	 */
	public Contest eval(String string) throws ScriptException {
		return (Contest) this.elva.eval(string, this.context);
	}

	/**
	 * LISP処理系内部における{@link Contest}の実装です。
	 * 
	 * 
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2017/02/20
	 */
	private static final class ContestImpl extends Contest {
		private final String name;
		private final List<Section> list;

		/**
		 * 指定された規約定義と評価器で規約を構築します。
		 *
		 * @param rule 規約
		 * @param eval 評価器
		 */
		public ContestImpl(Struct rule, Kernel eval) {
			this.name = eval.text(rule.car());
			this.list = SectionImpl.map(rule.cdr(), eval);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public java.util.Iterator<Section> iterator() {
			return list.iterator();
		}
	}

	/**
	 * LISP処理系内部における{@link Section}の実装です。
	 * 
	 * 
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2017/02/20
	 */
	private static final class SectionImpl extends Section {
		private final String name;
		private final String code;
		private final Kernel eval;
		private final Function rule;

		/**
		 * 指定された規約定義と評価器で部門を構築します。
		 *
		 * @param rule 規約
		 * @param eval 評価器
		 */
		public SectionImpl(Struct rule, Kernel eval) {
			this.name = eval.text(rule.get(0));
			this.code = eval.text(rule.get(1));
			this.rule = eval.eval(rule.get(2), Function.class);
			this.eval = eval;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getCode() {
			return code;
		}

		@Override
		public Message validate(Item item) throws ScriptException {
			try {
				final Object sexp = eval.eval(Struct.of(rule, item));
				if(sexp instanceof Success) return (Success) sexp;
				if(sexp instanceof Failure) return (Failure) sexp;
				String temp = "%s must return a success or failure";
				throw new ScriptException(String.format(temp, rule));
			} catch(ElvaLisp.ElvaRuntimeException ex) {
				throw ex.toScriptException();
			}
		}

		/**
		 * 指定されたリストを部門の列挙として評価します。
		 *
		 * @param rule 規約
		 * @param eval 評価器
		 * @return 部門のリスト
		 */
		private static List<Section> map(Struct rule, Kernel eval) {
			final ArrayList<Section> list = new ArrayList<Section>();
			for(var sc: rule) list.add(eval.eval(sc, Section.class));
			return Collections.unmodifiableList(list);
		}
	}

	/**
	 * LISP処理系で事前に定義された関数や値を保持する環境を作成します。
	 *
	 * @return 事前に定義された環境
	 */
	public Bindings createBindings() {
		final Nested lude = new Nested(null, null);
		/*
		 * preinstalled functions for contest & section definition
		 * 
		 * (contest contest-name sections...)
		 * (special section-name section-code lambda)
		 */
		lude.put(new $Contest());
		lude.put(new $Section());

		/*
		 * preinstalled functions for success & failure construction
		 * 
		 * (success ITEM score keys...)
		 * (failure ITEM message)
		 */
		lude.put(new $Success());
		lude.put(new $Failure());

		/*
		 * preinstalled functions for rcvd & sent access
		 *
		 * (rcvd item)
		 * (sent item)
		 */
		lude.put(new $Rcvd());
		lude.put(new $Sent());

		/*
		 * preinstalled functions for field access
		 *
		 * (get-field item namespace name)
		 * (set-field item namespace name value-string)
		 */
		lude.put(new $GetField());
		lude.put(new $SetField());

		/*
		 * preinstalled functions for time access
		 *
		 * (hour item)
		 */
		lude.put(new $Hour());

		/*
		 * preinstalled functions for city access
		 *
		 * (city database-name code region-level)
		 */
		lude.put(new $City());
		return lude;
	}

	/**
	 * LISP処理系で事前に定義されるcontest関数です。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/05/15
	 */
	@Native("contest")
	@Params(min = 1, max = -1)
	private static final class $Contest extends Function {
		public Object apply(Struct args, Kernel eval) {
			return new ContestImpl(args, eval);
		}
	}

	/**
	 * LISP処理系で事前に定義されるsection関数です。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/05/15
	 */
	@Native("section")
	@Params(min = 3, max = 3)
	private static final class $Section extends Function {
		public Object apply(Struct args, Kernel eval) {
			return new SectionImpl(args, eval);
		}
	}

	/**
	 * LISP処理系で事前に定義されるsuccess関数です。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/05/18
	 */
	@Native("success")
	@Params(min = 3, max = -1)
	private static final class $Success extends Function {
		public Object apply(Struct args, Kernel eval) {
			final List<Object> ks = new ArrayList<>();
			final Item item = eval.eval(args.car(), Item.class);
			final int score = eval.real(args.get(1)).intValueExact();
			for(Object key: args.cdr().cdr()) ks.add(eval.eval(key));
			return new Success(score, item, ks.toArray());
		}
	}

	/**
	 * LISP処理系で事前に定義されるfailure関数です。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/05/18
	 */
	@Native("failure")
	@Params(min = 2, max = 2)
	private static final class $Failure extends Function {
		public Object apply(Struct args, Kernel eval) {
			final Item item = eval.eval(args.car(), Item.class);
			return new Failure(eval.text(args.get(1)), item);
		}
	}

	/**
	 * LISP処理系で事前に定義されるrcvd関数です。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/05/18
	 */
	@Native("rcvd")
	@Params(min = 1, max = 1)
	private static final class $Rcvd extends Function {
		public Object apply(Struct args, Kernel eval) {
			return eval.eval(args.car(), Item.class).getRcvd();
		}
	}

	/**
	 * LISP処理系で事前に定義されるsent関数です。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/05/18
	 */
	@Native("sent")
	@Params(min = 1, max = 1)
	private static final class $Sent extends Function {
		public Object apply(Struct args, Kernel eval) {
			return eval.eval(args.car(), Item.class).getSent();
		}
	}

	/**
	 * LISP処理系で事前に定義されるget-field関数です。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/06/29
	 */
	@Native("get-field")
	@Params(min = 3, max = 3)
	private static final class $GetField extends Function {
		public Object apply(Struct args, Kernel eval) {
			final Tuple tuple = eval.eval(args.car(), Tuple.class);
			final String space = eval.text(args.get(1));
			final String local = eval.text(args.get(2));
			return tuple.value(new QName(space, local));
		}
	}

	/**
	 * LISP処理系で事前に定義されるset-field関数です。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/06/29
	 */
	@Native("set-field")
	@Params(min = 4, max = 4)
	private static final class $SetField extends Function {
		public Object apply(Struct args, Kernel eval) {
			final Tuple tuple = eval.eval(args.car(), Tuple.class);
			final String space = eval.text(args.get(1));
			final String local = eval.text(args.get(2));
			final Object value = eval.eval(args.get(3));
			return tuple.set(new QName(space, local), value.toString());
		}
	}

	/**
	 * LISP処理系で事前に定義されるhour関数です。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/05/18
	 */
	@Native("hour")
	@Params(min = 2, max = 2)
	private static final class $Hour extends Function {
		public Object apply(Struct args, Kernel eval) {
			ZonedDateTime time = eval.eval(args.car(), ZonedDateTime.class);
			ZoneId id = ZoneId.of(eval.text(args.get(1)), ZoneId.SHORT_IDS);
			return BigDecimal.valueOf(time.withZoneSameInstant(id).getHour());
		}
	}

	/**
	 * LISP処理系で事前に定義されるcity関数です。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/05/18
	 */
	@Native("city")
	@Params(min = 2, max = 3)
	private static final class $City extends Function {
		public Object apply(Struct args, Kernel eval) {
			final String base = eval.text(args.car());
			final String code = eval.text(args.get(1));
			final City city = City.forCode(base, code);
			if(city == null) return null;
			if(args.size() == 2) return city.getFullName();
			final BigDecimal lv = eval.real(args.get(2));
			return city.getFullPath().get(lv.intValueExact());
		}
	}
}
