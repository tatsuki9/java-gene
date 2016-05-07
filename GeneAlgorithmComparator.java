import java.util.Comparator;
public class GeneAlgorithmComparator implements Comparator<Invidual>{
	
	public int compare(Invidual a, Invidual b){
		double point_a = a.getPoint();
		double point_b = b.getPoint();
		
		if (point_a > point_b) {
			return -1;
		} else if(point_a == point_b) {
			return 0;
		} else {
			return 1;
		}
	}
}
