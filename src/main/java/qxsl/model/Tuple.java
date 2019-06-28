/*****************************************************************************
 * Amateur Radio Operational Logging Library 'xsum' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package qxsl.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.xml.namespace.QName;
import qxsl.field.FieldMappers;

/**
 * {@link Item}クラスや{@link Exch}クラスはこのクラスを実装します。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2015/08/05
 *
 */
public abstract class Tuple implements Iterable<Field> {
	private static final FieldMappers maps = new FieldMappers();
	private final Map<QName, Field> table;
	private final QName name;

	/**
	 * 指定した名前の空の{@link Tuple}を構築します。
	 * 
	 * @param name 名前
	 */
	public Tuple(QName name) {
		this.name = name;
		table = new HashMap<>();
	}

	/**
	 * この{@link Tuple}の名前を返します。
	 *
	 * @return タプルの名前
	 */
	public final QName name() {
		return name;
	}

	/**
	 * この{@link Tuple}のハッシュ値を計算します。
	 * 
	 * @return ハッシュ値
	 */
	@Override
	public int hashCode() {
		int val = Objects.hash(name(), table);
		if(this instanceof Item) {
			final Rcvd rcvd = ((Item) this).getRcvd();
			final Sent sent = ((Item) this).getSent();
			val = val * 31 + Objects.hash(rcvd, sent);
		}
		return val;
	}

	/**
	 * 指定されたオブジェクトと等値であるか確認します。
	 * 
	 * @param obj 比較するオブジェクト
	 * @return このタプルと等しい場合true
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Tuple)) return false;
		return table.equals(((Tuple) obj).table);
	}

	/**
	 * この要素に設定された全ての属性をイテレータで反復します。
	 * 隷下の要素に設定された属性はイテレーターに含まれません。
	 * 
	 * @return 全ての属性を列挙したイテレータ
	 */
	@Override
	public final Iterator<Field> iterator() {
		return table.values().iterator();
	}

	/**
	 * この要素の隷下にある全ての要素をイテレータで返します。
	 * 
	 * @return 全ての要素を列挙した反復子
	 */
	public abstract Iterator<Tuple> children();

	/**
	 * 指定した属性をこの{@link Tuple}に追加します。
	 * 
	 * @param field 追加する属性
	 * @return このタプル
	 * @throws NullPointerException 属性がnullの場合
	 */
	public final Tuple add(Field field) {
		table.put(field.name(), field);
		return this;
	}

	/**
	 * 指定した{@link QName}に対応する属性を返します。
	 * 
	 * @param name 属性の名前
	 * @return 設定されている属性
	 */
	public final Field get(QName name) {
		Field field = table.get(name);
		if(field != null) return field;
		return maps.search(name, this);
	}

	/**
	 * 指定した{@link QName}に対応する属性の値を返します。
	 * 
	 * @param name 属性の名前
	 * @return 設定されている属性の値
	 */
	public final Object value(QName name) {
		final Field field = this.get(name);
		return field != null? field.value(): null;
	}

	/**
	 * 指定した{@link QName}に対応する属性を削除します。
	 * 
	 * @param qname 属性の名前
	 * @return このタプル
	 */
	public final Tuple remove(QName qname) {
		table.remove(qname);
		return this;
	}
}
