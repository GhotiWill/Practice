package com.example.bardakovexam.data.remotes

import com.example.bardakovexam.data.models.Profile
import com.example.bardakovexam.data.models.User
import com.example.bardakovexam.domain.utils.supabaseConnectionValues
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class UserRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val json = "application/json".toMediaType()

    private fun extractSupabaseError(raw: String): String {
        if (raw.isBlank()) return "Пустой ответ сервера"
        return runCatching {
            val obj = JSONObject(raw)
            obj.optString("msg").takeIf { it.isNotBlank() }
                ?: obj.optString("message").takeIf { it.isNotBlank() }
                ?: obj.optString("error_description").takeIf { it.isNotBlank() }
                ?: obj.optString("error").takeIf { it.isNotBlank() }
                ?: raw
        }.getOrElse {
            runCatching {
                val arr = JSONArray(raw)
                arr.optJSONObject(0)?.optString("message") ?: raw
            }.getOrDefault(raw)
        }
    }

    suspend fun signUp(name: String, email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject()
                .put("email", email)
                .put("password", password)
                .put("data", JSONObject().put("name", name))
                .toString()
                .toRequestBody(json)
            val request = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/auth/v1/signup")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()
            client.newCall(request).execute().use {
                val raw = it.body?.string().orEmpty()
                if (!it.isSuccessful) {
                    throw IllegalStateException("HTTP ${it.code}: ${extractSupabaseError(raw)}")
                }
            }
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().put("email", email).put("password", password).toString().toRequestBody(json)
            val request = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/auth/v1/token?grant_type=password")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()
            client.newCall(request).execute().use {
                val raw = it.body?.string().orEmpty()
                if (!it.isSuccessful) {
                    throw IllegalStateException("HTTP ${it.code}: ${extractSupabaseError(raw)}")
                }
                val obj = JSONObject(raw)
                val user = obj.getJSONObject("user")
                SessionManager.accessToken = obj.getString("access_token")
                SessionManager.userId = user.getString("id")
                SessionManager.email = user.optString("email")
                User(user.getString("id"), user.optString("email"))
            }
        }
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val token = SessionManager.accessToken ?: return@runCatching
            val request = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/auth/v1/logout")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Authorization", "Bearer $token")
                .post("".toRequestBody(null))
                .build()
            client.newCall(request).execute().use {
                val raw = it.body?.string().orEmpty()
                if (!it.isSuccessful) {
                    throw IllegalStateException("HTTP ${it.code}: ${extractSupabaseError(raw)}")
                }
            }
            SessionManager.clear()
        }
    }

    suspend fun sendRecovery(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().put("email", email).toString().toRequestBody(json)
            val request = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/auth/v1/recover")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()
            client.newCall(request).execute().use {
                val raw = it.body?.string().orEmpty()
                if (!it.isSuccessful) {
                    throw IllegalStateException("HTTP ${it.code}: ${extractSupabaseError(raw)}")
                }
            }
        }
    }

    suspend fun verifyRecovery(email: String, code: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().put("email", email).put("token", code).put("type", "recovery").toString().toRequestBody(json)
            val request = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/auth/v1/verify")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()
            client.newCall(request).execute().use {
                val raw = it.body?.string().orEmpty()
                if (!it.isSuccessful) {
                    throw IllegalStateException("HTTP ${it.code}: ${extractSupabaseError(raw)}")
                }
                SessionManager.accessToken = JSONObject(raw).optString("access_token")
            }
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        updateAccount(password = newPassword)
    }

    suspend fun updateAccount(email: String? = null, password: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val token = SessionManager.accessToken ?: error("Нет токена")
            val payload = JSONObject().apply {
                if (!email.isNullOrBlank()) put("email", email)
                if (!password.isNullOrBlank()) put("password", password)
            }
            if (payload.length() == 0) return@runCatching
            val request = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/auth/v1/user")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .put(payload.toString().toRequestBody(json))
                .build()
            client.newCall(request).execute().use {
                val raw = it.body?.string().orEmpty()
                if (!it.isSuccessful) {
                    throw IllegalStateException("HTTP ${it.code}: ${extractSupabaseError(raw)}")
                }
            }
            if (!email.isNullOrBlank()) {
                SessionManager.email = email
            }
        }
    }

    suspend fun loadProfile(): Result<Profile?> = withContext(Dispatchers.IO) {
        runCatching {
            val userId = SessionManager.userId ?: return@runCatching null
            val request = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/rest/v1/profiles?user_id=eq.$userId&select=*")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Authorization", "Bearer ${SessionManager.accessToken ?: supabaseConnectionValues.API_KEY}")
                .get()
                .build()
            client.newCall(request).execute().use {
                val raw = it.body?.string().orEmpty()
                if (!it.isSuccessful) {
                    throw IllegalStateException("HTTP ${it.code}: ${extractSupabaseError(raw)}")
                }
                val listType = object : TypeToken<List<Profile>>() {}.type
                gson.fromJson<List<Profile>>(raw, listType).firstOrNull()?.let {
                    it.copy(email = it.email ?: SessionManager.email)
                } ?: Profile(user_id = userId, email = SessionManager.email)
            }
        }
    }

    suspend fun saveProfile(profile: Profile): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sanitizedProfile = profile.copy(email = profile.email?.trim())
            updateAccount(email = sanitizedProfile.email, password = sanitizedProfile.password).getOrThrow()
            val request = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/rest/v1/profiles?on_conflict=user_id")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Authorization", "Bearer ${SessionManager.accessToken ?: supabaseConnectionValues.API_KEY}")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation,resolution=merge-duplicates")
                .post(gson.toJson(sanitizedProfile).toRequestBody(json))
                .build()
            client.newCall(request).execute().use {
                val raw = it.body?.string().orEmpty()
                if (!it.isSuccessful) {
                    throw IllegalStateException("HTTP ${it.code}: ${extractSupabaseError(raw)}")
                }
            }
        }
    }
}
