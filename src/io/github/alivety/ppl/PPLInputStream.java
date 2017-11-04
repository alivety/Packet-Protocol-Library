package io.github.alivety.ppl;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PPLInputStream implements DataInput {
	private final DataInputStream in;

	public PPLInputStream(final InputStream in) {
		this.in = new DataInputStream(in);
	}

	public int available() throws IOException {
		return this.in.available();
	}

	public int read() throws IOException {
		return this.in.read();
	}

	@Override
	public boolean readBoolean() throws IOException {
		return this.in.readBoolean();
	}

	public boolean[] readBooleanArray() throws IOException {
		final int len = this.readInt();
		final boolean[] b = new boolean[len];
		for (int i = 0; i < len; i++) {
			b[i] = this.readBoolean();
		}
		return b;
	}

	@Override
	public byte readByte() throws IOException {
		return this.in.readByte();
	}

	public byte[] readByteArray() throws IOException {
		final int len = this.readInt();
		final byte[] b = new byte[len];
		for (int i = 0; i < len; i++) {
			b[i] = this.readByte();
		}
		return b;
	}

	@Override
	public char readChar() throws IOException {
		return (char) this.readShort();
	}

	@Override
	public double readDouble() throws IOException {
		return this.in.readDouble();
	}

	public double[] readDoubleArray() throws IOException {
		final int len = this.readInt();
		final double[] b = new double[len];
		for (int i = 0; i < len; i++) {
			b[i] = this.readDouble();
		}
		return b;
	}

	@Override
	public float readFloat() throws IOException {
		return this.in.readFloat();
	}

	public float[] readFloatArray() throws IOException {
		final int len = this.readInt();
		final float[] b = new float[len];
		for (int i = 0; i < len; i++) {
			b[i] = this.readFloat();
		}
		return b;
	}

	@Override
	public void readFully(final byte[] b) throws IOException {
		this.in.readFully(b);
	}

	@Override
	public void readFully(final byte[] b, final int off, final int len) throws IOException {
		this.in.readFully(b, off, len);
	}

	@Override
	public int readInt() throws IOException {
		return this.in.readInt();
	}

	public int[] readIntArray() throws IOException {
		final int len = this.readInt();
		final int[] b = new int[len];
		for (int i = 0; i < len; i++) {
			b[i] = this.readInt();
		}
		return b;
	}
	
	public int[][] readIntArrayArray() throws IOException {
		int len=this.readInt();
		int[][] b=new int[len][];
		for (int i=0;i<len;i++) {
			b[i]=this.readIntArray();
		}
		return b;
	}

	@Override
	public String readLine() throws IOException {
		return this.readUTF();
	}

	@Override
	public long readLong() throws IOException {
		return this.in.readLong();
	}

	public long[] readLongArray() throws IOException {
		final int len = this.readInt();
		final long[] b = new long[len];
		for (int i = 0; i < len; i++) {
			b[i] = this.readLong();
		}
		return b;
	}

	@Override
	public short readShort() throws IOException {
		return this.in.readShort();
	}

	public short[] readShortArray() throws IOException {
		final int len = this.readInt();
		final short[] b = new short[len];
		for (int i = 0; i < len; i++) {
			b[i] = this.readShort();
		}
		return b;
	}

	public String readString() throws IOException {
		return this.readUTF();
	}

	public String[] readStringArray() throws IOException {
		final int len = this.readInt();
		final String[] b = new String[len];
		for (int i = 0; i < len; i++) {
			b[i] = this.readString();
		}
		return b;
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return this.in.readUnsignedByte();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return this.in.readUnsignedShort();
	}

	@Override
	public String readUTF() throws IOException {
		final int len = this.readInt();
		final char[] c = new char[len];
		for (int i = 0; i < len; i++) {
			c[i] = (char) this.readShort();
		}
		return new String(c);
	}

	@Override
	public int skipBytes(final int n) throws IOException {
		return this.in.skipBytes(n);
	}
}
