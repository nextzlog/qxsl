/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*****************************************************************************/
package qxsl.field;

import java.util.Iterator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link FieldFormat}クラスのテスト用クラスです。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2019/07/02
 *
 */
public final class FieldFormatTest extends test.RandTest {
	/**
	 * クラスパスにある全ての書式を返します。
	 *
	 * 
	 * @return 書式のイテレータ
	 */
	public static Iterator<FieldFormat> testMethodSource() {
		return new FieldFormats().iterator();
	}
	@ParameterizedTest
	@MethodSource("testMethodSource")
	public void testTarget(FieldFormat format) {
		assertThat(format.target().getNamespaceURI()).isNotNull();
	}
}