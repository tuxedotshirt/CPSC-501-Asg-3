import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;





public class Sender {

	public static void main(String[] args) {
		String server = "localhost";
		int port = Integer.parseInt("3333"); //1111 and 2222 don't work

		Socket s = createConnection(port, server);
		
		createObject(getMenuChoice());
	}

	public static Socket createConnection(int port, String server) {
		Socket s = null;
		try {
			s = new Socket(server, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	public static void createObject(int objectSelection) {
		switch(objectSelection) {
		//Simple object with only primitives for instance variables
		case 1: break;
		//An object that contains a reference to another object
		case 2: break;
		//An object that contains an array of primitives
		case 3: break;
		//An object that contains an array of object references
		case 4: break;
		//An object that uses an instance of one of Java's collection classes
		case 5: break;
		//Quit
		case 6: break;
		default: break;
		}
	}
	
	
	public static int getMenuChoice() {
		Scanner in = new Scanner(System.in);
		String s = null;// = in.nextLine();
		int selection;
		displayMenu();
		do {
		System.out.println("Enter a menu number: ");
		while (!in.hasNextInt()) {
	        in.next();
		}
		selection = in.nextInt();
		}while(selection < 1 || selection > 6);
		//System.out.println("You have selected: " + selection);
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
