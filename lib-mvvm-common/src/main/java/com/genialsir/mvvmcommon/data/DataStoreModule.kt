package com.genialsir.mvvmcommon.data

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/25
 */

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// 全局唯一 DataStore
val Context.preDataStore by preferencesDataStore("local_data_prefs")
