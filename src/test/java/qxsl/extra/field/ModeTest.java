/*******************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package qxsl.extra.field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import qxsl.field.FieldFormats;
import qxsl.field.FieldFormats.Cache;
import qxsl.junit.RandomStringParameterExtension;
import qxsl.junit.RandomStringParameterExtension.RandomString;

/**
 * {@link Mode}クラスのテスト用クラスです。
 *
 *
 * @author 無線部開発班
 *
 * @since 2017/02/24
 */
@ExtendWith(RandomStringParameterExtension.class)
public final class ModeTest extends org.assertj.core.api.Assertions {
	private final Cache cache = new FieldFormats().cache(Qxsl.MODE);

	@Test
	public void testValue() {
		assertThat(new Mode("CW").value()).isEqualTo("CW");
		assertThat(new Mode("AM").value()).isEqualTo("AM");
	}

	@Test
	public void testToString(@RandomString String text) {
		assertThat(new Mode(text)).hasToString(text);
	}

	@Test
	public void testMode$Format(@RandomString String text) throws Exception {
		final var form = new Mode.Format();
		final var mode = new Mode(text);
		assertThat(form.decode(form.encode(mode))).isEqualTo(mode);
		assertThat(cache.field(form.encode(mode))).isEqualTo(mode);
	}
}
