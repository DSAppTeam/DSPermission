package com.ds.permission.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import com.ds.permission.R
import com.ds.permission.ui.dialog.CommonDialog

/**
 * 提供检测各种场景工具类
 * created by songsiting on 2021/08/24
 */
object PermissionUtils {
    /**
     * 传递单个权限判断
     */
    @JvmStatic
    fun check(context: Context?, permission: String): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            if (TextUtils.equals(permission, Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context?.packageManager?.canRequestPackageInstalls() == true
                } else {
                    true
                }
            }
            context?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 传递多个权限判断
     */
    @JvmStatic
    fun check(context: Context, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        for (permission in permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 检查权限并跳转系统设置
     * @param permission
     * @param explanation 跳转弹窗提示内容
     */
    @JvmStatic
    fun checkAndPromptSetting(context: Context, permission: String, explanation: String): Boolean {
        return checkAndPromptSetting(context, permission, explanation, null)
    }

    @JvmStatic
    fun checkAndPromptSetting(context: Context, permissions: Array<String>, explanation: String): Boolean {
        return checkAndPromptSetting(context, permissions, explanation, null)
    }

    @JvmStatic
    fun checkAndPromptSetting(context: Context, permission: String, explanation: String, cancelListener: View.OnClickListener?): Boolean {
        if (check(context, permission)) {
            return true
        }
        promptSetting(context, explanation, cancelListener)
        return false
    }

    @JvmStatic
    fun checkAndPromptSetting(context: Context, permissions: Array<String>, explanation: String, cancelListener: View.OnClickListener?): Boolean {
        var isAllGranted = true
        for (permission in permissions) {
            if (!check(context, permission)) {
                isAllGranted = false
                break
            }
        }
        if (isAllGranted) {
            return true
        }
        promptSetting(context, explanation, cancelListener)
        return false
    }

    @JvmStatic
    fun promptSetting(context: Context, explanation: String, cancelListener: View.OnClickListener?) {
        val commonDialog = CommonDialog(context)
        commonDialog.setContent(explanation)
        commonDialog.setLeftAction("取消", cancelListener)
        commonDialog.setRightAction("去设置", R.color.libpermission_themeColorPrimary, View.OnClickListener {
            commonDialog.dismiss()
            launchSystemAppSettings(context)
        })
        commonDialog.setCanceledOnTouchOutside(false)
        commonDialog.show()
    }

    /**
     * 跳转系统设置
     */
    @JvmStatic
    fun launchSystemAppSettings(context: Context?) {
        try {
            if (RomUtil.isMiui) {
                launchSystemAppSettingsForMIUI(context)
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${context?.packageName}")
                context?.startActivity(intent)
            }
        } catch (tr: Throwable) {
            tr.printStackTrace()
        }
    }


    /**
     * 跳转到MIUI应用权限设置页面
     */
    private fun launchSystemAppSettingsForMIUI(context: Context?) {
        if (context == null) {
            return
        }
        try {
            // MIUI 8
            val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
            localIntent.putExtra("extra_pkgname", context.packageName)
            context.startActivity(localIntent)
        } catch (e: Exception) {
            try {
                // MIUI 5/6/7
                val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
                localIntent.putExtra("extra_pkgname", context.packageName)
                context.startActivity(localIntent)
            } catch (e1: Exception) {
                // 否则跳转到应用详情
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        }
    }


}