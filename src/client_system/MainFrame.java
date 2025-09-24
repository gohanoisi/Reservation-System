package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

public class MainFrame extends Frame implements ActionListener, WindowListener, ItemListener, TextListener {

	enum Mode {
		EXPLANATION, RESERVATION
	}

	ReservationControl reservationControl;
	Panel panelNorth, panelNorthSub1, panelNorthSub2, panelCenter, panelSouth;
	Button buttonLog, buttonExplanation, buttonReservation, buttonViewReservation, buttonSearchNoReservation, buttonSelfViewReservation;
	ChoiceFacility choiceFacility;
	TextField tfLoginID;
	TextArea textMessage;
	TextField tfYear, tfMonth, tfDay;

	Mode currentMode = Mode.EXPLANATION;

	public MainFrame(ReservationControl rc) {
		reservationControl = rc;

		// ボタン生成
		buttonLog = new Button(" ログイン ");
		buttonExplanation = new Button("教室概要");
		buttonViewReservation = new Button("予約状況");
		buttonReservation = new Button("新規予約");
		buttonSearchNoReservation = new Button("空き検索");
		buttonSelfViewReservation = new Button("自己予約確認");

		// 教室選択
		List<String> facilityId = rc.getFacilityId();
		choiceFacility = new ChoiceFacility(facilityId);
		choiceFacility.addItemListener(this);

		// ログインID
		tfLoginID = new TextField("未ログイン", 10);
		tfLoginID.setEditable(false);

		// 日付入力
		LocalDate now = LocalDate.now();
		tfYear = new TextField(String.valueOf(now.getYear()), 4);
		tfMonth = new TextField(String.valueOf(now.getMonthValue()), 2);
		tfDay = new TextField(String.valueOf(now.getDayOfMonth()), 2);
		tfYear.addTextListener(this);
		tfMonth.addTextListener(this);
		tfDay.addTextListener(this);

		setLayout(new BorderLayout());

		// 上部パネル（上）
		panelNorthSub1 = new Panel();
		panelNorthSub1.add(new Label("教室予約システム　"));
		panelNorthSub1.add(buttonLog);
		panelNorthSub1.add(new Label("　　ログインID："));
		panelNorthSub1.add(tfLoginID);
		panelNorthSub1.add(new Label("　"));
		panelNorthSub1.add(buttonSelfViewReservation);

		// 上部パネル（下）
		panelNorthSub2 = new Panel();
		Panel panelSwitchButtons = new Panel(new GridLayout(2, 1));
		panelSwitchButtons.add(buttonExplanation);
		panelSwitchButtons.add(buttonViewReservation);
		panelNorthSub2.add(panelSwitchButtons);
		panelNorthSub2.add(new Label("　"));
		panelNorthSub2.add(new Label("教室"));
		panelNorthSub2.add(choiceFacility);
		panelNorthSub2.add(new Label("　"));

		// 日付入力欄
		Panel panelSearchDate = new Panel();
		panelSearchDate.add(tfYear);
		panelSearchDate.add(new Label("年"));
		panelSearchDate.add(tfMonth);
		panelSearchDate.add(new Label("月"));
		panelSearchDate.add(tfDay);
		panelSearchDate.add(new Label("日"));
		panelNorthSub2.add(panelSearchDate);

		panelNorth = new Panel(new BorderLayout());
		panelNorth.add(panelNorthSub1, BorderLayout.NORTH);
		panelNorth.add(panelNorthSub2, BorderLayout.CENTER);
		add(panelNorth, BorderLayout.NORTH);

		// 中央パネル（メッセージ）
		panelCenter = new Panel();
		textMessage = new TextArea(20, 80);
		textMessage.setEditable(false);
		panelCenter.add(textMessage);
		add(panelCenter, BorderLayout.CENTER);

		// 下部パネル（予約・空き検索）
		panelSouth = new Panel();
		panelSouth.add(buttonReservation);
		panelSouth.add(new Label("　　　"));
		panelSouth.add(buttonSearchNoReservation);
		add(panelSouth, BorderLayout.SOUTH);

		// リスナー登録
		buttonLog.addActionListener(this);
		buttonExplanation.addActionListener(this);
		buttonViewReservation.addActionListener(this);
		buttonReservation.addActionListener(this);
		buttonSearchNoReservation.addActionListener(this);
		buttonSelfViewReservation.addActionListener(this);
		addWindowListener(this);

		// 初期状態更新
		updateView();
		updateLoginStatus(); // ← 追加
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonLog) {
			textMessage.setText(reservationControl.loginLogout(this));
			updateLoginStatus();  // ← 追加：ログイン状態更新
		} else if (e.getSource() == buttonReservation) {
			textMessage.setText(reservationControl.makeReservation(this));
		} else if (e.getSource() == buttonExplanation) {
			currentMode = Mode.EXPLANATION;
			updateView();
		} else if (e.getSource() == buttonViewReservation) {
			currentMode = Mode.RESERVATION;
			updateView();
		} else if (e.getSource() == buttonSearchNoReservation) {
			textMessage.setText(reservationControl.searchNoReservation(this));
		} else if (e.getSource() == buttonSelfViewReservation) {
			reservationControl.selfViewReservation(this);
		}
	}

	private void updateView() {
		// ボタンの背景で状態表示
		if (currentMode == Mode.EXPLANATION) {
			buttonExplanation.setBackground(Color.LIGHT_GRAY);
			buttonViewReservation.setBackground(null);

			tfYear.setEnabled(false);
			tfMonth.setEnabled(false);
			tfDay.setEnabled(false);

			String facility = choiceFacility.getSelectedItem();
			textMessage.setText(reservationControl.getFacilityExplanation(facility));

		} else if (currentMode == Mode.RESERVATION) {
			buttonExplanation.setBackground(null);
			buttonViewReservation.setBackground(Color.LIGHT_GRAY);

			tfYear.setEnabled(true);
			tfMonth.setEnabled(true);
			tfDay.setEnabled(true);

			try {
				int year = Integer.parseInt(tfYear.getText().trim());
				int month = Integer.parseInt(tfMonth.getText().trim());
				int day = Integer.parseInt(tfDay.getText().trim());

				LocalDate.of(year, month, day);

				String facilityId = choiceFacility.getSelectedItem();
				textMessage.setText(reservationControl.getReservations(facilityId, String.valueOf(year), String.valueOf(month), String.valueOf(day)));
			} catch (NumberFormatException | DateTimeException ex) {
				textMessage.setText("無効な値が入力されました。");
			}
		}
	}

	// ログイン状態に応じてボタンを有効化/無効化
	private void updateLoginStatus() {
		String loginId = tfLoginID.getText().trim();
		boolean isLoggedIn = !loginId.equals("未ログイン");
		buttonSelfViewReservation.setEnabled(isLoggedIn);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		updateView();
	}

	@Override
	public void textValueChanged(TextEvent e) {
		if (currentMode == Mode.RESERVATION) {
			updateView();
		}
	}

	@Override public void windowOpened(WindowEvent e) {}
	@Override public void windowClosing(WindowEvent e) { System.exit(0); }
	@Override public void windowClosed(WindowEvent e) {}
	@Override public void windowIconified(WindowEvent e) {}
	@Override public void windowDeiconified(WindowEvent e) {}
	@Override public void windowActivated(WindowEvent e) {}
	@Override public void windowDeactivated(WindowEvent e) {}
}
