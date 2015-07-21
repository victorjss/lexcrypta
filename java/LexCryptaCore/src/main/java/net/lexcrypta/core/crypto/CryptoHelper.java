/*
 * Copyright (C) 2015 Víctor Suárez <victorjss@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.lexcrypta.core.crypto;

import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Víctor Suárez <victorjss@gmail.com>
 */
public class CryptoHelper {
    static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";
    static final String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding"; //used for encrypt iv
    public static final int KEY_LENGTH = 128;
    public static final int IV_LENGTH = 128; //always on AES (128 block size, regardless key length)
    public static final int MIN_IV_LENGTH = 6;
    
    
    KeyGenerator keyGenerator;

    public CryptoHelper() {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(KEY_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public SecretKey getNewSecretKey() {
        return keyGenerator.generateKey();
    }
    
    public byte[] getNewKey() {
        return keyGenerator.generateKey().getEncoded();
    }
    
    public String convertKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    public SecretKey convertKey(String key) {
        byte[] bytes = Base64.getDecoder().decode(key);
        return new SecretKeySpec(bytes, "AES");
    }

    public SecretKey convertKey(byte[] key) {
        return new SecretKeySpec(key, "AES");
    }
    
    public byte[] fixIv(byte[] iv) {
        int ivBytes = IV_LENGTH / 8;
        if (iv.length < MIN_IV_LENGTH) {
            throw new IllegalArgumentException("IV must be at least 6 bytes long");
        }
        byte[] fixed = new byte[ivBytes];
        
        if (iv.length >= ivBytes) {
            System.arraycopy(iv, 0, fixed, 0, ivBytes);
        } else {
            System.arraycopy(iv, 0, fixed, 0, iv.length);
            for (int i = iv.length; i < ivBytes; i++) {
                fixed[i] = iv[i % iv.length];
            }
        }
        
        return fixed;
    }

    public InputStream decrypt(InputStream encryptedContent, byte[] iv, byte[] key) {
        try {
            Cipher aesCipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
            aesCipher.init(Cipher.DECRYPT_MODE, convertKey(key), new IvParameterSpec(iv));
            return new CipherInputStream(encryptedContent, aesCipher);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encryptIv(byte[] iv, byte[] key) {
        try {
            Cipher aesIvCipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
            aesIvCipher.init(Cipher.ENCRYPT_MODE, convertKey(key)); //ECB does not need IV
            byte [] encryptedIv = aesIvCipher.doFinal(iv);
            return encryptedIv;
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream encrypt(InputStream plainContent, byte[] iv, byte[] key) {
        try {
            Cipher aesCipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
            aesCipher.init(Cipher.ENCRYPT_MODE, convertKey(key), new IvParameterSpec(iv));
            return new CipherInputStream(plainContent, aesCipher);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }
    
}
