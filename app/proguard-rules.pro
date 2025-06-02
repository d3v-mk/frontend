########################################################
# RETROFIT 2 + GSON + COROUTINES + KOTLIN + COMPOSE
########################################################

# === RETROFIT ===
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Signature

# === SUA API (não pode ofuscar interfaces e DTOs usados) ===
-keep interface com.panopoker.data.api.** { *; }
-keep class com.panopoker.data.api.** { *; }
-keep class com.panopoker.model.** { *; }

# === GSON (mantém campos com @SerializedName) ===
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepattributes *Annotation*

# === KOTLIN METADATA (obrigatório p/ coroutines e generics)
-keepattributes KotlinMetadata
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**

# === COROUTINE SUSPEND FUN FIX
-keep class kotlin.coroutines.** { *; }
-keepclassmembers class ** {
    **(kotlin.coroutines.Continuation);
}

# === VIEWMODEL & ANDROID BASICS ===
-keep class * extends androidx.lifecycle.ViewModel
-keep class * extends android.app.Activity
-keepclassmembers class * {
    public <init>(...);
}
-keepclasseswithmembers class * {
    public <init>(...);
}

# === OKHTTP / LOGGING (pra evitar warning desnecessário) ===
-dontwarn okhttp3.**
-dontwarn okio.**

# === GMS (Google Login / Play Services Auth)
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }

# === DEBUG PURPOSES (remove depois se quiser)
# -printconfiguration build/proguard/mapping.txt
