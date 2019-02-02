package org.lappsgrid.eager.mining.web.util

import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import org.apache.commons.codec.binary.Base64

import java.security.InvalidKeyException
import java.security.Security

/**
 *
 */
class Encoder {

    String secret

    Encoder() {
        //secret = System.getenv("GALAXY_SECRET")
        secret = '0123456789abcdef'
        if (secret == null) {
            throw new InvalidKeyException("GALAXY_SECRET has not been set.")
        }
//        println secret.substring(0, 8)
        println secret.length()
        secret = secret.substring(0, 16)
    }

    String encrypt(String data) {
        byte[] encrypt = data.bytes
        if (secret == null) {
            throw new InvalidKeyException("GALAXY_SECRET has not been set.")
        }

        if(encrypt.size() % 8 != 0){ //not a multiple of 8
            byte[] padded = new byte[encrypt.length + 8 - (encrypt.length % 8)];

            //copy the old array into it
            System.arraycopy(encrypt, 0, padded, 0, encrypt.length);
            encrypt = padded;
        }
        byte[] result

//        try {
            SecretKeySpec key = new SecretKeySpec(secret.bytes, "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            result = cipher.doFinal(encrypt);
//        } catch (Exception e) {
//            e.printStackTrace();
//            result = null;
//        }
        return result.encodeHex().toString()
//        println new String(result)
//        new String(Base64.encodeBase64(result))
    }

    String decrypt(String data) {
        byte[] result = Base64.decodeBase64(data.bytes)
//        try {
            SecretKeySpec key = new SecretKeySpec(secret.bytes, "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            result = cipher.doFinal(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            result = null;
//        }

        new String(result)
    }

    static void main(String[] args) {
//        Security.setProperty("crypto.policy", "unlimited");

        Encoder encoder = new Encoder()
        println encoder.encrypt("!!suderman@cs.vassar.edu")
        println 'ksuderman'.bytes.encodeHex().toString()
        println '!!suderman@cs.vassar.edu'.bytes.encodeHex().toString()
    }
}
