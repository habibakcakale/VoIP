package habib.voip.crypto;

import android.util.Log;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import habib.voip.Values;

public class SecurityManager {
    public RSAPublicKey publicKey;
    public RSAPrivateKey privateKey;
    Cipher cipher;

    public SecurityManager() throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("RSA");
    }

    public void generateKeys(int size) {
        try {
            //Get Key Pair Generator for RSA.
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");//Key pair generator for RSA
            keyGen.initialize(size);
            KeyPair keypair = keyGen.genKeyPair();

            // Get the bytes of the public and private keys
            byte[] privateKeyBytes = keypair.getPrivate().getEncoded();
            byte[] publicKeyBytes = keypair.getPublic().getEncoded();

            //Generate the Private Key, Public Key and Public Key in XML format.
            KeyFactory factory = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            publicKey = (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.i(Values.LogTag, "No such algorithm. Please check the JDK version." + e.getCause());
        } catch (InvalidKeySpecException ik) {
            Log.i(Values.LogTag, "Invalid Key Specs. Not valid Key files." + ik.getCause());
        }
    }

    public byte[] getModulusBytes() {
        byte[] modulusBytes = publicKey.getModulus().toByteArray();
        return stripLeadingZeros(modulusBytes);
    }

    public byte[] getExponentBytes() {
        return publicKey.getPublicExponent().toByteArray();
    }

    /**
     * Utility method to delete the leading zeros from the modulus.
     *
     * @param a modulus
     * @return modulus
     */
    private byte[] stripLeadingZeros(byte[] a) {
        int lastZero = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0) {
                lastZero = i;
            } else {
                break;
            }
        }
        lastZero++;
        byte[] result = new byte[a.length - lastZero];
        System.arraycopy(a, lastZero, result, 0, result.length);
        return result;
    }

    public byte[] EncrytWithPublic(byte[] data) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public byte[] EncrytWithPrivate(byte[] data) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {

        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public byte[] DecryptWithPublic(byte[] data) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public byte[] DecryptWithPrivate(byte[] data) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException {
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }
}