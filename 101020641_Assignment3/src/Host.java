import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Host {
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private DatagramSocket receiveSocket, sendSocket, sendReceiveSocket;
	private int clientPort, clientLen;
	private InetAddress clientAddress;

	public Host() {
		try {
			sendReceiveSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(23);
			sendSocket = new DatagramSocket();
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

	// method for receive from client
	private void receiveFromClient(byte[] data) {
		// Block until a Datagram packet is received on the receiveSocket.
		try {
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		// get client information
		clientAddress = receivePacket.getAddress();
		clientPort = receivePacket.getPort();
		clientLen = receivePacket.getLength();

		// Process the received datagram.
		System.out.println("Host: Client Packet received:");
		System.out.println("From host: " + clientAddress);
		System.out.println("Host port: " + clientPort);
		System.out.println("Length: " + clientLen);
		System.out.print("Containing: ");

		// Form a String from the byte array.
		System.out.println(new String(data, 0, clientLen));
		this.printByte(data);
	}

	// method for send to client
	private void sendToClient(byte[] data) {

		sendPacket = new DatagramPacket(data, receivePacket.getLength(), clientAddress, clientPort);
		System.out.println("Host: Sending packet to CLIENT:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		System.out.println("Length: " + sendPacket.getLength());
		System.out.print("Containing: ");
		System.out.println(new String(sendPacket.getData(), 0, sendPacket.getLength()));
		this.printByte(sendPacket.getData());

		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Host: packet sent to CLIENT.\n");
	}

	// method for receive from server
	public void receiveFromServer(byte[] data) {
		// Block until a Datagram packet is received on the sendReceiveSocket.
		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Host: Server Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		System.out.println("Length: " + receivePacket.getLength());
		System.out.print("Containing: ");

		// Form a String from the byte array.
		String received = new String(data, 0, receivePacket.getLength());
		System.out.println(received);
		this.printByte(data);
	}

	// method for send from server
	private void sendToServer(byte[] data) {
		sendPacket = new DatagramPacket(data, receivePacket.getLength(), receivePacket.getAddress(), 69);
		System.out.println("Host: Forwarding packet to SERVER:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		System.out.println("Length: " + sendPacket.getLength());
		System.out.print("Containing: ");
		System.out.println(new String(sendPacket.getData(), 0, sendPacket.getLength()));
		this.printByte(sendPacket.getData());

		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Host: packet sent to SERVER.");
	}

	// combine all function
	public void sendAndReceive() {
		byte data[] = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		int i = 1;
		while (true) {
			System.out.println(i + ":");
			i++;
			this.receiveFromClient(data);
			this.sendToServer(data);
			this.receiveFromServer(data);
			this.sendToClient(data);
		}
	}

	public static void main(String[] args) {
		Host h = new Host();
		h.sendAndReceive();

	}

}
