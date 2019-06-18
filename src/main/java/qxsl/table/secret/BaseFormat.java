/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package qxsl.table.secret;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import qxsl.table.TableFormat;

/**
 * 書式の説明を設定ファイルから取得する仕組みを提供します。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2019/06/16
 *
 */
public abstract class BaseFormat implements TableFormat {
	private final String name;
	private final Properties conf;

	/**
	 * 指定した名前の書式を構築します。
	 *
	 * @param name 書式の名前
	 */
	public BaseFormat(String name) {
		this.name = name;
		this.conf = new Properties();
		String file = String.format("%s.xml", name);
		URL url = this.getClass().getResource(file);
		try(InputStream is = url.openStream()) {
			conf.loadFromXML(is);
		} catch(IOException ex) {}
	}

	/**
	 * この書式を識別する完全な名前を返します。
	 * 
	 * @return 書式の名前
	 */
	@Override
	public final String getName() {
		return this.name;
	}

	/**
	 * この書式のUIへの表示に適した文字列を返します。
	 * 
	 * @return 書式のUI文字列
	 */
	@Override
	public final String toString() {
		return conf.getProperty("label");
	}

	/**
	 * この書式の詳細を説明する複数行の文字列を返します。
	 * 
	 * @return 書式の説明
	 */
	@Override
	public final String getDescription() {
		return conf.getProperty("description");
	}

	/**
	 * この書式を適用するファイル名拡張子の不変のリストを返します。
	 * 
	 * @return ファイル名拡張子のリスト
	 */
	@Override
	public final List<String> getExtensions() {
		String[] exts = conf.getProperty("extensions").split(",");
		return Collections.unmodifiableList(Arrays.asList(exts));
	}
}
