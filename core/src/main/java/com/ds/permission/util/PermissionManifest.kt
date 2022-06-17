package com.ds.permission.util

import android.Manifest

object PermissionManifest {
    private var NormalPermissions: List<String> = ArrayList(listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ))

    private var InstallPackagesPermission: List<String> = ArrayList(listOf(
        "android.permission.REQUEST_INSTALL_PACKAGES"
    ))

    fun getNormalPermission(permissions: Array<String?>?): Array<String> {
        val result: MutableList<String> = ArrayList()
        if (permissions != null) {
            for (permission in permissions) {
                if (NormalPermissions.contains(permission)) {
                    result.add(permission!!)
                }
            }
        }
        return result.toTypedArray()
    }

    fun getInstallPackagesPermission(permissions: Array<String?>?): Array<String> {
        val result: MutableList<String> = ArrayList()
        if (permissions != null) {
            for (permission in permissions) {
                if (InstallPackagesPermission.contains(permission)) {
                    result.add(permission!!)
                }
            }
        }
        return result.toTypedArray()
    }
}