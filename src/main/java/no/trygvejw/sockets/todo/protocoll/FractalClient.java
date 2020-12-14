package no.trygvejw.sockets.todo.protocoll;




import no.trygvejw.sockets.todo.Client;
import no.trygvejw.sockets.todo.TcpServer;
import no.trygvejw.sockets.todo.protocoll.request.AbstractRequest;
import no.trygvejw.sockets.todo.protocoll.request.FractalRequestMeta;
import no.trygvejw.sockets.todo.protocoll.request.SendThumbnailRequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class FractalClient extends Client {

	// Handles logging for the FractalClient
	private static Logger LOGGER = Logger.getLogger(FractalClient.class.getName());

	private FractalProtocol protocol = new FractalProtocol();


	public FractalClient(Socket clientSocket, TcpServer server) throws IOException {
		super(clientSocket, server);

	}

	@Override
	public void run() {
		// initial setup
		this.read();
	}


	/**
	 * Reads all incoming headers and routes the payloads to the approporiate
	 * payload handlers.
	 */
	@Override
	protected void read() {
		BufferedInputStream in = this.getInputReader();
		BufferedOutputStream out = this.getOutputStream();

		while (true) {

			FractalRequestMeta requestMeta = FractalProtocol.parseRequestMeta(in);

			AbstractRequest request = switch (requestMeta.getRequestId()){
				case "THUMBNAIL" -> new SendThumbnailRequest(requestMeta);
				default -> null;
			};

			request.finishIO(in);
			request.doAction(); // this one can be done on another thread

		}

	}

}
