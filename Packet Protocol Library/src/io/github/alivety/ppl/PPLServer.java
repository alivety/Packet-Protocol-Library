package io.github.alivety.ppl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;

public class PPLServer extends PPL {
	private final HashMap<SocketChannel, ByteArrayOutputStream> buffers = new HashMap<>();
	private ServerSocketChannel servlet;
	private Thread servert;

	private void accept(final SelectionKey key, final Selector sel) {
		final ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel ch=null;
		try {
			ch=server.accept();
		ch.configureBlocking(false);
		ch.register(sel, SelectionKey.OP_READ);
		this.buffers.put(ch, new ByteArrayOutputStream());
		final Iterator<SocketListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			iter.next().connect(ch);
		}
		}catch (Exception e) {
			Iterator<SocketListener> iter=listeners.iterator();
			while (iter.hasNext()) iter.next().exception(ch, e);
		}
	}

	public PPLServer bind(final int port) throws Exception {
		try {
			final ServerSocket servlet = new ServerSocket(port);// easy check
																// for open
			// port
			servert=new Thread("ppl-server-" + port) {
				@Override
				public void run() {
					try {
						servlet.close();
						final Selector sel = Selector.open();
						final ServerSocketChannel server = ServerSocketChannel.open();
						server.configureBlocking(false);
						server.bind(new InetSocketAddress("localhost", port));
						server.register(sel, SelectionKey.OP_ACCEPT);
						PPLServer.this.servlet=server;

						while (true) {
							sel.select();
							final Iterator<SelectionKey> keys = sel.selectedKeys().iterator();
							while (keys.hasNext()) {
								final SelectionKey key = keys.next();
								keys.remove();

								if (!key.isValid()) {
									continue;
								}
								if (key.isAcceptable()) {
									PPLServer.this.accept(key, sel);
								}
								if (key.isReadable()) {
									PPLServer.this.read(key);
								}
							}
						}
					} catch (final Exception e) {
						throw new RuntimeException(e);
					}
				}
			};
			servert.start();
		} catch (final Exception e) {
			throw e;
		}
		return this;
	}

	private void read(final SelectionKey key) {
		SocketChannel ch=(SocketChannel)key.channel();
		try {
		if (this.buffers.get(ch).toByteArray().length == 0) {// nothing has been
			// written yet
			final ByteBuffer buf = ByteBuffer.allocate(4);// we need the packet
															// length
			// first
			buf.order(ByteOrder.BIG_ENDIAN);
			final int numRead = ch.read(buf);
			final byte[] data = buf.array();
			this.buffers.get(ch).write(data);
			if (numRead < 4) {
				// System.out.println("only "+numRead+" bytes read");
				return;// try again on the next read
			} else {
				this.read(key);
				return;
			}
		}
		// data has been written
		final int len = PPL.decodeInt(ByteBuffer.wrap(this.buffers.get(ch).toByteArray()));
		final ByteBuffer databuf = ByteBuffer.allocate(len);
		final int existing = databuf.array().length;
		final int numRead = ch.read(databuf) + existing;// include bytes already
														// read
		if (numRead < len) {
			final byte[] data = databuf.array();
			this.buffers.get(ch).write(data);
			return;// try again on the next read
		}
		// all data is ready
		final Iterator<SocketListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			iter.next().read(ch, databuf);
		}
		this.buffers.get(ch).reset();// clear all data
		} catch (Exception e) {
			Iterator<SocketListener> iter=listeners.iterator();
			while (iter.hasNext()) iter.next().exception(ch, e);
		}
	}
	
	public PPLServer addListener(SocketListener l) {
		return (PPLServer) super.addListener(l);
	}

	@Override
	public void shutdown() throws IOException {
		servlet.close();
		servert.stop();
	}
}
