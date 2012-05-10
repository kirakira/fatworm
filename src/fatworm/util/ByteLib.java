package fatworm.util;

import java.nio.ByteBuffer;

public class ByteLib {
    public static int bytesToInt(byte[] data, int offset) {
        ByteBuffer.wrap(data, offset, data.length - offset).getInt();
    }

    public static void intToBytes(int value, byte[] data, int offset) {
        ByteBuffer.wrap(data, offset, data.length - offset).putInt(value);
    }

    public static long bytesToLong(byte[] data, int offset) {
        ByteBuffer.wrap(data, offset, data.length - offset).getLong();
    }

    public static void longToBytes(long value, byte[] data, int offset) {
        ByteBuffer.wrap(data, offset, data.length - offset).putLong(value);
    }

    public static double bytesToDouble(byte[] data, int offset) {
        ByteBuffer.wrap(data, offset, data.length - offset).getDouble();
    }

    public static void doubleToBytes(double value, byte[] data, int offset) {
        ByteBuffer.wrap(data, offset, data.length - offset).putDouble(value);
    }

    public static String bytesToString(byte[] data, int offset, int len) {
        return new String(data, offset, len);
    }

    public static byte[] stringToBytes(String s) {
        return s.getBytes();
    }
}
