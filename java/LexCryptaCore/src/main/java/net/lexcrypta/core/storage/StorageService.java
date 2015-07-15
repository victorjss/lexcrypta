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
package net.lexcrypta.core.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import net.lexcrypta.core.crypto.CryptoHelper;
import net.lexcrypta.core.jdbc.JdbcHelper;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Víctor Suárez <victorjss@gmail.com>
 */
public class StorageService {
    CryptoHelper cryptoHelper = new CryptoHelper();
    JdbcHelper jdbcHelper = new JdbcHelper();

    static volatile Properties coreProps = null;
    
    /**
     * Right pad 's' with the 'c' character to obtain a valid AES key. 
     * The number of padding characters depends on s.length(), but the resulting 
     * string will always be of the same size as the AES key. If 's' is bigger 
     * than AES key valid length, then 's' will be truncated.
     * @param s String to be padded
     * @param c Character for right padding
     * @return 's' string right padded with 'c' characters, or truncated to AES
     * key size.
     */
    public String rightPad(String s, char c) {
        StringBuilder sb = new StringBuilder(s);
        final int keyLength = cryptoHelper.getKeyLength() / 8;
        final int niters = keyLength - s.length();
        for (int i = 0; i < niters; i++) {
            sb.append(c);
        }
        return sb.substring(0, keyLength);
    }
    
    /**
     * Encrypt content, using AES with a random key and a Initialization Vector
     * generated from 'seed' parameter.
     * This method returns a EncryptedData structure with all the necessary 
     * information to allow following storage in data base.
     * @param content
     * @param seed
     * @return 
     */
    public EncryptedData encryptContent(InputStream content, String seed) {
        try {
            byte[] iv = getIv(seed);
            byte[] key = cryptoHelper.getNewKey();
            InputStream encryptedStream = cryptoHelper.encrypt(content, iv, key);

            String basePath = jdbcHelper.getConfigurationValue("storage.basePath");
            String destDirPath = basePath + File.separator + System.currentTimeMillis();
            File destDir = new File(destDirPath);
            if (!destDir.exists()) {
                destDir.mkdir();
            }
            File aesFile = File.createTempFile("", ".aes", destDir);
            FileOutputStream fos = new FileOutputStream(aesFile);

            IOUtils.copyLarge(encryptedStream, fos, new byte[512]);

            InputStream encryptedSeedStream = cryptoHelper.encrypt(new ByteArrayInputStream(seed.getBytes("utf-8")), iv, key);
            byte[] id = IOUtils.toByteArray(encryptedSeedStream);
            
            InputStream encryptedPathStream = cryptoHelper.encrypt(new ByteArrayInputStream(destDirPath.getBytes("utf-8")), iv, key);
            byte[] encryptedPath = IOUtils.toByteArray(encryptedPathStream);

            EncryptedData ed = new EncryptedData();
            ed.setKey(key);
            ed.setId(id);
            ed.setEncryptedPath(encryptedPath);

            return ed;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate the Initialization Vector from seed bytes
     * @param seed the seed
     * @return
     */
    protected byte[] getIv(String seed) {
        try {
            return rightPad(seed, '!').getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            //weird, hardcoded UTF-8
            throw new RuntimeException(e);
        }
    }
}
