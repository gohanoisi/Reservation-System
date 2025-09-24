// @1（ファイル全体）
package client_system;

import java.awt.Choice;
import	java.util.List;

public class ChoiceFacility extends Choice{
	//// ChoiceFacilityコンストラクタ
	ChoiceFacility( List<String> facility){				// 引数でfacility_idのリストを受け取る
		for( String id : facility) {					// facility_idをidに1つづつ取り出す
			add( id);									// choiceにfacility_idを1つ追加する
		}
	}
}
