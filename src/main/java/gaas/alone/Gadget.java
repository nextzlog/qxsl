/*******************************************************************************
 * Amateur Radio Operational Logging Library 'qxsl' since 2013 February 16th
 * License : GNU Lesser General Public License v3 (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package gaas.alone;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * コマンドラインツールが提供するコマンド群を定義します。
 *
 *
 * @author 無線部開発班
 *
 * @since 2020/11/09
 */
@Command(name = "qxsl", subcommands = {Filter.class, Format.class})
public final class Gadget implements Callable<Integer> {
	@Option(names = {"-h", "--help"}, usageHelp = true)
	private boolean showHelp;

	/**
	 * 省略されたコマンドを実行して終了コードを返します。
	 *
	 *
	 * @return 終了コード
	 */
	@Override
	public Integer call() {
		return new Filter().call();
	}

	/**
	 * コマンドラインツールを起動します。
	 *
	 *
	 * @param args コマンドライン引数
	 */
	public static final void main(String[] args) {
		System.exit(new CommandLine(new Gadget()).execute(args));
	}
}
