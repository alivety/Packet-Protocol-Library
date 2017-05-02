package io.github.alivety.ppl.examples;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import io.github.alivety.ppl.AbstractPacket;
import io.github.alivety.ppl.PPL;
import io.github.alivety.ppl.PPLClient;
import io.github.alivety.ppl.PPLServer;
import io.github.alivety.ppl.SocketListener;

/**
 * In this example, Packet0 will be the "Call Packet" and Packet1 will be the "Response Packet"
 * @author sn
 *
 */
public class CallAndResponse {
	public static void main(String[]args) throws Exception {
		PPLServer server=new PPLServer();
		server.addListener(new SocketListener(){
			@Override
			public void connect(SocketChannel ch) throws Exception {}

			@Override
			public void read(SocketChannel ch, ByteBuffer msg) throws Exception {
				System.out.println("PPLServer.read()");
				AbstractPacket call=AbstractPacket.decode("io.github.dyslabs.ppl.example.Packet", msg);
				System.out.println(call.<String>getField("call"));
				AbstractPacket response=AbstractPacket.c(Packet1.class, "Hello to you !!!");
				ch.write(PPL.encapsulate(response.encode()));
			}

			@Override
			public void exception(SocketChannel h, Throwable t) {
				throw new RuntimeException(t);
			}}).bind(3000);
		
		PPLClient client=new PPLClient();
		client.addListener(new SocketListener(){
			@Override
			public void connect(SocketChannel ch) throws Exception {
				AbstractPacket call=AbstractPacket.c(Packet0.class, "Hi !!!");
				ch.write(PPL.encapsulate(call.encode()));
			}

			@Override
			public void read(SocketChannel ch, ByteBuffer msg) throws Exception {
				System.out.println("PPLClient.read()");
				AbstractPacket response=AbstractPacket.decode("io.github.dyslabs.ppl.example.Packet", msg);
				System.out.println(response.toString());
				server.shutdown();
				client.shutdown();
			}

			@Override
			public void exception(SocketChannel h, Throwable t) {
				throw new RuntimeException(t);
			}}).connect("localhost", 3000); // (2)
	}
}
