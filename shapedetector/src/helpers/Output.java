package helpers;

public class Output {

	/**
	 * Helper function for printing arrays.
	 * 
	 * @param array
	 * @return
	 */
	public static String toString(int[] array) {
		if (array == null || array.length == 0) {
			return "";
		}

		String str = "" + array[0];
		for (int i = 1; i < array.length; i++) {
			str += ", " + array[i];
		}
		return str;
	}

	/**
	 * Helper function for printing arrays.
	 * 
	 * @param array
	 * @return
	 */
	public static String toString(double[] array) {
		if (array == null || array.length == 0) {
			return "";
		}

		String str = "" + array[0];
		for (int i = 1; i < array.length; i++) {
			str += ", " + array[i];
		}
		return str;
	}

}
