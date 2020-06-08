/*******************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package elva.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import static javax.script.ScriptEngine.ENGINE;
import static javax.script.ScriptEngine.ENGINE_VERSION;
import static javax.script.ScriptEngine.LANGUAGE;
import static javax.script.ScriptEngine.LANGUAGE_VERSION;
import static javax.script.ScriptEngine.NAME;

/**
 * 無線部開発班が実装するLISP処理系のインスタンスを提供します。
 *
 *
 * @author 無線部開発班
 *
 * @since 2020/06/07
 */
public final class ElvaFactory implements ScriptEngineFactory {
	private final Properties conf;

	/**
	 * 各種設定を読み出してファクトリを構築します。
	 *
	 * @throws UncheckedIOException 設定の読み出し時の例外
	 */
	public ElvaFactory() throws UncheckedIOException {
		this.conf = new Properties();
		install("elva.xml");
	}

	/**
	 * 指定された名前の設定ファイルを読み出します。
	 *
	 * @param name 設定ファイルの名前
	 *
	 * @throws UncheckedIOException 設定の読み出し時の例外
	 */
	private final void install(String name) {
		final var type = getClass();
		final var path = type.getResource(name);
		try(InputStream is = path.openStream()) {
			conf.loadFromXML(is);
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * このエンジンの名前を返します。
	 *
	 * @return エンジンの名前
	 */
	@Override
	public String getEngineName() {
		return getParameter(ENGINE);
	}

	/**
	 * このエンジンの改訂番号を返します。
	 *
	 * @return エンジンの改訂番号
	 */
	@Override
	public String getEngineVersion() {
		return getParameter(ENGINE_VERSION);
	}

	/**
	 * このエンジンが対応する言語の名前を返します。
	 *
	 * @return 言語の名前
	 */
	@Override
	public String getLanguageName() {
		return getParameter(LANGUAGE);
	}

	/**
	 * このエンジンが対応する言語の改訂番号を返します。
	 *
	 * @return 言語の改訂番号
	 */
	@Override
	public String getLanguageVersion() {
		return getParameter(LANGUAGE_VERSION);
	}

	/**
	 * このエンジンの名前のリストを返します。
	 *
	 * @return 名前のリスト
	 */
	@Override
	public List<String> getNames() {
		return Arrays.asList(getEngineName());
	}

	/**
	 * このエンジンが対応する媒体のリストを返します。
	 *
	 * @return 媒体のリスト
	 */
	@Override
	public List<String> getMimeTypes() {
		return Arrays.asList();
	}

	/**
	 * このエンジンが対応する拡張子のリストを返します。
	 *
	 * @return 拡張子のリスト
	 */
	@Override
	public List<String> getExtensions() {
		return Arrays.asList("lisp");
	}

	/**
	 * このエンジンの指定された属性の設定値を返します。
	 *
	 * @param name 属性の名前
	 * @return 設定値
	 */
	@Override
	public String getParameter(String name) {
		return conf.getProperty(name);
	}

	/**
	 * エンジンのインスタンスを生成します。
	 *
	 * @return エンジン
	 */
	@Override
	public ElvaRuntime getScriptEngine() {
		return new ElvaRuntime();
	}

	/**
	 * 指定された式を順番に実行する式を返します。
	 *
	 * @param list 式のリスト
	 * @return 式
	 */
	public String getProgram(String...list) {
		final var join = new StringJoiner(" ");
		for(String state: list) join.add(state);
		return String.format("(block %s)", join);
	}

	/**
	 * 指定された文字列を標準出力に書き出す式を返します。
	 *
	 * @param text 書き出す文字列
	 * @return 式
	 */
	@Override
	public String getOutputStatement(String text) {
		return String.format("(print %s)", text);
	}

	/**
	 * 指定されたメソッドを実行する式を返します。
	 *
	 * @param obj 対象となるオブジェクト
	 * @param met メソッドの名前
	 * @param seq 引数
	 */
	@Override
	public String getMethodCallSyntax(String obj, String met, String...seq) {
		final String acc = String.format("(access (type %s) '%s)", obj, met);
		return String.format("(%s %s %s)", acc, obj, String.join(" ", seq));
	}
}
