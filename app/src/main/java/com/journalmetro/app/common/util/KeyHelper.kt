package com.journalmetro.app.common.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class KeyHelper(ctx: Context, var prefs: SharedPreferences) {
        private var keyStore: KeyStore? = null

            @Throws(
            NoSuchProviderException::class,
            NoSuchAlgorithmException::class,
            InvalidAlgorithmParameterException::class,
            KeyStoreException::class,
            CertificateException::class,
            IOException::class
        )
        private fun generateEncryptKey(ctx: Context) {
            keyStore = KeyStore.getInstance(AndroidKeyStore)
                if (keyStore != null) {
                    keyStore!!.load(null)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!keyStore!!.containsAlias(KEY_ALIAS)) {
                            val keyGenerator: KeyGenerator =
                                KeyGenerator.getInstance(
                                    KeyProperties.KEY_ALGORITHM_AES,
                                    AndroidKeyStore
                                )
                            keyGenerator.init(
                                KeyGenParameterSpec.Builder(
                                    KEY_ALIAS,
                                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                                )
                                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                    .setRandomizedEncryptionRequired(false)
                                    .build()
                            )
                            keyGenerator.generateKey()
                        }
                    } else {
                        if (!keyStore!!.containsAlias(KEY_ALIAS)) {
                            // Generate a key pair for encryption
                            val start: Calendar = Calendar.getInstance()
                            val end: Calendar = Calendar.getInstance()
                            end.add(Calendar.YEAR, 30)
                            val spec = KeyPairGeneratorSpec.Builder(ctx)
                                .setAlias(KEY_ALIAS)
                                .setSubject(X500Principal("CN=$KEY_ALIAS"))
                                .setSerialNumber(BigInteger.TEN)
                                .setStartDate(start.time)
                                .setEndDate(end.time)
                                .build()
                            val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
                                "RSA",
                                AndroidKeyStore
                            )
                            kpg.initialize(spec)
                            kpg.generateKeyPair()
                        }
                    }
                }
        }

        @Throws(Exception::class)
        private fun rsaEncrypt(secret: ByteArray): ByteArray {
            val privateKeyEntry: KeyStore.PrivateKeyEntry =
                keyStore!!.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
            // Encrypt the text
            val inputCipher: Cipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
            inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)
            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(outputStream, inputCipher)
            cipherOutputStream.write(secret)
            cipherOutputStream.close()
            return outputStream.toByteArray()
        }

        @Throws(Exception::class)
        private fun rsaDecrypt(encrypted: ByteArray): ByteArray {
            val privateKeyEntry: KeyStore.PrivateKeyEntry =
                keyStore!!.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
            val output: Cipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)
            val cipherInputStream = CipherInputStream(
                ByteArrayInputStream(encrypted), output
            )
            val values: ArrayList<Byte> = ArrayList()
            var nextByte: Int?
            while (cipherInputStream.read().also { nextByte = it } != -1) {
                if (nextByte != null) {
                    values.add(nextByte!!.toByte())
                }
            }
            val bytes = ByteArray(values.size)
            for (i in bytes.indices) {
                bytes[i] = values[i]
            }
            return bytes
        }

        @Throws(Exception::class)
        private fun generateAESKey() {
            var enryptedKeyB64 = prefs.getString(ENCRYPTED_KEY, null)
            if (enryptedKeyB64 == null) {
                val key = ByteArray(16)
                val secureRandom = SecureRandom()
                secureRandom.nextBytes(key)
                val encryptedKey = rsaEncrypt(key)
                enryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT)
                val edit = prefs.edit()
                edit.putString(ENCRYPTED_KEY, enryptedKeyB64)
                edit.apply()
            }
        }

        @get:Throws(
            NoSuchProviderException::class,
            NoSuchAlgorithmException::class,
            InvalidAlgorithmParameterException::class,
            KeyStoreException::class,
            CertificateException::class,
            IOException::class,
            UnrecoverableKeyException::class
        )
        private val aESKeyFromKS: Key
            get() {
                keyStore = KeyStore.getInstance(AndroidKeyStore)
                keyStore!!.load(null)
                return keyStore!!.getKey(KEY_ALIAS, null) as SecretKey
            }

        @Throws(Exception::class)
        private fun getSecretKey(): Key {
            val enryptedKeyB64 = prefs.getString(ENCRYPTED_KEY, null)
            val encryptedKey: ByteArray = Base64.decode(enryptedKeyB64, Base64.DEFAULT)
            val key = rsaDecrypt(encryptedKey)
            return SecretKeySpec(key, "AES")
        }

        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            NoSuchProviderException::class,
            BadPaddingException::class,
            IllegalBlockSizeException::class,
            UnsupportedEncodingException::class
        )
        fun encrypt(input: String): String? {
            val c: Cipher
            val publicIV = prefs.getString(PUBLIC_IV, null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                c = Cipher.getInstance(AES_MODE_M)
                return try {
                    c.init(
                        Cipher.ENCRYPT_MODE,
                        aESKeyFromKS, GCMParameterSpec(128, Base64.decode(publicIV, Base64.DEFAULT))
                    )
                    val encodedBytes: ByteArray = c.doFinal(input.toByteArray(charset("UTF-8")))
                    Base64.encodeToString(encodedBytes, Base64.DEFAULT)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            } else {
                c = Cipher.getInstance(AES_MODE_M)
                return try {
                    c.init(
                        Cipher.ENCRYPT_MODE,
                        getSecretKey(),
                        GCMParameterSpec(128, Base64.decode(publicIV, Base64.DEFAULT))
                    )
                    val encodedBytes: ByteArray = c.doFinal(input.toByteArray(charset("UTF-8")))
                    Base64.encodeToString(encodedBytes, Base64.DEFAULT)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            NoSuchProviderException::class,
            BadPaddingException::class,
            IllegalBlockSizeException::class,
            UnsupportedEncodingException::class
        )
        fun decrypt(encrypted: String): String? {
            val c: Cipher
            val publicIV = prefs.getString(PUBLIC_IV, null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                c = Cipher.getInstance(AES_MODE_M)
                return try {
                    c.init(
                        Cipher.DECRYPT_MODE,
                        aESKeyFromKS, GCMParameterSpec(128, Base64.decode(publicIV, Base64.DEFAULT))
                    )
                    val decodedValue: ByteArray =
                        Base64.decode(encrypted.toByteArray(charset("UTF-8")), Base64.DEFAULT)
                    val decryptedVal: ByteArray = c.doFinal(decodedValue)
                    String(decryptedVal)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            } else {
                c = Cipher.getInstance(AES_MODE_M)
                return try {
                    c.init(
                        Cipher.DECRYPT_MODE,
                        getSecretKey(),
                        GCMParameterSpec(128, Base64.decode(publicIV, Base64.DEFAULT))
                    )
                    val decodedValue: ByteArray =
                        Base64.decode(encrypted.toByteArray(charset("UTF-8")), Base64.DEFAULT)
                    val decryptedVal: ByteArray = c.doFinal(decodedValue)
                    String(decryptedVal)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

        }

        private fun generateRandomIV() {
            val publicIV = prefs.getString(PUBLIC_IV, null)
            if (publicIV == null) {
                val random = SecureRandom()
                val generated: ByteArray = random.generateSeed(12)
                val generatedIVstr: String = Base64.encodeToString(generated, Base64.DEFAULT)
                val edit = prefs.edit()
                edit.putString(PUBLIC_IV, generatedIVstr)
                edit.apply()
            }
        }

        companion object {
            private const val RSA_MODE = "RSA/ECB/PKCS1Padding"
            private const val AES_MODE_M = "AES/GCM/NoPadding"
            private const val KEY_ALIAS = "KEY"
            private const val AndroidKeyStore = "AndroidKeyStore"
            const val ENCRYPTED_KEY = "ENCRYPTED_KEY"
            const val PUBLIC_IV = "PUBLIC_IV"
            private var keyHelper: KeyHelper? = null
            fun getInstance(ctx: Context, prefs: SharedPreferences): KeyHelper? {
                if (keyHelper == null) {
                    try {
                        keyHelper =
                            KeyHelper(
                                ctx,
                                prefs
                            )
                    } catch (e: NoSuchPaddingException) {
                        e.printStackTrace()
                    } catch (e: NoSuchProviderException) {
                        e.printStackTrace()
                    } catch (e: NoSuchAlgorithmException) {
                        e.printStackTrace()
                    } catch (e: InvalidAlgorithmParameterException) {
                        e.printStackTrace()
                    } catch (e: KeyStoreException) {
                        e.printStackTrace()
                    } catch (e: CertificateException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return keyHelper
            }
        }

        init {
            generateEncryptKey(ctx)
            generateRandomIV()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                try {
                    generateAESKey()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
