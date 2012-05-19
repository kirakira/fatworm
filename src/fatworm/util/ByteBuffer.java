package fatworm.util;

public class ByteBuffer {
    private java.nio.ByteBuffer buffer;
    int length;

    public ByteBuffer() {
        buffer = java.nio.ByteBuffer.allocate(4096);
        length = 0;
    }

    public ByteBuffer(byte[] array) {
        buffer = java.nio.ByteBuffer.wrap(array);
        length = array.length;
    }

    private void makeRoom(int size) {
        if (buffer.capacity() - length < size) {
            int position = buffer.position();
            byte[] newBuffer = new byte[buffer.capacity() * 2];
            byte[] oldBuffer = buffer.array();
            System.arraycopy(oldBuffer, 0, newBuffer, 0, length);
            buffer = java.nio.ByteBuffer.wrap(newBuffer);
            buffer.position(position);
        }
    }
    
    private void updateLength() {
        if (buffer.position() > length)
            length = buffer.position();
    }

    public byte[] array() {
        if (buffer.capacity() == length)
            return buffer.array();
        else {
            byte[] ret = new byte[length];
            byte[] src = buffer.array();
            System.arraycopy(src, 0, ret, 0, length);
            return ret;
        }
    }

    public void putByte(byte value) {
        makeRoom(1);
        buffer.put(value);
        updateLength();
    }

    public byte getByte() {
        return buffer.get();
    }

    public void putBoolean(boolean value) {
        if (value)
            putByte((byte) 1);
        else
            putByte((byte) 0);
    }

    public boolean getBoolean() {
        byte value = getByte();
        if (value == 0)
            return false;
        else
            return true;
    }

    public void putInt(int value) {
        makeRoom(4);
        buffer.putInt(value);
        updateLength();
    }

    public int getInt() {
        return buffer.getInt();
    }

    public void putLong(long value) {
        makeRoom(8);
        buffer.putLong(value);
        updateLength();
    }

    public long getLong() {
        return buffer.getLong();
    }

    public void putDouble(double value) {
        makeRoom(8);
        buffer.putDouble(value);
        updateLength();
    }

    public double getDouble() {
        return buffer.getDouble();
    }

    public void putChar(char value) {
        makeRoom(2);
        buffer.putChar(value);
        updateLength();
    }

    public char getChar() {
        return buffer.getChar();
    }

    public void putString(String s) {
        byte[] data = s.getBytes();
        putInt(data.length);
        putBytes(data, 0, data.length);
        updateLength();
    }

    public String getString() {
        int len = getInt();
        int pos = buffer.position();
        String ret = new String(buffer.array(), pos, len);
        buffer.position(pos + len);
        return ret;
    }

    public void putBytes(byte[] data, int offset, int len) {
        makeRoom(len);
        buffer.put(data, offset, len);
        updateLength();
    }

    public void getBytes(byte[] data, int offset, int len) {
        buffer.get(data, offset, len);
    }
}
