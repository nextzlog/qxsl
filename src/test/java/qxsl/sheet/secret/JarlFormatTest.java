/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package qxsl.sheet.secret;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Test;
import qxsl.field.*;
import qxsl.model.Item;
import qxsl.sheet.Sheets;
import qxsl.table.Tables;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * {@see JarlFormat}クラスのテスト用クラスです。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2017/03/12
 *
 */
public final class JarlFormatTest extends junit.framework.TestCase {
	private final JarlFormat format = new JarlFormat();
	private final Sheets sheets = new Sheets();
	private final Tables tables = new Tables();
	private final ArrayList<Band> bands = new ArrayList<>();
	private final Random random = new Random();
	public JarlFormatTest() {
		bands.add(new Band(    3_500));
		bands.add(new Band(    7_000));
		bands.add(new Band(   14_000));
		bands.add(new Band(  144_000));
		bands.add(new Band(1_200_000));
		bands.add(new Band(5_600_000));
	}
	@Test
	public void testDecode() throws java.io.IOException {
		final ArrayList<Item> items = new ArrayList<>();
		for(int row = 0; row < random.nextInt(50); row++) {
			final Item item = new Item();
			item.set(new Time());
			item.set(bands.get(random.nextInt(bands.size())));
			item.set(new Call(randomAlphanumeric(1, 14)));
			item.set(new Mode(randomAlphanumeric(1, 6)));
			item.getRcvd().set(new RSTQ(random.nextInt(600)));
			item.getRcvd().set(new Code(randomAlphanumeric(1, 8)));
			item.getSent().set(new RSTQ(random.nextInt(600)));
			item.getSent().set(new Code(randomAlphanumeric(1, 8)));
			items.add(item);
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		tables.getFormat("jarl").encode(os, items);
		Map<String, String> vals = new HashMap<>();
		vals.put("VERSION", "R2.0");
		vals.put("SCORE BAND=144MHz", "10,10,10");
		vals.put("SCORE BAND=430MHz", "20,20,20");
		vals.put("LOGSHEET", os.toString("SJIS").trim());
		os = new ByteArrayOutputStream();
		format.encode(os, vals);
		final byte[] b = os.toByteArray();
		assertThat(format.decode(new ByteArrayInputStream(b)), is(vals));
		assertThat(sheets.decode(new ByteArrayInputStream(b)), is(vals));
	}
}
