import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Serializer {
	static Map im = new IdentityHashMap();
	static File file = new File("send.xml");
	
	public static Document serialize(Object obj) throws IllegalArgumentException, IllegalAccessException {
		Document doc = new Document();
		Element root = new Element("serialized");
		doc.setRootElement(root);

		if(obj.getClass().getName().equals("SimpleObject"))
			serializeSimpleObject(obj, doc);
		else if(obj.getClass().getName().equals("ReferenceObject")) {
			serializeReferenceObject(obj, doc);
		}
		else if(obj.getClass().getName().equals("PrimitiveArray")) {
			serializePrimitiveArray(obj,doc);
		}
		else if(obj.getClass().getName().equals("ReferenceArray")) {
			serializeReferenceArray(obj,doc);
		}
		else if(obj.getClass().getName().equals("CollectionClassObject")) {
			serializeCollectionClassObject(obj,doc);
		}
		XMLOutputter outputter = new XMLOutputter(); 
	    try {
	    	outputter.setFormat(Format.getPrettyFormat());
			outputter.output(doc, System.out);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			outputter.output(doc, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}       

		return doc;
	}
	
	private static void serializeCollectionClassObject(Object obj, Document doc) {
	}
	
	@SuppressWarnings("rawtypes")
	private static Document serializeReferenceArray(Object obj, Document doc) throws IllegalArgumentException, IllegalAccessException {
		Class cls = obj.getClass();
		Field[] fields = cls.getDeclaredFields();
		Element oElt = new Element("object");
		
		oElt.setAttribute("class", cls.getName());
		
		String id = Integer.toString(im.size());
		im.put(obj, id);
		oElt.setAttribute("id", id);
		
		doc.getRootElement().addContent(oElt);

		
		for(int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Object fieldObj = field.get(obj);
			Element fElt = new Element("field");
			fElt.setAttribute("name", fields[i].getName());
			
			Class<?> declClass = fields[i].getDeclaringClass();
			fElt.setAttribute("declaringclass", declClass.getName());
			Class tclass = fieldObj.getClass();
			
			Object[] objArray;
			objArray =  (Object[]) fieldObj;

			//serialize array
			id = Integer.toString(im.size());
			im.put(fieldObj, id);
			Element reference = new Element("reference");
			reference.setText(id);
			fElt.addContent(reference);
			
			Element objElt = new Element("object");
			objElt.setAttribute("class", tclass.getName());
			
			id = Integer.toString(im.size());
			im.put(objArray[i], id);
			objElt.setAttribute("id", id);
			objElt.setAttribute("length", Integer.toString(objArray.length));
			reference.addContent(objElt);
			
			//get simpleObjects
			for(int j =0; j < objArray.length; j++){
	
				Element simpleElement = new Element("object");
	            Object index = objArray[j];

	            //serialize a simpleobject
	    		Class simpleClass = index.getClass();
	    		Field[] simpleFields = simpleClass.getDeclaredFields();
	    		simpleElement.setAttribute("class", index.getClass().getName());
	    		
	    		id = Integer.toString(im.size());
	    		System.out.println(Integer.toString(im.size()));
				im.put(index, id);
				simpleElement.setAttribute("id", id);
				Element ref = new Element("reference");
				ref.setText(id);
				
	    		for(int k = 0; k < simpleFields.length; k++) {
	    			Field[] fFields = simpleClass.getDeclaredFields();
	    			Element ffElt = new Element("field");
	    			Element faElt = new Element("value");
	    			ffElt.setAttribute("field", fFields[k].getName());
	    			int value = simpleFields[k].getInt(objArray[j]);
	    			String valueString = Integer.toString(value);
	    			faElt.setText(valueString);
	    			ffElt.addContent(faElt);
	    			simpleElement.addContent(ffElt);
	    		}
	    		ref.addContent(simpleElement);
	    		objElt.addContent(ref);
			}
			
			oElt.addContent(fElt);
		}
		return doc;
	}

	@SuppressWarnings("rawtypes")
	private static Document serializePrimitiveArray(Object obj, Document doc) throws IllegalArgumentException, IllegalAccessException {
		Class cls = obj.getClass();
		Field[] fields = cls.getDeclaredFields();
		Element oElt = new Element("object");
		oElt.setAttribute("class", cls.getName());
		String id = Integer.toString(im.size());
		im.put(obj, id);
		oElt.setAttribute("id", id);
		doc.getRootElement().addContent(oElt);
		
		for(int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Object fieldObj = field.get(obj);
			
			Element fElt = new Element("field");
			fElt.setAttribute("name", fields[i].getName());
			Class<?> declClass = fields[i].getDeclaringClass();
			Class c = fields[i].getClass();
			fElt.setAttribute("declaringclass", declClass.getName());
			Class fieldtype = fields[i].getType();
			
			Element objElt = new Element("object");
			id = Integer.toString(im.size());
			im.put(obj, id);
			
			objElt.setAttribute("class", fieldtype.getName());
			objElt.setAttribute("id", id);
			
			Object[] objArray;
			
			int arrayLength = Array.getLength(fieldObj);
			objArray = new Object[arrayLength];
			objElt.setAttribute("length", Integer.toString(objArray.length));
			//add objElt to felt
				fElt.addContent(objElt);
				for(int a = 0; a < arrayLength; a++){
					objArray[a] = Array.get(fieldObj, a);
					Element value = new Element("value");
					
					String val = objArray[a].toString();

					value.setText(val);

					objElt.addContent(value);
				}
			oElt.addContent(fElt);

		}
		return doc;
	}

	@SuppressWarnings("rawtypes")
	private static Document serializeSimpleObject(Object obj, Document doc) throws IllegalArgumentException, IllegalAccessException {
		Class cls = obj.getClass();
		Field[] fields = cls.getDeclaredFields();
		Element oElt = new Element("object");
		oElt.setAttribute("class", cls.getName());
		String id = Integer.toString(im.size());
		im.put(obj, id);
		oElt.setAttribute("id", id);
		
		doc.getRootElement().addContent(oElt);
		for(int i = 0; i < fields.length; i++) {
			Element fElt = new Element("field");
			Element aElt = new Element("value");
			fElt.setAttribute("name", fields[i].getName());
			fElt.setAttribute("declaringclass", cls.getName());
			int value = fields[i].getInt(obj);
			String valueString = Integer.toString(value);
			aElt.setText(valueString);
			fElt.addContent(aElt);
			oElt.addContent(fElt);
		}
		return doc;
	}
	
	@SuppressWarnings("rawtypes")
	private static Document serializeReferenceObject(Object obj, Document doc) throws IllegalArgumentException, IllegalAccessException {
		Class cls = obj.getClass();
		Field[] fields = cls.getDeclaredFields();
		Element oElt = new Element("object");
		oElt.setAttribute("class", cls.getName());
		String id = Integer.toString(im.size());
		im.put(obj, id);
		oElt.setAttribute("id", id);
		doc.getRootElement().addContent(oElt);

		//get simpleObject
		for(int i = 0; i < fields.length; i++) {
			
			SimpleObject test = (SimpleObject) fields[i].get(obj);
			Class testClass = test.getClass();
			Element fElt = new Element("field");
			Element ref = new Element("reference");

			id = Integer.toString(im.size());
			im.put(test, id);
			ref.setText(id);
			
			fElt.setAttribute("name", test.getClass().toString());
			fElt.setAttribute("declaringclass", cls.getName());
			
			fElt.addContent(ref);
			Field[] of = testClass.getDeclaredFields();
			for(int j = 0; j < of.length; j++) {
				Element sElt = new Element("field");
				sElt.setAttribute("name", of[j].getName());
				sElt.setAttribute("declaringclass", testClass.getName());
				
				Element aElt = new Element("value");			
				int value = of[j].getInt(test);
				String valueString = Integer.toString(value);
				aElt.setText(valueString);
				sElt.addContent(aElt);
				ref.addContent(sElt);
			}
			oElt.addContent(fElt);
		}
		return doc;
	}
	
}
