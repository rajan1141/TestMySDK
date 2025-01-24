package com.test.my.app.security.util

import com.test.my.app.common.constants.Constants
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.net.ProxySelector
import java.net.URI

object SecurityChecks {

    fun isCheckMethodAppT(): Boolean {
        return checkPM() || checkPT() || checkProM() ||isDebuggerConnected()||
                 checkProE() ||checkForSuspiciousFiles() ||checkForSuspiciousStrings()||isFindFD()
    }

    private fun isDebuggerConnected(): Boolean {
        return android.os.Debug.isDebuggerConnected() || android.os.Debug.waitingForDebugger()
    }

    fun checkMethodPro(): Boolean {
        val proxySelector = ProxySelector.getDefault()
        val uri = URI(Constants.strAPIUrl)
        val proxyList = proxySelector.select(uri)

        if (proxyList != null && proxyList.isNotEmpty()) {
            val proxy = proxyList[0].toString()
            if (proxy.contains("DIRECT")) {
                // No proxy, app can run
                return false
            } else {
                // Proxy detected

                return true
            }
        }
        // Default case when no proxy is detected
        return false
    }

    private fun checkForSuspiciousFiles(): Boolean {
        val suspiciousPaths = listOf(
            "/data/local/tmp/re.frida.server/frida-agent-64.so",
            "/data/local/tmp/re.frida.server",
            "/sbin/.magisk"
        )

        return suspiciousPaths.any { path ->
            File(path).exists()
        }
    }

    private fun isFindFD(): Boolean {
        val suspiciousStrings = listOf("frida-server","frida-server-64","frida-server-32",
            "re.frida.server", "re.frida", "frida.", "frida-agent-64.so",
            "rida-agent-64.so", "agent-64.so", "frida-agent-32.so", "frida-helper-32",
            "frida-helper", "frida-agent", "pool-frida", "frida", "frida-",
            "frida-server", "linjector", "gum-js-loop", "frida_agent_main", "gmain",
            "frida", "magisk", ".magisk", "libriru", "xposed"
        )

        val filePath = "/data/local/tmp/"
        try {
            val process = Runtime.getRuntime().exec("ps")
            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            if (output.contains("frida-server")) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return try {
            File(filePath).readLines().any { line ->
                suspiciousStrings.any { it in line }
            }
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }

    private fun checkForSuspiciousStrings(): Boolean {
        val suspiciousStrings = listOf(
            "re.frida.server", "re.frida", "frida.", "frida-agent-64.so",
            "rida-agent-64.so", "agent-64.so", "frida-agent-32.so", "frida-helper-32",
            "frida-helper", "frida-agent", "pool-frida", "frida", "frida-",
            "frida-server", "linjector", "gum-js-loop", "frida_agent_main", "gmain",
            "frida", "magisk", ".magisk", "libriru", "xposed"
        )

        val filePath = "/proc/self/maps"
        return File(filePath).readLines().any { line ->
            suspiciousStrings.any { it in line }
        }
    }

    private fun checkPM(): Boolean {
        return checkFileForStrings(
            "/proc/self/maps", listOf(
                "re.frida.server", "frida-agent-64.so", "rida-agent-64.so",
                "agent-64.so", "frida-agent-32.so", "frida-helper-32",
                "frida-helper", "frida-agent", "pool-frida", "frida",
                "frida-", "frida-server",
                "linjector", "gum-js-loop", "frida_agent_main", "gmain",
                "frida", "magisk", ".magisk", "/sbin/.magisk", "libriru", "xposed"
            )
        )
    }


    private fun checkPT(): Boolean {
        return checkFileForStrings("/proc/self/task", listOf(
            "re.frida.server", "frida-agent-64.so", "rida-agent-64.so",
            "agent-64.so", "frida-agent-32.so", "frida-helper-32",
            "frida-helper", "frida-agent", "pool-frida", "frida",
             "frida-server", "linjector",
            "gum-js-loop", "frida_agent_main", "gmain", "magisk",
            ".magisk", "/sbin/.magisk", "libriru", "xposed", "pool-spawner", "gdbus"
        ))
    }

    private fun checkProM(): Boolean {
        return checkFileForStrings("/proc/mounts", listOf(
            "magisk", "/sbin/.magisk", "libriru", "xposed", "mirror", "system_root"
        ))
    }

    /*private fun isTrPidNZ(): Boolean {
        return try {
            val reader = BufferedReader(FileReader("/proc/self/status"))
            reader.use {
                it.lineSequence().forEach { line ->
                    if (line.startsWith("TracerPid:")) {
                        val tracerPid = line.split("\\s+".toRegex())[1]
                        return tracerPid != "0"
                    }
                }
            }
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }*/

    private fun checkProE(): Boolean {
        return checkFileForStrings("/proc/self/exe", listOf(
            "frida-agent-64.so", "frida-agent-32.so", "re.frida.server",
            "frida-helper-32", "frida-helper", "pool-frida", "frida",
            "/data/local/tmp", "frida-server", "linjector", "gum-js-loop",
            "frida_agent_main", "gmain", "frida-agent"
        ))
    }

    private fun checkFileForStrings(filePath: String, suspiciousStrings: List<String>): Boolean {
        return try {
            val reader = BufferedReader(FileReader(filePath))
            reader.use {
                it.lineSequence().forEach { line ->
                    suspiciousStrings.forEach { suspicious ->
                        if (line.contains(suspicious)) {
                            return true
                        }
                    }
                }
            }
            false
        } catch (e: IOException) {
//            e.printStackTrace()
            false
        }
    }
}
