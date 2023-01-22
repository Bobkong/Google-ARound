package com.google.ar.core.codelabs.arlocalizer.utils

import android.util.Base64
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object PasswordEncryptUtil {

    /** encrypt key  */
    private var PASSWORD_ENC_SECRET = "xxxxxxx-20202020"
    /** encrypt algorithm */
    private val KEY_ALGORITHM = "AES"
    /** encrypt charset */
    private val CHARSET = Charset.forName("UTF-8")
    /** encrypt cipher */
    private val CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding"

    fun encrypt(initialPassword: String): String {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val byteArray = PASSWORD_ENC_SECRET.toByteArray(CHARSET)
        val keySpec = SecretKeySpec(byteArray, KEY_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(byteArray))
        val encrypted = cipher.doFinal(initialPassword.toByteArray(CHARSET))
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    fun decrypt(encryptPassword: String): String {
        val encrypted = Base64.decode(encryptPassword.toByteArray(CHARSET), Base64.NO_WRAP)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val byteArray = PASSWORD_ENC_SECRET.toByteArray(CHARSET)
        val keySpec = SecretKeySpec(byteArray, KEY_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(byteArray))
        val original = cipher.doFinal(encrypted)
        return String(original, CHARSET)
    }



}