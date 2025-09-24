package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class SelfReservationsDialog extends Dialog implements ActionListener, WindowListener {

	ReservationControl rc;
	Label textMessage;
	Panel panelHeader, panelList, panelSouth;

	public SelfReservationsDialog(Frame owner, ReservationControl rc) {
		super(owner, "予約一覧", true);
		this.rc = rc;

		// パネルの初期化
		panelHeader = new Panel(new GridLayout(1, 5, 10, 5));
		panelList = new Panel();
		panelList.setLayout(new GridLayout(0, 5, 10, 5));
		panelSouth = new Panel();

		// ヘッダー
		panelHeader.add(new Label("教室"));
		panelHeader.add(new Label("日付"));
		panelHeader.add(new Label("曜日"));
		panelHeader.add(new Label("時間"));
		panelHeader.add(new Label(""));

		// メッセージ表示欄
		textMessage = new Label(" ");
		panelSouth.add(textMessage);

		// 自己予約情報を取得
		List<String[]> list = rc.getSelfReservationsData();
		if (list.isEmpty()) {
			Label noRes = new Label("自己の予約はありません。");
			noRes.setAlignment(Label.CENTER);
			panelList.setLayout(new BorderLayout());
			panelList.add(noRes, BorderLayout.CENTER);
		} else {
			for (String[] r : list) {
				String facility = r[0];
				LocalDate ld = LocalDate.parse(r[1]);
				String date = ld.format(DateTimeFormatter.ofPattern("yyyy/M/d"));
				String wd = ld.format(DateTimeFormatter.ofPattern("E", Locale.JAPAN));
				String time = r[2] + "～" + r[3];

				panelList.add(new Label(facility));
				panelList.add(new Label(date));
				panelList.add(new Label(wd));
				panelList.add(new Label(time));

				Button btnCancel = new Button("キャンセル");
				panelList.add(btnCancel);
				btnCancel.addActionListener(e -> {
					boolean ok = rc.cancelReservation(facility, r[1], r[2]);
					if (ok) {
						dispose(); // 閉じてから再表示
						SelfReservationsDialog refreshed = new SelfReservationsDialog(owner, rc);
						refreshed.setVisible(true);
					} else {
						textMessage.setText("キャンセルに失敗しました。");
					}
				});
			}
		}

		// ダイアログの構成
		setLayout(new BorderLayout(10, 10));
		add(panelHeader, BorderLayout.NORTH);
		add(new ScrollPane().add(panelList), BorderLayout.CENTER);
		add(panelSouth, BorderLayout.SOUTH);

		addWindowListener(this);
		setSize(600, 120 + list.size() * 30);
		setLocationRelativeTo(owner);
		setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 未使用
	}

	@Override public void windowClosing(WindowEvent e) { dispose(); }
	@Override public void windowOpened(WindowEvent e) {}
	@Override public void windowClosed(WindowEvent e) {}
	@Override public void windowIconified(WindowEvent e) {}
	@Override public void windowDeiconified(WindowEvent e) {}
	@Override public void windowActivated(WindowEvent e) {}
	@Override public void windowDeactivated(WindowEvent e) {}
}
