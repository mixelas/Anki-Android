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
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class AnkiServerTest {
    @Mock
    private lateinit var mockPostHandler: PostRequestHandler

    @Test
    fun testBaseUrlReturnsHttpWhenContextNotProvided() {
        val server = AnkiServer(mockPostHandler, port = 0)
        assertTrue(server.baseUrl().startsWith("http://"), "Should return HTTP URL when no context provided")
    }

    @Test
    fun testBaseUrlContainsLocalhost() {
        val server = AnkiServer(mockPostHandler, port = 0)
        assertTrue(server.baseUrl().contains("127.0.0.1"), "Should contain localhost address")
    }
}

@RunWith(RobolectricTestRunner::class)
class AnkiServerHttpsTest {
    @Mock
    private lateinit var mockPostHandler: PostRequestHandler

    @Test
    fun testBaseUrlReturnsHttpsWhenContextProvided() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val server = AnkiServer(mockPostHandler, context, port = 0)
        assertTrue(server.baseUrl().startsWith("https://"), "Should return HTTPS URL when context is provided")
    }
}
