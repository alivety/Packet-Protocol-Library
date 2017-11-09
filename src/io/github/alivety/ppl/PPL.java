package io.github.alivety.ppl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.reflections.Reflections;

import io.github.alivety.ppl.packet.Packet;

public class PPL {
	private static Set<Class<? extends Packet>> packets;
	private static Class<? extends Packet>[] pids;
	
	@SuppressWarnings("unchecked")
	public static void loadPackets() throws InstantiationException, IllegalAccessException {
		if (packets!=null) {
			throw new IllegalStateException("Packets have already been loaded");
		}
		Reflections ref=new Reflections("");
		PPL.packets=ref.getSubTypesOf(Packet.class);
		pids=(Class<? extends Packet>[]) new Class<?>[PPL.packets.size()];
		Iterator<Class<? extends Packet>> iter=packets.iterator();
		while (iter.hasNext()) {
			Packet p=iter.next().newInstance();
			if (pids[p.getId()]==null) {
				pids[p.getId()]=p.getClass();
			} else {
				throw new IllegalStateException(p.getClass().toString()+" and "+pids[p.getId()]+" share the same packet id");
			}
		}
	}
	
	public static Packet newInstance(int id,Object...fields) throws InstantiationException, IllegalAccessException {
		if (id>pids.length-1 || id<0) {
			throw new IllegalArgumentException("There is no packet id="+id);
		}
		if (pids[id]==null) {
			throw new IllegalArgumentException("There is no packet id="+id);
		}
		Packet p=pids[id].newInstance();
		if (p.getPacketFields().length!=fields.length) {
			throw new IllegalArgumentException("Mismatched number of fields: expected "+p.getPacketFields().length+", got "+fields.length);
		}
		for (int i=0;i<fields.length;i++) {
			p.setPacketField(p.getPacketFields()[i].getName(), fields[i]);
		}
		return p;
	}
	
	public static ByteBuffer byteStreamToBuffer(final ByteArrayOutputStream o) {
		return ByteBuffer.wrap(o.toByteArray()).order(ByteOrder.BIG_ENDIAN);
	}

	public static int decodeInt(final ByteBuffer b) {
		return b.order(ByteOrder.BIG_ENDIAN).getInt();
	}

	private static ByteBuffer encapsulate(final ByteBuffer data) {
		final ByteBuffer len = PPL.encodeInt(data.array().length);
		final ByteBuffer capsule = ByteBuffer.allocate(len.array().length + data.array().length);
		capsule.put((ByteBuffer) len.position(0)).put((ByteBuffer) data.position(0));
		return ((ByteBuffer) capsule.position(0)).order(ByteOrder.BIG_ENDIAN);
	}
	
	public static ByteBuffer encode(Packet c) throws IOException {
		ByteArrayOutputStream bo=new ByteArrayOutputStream();
		ObjectOutputStream out=new ObjectOutputStream(bo);
		out.writeObject(c.getId());
		for (Field f:c.getPacketFields()) {
			try {
				out.writeObject(f.get(c));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IOException(e);
			}
		}
		return PPL.encapsulate(ByteBuffer.wrap(bo.toByteArray()));
	}
	
	public static Packet decode(ByteBuffer b) throws IOException {
		Packet p=null;
		try {
		ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(b.array()));
		int id=(int)in.readObject();
		Class<? extends Packet> clz=pids[id];
		p=clz.newInstance();
		for (Field f:p.getPacketFields()) {
			f.set(p, in.readObject());
		}
		} catch (Exception e) {
			throw new IOException(e);
		}
		return p;
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
