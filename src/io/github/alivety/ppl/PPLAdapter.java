package io.github.alivety.ppl;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import io.github.alivety.ppl.packet.Packet;

public class PPLAdapter {
	private final SocketChannel ch;
	
	protected PPLAdapter(final SocketChannel ch) {
		this.ch = ch;
	}
	
	public void writePacket(final Packet c) throws IOException {
		this.ch.write(PPL.encode(c));
	}
	
	@Override
	public String toString() {
		return this.ch.toString();
	}
}
