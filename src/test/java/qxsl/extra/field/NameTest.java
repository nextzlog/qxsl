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
 * {@link Name}クラスのテスト用クラスです。
 *
 *
 * @author 無線部開発班
 *
 * @since 2017/02/24
 */
@ExtendWith(RandomStringParameterExtension.class)
public final class NameTest extends org.assertj.core.api.Assertions {
	private final Cache cache = new FieldFormats().cache(Qxsl.NAME);

	@Test
	public void testValue() {
		assertThat(new Name("筑波大").value()).isEqualTo("筑波大");
		assertThat(new Name("電通大").value()).isEqualTo("電通大");
	}

	@Test
	public void testToString(@RandomString String text) {
		assertThat(new Name(text)).hasToString(text);
	}

	@Test
	public void testName$Format(@RandomString String text) throws Exception {
		final var form = new Name.Format();
		final var name = new Name(text);
		assertThat(form.decode(form.encode(name))).isEqualTo(name);
		assertThat(cache.field(form.encode(name))).isEqualTo(name);
	}
}
