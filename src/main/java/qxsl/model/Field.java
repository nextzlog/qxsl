/*******************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package qxsl.model;

import java.util.Objects;
import javax.xml.namespace.QName;

import qxsl.field.FieldFormats.Any;

/**
 * 交信に付与される属性の共通実装です。
 *
 *
 * @author 無線部開発班
 *
 * @since 2015/08/05
 *
 * @param <V> 属性の属性値の総称型
 */
public abstract class Field<V> {
	private final QName qname;

	/**
	 * 指定された名前の属性を構築します。
	 *
	 * @param qname 属性の名前
	 */
	public Field(QName qname) {
		this.qname = qname;
	}

	/**
	 * この属性の名前を返します。
	 *
	 * @return 属性の名前
	 */
	public final QName name() {
		return qname;
	}

	/**
	 * この属性の値を返します。
	 *
	 * @return 属性の値
	 */
	public abstract V value();

	/**
	 * この属性が{@link Any}か確認します。
	 *
	 * @return Anyの場合は真
	 */
	public final boolean isAny() {
		return this instanceof Any;
	}

	/**
	 * 属性値のハッシュ値を計算します。
	 *
	 * @return ハッシュ値
	 */
	@Override
	public final int hashCode() {
		return Objects.hash(name(), value());
	}

	/**
	 * 指定された属性と等値であるか確認します。
	 *
	 *
	 * @param obj 比較する属性
	 *
	 * @return 同じ情報を保持する属性は真
	 */
	@Override
	public boolean equals(Object obj) {
		if(!getClass().isInstance(obj)) return false;
		return value().equals(((Field) obj).value());
	}

	/**
	 * 属性値を文字列で返します。
	 *
	 * @return 文字列
	 */
	@Override
	public String toString() {
		return String.valueOf(value());
	}
}
