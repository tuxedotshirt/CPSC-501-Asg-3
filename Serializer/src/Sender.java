import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Sender {
	public static String server = "localhost";
	public static int port = Integer.parseInt("3333"); //1111 and 2222 don't work
	public static Socket s;
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, IOException {
		createObject(getMenuChoice());
	}
	

	
	private static void serialize(Object obj) throws IllegalArgumentException, IllegalAccessException, IOException {
		System.out.println("Serializing object");

		Serializer.serialize(obj);

		System.out.println("Sending object");
		File file = Serializer.file;
		
		transferFile(server, port, file);
	}
	
	//can't make file send over any connection, connection reset exception
	private static void transferFile(String server, int port, File aFile) {
		System.out.println("Transferring file");
		try {
			Socket s = new Socket(server,port);
			//createConnection(port, server);
			OutputStream output = s.getOutputStream();
			FileInputStream fileInputStream = new FileInputStream(aFile);
			byte[] buffer = new byte[1024*1024];
			int bytesRead = 0;
			while ((bytesRead = fileInputStream.read(buffer)) > 0) {
				output.write(buffer, 0, bytesRead);
			}
			fileInputStream.close();
			s.close();
			System.out.println("Transfer Complete");
		} catch (IOException e) {
			System.out.println("Can't connect");
		}
	}
	
	public static Socket createConnection(int port, String server) {
		Socket s = null;
		try {
			s = new Socket(server, port);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host exception");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Unable to connect");
			e.printStackTrace();
		}
		return s;
	}

	public static void createObject(int objectSelection) throws IllegalArgumentException, IllegalAccessException, IOException {
		switch(objectSelection) {
		//Simple object with only primitives for instance variables
		case 1: 
			serialize(createSimpleObject());
			//createSimpleObject();
			break;
		//An object that contains a reference to another object
		case 2: 
			serialize(createReferenceObject());
			//createReferenceObject();
			break;
		//An object that contains an array of primitives
		case 3: 
			serialize(createSimpleArray());
			//createSimpleArray();
			break;
		//An object that contains an array of object references
		case 4: 
			serialize(createReferenceArray());
			//createReferenceArray();
			break;
		//An object that uses an instance of one of Java's collection classes
		case 5: 
			serialize(createCollectionClassObject());
			//createCollectionClassObject();
			break;
		//Quit
		case 6: break;
		default: break;
		}
	}
	
	@SuppressWarnings("resource")
	public static CollectionClassObject createCollectionClassObject() {
		ArrayList<SimpleObject> list = new ArrayList<SimpleObject>();
		char quit = 'Y';
		Scanner sc = new Scanner(System.in);
		
		while (quit == 'Y') {
			SimpleObject obj = createSimpleObject();
			list.add(obj);
			
			System.out.println("Add another SimpleObject to collection? (Y/N)");
			
			String word = sc.next();
			word = word.toUpperCase();
			quit = word.charAt(0);
		}
		CollectionClassObject collection = new CollectionClassObject(list);
		
		return collection;
	}

	@SuppressWarnings("resource")
	public static ReferenceArray createReferenceArray(){
		char quit = 'Y';
		ArrayList<SimpleObject> arrayList = new ArrayList<SimpleObject>();

		Scanner sc = new Scanner(System.in);
		System.out.println("Create SimpleObjects and add to array.");
		System.out.println();

		while(quit == 'Y') {
			arrayList.add(createSimpleObject());
		
			System.out.println();
			System.out.print("Create another SimpleObject? (Y/N)");
			String word = sc.next();
	        word = word.toUpperCase();
	        quit= word.charAt(0);
		}

		SimpleObject[] simpleArray = new SimpleObject[arrayList.size()];
		
		int index = 0;
		for(SimpleObject obj : arrayList){
			simpleArray[index++] = obj;
		}
		
		ReferenceArray arr = new ReferenceArray(simpleArray);
		return arr;
	}

	public static PrimitiveArray createSimpleArray() {
		
		String input;
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter an array of integers separated by commas: int,int,int...");
		input = sc.nextLine();
		String[] arrayIndices = input.split(",");
		int[] arr = new int[arrayIndices.length];

		int i = 0;
		for(String index : arrayIndices) {
			try {
				arr[i] = Integer.parseInt(index);
				i++;
			} catch (Exception e) {
				System.out.println("Invalid entry: " + index);
			}
		}
		PrimitiveArray arrClass = new PrimitiveArray(arr);

		return arrClass;
	}
	
 	public static ReferenceObject createReferenceObject() {
		
		SimpleObject simpleObject = createSimpleObject();
		ReferenceObject referenceObj = new ReferenceObject(simpleObject);
		
		return referenceObj;
	}
	
	@SuppressWarnings("resource")
	public static SimpleObject createSimpleObject() {
		
		SimpleObject obj = null;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter parameters: int,int");
		
		String input = sc.nextLine();
		String[] fields = input.split(",");
		int fieldOne = Integer.parseInt(fields[0]);
		int fieldTwo = Integer.parseInt(fields[1]);
		obj = new SimpleObject(fieldOne, fieldTwo);

		return obj;
	}
	
	@SuppressWarnings("resource")
	public static int getMenuChoice() {
		Scanner in = new Scanner(System.in);
		int selection;
		displayMenu();
		do {
		System.out.println("Enter a menu number: ");
		while (!in.hasNextInt()) {
	        in.next();
		}
		selection = in.nextInt();
		}while(selection < 1 || selection > 6);
	
		return selection;
	}
	
	public static void displayMenu() {
		System.out.println("Create object to serialize.");
		System.out.println("Select from the following options:");
		
		System.out.println("1. Create simple object");
		
		System.out.println("2. Create reference object");
		
		System.out.println("3. Create array of primitives");
		
		System.out.println("4. Create array of object references");
		
		System.out.println("5. Create Java collection class");
		System.out.println("6. Quit");
	}
}
