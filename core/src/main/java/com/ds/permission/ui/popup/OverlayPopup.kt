package com.ds.permission.ui.popup

import android.app.Activity
import android.widget.TextView
import android.annotation.SuppressLint
import com.ds.permission.R
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Message
import android.view.*

class OverlayPopup(private val mActivity: Activity?) : SafePopupWindow(mActivity) {
    private val mContentView: View
    private lateinit var mTvTitle: TextView
    private lateinit var mTvContent: TextView

    private var mTitle: String? = null
    private var mContent: String? = null

    companion object {
        private const val DELAY_MSG = 1000

        fun build(activity: Activity?): OverlayPopup {
            return OverlayPopup(activity)
        }
    }

    init {
        mContentView = LayoutInflater.from(mActivity)
            .inflate(R.layout.libpermission_dialog_overlay, null, false)
        initViews()
        this.contentView = mContentView
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.isFocusable = false
        this.setBackgroundDrawable(ColorDrawable(0x00000000))
        this.isOutsideTouchable = false
        this.animationStyle = R.style.libpermission_PopupWindowAnimStyle
    }

    @SuppressLint("HandlerLeak")
    private val mAutoShowHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == DELAY_MSG) {
                show()
            }
        }
    }

    private fun initViews() {
        mTvTitle = mContentView.findViewById(R.id.title)
        mTvContent = mContentView.findViewById(R.id.content)
        mContentView.findViewById<View>(R.id.root).setOnClickListener { dismiss() }
    }

    fun initData(title: String?, content: String?) {
        mTitle = title
        mContent = content
        mTvTitle.text = mTitle
        mTvContent.text = mContent
    }

    fun showDelay(delayMillis: Long) {
        if (delayMillis > 0) {
            scheduleAutoShow(delayMillis)
        } else {
            show()
        }
    }

    fun show() {
        if (mActivity == null || mActivity.window == null) {
            return
        }
        showAtLocation(mActivity.window.decorView, Gravity.TOP, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun scheduleAutoShow(delayMillis: Long) {
        mAutoShowHandler.removeMessages(DELAY_MSG)
        val msg = mAutoShowHandler.obtainMessage(DELAY_MSG)
        mAutoShowHandler.sendMessageDelayed(msg, delayMillis)
    }

    override fun dismiss() {
        mAutoShowHandler.removeMessages(DELAY_MSG)
        if (isShowing) {
            super.dismiss()
        }
    }

}