#############################
# 基础通用配置
#############################
# 保留类名、字段、方法
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
# 保留所有枚举字段名
-keepclassmembers enum * { *; }
# 保留序列化相关类
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 不混淆R文件
-keep class **.R$* { *; }
-keep class **.R { *; }

#############################
# Kotlin / 协程 / Jetpack 基础库
#############################
# Kotlin反射相关
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { *; }

#############################
# AndroidX Lifecycle / ViewModel / LiveData
#############################
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

#############################
# Jetpack Navigation
#############################
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

#############################
# DataStore Preferences
#############################
-dontwarn androidx.datastore.preferences.**
-keep class androidx.datastore.preferences.** { *; }

#############################
# Hilt / Dagger
#############################
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.**
-dontwarn javax.inject.**
# Hilt内部生成的类
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class dagger.hilt.internal.aggregatedroot.codegen.* { *; }
-keep class dagger.hilt.internal.componenttreedeps.ComponentTreeDeps { *; }

#############################
# Retrofit / OkHttp / Moshi
#############################
# Retrofit接口
-keep interface * {
    @retrofit2.http.* <methods>;
}
-keepattributes RuntimeVisibleAnnotations
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**
# Moshi反射与代码生成模型
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class ** {
    @com.squareup.moshi.* <fields>;
}
-dontwarn com.squareup.moshi.**

#############################
# Glide / Picasso 图片库
#############################
# Glide模块
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**
# Picasso
-dontwarn com.squareup.picasso.**

#############################
# Google Material / AppCompat
#############################
-dontwarn androidx.appcompat.**
-dontwarn com.google.android.material.**

#############################
# 测试框架 (Junit / Espresso)
#############################
-dontwarn org.junit.**
-dontwarn androidx.test.**
-dontwarn androidx.test.espresso.**
-dontwarn org.assertj.**
-dontwarn org.mockito.**

#############################
# TensorFlow Lite
#############################
-keep class org.tensorflow.lite.** { *; }
-dontwarn org.tensorflow.lite.**

#############################
# 其他实用库（mockk、annotation等）
#############################
-dontwarn io.mockk.**
-keep class androidx.annotation.** { *; }

#############################
# 日志 / 调试（可选）
#############################
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

#############################
# SmartCare
#############################
-keep class com.tls.smartcare.data.dto.** { *; }

#############################
# Auto
#############################
-keep class com.genialsir.autosize.** { *; }

#############################
# MPChart
#############################
-keep class com.github.mikephil.charting.** { *; }

#############################
# PermissionX
#############################
-keep class com.genialsir.permissionx.** { *; }

# Paho Android Service
-keep class org.eclipse.paho.android.service.** { *; }
-keep interface org.eclipse.paho.android.service.** { *; }

# Paho MQTT Client
-keep class org.eclipse.paho.client.mqttv3.** { *; }
-keep interface org.eclipse.paho.client.mqttv3.** { *; }
