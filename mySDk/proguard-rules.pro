# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

-dontwarn okhttp3.internal.platform.*
-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontobfuscate
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}



# Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Coroutine (if used with Koin)
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# AndroidX ViewModel (if used with Koin)
-keep class androidx.lifecycle.ViewModel { *; }
-dontwarn androidx.lifecycle.ViewModel

# Add any other specific rules based on your project dependencies
## SQLCipher
#-keep class net.sqlcipher.database.SQLiteDatabase { *; }
#-keep class net.sqlcipher.database.SQLiteCursor { *; }
#-keep class net.sqlcipher.database.SQLiteCompiledSql { *; }
#-keep class net.sqlcipher.database.SQLiteProgram { *; }
#-keep class net.sqlcipher.database.SQLiteStatement { *; }
#-keep class net.sqlcipher.database.SQLiteOpenHelper { *; }

-keep class com.aktivolabs.aktivoelk.** { *; }
-keep class com.aktivolabs.aktivocore.** { *; }
-keep class com.aktivolabs.aktivocore.data.models.challenge.Challenge.** { *; }
-keep class com.aktivolabs.aktivocore.data.models.User.** { *; }
-keep class com.aktivolabs.aktivocore.data.models.aktivolite.AktivoLiteToolbarTheme.** { *; }
-keep class com.aktivolabs.aktivocore.managers.AktivoManager.** { *; }

-keep class com.smitfit.media.lib.** { *; }
-keep class com.smitfit.media.lib.ui.feature.main.ui.** { *; }


-keep class net.sqlcipher.** { *; }

-keep class com.test.my.app.uat.** { *; }

-keep class com.test.my.app.di.**{*;}
-keepclassmembernames class com.test.my.app.di.**{*;}
-keep class com.test.my.app.repository.**{*;}
-keep class com.test.my.app.remote.**{*;}
-keep class com.test.my.app.model.**{*;}
-keep class com.test.my.app.local.**{*;}

-keep class com.test.my.app.receiver.** { *; }
-keepclassmembers class com.test.my.app.receiver.** {*;}
-keep class com.test.my.app.receiver.FCMMessagingService.** { *; }
-keep class com.test.my.app.receiver.AppNavigationBroadcastReceiver.** { *; }

-keep class com.test.my.app.blogs.**{*;}
-keep class com.test.my.app.fitness_tracker.**{*;}
-keep class com.test.my.app.home.**{*;}
-keep class com.test.my.app.hra.**{*;}
-keep class com.test.my.app.medication_tracker.**{*;}
-keep class com.test.my.app.records_tracker.**{*;}
-keep class com.test.my.app.security.**{*;}
-keep class com.test.my.app.track_parameter.**{*;}
-keep class com.test.my.app.water_tracker.**{*;}
-keep class com.test.my.app.tools_calculators.**{*;}
-keep class com.test.my.app.navigation.**{*;}






-keep class com.test.my.app.repository.utils.**{*;}
-keep class com.test.my.app.repository.utils.NetworkBoundResource.**{*;}
-keepclassmembers class com.test.my.app.repository.utils.NetworkBoundResource.** {*;}
-keep class com.test.my.app.repository.utils.NetworkDataBoundResource.**{*;}
-keepclassmembers class com.test.my.app.repository.utils.NetworkDataBoundResource.** {*;}
-keep class com.test.my.app.repository.utils.Resource.**{*;}
-keepclassmembers class com.test.my.app.repository.utils.Resource.** {*;}
-keep class com.test.my.app.repository.utils.StoreNetworkDataBoundResource.**{*;}
-keepclassmembers class com.test.my.app.repository.utils.StoreNetworkDataBoundResource.** {*;}


-keepclassmembers class com.test.my.app.remote.** {*;}
-keepclassmembers class com.test.my.app.remote.interceptor.** {*;}
-keep class com.test.my.app.remote.interceptor.DecryptInterceptor.**{*;}
-keepclassmembers class com.test.my.app.remote.interceptor.DecryptInterceptor {*;}
-keep class com.test.my.app.remote.interceptor.EncryptInterceptor.**{*;}
-keepclassmembers class com.test.my.app.remote.interceptor.EncryptInterceptor {*;}



-keep class com.test.my.app.common.**{*;}
-keep class com.test.my.app.common.utils.**{*;}
-keep class com.test.my.app.common.utils.EncryptionUtility.**{*;}


-dontwarn com.google.android.exoplayer2.ExoPlayer$Builder
-dontwarn com.google.android.exoplayer2.ExoPlayer
-dontwarn com.google.android.exoplayer2.MediaItem
-dontwarn com.google.android.exoplayer2.Player$Listener
-dontwarn com.google.android.exoplayer2.Player
-dontwarn com.google.android.exoplayer2.source.MediaSource
-dontwarn com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
-dontwarn com.google.android.exoplayer2.source.hls.HlsMediaSource
-dontwarn com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection$Factory
-dontwarn com.google.android.exoplayer2.trackselection.DefaultTrackSelector
-dontwarn com.google.android.exoplayer2.trackselection.ExoTrackSelection$Factory
-dontwarn com.google.android.exoplayer2.trackselection.TrackSelector
-dontwarn com.google.android.exoplayer2.ui.StyledPlayerView
-dontwarn com.google.android.exoplayer2.upstream.BandwidthMeter
-dontwarn com.google.android.exoplayer2.upstream.DataSource$Factory
-dontwarn com.google.android.exoplayer2.upstream.DefaultBandwidthMeter$Builder
-dontwarn com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
-dontwarn com.google.android.exoplayer2.upstream.DefaultDataSource$Factory
-dontwarn com.google.android.exoplayer2.upstream.DefaultHttpDataSource$Factory
-dontwarn com.google.android.exoplayer2.upstream.TransferListener
-dontwarn com.google.android.exoplayer2.util.Util
-dontwarn com.smit.fit.media.lib.ui.BR
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString
-dontwarn org.jtransforms.fft.DoubleFFT_1D



