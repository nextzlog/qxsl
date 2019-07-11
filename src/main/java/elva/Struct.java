/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*****************************************************************************/
package elva;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LISP処理系内部で利用される不変リストの実装です。
 * 
 * 
 * @author Journal of Hamradio Informatics
 *
 * @since 2017/02/18
 */
public final class Struct extends AbstractList<Object> {
	private final Object head;
	private final Struct tail;
	private final int size;

	/**
	 * 先頭と末尾の構成要素を指定してリストを構築します。
	 *
	 * @param head 先頭の要素
	 * @param tail 末尾の要素
	 */
	public Struct(Object head, Struct tail) {
		this.head = head;
		this.tail = tail == null? NIL: tail;
		this.size = this.tail.size + 1;
	}

	/**
	 * {@link #NIL}のための専用のコンストラクタです。
	 */
	private Struct() {
		this.head = null;
		this.tail = null;
		this.size = 0;
	}

	/**
	 * 内容が空のリストを示す特別なインスタンスです。
	 */
	public static final Struct NIL = new Struct();

	/**
	 * 指定された要素を持つ引用式を構築します。
	 *
	 * @param quote 引用演算子
	 * @param value 要素
	 */
	public Struct(Quotes quote, Object value) {
		this(quote.toSymbol(), Struct.of(value));
	}

	/**
	 * 指定された要素を持つリストを構築します。
	 *
	 * @param vals 要素
	 * @return リスト 空の場合は{@link #NIL}
	 */
	public static final Struct of(Object...vals) {
		return Struct.of(Arrays.asList(vals));
	}

	/**
	 * 指定された要素を持つリストを構築します。
	 *
	 * @param vals 要素
	 * @return リスト 空の場合は{@link #NIL}
	 */
	public static final Struct of(List<Object> vals) {
		final int size = vals.size();
		if(size == 0) return Struct.NIL;
		final Object head = vals.get(0);
		return new Struct(head, of(vals.subList(1, size)));
	}

	/**
	 * このリストの先頭のコンスセルのCAR部を返します。
	 *
	 * @return CAR部
	 */
	public final Object car() {
		return this == NIL? NIL: head;
	}

	/**
	 * このリストの先頭のコンスセルのCDR部を返します。
	 *
	 * @return CDR部
	 */
	public final Struct cdr() {
		return this == NIL? NIL: tail;
	}

	/**
	 * このリストの指定された位置の要素を返します。
	 *
	 * @param index 要素の位置
	 * @return 要素
	 */
	public final Object get(int index) {
		if(index == 0) return car();
		if(index >= 0) return tail.get(index -1);
		final String msg = String.valueOf(index);
		throw new IndexOutOfBoundsException(msg);
	}

	/**
	 * このリストの要素数を返します。
	 *
	 * @return 要素数
	 */
	public final int size() {
		return this.size;
	}

	/**
	 * このリストの文字列による表現を返します。
	 *
	 * @return 文字列表現
	 */
	@Override
	public final String toString() {
		Stream<String> strm = stream().map(Objects::toString);
		return strm.collect(Collectors.joining(" ", "(", ")"));
	}
}
