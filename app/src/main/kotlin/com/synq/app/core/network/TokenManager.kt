package com.synq.app.core.network
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    private val sharedPreferences = EncryptedSharedPreferences.create(context, "secure_prefs", masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    fun saveToken(token: String?) { if (token == null) sharedPreferences.edit().remove("ACCESS_TOKEN").apply() else sharedPreferences.edit().putString("ACCESS_TOKEN", token).apply() }
    fun getToken(): String? = sharedPreferences.getString("ACCESS_TOKEN", null)
    fun saveUserId(userId: String?) { if (userId == null) sharedPreferences.edit().remove("USER_ID").apply() else sharedPreferences.edit().putString("USER_ID", userId).apply() }
    fun getUserId(): String? = sharedPreferences.getString("USER_ID", null)
}
