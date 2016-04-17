import java.util.ArrayList;
import java.util.Arrays;
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
		int inviduals[][] = new int[GeneAlgorithmConst.INVIDUALS][GeneAlgorithmConst.CITY_N];
		ArrayList<Integer> list = new ArrayList<Integer>();
		boolean flag = this.initialize_base(list); 
		int base[]        = new int[]{0,1,2,3,4,5,6,7,8,9,10,11};
		
		try {
			if(flag){
				for(int i = 0; i < inviduals.length; i++){
					for(int j = 0; j < inviduals[i].length; j++){
						int index = rnd.nextInt(list.size());
						inviduals[i][j] = index;
						System.out.println(index);
						list.remove(index);
					}
					if(!!this.initialize_base(list)){
						throw new Error("hgoege");
					}
				}
			} 
			else{
				throw new Error("hogehoge");
			}
		} 
		catch(Exception e) {
			e.getMessage();
		}
		
		
		System.out.println(Arrays.deepToString(inviduals));
		
		// (2)���������������������
		
		// (3)���������������������������������������
		
		// (4)���������6���������������������2���������������2���������������6���������������������
		
		// (5)������������������������������������
		
		// (6)(2)������������������������������������������
	}
	
	// ���������������������������
	public boolean initialize_base(ArrayList<Integer> list){
		if(list.size() != 0){
			return false;
		}
		
		for(int i = 0; i < GeneAlgorithmConst.INVIDUALS; i++){
			list.add(i);
		}
		
		return true;
	}
}
