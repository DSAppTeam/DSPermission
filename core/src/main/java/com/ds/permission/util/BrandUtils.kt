package com.ds.permission.util

import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

object BrandUtils {
    const val SYS_EMUI = "sys_emui"
    const val SYS_MIUI = "sys_miui"
    const val SYS_FLYME = "sys_flyme"
    private const val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private const val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private const val KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage"
    private const val KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level"
    private const val KEY_EMUI_VERSION = "ro.build.version.emui"
    private const val KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion"
    private val systemInfoInstance: SystemInfo = SystemInfo()

    @JvmStatic
    val systemInfo: SystemInfo
        get() {
            getSystem(systemInfoInstance)
            return systemInfoInstance
        }

    private fun getSystem(info: SystemInfo) {
        try {
            val prop = Properties()
            prop.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
            if (prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null) {
                info.os = SYS_MIUI //小米
                info.versionCode = Integer.valueOf(prop.getProperty(KEY_MIUI_VERSION_CODE, "0"))
                info.versionName = prop.getProperty(KEY_MIUI_VERSION_NAME, "V0")
            } else if (prop.getProperty(KEY_EMUI_API_LEVEL, null) != null || prop.getProperty(KEY_EMUI_VERSION, null) != null || prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null) {
                info.os = SYS_EMUI //华为
                info.versionCode = Integer.valueOf(prop.getProperty(KEY_EMUI_API_LEVEL, "0") as String)
                info.versionName = prop.getProperty(KEY_EMUI_VERSION, "unknown")
            } else if (meizuFlymeOSFlag.toLowerCase().contains("flyme")) {
                info.os = SYS_FLYME //魅族
                info.versionCode = 0
                info.versionName = "unknown"
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private val meizuFlymeOSFlag: String
        private get() = getSystemProperty("ro.build.display.id", "")

    private fun getSystemProperty(key: String, defaultValue: String): String {
        try {
            val clz = Class.forName("android.os.SystemProperties")
            val get = clz.getMethod("get", String::class.java, String::class.java)
            return get.invoke(clz, key, defaultValue) as String
        } catch (e: Exception) {
        }
        return defaultValue
    }

    class SystemInfo {
        var os = "android"
        var versionName = Build.VERSION.RELEASE
        var versionCode = Build.VERSION.SDK_INT
    }
}