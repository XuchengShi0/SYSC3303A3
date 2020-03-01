import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;

	public Client() {
		try {
			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// send and receive UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) { // Can't create the socket.
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

	// method for send
	public void send(byte[] data) {
		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 23);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Client: Sending packet:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		System.out.println("Length: " + sendPacket.getLength());
		System.out.print("Containing: ");
		System.out.println(new String(sendPacket.getData(), 0, sendPacket.getLength()));
		this.printByte(data);

		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Client: Packet sent.");
	}

	// method for receive
	public void receive(byte data[]) {

		System.out.println("Client: Waiting for Packet.");
		try {
			// Block until a Datagram is received via sendReceiveSocket.
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Client: Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		System.out.println("Length: " + receivePacket.getLength());
		System.out.print("Containing: ");

		// Form a String from the byte array.
		String received = new String(data, 0, receivePacket.getLength());
		System.out.println(received);
		this.printByte(data);

	}

	// combine all function
	public void sendAndReceive() {
		String file = "test.txt";
		String mode = "octet";
		String r = "Read request";
		char type;
		byte d[] = new byte[100];
		receivePacket = new DatagramPacket(d, d.length);

		for (int i = 0; i < 11; i++) {
			System.out.println(i + 1 + ":");
			if (i % 2 == 0) {
				r = "Read request";
				type = 'r';
			} else {
				r = "Write request";
				type = 'w';
			}
			if (i == 10) {
				r = "ERROR";
				type = 'e';
			}

			System.out.println("Client: Create packet #" + (i + 1) + " - " + r);

			byte[] data = new byte[4 + file.length() + mode.length()];
			int j = 0;
			data[j] = (byte) 0;
			j++;
			if (type == 'r') {
				data[j] = (byte) 1;
			} else if (type == 'w') {
				data[j] = (byte) 2;
			} else {
				data[j] = (byte) 0;
			}
			j++;
			for (byte b : file.getBytes()) {
				data[j] = b;
				j++;
			}
			data[j] = (byte) 0;
			j++;
			for (byte b : mode.getBytes()) {
				data[j] = b;
				j++;
			}
			data[j] = (byte) 0;

			this.send(data);

			this.receive(d);
		}
		sendReceiveSocket.close();
	}

	public static void main(String[] args) {
		Client c = new Client();
		c.sendAndReceive();

	}

}
