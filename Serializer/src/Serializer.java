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
	@SuppressWarnings("rawtypes")
	static Map im = new IdentityHashMap();
	static File file = new File("C:\\Users\\Don\\Documents\\send.xml");
	
	public static Document serialize(Object obj) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Document serializeReferenceArray(Object obj, Document doc) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Class cls = obj.getClass();
		//Field[] fields = cls.getDeclaredFields();
		Element oElt = new Element("object");
		oElt.setAttribute("class", cls.getName());
		String id = Integer.toString(im.size());
		im.put(obj, id);
		oElt.setAttribute("id", id);		
		doc.getRootElement().addContent(oElt);

		Element fRef = new Element("reference");
		id = Integer.toString(im.size());
		fRef.setText(id);
		
		Field field = cls.getDeclaredField("fieldOne");
		Object fieldObj = field.get(obj);
		Element fElt = new Element("field");
		fElt.setAttribute("name", field.getName());
		fElt.setAttribute("declaringclass", field.getDeclaringClass().getName());
		fElt.addContent(fRef);
		oElt.addContent(fElt);
		
		id = Integer.toString(im.size());
		im.put(fieldObj, id);
		
		Element fObj = new Element("object");
		fObj.setAttribute("class", fieldObj.getClass().getName());
		fObj.setAttribute("id", id);
		/////
		Object fArr = (Object) field.get(obj);
		int arrayLength = Array.getLength(fArr);
		String aLength = Integer.toString(arrayLength);
		fObj.setAttribute("length", aLength);
		/////
		doc.getRootElement().addContent(fObj);
		Object[] objArray;
		int arrLen = Array.getLength(fArr);
		objArray = new Object[arrLen];
		
		for(int a = 0; a < arrLen; a++){
			objArray[a] = Array.get(fArr, a);
			Element oRef = new Element("reference");
			id = Integer.toString(im.size());
			im.put(objArray[a], id);
			oRef.setText(id);
			fObj.addContent(oRef);
			
			Element sElt = new Element("object");
					
			Class fCls = objArray[a].getClass();
			
			Field[] fields = fCls.getDeclaredFields();
			
			sElt.setAttribute("class", fCls.getName());
			sElt.setAttribute("id", id);

			for(int i = 0; i < fields.length; i++) {
				Element fdElt = new Element("field");
				Element aElt = new Element("value");
				fdElt.setAttribute("name", fields[i].getName());
				fdElt.setAttribute("declaringclass", fCls.getName());
				int value = fields[i].getInt(objArray[a]);
				
				String valueString = Integer.toString(value);
				aElt.setText(valueString);
				fdElt.addContent(aElt);
				sElt.addContent(fdElt);
			}
			doc.getRootElement().addContent(sElt);
		}
		return doc;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Document serializePrimitiveArray(Object obj, Document doc) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Class cls = obj.getClass();
		
		Field ofield = cls.getDeclaredField("fieldOne");
		Element oElt = new Element("object");
		oElt.setAttribute("class", cls.getName());
		String id = Integer.toString(im.size());
		im.put(obj, id);
		oElt.setAttribute("id", id);
		Element fdElt = new Element("field");
		fdElt.setAttribute("name", ofield.getName());
		fdElt.setAttribute("declaringclass", cls.getName());
		
		Object fArr = (Object) ofield.get(obj);
		oElt.addContent(fdElt);
		Element ref = new Element("reference");
		id = Integer.toString(im.size());
		im.put(fArr, id);
		ref.setText(id);
		fdElt.addContent(ref);
		doc.getRootElement().addContent(oElt);
		
		Element fObj = new Element("object");
		fObj.setAttribute("class", fArr.getClass().getName());
		fObj.setAttribute("id", id);
		int arrayLength = Array.getLength(fArr);
		String aLength = Integer.toString(arrayLength);
		fObj.setAttribute("length", aLength);
		doc.getRootElement().addContent(fObj);
		
		
		Object[] objArray;
		int arrLen = Array.getLength(fArr);
		objArray = new Object[arrLen];
		
		for(int a = 0; a < arrLen; a++){
			objArray[a] = Array.get(fArr, a);
			Element value = new Element("value");
				
			String val = objArray[a].toString();

			value.setText(val);

			fObj.addContent(value);
			}
		return doc;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Document serializeReferenceObject(Object obj, Document doc) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Class cls = obj.getClass();
		Field field = cls.getDeclaredField("fieldOne");
		Element oElt = new Element("object");
		oElt.setAttribute("class", cls.getName());
		String id = Integer.toString(im.size());
		im.put(obj, id);
		oElt.setAttribute("id", id);
		doc.getRootElement().addContent(oElt);

		SimpleObject sObj = (SimpleObject) field.get(obj);
		Class sCls = sObj.getClass();
		Element fElt = new Element("field");
		fElt.setAttribute("name", field.getName());
		fElt.setAttribute("declaringclass", cls.getName());
		id = Integer.toString(im.size());
		im.put(sObj, id);
		Element rElt = new Element("reference");
		rElt.setText(id);
		fElt.addContent(rElt);
		oElt.addContent(fElt);
		
		Element fObj = new Element("object");
		fObj.setAttribute("class", sCls.getName());
		fObj.setAttribute("id", im.get(sObj).toString());
		Field[] fields = sCls.getDeclaredFields();
		for(int i = 0; i < fields.length; i++) {
			Element fdElt = new Element("field");
			fdElt.setAttribute("name", fields[i].getName());
			fdElt.setAttribute("declaringclass", sCls.getName());
			Element vElt = new Element("value");
			
			int value = fields[i].getInt(sObj);
			String valueString = Integer.toString(value);
			
			vElt.setText(valueString);
			fdElt.addContent(vElt);
			fObj.addContent(fdElt);
		}
		doc.getRootElement().addContent(fObj);

		return doc;
	}
	
}
