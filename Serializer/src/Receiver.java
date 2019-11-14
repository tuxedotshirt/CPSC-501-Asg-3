import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Receiver {
	public static void main(String[] args) throws IOException {
		int port = 3333;
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(port);
			
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
			System.exit(1);
		}
		while (true) {
			System.out.println("Waiting for sender");
			File aFile = new File("recdata.xml");
			
			Socket s = null;
			try {
				s = serverSocket.accept();
				System.out.println("Client connected");
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
			receiveFile(aFile, s);
			//serverSocket.close();
		}
	}

	private static void receiveFile(File file, Socket s) throws IOException,
			FileNotFoundException {
		InputStream input = s.getInputStream();
		FileOutputStream out = new FileOutputStream(file);
		OutputStream output = System.out;
		byte[] buffer = new byte[1024*1024];

		int bytesReceived = 0;
		System.out.println("Receiving file");
		while ((bytesReceived = input.read(buffer)) > 0) {
			out.write(buffer, 0, bytesReceived);
			output.write(bytesReceived);
			break;
		}
	}
}