package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
	public void start(int port) throws SQLException{
		ServerSocket server;
		Socket socket;
		ServerThread thread;
		try{
			server = new ServerSocket(port);
			System.out.println("Start Server" + port);
			while(true){
				try{
					socket = server.accept();
					thread = new ServerThread(socket);
					thread.start();
					
				}catch(IOException ioe){
					
				}
			}
		}catch(IOException ioe){
			System.err.println(ioe.toString());
		}
	}
	public static void main(String[] ar) throws SQLException{
		Server server = new Server();
		server.start(3000);
	}
}
