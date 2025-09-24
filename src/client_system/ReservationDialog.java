// @2 新規予約機能で新たに追加したクラス
package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import	java.awt.event.ActionListener;
import	java.awt.event.ItemEvent;
import	java.awt.event.ItemListener;
import	java.awt.event.WindowEvent;
import	java.awt.event.WindowListener;
import	java.util.ArrayList;
import	java.util.List;

public class ReservationDialog extends Dialog implements ActionListener, WindowListener, ItemListener {

	boolean	canceled;							// 新規予約キャンセルステータス（キャンセル：true）
	ReservationControl	rc;						// ReservationControlインスタンス保存用
	
	// パネル
	Panel	panelNorth;
	Panel	panelCenter;
	Panel	panelSouth;
	
	// 入力用コンポーネント
	ChoiceFacility	choiceFacility;				// 教室選択用ボックス
	TextField		tfYear, tfMonth, tfDay;		// 年月日のテキストフィールド
	ChoiceHour		startHour;					// 予約開始時間（時）の選択ボックス
	ChoiceMinute	startMinute;				// 予約開始時間（分）の選択ボックス
	ChoiceHour		endHour;					// 予約終了時間（時）の選択ボックス
	ChoiceMinute	endMinute;					// 予約終了時間（分）の選択ボックス
	
	// ボタン
	Button			buttonOK;					// OKボタン
	Button			buttonCancel;				// キャンセルボタン
	
