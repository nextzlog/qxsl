/*******************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package gaas.table;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import qxsl.draft.Qxsl;
import qxsl.draft.Time;
import qxsl.field.FieldManager;
import qxsl.model.Item;
import qxsl.table.PrintDecoder;

/**
 * CTESTWIN書式で直列化された交信記録をデコードします。
 *
 *
 * @author 無線部開発班
 *
 * @since 2013/07/02
 */
public final class CTxtDecoder extends PrintDecoder {
	private static final int TIME = 1;
	private static final int CALL = 2;
	private static final int BAND = 3;
	private static final int MODE = 4;
	private static final int SENT = 5;
	private static final int RCVD = 6;
	private final DateTimeFormatter tstamp;
	private final FieldManager fields;
	private final CTxtFactory format;

	/**
	 * 指定された入力を読み込むデコーダを構築します。
	 *
	 *
	 * @param reader 入力
	 * @param format 書式
	 */
	public CTxtDecoder(Reader reader, CTxtFactory format) {
		super(reader);
		this.format = format;
		this.fields = new FieldManager();
		this.tstamp = format.getTimeDecoderOld();
	}

	/**
	 * ストリームの交信記録の冒頭を読み取ります。
	 *
	 *
	 * @throws IOException 読み取りに失敗した場合
	 *
	 * @since 2020/09/04
	 */
	@Override
	public final void head() throws IOException {
		String line;
		while((line = super.readLine()) != null) {
			if(line.isBlank()) continue;
			if(line.startsWith("Worked")) continue;
			super.reset();
			break;
		}
	}

	/**
	 * ストリームの交信記録の末尾を読み取ります。
	 *
	 *
	 * @throws IOException 読み取りに失敗した場合
	 *
	 * @since 2020/09/04
	 */
	@Override
	public final void foot() throws IOException {}

	/**
	 * ストリームの現在位置の交信記録を読み取ります。
	 *
	 *
	 * @return 読み取った交信記録
	 *
	 * @throws IOException 読み取りに失敗した場合
	 *
	 * @since 2020/09/04
	 */
	@Override
	public final Item next() throws IOException {
		final var item = new Item();
		final var vals = split(0, 5, 16, 28, 36, 41, 54, 67);
		try {
			Integer.parseInt(vals[0]);
			if(!vals[TIME].isEmpty()) time(item, vals[TIME]);
			if(!vals[CALL].isEmpty()) call(item, vals[CALL]);
			if(!vals[BAND].isEmpty()) band(item, vals[BAND]);
			if(!vals[MODE].isEmpty()) mode(item, vals[MODE]);
			if(!vals[SENT].isEmpty()) sent(item, vals[SENT]);
			if(!vals[RCVD].isEmpty()) rcvd(item, vals[RCVD]);
			return item;
		} catch (RuntimeException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * ストリームに交信記録が存在するかを確認します。
	 *
	 *
	 * @return 交信記録を読み取れる場合は真
	 *
	 * @throws IOException 構文上または読取り時の例外
	 *
	 * @since 2020/09/04
	 */
	@Override
	public final boolean hasNext() throws IOException {
		final var exists = super.readLine() != null;
		super.reset();
		return exists;
	}

	/**
	 * 交信記録に交信日時を設定します。
	 *
	 *
	 * @param item 設定する交信記録
	 * @param text 交信日時の文字列
	 */
	private final void time(Item item, String text) {
		item.set(new Time(LocalDateTime.parse(text, tstamp)));
	}

	/**
	 * 交信記録に相手局のコールサインを設定します。
	 *
	 *
	 * @param item 設定する交信記録
	 * @param text コールサインの文字列
	 */
	private final void call(Item item, String text) {
		item.set(fields.cache(Qxsl.CALL).field(text));
	}

	/**
	 * 交信記録に周波数帯を設定します。
	 *
	 *
	 * @param item 設定する交信記録
	 * @param text 周波数帯の文字列
	 */
	private final void band(Item item, String text) {
		final var num = text.replaceAll("[GMk]Hz$", "");
		final var exp = getBandUnit(text.trim());
		final var val = Double.parseDouble(num);
		final var kHz = String.valueOf(exp * val);
		item.set(fields.cache(Qxsl.BAND).field(kHz));
	}

	/**
	 * 指定された周波数の文字列の単位を返します。
	 *
	 *
	 * @param text 周波数の文字列
	 *
	 * @return 単位
	 *
	 * @since 2020/09/06
	 */
	private final double getBandUnit(String text) {
		switch(text.substring(text.length() - 3)) {
			case "GHz": return 1e6;
			case "MHz": return 1e3;
			case "kHz": return 1e0;
		}
		throw new NumberFormatException(text);
	}

	/**
	 * 交信記録に通信方式を設定します。
	 *
	 *
	 * @param item 設定する交信記録
	 * @param text 通信方式の文字列
	 */
	private final void mode(Item item, String text) {
		item.set(fields.cache(Qxsl.MODE).field(text));
	}

	/**
	 * 交信記録に相手局に送信したナンバーを設定します。
	 *
	 *
	 * @param item 設定する交信記録
	 * @param text ナンバーの文字列
	 */
	private final void sent(Item item, String text) {
		item.getSent().set(fields.cache(Qxsl.CODE).field(text));
	}

	/**
	 * 交信記録に相手局から受信したナンバーを設定します。
	 *
	 *
	 * @param item 設定する交信記録
	 * @param text ナンバーの文字列
	 */
	private final void rcvd(Item item, String text) {
		item.getRcvd().set(fields.cache(Qxsl.CODE).field(text));
	}
}
