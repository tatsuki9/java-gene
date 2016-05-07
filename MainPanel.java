import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import javax.swing.JPanel;

public class MainPanel extends JPanel{
	
	private GeneAlgorithm ge;
	private ChildPanel c_pn;
	
	public ChildPanel getChildPanle(){
		return this.c_pn;
	}
	
	public MainPanel(GeneAlgorithm ge){
		this.ge = ge;
		setPreferredSize(new Dimension(100,100));
		setLayout(null);
		setBounds(0,0,300,300);
		this.c_pn = new ChildPanel(ge);
		add(c_pn);
		// System.out.println("currentThread = " + Thread.currentThread().getName());
	}

	/*
	 * 遺伝的アルゴリズムが終了して、MainPanelで再描画させようとすると、ChildPanelで上塗りされるので、ChildPanel
で処理させるようにしました。なので、以下廃止
	 */
//	@Override
//	public void paintComponent(Graphics g){
//		Graphics2D g2 = (Graphics2D)g;
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		BasicStroke wideStroke = new BasicStroke(8.0f);
//
//		int city_n[][] = this.ge.getcityN();
//		for(int n = 0; n < city_n.length - 1; n++){
//			Line2D.Double point_s = new Line2D.Double((double)city_n[n][0], (double)city_n[n][1],(double)city_n[n][0], (double)city_n[n][1]);
//			Line2D.Double point_e = new Line2D.Double((double)city_n[n+1][0], (double)city_n[n+1][1],(double)city_n[n+1][0], (double)city_n[n+1][1]);
//			g2.setPaint(Color.BLUE);
//			g2.setStroke(wideStroke);
//			g2.draw(point_s);
//			g2.draw(point_e);
//		}
//	}
}