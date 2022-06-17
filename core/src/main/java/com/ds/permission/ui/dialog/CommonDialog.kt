package com.ds.permission.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ds.permission.R
import com.ds.permission.databinding.LibpermissionDialogCommonBinding

class CommonDialog : SafeAppCompatDialog {
    var which = WHICH_NONE

    private lateinit var mBinding: LibpermissionDialogCommonBinding

    constructor(context: Context) : super(context, R.style.libpermission_easy_dialog_style) {
        init(context)
    }

    constructor(context: Context, theme: Int) : super(context, theme) {
        init(context)
    }

    private fun init(context: Context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.libpermission_dialog_common, null, false)
        setContentView(mBinding.root)
        window?.let {
            it.setGravity(Gravity.CENTER)
            it.setWindowAnimations(R.style.libpermission_bottomDialogStyle)
            val lp = it.attributes
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            it.attributes = lp
        }
        setCanceledOnTouchOutside(true)
    }

    //fix Android10部分手机 后台弹窗只显示蒙层，不显示布局
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (Build.VERSION.SDK_INT >= 29 && hasFocus) {
            if (hasFocus && window != null && window.decorView != null) {
                val decorView = window.decorView
                if (decorView.height == 0 || decorView.width == 0) {
                    decorView.requestLayout()
                }
            }
        }
    }

    fun setContent(resId: Int) {
        setContent(context.getString(resId))
    }

    fun setContent(content: CharSequence?) {
        mBinding.tvContent.text = content
        mBinding.tvContent.visibility = View.VISIBLE
    }

    fun setContentSingLine() {
        mBinding.tvContent.setSingleLine()
    }

    fun setContent(view: View?) {
        mBinding.llContent.removeAllViews()
        mBinding.llContent.addView(view)
    }

    fun setContentTextSize(size: Int) {
        mBinding.tvContent.textSize = size.toFloat()
    }

    fun setContentAction(content: CharSequence?, @ColorRes color: Int, onClickListener: View.OnClickListener?) {
        mBinding.tvContent.setOnClickListener(onClickListener)
        mBinding.tvContent.text = content
        mBinding.tvContent.visibility = View.VISIBLE
        mBinding.tvContent.setTextColor(ContextCompat.getColor(context, color))
    }

    fun setLeftAction(resId: Int, onClickListener: View.OnClickListener?) {
        setLeftAction(context.getString(resId), onClickListener)
    }

    fun setLeftAction(leftText: CharSequence?, onClickListener: View.OnClickListener?) {
        mBinding.layoutLeft.visibility = View.VISIBLE
        if (onClickListener == null) {
            mBinding.layoutLeft.setOnClickListener {
                which = WHICH_LEFT
                dismiss()
            }
        } else {
            mBinding.layoutLeft.setOnClickListener(onClickListener)
        }
        mBinding.tvLeft.text = leftText
        mBinding.divider.visibility = View.GONE
    }

    fun setSingleAction(resId: Int, @ColorRes color: Int, onClickListener: View.OnClickListener?) {
        setSingleAction(context.getString(resId), color, onClickListener)
    }

    fun setSingleAction(leftText: CharSequence?, @ColorRes color: Int, onClickListener: View.OnClickListener?) {
        mBinding.layoutLeft.visibility = View.VISIBLE
        if (onClickListener == null) {
            mBinding.layoutLeft.setOnClickListener {
                which = WHICH_LEFT
                dismiss()
            }
        } else {
            mBinding.layoutLeft.setOnClickListener(onClickListener)
        }
        mBinding.tvLeft.text = leftText
        mBinding.tvLeft.setTextColor(ContextCompat.getColor(context, color))
        mBinding.divider.visibility = View.GONE
    }

    fun setRightAction(resId: Int, onClickListener: View.OnClickListener?) {
        setRightAction(context.getString(resId), onClickListener)
    }

    fun setRightAction(rightText: CharSequence?, onClickListener: View.OnClickListener?) {
        mBinding.layoutRight.visibility = View.VISIBLE
        mBinding.layoutRight.setOnClickListener(onClickListener)
        mBinding.tvRight.text = rightText
        mBinding.divider.visibility = View.VISIBLE
    }

    fun hideRightAction() {
        mBinding.layoutRight.visibility = View.GONE
        mBinding.divider.visibility = View.GONE
    }

    fun showRightAction() {
        mBinding.layoutRight.visibility = View.VISIBLE
        mBinding.divider.visibility = View.VISIBLE
    }

    fun setRightAction(rightText: CharSequence?, @ColorRes color: Int, onClickListener: View.OnClickListener?) {
        mBinding.layoutRight.visibility = View.VISIBLE
        mBinding.layoutRight.setOnClickListener(onClickListener)
        mBinding.tvRight.text = rightText
        mBinding.tvRight.setTextColor(ContextCompat.getColor(context, color))
        mBinding.divider.visibility = View.VISIBLE
    }

    fun setRightAction(resId: Int, @ColorRes color: Int, onClickListener: View.OnClickListener?) {
        setRightAction(context.getString(resId), color, onClickListener)
    }

    override fun setTitle(resId: Int) {
        mBinding.tvTitle.setText(resId)
        mBinding.tvTitle.visibility = View.VISIBLE
    }

    override fun setTitle(text: CharSequence) {
        mBinding.tvTitle.text = text
        mBinding.tvTitle.visibility = View.VISIBLE
    }

    fun setTitleTextSize(size: Int) {
        mBinding.tvTitle.textSize = size.toFloat()
    }

    fun hideRight() {
        if (mBinding.layoutRight.visibility != View.GONE) {
            mBinding.layoutRight.visibility = View.GONE
            mBinding.divider.visibility = View.GONE
        }
    }

    fun hideLeft() {
        if (mBinding.layoutLeft.visibility != View.GONE) {
            mBinding.layoutLeft.visibility = View.GONE
            mBinding.divider.visibility = View.GONE
        }
    }

    fun hideTitle() {
        if (mBinding.tvTitle.visibility != View.GONE) {
            mBinding.tvTitle.visibility = View.GONE
        }
    }

    fun setCloseIconVisible(visible: Boolean) {
        mBinding.ivClose.visibility = if (visible) View.VISIBLE else View.GONE
        mBinding.ivClose.setOnClickListener { dismiss() }
    }

    fun buildAsAlerter(title: CharSequence, text: CharSequence?, singleText: String?, cancelOutside: Boolean) {
        buildAsAlerter(title, text, singleText, null, cancelOutside)
    }

    fun buildAsAlerter(title: CharSequence, text: CharSequence?, singleText: String?, listener: View.OnClickListener?, cancelOutside: Boolean) {
        setTitle(title)
        setContent(text)
        if (listener == null) {
            setLeftAction(singleText, View.OnClickListener { dismiss() })
        } else {
            setLeftAction(singleText, listener)
        }
        hideRight()
        setCanceledOnTouchOutside(cancelOutside)
        setOnCancelListener(null)
    }

    fun buildAsAlerter(text: CharSequence?, singleText: String?, cancelOutside: Boolean) {
        buildAsAlerter(text, singleText, cancelOutside, null)
    }

    @JvmOverloads
    fun buildAsAlerter(text: CharSequence?, singleText: CharSequence?, cancelOutside: Boolean, onClickListener: View.OnClickListener?, listener: DialogInterface.OnCancelListener? = null) {
        setContent(text)
        hideTitle()
        if (onClickListener == null) {
            setLeftAction(singleText, View.OnClickListener { dismiss() })
        } else {
            setLeftAction(singleText, onClickListener)
        }
        hideRight()
        setCanceledOnTouchOutside(cancelOutside)
        setOnCancelListener(listener)
    }

    fun buildAsAlerter(text: CharSequence?, leftText: CharSequence?, rightText: CharSequence?, cancelOutside: Boolean, rightOnClickListener: View.OnClickListener?) {
        buildAsAlerter(text, leftText, null, rightText, rightOnClickListener, cancelOutside)
    }

    fun buildAsAlerter(text: CharSequence?, leftText: CharSequence?, leftOnClickListener: View.OnClickListener?, rightText: CharSequence?, rightOnClickListener: View.OnClickListener?, cancelOutside: Boolean) {
        setContent(text)
        hideTitle()
        if (leftOnClickListener == null) {
            setLeftAction(leftText, View.OnClickListener { dismiss() })
        } else {
            setLeftAction(leftText, leftOnClickListener)
        }
        setRightAction(rightText, rightOnClickListener)
        setCanceledOnTouchOutside(cancelOutside)
        setOnCancelListener(null)
    }

    fun buildAsAlerter(text: CharSequence?, leftText: CharSequence?, leftOnClickListener: View.OnClickListener?, rightText: CharSequence?, @ColorRes rightTextColor: Int, rightOnClickListener: View.OnClickListener?, cancelOutside: Boolean) {
        setContent(text)
        hideTitle()
        if (leftOnClickListener == null) {
            setLeftAction(leftText, View.OnClickListener { dismiss() })
        } else {
            setLeftAction(leftText, leftOnClickListener)
        }
        setRightAction(rightText, rightTextColor, rightOnClickListener)
        setCanceledOnTouchOutside(cancelOutside)
        setOnCancelListener(null)
    }

    fun buildAsAlerter(text: CharSequence?, leftText: CharSequence?, leftOnClickListener: View.OnClickListener?, rightText: CharSequence?, rightOnClickListener: View.OnClickListener?, cancelOutside: Boolean, onCancelListener: DialogInterface.OnCancelListener?) {
        setContent(text)
        hideTitle()
        setLeftAction(leftText, leftOnClickListener)
        setRightAction(rightText, rightOnClickListener)
        setCanceledOnTouchOutside(cancelOutside)
        setOnCancelListener(onCancelListener)
    }

    @JvmOverloads
    fun buildAsAlerter(title: CharSequence, text: CharSequence?, leftText: CharSequence?, leftOnClickListener: View.OnClickListener?, rightText: CharSequence?, rightOnClickListener: View.OnClickListener?, cancelOutside: Boolean, onCancelListener: DialogInterface.OnCancelListener? = null) {
        setTitle(title)
        setContent(text)
        setLeftAction(leftText, leftOnClickListener)
        setRightAction(rightText, rightOnClickListener)
        setCanceledOnTouchOutside(cancelOutside)
        onCancelListener?.let { setOnCancelListener(it) }
    }

    companion object {
        const val WHICH_NONE = -1
        const val WHICH_LEFT = 0
        const val WHICH_RIGHT = 1
    }
}