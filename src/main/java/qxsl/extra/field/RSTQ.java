/*****************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * Language: Java Standard Edition 8
 *****************************************************************************
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics http://pafelog.net
*****************************************************************************/
package qxsl.extra.field;

import javax.xml.namespace.QName;
import qxsl.field.FieldFormat;
import qxsl.field.FieldMapper;
import qxsl.model.Field;
import qxsl.model.Rcvd;
import qxsl.model.Sent;

/**
 * 交信のRST(RSQ)レポートを表現する{@link Field}実装クラスです。
 * 
 * 
 * @author Journal of Hamradio Informatics
 * 
 * @since 2013/06/08
 *
 */
public final class RSTQ extends Qxsl<Integer> {
	private final int r, s, t;

	/**
	 * 指定された整数で{@link RSTQ}を構築します。
	 * 3桁の整数の場合、音調レポートまで読み取ります。
	 * 2桁の整数の場合、音調レポートを読み取りません。
	 * 
	 * @param rst RSTQをそのまま整数値にした値
	 */
	public RSTQ(int rst) {
		super(RSTQ);
		int r = (rst / 100) % 10;
		int s = (rst / 10 ) % 10;
		int t = (rst / 1  ) % 10;
		if(r > 0) {
			this.r = Math.max(1, Math.min(5, r));
			this.s = Math.max(1, Math.min(9, s));
			this.t = Math.max(1, Math.min(9, t));
		} else {
			this.r = Math.max(1, Math.min(5, s));
			this.s = Math.max(1, Math.min(9, t));
			this.t = 0;
		}
	}

	/**
	 * RSTQを整数で指定して{@link RSTQ}を構築します。
	 * 
	 * @param r 了解度
	 * @param s 信号強度
	 * @param t 音調 または品質
	 */
	public RSTQ(int r, int s, int t) {
		this(r * 100 + s * 10 + t);
	}

	/**
	 * 了解度レポートを返します。
	 * この値は常に1以上5以下です。
	 * 
	 * @return 了解度
	 */
	public int getR() {
		return r;
	}

	/**
	 * 信号強度レポートを返します。
	 * この値は常に1以上9以下です。
	 * 
	 * @return 信号強度
	 */
	public int getS() {
		return s;
	}

	/**
	 * 音調レポートを返します。
	 * この値は常に0以上9以下です。
	 * 
	 * @return 音調 もしくは品質
	 */
	public int getT() {
		return t;
	}

	@Override
	public Integer value() {
		if(this.t < 1) return this.r * 10 + this.s;
		return this.r * 100 + this.s * 10 + this.t;
	}

	/**
	 * {@link RSTQ}を生成する書式です。
	 * 
	 * 
	 * @author Journal of Hamradio Informatics
	 * 
	 * @since 2013/06/08
	 *
	 */
	public static final class Format implements FieldFormat {
		@Override
		public QName target() {
			return RSTQ;
		}

		@Override
		public RSTQ decode(String value) {
			return new RSTQ(Integer.parseInt(value));
		}

		@Override
		public String encode(Field field) {
			return field.value().toString();
		}
	}

	/**
	 * {@link RSTQ}への変換を行う変換器です。
	 * 
	 * 
	 * @author Journal of Hamradio Informatics
	 * 
	 * @since 2019/06/29
	 *
	 */
	public static final class Mapper implements FieldMapper {
		@Override
		public QName target() {
			return RSTQ;
		}

		@Override
		public RSTQ search(Rcvd rcvd) {
			final Object rst = rcvd.getItem().value(new QName(ADIF, "RST_RCVD"));
			return rst != null? new RSTQ(Integer.parseInt(rst.toString())): null;
		}

		@Override
		public RSTQ search(Sent sent) {
			final Object rst = sent.getItem().value(new QName(ADIF, "RST_SENT"));
			return rst != null? new RSTQ(Integer.parseInt(rst.toString())): null;
		}
	}
}