package client_system;

public class ReservationSystem {
	public	static	void	main( String argv[]) {
		ReservationControl	reservationControl	= new ReservationControl();				// ReservationControlクラスのインスタンス生成
		MainFrame			mainFrame			= new MainFrame( reservationControl);	// MainFrameクラスのインスタンス生成
		mainFrame.setBounds( 5, 5, 900, 455);											// mainFrameのWindowを生成
		mainFrame.setVisible( true);													// mainFrameのWindowを可視化
	}
}
