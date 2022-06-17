package com.ds.permission.ui.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog

open class SafeAppCompatDialog(context: Context?, theme: Int) : AppCompatDialog(context, theme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun show() {
        try {
            super.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun cancel() {
        try {
            super.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}