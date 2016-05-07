import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GeneAlgorithm extends Thread{

	private int city_n[][];
	private Invidual id;
	private MainPanel m_pn;
	
	public void setMainPanel(MainPanel m_pn){
		this.m_pn = m_pn;
	}
	
	public MainPanel getMainPanel(){
		return this.m_pn;
	}
	
	public int[][] getcityN(){
		return this.city_n;
	}
	
	public void setCityN(int city_n[][]){
		this.city_n = city_n;
	}
	
	public Invidual getInvidual(){
		return this.id;
	}
	
	public void setInvidual(Invidual id){
		this.id = id;
	}
	
	public GeneAlgorithm(){
		this.city_n = new int[GeneAlgorithmConst.CITY_N][2];
		this.id = new Invidual();
		Random rnd = new Random();
		// 都市番号と対応する座標を設定
		for(int n = 0; n < GeneAlgorithmConst.CITY_N; n++){
			id.addCityOrder(n);
			this.city_n[n][0] = rnd.nextInt(GeneAlgorithmConst.X);
			this.city_n[n][1] = rnd.nextInt(GeneAlgorithmConst.Y);
		}
	}
	
	public void run(){
		// 初期集団生成
		// System.out.println("currentThread = " + Thread.currentThread().getName());
		Random rnd = new Random();
		
		int inviduals[][]       = new int[GeneAlgorithmConst.INVIDUALS][GeneAlgorithmConst.CITY_N];
		ArrayList<Invidual> ids = new ArrayList<Invidual>();
		ArrayList<Integer> list = new ArrayList<Integer>();
		int first_city = 0;
		int end_city   = 9;
		boolean flag   = this.initialize_base(list, first_city, end_city, GeneAlgorithmConst.CITY_N); 

		try {
			if(flag){
				// 個体の数
				for(int i = 0; i < GeneAlgorithmConst.INVIDUALS; i++){
					Invidual id = new Invidual();
					id.addCityOrder(first_city);
					// 都市の数
					for(int j = 0; j < GeneAlgorithmConst.CITY_N - 2; j++){
						int index = rnd.nextInt(list.size());
						id.addCityOrder(list.get(index));
						list.remove(index);
					}
					if(!this.initialize_base(list, first_city, end_city, GeneAlgorithmConst.CITY_N)){
						throw new Error("list could not initialize");
					}
					id.addCityOrder(end_city);
					ids.add(id);
				}
			} 
			else{
				throw new Error("list size is 0. this cause ids size 0");
			}
		} 
		catch(Exception e) {
			e.getMessage();
		}
		
		// 最適化経路入手
		Invidual optimized_id = this.appreciate_gene(ids);
		this.setInvidual(optimized_id);
		// 再描画
		this.getMainPanel().getChildPanle().repaint();
	}
	
	// indexlist初期化
	private boolean initialize_base(ArrayList<Integer> list, int first_city, int end_city, int initialize_n){
		if(list.size() != 0){
			return false;
		}
		for(int i = 0; i < initialize_n; i++){
			if((first_city >= 0 && end_city >= 0 && i != first_city && i != end_city)
				|| (first_city < 0 && end_city < 0)){
				list.add(i);
			}
		}
		if(list.size() <= 0){
			return false;
		}
		return true;
	}
	// 評価関数
	private Invidual appreciate_gene(ArrayList<Invidual> ids){
		for(int z = 0; z < 100; z++){
			// 個体の数
			ArrayList<Integer> city_length = new ArrayList<Integer>();
			for(int i = 0; i < ids.size(); i++){
				// 距離計測
				int sum = this.calc_total_length(ids.get(i));
				ids.get(i).setTotalLength(sum);
				// 個体ごとの都市総経路を格納
				ids.get(i).setPoint((double)1/sum);
			}
			// 評価値を降順にソート
			Collections.sort(ids, new GeneAlgorithmComparator());

			// 交叉
			this.crossing(ids);
		}
		// 最終評価
		Collections.sort(ids, new GeneAlgorithmComparator());
		return ids.get(0);
	}
	
	private int calc_total_length(Invidual id){
		int sum = 0;
		// 都市の数
		for(int j = 0; j < id.getCityOrder().size() - 1; j++){
			int dx = Math.abs(this.city_n[id.getCityOrder().get(j)][0] - this.city_n[id.getCityOrder().get(j+1)][0]);
			int dy = Math.abs(this.city_n[id.getCityOrder().get(j)][1] - this.city_n[id.getCityOrder().get(j+1)][1]);
			sum += (int)Math.sqrt((dx*dx) + (dy*dy));
		}
		return sum;
	}
	
	private void crossing(ArrayList<Invidual> ids){
		// 評価値の低い下位半分を削除
		int init = ids.size() - 1;
		int max  = ids.size() / 2;
		for(int i = init; i >= max; i--){
			ids.remove(i);
		}
		
		// ----------------交叉(親１親２、親３親４、親５親６を選出)-------------------
		ArrayList<Integer> list = new ArrayList<Integer>();
		ArrayList<Invidual> parent_pool = new ArrayList<Invidual>();
		// listを6つ分生成
		this.initialize_base(list, -1, -1, ids.size());
		// 交叉する親ペアの数
		int parent_pair = ids.size() / GeneAlgorithmConst.CROSSING;
		// 要素交換マップ
		ArrayList[][] swap_map = new ArrayList[parent_pair][GeneAlgorithmConst.CROSSING_ELEMENT_END - GeneAlgorithmConst.CROSSING_ELEMENT_START];
		// 子供用個体群
		ArrayList<Invidual> childs = new ArrayList<Invidual>();
		// 親ペア選択と部分配列の抽出
		for(int i = 0; i < parent_pair ; i++){
			// 交叉するペアとなる親の選択とベースとなる子供の生成
			this.create_parentpool_and_base_childs(parent_pool, childs, ids, list);
			// 部分配列抽出
			ArrayList[] swap_element = this.extract_sublist(parent_pool);
			/*
			 * (1)親ペア3つ
			 * array(
			 * 		array(1,3,4,6,8),
			 * 		array(2,4,6,9,1)
			 * ),
			 * array(
			 * 		array(1,3,4,6,8),
			 * 		array(2,4,6,9,1)
			 * ),
			 * array(
			 * 		array(1,3,4,6,8),
			 * 		array(2,4,6,9,1)
			 * ),
			 * (2)上記の大枠の配列3つに対して、それぞれの子配列の要素ごとにペアを作っていく
			 * swap_map[i][j] = array(
			 * 		array(1,2),
			 * 		array(3,4),
			 * 		array(4,6),
			 * 		array(6,9),
			 * 		array(8,1),
			 * ),
			 * array(
			 * 		array(1,2),
			 * 		array(3,4),
			 * 		array(4,6),
			 * 		array(6,9),
			 * 		array(8,1),
			 * ),
			 * array(
			 * 		array(1,2),
			 * 		array(3,4),
			 * 		array(4,6),
			 * 		array(6,9),
			 * 		array(8,1),
			 * ),
			 */
			// 要素番号ごとにマージしていく
			for(int j = 0; j < GeneAlgorithmConst.CROSSING_ELEMENT_END - GeneAlgorithmConst.CROSSING_ELEMENT_START; j++){
				swap_map[i][j] = new ArrayList();
				for(int k = 0; k < swap_element.length; k++){
					swap_map[i][j].add(swap_element[k].get(j));
				}
			}
			parent_pool.clear();
		}
		
		// 交叉実行
		this.execute(childs, swap_map, parent_pair);
		
		// 親個体に子供個体をマージ
		ids.addAll(childs);
	}
	
	private void create_parentpool_and_base_childs(ArrayList<Invidual> parent_pool, ArrayList<Invidual> childs, ArrayList<Invidual> ids, ArrayList<Integer> list){
		Random rnd = new Random();
		// 交叉で使う２組の親を選択
		for(int j = 0; j < GeneAlgorithmConst.CROSSING; j++){
			int index = rnd.nextInt(list.size());
			int id_n = list.get(index);
			parent_pool.add(ids.get(id_n));
			// 生成用の子供の個体準備
			// ↓ダメな例 => シャローコピー
//			Invidual child = new Invidual();
//			child.setCityOrder(ids.get(id_n).getCityOrder());
			// ↓okな例 => ディープコピー
			Invidual child = ids.get(id_n).clone();
			childs.add(child);
			list.remove(index);	
		}
	}
	
	private ArrayList[] extract_sublist(ArrayList<Invidual> parent_pool){
		ArrayList[] swap_element = new ArrayList[GeneAlgorithmConst.CROSSING];
		// 部分配列抽出
		for(int j = 0; j < GeneAlgorithmConst.CROSSING; j++){
			ArrayList<Integer> sublist = new ArrayList<Integer>(parent_pool.get(j).getCityOrder().subList(GeneAlgorithmConst.CROSSING_ELEMENT_START, GeneAlgorithmConst.CROSSING_ELEMENT_END));
			swap_element[j] = new ArrayList();
			for(int k = 0; k < sublist.size(); k++){
				swap_element[j].add(sublist.get(k));
				// System.out.print(swap_element[j].get(k) + " ");
			}
			// System.out.println();
		}
		return swap_element;
	}
	
	private void execute(ArrayList<Invidual> childs, ArrayList[][] swap_map, int parent_pair){
		// 親ペア分(3ペア),それぞれ2回ごと子供に対して交叉を行う
		for(int i = 0; i < parent_pair; i++){
			for(int j = 0; j < GeneAlgorithmConst.CROSSING; j++){
				// 親ペア分
				for(int k = 0; k < swap_map[i].length; k++){
					swap_by_element(childs.get(i*(GeneAlgorithmConst.CROSSING) + j).getCityOrder(), swap_map[i][k]);
				}
			}
		}
	}
	
	private <T> void swap_by_element(ArrayList<T> city_order, ArrayList<T> swap_elements){
		T tmp = city_order.get(city_order.indexOf(swap_elements.get(0)));
		int index1 = city_order.indexOf(swap_elements.get(0));
		int index2 = city_order.indexOf(swap_elements.get(1));
		city_order.set(index1, city_order.get(city_order.indexOf(swap_elements.get(1))));
		city_order.set(index2, tmp);
	}
}

class Invidual implements Comparable<Invidual>{
	private int total_length;
	private double point;
	private ArrayList<Integer> city_order;

	public Invidual(){
		this.city_order = new ArrayList<Integer>();
	}
	
	public Invidual clone(){
		Invidual clone_id = new Invidual();
		clone_id.city_order = (ArrayList<Integer>) this.city_order.clone();
		return clone_id;
	}
	
	public void addCityOrder(int n){
		this.city_order.add(n);
	}
	
	public ArrayList<Integer> getCityOrder(){
		return this.city_order;
	}
	
	public void setCityOrder(ArrayList<Integer> city_order){
		this.city_order = city_order;
	}
	
	public void setTotalLength(int total_length){
		this.total_length = total_length;
	}
	
	public int getTotalLength(){
		return this.total_length;
	}

	public void setPoint(double point){
		this.point = point;
	}

	public double getPoint(){
		return this.point;
	}
	
	@Override
	public int compareTo(Invidual o) {
		// TODO Auto-generated method stub
		return this.total_length - o.total_length;
	}
}
