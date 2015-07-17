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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import net.lexcrypta.core.crypto.CryptoHelper;
import net.lexcrypta.core.conf.CoreHelper;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Víctor Suárez <victorjss@gmail.com>
 */
public class StorageService {
    CryptoHelper cryptoHelper = new CryptoHelper();
    CoreHelper coreHelper = new CoreHelper();

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
     * information to allow following storage in data base. Encryted data is 
     * stored in file system, without a clue of original name/info.
     * @param content conted to be encrypted
     * @param seed value used to genererate the AES Initialization Vector
     * @return struct with reference info of encryptation result
     */
    public EncryptedData encryptContent(InputStream content, String seed) {
        try {
            byte[] key = cryptoHelper.getNewKey();
            String targetDirPath = getTargetDirPath();
            File targetFile = getTargetFile(targetDirPath);
            return doEncryptContent(content, targetFile, seed, key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method was created/refactored for facilitating development
     * of integration tests. 
     * This method builds a EncryptedData structure from parameters.
     * @param content conted to be encrypted
     * @param targetFile File where encrypted content will be saved
     * @param seed value used to genererate the AES Initialization Vector
     * @param key AES key used for encryption
     * @return struct with reference info of encryptation result
     * @throws IOException
     * @throws FileNotFoundException 
     */
    protected EncryptedData doEncryptContent(InputStream content, 
            File targetFile,
            String seed,
            byte[] key)
            throws IOException, FileNotFoundException {
        
        byte[] iv = getIv(seed);
        InputStream encryptedStream = cryptoHelper.encrypt(content, iv, key);

        FileOutputStream fos = new FileOutputStream(targetFile);
        IOUtils.copyLarge(encryptedStream, fos, new byte[512]);
        
        byte[] id = encryptString(seed, iv, key);
        byte[] encryptedPath = encryptString(targetFile.getPath(), iv, key);
        
        EncryptedData ed = new EncryptedData();
        ed.setKey(key);
        ed.setId(id);
        ed.setEncryptedPath(encryptedPath);
        
        return ed;
    }

    protected File getTargetFile(String targetDirPath)
            throws IOException {
        File targetDir = new File(targetDirPath);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        File aesFile = File.createTempFile("", ".aes", targetDir);
        return aesFile;
    }

    protected String getTargetDirPath() {
        String basePath = coreHelper.getConfigurationValue("storage.basePath");
        String targetDirPath = basePath + File.separator + System.currentTimeMillis();
        return targetDirPath;
    }

    protected byte[] encryptString(String s,
            byte[] iv,
            byte[] key)
            throws IOException, UnsupportedEncodingException {
        InputStream encryptedStream = cryptoHelper.encrypt(new ByteArrayInputStream(s.getBytes("utf-8")), iv, key);
        byte[] id = IOUtils.toByteArray(encryptedStream);
        return id;
    }

    /**
     * Generate the Initialization Vector from seed bytes
     * @param seed the seed
     * @return correct padded IV for AES use
     */
    protected byte[] getIv(String seed) {
        if (seed.length() < 6) {
            throw new IllegalArgumentException("seed too short");
        }
        try {
            return rightPad(seed, '!').getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            //weird, hardcoded UTF-8
            throw new RuntimeException(e);
        }
    }
}
