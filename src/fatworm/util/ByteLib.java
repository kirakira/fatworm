package fatworm.util;

import java.nio.ByteBuffer;

public class ByteLib {
    public static int bytesToInt(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, data.length - offset).getInt();
    }

    public static void intToBytes(int value, byte[] data, int offset) {
        ByteBuffer.wrap(data, offset, data.length - offset).putInt(value);
    }
}
