/*
 *  Copyright (c) 2026 Georgios Michelakis <michelakisgio@gmail.com>
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

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.GeneralName
import org.bouncycastle.asn1.x509.GeneralNames
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.security.Security
import java.util.Date
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext

/** Manages SSL/TLS support for HTTPS connections in AnkiServer */
object SslUtil {
    private const val KEYSTORE_FILENAME = "anki_keystore.p12"
    private const val KEYSTORE_ALIAS = "localhost"
    private const val KEY_SIZE = 2048

    /**
     * Password for the keystore. Used for both loading and storing the PKCS12 keystore.
     * This is an app-local credential and not transmitted over the network.
     */
    private const val KEYSTORE_PASSWORD = "localhost"
    private const val KEY_PASSWORD = "localhost"

    /**
     * Get or create an SSLContext for localhost HTTPS.
     * The keystore is cached in the app cache directory after first generation.
     */
    fun getSSLContext(cacheDir: File): SSLContext {
        val keystoreFile = File(cacheDir, KEYSTORE_FILENAME)

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
        val keyStore = KeyStore.getInstance("PKCS12")
        FileInputStream(file).use { fis ->
            keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray())
        }
        return keyStore
    }

    @Suppress("DirectDateInstantiation", "DirectSystemCurrentTimeMillisUsage")
    private fun generateKeystore(file: File): KeyStore {
        // Ensure BouncyCastle provider is available for certificate generation
        try {
            Security.addProvider(BouncyCastleProvider())
        } catch (_: Exception) {
            // provider may already be present
        }

        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(null, null)

        try {
            // CA key pair
            val caKeyPair = generateKeyPair()
            val now = Date()
            val caNotBefore = Date(now.time - 1000L * 60)
            val caNotAfter = Date(now.time + 3650L * 24 * 60 * 60 * 1000) // 10 years

            val caName = X500Name("CN=Anki Local CA")
            val caSerial = BigInteger.valueOf(System.currentTimeMillis())

            val caBuilder =
                JcaX509v3CertificateBuilder(
                    caName,
                    caSerial,
                    caNotBefore,
                    caNotAfter,
                    caName,
                    caKeyPair.public,
                )

            caBuilder.addExtension(
                Extension.basicConstraints,
                true,
                BasicConstraints(true),
            )

            val caSigner = JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(caKeyPair.private)
            val caHolder = caBuilder.build(caSigner)
            val caCert = JcaX509CertificateConverter().setProvider("BC").getCertificate(caHolder)

            // Leaf key pair
            val leafKeyPair = generateKeyPair()
            val leafName = X500Name("CN=localhost")
            val leafSerial = BigInteger.valueOf(System.currentTimeMillis() + 1)
            val leafNotBefore = caNotBefore
            val leafNotAfter = Date(now.time + 365L * 24 * 60 * 60 * 1000) // 1 year

            val sanNames =
                GeneralNames(
                    arrayOf(
                        GeneralName(GeneralName.dNSName, "localhost"),
                        GeneralName(GeneralName.iPAddress, "127.0.0.1"),
                    ),
                )

            val leafBuilder =
                JcaX509v3CertificateBuilder(
                    caName,
                    leafSerial,
                    leafNotBefore,
                    leafNotAfter,
                    leafName,
                    leafKeyPair.public,
                )

            leafBuilder.addExtension(Extension.basicConstraints, false, BasicConstraints(false))
            leafBuilder.addExtension(Extension.subjectAlternativeName, false, sanNames)
            leafBuilder.addExtension(Extension.keyUsage, true, KeyUsage(KeyUsage.digitalSignature or KeyUsage.keyEncipherment))

            val leafSigner = JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(caKeyPair.private)
            val leafHolder = leafBuilder.build(leafSigner)
            val leafCert = JcaX509CertificateConverter().setProvider("BC").getCertificate(leafHolder)

            // Store leaf private key and certificate chain (leaf, ca)
            keyStore.setKeyEntry(
                KEYSTORE_ALIAS,
                leafKeyPair.private,
                KEY_PASSWORD.toCharArray(),
                arrayOf(leafCert, caCert),
            )
        } catch (e: Exception) {
            Timber.w(e, "Failed to generate certificate chain for keystore")
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
