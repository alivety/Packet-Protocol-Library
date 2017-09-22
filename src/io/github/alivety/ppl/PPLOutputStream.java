package io.github.alivety.ppl;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PPLOutputStream implements DataOutput {
	private final DataOutputStream out;

	public PPLOutputStream(final OutputStream os) {
		this.out = new DataOutputStream(os);
	}

	@Override
	public void write(final byte[] b) throws IOException {
		this.out.write(b);
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		this.out.write(b, off, len);
	}

	@Override
	public void write(final int arg0) throws IOException {
		this.out.write(arg0);
	}

	@Override
	public void writeBoolean(final boolean b) throws IOException {
		this.out.writeBoolean(b);
	}

	public void writeBoolean(final Boolean b) throws IOException {
		this.writeBoolean(b.booleanValue());
	}

	public void writeBooleanArray(final Boolean[] b) throws IOException {
		this.writeInt(b.length);
		for (final Boolean element : b) {
			this.out.writeBoolean(element);
		}
	}

	public void writeByte(final Byte b) throws IOException {
		this.writeByte(b.byteValue());
	}

	@Override
	public void writeByte(final int b) throws IOException {
		this.out.writeByte(b);
	}

	public void writeByteArray(final byte[] b) throws IOException {
		this.writeInt(b.length);
		for (final Byte element : b) {
			this.writeByte(element);
		}
	}

	public void writeByteArray(final Byte[] b) throws IOException {
		this.writeInt(b.length);
		for (final Byte element : b) {
			this.writeByte(element);
		}
	}

	@Override
	public void writeBytes(final String s) throws IOException {
		this.out.writeBytes(s);
	}

	public void writeChar(final Character c) throws IOException {
		this.writeChar(c.charValue());
	}

	@Override
	public void writeChar(final int c) throws IOException {
		this.writeShort((short) c);
	}

	@Override
	public void writeChars(final String s) throws IOException {
		this.out.writeChars(s);
	}

	@Override
	public void writeDouble(final double d) throws IOException {
		this.out.writeDouble(d);
	}

	public void writeDouble(final Double d) throws IOException {
		this.writeDouble(d.doubleValue());
	}

	public void writeDoubleArray(final double[] b) throws IOException {
		this.writeInt(b.length);
		for (final double element : b) {
			this.writeDouble(element);
		}
	}

	@Override
	public void writeFloat(final float f) throws IOException {
		this.out.writeFloat(f);
	}

	public void writeFloat(final Float f) throws IOException {
		this.writeFloat(f.floatValue());
	}

	public void writeFloatArray(final float[] b) throws IOException {
		this.writeInt(b.length);
		for (final float element : b) {
			this.writeFloat(element);
		}
	}

	@Override
	public void writeInt(final int i) throws IOException {
		this.out.writeInt(i);
	}

	public void writeInt(final Integer i) throws IOException {
		this.writeInt(i.intValue());
	}

	public void writeIntArray(final int[] b) throws IOException {
		this.writeInt(b.length);
		for (final int element : b) {
			this.writeInt(element);
		}
	}

	@Override
	public void writeLong(final long l) throws IOException {
		this.out.writeLong(l);
	}

	public void writeLong(final Long l) throws IOException {
		this.writeLong(l.longValue());
	}

	public void writeLongArray(final long[] b) throws IOException {
		this.writeInt(b.length);
		for (final long element : b) {
			this.writeLong(element);
		}
	}

	@Override
	public void writeShort(final int s) throws IOException {
		this.out.writeShort(s);
	}

	public void writeShort(final Short s) throws IOException {
		this.writeShort(s.shortValue());
	}

	public void writeShortArray(final Short[] b) throws IOException {
		this.writeInt(b.length);
		for (final Short element : b) {
			this.writeShort(element);
		}
	}

	public void writeString(final String s) throws IOException {
		this.writeUTF(s);
	}

	public void writeStringArray(final String[] b) throws IOException {
		this.writeInt(b.length);
		for (final String element : b) {
			this.writeString(element);
		}
	}

	@Override
	public void writeUTF(final String s) throws IOException {
		final char[] c = s.toCharArray();
		this.writeInt(c.length);
		for (final char element : c) {
			this.writeChar(element);
		}
	}

	public void writeVector(final float x, final float y, final float z) throws IOException {
		this.writeFloat(x);
		this.writeFloat(y);
		this.writeFloat(z);
	}

}
