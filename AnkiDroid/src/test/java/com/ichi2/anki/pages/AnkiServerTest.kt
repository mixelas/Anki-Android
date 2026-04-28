/*
 *  Copyright (c) 2024 AnkiDroid Contributors
 *
 *  This program is free software; you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software
 *  Foundation; either version 3 of the License, or (at your option) any later
 *  version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ichi2.anki.pages

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class AnkiServerTest {
    @Mock
    private lateinit var mockPostHandler: PostRequestHandler

    @Test
    fun testBaseUrlReturnsHttpProtocol() {
        val server = AnkiServer(mockPostHandler, port = 0)
        assertTrue(server.baseUrl().startsWith("http://"), "Should return HTTP URL")
    }

    @Test
    fun testBaseUrlContainsLocalhost() {
        val server = AnkiServer(mockPostHandler, port = 0)
        assertTrue(server.baseUrl().contains("127.0.0.1"), "Should contain localhost address")
    }

    @Test
    fun testAnkiServerAcceptsContextParameter() {
        // Verify that AnkiServer accepts optional context parameter for future HTTPS support
        val server1 = AnkiServer(mockPostHandler, port = 0)
        val server2 = AnkiServer(mockPostHandler, null, port = 0)
        assertTrue(server1.baseUrl().startsWith("http://"))
        assertTrue(server2.baseUrl().startsWith("http://"))
    }
}
