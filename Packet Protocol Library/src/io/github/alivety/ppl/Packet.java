package io.github.alivety.ppl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class Packet {
	public static Packet c(final Class<?> packet, final Object... fieldValues)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
			InstantiationException, InvocationTargetException, NoSuchMethodException {
		final Constructor<?> ctor = packet.getConstructor();
		final Packet p = (Packet) ctor.newInstance();
		final String[] fields = p.getFields();
		if (fieldValues.length != (fields.length - 1) && fields.length!=1) {
			throw new IllegalStateException("Supplied fields and the number of fields do not match for "+packet);
		}
		for (int i = 0; i < fields.length; i++) {
			final String f = fields[i];
			if (f.equals("id")) {
				continue;
			}
			final Object v = fieldValues[i - 1];
			p.set(f, v);
		}
		return p;
	}

	/**
	 * Please use {@link AbstractPacket.c(Class<?> packet, Object... fieldValues} instead
	 * @param classLocation
	 * @param fieldValues
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 */
	public static Packet c(final String classLocation, final Object... fieldValues)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, ClassNotFoundException {
		Class<?> clazz;
		clazz = Class.forName(classLocation);
		return Packet.c(clazz, fieldValues);
	}

	/**
	 * Packet should be styleized as[packetLocation][Packet ID], eg
	 * example.packets.[Packet][0]
	 *
	 * @param packetLocation
	 * @param buffer
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws NoSuchFieldException
	 */
	public static Packet decode(final String packetLocation, final ByteBuffer buffer)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, IOException, ClassNotFoundException, InstantiationException, NoSuchFieldException {
		final PPLInputStream pin = new PPLInputStream(new ByteArrayInputStream(buffer.array()));
		final int packetID = pin.readInt();// account for packet id
		Class<?> clazz;
		clazz = Class.forName(packetLocation + packetID);

		final Constructor<?> ctor = clazz.getConstructor();
		final Packet p = (Packet) ctor.newInstance();
		final String[] fields = p.getFields();
		final String[] types = p.getFieldTypes();
		for (int i = 0; i < fields.length; i++) {
			final String f = fields[i].replaceAll("p_", "");
			if (f.equals("id")) {
				continue;// we already have the id
			}
			final String t = types[i];
			// System.out.println(f+"("+t+")");
			final Object val = pin.getClass().getMethod("read" + t).invoke(pin);
			p.set(f, val);
		}
		return p;
	}

	private String c(final String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	public ByteBuffer encode() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		if (!this.initialized()) {
			throw new IllegalStateException("Cannot encode packet before it is initialized");
		}
		final ByteArrayOutputStream bo = new ByteArrayOutputStream();
		final PPLOutputStream out = new PPLOutputStream(bo);
		final String[] fields = this.getFields();
		final String[] dataTypes = this.getFieldTypes();
		final Object[] data = this.getFieldValues();
		for (int i = 0; i < dataTypes.length; i++) {
			final String type = dataTypes[i];
			final Object field = data[i];
			try {
				out.getClass().getMethod("write" + type, field.getClass()).invoke(out, field);
			} catch (final NullPointerException npe) {
				throw new IllegalStateException(
						"Attempted to encode non-initialized packet. This should never happem. Missing field "
								+ fields[i]);
			}
		}
		return ByteBuffer.wrap(bo.toByteArray());
	}

	private boolean fieldHasAnnotation(final Field f) {
		return f.getAnnotation(PacketField.class) != null;
	}

	@SuppressWarnings("unchecked")
	public <T> T getField(final String field) throws IllegalArgumentException, IllegalAccessException {
		final String[] fields = this.getFields();
		final Object[] values = this.getFieldValues();
		for (int i = 0; i < fields.length; i++) {
			final String f = fields[i];
			final Object v = values[i];
			if (f.equals(field)) {
				return (T) v;
			}
		}
		throw new IllegalArgumentException("Packet" + this.getPacketID() + " does not have field " + field);
	}

	public String[] getFields() {
		final Field[] fields = this.getClass().getDeclaredFields();
		final ArrayList<String> packetFields = new ArrayList<>();
		for (final Field field : fields) {
			if (this.fieldHasAnnotation(field)) {
				packetFields.add(field.getName());
			}
		}
		String[] pfs = new String[packetFields.size()];
		pfs = packetFields.toArray(pfs);
		return pfs;
	}

	public String[] getFieldTypes() {
		final Field[] fields = this.getClass().getDeclaredFields();
		final ArrayList<String> packetFields = new ArrayList<>();
		for (final Field field : fields) {
			if (this.fieldHasAnnotation(field)) {
				packetFields.add(this.c(field.getType().getSimpleName().toLowerCase()).replace("[]", "Array").trim());
			}
		}
		String[] pfs = new String[packetFields.size()];
		pfs = packetFields.toArray(pfs);
		return pfs;
	}

	public Object[] getFieldValues() throws IllegalArgumentException, IllegalAccessException {
		final Field[] fields = this.getClass().getDeclaredFields();
		final ArrayList<Object> packetFields = new ArrayList<>();
		for (final Field field : fields) {
			if (this.fieldHasAnnotation(field)) {
				final boolean accessible = field.isAccessible();
				field.setAccessible(true);
				packetFields.add(field.get(this));
				field.setAccessible(accessible);
			}
		}
		Object[] pfs = new Object[packetFields.size()];
		pfs = packetFields.toArray(pfs);
		return pfs;
	}

	public int getPacketID() throws IllegalArgumentException, IllegalAccessException {
		return this.<Integer>getField("id");
	}

	public boolean initialized() throws IllegalArgumentException, IllegalAccessException {
		boolean init = true;
		final Object[] fields = this.getFieldValues();
		for (final Object field : fields) {
			if (field == null) {
				init = false;
			}
		}
		return init;
	}

	/**
	 * Do not include the "p_" prefix
	 *
	 * @param field
	 * @param value
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public <T> void set(final String field, final T value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final Field f = this.getClass().getDeclaredField(field);
		final boolean accessible = f.isAccessible();
		f.setAccessible(true);
		f.set(this, value);
		f.setAccessible(accessible);
	}

	@Override
	public String toString() {
		String s = "AbstractPacket{";
		try {
			final String[] fields = this.getFields();
			final String[] fieldTypes = this.getFieldTypes();
			final Object[] values = this.getFieldValues();
			for (int i = 0; i < fields.length; i++) {
				final String f = fields[i];
				final String t = fieldTypes[i];
				Object v = values[i];
				final String type = t;
				if (type.contains("Array") && !type.contains("Byte")) {
					final Object[] arr = (Object[]) v;
					final StringBuilder sb = new StringBuilder();
					sb.append(t.replaceAll("Array", "["));
					for (final Object element : arr) {
						sb.append(element).append("; ");
					}
					if (sb.lastIndexOf("; ") != -1) {
						v = sb.toString().substring(0, sb.lastIndexOf("; ")) + "]";
					} else {
						v = sb.toString() + "]";
					}
				}
				s = s + f + "(" + type.replaceFirst("Array", "[]") + ")=" + v + "; ";
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return s + "NULL";
		}
		s = s.substring(0, s.lastIndexOf("; "));
		return s + "}";
	}
}
