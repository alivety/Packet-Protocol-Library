package io.github.alivety.ppl;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import io.github.alivety.ppl.packet.Packet;

public abstract class SocketAdapter implements SocketListener {
	
	@Override
	public final void connect(final SocketChannel ch) throws Exception {
		this.connect(new PPLAdapter(ch));
	}
	
	@Override
	public final void read(final SocketChannel ch, final ByteBuffer msg) throws Exception {
		this.read(new PPLAdapter(ch), PPL.decode(msg));
	}
	
	@Override
	public final void exception(final SocketChannel ch, final Throwable t) {
		this.exception(new PPLAdapter(ch), t);
	}
	
	public abstract void connect(PPLAdapter adapter) throws Exception;
	
	public abstract void read(PPLAdapter adapter, Packet packet) throws Exception;
	
	public abstract void exception(PPLAdapter adapter, Throwable t);
}
