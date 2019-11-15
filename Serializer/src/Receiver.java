import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;


public class Receiver {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		int port = 3333;
		ServerSocket serverSocket = null;
		Inspector inspector = new Inspector();
		
		try {
			serverSocket = new ServerSocket(port);
			
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
			System.exit(1);
		}
		while (true) {
			System.out.println("Waiting for sender");
			File aFile = new File("C:\\Users\\Don\\Documents\\recdata.xml");
			
			Socket s = null;
			try {
				s = serverSocket.accept();
				System.out.println("Client connected");
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
			receiveFile(aFile, s);
			SAXBuilder saxBuilder = new SAXBuilder();
            Document document = (Document) saxBuilder.build(aFile);
			Object obj = Deserializer.deserialize(document);
			//serverSocket.close();
			
			System.out.println("********************************************************************");
            inspector.inspect(obj, true);
            System.out.println("********************************************************************");
		}
	}

	private static void receiveFile(File file, Socket s) throws IOException,
			FileNotFoundException {
		InputStream input = s.getInputStream();
		@SuppressWarnings("resource")
		FileOutputStream out = new FileOutputStream(file);

		byte[] buffer = new byte[1024*1024];

		int bytesReceived = 0;
		System.out.println("Receiving file");
		while ((bytesReceived = input.read(buffer)) > 0) {
			out.write(buffer, 0, bytesReceived);
			break;
		}
	}
}