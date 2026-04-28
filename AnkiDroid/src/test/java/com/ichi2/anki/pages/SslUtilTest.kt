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
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class SslUtilTest {
    @Test
    fun testCreateSSLContextReturnsNonNull() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sslContext = SslUtil.createSSLContext(context)
        assertNotNull(sslContext, "SSL context should be created successfully")
    }

    @Test
    fun testSSLContextCanBeUsedMultipleTimes() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sslContext1 = SslUtil.createSSLContext(context)
        val sslContext2 = SslUtil.createSSLContext(context)
        assertNotNull(sslContext1)
        assertNotNull(sslContext2, "SSL context should be reused from cached keystore")
    }
}
