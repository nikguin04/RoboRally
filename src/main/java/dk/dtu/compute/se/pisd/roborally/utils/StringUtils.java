package dk.dtu.compute.se.pisd.roborally.utils;

public class StringUtils {
	public static int[] intarrFromCommaStr(String str) {
		String[] arr = str.split(",");
		int[] iarr = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			iarr[i] = Integer.parseInt(arr[i]);
		}
		return iarr;
	}
}
