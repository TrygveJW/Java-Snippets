package no.trygvejw.sockets.todo.protocoll;


import no.fractal.socket.Client;
import no.fractal.socket.FractalProtocol;
import no.fractal.socket.TcpServer;
import no.fractal.socket.factorysmabyeidk.request.AbstractRequest;
import no.fractal.socket.factorysmabyeidk.request.FractalRequestMeta;
import no.fractal.socket.factorysmabyeidk.request.SendThumbnailRequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class FractalClient extends Client {

	// Handles logging for the FractalClient
	private static Logger LOGGER = Logger.getLogger(FractalClient.class.getName());

	private no.fractal.socket.FractalProtocol protocol = new FractalProtocol();


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

			FractalRequestMeta requestMeta = no.fractal.socket.factorysmabyeidk.FractalProtocol.parseRequestMeta(in);

			AbstractRequest request = switch (requestMeta.getRequestId()){
				case "THUMBNAIL" -> new SendThumbnailRequest(requestMeta);
				default -> null;
			};

			request.finishIO(in);
			request.doAction(); // this one can be done on another thread

		}

	}

}
