package com.example.bardakovexam.data.remotes

import com.example.bardakovexam.data.models.ActionItem
import com.example.bardakovexam.data.models.Product
import com.example.bardakovexam.domain.utils.supabaseConnectionValues
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ProductRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val json = "application/json".toMediaType()

    suspend fun loadProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/rest/v1/products?select=*")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .get()
                .build()
            client.newCall(request).execute().use {
                val resp = it.body?.string().orEmpty()
                if (!it.isSuccessful) error(resp)
                gson.fromJson(resp, object : TypeToken<List<Product>>() {}.type)
            }
        }
    }

    suspend fun loadActions(): Result<List<ActionItem>> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/rest/v1/actions?select=*")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .get()
                .build()
            client.newCall(request).execute().use {
                val resp = it.body?.string().orEmpty()
                if (!it.isSuccessful) error(resp)
                gson.fromJson(resp, object : TypeToken<List<ActionItem>>() {}.type)
            }
        }
    }

    suspend fun loadFavorites(): Result<List<Product>> = withContext(Dispatchers.IO) {
        runCatching {
            val userId = SessionManager.userId ?: return@runCatching emptyList()
            val favReq = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/rest/v1/favourite?user_id=eq.$userId&select=product_id")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Authorization", "Bearer ${SessionManager.accessToken ?: supabaseConnectionValues.API_KEY}")
                .build()
            val ids = client.newCall(favReq).execute().use {
                val resp = it.body?.string().orEmpty()
                if (!it.isSuccessful) error(resp)
                Regex("\"product_id\":\"([\\w-]+)\"").findAll(resp).map { m -> m.groupValues[1] }.toList()
            }
            if (ids.isEmpty()) return@runCatching emptyList()
            val inClause = ids.joinToString(",") { "\"$it\"" }
            val req = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/rest/v1/products?id=in.($inClause)&select=*")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .build()
            client.newCall(req).execute().use {
                val resp = it.body?.string().orEmpty()
                if (!it.isSuccessful) error(resp)
                gson.fromJson(resp, object : TypeToken<List<Product>>() {}.type)
            }
        }
    }

    suspend fun addFavorite(productId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val userId = SessionManager.userId ?: error("Не авторизован")
            val body = "{\"product_id\":\"$productId\",\"user_id\":\"$userId\"}".toRequestBody(json)
            val req = Request.Builder()
                .url("${supabaseConnectionValues.BASE_URL}/rest/v1/favourite")
                .addHeader("apikey", supabaseConnectionValues.API_KEY)
                .addHeader("Authorization", "Bearer ${SessionManager.accessToken ?: supabaseConnectionValues.API_KEY}")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .post(body)
                .build()
            client.newCall(req).execute().use { if (!it.isSuccessful) error(it.body?.string().orEmpty()) }
        }
    }
}
