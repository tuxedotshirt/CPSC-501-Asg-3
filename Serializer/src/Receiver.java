import java.io.IOException;
import java.net.ServerSocket;

public class Receiver {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		int port = 3333;
		
		serverSocket = createConnection(port, serverSocket);
		
		//Keep server running
		while(true) {
			
		}
	}
	
	public static ServerSocket createConnection(int port, ServerSocket socket) {
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socket;
	}

}
