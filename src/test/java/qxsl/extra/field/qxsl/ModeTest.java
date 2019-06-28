/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package qxsl.extra.field.qxsl;

import org.junit.Test;
import qxsl.table.Fields;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Mode}クラスのテスト用クラスです。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2017/02/24
 *
 */
public final class ModeTest extends junit.framework.TestCase {
	private final Fields.Cache cache = new Fields().cache(Qxsl.MODE);
	@Test
	public void testValue() {
		assertThat(new Mode("CW").value()).isEqualTo("CW");
		assertThat(new Mode("AM").value()).isEqualTo("AM");
	}
	@Test
	public void testToString() {
		final String text = util.RandText.alnum(100);
		assertThat(new Mode(text)).hasToString(text);
	}
	@Test
	public void testMode$Format() throws Exception {
		final Mode.Format form = new Mode.Format();
		final Mode mode = new Mode(util.RandText.alnum(100));
		assertThat(form.decode(form.encode(mode))).isEqualTo(mode);
		assertThat(cache.field(form.encode(mode))).isEqualTo(mode);
	}
}