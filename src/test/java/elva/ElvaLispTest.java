/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package elva;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.script.ScriptException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ElvaLisp}クラスのテスト用クラスです。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2017/02/26
 *
 */
public final class ElvaLispTest extends test.RandTest {
	private final ElvaLisp elva = new ElvaLisp();
	@Test
	public void testNil() throws ScriptException {
		assertThat(elva.eval("()")).isEqualTo(Struct.NIL);
	}
	@Test
	public void testNull() throws ScriptException {
		assertThat(elva.eval("null")).isNull();
	}
	@Test
	public void testInt() throws ScriptException {
		assertThat(elva.eval("114")).isEqualTo(BigDecimal.valueOf(114));
		assertThat(elva.eval("514")).isEqualTo(BigDecimal.valueOf(514));
	}
	@Test
	public void testTrue() throws ScriptException {
		assertThat(elva.eval("true")).isEqualTo(true);
	}
	@Test
	public void testFalse() throws ScriptException {
		assertThat(elva.eval("false")).isEqualTo(false);
	}
	@Test
	public void testString() throws ScriptException {
		assertThat(elva.eval("\"JA1ZLO\"")).isEqualTo("JA1ZLO");
		assertThat(elva.eval("\"JA1ZGP\"")).isEqualTo("JA1ZGP");
	}
	@ParameterizedTest
	@MethodSource("testMethodSource")
	public void test(String source) throws ScriptException {
		if(!elva.scan(source).isEmpty()) {
			final String test = String.format("(equal %s)", source);
			assertThat(elva.eval(test)).isEqualTo(true);
		}
	}
	public static Stream<String> testMethodSource() throws IOException {
		URL path = ElvaLisp.class.getResource("elva.test.lisp");
		final Reader source = new InputStreamReader(path.openStream());
		try (final BufferedReader reader = new BufferedReader(source)) {
			return reader.lines().collect(Collectors.toList()).stream();
		}
	}
}