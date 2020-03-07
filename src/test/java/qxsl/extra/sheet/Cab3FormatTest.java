/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*****************************************************************************/
package qxsl.extra.sheet;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import qxsl.extra.field.*;
import qxsl.model.Item;
import qxsl.sheet.SheetFormats;
import qxsl.table.TableFormats;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Cab3Format}クラスのテスト用クラスです。
 *
 *
 * @author 無線部開発班
 *
 * @since 2019/05/03
 *
 */
public final class Cab3FormatTest extends test.RandTest {
	private final SheetFormats sheets = new SheetFormats();
	private final TableFormats tables = new TableFormats();
	private final ArrayList<Band> bands = new ArrayList<>();
	public Cab3FormatTest() {
		bands.add(new Band( 1_800));
		bands.add(new Band( 3_500));
		bands.add(new Band( 7_000));
		bands.add(new Band(14_000));
		bands.add(new Band(21_000));
		bands.add(new Band(28_000));
		bands.add(new Band(50_000));
	}
	public static IntStream testMethodSource() {
		return IntStream.range(0, 100);
	}
	@ParameterizedTest
	@MethodSource("testMethodSource")
	public void testDecode(int numItems) throws Exception {
		final Cab3Format format = new Cab3Format();
		final ArrayList<Item> items = new ArrayList<>();
		for(int row = 0; row < numItems; row++) {
			final Item item = new Item();
			item.add(new Time());
			item.add(bands.get(randInt(bands.size())));
			item.add(new Call(alnum(13)));
			item.add(new Mode(alnum(2)));
			item.getRcvd().add(new RSTQ(randInt(600)));
			item.getRcvd().add(new Code(alnum(6)));
			item.getSent().add(new RSTQ(randInt(600)));
			item.getSent().add(new Code(alnum(6)));
			items.add(item);
		}
		StringWriter sw1 = new StringWriter();
		StringWriter sw2 = new StringWriter();
		tables.forName("cqww").encoder(sw1).encode(items);
		final Map<String, String> kvals = new HashMap<>();
		kvals.put("CONTEST", "JIDX-CW");
		kvals.put("CALLSIGN", "JA1ZLO");
		kvals.put("QSO", sw1.toString().trim());
		format.encoder(sw2).encode(kvals);
		final StringReader strm = new StringReader(sw2.toString());
		assertThat(format.decoder(strm).decode()).isEqualTo(kvals);
		assertThat(sheets.unpack(sw2.toString())).isEqualTo(items);
	}
}