	public	ReservationDialog( Frame owner, ReservationControl rc) {
		// 基底クラスのコンストラクタを呼び出す
		super( owner, "新規予約", true);
		
		this.rc = rc;							// ReservationControlのインスタンスを保存
		// 初期値キャンセルを設定
		canceled = true;
		
		// 教室選択ボックスの生成
		List<String> facilityId = new ArrayList<String>();		// 全てのfacilityIDを入れるリスト
		facilityId 		= rc.getFacilityId();					// 全facilityIDを読出し，リストに入れる
		choiceFacility	= new ChoiceFacility( facilityId);		// 教室選択用コンボボックスのインスタンス生成
		// テキストフィールドの生成（年月日）
		tfYear		= new TextField("", 4);
		tfMonth 	= new TextField("", 2);
		tfDay		= new TextField("",2);
		// 開始時刻（時分）選択ボックスの生成
		startHour	= new ChoiceHour();
		startMinute = new ChoiceMinute();
		// 終了時刻（時分）選択ボックスの生成
		endHour		= new ChoiceHour();
		endMinute	= new ChoiceMinute();
		
		// ボタンの生成
		buttonOK	= new Button("予約実行");
		buttonCancel= new Button("キャンセル");
		
		// パネルの生成
		panelNorth	= new Panel();
		panelCenter	= new Panel();
		panelSouth	= new Panel();
		
		// 上部パネルに教室選択ボックス，年月日入力欄を配置
		panelNorth.add( new Label("教室　"));
		panelNorth.add( choiceFacility);
		panelNorth.add( new Label("予約日"));
		panelNorth.add( tfYear);
		panelNorth.add( new Label("年"));
		panelNorth.add( tfMonth);
		panelNorth.add( new Label("月"));
		panelNorth.add( tfDay);
		panelNorth.add( new Label("日"));
		
		// 中央パネルに予約開始時刻，終了時刻入力用選択ボックスを追加
		panelCenter.add( new Label("予約時間"));
		panelCenter.add( startHour);
		panelCenter.add( new Label("時"));
		panelCenter.add( startMinute);
		panelCenter.add( new Label("分～"));
		panelCenter.add( endHour);
		panelCenter.add( new Label("時"));
		panelCenter.add( endMinute);
		panelCenter.add( new Label("分"));
		
		// 下部パネルに2つのボタンを追加
		panelSouth.add( buttonCancel);
		panelSouth.add( new Label("   "));
		panelSouth.add( buttonOK);
		
		// ReservationDialogをBorderLayoutに設定し，3つのパネルを追加
		setLayout( new BorderLayout());
		add( panelNorth,	BorderLayout.NORTH);
		add( panelCenter,	BorderLayout.CENTER);
		add( panelSouth,	BorderLayout.SOUTH);
		
		// Window Listenerを追加
		addWindowListener( this);
		// ボタンにアクションリスナを追加
		buttonOK.addActionListener( this);
		buttonCancel.addActionListener( this);
		// 教室選択ボックス，時・分選択ボックスそれぞれに項目Listenerを追加
		choiceFacility.addItemListener( this);
		startHour.addItemListener( this);
		endHour.addItemListener( this);
		
		// 選択された教室によって，時刻の範囲を設定
		resetTimeRange( choiceFacility.getSelectedItem());
		
		// 大きさの設定，ウィンドウのサイズ変更不可の設定
		this.setBounds( 100, 100, 500, 150);
		setResizable( false);
		
	}
	private	void	resetTimeRange( String facility) {
		int[][]	availableTime;
		// 指定教室の利用可能開始時刻・終了時刻を取得する．
		availableTime = rc.getAvailableTime( facility);
		// 時刻の範囲に初期値を設定する
		startHour.resetRange( availableTime[0][0], availableTime[1][0]);
		endHour.resetRange( availableTime[0][0], availableTime[1][0]);
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		// 選択された教室が変わったとき
		if( e.getSource() == choiceFacility) {
			String	startTime	= startHour.getSelectedItem();								// 開始時刻を読み出す
			String	endTime		= endHour.getSelectedItem();								// 終了時刻を読み出す
			resetTimeRange( choiceFacility.getSelectedItem());								// 教室に応じた利用可能時間をコンボボックスの「時」に設定する
			if( Integer.parseInt( startTime) < Integer.parseInt( startHour.getFirst())) {	// 選択教室の利用可能開始時が現在設定値より後の時
				startTime	= startHour.getFirst();											// 　利用開始時を利用可能最速時に設定
			}
			if( Integer.parseInt( endTime) > Integer.parseInt( endHour.getLast())) {		// 選択教室の利用可能終了時が現在設定値より前の時
				endTime		= endHour.getLast();											// 　利用終了時を利用可能最遅時に設定	
			}
			startHour.select( startTime);													// 先程まで設定されていた開始時刻を復旧
			endHour.select( endTime);														// 先程まで設定されていた終了時刻を復旧
		// 利用開始時刻（時）が変わったとき
		} else if( e.getSource() == startHour) {
			// 開始時刻が変更されたら，終了時刻入力欄の時を開始時刻に合わせる
			int	start = Integer.parseInt( startHour.getSelectedItem());						// 開始時刻を数値で読み出し
			String	endTime	= endHour.getSelectedItem();									// 現在選択されている終了時刻を読み出し
			endHour.resetRange( start,  Integer.parseInt( endHour.getLast()));				// 終了時刻の範囲を開始時刻から最終時刻までに設定
			if( Integer.parseInt( endTime) >= start) {										// 選択されていた終了時刻が開始時刻かそれより後の時
				endHour.select( endTime);													// 先程設定されていた終了時刻を選択した状態にする
			}
		// 利用終了時刻（時）が変わったとき
		} else if( e.getSource() == endHour) {
			// 終了時刻が変更されたら，開始時刻入力欄の時を終了時刻に合わせる
			int	end		= Integer.parseInt( endHour.getSelectedItem());						// 選択時刻を整数で読み出し
			String	startTime	= startHour.getSelectedItem();								// 現在選択されている開始時刻を読み出し
			startHour.resetRange( Integer.parseInt( startHour.getFirst()), end);			// 開始時刻の範囲を開始時刻から終了時刻に設定
			if( Integer.parseInt( startTime) <= end) {										// 選択されていた開始時刻が終了時刻かそれより前の時
				startHour.select( startTime);												// 先程設定されていた開始時刻を設定
			}
		}
	}
	

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		setVisible( false);
		dispose();		
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == buttonCancel) {
			setVisible( false);
			dispose();
		} else if( e.getSource() == buttonOK) {
			canceled = false;
			setVisible( false);
			dispose();
		}
	}

	
}
