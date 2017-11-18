package io.github.alivety.ppl;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface SocketListener {
	public void connect(SocketChannel ch) throws Exception;
	
	public void read(SocketChannel ch, ByteBuffer msg) throws Exception;
	
	public void exception(SocketChannel h, Throwable t);
}
