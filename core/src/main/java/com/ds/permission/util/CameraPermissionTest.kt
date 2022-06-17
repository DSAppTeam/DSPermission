package com.ds.permission.util

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.os.Build
import android.text.TextUtils
import android.view.SurfaceHolder
import android.view.SurfaceView

class CameraPermissionTest internal constructor(private val mContext: Context) {
    companion object {
        private val PREVIEW_CALLBACK = PreviewCallback { data, camera -> }
        private val CALLBACK: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {}
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        }
    }

    @Throws(Throwable::class)
    fun test(): Boolean {
        val surfaceView = SurfaceView(mContext)
        val holder = surfaceView.holder
        holder.addCallback(CALLBACK)
        var camera: Camera? = null
        return try {
            camera = Camera.open()
            val parameters = camera.parameters
            camera.parameters = parameters
            camera.setPreviewDisplay(holder)
            camera.setPreviewCallback(PREVIEW_CALLBACK)
            camera.startPreview()
            isReallyHasCameraPermission(camera)
        } catch (e: Throwable) {
            val packageManager = mContext.packageManager
            !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
        } finally {
            if (camera != null) {
                camera.stopPreview()
                camera.setPreviewDisplay(null)
                camera.setPreviewCallback(null)
                camera.release()
            }
        }
    }

    private fun isReallyHasCameraPermission(camera: Camera): Boolean {
        return try {
            val manufacturer = Build.MANUFACTURER
            var isVivo = false
            if (!TextUtils.isEmpty(manufacturer) && manufacturer.equals("vivo", ignoreCase = true)) {
                isVivo = true
            }
            if (!isVivo) {
                return true
            }
            val fieldPassword = camera.javaClass.getDeclaredField("mHasPermission")
            fieldPassword.isAccessible = true
            fieldPassword[camera] as Boolean
        } catch (e: Exception) {
            true
        }
    }
}