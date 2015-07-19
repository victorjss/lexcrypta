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
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;
import net.lexcrypta.core.conf.CoreHelper;
import net.lexcrypta.core.crypto.CryptoHelper;
import org.apache.commons.io.IOUtils;
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
        CoreHelper.setCoreProps(null);
        CoreHelper.setQueriesProps(null);
    }

    @Test
    public void testRightPad() {
        StorageService storage = new StorageService();
        
        assertEquals("1234567890000000", storage.rightPad("123456789", '0'));
        assertEquals("123456789zzzzzzz", storage.rightPad("123456789", 'z'));
        assertEquals("123456789aaaaaaa", storage.rightPad("123456789aaaaaaabbbbbbb", 'z'));
    }
    
    @Test
    public void testEncryptString() throws Exception {
        String plainText = "This is a encryption test!!!";
        byte[] encryptedText = Base64.getDecoder().decode("zy2FE8sd/4WK3mMBVyay0GNYFa7CwZAkKWsqDRFf7og=");
        byte[] key = Base64.getDecoder().decode("mVMtHSqtTHF3JBaXoaA+/Q==");
        byte[] iv = "12345678Z0000000".getBytes("utf-8"); //128 bits = 16 bytes
        
        assertArrayEquals(encryptedText, new StorageService().encryptString(plainText, iv, key));

    }
    
    @Test
    public void testDecryptString() throws Exception {
        String plainText = "This is a encryption test!!!";
        byte[] encryptedText = Base64.getDecoder().decode("zy2FE8sd/4WK3mMBVyay0GNYFa7CwZAkKWsqDRFf7og=");
        byte[] key = Base64.getDecoder().decode("mVMtHSqtTHF3JBaXoaA+/Q==");
        byte[] iv = "12345678Z0000000".getBytes("utf-8"); //128 bits = 16 bytes
        
        assertEquals(plainText, new StorageService().decryptString(encryptedText, iv, key));

    }
    
    @Test
    public void testGetIv() throws Exception {
        String seed1 = "12345678";
        String seed2 = "12345678901234567890";
        String seed3 = "áéíóúäëïöüàèìòù¢€ç[]"; //more than 16 bytes in utf-8 encoding
        
        StorageService service = new StorageService();
        
        assertArrayEquals("12345678!!!!!!!!".getBytes("utf-8"), service.getIv(seed1));
        assertArrayEquals("1234567890123456".getBytes("utf-8"), service.getIv(seed2));
        byte[] ivSeed3 = service.getIv(seed3);
        assertArrayEquals(Arrays.copyOf(seed3.getBytes("utf-8"), 16), ivSeed3);
        
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
    public void testGetTargetDirPath() {
        StorageService service = new StorageService();
        Properties props = new Properties();
        props.setProperty("storage.basePath", "/lexcrypta/java/iutest/getTargetDirPath");
        CoreHelper.setCoreProps(props);

        String suffix = service.sdf.format(new Date());
        assertEquals("/lexcrypta/java/iutest/getTargetDirPath" + File.separator + suffix, service.getTargetDirPath());
    }
    
    @Test
    public void testDoEncryptContent() throws Exception {
        ByteArrayInputStream noTestedBais = new ByteArrayInputStream(new byte[512]);
        File noTestedTempFile = File.createTempFile("dummy", ".aes");
        noTestedTempFile.deleteOnExit();

        StorageService service = new StorageService();
        Properties props = new Properties();
        props.setProperty("storage.basePath", noTestedTempFile.getParent());
        CoreHelper.setCoreProps(props);
        
        String seed = "123456";
        byte[] key = new CryptoHelper().getNewKey();
        byte[] iv =  service.getIv(seed);
        byte[] id = service.encryptString(seed, iv, key);
        
        EncryptedData ed = service.doEncryptContent(noTestedBais, 
                seed, key);
        
        //key is not stored (anywhere)
        assertArrayEquals(key, ed.getKey());
        //id is encrypted seed and is stored in database besides encrypted path
        assertArrayEquals(id, ed.getId());
        //encrypted path = encrypted(<storage.basePath> + "/" + <yyyyMMdd> + "/" + <undetermined>)
        byte[] encryptedPath = ed.getEncryptedPath();
        //target path = <storage.basePath> + "/" + <yyyyMMdd>
        String suffix = service.sdf.format(new Date());
        String targetPath = noTestedTempFile.getParent() + File.separator + suffix;
        String path = service.decryptString(encryptedPath, iv, key);
        assertTrue(path.startsWith(targetPath + File.separator));
        assertTrue(Files.exists(FileSystems.getDefault().getPath(path)));     
    }
    
    @Test
    public void testCreateDatabaseRecord() throws Exception {
        byte[] iv = "12345678Z0000000".getBytes("utf-8"); //128 bits = 16 bytes
        byte[] key = Base64.getDecoder().decode("mVMtHSqtTHF3JBaXoaA+/Q==");
        
        StorageService service = new StorageService();
        
        String seed = "abcdefg";
        byte[] encryptedSeed = service.encryptString(seed, iv, key);
        String b64EncryptedSeed = Base64.getEncoder().encodeToString(encryptedSeed);
        
        String path = "/lexcrypta/java/iutest/createDatabaseRecord";
        byte[] encryptedPath = service.encryptString(path, iv, key);
        String b64EncryptedPath = Base64.getEncoder().encodeToString(encryptedPath);
        
        Class.forName("org.hsqldb.jdbcDriver");
        service.coreHelper.setTestConnectionString("jdbc:hsqldb:mem:testdb;shutdown=true");
        Connection c = service.coreHelper.getConnection();
        PreparedStatement ps = c.prepareStatement("CREATE TABLE lexcrypta (id VARCHAR(512), filepath VARCHAR(2048), creation DATE)");
        ps.executeUpdate();

        EncryptedData ed = new EncryptedData();
        ed.setId(encryptedSeed);
        ed.setKey(key);
        ed.setEncryptedPath(encryptedPath);
        
        service.createDatabaseRecord(ed);
        
        //test database result
        ps = c.prepareStatement("SELECT id, filepath, creation FROM lexcrypta");
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(b64EncryptedSeed, rs.getString(1));
        assertEquals(b64EncryptedPath, rs.getString(2));
        assertNotNull(rs.getDate(3));
        assertTrue(rs.getDate(3).getTime() <= System.currentTimeMillis());
        assertFalse(rs.next());
        
        //close connection and database
        c.close();
    }
    
    @Test
    public void testGetPathFromDatabase() throws Exception {
        byte[] iv = "12345678Z0000000".getBytes("utf-8"); //128 bits = 16 bytes
        byte[] key = Base64.getDecoder().decode("mVMtHSqtTHF3JBaXoaA+/Q==");
        
        StorageService service = new StorageService();
        
        String seed = "abcdefg";
        byte[] encryptedSeed = service.encryptString(seed, iv, key);
        String b64EncryptedSeed = Base64.getEncoder().encodeToString(encryptedSeed);
        
        String path = "/lexcrypta/java/iutest/getPathFromDatabase";
        byte[] encryptedPath = service.encryptString(path, iv, key);
        String b64EncryptedPath = Base64.getEncoder().encodeToString(encryptedPath);
        
        Class.forName("org.hsqldb.jdbcDriver");
        service.coreHelper.setTestConnectionString("jdbc:hsqldb:mem:testdb;shutdown=true");
        Connection c = service.coreHelper.getConnection();
        PreparedStatement ps = c.prepareStatement("CREATE TABLE lexcrypta (id VARCHAR(512), filepath VARCHAR(2048), creation DATE)");
        ps.executeUpdate();
        ps.close();
        
        ps = c.prepareStatement("INSERT INTO lexcrypta (id, filepath) VALUES (?, ?)");
        ps.setString(1, b64EncryptedSeed); //id
        ps.setString(2, b64EncryptedPath); //path
        ps.executeUpdate();
        ps.close();

        assertEquals(path, service.getPathFromDatabase(encryptedSeed, iv, key));
        
        //close connection and database
        c.close();
    }

    @Test
    public void testGetContentFromFilesystem() throws Exception {
        byte[] iv = "12345678Z0000000".getBytes("utf-8"); //128 bits = 16 bytes
        byte[] key = Base64.getDecoder().decode("mVMtHSqtTHF3JBaXoaA+/Q==");
        String plainText = "This is a encryption test!!!";
        byte[] encryptedText = Base64.getDecoder().decode("zy2FE8sd/4WK3mMBVyay0GNYFa7CwZAkKWsqDRFf7og=");

        StorageService service = new StorageService();
        
        String seed = "abcdefg";
        byte[] encryptedSeed = service.encryptString(seed, iv, key);
        String b64EncryptedSeed = Base64.getEncoder().encodeToString(encryptedSeed);
        
        File tmpFile = File.createTempFile("test", ".aes");
        tmpFile.deleteOnExit();
        String path = tmpFile.getPath();
        byte[] encryptedPath = service.encryptString(path, iv, key);
        String b64EncryptedPath = Base64.getEncoder().encodeToString(encryptedPath);
        
        FileOutputStream fos = new FileOutputStream(tmpFile);
        IOUtils.copy(new ByteArrayInputStream(encryptedText), fos);        
        
        Class.forName("org.hsqldb.jdbcDriver");
        service.coreHelper.setTestConnectionString("jdbc:hsqldb:mem:testdb;shutdown=true");
        Connection c = service.coreHelper.getConnection();
        PreparedStatement ps = c.prepareStatement("CREATE TABLE lexcrypta (id VARCHAR(512), filepath VARCHAR(2048), creation DATE)");
        ps.executeUpdate();
        ps.close();
        
        ps = c.prepareStatement("INSERT INTO lexcrypta (id, filepath) VALUES (?, ?)");
        ps.setString(1, b64EncryptedSeed); //id
        ps.setString(2, b64EncryptedPath); //path
        ps.executeUpdate();
        ps.close();

        InputStream decryptedContent = service.getContentFromFileSystem(encryptedSeed, iv, key);
        assertArrayEquals(plainText.getBytes("utf-8"), IOUtils.toByteArray(decryptedContent));
        
        //close connection and database
        c.close();
    }
}
