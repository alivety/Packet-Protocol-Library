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
		Field[] a = this.getClass().getDeclaredFields();
		List<Field> al = new ArrayList<>();
		for (Field f : a) {
			if (f.isAnnotationPresent(PacketField.class)) {
				al.add(f);
			}
		}
		return al.toArray(new Field[] {});
	}

	public final void setPacketField(String name, Object t) throws IllegalArgumentException, IllegalAccessException {
		Field[] a = this.getPacketFields();
		for (Field f : a) {
			if (f.getName().equals(name)) {
				f.set(this, t);
			}
		}
	}

	public boolean clientBound() {
		return getData().bound().equals(Clientside.class) || commonBound();
	}

	public boolean serverBound() {
		return getData().bound().equals(Serverside.class) || commonBound();
	}

	public boolean commonBound() {
		return getData().bound().equals(Common.class);
	}

	private final void checkSanity() {
		PacketData data = this.getData();
		if (data == null) {
			throw new IllegalStateException(this.getClass().toString() + " has no @PacketData declaration");
		}
		if (!clientBound() || !serverBound() || !commonBound()) {
			throw new IllegalStateException(
					this.getClass().toString() + " has an invalid @PacketData.bound() delcaration");
		}
	}

	private final PacketData getData() {
		return this.getClass().getAnnotation(PacketData.class);
	}

	private ArrayList<Object> arrayToList(Object[] a) {
		ArrayList<Object> o = new ArrayList<>();
		for (Object ob : a) {
			if (ob instanceof Object[]) {
				ob = arrayToList((Object[]) ob);
			}
			o.add(ob);
		}
		return o;
	}

	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(this.getClass().getSimpleName()).append("(").append(this.getId()).append(")").append("{");
			for (Field f : this.getPacketFields()) {
				Object v = f.get(this);
				Object val;
				if (f.getType().isArray()) {
					val = arrayToList((Object[]) v);
				} else {
					val = v;
				}
				sb.append("").append(f.getType().getSimpleName() + " " + f.getName() + " = " + val);
				sb.append(";");
			}
			return sb.toString().substring(0, sb.lastIndexOf(";")) + "}";
		} catch (Exception e) {
			return e.toString();
		}
	}
}