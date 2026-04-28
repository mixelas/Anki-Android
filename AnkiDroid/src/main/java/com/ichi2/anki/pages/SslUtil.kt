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
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext

/** Provides SSL/TLS support for AnkiServer HTTPS connections (Issue #15991) */
object SslUtil {
    private const val KEYSTORE_FILENAME = "anki_keystore.bks"
    private const val KEYSTORE_PASSWORD = "ankidroid"
    private const val KEY_PASSWORD = "ankidroid"

    /**
     * Get or create an SSLContext for HTTPS on localhost.
     * Caches the keystore in the app's cache directory after first generation.
     */
    fun getSSLContext(context: Context): SSLContext {
        val keystoreFile = File(context.cacheDir, KEYSTORE_FILENAME)

        val keyStore =
            if (keystoreFile.exists()) {
                loadKeystore(keystoreFile)
            } else {
                generateKeystore(keystoreFile)
            }

        val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(keyStore, KEY_PASSWORD.toCharArray())

        return SSLContext.getInstance("TLSv1.2").apply {
            init(kmf.keyManagers, null, java.security.SecureRandom())
        }
    }

    private fun loadKeystore(file: File): KeyStore {
        val keyStore = KeyStore.getInstance("BKS")
        FileInputStream(file).use { fis ->
            keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray())
        }
        return keyStore
    }

    private fun generateKeystore(file: File): KeyStore {
        val keyStore = KeyStore.getInstance("BKS")
        keyStore.load(null, null)

        // TODO: Generate self-signed X.509 certificate
        // Currently creates empty keystore. Future implementations should use:
        // - Android KeyStore API (preferred), or
        // - BouncyCastle library for broader compatibility

        FileOutputStream(file).use { fos ->
            keyStore.store(fos, KEYSTORE_PASSWORD.toCharArray())
        }

        return keyStore
    }
}
