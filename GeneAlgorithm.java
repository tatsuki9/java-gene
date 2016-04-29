import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GeneAlgorithm extends Thread{

	private int city_n[][];
	
	public int[][] get_city_n(){
		return this.city_n;
	}
	
	public void set_city_n(int city_n[][]){
		this.city_n = city_n;
	}
	
	public GeneAlgorithm(){
		this.city_n = new int[GeneAlgorithmConst.CITY_N][2];
		
		Random rnd = new Random();
		for(int n = 0; n < GeneAlgorithmConst.CITY_N; n++){
			this.city_n[n][0] = rnd.nextInt(GeneAlgorithmConst.X);
			this.city_n[n][1] = rnd.nextInt(GeneAlgorithmConst.Y);
		}
	}
	
	public void run(){
		// 初期集団生成
		System.out.println("currentThread = " + Thread.currentThread().getName());
		Random rnd = new Random();
		/*
		 * array(
		 *  //個体の数 => 都市のインデックス番号
		 * 	0 => (1,2,3,4,5,6,7,8,9),
		 *  1 => (2,1,3,4,5,6,7,8,9),
		 *  ・
		 *  ・
		 *  ・
		 * )
		 */
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
		
		for(int i = 0; i < ids.size(); i++){
			System.out.println(ids.get(i).getCityOrder());
		}
		
		// (2)距離計算,評価
//		int warrior_chosen[][] = this.appreciate_gene(ids);
		this.appreciate_gene(ids);
		// (3)下位6つは淘汰
		
		// (4)残り6つで２回交叉
		
		// (5)子供に一定確率で突然k変異を起こさせる
		
		// (6) (2)〜（５）を繰り返す
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
	private void appreciate_gene(ArrayList<Invidual> ids){

		for(int z = 0; z < 100; z++){
			int sum = 0;
			// 個体の数
			ArrayList<Integer> city_length = new ArrayList<Integer>();

			for(int i = 0; i < ids.size(); i++){
				Invidual id = ids.get(i);
				// 都市の数
				for(int j = 0; j < ids.get(i).getCityOrder().size() - 1; j++){
					int dx = Math.abs(this.city_n[id.getCityOrder().get(j)][0] - this.city_n[id.getCityOrder().get(j+1)][0]);
					int dy = Math.abs(this.city_n[id.getCityOrder().get(j)][1] - this.city_n[id.getCityOrder().get(j+1)][1]);
					sum += (int)Math.sqrt((dx*dx) + (dy*dy));
				}
				// 個体ごとの都市総経路を格納
				id.setLength(sum);
				System.out.print(sum + " ");
			}
			System.out.println();
			/*
			System.out.println("before");
			for(int i = 0; i < ids.size(); i++){
				System.out.print(ids.get(i).getLength() + " , ");
			}
			System.out.println();
			*/
			
			// 評価値を降順にソート
			Collections.reverse(ids);
			/*
			System.out.println("after sort");
			for(int i = 0; i < ids.size(); i++){
				System.out.print(ids.get(i).getLength() + " , ");
			}
			*/
			
			// 評価値の低い下位半分を削除
			int init = ids.size() - 1;
			int max  = ids.size() / 2;
			for(int i = init; i >= max; i--){
				ids.remove(i);
			}
			/*
			System.out.println();
			System.out.println("after delete half");
			for(int i = 0; i < ids.size(); i++){
				System.out.print(ids.get(i).getLength() + " , ");
			}		
			System.out.println();
			*/
			// ----------------交叉(親１親２、親３親４、親５親６を選出)-------------------
			ArrayList<Integer> list = new ArrayList<Integer>();
			ArrayList<Invidual> parent_pool = new ArrayList<Invidual>();

			/*
			System.out.println("view ids cityorder");
			for(int i = 0; i < ids.size(); i++){
				System.out.println(ids.get(i).getCityOrder());
			}
			*/
			// System.out.println("sublist is ");
			// listを6つ分生成
			this.initialize_base(list, -1, -1, ids.size());
			Random rnd = new Random();
			// System.out.println("parent size is " + list.size());
			// 要素交換マップ
			ArrayList[][] swap_map = new ArrayList[ids.size() / GeneAlgorithmConst.CROSSING][GeneAlgorithmConst.CROSSING_ELEMENT_END - GeneAlgorithmConst.CROSSING_ELEMENT_START];
			// 子供用個体群
			ArrayList<Invidual> childs = new ArrayList<Invidual>();
			// 親ペア選択と部分配列の抽出
			for(int i = 0; i < ids.size() / GeneAlgorithmConst.CROSSING ; i++){
				// 交叉で使う２組の親を選択
				for(int j = 0; j < GeneAlgorithmConst.CROSSING; j++){
					int index = rnd.nextInt(list.size());
					int id_n = list.get(index);
					parent_pool.add(ids.get(id_n));
					// 生成用の子供の個体準備
					Invidual child = new Invidual();
					child.setCityOrder(ids.get(id_n).getCityOrder());
					childs.add(child);
					list.remove(index);	
				}
				/*
				System.out.println("parent is.....");
				for(int j = 0; j < parent_pool.size(); j++){
					System.out.println(parent_pool.get(j).getCityOrder());
				}
				System.out.println("child is");
				*/
				ArrayList[] swap_element = new ArrayList[GeneAlgorithmConst.CROSSING];
				// Invidual tmp = new Invidual();
				// ArrayList<Invidual> childs = new ArrayList<Invidual>();
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
				// 比較
				/*
				 * swap_element = array(
				 * 	array('1,4,5,7'),
				 * 	array('3,2,7,5'),
				 * )
				 */
				// 要素番号ごとにマージしていく
				for(int j = 0; j < GeneAlgorithmConst.CROSSING_ELEMENT_END - GeneAlgorithmConst.CROSSING_ELEMENT_START; j++){
					swap_map[i][j] = new ArrayList();
					for(int k = 0; k < swap_element.length; k++){
						swap_map[i][j].add(swap_element[k].get(j));
					}
				}
//				System.out.println(Arrays.deepToString(swap_map));
				parent_pool.clear();
			}
			/*
			System.out.println("-------swap_map--------");
			System.out.println(Arrays.deepToString(swap_map));
			System.out.println("-------before childs--------");
			for(int i = 0; i < childs.size(); i++){
				System.out.println(childs.get(i).getCityOrder());
			}
			System.out.println("-------swap childs--------");
			*/
			// 親ペア分(3ペア),それぞれ2回ごと子供に対して交叉を行う
			for(int i = 0; i < ids.size() / GeneAlgorithmConst.CROSSING; i++){
				for(int j = 0; j < GeneAlgorithmConst.CROSSING; j++){
					// 親ペア分
					for(int k = 0; k < swap_map[i].length; k++){
						swap_by_element(childs.get(i*(GeneAlgorithmConst.CROSSING) + j).getCityOrder(), swap_map[i][k]);
					}
					// System.out.println();
				}
			}
			/*
			System.out.println("-------after childs--------");
			for(int i = 0; i < childs.size(); i++){
				System.out.println(childs.get(i).getCityOrder());
			}
			System.out.println("-------knights--------");
			for(int i = 0; i < ids.size(); i++){
				System.out.println(ids.get(i).getCityOrder());
			}
			*/

			// 親個体に子供個体をマージ
			ids.addAll(childs);
			/*
			System.out.println("-------knights--------");
			for(int i = 0; i < ids.size(); i++){
				System.out.println(ids.get(i).getCityOrder());
			}
			*/
		}
		//		return list;
	}
	
	private <T> void swap_by_element(ArrayList<T> city_order, ArrayList<T> swap_elements){
		T tmp = city_order.get(city_order.indexOf(swap_elements.get(0)));
		int index1 = city_order.indexOf(swap_elements.get(0));
		int index2 = city_order.indexOf(swap_elements.get(1));
		// System.out.print(swap_elements);
//		System.out.print(city_order.indexOf(swap_elements.get(0)));
//		System.out.println(city_order.indexOf(swap_elements.get(1)));
		city_order.set(index1, city_order.get(city_order.indexOf(swap_elements.get(1))));
//		System.out.println(city_order);
		// 1のインデックス番号を現在のcity_orderから取ると、2になる.つまり、以下はindex=2を5に戻す動作
		city_order.set(index2, tmp);
		// System.out.println(city_order);
	}
	
	// 距離計測
	private int calc_distance(){
		
		return 1;
	}
}

class Invidual implements Comparable<Invidual>{
	private int length;
	private ArrayList<Integer> city_order;
	
	public Invidual(){
		this.city_order = new ArrayList<Integer>();
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

	public void setLength(int sum){
		this.length = sum;
	}

	public int getLength(){
		return this.length;
	}
	
	@Override
	public int compareTo(Invidual o) {
		// TODO Auto-generated method stub
		return this.length - o.length;
	}
	
}
