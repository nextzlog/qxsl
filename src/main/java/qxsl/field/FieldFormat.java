/*****************************************************************************
 * Amateur Radio Operational Logging Library 'xsum' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package qxsl.field;

import javax.xml.namespace.QName;
import qxsl.model.Field;

/**
 * {@link Field}と文字列による表現を相互に変換する仕組みを提供します。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2015/08/05
 * 
 */
public interface FieldFormat {
	/**
	 * 対応する属性の名前を返します。
	 * 
	 * @return 属性の名前
	 */
	public QName target();

	/**
	 * 文字列から{@link Field}のインスタンスを構築します。
	 * 
	 * @param value 属性値を表す文字列
	 * @return 生成された属性値
	 */
	public Field decode(String value);

	/**
	 * 指定された属性値を文字列に変換して永続化します。
	 * 
	 * @param field 永続化する属性値
	 * @return 文字列化された属性値
	 */
	public String encode(Field field);
}