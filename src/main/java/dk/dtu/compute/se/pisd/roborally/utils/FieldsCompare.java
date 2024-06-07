package dk.dtu.compute.se.pisd.roborally.utils;

import java.util.List;
import java.lang.reflect.Field;
import static dk.dtu.compute.se.pisd.roborally.utils.ArrayCompare.compareArray;

public class FieldsCompare<T> {
	public boolean CompareFields (T t_one, T t_two, List<String> ignoreVariables) throws CompareException {
 		Field[] board_fields = t_one.getClass().getDeclaredFields();
		if ((t_one == null || t_two == null)) return (t_one == null && t_two == null);

		for (int i = 0; i < board_fields.length; i++) {
			if (ignoreVariables.contains(board_fields[i].getName())) { continue; }
			if (board_fields[i].getName().startsWith("$SWITCH_TABLE")) { continue; } // ignore switch tables which is counted with fields
			//System.out.println("Checking: " + board_fields[i].getName());
			try {
 				board_fields[i].setAccessible(true);
				Object comp = board_fields[i].get(t_one);
				Object def = board_fields[i].get(t_two);

				if (comp == null && def == null) { // Check if both are null
					continue;
				} else if (comp == null || def == null) {
					throw new CompareException("Variable \"" + board_fields[i].getName() + "\" check failed, single variable is null: " + (comp == null  ? "null" : comp.toString()) + ":" + (def == null  ? "null" : def.toString()) );
				} else { // No null pointers
					if (def.getClass().isArray()) {
						compareArray(def, comp);
					} else {
						if (!comp.equals(def)) throw new CompareException("Variable \"" + board_fields[i].getName() + "\" check failed, variables are not equal: " + comp.toString() + ":" + def.toString());
					}
				}
			} catch (IllegalAccessException e) {
				System.out.println("Debug: " + board_fields[i].getName() + " Error: " + e.getMessage());
				throw new CompareException("IllegalAccessException - " + board_fields[i].getName() + " Error: " + e.getMessage());
			}
		}
		return true;
	}
}
