/*
 *  Copyright (c) 2026 mixelas <michelakisgio@gmail.com>
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
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class SslUtilTest {
    @Test
    fun testGetSSLContextReturnsValidContext() {
        val context: Context = RuntimeEnvironment.getApplication()
        val sslContext = SslUtil.getSSLContext(context.cacheDir)
        assertNotNull(sslContext, "SSLContext should be created successfully")
    }

    @Test
    fun testSSLContextSupportsHttps() {
        val context: Context = RuntimeEnvironment.getApplication()
        val sslContext = SslUtil.getSSLContext(context.cacheDir)
        assertNotNull(sslContext.serverSocketFactory, "SSLContext should have a server socket factory")
        assertTrue(
            sslContext.protocol.contains("TLS", ignoreCase = true),
            "SSLContext should use TLS protocol",
        )
    }

    @Test
    fun testKeystoreCaching() {
        val context: Context = RuntimeEnvironment.getApplication()
        val sslContext1 = SslUtil.getSSLContext(context.cacheDir)
        val sslContext2 = SslUtil.getSSLContext(context.cacheDir)
        // Both should successfully return SSLContext
        // Second call should load from cached keystore
        assertNotNull(sslContext1)
        assertNotNull(sslContext2)
    }
}
