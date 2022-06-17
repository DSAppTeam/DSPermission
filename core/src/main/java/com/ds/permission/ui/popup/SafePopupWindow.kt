package com.ds.permission.ui.popup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.PopupWindow
import java.lang.Exception


open class SafePopupWindow : PopupWindow {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}
    constructor() {}
    constructor(contentView: View?) : super(contentView) {}
    constructor(width: Int, height: Int) : super(width, height) {}
    constructor(contentView: View?, width: Int, height: Int) : super(contentView, width, height) {}
    constructor(contentView: View?, width: Int, height: Int, focusable: Boolean) : super(contentView, width, height, focusable) {}

    override fun setContentView(contentView: View) {
        super.setContentView(contentView)
    }

    override fun showAsDropDown(anchor: View) {
        try {
            super.showAsDropDown(anchor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showAsDropDown(anchor: View, xoff: Int, yoff: Int) {
        try {
            super.showAsDropDown(anchor, xoff, yoff)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showAsDropDown(anchor: View, xoff: Int, yoff: Int, gravity: Int) {
        try {
            super.showAsDropDown(anchor, xoff, yoff, gravity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        try {
            super.showAtLocation(parent, gravity, x, y)
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
}