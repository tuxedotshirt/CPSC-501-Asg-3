import java.lang.reflect.*;
import org.jdom2.Document;
import org.jdom2.Element;
import java.util.*;


public class Deserializer {
	static Object obj = null;
    public static Object deserialize(Document document) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        HashMap<String, ?> objMap =  new HashMap<String, Object>();
        
        Element root = document.getRootElement();
        List<?> list = root.getChildren();

        buildObjects(list, objMap);

        setValues(list, objMap);

        //root element set to length, or 0
        return objMap.get("0");
    }

    private static void buildObjects(List objList, HashMap objMap) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        
    	//for each obj
    	for(int i =0; i < objList.size(); i++){
                Element oElt = (Element) objList.get(i);

                Class oClass =  Class.forName(oElt.getAttributeValue("class"));

                Object obj;
                if(oClass.isArray()){
                    int arrayLength = Integer.parseInt(oElt.getAttributeValue("length"));
                    Class arrayType = oClass.getComponentType();

                    obj = Array.newInstance(arrayType, arrayLength);
                }
                else{
                    Constructor constructor =  oClass.getConstructor(null);

                    obj = constructor.newInstance(null);
                }
                String objId = oElt.getAttributeValue("id");
                objMap.put(objId, obj);
            }
        }
    
    
    private static Object deserializeElement(Class classType, Element contentElement, HashMap objMap){
        Object contentObject = null;

         if(contentElement.getName().equals("reference")) {
        	contentObject = objMap.get(contentElement.getText());
        }
         else if(contentElement.getName().equals("value"))
            contentObject = Integer.valueOf(contentElement.getText());
        return contentObject;
    }

    

    private static void setValues(List objList, HashMap objMap){
        for(int i =0; i < objList.size(); i++){
            try{
                Element oElt = (Element) objList.get(i);

                Object oInstance =  objMap.get(oElt.getAttributeValue("id"));

                List childList = oElt.getChildren();

                Class objClass = oInstance.getClass();
                //System.out.println(objClass.getName());
                
                if(!objClass.isArray()) {
                    for(int j = 0; j < childList.size(); j++){
                        Element fieldElement = (Element) childList.get(j);

                        Class declaringClass =  Class.forName(fieldElement.getAttributeValue("declaringclass"));
                        String fieldName = fieldElement.getAttributeValue("name");
                        Field field = declaringClass.getDeclaredField(fieldName);

                        Class fieldType = field.getType();
                        Element fElt = (Element) fieldElement.getChildren().get(0);

                        Object fieldContent = deserializeElement(fieldType, fElt, objMap);

                        field.set(oInstance, fieldContent);
                    }
                }    
                else if(objClass.isArray()){
                    Class arrayType =  objClass.getComponentType();
                    for(int j= 0; j < childList.size(); j++){
                        Element arrayContentElement = (Element) childList.get(j);
                        Object arrayContent = deserializeElement(arrayType, arrayContentElement, objMap);
                        Array.set(oInstance, j, arrayContent);
                    }
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
    }
}
