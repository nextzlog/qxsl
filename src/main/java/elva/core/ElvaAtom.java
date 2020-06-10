/*******************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package elva.core;

import java.util.Objects;

/**
 * LISP処理系で使用される単純型の共通実装です。
 *
 *
 * @param <V> 値の総称型
 *
 * @author 無線部開発班
 *
 * @since 2020/06/04
 */
public abstract class ElvaAtom<V> extends ElvaNode {
	/**
	 * この式の値を処理系の外部に渡す際に使用します。
	 *
	 * @return 値
	 */
	public abstract V value();

	/**
	 * このアトムのハッシュ値を返します。
	 *
	 * @return ハッシュ値
	 */
	@Override
	public final int hashCode() {
		return Objects.hashCode(value());
	}
}
