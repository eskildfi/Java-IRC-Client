import java.net.*;
import java.io.*;
import java.util.Scanner;

class UserConnection {
	String server;
	int port;
	String channel;
	String nick;
	
}

class Message {
	String msg;
	
	public Message(String input) {
		this.msg = input;
	}
}

class InputThread extends Thread {
	Message msg;
	InputStream in;
	
	public InputThread(Message msg, InputStream in) {
		this.msg = msg;
		this.in = in;
	}
	
	public void run() {
		byte[] buff = new byte[1024];
		int read;
		while (true) {
			try {
				read = this.in.read(buff);
				if (read > 0) {
					this.msg = new Message(new String(buff, 0, read));
					System.out.print(this.msg.msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class OutputThread extends Thread {
	Message msg;
	PrintWriter pw;
	
	public OutputThread(Message msg, PrintWriter pw) {
		this.msg = msg;
		this.pw = pw;
	}
	
	public void run() {
		Scanner scanner = new Scanner(System.in);
		while(true) {
			String s = scanner.nextLine();
			pw.print(s+"\r\n");
			pw.flush();
		}
	}
}

public class IRC {
	
	public static void initConnect(byte[] buff, InputStream in, PrintWriter pw) {
		boolean joined = false;
		int read;
		while (!joined) {
			try {
				read = in.read(buff);
				if (read > 0) {
					String output = new String(buff, 0, read);
					System.out.print(output);
					System.out.flush();
					
					if (output.contains("No Ident response")) {
						pw.print("NICK javatestaasbsas\r\n");
						pw.flush();
						pw.print("USER javatest eskildfi Server eskildfi\r\n");
						pw.flush();
					}
					// got motd, we've joined the server
					else if (output.contains("376")) {
						joined = true;
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void main(String[] args) {
		try {
			Socket s = new Socket("chat.freenode.net", 6667);
			//Socket s = new Socket("www.google.com", 80);
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			String nick = "NICK javatest";
			String user = "USER javatest eskildfi Server eskildfi";
			/*
			pw.println("GET / HTTP/1.0");
			pw.println();
			pw.flush();
			*/
			InputStream in = s.getInputStream();
			Scanner scanner = new Scanner(System.in);
			String userInput = "";
			byte[] buff = new byte[1024];
			int read;
			System.out.println("Starting");
			
			

			initConnect(buff, in, pw);
			InputThread it = new InputThread(new Message(""), in);
			OutputThread ot = new OutputThread(new Message(""), pw);
			it.start();
			ot.start();
			while (true);
				
			
			
			/*
			while (true) {
				System.out.println("But does it block");
				read = in.read(buff);
				if (read > 0) {
					String output = new String(buff, 0, read);
					System.out.print(output);
					System.out.flush();
				}
			}
			*/
			//s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
