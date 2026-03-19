package com.example.bardakovexam.data.remotes

import com.example.bardakovexam.data.models.User
import com.example.bardakovexam.domain.utils.supabaseConnectionValues
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
                User(
                    id = user.getString("id"),
                    email = user.optString("email"),
                    name = user.optJSONObject("user_metadata")?.optString("name")?.takeIf { it.isNotBlank() },
                    password = password
                )
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

    suspend fun updateAccount(email: String? = null, password: String? = null, name: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val token = SessionManager.accessToken ?: error("Нет токена")
            val payload = JSONObject().apply {
                if (!email.isNullOrBlank()) put("email", email)
                if (!password.isNullOrBlank()) put("password", password)
                if (!name.isNullOrBlank()) put("data", JSONObject().put("name", name))
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
            if (!email.isNullOrBlank()) SessionManager.email = email
        }
    }


    private fun loadAuthUser(): User? {
        val token = SessionManager.accessToken ?: return null
        val request = Request.Builder()
            .url("${supabaseConnectionValues.BASE_URL}/auth/v1/user")
            .addHeader("apikey", supabaseConnectionValues.API_KEY)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()
        client.newCall(request).execute().use {
            val raw = it.body?.string().orEmpty()
            if (!it.isSuccessful) return null
            val obj = JSONObject(raw)
            return User(
                id = obj.optString("id").ifBlank { SessionManager.userId.orEmpty() },
                email = obj.optString("email").ifBlank { SessionManager.email.orEmpty() },
                name = obj.optJSONObject("user_metadata")?.optString("name")?.takeIf { it.isNotBlank() }
            )
        }
    }

    private fun userFromProfileJson(obj: JSONObject, fallbackUserId: String): User {
        val authUser = loadAuthUser()
        return User(
            id = fallbackUserId,
            email = obj.optString("address").takeIf { it.isNotBlank() } ?: authUser?.email.orEmpty().ifBlank { SessionManager.email.orEmpty() },
            profileId = obj.optString("id").takeIf { it.isNotBlank() },
            name = obj.optString("firstname").takeIf { it.isNotBlank() } ?: authUser?.name,
            password = obj.optString("phone").takeIf { it.isNotBlank() },
            photo = obj.optString("photo").takeIf { it.isNotBlank() }
        )
    }

    suspend fun loadCurrentUser(): Result<User> = withContext(Dispatchers.IO) {
        runCatching {
            val userId = SessionManager.userId ?: error("Пользователь не найден")
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
                val profile = runCatching { JSONArray(raw).optJSONObject(0) }.getOrNull()
                profile?.let { userFromProfileJson(it, userId) } ?: (loadAuthUser() ?: User(id = userId, email = SessionManager.email.orEmpty()))
            }
        }
    }

    suspend fun saveCurrentUser(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sanitizedUser = user.copy(
                name = user.name?.trim(),
                email = user.email.trim(),
                password = user.password?.trim()
            )
            updateAccount(
                email = sanitizedUser.email,
                password = sanitizedUser.password,
                name = sanitizedUser.name
            ).getOrThrow()

            val payload = JSONObject().apply {
                put("user_id", sanitizedUser.id)
                put("firstname", sanitizedUser.name ?: JSONObject.NULL)
                put("address", sanitizedUser.email)
                put("phone", sanitizedUser.password ?: JSONObject.NULL)
                put("photo", sanitizedUser.photo ?: JSONObject.NULL)
            }

            val requestBuilder = Request.Builder()
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Authorization", "Bearer ${SessionManager.accessToken ?: supabaseConnectionValues.API_KEY}")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")

            val request = if (!sanitizedUser.profileId.isNullOrBlank()) {
                requestBuilder
                    .url("${supabaseConnectionValues.BASE_URL}/rest/v1/profiles?id=eq.${sanitizedUser.profileId}")
                    .patch(payload.toString().toRequestBody(json))
                    .build()
            } else {
                requestBuilder
                    .url("${supabaseConnectionValues.BASE_URL}/rest/v1/profiles")
                    .post(payload.toString().toRequestBody(json))
                    .build()
            }

            client.newCall(request).execute().use {
                val raw = it.body?.string().orEmpty()
                if (!it.isSuccessful) {
                    throw IllegalStateException("HTTP ${it.code}: ${extractSupabaseError(raw)}")
                }
            }
        }
    }
}
