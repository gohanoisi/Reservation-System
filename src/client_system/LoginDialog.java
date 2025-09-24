package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class LoginDialog extends Dialog implements ActionListener, WindowListener{
	boolean		canceled;								// キャンセル:true　OK:false
	
	TextField	tfUserID;								// ユーザID入力用テキストフィールド
	TextField	tfPassword;								// パスワード入力用テキストフィールド
	
	Button		buttonOK;								// OKボタン
	Button		buttonCancel;							// キャンセルボタン
	
	Panel		panelNorth;								// 上部パネル
	Panel		panelCenter;							// 中央パネル
	Panel		panelSouth;								// 下部パネル
	
	public	LoginDialog( Frame arg0) {
		super( arg0, "Login", true);					// Superクラスのコンストラクタ呼出
		canceled	= true;								// キャンセルの初期値設定
		
		// テキストフィールド生成
		tfUserID	= new	TextField( "", 10);			// ユーザIDテキストフィールド生成
		tfPassword	= new	TextField( "", 10);			// パスワードテキストフィールド生成
		tfPassword.setEchoChar( '*');					// パスワードは*表示とする
		
		// ボタンの生成
		buttonOK	= new Button( "OK");				// OKボタン生成
		buttonCancel= new Button( "キャンセル");		// キャンセルボタン生成
		
		// パネルの生成
		panelNorth	= new Panel();						// 上部パネル生成
		panelCenter	= new Panel();						// 中央パネル生成
		panelSouth	= new Panel();						// 下部パネル生成
		
		// 上部パネルにユーザIDテキストフィールドを追加
		panelNorth.add( new Label( "ユーザID"));
		panelNorth.add( tfUserID);
		
		// 中央パネルにパスワードテキストフィールドを追加
		panelCenter.add( new Label( "パスワード"));
		panelCenter.add( tfPassword);
		
		// 下部パネルに2つのボタンを追加
		panelSouth.add( buttonCancel);					// キャンセルボタン追加
		panelSouth.add( buttonOK);						// OKボタン追加
		
		// LoginDialogをBorderLayoutに設定し，3つのパネルを追加
		setLayout( new BorderLayout());
		add( panelNorth, BorderLayout.NORTH);
		add( panelCenter, BorderLayout.CENTER);
		add( panelSouth, BorderLayout.SOUTH);
		
		// Window Listenerを追加
		addWindowListener( this);
		
		// ボタンにAction Listenerを追加
		buttonOK.addActionListener( this);
		buttonCancel.addActionListener( this);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		setVisible( false);								// ログインダイアログを非表示化
		canceled	= true;								// ログインをキャンセル状態にする
		dispose();										// ログインダイアログの部品を破棄
		
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
		if( e.getSource() == buttonCancel) {			// キャンセルボタン押下時
			canceled = true;							// ログインをキャンセル状態にする
		} else if( e.getSource() == buttonOK) {			// OKボタン押下時
			canceled = false;							// ログインをキャンセルでない状態にする
		}
		setVisible( false);								// ログインダイアログ非表示
		dispose();
	}
}
