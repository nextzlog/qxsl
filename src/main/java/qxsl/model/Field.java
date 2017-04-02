/*****************************************************************************
 * Amateur Radio Operational Logging Library 'xsum' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package qxsl.model;

import java.util.Objects;
import javax.xml.namespace.QName;

/**
 * 交信記録シートにおいて各属性の値を表現します。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2015/08/05
 * 
 * @param <V> 属性の属性値の総称型
 */
public abstract class Field<V> {
	private final QName type;

	/**
	 * 指定した属性名を持つ属性を構築します。
	 * 
	 * @param type 属性名
	 */
	public Field(QName type) {
		this.type = type;
	}

	/**
	 * この属性の名前を返します。
	 *
	 * @return 属性の名前
	 */
	public QName type() {
		return type;
	}

	/**
	 * この属性の値を返します。
	 * 
	 * @return 属性の値
	 */
	public abstract V value();

	/**
	 * {@link #value()}の返り値を文字列で返します。
	 * 
	 * @return 文字列
	 */
	@Override
	public String toString() {
		return String.valueOf(value());
	}

	/**
	 * {@link #value()}の返り値をハッシュ値にして返します。
	 * 
	 * @return ハッシュ値
	 */
	@Override
	public int hashCode() {
		return Objects.hash(type(), value());
	}

	/**
	 * 指定されたオブジェクトと等値であるか確認します。
	 * 対象のオブジェクトが{@link Field}であり、
	 * 双方の{@link #value()}の返り値が等価な場合、
	 * trueを返します。
	 * 
	 * @param obj 比較するオブジェクト
	 * @return この属性と等しい場合true
	 */
	@Override
	public boolean equals(Object obj) {
		if(getClass().isInstance(obj)) {
			Field nobj = (Field) obj;
			Object mine = this.value();
			Object your = nobj.value();
			return mine.equals(your);
		}
		return false;
	}
}
