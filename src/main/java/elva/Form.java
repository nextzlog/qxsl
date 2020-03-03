/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*****************************************************************************/
package elva;

import java.util.function.BiFunction;

/**
 * LISP処理系でシンボルが参照する関数やマクロの実体を表現します。
 *
 *
 * @author 無線部開発班
 *
 * @since 2019/05/15
 */
public abstract class Form implements BiFunction<Cons, Eval, Object> {
	/**
	 * 関数もしくはマクロを構築します。
	 */
	public Form() {}

	/**
	 * 指定された実引数と評価器に対し、返り値を求めます。
	 *
	 * @param args 実引数
	 * @param eval 評価器
	 * @return 返り値
	 */
	public abstract Object apply(Cons args, Eval eval);

	/**
	 * この関数の名前を注釈{@link Native}経由で返します。
	 *
	 * @return 関数の名前
	 */
	@Override
	public String toString() {
		Native annon = getClass().getAnnotation(Native.class);
		return annon != null? annon.value(): super.toString();
	}
}