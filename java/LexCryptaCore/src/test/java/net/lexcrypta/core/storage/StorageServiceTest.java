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
import net.lexcrypta.core.crypto.CryptoHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Víctor Suárez <victorjss@gmail.com>
 */
public class StorageServiceTest {

    public StorageServiceTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRightPad() {
        StorageService storage = new StorageService();
        
        assertEquals("1234567890000000", storage.rightPad("123456789", '0'));
        assertEquals("123456789zzzzzzz", storage.rightPad("123456789", 'z'));
        assertEquals("123456789aaaaaaa", storage.rightPad("123456789aaaaaaabbbbbbb", 'z'));
    }
    
    @Test
    public void testGetIv() throws Exception {
        String seed1 = "12345678";
        String seed2 = "12345678901234567890";
        
        StorageService service = new StorageService();
        
        assertArrayEquals("12345678!!!!!!!!".getBytes("utf-8"), service.getIv(seed1));
        assertArrayEquals("1234567890123456".getBytes("utf-8"), service.getIv(seed2));

        try {
            service.getIv(null);
            fail("NullPointerExceptin expected");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
        try {
            service.getIv("");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
        try {
            service.getIv("123");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testDoEncryptContent() throws Exception {
        ByteArrayInputStream noTestedBais = new ByteArrayInputStream(new byte[512]);
        File noTestedTempFile = File.createTempFile("dummy", ".aes");

        StorageService service = new StorageService();
        
        String seed = "123456";
        byte[] key = new CryptoHelper().getNewKey();
        byte[] iv =  service.getIv(seed);
        byte[] id = service.encryptString(seed, iv, key);
        byte[] encryptedPath = service.encryptString(noTestedTempFile.getPath(), iv, key);
        
        EncryptedData ed = service.doEncryptContent(noTestedBais, noTestedTempFile,
                seed, key);
        
        assertArrayEquals(key, ed.getKey());
        assertArrayEquals(id, ed.getId());
        assertArrayEquals(encryptedPath, ed.getEncryptedPath());
        
        
    }
}
