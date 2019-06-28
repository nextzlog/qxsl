/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package qxsl.extra.table;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.*;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import qxsl.extra.field.qxsl.*;
import qxsl.model.Item;
import qxsl.table.Fields;

/**
 * 2016年4月以前のCTESTWIN書式で交信記録を直列化するフォーマットです。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2013/07/02
 *
 */
public final class CTxtFormat extends TextFormat {
	/**
	 * 書式を構築します。
	 */
	public CTxtFormat() {
		super("ctxt");
	}

	/**
	 * 指定したストリームをこの書式でデコードして交信記録を読み込みます。
	 * 
	 * @param in 交信記録を読み込むストリーム
	 * @return 交信記録
	 * @throws IOException 入出力時の例外
	 */
	public List<Item> decode(InputStream in) throws IOException {
		return new CTxtDecoder(in).read();
	}

	/**
	 * この書式でエンコードした交信記録を指定したストリームに書き込みます。
	 * 
	 * @param out 交信記録を書き込むストリーム
	 * @param items 出力する交信記録
	 * @throws IOException 入出力時の例外
	 */
	public void encode(OutputStream out, List<Item> items) throws IOException {
		new CTxtEncoder(out).write(items);
	}

	/**
	 * CTESTWIN書式で直列化された交信記録をデコードします。
	 * 
	 * 
	 * @author Journal of Hamradio Informatics
	 * 
	 * @since 2013/07/02
	 * @deprecated この実装は概ね互換性がありますが、無保証です。
	 */
	@Deprecated
	private final class CTxtDecoder extends TextDecoder {
		private final DateTimeFormatter format;
		private final Fields fields;

		/**
		 * 指定されたストリームを読み込むデコーダを構築します。
		 * 
		 * @param in 読み込むストリーム
		 * @throws IOException SJISに対応していない場合
		 */
		public CTxtDecoder(InputStream in) throws IOException {
			super(in, "JISAutoDetect");
			fields = new Fields();
			DateTimeFormatterBuilder fb = new DateTimeFormatterBuilder();
			fb.parseDefaulting(ChronoField.YEAR, Year.now().getValue());
			this.format = fb.appendPattern("M/ppd HHmm").toFormatter();
		}

		/**
		 * 交信記録を読み込みます。ストリームは閉じられます。
		 * 
		 * @return 交信記録 交信記録がなければnull
		 * @throws IOException 入出力の例外
		 */
		public List<Item> read() throws IOException {
			try {
				return logSheet();
			} catch (IOException ex) {
				throw ex;
			} catch (Exception ex) {
				throw parseError(ex);
			} finally {
				super.close();
			}
		}

		private List<Item> logSheet() throws Exception {
			List<Item> items = new ArrayList<>();
			String line;
			while((line = super.readLine()) != null) {
				if(!line.matches("Worked\\s*[0-9]+\\s*stations|\\s*")) {
					items.add(item(line));
				}
			}
			return Collections.unmodifiableList(items);
		}

		/**
		 * 1行の文字列から{@link Item}を1件読み込みます。
		 * 
		 * @param line 1行
		 * @return 読み込んだ{@link Item}
		 * @throws Exception 読み込みに失敗した場合
		 */
		private Item item(String line) throws Exception {
			final Item item = new Item();
			final String time = subLine(5,  15);
			final String call = subLine(16, 27);
			final String band = subLine(28, 35);
			final String mode = subLine(36, 40);
			final String sent = subLine(41, 53);
			final String rcvd = subLine(54, -1);

			if(!time.isEmpty()) time(item, time);
			if(!call.isEmpty()) call(item, call);
			if(!band.isEmpty()) band(item, band);
			if(!mode.isEmpty()) mode(item, mode);
			if(!sent.isEmpty()) sent(item, sent);
			if(!rcvd.isEmpty()) rcvd(item, rcvd);

			return item;
		}

		/**
		 * {@link Item}に交信日時を設定します。
		 * 
		 * @param item 設定する{@link Item}
		 * @param time 交信日時の文字列
		 * @throws Exception 読み込みに失敗した場合
		 */
		private void time(Item item, String time) throws Exception {
			item.add(new Time(LocalDateTime.parse(time, format)));
		}

		/**
		 * {@link Item}に相手局のコールサインを設定します。
		 * 
		 * @param item 設定する{@link Item}
		 * @param call コールサインの文字列
		 * @throws Exception 読み込みに失敗した場合
		 */
		private void call(Item item, String call) throws Exception {
			item.add(fields.cache(Qxsl.CALL).field(call));
		}

