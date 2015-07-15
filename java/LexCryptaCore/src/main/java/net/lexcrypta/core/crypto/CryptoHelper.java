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
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
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

    KeyGenerator keyGenerator;

    public CryptoHelper() {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public SecretKey getNewKey() {
        return keyGenerator.generateKey();
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
    
    
    public InputStream decrypt(InputStream encryptedContent, byte[] iv, byte[] key) {
        try {
            Cipher aesCipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
            aesCipher.init(Cipher.DECRYPT_MODE, convertKey(key), new IvParameterSpec(iv));
            return new CipherInputStream(encryptedContent, aesCipher);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
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
