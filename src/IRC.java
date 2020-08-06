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
	
	public void sendMsg(String msg) {
		this.pw.print(msg+"\r\n");
		this.pw.flush();
	}
	
	public void run() {
		Scanner scanner = new Scanner(System.in);
		while(true) {
			String s = scanner.nextLine();
			sendMsg(s);
		}
	}
}

public class IRC {
	
	public static void initConnect(byte[] buff, InputStream in, PrintWriter pw, String nick, String username, String name) {
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
						pw.print("NICK " + nick + "\r\n");
						pw.flush();
						pw.print("USER " + username + " * * :" + name +"\r\n");
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
		if (args.length < 5) {
			System.out.println("Missing args");
			System.exit(0);;
		}
		
		try {
			String server = args[0];
			int port = Integer.parseInt(args[1]);
			String nick = args[2];
			String username = args[3];
			String name = args[4];
			
			Socket s = new Socket(server, port);
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			InputStream in = s.getInputStream();
			byte[] buff = new byte[1024];
			System.out.println("Starting");
			
			

			initConnect(buff, in, pw, nick, username, name);
			InputThread it = new InputThread(new Message(""), in);
			OutputThread ot = new OutputThread(new Message(""), pw);
			it.start();
			ot.start();
			while (true) {
				if (it.msg.msg.contains("PING")) {
					ot.sendMsg("PONG");
				}
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
