package helpers;

import java.util.Iterator;
import java.util.List;

public class Misc {

	public static double[] toArray(List<Double> list) {
		int n = list.size();
		double[] array = new double[n];
		Iterator<Double> iterator = list.iterator();
		for (int i = 0; i < n; i++) {
			array[i] = iterator.next();
		}
		return array;
	}

}
