package com.ds.permission.save

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringDef

object PermissionPreferenceHelper {
    private const val NAME = "ds_permission"

    private var instance: SharedPreferences? = null

    private fun getSharedPreference(context: Context): SharedPreferences {
        if (instance == null) {
            instance = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        }
        return instance!!
    }

    fun putLong(context: Context, key: String?, value: Long): Boolean {
        val sharedPreference = getSharedPreference(context)
        val editor = sharedPreference.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    fun getLong(context: Context, key: String?, defValue: Long): Long {
        val sharedPreference = getSharedPreference(context)
        return sharedPreference.getLong(key, defValue)
    }

    fun putBoolean(context: Context, key: String?, value: Boolean): Boolean {
        val sharedPreference = getSharedPreference(context)
        val editor = sharedPreference.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    fun getBoolean(context: Context, key: String?, defValue: Boolean): Boolean {
        val sharedPreference = getSharedPreference(context)
        return sharedPreference.getBoolean(key, defValue)
    }

    @StringDef(AutoShowAskNoMoreDialogTime.PERMISSION_STORAGE, AutoShowAskNoMoreDialogTime.PERMISSION_LOCATION, AutoShowAskNoMoreDialogTime.PERMISSION_PHONE_STATE, AutoShowAskNoMoreDialogTime.PERMISSION_CONTACT, AutoShowAskNoMoreDialogTime.PERMISSION_SMS, AutoShowAskNoMoreDialogTime.PERMISSION_CAMERA, AutoShowAskNoMoreDialogTime.PERMISSION_AUDIO, AutoShowAskNoMoreDialogTime.PERMISSION_CALENDAR, AutoShowAskNoMoreDialogTime.PERMISSION_INSTALL_PACKAGES, AutoShowAskNoMoreDialogTime.PERMISSION_DEFAULT)
    annotation class AutoShowAskNoMoreDialogTime {
        companion object {
            const val PERMISSION_STORAGE = "PERMISSION_STORAGE_ASK_NO_MORE" // ??????
            const val PERMISSION_LOCATION = "PERMISSION_LOCATION_ASK_NO_MORE" // ????????????
            const val PERMISSION_PHONE_STATE = "PERMISSION_PHONE_STATE_ASK_NO_MORE" // ??????????????????????????????
            const val PERMISSION_CONTACT = "PERMISSION_CONTACT_ASK_NO_MORE" // ?????????
            const val PERMISSION_SMS = "PERMISSION_SMS_ASK_NO_MORE" // ??????
            const val PERMISSION_CAMERA = "PERMISSION_CAMERA_ASK_NO_MORE" // ??????
            const val PERMISSION_AUDIO = "PERMISSION_AUDIO_ASK_NO_MORE" // ?????????
            const val PERMISSION_CALENDAR = "PERMISSION_CALENDAR_ASK_NO_MORE" // ??????
            const val PERMISSION_INSTALL_PACKAGES = "PERMISSION_INSTALL_PACKAGES_ASK_NO_MORE" // ????????????
            const val PERMISSION_DEFAULT = "PERMISSION_DEFAULT_ASK_NO_MORE" // ??????????????????????????????
        }
    }

    @StringDef(AutoShowInstructionDialogTime.PERMISSION_STORAGE, AutoShowInstructionDialogTime.PERMISSION_LOCATION, AutoShowInstructionDialogTime.PERMISSION_PHONE_STATE, AutoShowInstructionDialogTime.PERMISSION_CONTACT, AutoShowInstructionDialogTime.PERMISSION_SMS, AutoShowInstructionDialogTime.PERMISSION_CAMERA, AutoShowInstructionDialogTime.PERMISSION_AUDIO, AutoShowInstructionDialogTime.PERMISSION_CALENDAR, AutoShowInstructionDialogTime.PERMISSION_INSTALL_PACKAGES, AutoShowInstructionDialogTime.PERMISSION_DEFAULT)
    annotation class AutoShowInstructionDialogTime {
        companion object {
            const val PERMISSION_STORAGE = "PERMISSION_STORAGE_INSTRUCTION" // ??????
            const val PERMISSION_LOCATION = "PERMISSION_LOCATION_INSTRUCTION" // ????????????
            const val PERMISSION_PHONE_STATE = "PERMISSION_PHONE_STATE_INSTRUCTION" // ??????????????????????????????
            const val PERMISSION_CONTACT = "PERMISSION_CONTACT_INSTRUCTION" // ?????????
            const val PERMISSION_SMS = "PERMISSION_SMS_INSTRUCTION" // ??????
            const val PERMISSION_CAMERA = "PERMISSION_CAMERA_INSTRUCTION" // ??????
            const val PERMISSION_AUDIO = "PERMISSION_AUDIO_INSTRUCTION" // ?????????
            const val PERMISSION_CALENDAR = "PERMISSION_CALENDAR_INSTRUCTION" // ??????
            const val PERMISSION_INSTALL_PACKAGES = "PERMISSION_INSTALL_PACKAGES_INSTRUCTION" // ????????????
            const val PERMISSION_DEFAULT = "PERMISSION_DEFAULT_INSTRUCTION" // ??????????????????????????????
        }
    }
}