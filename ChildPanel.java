import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.*;
import java.awt.RenderingHints;

public class ChildPanel extends JPanel{
	private GeneAlgorithm ge;
	
	public ChildPanel(GeneAlgorithm ge){
		setPreferredSize(new Dimension(100,100));
		// デフォルトレイアウトマネージャ無効
		setLayout(null);
		// 親パネルの相対位置を考慮して
		setBounds(0,0,300,300);
		// 透過
		setOpaque(false);
		this.ge = ge;
		// System.out.println("currentThread = " + Thread.currentThread().getName());
	}
	
	// 描画
	@Override
	public void paintComponent(Graphics g){
		// System.out.println("currentThread = " + Thread.currentThread().getName());
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		BasicStroke wideStroke = new BasicStroke(4.0f);

		// 巡回セールスマン計算されたgeクラスを使って描画
		int city_n[][] = this.ge.getcityN();
		
		// 個体情報取得
		Invidual id = this.ge.getInvidual();
		for(int i = 0; i < id.getCityOrder().size() - 1 ; i++){
			int n    = id.getCityOrder().get(i);
			int next = id.getCityOrder().get(i+1);
			
			Line2D.Double line = new Line2D.Double((double)city_n[n][0], (double)city_n[n][1], (double)city_n[next][0], (double)city_n[next][1]);
			Line2D.Double point_s = new Line2D.Double((double)city_n[n][0], (double)city_n[n][1],(double)city_n[n][0], (double)city_n[n][1]);
			Line2D.Double point_e = new Line2D.Double((double)city_n[next][0], (double)city_n[next][1],(double)city_n[next][0], (double)city_n[next][1]);
			// 経路描画
			g2.setPaint(Color.PINK);
			g2.setStroke(wideStroke);
			g2.draw(line);
			// 都市座標位置描画
			g2.setPaint(Color.BLUE);
			g2.draw(point_s);
			g2.draw(point_e);
			// 都市番号描画
			JLabel city_label_1 = new JLabel(new Integer(n).toString());
			city_label_1.setBounds(city_n[n][0] + 2, city_n[n][1] + 2, 50, 10);
			add(city_label_1);
			if (i == id.getCityOrder().size() - 2) {
				JLabel city_label_2 = new JLabel(new Integer(next).toString());
				city_label_2.setBounds(city_n[next][0] + 2, city_n[next][1] + 2, 50, 10);
				add(city_label_2);
			}
		}
	}
}
