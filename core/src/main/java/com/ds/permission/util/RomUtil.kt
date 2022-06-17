package com.ds.permission.util

import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 识别手机类型
 */
object RomUtil {
    const val ROM_MIUI = "MIUI"
    const val ROM_EMUI = "EMUI"
    const val ROM_FLYME = "FLYME"
    const val ROM_OPPO = "OPPO"
    const val ROM_SMARTISAN = "SMARTISAN"
    const val ROM_VIVO = "VIVO"
    const val ROM_QIKU = "QIKU"
    const val ROM_SAMSUNG = "SAMSUNG"
    private const val KEY_VERSION_MIUI = "ro.miui.ui.version.name"
    private const val KEY_VERSION_EMUI = "ro.build.version.emui"
    private const val KEY_VERSION_OPPO = "ro.build.version.opporom"
    private const val KEY_VERSION_SMARTISAN = "ro.smartisan.version"
    private const val KEY_VERSION_VIVO = "ro.vivo.os.version"
    private val TAG = RomUtil::class.java.simpleName
    private var sName: String? = null
    private var sVersion: String? = null
    val isEmui: Boolean
        get() = check(ROM_EMUI)
    val isMiui: Boolean
        get() = check(ROM_MIUI)
    val isVivo: Boolean
        get() = check(ROM_VIVO)
    val isSamSung: Boolean
        get() {
            try {
                return Build.BRAND.toUpperCase().contains(ROM_SAMSUNG)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    val isOppo: Boolean
        get() = check(ROM_OPPO)
    val isFlyme: Boolean
        get() = check(ROM_FLYME)

    fun is360(): Boolean {
        return check(ROM_QIKU) || check("360")
    }

    val isSmartisan: Boolean
        get() = check(ROM_SMARTISAN)
    val name: String?
        get() {
            if (sName == null) {
                check("")
            }
            return sName
        }
    val version: String?
        get() {
            if (sVersion == null) {
                check("")
            }
            return sVersion
        }

    fun check(rom: String): Boolean {
        if (sName != null) {
            return sName == rom
        }
        if (!TextUtils.isEmpty(getProp(KEY_VERSION_MIUI).also { sVersion = it })) {
            sName = ROM_MIUI
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_EMUI).also { sVersion = it })) {
            sName = ROM_EMUI
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_OPPO).also { sVersion = it })) {
            sName = ROM_OPPO
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_VIVO).also { sVersion = it })) {
            sName = ROM_VIVO
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_SMARTISAN).also { sVersion = it })) {
            sName = ROM_SMARTISAN
        } else {
            sVersion = Build.DISPLAY
            if (sVersion?.toUpperCase()?.contains(ROM_FLYME) == true) {
                sName = ROM_FLYME
            } else {
                sVersion = Build.UNKNOWN
                sName = Build.MANUFACTURER.toUpperCase()
            }
        }
        return sName == rom
    }

    fun getProp(name: String): String? {
        var line: String? = null
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $name")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            Log.e(TAG, "Unable to read prop $name", ex)
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }

    /**
     * 是否6.0版本
     *
     * @return
     */
    val isM: Boolean
        get() = try {
            var result = true
            result = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                true
            } else {
                false
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
}