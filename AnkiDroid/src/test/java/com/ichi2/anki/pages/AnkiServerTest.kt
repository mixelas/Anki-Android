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

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class AnkiServerTest {
    @Mock
    private lateinit var mockPostHandler: PostRequestHandler

    @Before
    fun setUp() {
        // Mockito initializes @Mock fields when using MockitoJUnitRunner
    }

    @Test
    fun testBaseUrlHttpWhenNoContext() {
        // Without context, server should use HTTP
        val server = AnkiServer(mockPostHandler, port = 0)
        assertTrue(server.baseUrl().startsWith("http://"), "Should return HTTP URL when no context provided")
        assertTrue(!server.baseUrl().startsWith("https://"), "Should not be HTTPS when context is null")
    }

    @Test
    fun testBaseUrlHttpsWhenContextProvided() {
        // With context, server should use HTTPS
        val context: Context = RuntimeEnvironment.getApplication()
        val server = AnkiServer(mockPostHandler, context, port = 0)
        assertTrue(server.baseUrl().startsWith("https://"), "Should return HTTPS URL when context is provided")
    }

    @Test
    fun testBaseUrlContainsLocalhost() {
        val server = AnkiServer(mockPostHandler, port = 0)
        assertTrue(server.baseUrl().contains("127.0.0.1"), "Should contain localhost address")
    }

    @Test
    fun testHttpsServerWithPort() {
        val context: Context = RuntimeEnvironment.getApplication()
        val server = AnkiServer(mockPostHandler, context, port = 0)
        val baseUrl = server.baseUrl()
        assertTrue(baseUrl.startsWith("https://127.0.0.1:"), "Should construct valid HTTPS URL with port")
        assertTrue(baseUrl.endsWith("/"), "Base URL should end with /")
    }
}