		/**
		 * {@link Item}に周波数帯を設定します。
		 * 
		 * @param item 設定する{@link Item}
		 * @param band 周波数帯の文字列
		 * @throws Exception 読み込みに失敗した場合
		 */
		private void band(Item item, String band) throws Exception {
			Integer kHz;
			if(band.contains("GHz")){
				band = band.replace("GHz", "");
				kHz = (int) (Double.parseDouble(band) * 1000_000);
			} else if(band.contains("MHz")) {
				band = band.replace("MHz", "");
				kHz = (int) (Double.parseDouble(band) * 1000);
			} else {
				band = band.replace("kHz", "");
				kHz = Integer.parseInt(band);
			}
			item.add(fields.cache(Qxsl.BAND).field(kHz.toString()));
		}

		/**
		 * {@link Item}に通信方式を設定します。
		 * 
		 * @param item 設定する{@link Item}
		 * @param mode 通信方式の文字列
		 * @throws Exception 読み込みに失敗した場合
		 */
		private void mode(Item item, String mode) throws Exception {
			item.add(fields.cache(Qxsl.MODE).field(mode));
		}

		/**
		 * {@link Item}に相手局に送信したナンバーを設定します。
		 * 
		 * @param item 設定する{@link Item}
		 * @param sent ナンバーの文字列
		 * @throws Exception 読み込みに失敗した場合
		 */
		private void sent(Item item, String sent) throws Exception {
			item.getSent().add(fields.cache(Qxsl.CODE).field(sent));
		}

		/**
		 * {@link Item}に相手局から受信したナンバーを設定します。
		 * 
		 * @param item 設定する{@link Item}
		 * @param rcvd ナンバーの文字列
		 * @throws Exception 読み込みに失敗した場合
		 */
		private void rcvd(Item item, String rcvd) throws Exception {
			item.getRcvd().add(fields.cache(Qxsl.CODE).field(rcvd));
		}
	}

	/**
	 * 交信記録をCTESTWIN書式に直列化するエンコーダーです。
	 *
	 *
	 * @author Journal of Hamradio Informatics
	 *
	 * @since 2013/07/02
	 * @deprecated この実装は概ね互換性がありますが、無保証です。
	 */
	private final class CTxtEncoder extends TextEncoder {
		private final DateTimeFormatter format;

		/**
		 * 指定されたストリームに出力するエンコーダーを構築します。
		 * 
		 * @param out 交信記録を出力するストリーム
		 * @throws IOException  SJISに対応していない場合
		 */
		public CTxtEncoder(OutputStream out) throws IOException {
			super(out, "SJIS");
			format = DateTimeFormatter.ofPattern("MM/dd HHmm");
		}

		/**
		 * 交信記録を出力します。ストリームは閉じられます。
		 * 
		 * @param items 交信記録
		 * @throws IOException 入出力の例外
		 */
		public void write(List<Item> items) throws IOException {
			printf("Worked %4s stations", items.size());
			println();
			println();
			int counter = 1;
			for(Item r : items) item(r, counter++);
			super.close();
		}

		/**
		 * 指定された{@link Item}をテキスト書式で出力します。
		 * 
		 * @param item 出力する{@link Item}
		 * @param num 出力する{@link Item}の番号
		 * @throws IOException 出力に失敗した場合
		 */
		private void item(Item item, int num) throws IOException {
			printR(4, String.valueOf(num));
			printSpace(1);
			time(item.get(Time.class));
			printSpace(1);
			printR(11, item.get(Call.class));
			printSpace(1);
			band(item.get(Band.class));
			printSpace(1);
			printR(4, item.get(Mode.class));
			printSpace(1);
			printR(12, item.getSent().get(Code.class));
			printSpace(1);
			printR(12, item.getRcvd().get(Code.class));
			println();
		}

		/**
		 * 指定された日時を文字列として出力します。
		 * 
		 * @param date 出力する日時
		 * @throws IOException 出力に失敗した場合
		 */
		private void time(Time date) throws IOException {
			if(date == null) printSpace(10);
			else print(format.format(date.value()));
		}

		/**
		 * 指定された周波数帯を文字列として出力します。
		 * 
		 * @param band 出力する周波数帯
		 * @throws IOException 出力に失敗した場合
		 */
		private void band(Band band) throws IOException {
			printf("%-7.7s", band != null? band.toString() : "");
		}
	}
}
