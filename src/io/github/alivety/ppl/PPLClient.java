package io.github.alivety.ppl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import io.github.alivety.ppl.packet.Packet;

public class PPLClient extends PPL {
	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	private SocketChannel ch;
	private Thread clientt;
	private PPLAdapter adapter;
	
	public PPLClient connect(final String host, final int port) throws InterruptedException {
		this.clientt = new Thread("ppl-client-" + host + ":" + port) {
			@Override
			public void run() {
				try {
					final Selector selector = Selector.open();
					final SocketChannel ch = SocketChannel.open();
					ch.configureBlocking(false);
					ch.connect(new InetSocketAddress(host, port));
					ch.register(selector, SelectionKey.OP_CONNECT);
					PPLClient.this.ch = ch;
					PPLClient.this.adapter=new PPLAdapter(ch);
					while (true) {
						selector.select();
						final Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
						while (keys.hasNext()) {
							final SelectionKey key = keys.next();
							keys.remove();
							
							if (!key.isValid())
								continue;
							if (key.isConnectable()) {
								((SocketChannel) key.channel()).finishConnect();
								key.interestOps(SelectionKey.OP_READ);
								final Iterator<SocketListener> iter = PPLClient.this.listeners.iterator();
								while (iter.hasNext())
									iter.next().connect((SocketChannel) key.channel());
							}
							if (key.isReadable())
								PPLClient.this.read(key);
						}
					}
				} catch (final ConnectException e) {
					final Iterator<SocketListener> iter = PPLClient.this.listeners.iterator();
					while (iter.hasNext())
						iter.next().exception(PPLClient.this.ch, new RuntimeException("Unable to connect", e));
				} catch (final Exception e) {
					final Iterator<SocketListener> iter = PPLClient.this.listeners.iterator();
					while (iter.hasNext())
						iter.next().exception(PPLClient.this.ch, e);
				}
			}
		};
		this.clientt.start();
		return this;
	}
	
	private void read(final SelectionKey key) {
		final SocketChannel ch = (SocketChannel) key.channel();
		try {
			System.out.println(buffer);
			if (this.buffer.toByteArray().length == 0) {
				final ByteBuffer buf = ByteBuffer.allocate(4);
				buf.order(ByteOrder.BIG_ENDIAN);
				final int numRead = ch.read(buf);
				final byte[] data = buf.array();
				this.buffer.write(data);
				if (numRead < 4)
					return;
				else {
					this.read(key);
					return;
				}
			}
			final int len = PPL.decodeInt(PPL.byteStreamToBuffer(this.buffer));
			final ByteBuffer databuf = ByteBuffer.allocate(len);
			final int existing = databuf.array().length;
			final int numRead = ch.read(databuf) + existing;
			if (numRead < len) {
				final byte[] data = databuf.array();
				this.buffer.write(data);
				return;
			}
			final Iterator<SocketListener> iter = this.listeners.iterator();
			while (iter.hasNext())
				iter.next().read(ch, databuf);
			this.buffer.reset();
		} catch (final Exception e) {
			final Iterator<SocketListener> iter = this.listeners.iterator();
			while (iter.hasNext())
				iter.next().exception(ch, e);
		}
	}
	
	@Override
	public PPLClient addListener(final SocketListener l) {
		return (PPLClient) super.addListener(l);
	}
	
	public void writePacket(final Packet c) throws IOException {
		this.ch.write(PPL.encode(c));
	}
}
