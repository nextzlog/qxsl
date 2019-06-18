/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package qxsl.sheet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 交信記録を提出書類に変換する書式はこのインターフェースを実装します。
 *
 *
 * @author Journal of Hamradio Informatics
 * 
 * @since 2017/03/11
 *
 */
public interface SheetFormat {
	/**
	 * この書式を識別する完全な名前を返します。
	 * 
	 * @return 書式の名前
	 */
	public String getName();

	/**
	 * この書式のUIへの表示に適した文字列を返します。
	 * 
	 * @return 書式のUI文字列
	 */
	public String toString();

	/**
	 * この書式の詳細を説明する複数行の文字列を返します。
	 * 
	 * @return 書式の説明
	 */
	public String getDescription();

	/**
	 * この書式を適用するファイル名拡張子の不変のリストを返します。
	 * 
	 * @return ファイル名拡張子のリスト
	 */
	public List<String> getExtensions();

	/**
	 * この書式で交信記録を抽出する際に鍵として使用する文字列を返します。
	 *
	 * @return 交信記録を指す鍵
	 */
	public String getTableKey();

	/**
	 * 指定したストリームをこの書式でデコードして提出書類を読み込みます。
	 * 
	 * @param reader 提出書類を読み込むストリーム
	 * @return 提出書類
	 * @throws IOException 入出力時の例外
	 */
	public Map<String, String> decode(InputStream reader) throws IOException;

	/**
	 * この書式でエンコードした提出書類を指定したストリームに書き込みます。
	 * 
	 * @param writer 提出書類を書き込むストリーム
	 * @param map 出力する提出書類
	 * @throws IOException 入出力時の例外
	 */
	public void encode(OutputStream writer, Map<String, String> map) throws IOException;
}
