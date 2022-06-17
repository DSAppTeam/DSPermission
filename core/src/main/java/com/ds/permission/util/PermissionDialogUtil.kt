package com.ds.permission.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.ds.permission.PermissionConfig
import com.ds.permission.R
import com.ds.permission.save.PermissionPreferenceHelper

/**
 * 权限模块内部工具类
 */
object PermissionDialogUtil {
    private const val TIME_HOUR_48 = 1000 * 60 * 60 * 48

    private fun filterPermissionByTitle(
        context: Context?,
        permissions: Array<String?>
    ): Array<String?> {
        val titlePermissionMap: HashMap<String, String?> = HashMap()
        for (permission in permissions) {
            val titleName = context?.getString(getTitleResId(permission))
            if (!TextUtils.isEmpty(titleName)) {
                titlePermissionMap[titleName!!] = permission
            }
        }
        return titlePermissionMap.values.toTypedArray()
    }

    fun getDialogTitle(context: Context?, permissions: Array<String?>): String {
        val permissionList = filterPermissionByTitle(context, permissions)
        return if (permissionList.size == 1) {
            "${context?.getString(getTitleResId(permissions[0]))}权限使用说明"
        } else {
            "权限使用说明"
        }
    }

    fun getDialogContent(context: Context?, permissions: Array<String?>): String {
        val permissionList = filterPermissionByTitle(context, permissions)
        return if (permissionList.size == 1) {
            "${context?.getString(getContentResId(permissionList[0]))}"
        } else {
            var content = ""
            var index = 0
            for (permission in permissionList) {
                if (!content.contains(context?.getString(getContentResId(permission)).toString())) {
                    if (!TextUtils.isEmpty(content)) {
                        content += "\n"
                    }
                    content += "${++index}.${context?.getString(getTitleResId(permission))}：${
                        context?.getString(
                            getContentResId(permission)
                        )
                    }"
                }
            }
            content
        }
    }

    fun checkAndShowSettingDialog(activity: Activity?, config: PermissionConfig) {
        val unrequestedPermissionsArray: ArrayList<String> = ArrayList()
        for (permission in config.permissions) {
            if (!TextUtils.isEmpty(permission) && !PermissionUtils.check(activity, permission)) {
                unrequestedPermissionsArray.add(permission)
            }
        }
        if (activity != null && checkAutoShowAskNoMoreDialogTime(
                activity,
                unrequestedPermissionsArray
            ) && config.isShowSysSettingDialog
        ) {
            PermissionUtils.checkAndPromptSetting(
                activity,
                unrequestedPermissionsArray.toTypedArray(),
                getSettingDialogContent(
                    activity,
                    unrequestedPermissionsArray
                ),
                null
            )
            updateAutoShowAskNoMoreDialogTime(
                activity,
                unrequestedPermissionsArray
            )
        }
    }

    private fun getSettingDialogContent(context: Context, permissions: ArrayList<String>): String {
        var content = "需要在系统设置中开启"
        for (permission in permissions) {
            if (!content.contains(context.getString(getTitleResId(permission)))) {
                if (content.length > 10 && !TextUtils.isEmpty(permission)) {
                    content += "和"
                }
                content += context.getString(getTitleResId(permission))
            }
        }
        return content + "权限，才能使用该功能"
    }

    private fun getTitleResId(permission: String?): Int {
        return when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE -> R.string.libpermission_permission_storage
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> R.string.libpermission_permission_location
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE -> R.string.libpermission_permission_phone_state
            Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS -> R.string.libpermission_permission_contact
            Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS -> R.string.libpermission_permission_sms
            Manifest.permission.CAMERA -> R.string.libpermission_permission_camera
            Manifest.permission.RECORD_AUDIO -> R.string.libpermission_permission_audio
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR -> R.string.libpermission_permission_calendar
            Manifest.permission.REQUEST_INSTALL_PACKAGES -> R.string.libpermission_permission_install_packages
            else -> R.string.libpermission_permission_default
        }
    }

    private fun getContentResId(permission: String?): Int {
        return when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE -> R.string.libpermission_permission_storage_content
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> R.string.libpermission_permission_location_content
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE -> R.string.libpermission_permission_phone_state_content
//            Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS -> R.string.permission_contact_content
            Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS -> R.string.libpermission_permission_sms_content
            Manifest.permission.CAMERA -> R.string.libpermission_permission_camera_content
            Manifest.permission.RECORD_AUDIO -> R.string.libpermission_permission_audio_content
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR -> R.string.libpermission_permission_calendar_content
            Manifest.permission.REQUEST_INSTALL_PACKAGES -> R.string.libpermission_permission_install_packages_content
            else -> R.string.libpermission_permission_default_content
        }
    }

    /**
     * @param context
     * @param permissions
     * @return 对一组权限，校验是否需要弹禁止后不再询问引导弹窗
     *         若其中之一距离上次弹窗超过48小时，返回 true
     */
    private fun checkAutoShowAskNoMoreDialogTime(
        context: Context,
        permissions: ArrayList<String>
    ): Boolean {
        for (permission in permissions) {
            if (System.currentTimeMillis() - PermissionPreferenceHelper.getLong(
                    context,
                    getAutoShowAskNoMoreDialogTimeKey(permission),
                    0
                ) > TIME_HOUR_48
            ) {
                return true
            }
        }
        return false
    }

    private fun updateAutoShowAskNoMoreDialogTime(
        context: Context,
        permissions: ArrayList<String>
    ) {
        for (permission in permissions) {
            val key = getAutoShowAskNoMoreDialogTimeKey(permission)
            if (!TextUtils.isEmpty(permission) && !TextUtils.isEmpty(key)) {
                PermissionPreferenceHelper.putLong(
                    context,
                    getAutoShowAskNoMoreDialogTimeKey(permission),
                    System.currentTimeMillis()
                )
            }
        }
    }

    private fun getAutoShowAskNoMoreDialogTimeKey(permission: String?): String {
        return when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                PermissionPreferenceHelper.AutoShowAskNoMoreDialogTime.PERMISSION_STORAGE
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION ->
                PermissionPreferenceHelper.AutoShowAskNoMoreDialogTime.PERMISSION_LOCATION
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE ->
                PermissionPreferenceHelper.AutoShowAskNoMoreDialogTime.PERMISSION_PHONE_STATE
            Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS ->
                PermissionPreferenceHelper.AutoShowAskNoMoreDialogTime.PERMISSION_CONTACT
            Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS ->
                PermissionPreferenceHelper.AutoShowAskNoMoreDialogTime.PERMISSION_SMS
            Manifest.permission.CAMERA ->
                PermissionPreferenceHelper.AutoShowAskNoMoreDialogTime.PERMISSION_CAMERA
            Manifest.permission.RECORD_AUDIO ->
                PermissionPreferenceHelper.AutoShowAskNoMoreDialogTime.PERMISSION_AUDIO
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR ->
                PermissionPreferenceHelper.AutoShowAskNoMoreDialogTime.PERMISSION_CALENDAR
            Manifest.permission.REQUEST_INSTALL_PACKAGES ->
                PermissionPreferenceHelper.AutoShowAskNoMoreDialogTime.PERMISSION_INSTALL_PACKAGES
            else ->
                PermissionPreferenceHelper.AutoShowAskNoMoreDialogTime.PERMISSION_DEFAULT
        }
    }

    /**
     * @param context
     * @param permissions
     * @return 对一组权限，校验是否弹出过权限使用说明弹窗
     *         若其中之一没有弹出过，返回 true
     */
    fun checkAutoShowInstructionDialogTime(context: Context, permissions: Array<String?>): Boolean {
        for (permission in permissions) {
            if (!PermissionPreferenceHelper.getBoolean(
                    context,
                    getAutoShowInstructionDialogTimeKey(permission),
                    false
                )
            ) {
                return true
            }
        }
        return false
    }

    fun updateAutoShowInstructionDialogTime(context: Context, permissions: Array<String?>) {
        for (permission in permissions) {
            val key = getAutoShowInstructionDialogTimeKey(permission)
            if (!TextUtils.isEmpty(permission) && !TextUtils.isEmpty(key)) {
                PermissionPreferenceHelper.putBoolean(
                    context,
                    getAutoShowInstructionDialogTimeKey(permission),
                    true
                )
            }
        }
    }

    private fun getAutoShowInstructionDialogTimeKey(permission: String?): String {
        return when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                PermissionPreferenceHelper.AutoShowInstructionDialogTime.PERMISSION_STORAGE
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION ->
                PermissionPreferenceHelper.AutoShowInstructionDialogTime.PERMISSION_LOCATION
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE ->
                PermissionPreferenceHelper.AutoShowInstructionDialogTime.PERMISSION_PHONE_STATE
            Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS ->
                PermissionPreferenceHelper.AutoShowInstructionDialogTime.PERMISSION_CONTACT
            Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS ->
                PermissionPreferenceHelper.AutoShowInstructionDialogTime.PERMISSION_SMS
            Manifest.permission.CAMERA ->
                PermissionPreferenceHelper.AutoShowInstructionDialogTime.PERMISSION_CAMERA
            Manifest.permission.RECORD_AUDIO ->
                PermissionPreferenceHelper.AutoShowInstructionDialogTime.PERMISSION_AUDIO
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR ->
                PermissionPreferenceHelper.AutoShowInstructionDialogTime.PERMISSION_CALENDAR
            Manifest.permission.REQUEST_INSTALL_PACKAGES ->
                PermissionPreferenceHelper.AutoShowInstructionDialogTime.PERMISSION_INSTALL_PACKAGES
            else ->
                PermissionPreferenceHelper.AutoShowInstructionDialogTime.PERMISSION_DEFAULT
        }
    }
}