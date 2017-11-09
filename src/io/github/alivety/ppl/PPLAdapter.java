package io.github.alivety.ppl;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import io.github.alivety.ppl.packet.Packet;

public class PPLAdapter {
	private SocketChannel ch;

	protected PPLAdapter(SocketChannel ch) {
		this.ch = ch;
	}

	public void writePacket(Packet c) throws IOException {
		ch.write(PPL.encode(c));
	}

	@Override
	public String toString() {
		return ch.toString();
	}
}
