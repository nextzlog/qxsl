/*******************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package qxsl.sheet;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.StringJoiner;

/**
 * 要約書類の書式をクラスパスから自動的に検出して管理します。
 *
 *
 * @author 無線部開発班
 *
 * @since 2017/03/11
 */
public final class SheetManager implements Iterable<SheetFactory> {
	private final ServiceLoader<SheetFactory> list;

	/**
	 * インスタンスを構築します。
	 *
	 *
	 * @see ServiceLoader#load(Class)
	 */
	public SheetManager() {
		this(SheetManager.class.getClassLoader());
	}

	/**
	 * 指定されたローダから書式の実装を検索します。
	 *
	 *
	 * @param cl 書式の実装を検出するクラスローダ
	 */
	public SheetManager(ClassLoader cl) {
		this.list = ServiceLoader.load(SheetFactory.class, cl);
	}

	/**
	 * このインスタンスが検出した書式を列挙します。
	 *
	 *
	 * @return 書式のイテレータ
	 */
	@Override
	public final Iterator<SheetFactory> iterator() {
		return list.iterator();
	}

	/**
	 * 指定された名前もしくはラベルを持つ書式の実装を検索します。
	 *
	 *
	 * @param name 属性の名前
	 *
	 * @return 対応する書式 またはnull
	 */
	public final SheetFactory factory(String name) {
		for(var f: list) if(f.type().equals(name)) return f;
		for(var f: list) if(f.name().equals(name)) return f;
		for(var f: list) for(var ext: f.extensions()) {
			if(ext.equalsIgnoreCase(name)) return f;
		}
		return null;
	}

	/**
	 * 指定されたバイト列が表す要約書類から交信記録を抽出します。
	 *
	 *
	 * @param binary 要約書類を読み込むバイト列
	 *
	 * @return 抽出された交信記録
	 *
	 * @throws UncheckedIOException 読み込み時の例外
	 */
	public final byte[] unpack(byte[] binary) {
		final var join = new StringJoiner("\n");
		for(var f: this) try {
			return f.unpack(binary);
		} catch (Exception ex) {
			join.add(f.name().concat(":"));
			join.add(ex.toString());
		}
		final var ms = join.toString();
		final var ex = new IOException(ms);
		throw new UncheckedIOException(ex);
	}

	/**
	 * 指定された文字列が表す要約書類から交信記録を抽出します。
	 *
	 *
	 * @param string 要約書類を読み込む文字列
	 *
	 * @return 抽出された交信記録
	 *
	 * @throws UncheckedIOException 読み込み時の例外
	 */
	public final byte[] unpack(String string) {
		final var join = new StringJoiner("\n");
		for(var f: this) try {
			return f.unpack(string);
		} catch (Exception ex) {
			join.add(f.name().concat(":"));
			join.add(ex.toString());
		}
		final var ms = join.toString();
		final var ex = new IOException(ms);
		throw new UncheckedIOException(ex);
	}
}