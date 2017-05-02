package io.github.alivety.ppl.examples;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import io.github.alivety.ppl.PPL;
import io.github.alivety.ppl.PPLClient;
import io.github.alivety.ppl.PPLServer;
import io.github.alivety.ppl.SocketListener;

/**
 * This is the same as CallAndResponse but without using packets
 * @author sn
 *
 */
public class CallAndResponseBinary {
	public static void main(String[]args) throws Exception {
		new PPLServer().addListener(new SocketListener(){
			@Override
			public void connect(SocketChannel ch) throws Exception {}

			@Override
			public void read(SocketChannel ch, ByteBuffer msg) throws Exception {
				System.out.println("client said: "+new String(msg.array()));
				byte[] data="Hello to you !!!".getBytes();
				ch.write(PPL.encapsulate(ByteBuffer.wrap(data)));
			}

			@Override
			public void exception(SocketChannel h, Throwable t) {
				throw new RuntimeException(t);
			}}).bind(3000);
		new PPLClient().addListener(new SocketListener(){
			@Override
			public void connect(SocketChannel ch) throws Exception {
				byte[] data="Hi !!!".getBytes();
				ch.write(PPL.encapsulate(ByteBuffer.wrap(data)));
			}
				
			@Override
			public void read(SocketChannel ch, ByteBuffer msg) throws Exception {
				System.out.println("server said: "+new String(msg.array()));
			}

			@Override
			public void exception(SocketChannel h, Throwable t) {
				throw new RuntimeException(t);
		}}).connect("localhost", 3000);
	}
}
