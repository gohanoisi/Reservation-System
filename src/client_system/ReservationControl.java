package client_system;

import java.awt.Dialog;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat; // @2
import java.text.ParseException; // @2
import java.text.SimpleDateFormat; // @2
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList; // @1
import java.util.Calendar; // @2
import java.util.Comparator;
import java.util.List;

public class ReservationControl {
	// MySQLに接続するためのデータ
	Connection sqlCon;
	Statement sqlStmt;
	String sqlUserID = "puser"; // ユーザID
	String sqlPassword = "1234"; // パスワード
	// 予約システムのユーザID及びLogin状態
	String reservationUserID;
	private boolean flagLogin; // ログイン状態(ログイン済:true)
	private SearchNoReservationDialog snrd; // 空き時間検索ダイアログを保持

	// ReservationControlクラスのコンストラクタ
	ReservationControl() {
		flagLogin = false;
	}

	// MySQLに接続するためのメソッド
	private void connectDB() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver"); // MySQLのドライバをLoadする
			// MySQLに接続
			String url = "jdbc:mysql://localhost?useUnicode=true&characterEncoding=SJIS";
			sqlCon = DriverManager.getConnection(url, sqlUserID, sqlPassword);
			sqlStmt = sqlCon.createStatement(); // Statement Objectを生成
		} catch (Exception e) { // 例外発生時
			e.printStackTrace(); // Stack Traceを表示
		}
	}

	//// MySQLから切断するためのメソッド
	private void closeDB() {
		try {
			sqlStmt.close(); // Statement ObjectをCloseする
			sqlCon.close(); // MySQLとの接続を切る
		} catch (Exception e) { // 例外発生時
			e.printStackTrace(); // StackTraceを表示
		}
	}

	//// ログイン・ログアウトボタンの処理
	public String loginLogout(MainFrame frame) {
		String res = ""; // 結果表示エリアのメッセージをNullを結果で初期化
		if (flagLogin) {
			flagLogin = false;
			frame.buttonLog.setLabel(" ログイン ");
			frame.tfLoginID.setText("未ログイン");
		} else {
			// ログインダイアログ生成＋表示
			LoginDialog ld = new LoginDialog(frame);
			ld.setBounds(100, 100, 350, 150); // Windowの位置とサイズ設定
			ld.setResizable(false); // Windowをサイズ固定化
			ld.setVisible(true); // Windowを可視化
			ld.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);// このWindowを閉じるまで他のWindowの操作禁止

			// IDとパスワードの入力がキャンセルされたら，Nullを結果として返し終了
			if (ld.canceled) {
				return "";
			}

			// ユーザIDとパスワードが入力された場合の処理
			reservationUserID = ld.tfUserID.getText();
			String password = ld.tfPassword.getText();

			connectDB(); // MySQLに接続
			try {
				// ユーザの情報を取得するクエリ
				String sql = "SELECT * FROM db_reservation.user WHERE BINARY user_id ='" + reservationUserID + "';";
				System.out.println(sql); // @@@@ デバッグ用SQLをコンソールに表示
				// クエリを実行して結果セットを取得
				ResultSet rs = sqlStmt.executeQuery(sql);
				// パスワードチェック
				if (rs.next()) {
					String password_from_db = rs.getString("password");// DBに登録されているパスワードを取得
					if (password_from_db.equals(password)) { // 入力パスワードが正しい時
						flagLogin = true; // ログイン済みに設定
						frame.buttonLog.setLabel("ログアウト"); // ログインボタンの表示をログアウトに変更
						frame.tfLoginID.setText(reservationUserID); // ログインユーザIDにログイン済みのIDを表示
					} else { // パスワードが正しくない時
						res = "IDまたはパスワードが違います。"; // 結果表示エリアに表示するメッセージをセット
					}
				} else { // 非登録ユーザの時
					res = "IDが違います。"; // 結果表示エリアに表示するメッセージをセット
				}
			} catch (Exception e) { // 例外発生時
				e.printStackTrace(); // StackTraceを表示
			}
			closeDB(); // MySQLの接続を切断
		}
		return res;
	}

	//// @1 教室概要ボタン押下時の処理を行うメソッド
	public String getFacilityExplanation(String facility_id) { // @1
		String res = ""; // @1 戻り値変数の初期化
		String exp = ""; // @1 explanationを入れる変数の宣言
		String openTime = ""; // @1 open_timeを入れる変数の宣言
		String closeTime = ""; // @1 close_timeを入れる変数の宣言
		connectDB(); // @1 MySQLに接続
		try { // @1
			String sql = "SELECT * FROM db_reservation.facility WHERE facility_id = '" + facility_id + "';"; // @1
			ResultSet rs = sqlStmt.executeQuery(sql); // @1 選択された教室IDと同じレコードを抽出
			if (rs.next()) { // @1 1件目のレコードを取得
				exp = rs.getString("explanation"); // @1 explanation属性データを取得
				openTime = rs.getString("open_time"); // @1 open_time属性データの取得
				closeTime = rs.getString("close_time"); // @1 close_time属性データの取得
				// @1 教室概要データの作成
				res = exp + "　利用可能時間：" + openTime.substring(0, 5) + "～" + closeTime.substring(0, 5); // @1
			} else { // @1 該当するレコードが無い場合
				res = "教室番号が違います。"; // @1 結果表示エリアに表示する文言をセット
			} // @1
		} catch (Exception e) { // @1 例外発生時
			e.printStackTrace(); // @1 StackTraceをコンソールに表示
		} // @1
		closeDB(); // @1 MySQLの接続を切断
		return res; // @1
	} // @1
		// @1
		//// @1 全てのfacility_idを取得するメソッド

	public ArrayList<String> getFacilityId() { // @1
		ArrayList<String> facilityId = new ArrayList<String>(); // @1 全てのfacilityIDを入れるリストを作成
		connectDB(); // @1 MySQLに接続
		try { // @1
				// @1 facilityテーブルの全データを取得するSQL文
			String sql = "SELECT * FROM db_reservation.facility ORDER BY facility_id DESC;"; // @1 
			ResultSet rs = sqlStmt.executeQuery(sql); // @1 SQL文を送信し，テーブルデータを取得
			while (rs.next()) { // @1 取得したレコードがなくなるまで繰り返す
				facilityId.add(rs.getString("facility_id")); // @1 取り出したレコードのfacility_idをリストに加える
			} // @1
		} catch (Exception e) { // @1 例外発生時
			e.printStackTrace(); // @1 StackTraceをコンソールに表示
		} // @1
		closeDB(); // @1 MySQLを切断
		return facilityId; // @1 全てのfacility_idの入ったListを返す
	} // @1

	//// @2 新規予約ボタン押下時の処理を行うメソッド
	public String makeReservation(MainFrame frame) { // @2
		String res = ""; // @2 結果を入れる戻り値変数を初期化（Nullを結果）
							// @2
		if (flagLogin) { // @2 ログイン済みの場合
			// @2 新規予約画面生成
			ReservationDialog rd = new ReservationDialog(frame, this); // @2
																		// @2
																		// @2 新規予約画面を表示
			rd.setVisible(true); // @2 新規予約画面を表示（ここで制御がrdインスタンスに移る）
			if (rd.canceled) { // @2 新規予約操作をキャンセルしたとき
				return res; // @2 新規予約終了
			} // @2
				// @2 新規予約操作を正常実行したとき
				// @2 新規予約画面から年月日を取得
			String ryear_str = rd.tfYear.getText(); // @2 入力された年情報をテキストで取得
			String rmonth_str = rd.tfMonth.getText(); // @2 選択された月情報をテキストで取得
			String rday_str = rd.tfDay.getText(); // @2 選択された日情報をテキストで取得
			// @2 月と日が一桁だったら，前に0を付加
			if (rmonth_str.length() == 1) { // @2 月の文字数が1桁の時
				rmonth_str = "0" + rmonth_str; // @2 　月の先頭に"0"を付加
			} // @2
			if (rday_str.length() == 1) { // @2 日の文字数が1桁の時
				rday_str = "0" + rday_str; // @2 　日の先頭に"0"を付加
			} // @2
				// @2
				// @2 入力された日付が正しいか，以下2点をチェック
				// @2   入力された文字が半角数字になっているか．
				// @2   日付として成立している値か
			try { // @2
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // @2 日付のフォーマットを定義
				df.setLenient(false); // @2 日付フォーマットのチェックを厳格化
				// @2 入力された日付を文字列に変換したものと，SimpleDateFormatに当てはめて同じ値になるかをチェック
				String inData = ryear_str + "-" + rmonth_str + "-" + rday_str; // @2 入力日付を文字列形式でyyyy-MM-dd形式に合成
				String convData = df.format(df.parse(inData)); // @2 入力日付をSimpleDateFormat形式に変換
				if (!inData.equals(convData)) { // @2 2つの文字列が等しくない時．
					res = "日付の書式を修正して下さい。（年：西暦4桁，月：1～12，日：1～31(各月月末まで))"; // @2 エラー文を設定し，新規予約終了
					return res; // @2
				} // @2
			} catch (ParseException p) { // @2 年月日の文字が誤っていてSimpleDateFormatに変換不可の時
				res = "日付の値を修正して下さい。"; // @2 数字以外，入力されていないことを想定したエラー処理
				return res; // @2
			} // @2
				// @2
				// @2 入力された開始日が現時点より後であるかのチェック
			Calendar dateReservation = Calendar.getInstance(); // @2 
			// @2 入力された予約日付及び現在の日付をCalendarクラスの情報として持つ
			dateReservation.set(Integer.parseInt(ryear_str), Integer.parseInt(rmonth_str) - 1,
					Integer.parseInt(rday_str)); //@2
			Calendar dateNow = Calendar.getInstance(); // @2 現在日時を取得
														// @2
														// @2 翌日以降の予約の時
			if (dateReservation.after(dateNow)) { // @2
				// @2 新規予約画面から教室名,利用開始時刻，終了時刻を取得
				String facility = rd.choiceFacility.getSelectedItem(); // @2
				String st = rd.startHour.getSelectedItem() + ":" + rd.startMinute.getSelectedItem() + ":00"; // @2
				String et = rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() + ":00"; // @2
				// @2
				// @2 開始時刻と終了時刻が同じ時
				if (st.compareTo(et) >= 0) { // @2
					res = "開始時刻と終了時刻が同じか終了時刻の方が早くなっています。";// @2
				} else { // @2
					// @2 開始時刻と終了時刻は同じではない時
					try { // @2
							// @2 選択されている時間が利用可能時間の範囲か
						int limit[][] = getAvailableTime(facility); // @2 選択教室の利用可能な開始時刻と終了時刻を取得
						String limitString[][] = { { "", "" }, { "", "" } }; // @2 　開始時分，終了時分を文字列で持つための変数定義
						for (int i = 0; i < 2; i++) { // @2
							for (int j = 0; j < 2; j++) { // @2
								limitString[i][j] = String.valueOf(limit[i][j]); // @2 　開始時分，終了時分を文字列に変換
								if (limitString[i][j].length() == 1) { // @2 　開始時分，終了時分が1桁なら先頭に0を付加
									limitString[i][j] = "0" + limitString[i][j]; // @2
								} // @2
							} // @2
						} // @2
						String startLimit = limitString[0][0] + ":" + limitString[0][1] + ":00"; // @2 利用可能開始時分を文字列に合成
						String endLimit = limitString[1][0] + ":" + limitString[1][1] + ":00"; // @2 利用可能終了時分を文字列に合成
						// @2 入力された利用開始・終了時間が教室の利用可能開始時前もしくは利用可能終了時後の時
						if (startLimit.compareTo(st) > 0 || endLimit.compareTo(et) < 0) { // @2
							res = "利用可能時間外です。"; // @2
							// @2 開始時間及び終了時間が利用可能な範囲の時
						} else { // @2
							// @2 指定された時間で予約可能かどうかのチェック
							connectDB(); // @2 MySQLに接続
							// @2 月日が1桁なら前に0を付ける
							if (rmonth_str.length() == 1) { // @2
								rmonth_str = "0" + rmonth_str; // @2
							} // @2
							if (rday_str.length() == 1) { // @2
								rday_str = "0" + rday_str; // @2
							} // @2
								// @2 reservationテーブルより，新規予約日の予約情報を取得する
							String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str; // @2
							// @2 指定教室の新規予約日の予約情報を取得するクエリ作成
							String sql = "SELECT * FROM db_reservation.reservation WHERE facility_id = '" + facility
									+ "' AND day = '" + rdate + "';"; // @2
							System.out.println(sql); // @@@@ デバッグ用SQLをコンソールに表示
							ResultSet rs = sqlStmt.executeQuery(sql); // @2 
							// @2 検索結果に対して時間の重なりをチェック
							boolean ng = false; // @2 結果の初期値をチェックOKに設定
							// @2 取得したタプルを1件ずつチェック
							while (rs.next()) { // @2
								// @2 タプルの開始時刻，終了時刻をそれぞれstartとendに設定
								String start = rs.getString("start_time"); // @2
								String end = rs.getString("end_time"); // @2
								// @2 予約済みの開始時刻が新規予約の開始時刻以前で，新規予約の開始時刻が予約済みの終了時刻以前か
								// @2 新規予約の開始時刻が予約済みの開始時刻以前で，予約済みの開始時刻が新規予約の終了時刻以前ならば
								// @2 重複ありと判定
								if ((start.compareTo(st) <= 0 && st.compareTo(end) <= 0) || // @2
										(st.compareTo(start) <= 0 && start.compareTo(et) <= 0)) { // @2
									ng = true; // @2
									break; // @2
								} // @2
							} // @2

							// @2 予約済みと重なりがない場合
							if (!ng) { // @2
								Calendar justNow = Calendar.getInstance(); // @2 予約した日時を取得
								SimpleDateFormat resDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//@2 予約した日時のフォーマットを定義
								String now = resDate.format(justNow.getTime()); // @2 予約した日時（現在日時）データを取得
																				// @2
																				// @2 予約情報をreservatonテーブルに登録する
								sql = "INSERT INTO db_reservation.reservation( facility_id, user_id, date, day, start_time, end_time) VALUES( '"
										+ facility + "','" + reservationUserID + "','" + now + "','" + rdate + "','"
										+ st + "','" + et + "');";
								System.out.println(sql); // @@@@2 デバッグ用SQLをコンソールに表示
								sqlStmt.executeUpdate(sql); // @2 SQL文をMySQLに投げる
															// @2 予約完了表示作成
								res = rdate + " " + st.substring(0, 5) + "～" + et.substring(0, 5) + " " + facility
										+ "教室を予約しました。";// @2
								// @2 登録されている予約情報と重なりがある場合
							} else { // @2
								res = "既にある予約に重なっています。"; // @2
							} // @2
						} // @2
							// @2 途中で予期しない例外が発生した場合
					} catch (Exception e) { // @2
						res = "予期しないエラーが発生しました。"; // @2
						e.printStackTrace(); // @2 StackTraceをコンソールに表示
					} // @2
					closeDB(); // @2 MySQLとの接続を切る
				} // @2
					// @2 予約日が当日かそれより前だった場合
			} else { // @2
				res = "予約日が無効です。"; // @2
			} // @2
				// @2 未ログイン状態の場合
		} else { // @2
			res = "ログインして下さい。"; // @2
		} // @2
		return res; // @2
	} // @2

	//// @2 指定教室の利用可能開始・終了時間を取得する
	//// @2	（戻り値：abailableTime[][]={{ 開始時, 開始分}, { 終了時, 終了分}}
	public int[][] getAvailableTime(String facility) { // @2
		int[][] abailableTime = { { 0, 0 }, { 0, 0 } }; // @2 開始時・終了時をこの配列に入れて呼び元に返す
		connectDB(); // @2 MySQLに接続
		try { // @2
			String sql = "SELECT * FROM db_reservation.facility WHERE facility_id = " + facility + ";"; // @2
			ResultSet rs = sqlStmt.executeQuery(sql); // @2 選択された教室IDのタプルを取得
			while (rs.next()) { // @2 タプルが無くなるまで繰り返す
				String timeData = rs.getString("open_time"); // @2 タプルのopen_timeを取得
				abailableTime[0][0] = Integer.parseInt(timeData.substring(0, 2)); // @2 open_timeの「時」を整数型に変換
				abailableTime[0][1] = Integer.parseInt(timeData.substring(3, 5)); // @2 open_timeの「分」を整数型に変換
				timeData = rs.getString("close_time"); // @2 タプルのclose_timeを取得
				abailableTime[1][0] = Integer.parseInt(timeData.substring(0, 2)); // @2 close_timeの「時」を整数型に変換
				abailableTime[1][1] = Integer.parseInt(timeData.substring(3, 5)); // @2 close_timeの「分」を整数型に変換
			} // @2
		} catch (Exception e) { // @2 該当するレコードがない，「時」や「分」を変換出来ないなど
			e.printStackTrace(); // @2 StackTraceをコンソールに表示
		} // @2
		return abailableTime; // @2 open_time,close_timeの「時」を返す（エラーなら{0,0}が返る
	}

	public String getReservations(String facilityId, String year, String month, String day) {
		StringBuilder result = new StringBuilder();
		connectDB(); // DB接続

		try {
			// 教室情報の説明文を取得
			String explanation = "";
			String sqlFacility = "SELECT explanation FROM db_reservation.facility WHERE facility_id = ?";
			PreparedStatement pstmtFacility = sqlCon.prepareStatement(sqlFacility);
			pstmtFacility.setString(1, facilityId);
			ResultSet rsFacility = pstmtFacility.executeQuery();
			if (rsFacility.next()) {
				explanation = rsFacility.getString("explanation");
			}
			rsFacility.close();
			pstmtFacility.close();

			// 「座席数：」より前を抽出（例：「353講義室」）
			int index = explanation.indexOf("座席数");
			if (index != -1) {
				explanation = explanation.substring(0, index).trim();
			}

			// 予約取得SQL
			String sql = """
					    SELECT
					        r.start_time,
					        r.end_time
					    FROM
					        db_reservation.reservation r
					    WHERE
					        r.facility_id = ? AND YEAR(r.day) = ? AND MONTH(r.day) = ? AND DAY(r.day) = ?
					    ORDER BY
					        r.start_time ASC;
					""";

			PreparedStatement pstmt = sqlCon.prepareStatement(sql);
			pstmt.setString(1, facilityId);
			pstmt.setString(2, year);
			pstmt.setString(3, month);
			pstmt.setString(4, day);

			ResultSet rs = pstmt.executeQuery();

			boolean hasReservation = false;
			while (rs.next()) {
				hasReservation = true;
				String startTime = rs.getString("start_time");
				String endTime = rs.getString("end_time");
				result.append(explanation).append("　予約されている時間　").append(startTime).append("～").append(endTime)
						.append("\n");
			}

			if (!hasReservation) {
				result.append(explanation).append(" 予約されている時間はありません\n");
			}

			rs.close();
			pstmt.close();

		} catch (Exception e) {
			e.printStackTrace();
			return "エラーが発生しました";
		}

		closeDB(); // DB切断
		return result.toString();
	}

	public String searchNoReservation(MainFrame frame) {
		StringBuilder result = new StringBuilder();

		if (snrd == null) {
			snrd = new SearchNoReservationDialog(frame, this);
		}
		snrd.setVisible(true);

		if (!snrd.wasSearchPressed()) {
			System.out.println("ダイアログがバッテンで閉じられたため処理中断");
			return "";
		}

		// ダイアログの状態を取得
		boolean useFacility = snrd.cbFacility.getState();
		boolean useDate = snrd.cbDate.getState();
		boolean useDateDayOnly = snrd.cbDateDayOnly.getState() && snrd.cbDateDayOnly.isEnabled();
		boolean useDateAfter = snrd.cbDateAfter.getState() && snrd.cbDateAfter.isEnabled();
		boolean useStartTime = snrd.cbStartTime.getState();
		boolean useEndTime = snrd.cbEndTime.getState();

		String facility = snrd.choiceFacility.getSelectedItem();
		String year = snrd.tfYear.getText().trim();
		String month = snrd.tfMonth.getText().trim();
		String day = snrd.tfDay.getText().trim();
		String startHour = snrd.startHour.getSelectedItem();
		String startMinute = snrd.startMinute.getSelectedItem();
		String endHour = snrd.endHour.getSelectedItem();
		String endMinute = snrd.endMinute.getSelectedItem();

		// 曜日フィルター配列（index 0 = 日, 1 = 月, ..., 6 = 土）
		boolean[] useWeekday = new boolean[7];
		for (int i = 0; i < 7; i++) {
			useWeekday[i] = snrd.cbWeekdays[i].getState();
		}

		List<String> allResults = new ArrayList<>();
		final int MAX_LINES = 255;

		// 検索対象の日付リスト作成
		List<LocalDate> dateList = new ArrayList<>();
		if (useDate) {
			try {
				LocalDate base = LocalDate.of(
						Integer.parseInt(year),
						Integer.parseInt(month),
						Integer.parseInt(day));
				if (useDateDayOnly && useDateAfter) {
					for (int i = 0; i < 30; i++) {
						dateList.add(base.plusDays(i));
					}
				} else if (useDateDayOnly) {
					dateList.add(base);
				} else if (useDateAfter) {
					for (int i = 0; i < 30; i++) {
						dateList.add(base.plusDays(i));
					}
				} else {
					int lastDay = base.lengthOfMonth();
					for (int i = 1; i <= lastDay; i++) {
						dateList.add(LocalDate.of(base.getYear(), base.getMonth(), i));
					}
				}
			} catch (Exception e) {
				result.append("日付の入力が正しくありません\n");
				return result.toString();
			}
		} else {
			LocalDate today = LocalDate.now();
			for (int i = 0; i < 30; i++) {
				dateList.add(today.plusDays(i));
			}
		}

		// 検索処理本体
		for (String f : getFacilityId()) {
			for (LocalDate date : dateList) {
				String partial = getSearchNoReservation(
						f,
						String.valueOf(date.getYear()),
						String.valueOf(date.getMonthValue()),
						String.valueOf(date.getDayOfMonth()),
						useFacility, facility,
						useDate, useDateDayOnly, useDateAfter, year, month, day,
						useStartTime, startHour, startMinute,
						useEndTime, endHour, endMinute,
						useWeekday);

				String[] lines = partial.split("\n");
				for (String line : lines) {
					if (!line.isEmpty()) {
						allResults.add(line);
					}
				}
			}
		}

		// 日付 → 時刻 → 教室名の順でソート
		allResults.sort(Comparator
				.comparing((String s) -> LocalDate.parse(s.split("　")[2]))
				.thenComparing(s -> LocalTime.parse(s.split("　")[3].split("～")[0]))
				.thenComparing(s -> s.split("　")[0]));

		int totalLines = 0;
		for (String line : allResults) {
			if (totalLines >= MAX_LINES) {
				result.append("※表示可能上限（255件）を超えたため一部を省略しました。\n");
				break;
			}
			result.append(line).append("\n");
			totalLines++;
		}

		if (totalLines == 0) {
			result.append("空き時間は見つかりませんでした。\n");
		}

		return result.toString();
	}

	public String getSearchNoReservation(String facilityId, String year, String month, String day,
			boolean useFacility, String selectedFacility,
			boolean useDate, boolean useDateDayOnly, boolean useDateAfter, String selectedYear, String selectedMonth,
			String selectedDay,
			boolean useStart, String startHour, String startMinute,
			boolean useEnd, String endHour, String endMinute,
			boolean[] useWeekday) {

		StringBuilder result = new StringBuilder();

		// 教室フィルター
		if (useFacility && !facilityId.equals(selectedFacility)) {
			return "";
		}

		// 日付条件
		if (useDate) {
			LocalDate inputDate;
			LocalDate current = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
			try {
				inputDate = LocalDate.of(Integer.parseInt(selectedYear), Integer.parseInt(selectedMonth),
						Integer.parseInt(selectedDay));
			} catch (Exception e) {
				return "";
			}

			if (useDateAfter) {
				if (current.isBefore(inputDate)) {
					return "";
				}
			} else {
				if (!year.equals(selectedYear) || !month.equals(selectedMonth)) {
					return "";
				}
				if (useDateDayOnly && !day.equals(selectedDay)) {
					return "";
				}
			}
		}

		// 曜日フィルター処理
		try {
			LocalDate current = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
			int dayOfWeekIndex = current.getDayOfWeek().getValue() % 7; // 日:0 ～ 土:6
			if (!useWeekday[dayOfWeekIndex]) {
				return "";
			}
		} catch (Exception e) {
			return "";
		}

		connectDB();

		try {
			String sqlFacility = "SELECT explanation, open_time, close_time FROM db_reservation.facility WHERE facility_id = ?";
			PreparedStatement pstmtFacility = sqlCon.prepareStatement(sqlFacility);
			pstmtFacility.setString(1, facilityId);
			ResultSet rsFacility = pstmtFacility.executeQuery();

			String explanation = "";
			LocalTime openTime, closeTime;
			if (rsFacility.next()) {
				explanation = rsFacility.getString("explanation");
				openTime = rsFacility.getTime("open_time").toLocalTime();
				closeTime = rsFacility.getTime("close_time").toLocalTime();
			} else {
				rsFacility.close();
				pstmtFacility.close();
				return "";
			}
			rsFacility.close();
			pstmtFacility.close();

			String roomName = explanation;
			String seats = "";
			int index = explanation.indexOf("座席数");
			if (index != -1) {
				roomName = explanation.substring(0, index).trim();
				seats = explanation.substring(index).replace("座席数：", "").replaceAll("席.*", "").trim() + "席";
			}

			if (useStart) {
				LocalTime inputStart = LocalTime.of(Integer.parseInt(startHour), Integer.parseInt(startMinute));
				if (inputStart.isAfter(openTime)) {
					openTime = inputStart;
				}
			}
			if (useEnd) {
				LocalTime inputEnd = LocalTime.of(Integer.parseInt(endHour), Integer.parseInt(endMinute));
				if (inputEnd.isBefore(closeTime)) {
					closeTime = inputEnd;
				}
			}
			if (!openTime.isBefore(closeTime))
				return "";

			// 予約取得
			String sql = """
						SELECT start_time, end_time
						FROM db_reservation.reservation
						WHERE facility_id = ? AND YEAR(day) = ? AND MONTH(day) = ? AND DAY(day) = ?
						ORDER BY start_time ASC
					""";
			PreparedStatement pstmt = sqlCon.prepareStatement(sql);
			pstmt.setString(1, facilityId);
			pstmt.setString(2, year);
			pstmt.setString(3, month);
			pstmt.setString(4, day);
			ResultSet rs = pstmt.executeQuery();

			List<TimeRange> reserved = new ArrayList<>();
			while (rs.next()) {
				LocalTime start = rs.getTime("start_time").toLocalTime();
				LocalTime end = rs.getTime("end_time").toLocalTime();
				reserved.add(new TimeRange(start, end));
			}
			rs.close();
			pstmt.close();

			// 空き時間出力
			LocalTime cursor = openTime;
			LocalDate localDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
			for (TimeRange r : reserved) {
				if (cursor.isBefore(r.start)) {
					result.append(roomName).append("　")
							.append(seats).append("　")
							.append(localDate).append("　")
							.append(cursor).append("～").append(r.start).append("\n");
				}
				if (cursor.isBefore(r.end)) {
					cursor = r.end;
				}
			}
			if (cursor.isBefore(closeTime)) {
				result.append(roomName).append("　")
						.append(seats).append("　")
						.append(localDate).append("　")
						.append(cursor).append("～").append(closeTime).append("\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			closeDB();
		}

		return result.toString();
	}
	
	public void selfViewReservation(Frame owner) {
	    new SelfReservationsDialog(owner, this).setVisible(true);
	}

	/**
	 * ログイン中ユーザの予約一覧をデータ構造として取得
	 * @return List<String[]> 要素：{facility_id, day(yyyy-MM-dd), start_time(HH:mm), end_time(HH:mm)}
	 */
	public java.util.List<String[]> getSelfReservationsData() {
		java.util.List<String[]> list = new java.util.ArrayList<>();
		connectDB();
		try {
			String sql = 
				"SELECT facility_id, day, start_time, end_time " +
				"FROM db_reservation.reservation " +
				"WHERE user_id = ? " +
				"ORDER BY day ASC, start_time ASC;";
			PreparedStatement ps = sqlCon.prepareStatement(sql);
			ps.setString(1, reservationUserID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String facility = rs.getString("facility_id");
				String day      = rs.getString("day");
				String st       = rs.getString("start_time").substring(0,5);
				String et       = rs.getString("end_time").substring(0,5);
				list.add(new String[]{facility, day, st, et});
			}
			rs.close();
			ps.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return list;
	}

	/**
	 * 指定の予約をキャンセル（DBから削除）
	 * @return true: 成功 / false: 失敗
	 */
	public boolean cancelReservation(String facilityId, String day, String startTime) {
		connectDB();
		try {
			String sql =
				"DELETE FROM db_reservation.reservation " +
				"WHERE user_id = ? AND facility_id = ? AND day = ? AND start_time = ?;";
			PreparedStatement ps = sqlCon.prepareStatement(sql);
			ps.setString(1, reservationUserID);
			ps.setString(2, facilityId);
			ps.setString(3, day);
			ps.setString(4, startTime + ":00");
			int count = ps.executeUpdate();
			ps.close();
			return count > 0;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeDB();
		}
	}
}
