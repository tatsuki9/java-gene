import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import javax.swing.JFrame;

/*
 * 設計
 * 	startボタンを押すことにより、遺伝的アルゴリズム
 * で都市の最短経路を描画していく
 * 
 * 親パネル：
 * 	背景画像色変えたり
 * 	子パネルを追加
 * 子パネル：
 * 	都市の初期描画
 * 	子スレッドを作成し、遺伝的アルゴリズムを走らせる
 * 	
 */

public class Main extends JFrame implements ActionListener{

	GeneAlgorithm ge;
	
	public Main(){
		System.out.println("currentThread = " + Thread.currentThread().getName());
		// ロジッククラス生成
		this.ge = new GeneAlgorithm();
		
		// 親パネル生成
		MainPanel m_pn  = new MainPanel(ge);
		Container contentPane = getContentPane();
		contentPane.add(m_pn);
		// ボタン用意
		JButton start = new JButton("start");
		start.setMargin(new Insets(10,10,10,10));
		start.addActionListener(this);
		contentPane.add(start, BorderLayout.SOUTH);
		
		// 終了ボタン
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// 表示場所,大きさ
		setBounds(0,0,400,400);
		// 表題
		setTitle("巡回セールスマン");
		// 可視化
		setVisible(true);
	}

	public static void main(String[] args) {
		Main main = new Main();
	}

	@Override
	public void actionPerformed(ActionEvent e){
		this.ge.start();
	}
}
