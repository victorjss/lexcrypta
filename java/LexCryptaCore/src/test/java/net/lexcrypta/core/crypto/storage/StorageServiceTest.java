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
package net.lexcrypta.core.crypto.storage;

import net.lexcrypta.core.storage.StorageService;
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
}
