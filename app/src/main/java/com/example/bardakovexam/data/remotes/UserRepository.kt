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
                User(id = user.getString("id"), email = user.optString("email"))
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

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        val token = SessionManager.accessToken ?: return Result.failure(IllegalStateException("Нет токена"))
        val passwordBody = JSONObject()
            .put("password", newPassword)
            .toString()
            .toRequestBody(json)

        return withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder()
                    .url("${supabaseConnectionValues.BASE_URL}/auth/v1/user")
                    .addHeader("apikey", supabaseConnectionValues.API_KEY)
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("Content-Type", "application/json")
                    .put(passwordBody)
                    .build()
                client.newCall(request).execute().use { response ->
                    val raw = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        throw IllegalStateException("HTTP ${response.code}: ${extractSupabaseError(raw)}")
                    }
                }
            }
        }
    }

    private fun userFromProfileJson(obj: JSONObject, fallbackUserId: String): User {
        return User(
            id = fallbackUserId,
            email = SessionManager.email.orEmpty(),
            profileId = obj.optString("id").takeIf { it.isNotBlank() },
            firstname = obj.optString("firstname").takeIf { it.isNotBlank() },
            lastname = obj.optString("lastname").takeIf { it.isNotBlank() },
            address = obj.optString("address").takeIf { it.isNotBlank() },
            phone = obj.optString("phone").takeIf { it.isNotBlank() },
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
                profile?.let { userFromProfileJson(it, userId) } ?: User(id = userId, email = SessionManager.email.orEmpty())
            }
        }
    }

    suspend fun saveCurrentUser(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sanitizedUser = user.copy(
                firstname = user.firstname?.trim(),
                lastname = user.lastname?.trim(),
                address = user.address?.trim(),
                phone = user.phone?.trim()
            )
            val payload = JSONObject().apply {
                put("user_id", sanitizedUser.id)
                put("firstname", sanitizedUser.firstname ?: JSONObject.NULL)
                put("lastname", sanitizedUser.lastname ?: JSONObject.NULL)
                put("address", sanitizedUser.address ?: JSONObject.NULL)
                put("phone", sanitizedUser.phone ?: JSONObject.NULL)
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
