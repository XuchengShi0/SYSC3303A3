import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	byte[] b = {(byte)0,(byte)3,(byte)0,(byte)1};
	byte[] a = {(byte)0,(byte)4,(byte)0,(byte)0};

	public Server() {
		try {
			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// send UDP Datagram packets.
			sendSocket = new DatagramSocket();

			// Construct a datagram socket and bind it to port 69
			// on the local host machine. This socket will be used to
			// receive UDP Datagram packets.
			receiveSocket = new DatagramSocket(69);
			
			

		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	// convert msg in byte
	private void printByte(byte data[]) {
		int i = 0;
		for (byte b : data) {
			if (Integer.toHexString(b).length() == 1) {
				System.out.print("0");
			}
			System.out.print(Integer.toHexString(b) + " ");
			if (i % 4 == 0) {
				System.out.print(" ");
			}
			i++;
		}
		System.out.println("\n");
	}

	// method for receive
	public void receive(byte data[]) {

		System.out.println("Server: Waiting for Packet.");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Server: Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");

		// Form a String from the byte array.
		String received = new String(data, 0, len);
		System.out.println(received);
		this.printByte(data);

	}

	// method for send
	public void send(byte data[]) {

		sendPacket = new DatagramPacket(data, data.length, receivePacket.getAddress(), receivePacket.getPort());

		System.out.println("Server: Sending packet:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		int len = sendPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");
		System.out.println(new String(sendPacket.getData(), 0, len));
		this.printByte(data);

		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Server: packet sent.\n");

	}

	// method for parse
	private void parse(byte data[]) {
		System.out.println("Server: Parsing");
		if (data[0] != (byte) 0) {
			System.out.println("ERROR in first byte.");
			System.exit(1);
		}

		if ((data[1] == (byte) 1) || (data[1] == (byte) 2)) {
		} else {
			System.out.println("ERROR in second byte.");
			System.exit(1);
		}

		if (data[receivePacket.getLength() - 1] != (byte) 0) {
			System.out.println("ERROR in last byte.");
			System.exit(1);
		}

		int i = 0;
		for (int j = 2; j < receivePacket.getLength() - 1; j++) {
			if (data[j] == (byte) 0) {
				i++;
			}
		}
		if (i != 1) {
			System.out.println("Wrong number of 0 byte between string");
			System.exit(1);
		}

		System.out.println("Server: Parsing successful.");
	}

	// combine all function
	public void sendAndReceive() {
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		int i = 1;
		while (true) {
			System.out.println(i + ":");
			i++;
			this.receive(data);
			this.parse(data);
			System.out.println("Server: Create response packet.");
			if (data[1] == (byte) 1) {
				this.send(b);
			}
			if (data[1] == (byte) 2) {
				this.send(a);
			}
		}
	}

	public static void main(String args[]) {
		Server s = new Server();
		s.sendAndReceive();
	}
}
