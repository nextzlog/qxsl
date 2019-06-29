/*****************************************************************************
 * Amateur Radio Operational Logging Library 'xsum' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package qxsl.field;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import javax.xml.namespace.QName;
import qxsl.model.Field;

/**
 * {@link FieldFormat}クラスの自動検出及びインスタンス化機構を実装します。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2013/06/08
 *
 */
public final class FieldFormats implements Iterable<FieldFormat> {
	private final ServiceLoader<FieldFormat> loader;
	private final Map<QName, Cache> caches;

	/**
	 * 現在のクラスローダからインスタンス化機構を構築します。
	 */
	public FieldFormats() {
		this(Thread.currentThread().getContextClassLoader());
	}

	/**
	 * 指定のクラスローダからインスタンス化機構を構築します。
	 * 
	 * @param cl 書式を検出するクラスローダ
	 */
	public FieldFormats(ClassLoader cl) {
		this.loader = ServiceLoader.load(FieldFormat.class, cl);
		this.caches = new HashMap<>();
	}

	@Override
	public Iterator<FieldFormat> iterator() {
		return loader.iterator();
	}

	/**
	 * 指定された属性の入出力を行う{@link FieldFormat}を返します。
	 * 
	 * @param name 属性の名前
	 * @return 対応する書式 存在しない場合null
	 */
	public FieldFormat getFormat(QName name) {
		for(FieldFormat fmt: loader) {
			if(fmt.target().equals(name)) return fmt;
		}
		return null;
	}

	/**
	 * 指定された属性名に対する{@link Cache}を返します。
	 * 
	 * @param qname {@link Field}の名前
	 * @return キャッシュ
	 */
	public final Cache cache(QName qname) {
		return caches.computeIfAbsent(qname, Cache::new);
	}

	/**
	 * 任意の属性を保存的に格納する{@link Field}実装クラスです。
	 * クラスパスに{@link FieldFormat}がない場合に使用されます。
	 * 
	 * 
	 * @author Journal of Hamradio Informatics
	 * 
	 * @since 2019/06/28
	 *
	 */
	public static final class Any extends Field<String> {
		private final String value;

		/**
		 * 属性名と値を指定して{@link Any}を構築します。
		 * 
		 * @param qname 属性名
		 * @param value 属性値
		 */
		public Any(QName qname, String value) {
			super(qname);
			this.value = value;
		}

		@Override
		public String value() {
			return value;
		}
	}

	/**
	 * 特定の属性名を持つ属性に特化したキャッシュ機構です。
	 * 
	 * 
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2019/06/26
	 */
	public final class Cache extends HashMap<String, Field> {
		private final FieldFormat format;
		private final QName qname;

		/**
		 * 指定された属性に特化するキャッシュを構築します。
		 *
		 * @param qname 属性名
		 */
		private Cache(QName qname) {
			this.format = getFormat(this.qname = qname);
		}

		/**
		 * 指定された値の{@link Field}を生成します。
		 *
		 * @param value 属性値の文字列
		 * @return 読み込まれた属性
		 */
		private Field createField(String value) {
			try {
				return format.decode(value);
			} catch(NullPointerException ex) {
				return new Any(qname, value);
			}
		}

		/**
		 * 指定された値の{@link Field}を取得します。
		 * 
		 * @param value {@link Field}の値を表す文字列
		 * @return 属性値 属性が未登録の場合はnull
		 */
		public Field field(String value) {
			return computeIfAbsent(value, this::createField);
		}
	}
}