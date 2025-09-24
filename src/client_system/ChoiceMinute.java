// @2 新規予約機能で新たに追加したクラス
package client_system;

import java.awt.Choice;

public class ChoiceMinute extends Choice{

	public	ChoiceMinute() {					// 分は15分刻みで設定可能とする
		add("00");
		add("15");
		add("30");
		add("45");
	}
}
