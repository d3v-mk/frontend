# Keep mínimo, só o necessário para o Gson funcionar (campo SerializedName)
-keepclassmembers class com.panopoker.data.model.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep só interfaces do Retrofit (sem manter métodos desnecessários)
-keep interface com.panopoker.data.service.** { *; }

# Keep Activities e ViewModels, que o Android exige
-keep public class * extends android.app.Activity
-keep public class * extends androidx.lifecycle.ViewModel

# Keep annotations que o Gson usa para não quebrar
-keep class com.google.gson.annotations.SerializedName { *; }

# Remove warnings inúteis (pra não poluir o build)
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Ativa otimizações full (menos essas que podem quebrar)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Ativa obfuscação completa (renomear tudo que puder)
# Nota: R8 faz isso por padrão, mas vamos forçar a mão com essas flags:
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,Signature

# Remove comentários e atributos que não ajudam
-dontusemixedcaseclassnames
-dontpreverify
-verbose

# Força o minify e shrink (remove código morto)
# MinifyEnabled já faz isso no build.gradle, mas não custa reforçar
# Remove classes, métodos e campos não usados
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

# Remova keep desnecessários e deixe o R8 mandar ver
# Não colocar keep genérico tipo -keep class ** { *; } para não deixar tudo exposto

# Deixe o R8 otimizar e ofuscar ao máximo
