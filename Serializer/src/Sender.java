import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender {

	public static void main(String[] args) {
		String server = "localhost";
		int port = Integer.parseInt("3333");

		Socket s = createConnection(port, server);
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
}
