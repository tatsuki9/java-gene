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
		
		int sum = 0;
		// 個体の数
		// Intergerクラスをフィールドとしてもつ配列クラスを宣言
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
		}
		
		System.out.println("before");
		for(int i = 0; i < ids.size(); i++){
			System.out.print(ids.get(i).getLength() + " , ");
		}
		
		System.out.println();
		
		// 評価値を降順にソート
		Collections.reverse(ids);
		
		System.out.println("after sort");
		for(int i = 0; i < ids.size(); i++){
			System.out.print(ids.get(i).getLength() + " , ");
		}

		// 評価値の低い下位半分を削除
		int init = ids.size() - 1;
		int max  = ids.size() / 2;
		for(int i = init; i >= max; i--){
			ids.remove(i);
		}
		
		System.out.println();
		
		System.out.println("after delete half");
		for(int i = 0; i < ids.size(); i++){
			System.out.print(ids.get(i).getLength() + " , ");
		}		
		System.out.println();
		
		// ----------------交叉(親１親２、親３親４、親５親６を選出)-------------------
		ArrayList<Integer> list = new ArrayList<Integer>();
		ArrayList<Invidual> parent_pool = new ArrayList<Invidual>();

		System.out.println("view ids cityorder");
		for(int i = 0; i < ids.size(); i++){
			System.out.println(ids.get(i).getCityOrder());
		}
		
		System.out.println("sublist is ");
		// listを6つ分生成
		this.initialize_base(list, -1, -1, ids.size());
		Random rnd = new Random();
		
		System.out.println("parent size is " + list.size());
		
		// 親ペア選択
		for(int i = 0; i < ids.size(); i++){
			for(int j = 0; j < GeneAlgorithmConst.CROSSING; j++){
				int index = rnd.nextInt(list.size());
				int id_n = list.get(index);
				System.out.println(id_n);
				parent_pool.add(ids.get(id_n));
				list.remove(index);	
			}
			// 部分配列抽出
			for(int j = 0; j < GeneAlgorithmConst.CROSSING; j++){
				ArrayList<Integer> sublist = new ArrayList<Integer>(parent_pool.get(j).getCityOrder().subList(2, 10));
				// 親１と親２の部分配列を比較。
			}
			parent_pool.clear();
		}


		
		
		
//		List city_length_list = (List) Arrays.asList(city_length);
		
//		System.out.println("city_length is ");
//		System.out.println(Arrays.toString(city_length));
//		Collections.reverse(city_length_list);
//		Arrays.sort(city_length, Collections.reverseOrder());
//		System.out.println("city_length is ");
//		System.out.println(Arrays.toString(city_length_list));
		
//		return list;
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
