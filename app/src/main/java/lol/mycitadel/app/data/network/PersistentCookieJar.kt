package lol.mycitadel.app.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Cookie jar backed by EncryptedSharedPreferences.
 * Session cookies survive app restarts; storage is encrypted at rest.
 */
class PersistentCookieJar(context: Context) : CookieJar {

    private val prefs: SharedPreferences = createEncryptedSharedPreferences(context)

    private val memory: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

    init {
        try {
            prefs.all.forEach { (key, value) ->
                if (value is String && key.startsWith(KEY_PREFIX)) {
                    decodeCookie(value)?.let { cookie ->
                        val host = key.removePrefix(KEY_PREFIX).substringBefore("::")
                        memory.getOrPut(host) { mutableListOf() }.add(cookie)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PersistentCookieJar", "Failed to load cached cookies from prefs", e)
        }
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val list = memory.getOrPut(host) { mutableListOf() }

        cookies.forEach { incoming ->
            list.removeAll { it.name == incoming.name }
            if (incoming.expiresAt > System.currentTimeMillis()) {
                list.add(incoming)
                try {
                    prefs.edit()
                        .putString(KEY_PREFIX + host + "::" + incoming.name, encodeCookie(incoming))
                        .apply()
                } catch (e: Exception) {
                    Log.e("PersistentCookieJar", "Error saving cookie", e)
                }
            } else {
                try {
                    prefs.edit()
                        .remove(KEY_PREFIX + host + "::" + incoming.name)
                        .apply()
                } catch (e: Exception) {
                    Log.e("PersistentCookieJar", "Error removing cookie", e)
                }
            }
        }
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val now = System.currentTimeMillis()
        return memory[url.host].orEmpty().filter { it.expiresAt > now }
    }

    @Synchronized
    fun clear() {
        memory.clear()
        try {
            val editor = prefs.edit()
            prefs.all.keys.filter { it.startsWith(KEY_PREFIX) }.forEach { editor.remove(it) }
            editor.apply()
        } catch (e: Exception) {
            Log.e("PersistentCookieJar", "Error clearing cookies", e)
        }
    }

    private fun encodeCookie(c: Cookie): String = listOf(
        c.name, c.value, c.expiresAt.toString(), c.domain, c.path,
        if (c.secure) "1" else "0",
        if (c.httpOnly) "1" else "0"
    ).joinToString("|")

    private fun decodeCookie(s: String): Cookie? {
        val parts = s.split("|")
        if (parts.size != 7) return null
        return try {
            Cookie.Builder()
                .name(parts[0])
                .value(parts[1])
                .expiresAt(parts[2].toLong())
                .domain(parts[3])
                .path(parts[4])
                .apply {
                    if (parts[5] == "1") secure()
                    if (parts[6] == "1") httpOnly()
                }
                .build()
        } catch (_: Exception) {
            null
        }
    }

    private companion object {
        const val PREFS_NAME = "citadel_session_cookies"
        const val KEY_PREFIX = "cookie::"

        private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
            return try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                Log.w("PersistentCookieJar", "EncryptedSharedPreferences corrupted. Resetting...", e)
                context.deleteSharedPreferences(PREFS_NAME)
                try {
                    val masterKey = MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build()

                    EncryptedSharedPreferences.create(
                        context,
                        PREFS_NAME,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                } catch (_: Exception) {
                    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                }
            }
        }
    }
}
