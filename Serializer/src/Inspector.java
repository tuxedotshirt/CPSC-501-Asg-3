import java.util.*;
import java.lang.reflect.*;

public class Inspector {
	public Inspector() {
	}

	public void inspect(Object obj, boolean recursive) throws IllegalArgumentException, IllegalAccessException {
		Vector objectsToInspect = new Vector();
		Class objClass = obj.getClass();

		System.out.println("Declaring class: " + objClass.getSimpleName());
		System.out.println("Superclass: " + objClass.getSuperclass().getSimpleName());

		inspectFields(obj, objClass, objectsToInspect);

		if (recursive)
			inspectFieldClasses(obj, objClass, objectsToInspect, recursive);
	}

	private void inspectFields(Object obj, Class objClass,
			Vector objectsToInspect) {
		System.out.println();
		System.out.println(objClass.getSimpleName()
				+ " fields:");
		if (objClass.getDeclaredFields().length >= 1) {
			Field[] fields = objClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field aField = fields[i];
				aField.setAccessible(true);
				if (!aField.getType().isPrimitive())
					objectsToInspect.addElement(aField);

				printFields(obj, aField);
			}
		} else {
			System.out.println("No fields detected");
		}

		if (objClass.getSuperclass() != null)
			inspectFields(obj, objClass.getSuperclass(), objectsToInspect);
	}

	private void printFields(Object obj, Field aField) {
		try {
			if (aField.getType().isArray()) {
				Object array = aField.get(obj);
				int length = Array.getLength(array);
				System.out.print("Field: '" + aField.getName() + "' = {");
				for (int i=0;i<length-1;i++){
					Object element = Array.get(array, i);
					System.out.print(element+",");
				}
				Object element = Array.get(array, length-1);
				System.out.print(element);
				System.out.println("}"
						+ "'\n\t-Type: " + aField.getType().getComponentType()
						+ "\n\t-Modifier: "
						+ Modifier.toString(aField.getModifiers()));
			} else {
				System.out.println("Field: '" + aField.getName() + "' = "
						+ aField.get(obj) + "\n\t-Type: " + aField.getType()
						+ "\n\t-Modifier: "
						+ Modifier.toString(aField.getModifiers()));
			}
		} catch (Exception e) {
		}
	}

	private void inspectFieldClasses(Object obj, Class objClass,
			Vector objectsToInspect, boolean recursive) throws IllegalArgumentException, IllegalAccessException {

		if (objectsToInspect.size() > 0)
			System.out.println("'" + objClass.getSimpleName() + "' field Classes:");

		Enumeration e = objectsToInspect.elements();
		while (e.hasMoreElements()) {
			Field f = (Field) e.nextElement();
			System.out.println("Field: '" + f.getName() + "'");
				inspect(f.get(obj), recursive);	 
		}
	}
}
