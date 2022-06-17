package com.ds.permission

import java.util.*

class PermissionConfig(
    // 权限内容
    val permissions: ArrayList<String>,
    // 权限说明弹窗的内容
    val explanation: String?,
    showInstructionDialog: Boolean,
    showSysSettingDialog: Boolean
) {

    // 是否显示权限说明弹窗
    var isShowInstructionDialog = true

    //是否显示提示前往系统权限设置弹窗
    var isShowSysSettingDialog = true

    class Builder {
        private val permissions = ArrayList<String>()
        private var explanation: String? = null
        private var showInstructionDialog = true
        private var showSysSettingDialog = true
        fun addPermission(permission: String): Builder {
            permissions.add(permission)
            return this
        }

        fun addPermission(permission: Array<String?>): Builder {
            permissions.addAll(Arrays.asList<String>(*permission))
            return this
        }

        fun setExplanation(explanation: String?): Builder {
            this.explanation = explanation
            return this
        }

        fun setShowInstructionDialog(showInstructionDialog: Boolean): Builder {
            this.showInstructionDialog = showInstructionDialog
            return this
        }

        fun setShowSysSettingDialog(showSysSettingDialog: Boolean): Builder {
            this.showSysSettingDialog = showSysSettingDialog
            return this
        }

        fun build(): PermissionConfig {
            return PermissionConfig(permissions, explanation, showInstructionDialog, showSysSettingDialog)
        }
    }

    init {
        isShowInstructionDialog = showInstructionDialog
        isShowSysSettingDialog = showSysSettingDialog
    }
}