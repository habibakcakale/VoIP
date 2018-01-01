package habib.voip;

import java.util.Arrays;

import javax.crypto.spec.IvParameterSpec;

public class Values {
    public static final int PORT = 15446;
    public static final int BUFFER_SIZE = 32;
    public static String IP = "192.168.1.22";//"91.229.35.15";
    public static boolean running;
    public static String LogTag = "VoIP";
    public static int KEYSIZE = 2048;//RSA KEY SIZE
    public static byte[] SessionKey;
    public static IvParameterSpec IV;
    public static byte[] vector = new byte[]{0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x01,
            0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x01};

    public static char[] getSessionKey() {
        return new String(Arrays.copyOfRange(SessionKey, SessionKey.length - 128, SessionKey.length)).toCharArray();
    }

    static {
        IV = new IvParameterSpec(vector);
    }
}
