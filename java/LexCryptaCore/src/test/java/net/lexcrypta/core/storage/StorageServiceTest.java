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
        
        CryptoHelper cryptoHelper = new CryptoHelper();
        String seed = "123456";
        byte[] key = new CryptoHelper().getNewKey();
        byte[] iv =  service.getIv(seed);
        byte[] ivEncrypted = cryptoHelper.encryptIv(iv, key);
        /**
         * with a content of x bytes, cipher configured with pkcss5 padding will 
         * creeate a encrypted content of x + 16 - x%16 bytes length (AES has a
         * fixed 16-bytes block. This new key cannot be decrypted because we 
         * have removed padding info.
         */
        byte[] newKey = Arrays.copyOf(ivEncrypted, CryptoHelper.KEY_LENGTH / 8); //new key of right lenght
        byte[] id = service.encryptString(seed, iv, newKey);
        byte [] encryptedFileName = service.encryptString(noTestedTempFile.getName(), iv, newKey);
        
        EncryptedData ed = service.doEncryptContent(noTestedBais, noTestedTempFile.getName(),
                seed, key);
        
        //key is not stored (anywhere)
        assertArrayEquals(key, ed.getKey());

        //id is encrypted seed with the new key obtained from IV (fixed seed) ciphered with original key, and is stored in database along encrypted path and file name
        assertArrayEquals(id, ed.getId());
        
        //encrypted path = encrypted(<storage.basePath> + "/" + <yyyyMMdd> + "/" + <undetermined>), where "encrypted" uses the new key for doing encryption
        byte[] encryptedPath = ed.getEncryptedPath();
        //target path = <storage.basePath> + "/" + <yyyyMMdd>
        String suffix = StorageService.sdf.format(new Date());
        String targetPath = noTestedTempFile.getParent() + File.separator + suffix;
        String path = service.decryptString(encryptedPath, iv, newKey);
        assertTrue(path.startsWith(targetPath + File.separator));
        assertTrue(Files.exists(FileSystems.getDefault().getPath(path)));     
        
        //file name is encrypted with the new key
        assertArrayEquals(encryptedFileName, ed.getEncryptedName());
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
        String fileName = "filename.test";
        byte[] encryptedName = service.encryptString(fileName, iv, key);
        String b64EncryptedPath = Base64.getEncoder().encodeToString(encryptedPath);
        String b64EncryptedName = Base64.getEncoder().encodeToString(encryptedName);
        
        Class.forName("org.hsqldb.jdbcDriver");
        service.coreHelper.setTestConnectionString("jdbc:hsqldb:mem:testdb;shutdown=true");
        Connection c = service.coreHelper.getConnection();
        PreparedStatement ps = c.prepareStatement("CREATE TABLE lexcrypta (id VARCHAR(512), filepath VARCHAR(2048), filename VARCHAR(512))");
        ps.executeUpdate();

        EncryptedData ed = new EncryptedData();
        ed.setId(encryptedSeed);
        ed.setKey(key);
        ed.setEncryptedPath(encryptedPath);
        ed.setEncryptedName(encryptedName);
        
        service.createDatabaseRecord(ed);
        
        //test database result
        ps = c.prepareStatement("SELECT id, filepath, filename FROM lexcrypta");
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(b64EncryptedSeed, rs.getString(1));
        assertEquals(b64EncryptedPath, rs.getString(2));
        assertEquals(b64EncryptedName, rs.getString(3));
        assertFalse(rs.next());
        
        //close connection and database
        c.close();
    }
    
    @Test
    public void testGetPathAndNameFromDatabase() throws Exception {
        byte[] iv = "12345678Z0000000".getBytes("utf-8"); //128 bits = 16 bytes
        byte[] key = Base64.getDecoder().decode("mVMtHSqtTHF3JBaXoaA+/Q==");
        
        StorageService service = new StorageService();
        
        String seed = "abcdefg";
        byte[] encryptedSeed = service.encryptString(seed, iv, key);
        String b64EncryptedSeed = Base64.getEncoder().encodeToString(encryptedSeed);
        
        String path = "/lexcrypta/java/iutest/getPathFromDatabase";
        byte[] encryptedPath = service.encryptString(path, iv, key);
        String b64EncryptedPath = Base64.getEncoder().encodeToString(encryptedPath);
        
        String fileName = "filename.test";
        byte[] encryptedName = service.encryptString(fileName, iv, key);
        String b64EncryptedName = Base64.getEncoder().encodeToString(encryptedName);
        
        Class.forName("org.hsqldb.jdbcDriver");
        service.coreHelper.setTestConnectionString("jdbc:hsqldb:mem:testdb;shutdown=true");
        Connection c = service.coreHelper.getConnection();
        PreparedStatement ps = c.prepareStatement("CREATE TABLE lexcrypta (id VARCHAR(512), filepath VARCHAR(2048), filename VARCHAR(512))");
        ps.executeUpdate();
        ps.close();
        
        ps = c.prepareStatement("INSERT INTO lexcrypta (id, filepath, filename) VALUES (?, ?, ?)");
        ps.setString(1, b64EncryptedSeed); //id
        ps.setString(2, b64EncryptedPath); //path
        ps.setString(3, b64EncryptedName); //path
        ps.executeUpdate();
        ps.close();

        assertArrayEquals(new String[] {path, fileName}, service.getPathAndNameFromDatabase(encryptedSeed, iv, key));
        
        //close connection and database
        c.close();
    }

    @Test
    public void testGetPathAndNameFromDatabaseWhenNullReturned() throws Exception {
        byte[] iv = "12345678Z0000000".getBytes("utf-8"); //128 bits = 16 bytes
        byte[] key = Base64.getDecoder().decode("mVMtHSqtTHF3JBaXoaA+/Q==");
        
        StorageService service = new StorageService();
        
        Class.forName("org.hsqldb.jdbcDriver");
        service.coreHelper.setTestConnectionString("jdbc:hsqldb:mem:testdb;shutdown=true");
        Connection c = service.coreHelper.getConnection();
        PreparedStatement ps = c.prepareStatement("CREATE TABLE lexcrypta (id VARCHAR(512), filepath VARCHAR(2048), filename VARCHAR(512))");
        ps.executeUpdate();
        ps.close();
        
        assertNull(service.getPathAndNameFromDatabase("dummyname".getBytes(), iv, key));
        
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
        byte[] encryptedName = service.encryptString(tmpFile.getName(), iv, key);
        String b64EncryptedName = Base64.getEncoder().encodeToString(encryptedName);
        
        FileOutputStream fos = new FileOutputStream(tmpFile);
        IOUtils.copy(new ByteArrayInputStream(encryptedText), fos);        
        
        Class.forName("org.hsqldb.jdbcDriver");
        service.coreHelper.setTestConnectionString("jdbc:hsqldb:mem:testdb;shutdown=true");
        Connection c = service.coreHelper.getConnection();
        PreparedStatement ps = c.prepareStatement("CREATE TABLE lexcrypta (id VARCHAR(512), filepath VARCHAR(2048), filename VARCHAR(512))");
        ps.executeUpdate();
        ps.close();
        
        ps = c.prepareStatement("INSERT INTO lexcrypta (id, filepath, filename) VALUES (?, ?, ?)");
        ps.setString(1, b64EncryptedSeed); //id
        ps.setString(2, b64EncryptedPath); //path
        ps.setString(3, b64EncryptedName); //name
        ps.executeUpdate();
        ps.close();

        DecryptedData dd = service.getContentFromFileSystem(encryptedSeed, iv, key);
        assertArrayEquals(plainText.getBytes("utf-8"), IOUtils.toByteArray(dd.getContent()));
        assertEquals(tmpFile.getName(), dd.getFilaName());
        
        //close connection and database
        c.close();
    }
    
    @Test
    public void testGetContentFromFilesystemWithInvalidParameters() throws Exception {
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
        byte[] encryptedName = service.encryptString(tmpFile.getName(), iv, key);
        String b64EncryptedName = Base64.getEncoder().encodeToString(encryptedName);
        
        FileOutputStream fos = new FileOutputStream(tmpFile);
        IOUtils.copy(new ByteArrayInputStream(encryptedText), fos);        
        
        Class.forName("org.hsqldb.jdbcDriver");
        service.coreHelper.setTestConnectionString("jdbc:hsqldb:mem:testdb;shutdown=true");
        Connection c = service.coreHelper.getConnection();
        PreparedStatement ps = c.prepareStatement("CREATE TABLE lexcrypta (id VARCHAR(512), filepath VARCHAR(2048), filename VARCHAR(512))");
        ps.executeUpdate();
        ps.close();
        
        ps = c.prepareStatement("INSERT INTO lexcrypta (id, filepath, filename) VALUES (?, ?, ?)");
        ps.setString(1, b64EncryptedSeed); //id
        ps.setString(2, b64EncryptedPath); //path
        ps.setString(3, b64EncryptedName); //name
        ps.executeUpdate();
        ps.close();

        try {
            assertNull(service.getContentFromFileSystem("invalid seed".getBytes(), iv, key));
        } catch (Exception unexpected) {
            assertFalse("Unexpected exception: " + unexpected.getMessage(), true);
        }
        
        //close connection and database
        c.close();
    }
    
    @Test
    public void testDecryptContent() throws Exception {
        String s = "this encrypted data (or not)!!!";
        String s2 = "5Z46ZvFd6u7w1AAelahOQuladSXEcY0RVfEaF0lFsYs=";
        String seed = "1234567890";
        byte[] iv = "1234567890123456".getBytes("utf-8");
        String skey = "BMoGsF9v6hoCv3R+lwR+2g==";
        byte[] key = Base64.getDecoder().decode(skey);
        String snewKey = "KraUKTyQWS9U/oDqupahCQ==";
        byte[] newKey = Base64.getDecoder().decode(snewKey);

        StorageService service = new StorageService();

        File tmpFile = File.createTempFile("test", ".aes");
        tmpFile.deleteOnExit();
        String path = tmpFile.getPath();
        byte[] encryptedPath = service.encryptString(path, iv, newKey);
        String b64EncryptedPath = Base64.getEncoder().encodeToString(encryptedPath);
        byte[] encryptedName = service.encryptString(tmpFile.getName(), iv, newKey);
        String b64EncryptedName = Base64.getEncoder().encodeToString(encryptedName);
        byte[] id = service.encryptString(seed, iv, newKey);
        String b64Id = Base64.getEncoder().encodeToString(id);

        FileOutputStream fos = new FileOutputStream(tmpFile);
        IOUtils.copy(new ByteArrayInputStream(Base64.getDecoder().decode(s2)), fos);

        Class.forName("org.hsqldb.jdbcDriver");
        service.coreHelper.setTestConnectionString(
                "jdbc:hsqldb:mem:testdb;shutdown=true");
        Connection c = service.coreHelper.getConnection();
        PreparedStatement ps = c.prepareStatement(
                "CREATE TABLE lexcrypta (id VARCHAR(512), filepath VARCHAR(2048), filename VARCHAR(512))");
        ps.executeUpdate();
        ps.close();

        ps = c.prepareStatement(
                "INSERT INTO lexcrypta (id, filepath, filename) VALUES (?, ?, ?)");
        ps.setString(1, b64Id); //id
        ps.setString(2, b64EncryptedPath); //path
        ps.setString(3, b64EncryptedName); //name
        ps.executeUpdate();
        ps.close();
        
        DecryptedData dd = service.decryptContent(seed, key);
        byte[] b = IOUtils.toByteArray(dd.getContent());
        assertEquals(s, new String(b, "utf-8"));
        assertEquals(tmpFile.getName(), dd.getFilaName());
        
        //close connection and database
        c.close();
    }
}
