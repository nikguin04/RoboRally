package dk.dtu.compute.se.pisd.roborally.utils;

public class ArrayCompare {
	public static boolean compareArray(Object def, Object comp) {
		if (def.getClass() != comp.getClass()) { throw new IllegalArgumentException("Array classes are different"); }
		if (def.getClass().isArray() && comp.getClass().isArray()) {
			Object[] defarray = (Object[])def;
			Object[] comparray = (Object[])comp;
			if (defarray.length != comparray.length) { throw new IllegalArgumentException("Arrays differ in size: " + defarray.length + " - " + comparray.length); }
			if (defarray[0].getClass().isArray() && comparray[0].getClass().isArray()) { // check for nested array
				for (int i = 0; i < defarray.length; i++) {
					compareArray(defarray[i], comparray[i]);
				}
			} else {
				for (int i = 0; i < defarray.length; i++) {
					boolean single_object_comparison = defarray[i].equals(comparray[i]);
					//System.out.println("Comp result" + i + ": " + single_object_comparison);
					if (!single_object_comparison) throw new IllegalArgumentException("Objects are not equal: " + defarray[i] + " != " + comparray[i]);
				}
				return true;
			}
			return true;
		}
		return false;
	}
}
