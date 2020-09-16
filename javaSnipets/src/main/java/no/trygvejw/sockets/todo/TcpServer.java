package no.trygvejw.sockets.todo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpServer {

	private static Logger LOGGER = Logger.getLogger(TcpServer.class.getName());

	private final int MAX_THREADS = 10;

	private int port = 9876;

	private String host = "localhost";

	private ServerSocket welcomeSocket;

	private Map<String, Client> connectedClients = new HashMap<>();

	public TcpServer() {
	}

	public TcpServer(int port) {
		this.port = port;
	}

	public TcpServer(int port, String host) {
		this(port);
		this.host = host;
	}

	/**
	 * Initializes the server socket, and starts accepting clients.
	 */
	public void start() {
		ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
		try {
			this.welcomeSocket = new ServerSocket(port);
			LOGGER.log(Level.INFO, String.format("Server started on port: %s", port));
			while (true) {
				try {
					Socket clientSocket = this.welcomeSocket.accept();
					FractalClient client = new FractalClient(clientSocket, this);
					this.connectedClients.put(client.getClientID(), client);
					pool.execute(client);
					LOGGER.log(Level.INFO, String.format("A new client connected: %s", client.getClientID()));
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
