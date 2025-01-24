package com.test.my.app.security.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Locale


object RootUtil {
    val Context.isCheckMethodOneDR: Boolean
        get() = isCheckPR()|| isCheckMD() || isCheckFD()|| isCheckFR()
                || checkSuperUser() || checkDRByCommand()
                || isEm || checkEm()

    val Context.isCheckMethodVConnected: Boolean
        get() = isVActive()

    private fun isCheckPR(): Boolean {
        val paths = arrayOf(
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun isCheckMD(): Boolean {
        val magickSuFile = File("/sbin/su")
        return magickSuFile.exists()
    }

    private fun isCheckFD(): Boolean {
        val fridaLibFile = File("/data/local/tmp/frida-server")
        val fridaHelperFile = File("/data/local/tmp/frida-helper")
        return fridaLibFile.exists() || fridaHelperFile.exists()
    }

    private fun Context.isCheckFR(): Boolean {
        val fridaProcesses = arrayOf("frida-server", "frida-helper")
        for (process in fridaProcesses) {
            if (isCheckPR(this, process)) return true
        }
        return false
    }

    private fun isCheckPR(context: Context, processName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var process=false
        for (processInfo in activityManager.runningAppProcesses){
            if(processInfo.processName==processName){
                process=true
                break
            }
        }
        return process
    }

    private fun Context.checkSuperUser(): Boolean {
        val superuserApks = listOf(
            "com.koushikdutta.superuser",
            "eu.chainfire.supersu",
            "com.topjohnwu.magisk"
        )

        for (superuserApk in superuserApks) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.setPackage(superuserApk)
            val list: List<ResolveInfo> = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.isNotEmpty()) {
                return true
            }
        }

        return false
    }

    // Check if the "su" command is executable
    private fun checkDRByCommand(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            `in`.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    private val isEm: Boolean by lazy {
        // Android SDK emulator
        val kernelVersion = System.getProperty("os.version")
        return@lazy ((Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone_") && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone_"))
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.PRODUCT.contains("sdk_google")
                || Build.MODEL.contains("google_sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MODEL.contains("Android SDK built for x86_64")
                || "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("Genymotion")
                || Build.HOST.startsWith("Build") //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.PRODUCT == "google_sdk"
                || Build.PRODUCT == "sdk"
                || Build.PRODUCT == "sdk_x86"
                || Build.PRODUCT == "sdk_x86_x64"
                || Build.HARDWARE.lowercase(Locale.getDefault()).contains("nox")
                || Build.PRODUCT.lowercase(Locale.getDefault()).contains("nox")
                || Build.BRAND.lowercase(Locale.getDefault()).contains("nox")
                || Build.MANUFACTURER.lowercase(Locale.getDefault()).contains("nox")
                || (kernelVersion != null && (kernelVersion.contains("x86") || kernelVersion.contains("x86_64")))
                )

    }

    private fun checkEm(): Boolean {
        return (checkFiles(GENY_FILES)
                || checkFiles(ANDY_FILES)
                || checkFiles(NOX_FILES)
                || checkFiles(X86_FILES)
                || checkFiles(PIPES))
    }

    private fun checkFiles(targets: Array<String>): Boolean {
        for (pipe in targets) {
            val file = File(pipe)
            if (file.exists()) {
                return true
            }
        }
        return false
    }

    private val GENY_FILES = arrayOf(
        "/dev/socket/genyd",
        "/dev/socket/baseband_genyd"
    )
    private val PIPES = arrayOf(
        "/dev/socket/qemud",
        "/dev/qemu_pipe"
    )
    private val X86_FILES = arrayOf(
        "ueventd.android_x86.rc",
        "x86.prop",
        "ueventd.ttVM_x86.rc",
        "init.ttVM_x86.rc",
        "fstab.ttVM_x86",
        "fstab.vbox86",
        "init.vbox86.rc",
        "ueventd.vbox86.rc"
    )
    private val ANDY_FILES = arrayOf(
        "fstab.andy",
        "ueventd.andy.rc"
    )
    private val NOX_FILES = arrayOf(
        "fstab.nox",
        "init.nox.rc",
        "ueventd.nox.rc"
    )

    fun Context.isCheckMethodDME(): Boolean {
//        return Settings.Global.getInt(this.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0
        return Settings.Secure.getInt(
            contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        ) != 0
    }

    private fun Context.isVActive(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val vpnNetwork = connectivityManager.allNetworks.firstOrNull { network->
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
        }
        return vpnNetwork != null && connectivityManager.getNetworkInfo(vpnNetwork)?.isConnectedOrConnecting == true
    }
}

