package com.ds.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ds.permission.core.RxPermissions
import java.util.*

class PermissionManager {

    companion object {
        val instance: PermissionManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { PermissionManager() }

        @JvmStatic
        fun get(): PermissionManager {
            return instance
        }
    }

    private val mLifeActivityPermissions = WeakHashMap<AppCompatActivity, RxPermissions>()
    private val mLifeFragmentPermissions = WeakHashMap<Fragment, RxPermissions>()

    private fun scanForActivity(context: Context?): Activity? {
        return when (context) {
            null -> {
                null
            }
            is Activity -> {
                context
            }
            is ContextWrapper -> {
                scanForActivity(context.baseContext)
            }
            else -> null
        }
    }

    fun inject(context: Context): RxPermissions? {
        val activity = scanForActivity(context)
        return if (activity is AppCompatActivity) {
            inject(activity)
        } else null
    }

    fun inject(activity: AppCompatActivity): RxPermissions {
        if (mLifeActivityPermissions.containsKey(activity) && mLifeActivityPermissions[activity] != null) {
            return mLifeActivityPermissions[activity]!!
        }
        val rxPermissions = RxPermissions(activity)
        mLifeActivityPermissions[activity] = rxPermissions
        return rxPermissions
    }

    fun inject(fragment: Fragment): RxPermissions {
        if (mLifeFragmentPermissions.containsKey(fragment) && mLifeFragmentPermissions[fragment] != null) {
            return mLifeFragmentPermissions[fragment]!!
        }
        val rxPermissions = RxPermissions(fragment)
        mLifeFragmentPermissions[fragment] = rxPermissions
        return rxPermissions
    }

}