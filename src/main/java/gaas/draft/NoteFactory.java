/*******************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package gaas.draft;

import javax.xml.namespace.QName;

import qxsl.draft.Note;
import qxsl.draft.Qxsl;
import qxsl.field.FieldFactory;
import qxsl.value.Field;

/**
 * {@link Note}を生成する書式です。
 *
 * @author 無線部開発班
 * @since 2013/06/08
 */
public final class NoteFactory implements FieldFactory {
	@Override
	public QName target() {
		return Qxsl.NOTE;
	}

	@Override
	public Note decode(String value) {
		return new Note(value);
	}

	@Override
	public String encode(Field field) {
		return field.value().toString();
	}
}