package org.cssc.prototpe.net.monitoring;

public class MonitoringService {

	private long clientReceivedTransferredBytes;
	private long serverReceivedTransferredBytes;
	private long clientSentTransferredBytes;
	private long serverSentTransferredBytes;
	private int wholeBlocks;
	private int ipBlocks;
	private int uriBlocks;
	private int mediaTypeBlocks;
	private int sizeBlocks;
	private int leetTransformations;
	private int image180Transformations;

	public long getTotalTransferredBytes() {
		synchronized(this) {
			return
			clientReceivedTransferredBytes +
			serverReceivedTransferredBytes +
			clientSentTransferredBytes +
			serverSentTransferredBytes;
		}
	}

	public long getClientSentTransferredBytes() {
		synchronized(this) {
			return clientSentTransferredBytes;
		}
	}
	public long getServerSentTransferredBytes() {
		synchronized(this) {
			return serverSentTransferredBytes;
		}
	}
	public long getClientReceivedTransferredBytes() {
		synchronized(this) {
			return clientReceivedTransferredBytes;
		}
	}
	public long getServerReceivedTransferredBytes() {
		synchronized(this) {
			return serverReceivedTransferredBytes;
		}
	}
	public int getWholeBlocks() {
		synchronized(this) {
			return wholeBlocks;
		}
	}
	public int getIpBlocks() {
		synchronized(this) {
			return ipBlocks;
		}
	}
	public int getUriBlocks() {
		synchronized(this) {
			return uriBlocks;
		}
	}
	public int getMediaTypeBlocks() {
		synchronized(this) {
			return mediaTypeBlocks;
		}
	}
	public int getSizeBlocks() {
		synchronized(this) {
			return sizeBlocks;
		}
	}
	public int getLeetTransformations() {
		synchronized(this) {
			return leetTransformations;
		}
	}
	public int getImage180Transformations() {
		synchronized(this) {
			return image180Transformations;
		}
	}

	public void addClientSentTransferredBytes(long bytes) {
		synchronized(this) {
			clientSentTransferredBytes += bytes;
		}
	}

	public void addServerSentTransferredBytes(long bytes) {
		synchronized(this) {
			serverSentTransferredBytes += bytes;
		}
	}

	public void addClientReceivedTransferredBytes(long bytes) {
		synchronized(this) {
			clientReceivedTransferredBytes += bytes;
		}
	}

	public void addServerReceivedTransferredBytes(long bytes) {
		synchronized(this) {
			serverReceivedTransferredBytes += bytes;
		}
	}

	public void registerWholeBlock() {
		synchronized(this) {
			wholeBlocks++;
		}
	}

	public void registerIpBlock() {
		synchronized(this) {
			ipBlocks++;
		}
	}

	public void registerUriBlock() {
		synchronized(this) {
			uriBlocks++;
		}
	}

	public void registerMediaTypeBlock() {
		synchronized(this) {
			mediaTypeBlocks++;
		}
	}

	public void registerSizeBlock() {
		synchronized(this) {
			sizeBlocks++;
		}
	}

	public void registerLeetTransformation() {
		synchronized(this) {
			leetTransformations++;
		}
	}

	public void registerImage180Transformation() {
		synchronized(this) {
			image180Transformations++;
		}
	}

}
