package org.cssc.prototpe.net;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.exceptions.MissingHostException;
import org.cssc.prototpe.net.exceptions.FatalException;

public class Logger {

	private Writer output;

	public Logger(String filename) {
		try {
			FileWriter f = new FileWriter(filename);
			output = f;
		} catch (IOException e) {
			System.err.println("Unable to initialize logger: cause is " + e.getMessage());
			System.err.println("Logging into default System.out instead");
			output = new OutputStreamWriter(System.out);
		}
	}

	public void logRequest(InetAddress addr, HttpRequest request) {
		synchronized(output) {
			try {
				try {
					output.write(addr.toString() + " - " + request.getMethod() + " to " + request.getEffectiveHost() + ", path: " + request.getPath() + "\n");
					output.flush();
				} catch (MissingHostException e) {
					output.write(addr.toString() + " attempted an invalid " + request.getMethod() + ": cannot resolve hostname.\n");
					output.flush();
				}
			} catch (IOException e) {
				throw new FatalException("Unable to log.", e);
			}
		}
	}

	public void logFilterResponse(InetAddress addr, HttpResponse response) {
		try {
			output.write("CSSC Proxy - Replied " + response.getStatusCode().getCode() + " " + response.getReasonPhrase() + " to " + addr.toString() + ". Filtered.\n");
			output.flush();
		} catch (IOException e) {
			throw new FatalException("Unable to log.", e);
		}
	}

	public void logResponse(InetAddress addr, HttpResponse response, HttpRequest associatedRequest) {
		synchronized(output) {
			try {
				try {
					output.write(associatedRequest.getEffectiveHost() + " - " + "Replied " + response.getStatusCode().getCode() + " " + response.getReasonPhrase() + " to " + addr.toString() + ", requested path: " + associatedRequest.getPath() + "\n");
				} catch (MissingHostException e) {
					output.write("CSSC Proxy - Replied " + response.getStatusCode().getCode() + " " + response.getReasonPhrase() + " to " + addr.toString() + ", couldn't resolve host.\n");
				}
				output.flush();
			} catch (IOException e) {
				throw new FatalException("Unable to log.", e);
			}
		}
	}

	public void logErrorResponse(InetAddress addr, HttpResponse response, HttpRequest associatedRequest) {
		synchronized(output) {
			try {
				if(associatedRequest != null) {
					try {
						output.write("CSSC Proxy" + " - " + "Replied " + response.getStatusCode().getCode() + " " + response.getReasonPhrase() + " to " + addr.toString() + ", requested path: " + associatedRequest.getEffectiveHost() + associatedRequest.getEffectivePath() + "\n");
					} catch (MissingHostException e) {
						output.write("CSSC Proxy - Replied " + response.getStatusCode().getCode() + " " + response.getReasonPhrase() + " to " + addr.toString() + ", couldn't resolve host/path.\n");
					}
				} else {
					output.write("CSSC Proxy - Replied " + response.getStatusCode().getCode() + " " + response.getReasonPhrase() + " to " + addr.toString() + "\n");
				}
				output.flush();
			} catch (IOException e) {
				throw new FatalException("Unable to log.", e);
			}
		}
	}

}
