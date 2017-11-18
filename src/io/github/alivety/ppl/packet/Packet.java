package io.github.alivety.ppl.packet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Packet {
	public Packet() {
		this.checkSanity();
	}
	
	public final int getId() {
		this.checkSanity();
		return this.getData().id();
	}
	
	public final Field[] getPacketFields() {
		final Field[] a = this.getClass().getDeclaredFields();
		final List<Field> al = new ArrayList<>();
		for (final Field f : a)
			if (f.isAnnotationPresent(PacketField.class))
				al.add(f);
		return al.toArray(new Field[] {});
	}
	
	public final void setPacketField(final String name, final Object t) throws IllegalArgumentException, IllegalAccessException {
		final Field[] a = this.getPacketFields();
		for (final Field f : a)
			if (f.getName().equals(name))
				f.set(this, t);
	}
	
	public boolean clientBound() {
		return this.getData().bound().equals(Clientside.class) || this.commonBound();
	}
	
	public boolean serverBound() {
		return this.getData().bound().equals(Serverside.class) || this.commonBound();
	}
	
	public boolean commonBound() {
		return this.getData().bound().equals(Common.class);
	}
	
	private final void checkSanity() {
		final PacketData data = this.getData();
		if (data == null)
			throw new IllegalStateException(this.getClass().toString() + " has no @PacketData declaration");
		if (!this.clientBound() || !this.serverBound() || !this.commonBound())
			throw new IllegalStateException(this.getClass().toString() + " has an invalid @PacketData.bound() delcaration");
	}
	
	private final PacketData getData() {
		return this.getClass().getAnnotation(PacketData.class);
	}
	
	private ArrayList<Object> arrayToList(final Object[] a) {
		final ArrayList<Object> o = new ArrayList<>();
		for (Object ob : a) {
			if (ob instanceof Object[])
				ob = this.arrayToList((Object[]) ob);
			o.add(ob);
		}
		return o;
	}
	
	@Override
	public String toString() {
		try {
			final StringBuilder sb = new StringBuilder();
			sb.append(this.getClass().getSimpleName()).append("(").append(this.getId()).append(")").append("{");
			for (final Field f : this.getPacketFields()) {
				final Object v = f.get(this);
				Object val;
				if (f.getType().isArray())
					val = this.arrayToList((Object[]) v);
				else
					val = v;
				sb.append("").append(f.getType().getSimpleName() + " " + f.getName() + " = " + val);
				sb.append(";");
			}
			return sb.toString().substring(0, sb.lastIndexOf(";")) + "}";
		} catch (final Exception e) {
			return e.toString();
		}
	}
}