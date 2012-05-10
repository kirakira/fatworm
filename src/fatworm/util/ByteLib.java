package fatworm.util;

public class ByteLib {
    public static int bytesToInt(byte[] data, int offset) {
        return (int) ((((int) data[offset + 0] & 0xFF) << 0)
                | (((int) data[offset + 1] & 0xFF) << 8)
                | (((int) data[offset + 2] & 0xFF) << 16) | (((int) data[offset + 3] & 0xFF) << 24));
    }

    public static void intToBytes(int value, byte[] data, int offset) {
        data[offset + 0] = (byte) ((value >> 0) & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }

    public static String bytesToString(byte[] data, int offset, int len) {
        return new String(data, offset, len);
    }

    public static byte[] stringToBytes(String s) {
        return s.getBytes();
    }
}
