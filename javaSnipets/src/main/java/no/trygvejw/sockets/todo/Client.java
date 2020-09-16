package no.trygvejw.sockets.todo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A client connected to a socket. A socket client has access to the server
 *
 * @author Christoffer A Træen
 * @version 1
 */
public abstract class Client implements Runnable {

	/**
	 * Our own socket
	 */
	private Socket clientSocket;

	/**
	 * The server instance
	 */
	private TcpServer server;

	/**
	 * Input stream from the client
	 */
	private BufferedInputStream streamInput;

	/**
	 * Stream out from this socket
	 */
	private BufferedOutputStream streamOutput;

	/**
	 * Unique id for the client
	 */
	private String clientId;

	/**
	 * Creates the client socket. Creates the input reader.
	 *
	 * @param clientSocket the client socket
	 * @param server       the server
	 * @throws IOException              thrown if there are problems creating the
	 *                                  input stream reader
	 * @throws IllegalArgumentException thrown if socket or server is null
	 */
	public Client(Socket clientSocket, TcpServer server) throws IOException {
		this.setClientSocket(clientSocket);
		this.setServer(server);
		this.createInputReader();
		this.createOutputStream();
	}

	/**
	 * Creates the input stream reader and assigns it to locald field.
	 *
	 * @throws IOException thrown if we cant create the reader
	 */
	private void createInputReader() throws IOException {
		this.streamInput = new BufferedInputStream(clientSocket.getInputStream());
	}

	/**
	 * Creates the output stream for this socket.
	 *
	 * @throws IOException thrown if unable to create stream
	 */
	private void createOutputStream() throws IOException {
		this.streamOutput = new BufferedOutputStream(clientSocket.getOutputStream());
	}

	/**
	 * Returns the input reader which reads the stream from the client.
	 *
	 * @return input stream reader
	 */
	public BufferedInputStream getInputReader() {
		return this.streamInput;
	}

	/**
	 * Returns the output stream for this socket.
	 *
	 * @return output stream
	 */
	public BufferedOutputStream getOutputStream() {
		return this.streamOutput;
	}

	/**
	 * Sets the client socket.
	 *
	 * @param clientSocket the socket of the client
	 * @throws IllegalArgumentException if socket is null
	 */
	private void setClientSocket(Socket clientSocket) {
		if (clientSocket == null)
			throw new IllegalArgumentException();
		this.clientSocket = clientSocket;
	}

	/**
	 * Sets the server instance reference
	 *
	 * @param server the server instance
	 * @throws IllegalArgumentException if socket is null
	 */
	private void setServer(TcpServer server) {
		if (server == null)
			throw new IllegalArgumentException();
		this.server = server;
	}

	protected TcpServer getServer() {
		return this.server;
	}

	public void setClientID(String clientId) {
		this.clientId = clientId;
	}

	public String getClientID() {
		return this.clientId;
	}

	public Socket getClientSocket() {
		return this.clientSocket;
	}

	/**
	 * Method for handling reading of the inputstream from the client
	 */
	protected abstract void read();

}
