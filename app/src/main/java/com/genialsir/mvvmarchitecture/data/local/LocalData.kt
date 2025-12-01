package com.genialsir.mvvmarchitecture.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.genialsir.mvvmarchitecture.data.dto.login.LoginRequest
import com.genialsir.mvvmarchitecture.data.dto.login.LoginResponse
import com.genialsir.mvvmcommon.constant.FAVOURITES_KEY
import com.genialsir.mvvmcommon.data.DataResource
import com.genialsir.mvvmcommon.data.preDataStore
import com.genialsir.mvvmcommon.error.PASS_WORD_ERROR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/11
 */
class LocalData @Inject constructor(val context: Context) {

    // 收藏 key
    companion object {
        private val KEY_FAVOURITES = stringSetPreferencesKey(FAVOURITES_KEY)
    }

    fun doLogin(loginRequest: LoginRequest): DataResource<LoginResponse> {
        if (loginRequest == LoginRequest("genialsir@163.com", "123456")) {
            return DataResource.Success(
                LoginResponse(
                    "123", "Genial", "genialsir@163.com"
                )
            )
        }
        return DataResource.Error(PASS_WORD_ERROR)
    }

    suspend fun addFavourite(id: String) {
        context.preDataStore.edit { prefs ->
            val current = prefs[KEY_FAVOURITES]?.toMutableSet() ?: mutableSetOf()
            current.add(id)
            prefs[KEY_FAVOURITES] = current
        }
    }

    // 获取收藏 Flow
    suspend fun getFavourites(): List<String> {
        val prefs = context.preDataStore.data.first()
        return prefs[KEY_FAVOURITES]?.toList() ?: emptyList()
    }

    suspend fun getCachedFavourites(): Flow<DataResource<Set<String>>> =
        context.preDataStore.data.map { prefs ->
            DataResource.Success(prefs[KEY_FAVOURITES] ?: emptySet())
        }

    // 判断是否收藏
    suspend fun isFavourite(id: String): Flow<DataResource<Boolean>> =
        context.preDataStore.data.map { prefs ->
            val cache = prefs[KEY_FAVOURITES] ?: emptySet()
            DataResource.Success(cache.contains(id))
        }


    // 移除收藏
    suspend fun removeFromFavourites(id: String): DataResource<Boolean> {
        return try {
            context.preDataStore.edit { prefs ->
                val current = prefs[KEY_FAVOURITES]?.toMutableSet() ?: mutableSetOf()
                current.remove(id)
                prefs[KEY_FAVOURITES] = current
            }
            DataResource.Success(true)
        } catch (e: Exception) {
            DataResource.Error(-1)
        }
    }

}