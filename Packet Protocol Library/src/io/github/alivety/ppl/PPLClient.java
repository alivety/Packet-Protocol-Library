package io.github.alivety.ppl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class PPLClient extends PPL {
	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	private SocketChannel ch;
	private Thread clientt;

	public PPLClient connect(final String host, final int port) {
		clientt=new Thread("ppl-client-" + host + ":" + port) {
			@Override
			public void run() {
				try {
					final Selector selector = Selector.open();
					final SocketChannel ch = SocketChannel.open();
					ch.configureBlocking(false);
					ch.connect(new InetSocketAddress(host, port));
					ch.register(selector, SelectionKey.OP_CONNECT);
					PPLClient.this.ch=ch;

					while (true) {
						selector.select();
						final Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
						while (keys.hasNext()) {
							final SelectionKey key = keys.next();
							keys.remove();

							if (!key.isValid()) {
								continue;
							}
							if (key.isConnectable()) {
								((SocketChannel) key.channel()).finishConnect();
								key.interestOps(SelectionKey.OP_READ);
								final Iterator<SocketListener> iter = PPLClient.this.listeners.iterator();
								while (iter.hasNext()) {
									iter.next().connect((SocketChannel) key.channel());
								}
							}
							if (key.isReadable()) {
								PPLClient.this.read(key);
							}
						}
					}
				} catch (final Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
		clientt.start();
		return this;
	}

	private void read(final SelectionKey key) {
		final SocketChannel ch = (SocketChannel) key.channel();
		try {
		if (this.buffer.toByteArray().length == 0) {// nothing has been read
			final ByteBuffer buf = ByteBuffer.allocate(4);// we need the packet
															// length
			// first
			buf.order(ByteOrder.BIG_ENDIAN);
			final int numRead = ch.read(buf);
			final byte[] data = buf.array();
			this.buffer.write(data);
			if (numRead < 4) {
				return;// try again on the next read
			} else {
				this.read(key);
				return;
			}
		}
		final int len = PPL.decodeInt(PPL.byteStreamToBuffer(this.buffer));
		final ByteBuffer databuf = ByteBuffer.allocate(len);
		final int existing = databuf.array().length;
		final int numRead = ch.read(databuf) + existing;// include bytes already
														// read
		if (numRead < len) {
			final byte[] data = databuf.array();
			this.buffer.write(data);
			return;// try again on the next read
		}
		// all data is ready
		final Iterator<SocketListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			iter.next().read(ch, databuf);
		}
		this.buffer.reset();// clear all data
		} catch (Exception e) {
			Iterator<SocketListener> iter=listeners.iterator();
			while (iter.hasNext()) iter.next().exception(ch, e);
		}
	}
	
	public PPLClient addListener(SocketListener l) {
		return (PPLClient) super.addListener(l);
	}
	
	public void shutdown() throws IOException {
		clientt.stop();
		this.ch.close();
	}
}
