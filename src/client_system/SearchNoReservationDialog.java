package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.List;

public class SearchNoReservationDialog extends Dialog implements ActionListener, ItemListener {

	public ChoiceFacility choiceFacility;
	public TextField tfYear, tfMonth, tfDay;
	public ChoiceHour startHour, endHour;
	public ChoiceMinute startMinute, endMinute;
	public Button buttonSearch;

	public Checkbox cbFacility;
	public Checkbox cbDate;
	public Checkbox cbDateDayOnly;
	public Checkbox cbDateAfter;
	public Checkbox cbStartTime;
	public Checkbox cbEndTime;

	public Checkbox[] cbWeekdays = new Checkbox[7];
	private final String[] weekdayLabels = { "日", "月", "火", "水", "木", "金", "土" };

	private boolean wasSearchPressed = false;

	public boolean wasSearchPressed() {
		return wasSearchPressed;
	}

	public SearchNoReservationDialog(Frame owner, ReservationControl rc) {
		super(owner, "空き時間検索", true);

		// 教室一覧の取得
		List<String> facilityId = rc.getFacilityId();
		choiceFacility = new ChoiceFacility(facilityId);

		// 日付入力欄
		tfYear = new TextField("", 4);
		tfMonth = new TextField("", 2);
		tfDay = new TextField("", 2);

		// デフォルトを今日に設定
		LocalDate today = LocalDate.now();
		tfYear.setText(String.valueOf(today.getYear()));
		tfMonth.setText(String.valueOf(today.getMonthValue()));
		tfDay.setText(String.valueOf(today.getDayOfMonth()));

		// 時刻選択欄
		startHour = new ChoiceHour();
		startMinute = new ChoiceMinute();
		endHour = new ChoiceHour();
		endMinute = new ChoiceMinute();

		// 検索ボタン
		buttonSearch = new Button("空き時間検索");
		buttonSearch.addActionListener(this);

		// チェックボックス
		cbFacility = new Checkbox("教室を指定する", true);
		cbDate = new Checkbox("予約日を指定する", true);
		cbDateDayOnly = new Checkbox("日を指定", false);
		cbDateAfter = new Checkbox("以降を含める", false);
		cbStartTime = new Checkbox("開始時刻を指定", false);
		cbEndTime = new Checkbox("終了時刻を指定", false);

		// リスナー登録
		cbFacility.addItemListener(this);
		cbDate.addItemListener(this);
		cbDateDayOnly.addItemListener(this);
		cbDateAfter.addItemListener(this);
		cbStartTime.addItemListener(this);
		cbEndTime.addItemListener(this);
		startHour.addItemListener(this);
		startMinute.addItemListener(this);

		// 日付チェックボックスパネル
		Panel panelDateCheckboxes = new Panel(new FlowLayout(FlowLayout.LEFT));
		panelDateCheckboxes.add(cbDate);
		panelDateCheckboxes.add(cbDateDayOnly);
		panelDateCheckboxes.add(cbDateAfter);

		// 日付入力欄パネル
		Panel panelDateInput = new Panel(new FlowLayout(FlowLayout.LEFT));
		panelDateInput.add(tfYear);
		panelDateInput.add(new Label("年"));
		panelDateInput.add(tfMonth);
		panelDateInput.add(new Label("月"));
		panelDateInput.add(tfDay);
		panelDateInput.add(new Label("日"));

		// 曜日チェックパネル（横並び）
		Panel panelWeekdays = new Panel(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < 7; i++) {
			cbWeekdays[i] = new Checkbox(weekdayLabels[i], true); // 全曜日ON
			cbWeekdays[i].addItemListener(this);
			panelWeekdays.add(cbWeekdays[i]);
		}

		// 教室指定パネル
		Panel panelFacility = new Panel(new FlowLayout(FlowLayout.LEFT));
		panelFacility.add(cbFacility);
		panelFacility.add(choiceFacility);

		// 上段パネル全体（4行構成）
		Panel panelNorth = new Panel(new GridLayout(4, 1));
		panelNorth.add(panelFacility); // 1段目：教室
		panelNorth.add(panelDateCheckboxes); // 2段目：日付条件
		panelNorth.add(panelDateInput); // 3段目：日付入力
		panelNorth.add(panelWeekdays); // 4段目：曜日

		// 中央パネル（時刻）
		Panel panelCenter = new Panel(new FlowLayout(FlowLayout.LEFT));
		panelCenter.add(cbStartTime);
		panelCenter.add(startHour);
		panelCenter.add(new Label("時"));
		panelCenter.add(startMinute);
		panelCenter.add(new Label("分 ～ "));

		panelCenter.add(cbEndTime);
		panelCenter.add(endHour);
		panelCenter.add(new Label("時"));
		panelCenter.add(endMinute);
		panelCenter.add(new Label("分"));

		// 下段パネル（ボタン）
		Panel panelSouth = new Panel();
		panelSouth.add(buttonSearch);

		// レイアウトにパネルを追加
		setLayout(new BorderLayout());
		add(panelNorth, BorderLayout.NORTH);
		add(panelCenter, BorderLayout.CENTER);
		add(panelSouth, BorderLayout.SOUTH);

		// 状態初期化
		updateComponentState();
		updateEndTimeOptions();

		// ウィンドウ設定
		setBounds(200, 200, 620, 260);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose(); // バッテン押下で閉じるが、入力値は保持
			}
		});
	}

	private void updateComponentState() {
		choiceFacility.setEnabled(cbFacility.getState());

		tfYear.setEnabled(cbDate.getState());
		tfMonth.setEnabled(cbDate.getState());
		tfDay.setEnabled(cbDate.getState() && cbDateDayOnly.getState());
		cbDateDayOnly.setEnabled(cbDate.getState());
		cbDateAfter.setEnabled(cbDate.getState() && cbDateDayOnly.getState());

		startHour.setEnabled(cbStartTime.getState());
		startMinute.setEnabled(cbStartTime.getState());
		endHour.setEnabled(cbEndTime.getState());
		endMinute.setEnabled(cbEndTime.getState());

		for (Checkbox cb : cbWeekdays) {
			cb.setEnabled(true); // 曜日は常にオン/オフ可能
		}
	}

	private void updateEndTimeOptions() {
		if (!cbStartTime.getState() || !cbEndTime.getState())
			return;

		try {
			int startH = Integer.parseInt(startHour.getSelectedItem());
			int startM = Integer.parseInt(startMinute.getSelectedItem());
			int startMinutes = startH * 60 + startM;

			endHour.removeAll();
			endMinute.removeAll();

			for (int h = 0; h < 24; h++) {
				for (int m = 0; m < 60; m += 5) {
					if (h * 60 + m > startMinutes) {
						endHour.add(String.format("%02d", h));
						break;
					}
				}
			}
			for (int m = 0; m < 60; m += 15) {
				endMinute.add(String.format("%02d", m));
			}

			int defaultEnd = Math.min(startMinutes + 15, 1439);
			endHour.select(String.format("%02d", defaultEnd / 60));
			endMinute.select(String.format("%02d", (defaultEnd % 60) / 5 * 5));

		} catch (NumberFormatException e) {
			// 無視
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		updateComponentState();
		updateEndTimeOptions();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonSearch) {
			wasSearchPressed = true;
			setVisible(false);
		}
	}
}
