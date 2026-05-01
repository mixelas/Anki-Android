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
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext

/** Provides SSL/TLS support for AnkiServer HTTPS connections (Issue #15991) */
object SslUtil {
    private const val KEYSTORE_FILENAME = "anki_keystore.bks"
    private const val KEYSTORE_ALIAS = "localhost"
    private const val KEY_SIZE = 2048

    /**
     * Password for local development HTTPS keystore.
     * This is only used for local localhost connections and is not exposed to the network.
     * For production use, consider using Android KeyStore API.
     */
    private const val KEYSTORE_PASSWORD = "localhost"
    private const val KEY_PASSWORD = "localhost"

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

        // Generate a self-signed certificate for localhost
        // For production use, consider using Android KeyStore API or pre-generated certificates
        try {
            val keyPair = generateKeyPair()
            // TODO: Generate and sign X.509 certificate
            // Temporary approach: Store raw key pair until certificate generation is implemented
            // This allows HTTPS connections but without proper certificate validation
            keyStore.setKeyEntry(
                KEYSTORE_ALIAS,
                keyPair.private,
                KEY_PASSWORD.toCharArray(),
                arrayOfNulls(0), // Empty certificate chain for now
            )
        } catch (e: Exception) {
            Timber.w(e, "Failed to generate key pair for keystore")
        }

        FileOutputStream(file).use { fos ->
            keyStore.store(fos, KEYSTORE_PASSWORD.toCharArray())
        }

        return keyStore
    }

    private fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(KEY_SIZE)
        return keyGen.generateKeyPair()
    }
}
