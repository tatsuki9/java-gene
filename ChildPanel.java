import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.*;
import java.awt.RenderingHints;

public class ChildPanel extends JPanel{
	GeneAlgorithm ge;
	
	public ChildPanel(GeneAlgorithm ge){
		setPreferredSize(new Dimension(100,100));
		// デフォルトレイアウトマネージャ無効
		setLayout(null);
		// 親パネルの相対位置を考慮して
		setBounds(0,0,300,300);
		// 透過
		setOpaque(false);
		this.ge = ge;
		System.out.println("currentThread = " + Thread.currentThread().getName());
	}
	
	// 描画
	@Override
	public void paintComponent(Graphics g){
		System.out.println("currentThread = " + Thread.currentThread().getName());
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		BasicStroke wideStroke = new BasicStroke(4.0f);
		
		// 巡回セールスマン計算されたgeクラスを使って描画
		int city_n[][] = this.ge.get_city_n();
		for(int n = 0; n < city_n.length - 1 ; n++){
			Line2D.Double line = new Line2D.Double((double)city_n[n][0], (double)city_n[n][1], (double)city_n[n+1][0], (double)city_n[n+1][1]);
			Line2D.Double point_s = new Line2D.Double((double)city_n[n][0], (double)city_n[n][1],(double)city_n[n][0], (double)city_n[n][1]);
			Line2D.Double point_e = new Line2D.Double((double)city_n[n+1][0], (double)city_n[n+1][1],(double)city_n[n+1][0], (double)city_n[n+1][1]);;
			// 経路描画
			g2.setPaint(Color.PINK);
			g2.setStroke(wideStroke);
			g2.draw(line);
		}
	}
}
