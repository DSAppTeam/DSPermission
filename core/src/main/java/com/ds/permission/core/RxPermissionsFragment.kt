package com.ds.permission.core

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ds.permission.ui.popup.OverlayPopup
import com.ds.permission.ui.dialog.CommonDialog
import com.ds.permission.util.PermissionManifest
import io.reactivex.subjects.PublishSubject
import java.util.*

class RxPermissionsFragment : Fragment() {
    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private val mSubjects: MutableMap<String, PublishSubject<Permission>?> = HashMap()
    private var mLogging = false
    var dialog: OverlayPopup? = null
    private var taskHead: Task? = null
    private var taskTail: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(RxPermissions.TAG, "onCreate: RxPermissionsFragment")
        retainInstance = true
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissions(permissions: Array<String?>?) {
        taskHead = null
        taskTail = null

        // 普通权限
        val normalPermissions = PermissionManifest.getNormalPermission(permissions)
        if (normalPermissions.isNotEmpty()) {
            val task = object : Task {
                override var next: Task? = null

                override fun req() {
                    taskHead = taskHead?.next
                    requestPermissions(normalPermissions, NORMAL_PERMISSIONS_REQUEST_CODE)
                }

                override fun name(): String {
                    return "NormalPermission"
                }
            }
            addTaskToChain(task)
        }

        // 特殊权限-安装应用
        val installPackagesPermission = PermissionManifest.getInstallPackagesPermission(permissions)
        if (installPackagesPermission.isNotEmpty()) {
            val task = object : Task {
                override var next: Task? = null

                override fun req() {
                    taskHead = taskHead?.next
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        showHandleInstallPackagesPermissionDialog()
                    }
                }

                override fun name(): String {
                    return "InstallPackagesPermission"
                }
            }
            addTaskToChain(task)
        }

        // 发起请求
        taskHead?.req()
    }

    private fun addTaskToChain(task: Task) {
        if (taskHead == null) {
            taskHead = task
        }
        // add task to the tail
        taskTail?.next = task
        taskTail = task
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != NORMAL_PERMISSIONS_REQUEST_CODE) {
            return
        }
        val shouldShowRequestPermissionRationale = BooleanArray(permissions.size)
        for (i in permissions.indices) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i])
        }
        taskHead?.req()
        dialog?.dismiss()
        onRequestNormalPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale)
    }

    private fun onRequestNormalPermissionsResult(permissions: Array<String>, grantResults: IntArray, shouldShowRequestPermissionRationale: BooleanArray) {
        var i = 0
        val size = permissions.size
        while (i < size) {
            log("onRequestPermissionsResult  " + permissions[i])
            // Find the corresponding subject
            val subject = mSubjects[permissions[i]]
            if (subject == null) { // No subject found
                Log.e(RxPermissions.TAG, "RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request.")
                return
            }
            mSubjects.remove(permissions[i])
            val granted = grantResults[i] == PackageManager.PERMISSION_GRANTED
            subject.onNext(Permission(permissions[i], granted, shouldShowRequestPermissionRationale[i]))
            subject.onComplete()
            i++
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showHandleInstallPackagesPermissionDialog() {
        val commonDialog = CommonDialog(requireContext())
        commonDialog.setContent("需要您同意安装应用权限才能继续操作")
        commonDialog.setLeftAction("不同意", View.OnClickListener {
            onRequestInstallPackagesPermissionResult()
            commonDialog.dismiss()
        })
        commonDialog.setRightAction("同意", View.OnClickListener {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            intent.data = Uri.parse("package:${requireContext().packageName}")
            startActivityForResult(intent, INSTALL_PACKAGE_PERMISSIONS_REQUEST_CODE)
            commonDialog.dismiss()
        })
        commonDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INSTALL_PACKAGE_PERMISSIONS_REQUEST_CODE) {
            onRequestInstallPackagesPermissionResult()
        }
    }

    /**
     * Handle result of REQUEST_INSTALL_PACKAGES permission request.
     */
    private fun onRequestInstallPackagesPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val subject = mSubjects[Manifest.permission.REQUEST_INSTALL_PACKAGES]
            if (subject == null) { // No subject found
                Log.e(RxPermissions.TAG,
                    "RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request.")
                return
            }
            mSubjects.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            val granted = requireActivity().packageManager.canRequestPackageInstalls()
            subject.onNext(Permission(Manifest.permission.REQUEST_INSTALL_PACKAGES, granted, false))
            subject.onComplete()
        }
        taskHead?.req()
        dialog?.dismiss()
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun isGranted(permission: String?): Boolean {
        if (TextUtils.equals(permission, Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity?.packageManager?.canRequestPackageInstalls() == true
            } else {
                true
            }
        }
        val fragmentActivity = activity ?: return false
        // throw new IllegalStateException("This fragment must be attached to an activity.");
        return fragmentActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun isRevoked(permission: String?): Boolean {
        if (TextUtils.equals(permission, Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            return false
        }
        val fragmentActivity = activity ?: return false
        // throw new IllegalStateException("This fragment must be attached to an activity.");
        return fragmentActivity.packageManager.isPermissionRevokedByPolicy(
            permission,
            requireActivity().packageName
        )
    }

    fun setLogging(logging: Boolean) {
        mLogging = logging
    }

    fun getSubjectByPermission(permission: String): PublishSubject<Permission>? {
        return mSubjects[permission]
    }

    fun containsByPermission(permission: String): Boolean {
        return mSubjects.containsKey(permission)
    }

    fun setSubjectForPermission(permission: String, subject: PublishSubject<Permission>) {
        mSubjects[permission] = subject
    }

    fun log(message: String?) {
        if (mLogging) {
            Log.d(RxPermissions.TAG, message)
        }
    }

    interface Task {
        var next: Task?

        fun req()

        fun name(): String
    }

    companion object {
        private const val NORMAL_PERMISSIONS_REQUEST_CODE = 42
        private const val INSTALL_PACKAGE_PERMISSIONS_REQUEST_CODE = 43
    }
}