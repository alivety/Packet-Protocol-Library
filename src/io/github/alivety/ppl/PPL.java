package io.github.alivety.ppl;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public abstract class PPL {
	public static ByteBuffer byteStreamToBuffer(final ByteArrayOutputStream o) {
		return ByteBuffer.wrap(o.toByteArray()).order(ByteOrder.BIG_ENDIAN);
	}

	public static int decodeInt(final ByteBuffer b) {
		return b.order(ByteOrder.BIG_ENDIAN).getInt();
	}

	/**
	 * Prepends the length of the ByteBuffer onto the ByteBuffer
	 *
	 * @param data
	 * @return
	 */
	public static ByteBuffer encapsulate(final ByteBuffer data) {
		final ByteBuffer len = PPL.encodeInt(data.array().length);
		final ByteBuffer capsule = ByteBuffer.allocate(len.array().length + data.array().length);
		capsule.put((ByteBuffer) len.position(0)).put((ByteBuffer) data.position(0));
		return ((ByteBuffer) capsule.position(0)).order(ByteOrder.BIG_ENDIAN);
	}

	public static ByteBuffer encodeInt(final int i) {
		return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(i);
	}

	protected ArrayList<SocketListener> listeners = new ArrayList<>();

	public PPL addListener(final SocketListener l) {
		this.listeners.add(l);
		return this;
	}
	
	public int listeners() {
		return listeners.size();
	}
}
