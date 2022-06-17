package com.ds.permission.core

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.ds.permission.MultiResultCall
import com.ds.permission.PermissionConfig
import com.ds.permission.ResultCall
import com.ds.permission.ResultCallBuilder
import com.ds.permission.ui.popup.OverlayPopup
import com.ds.permission.util.PermissionUtils
import com.ds.permission.util.PermissionDialogUtil
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.subjects.PublishSubject
import java.lang.ref.WeakReference

class RxPermissions : LifecycleObserver {

    private val mDisposables: MutableList<Disposable> = ArrayList()

    @VisibleForTesting
    var mRxPermissionsFragment: Lazy<WeakReference<RxPermissionsFragment>>

    private var mLifeActivityWeakReference: WeakReference<AppCompatActivity>? = null
    private var mLifeFragmentWeakReference: WeakReference<Fragment>? = null

    constructor(fragment: Fragment) {
        mRxPermissionsFragment = getLazySingleton(WeakReference(fragment.childFragmentManager))
        mLifeFragmentWeakReference = WeakReference(fragment)
        fragment.lifecycle.addObserver(this)
    }

    constructor(activity: AppCompatActivity) {
        mRxPermissionsFragment = getLazySingleton(WeakReference(activity.supportFragmentManager))
        mLifeActivityWeakReference = WeakReference(activity)
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        destroy()
    }

    private fun getActivity(): Activity? {
        return if (mLifeActivityWeakReference == null) mLifeFragmentWeakReference?.get()?.activity else mLifeActivityWeakReference?.get()
    }

    private fun getLazySingleton(fragmentManager: WeakReference<FragmentManager>): Lazy<WeakReference<RxPermissionsFragment>> {
        return object : Lazy<WeakReference<RxPermissionsFragment>> {
            private var rxPermissionsFragmentWeakReference: WeakReference<RxPermissionsFragment>? = null

            @Synchronized
            override fun get(): WeakReference<RxPermissionsFragment> {
                if (rxPermissionsFragmentWeakReference == null || rxPermissionsFragmentWeakReference!!.get() == null) {
                    rxPermissionsFragmentWeakReference = getRxPermissionsFragment(fragmentManager)
                }
                return rxPermissionsFragmentWeakReference!!
            }
        }
    }

    private fun getRxPermissionsFragment(fragmentManager: WeakReference<FragmentManager>): WeakReference<RxPermissionsFragment> {
        var rxPermissionsFragment = findRxPermissionsFragment(fragmentManager.get())
        val isNewInstance = rxPermissionsFragment == null
        if (isNewInstance) {
            rxPermissionsFragment = RxPermissionsFragment()
            fragmentManager.get()
                ?.beginTransaction()
                ?.add(rxPermissionsFragment, TAG)
                ?.commitNowAllowingStateLoss()
        }
        return WeakReference(rxPermissionsFragment!!)
    }

    private fun findRxPermissionsFragment(fragmentManager: FragmentManager?): RxPermissionsFragment? {
        return fragmentManager?.findFragmentByTag(TAG) as RxPermissionsFragment?
    }

    fun setLogging(logging: Boolean) {
        mRxPermissionsFragment.get().get()?.setLogging(logging)
    }

    fun request(function: ResultCallBuilder.() -> Unit, config: PermissionConfig) {
        request(ResultCallBuilder().also(function), config)
    }

    fun request(resultCall: ResultCall, config: PermissionConfig) {
        val disposable = requestEachCombined(config)
            .subscribe { item: Permission ->
            when {
                item.granted -> {
                    resultCall.granted()
                }
                item.shouldShowRequestPermissionRationale -> {
                    resultCall.denied(false)
                }
                else -> {
                    PermissionDialogUtil.checkAndShowSettingDialog(getActivity(), config)
                    resultCall.denied(true)
                }
            }
        }
        mDisposables.add(disposable)
    }

    /**
     * 传递多个权限判断，结果回调多次
     *
     * @param multiResultCall void granted(String permission); permission可以通过传递参数判断对应哪个权限通过
     * void denied(String permission, boolean never);  permission可以通过传递参数判断对应哪个权限被拒绝
     */
    fun request(multiResultCall: MultiResultCall, config: PermissionConfig) {
        val disposable = requestEach(config).subscribe { permission: Permission ->
            when {
                permission.granted -> {
                    multiResultCall.multiGranted(permission.name)
                }
                permission.shouldShowRequestPermissionRationale -> {
                    multiResultCall.multiDenied(permission.name, false)
                }
                else -> {
                    PermissionDialogUtil.checkAndShowSettingDialog(getActivity(), config)
                    multiResultCall.multiDenied(permission.name, true)
                }
            }
        }
        mDisposables.add(disposable)
    }

    fun destroy() {
        for (disposable in mDisposables) {
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }
        mDisposables.clear()
        mLifeActivityWeakReference?.get()?.lifecycle?.removeObserver(this)
        mLifeFragmentWeakReference?.get()?.lifecycle?.removeObserver(this)
    }

    /**
     * Map emitted items from the source observable into `true` if permissions in parameters
     * are granted, or `false` if not.
     *
     *
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    private fun <T> ensure(permissionConfig: PermissionConfig): ObservableTransformer<T, Boolean> {
        return ObservableTransformer { o ->
            request(
                o,
                permissionConfig
            ) // Transform Observable<Permission> to Observable<Boolean>
                .buffer(permissionConfig.permissions.size)
                .flatMap(Function<List<Permission>, ObservableSource<Boolean>> { permissions ->
                    if (permissions.isEmpty()) { // Occurs during orientation change, when the subject receives onComplete.
// In that case we don't want to propagate that empty list to the
// subscriber, only the onComplete.
                        return@Function Observable.empty<Boolean>()
                    }
                    // Return true if all permissions are granted.
                    for (p in permissions) {
                        if (!p.granted) {
                            return@Function Observable.just(false)
                        }
                    }
                    Observable.just(true)
                })
        }
    }

    /**
     * Map emitted items from the source observable into [Permission] objects for each
     * permission in parameters.
     *
     *
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    private fun <T> ensureEach(permissionConfig: PermissionConfig): ObservableTransformer<T, Permission> {
        return ObservableTransformer { o -> request(o, permissionConfig) }
    }

    /**
     * Map emitted items from the source observable into one combined [Permission] object. Only if all permissions are granted,
     * permission also will be granted. If any permission has `shouldShowRationale` checked, than result also has it checked.
     *
     *
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    private fun <T> ensureEachCombined(permissionConfig: PermissionConfig): ObservableTransformer<T, Permission> {
        return ObservableTransformer { o ->
            request(o, permissionConfig)
                .buffer(permissionConfig.permissions.size)
                .flatMap { permissions ->
                    if (permissions.isEmpty()) {
                        Observable.empty()
                    } else Observable.just(
                        Permission(permissions)
                    )
                }
        }
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    private fun request(permissionConfig: PermissionConfig): Observable<Boolean> {
        return Observable.just(TRIGGER)
            .compose(ensure(permissionConfig))
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    private fun requestEach(permissionConfig: PermissionConfig): Observable<Permission> {
        return Observable.just(TRIGGER)
            .compose(ensureEach(permissionConfig))
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    private fun requestEachCombined(permissionConfig: PermissionConfig): Observable<Permission> {
        return Observable.just(TRIGGER)
            .compose(ensureEachCombined(permissionConfig))
    }

    private fun request(
        trigger: Observable<*>,
        permissionConfig: PermissionConfig
    ): Observable<Permission> {
        require(permissionConfig.permissions.isNotEmpty()) { "RxPermissions.request/requestEach requires at least one input permission" }
        return oneOf(trigger, pending(permissionConfig.permissions))
            .flatMap {
                requestImplementation(permissionConfig)
            }
    }

    private fun pending(permissions: ArrayList<String>): Observable<*> {
        for (p in permissions) {
            val fragment = mRxPermissionsFragment.get().get()
            if (fragment != null && !fragment.containsByPermission(p)) {
                return Observable.empty<Any>()
            }
        }
        return Observable.just(TRIGGER)
    }

    private fun oneOf(
        trigger: Observable<*>?,
        pending: Observable<*>
    ): Observable<*> {
        return if (trigger == null) {
            Observable.just(TRIGGER)
        } else Observable.merge(trigger, pending)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestImplementation(permissionConfig: PermissionConfig): Observable<Permission> {
        val list: MutableList<Observable<Permission>> = ArrayList(permissionConfig.permissions.size)
        val unrequestedPermissions: MutableList<String?> = java.util.ArrayList()
        // In case of multiple permissions, we create an Observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (permission in permissionConfig.permissions) {
            if (isGranted(permission)) { // Already granted, or not Android M, Return a granted Permission object.
                list.add(
                    Observable.just(
                        Permission(
                            permission,
                            granted = true,
                            shouldShowRequestPermissionRationale = false
                        )
                    )
                )
                continue
            }
            if (isRevoked(permission)) { // Revoked by a policy, return a denied Permission object.
                list.add(
                    Observable.just(
                        Permission(
                            permission,
                            granted = false,
                            shouldShowRequestPermissionRationale = false
                        )
                    )
                )
                continue
            }
            var subject = mRxPermissionsFragment.get().get()?.getSubjectByPermission(permission)
            // Create a new subject if not exists
            if (subject == null) {
                unrequestedPermissions.add(permission)
                subject = PublishSubject.create()
                mRxPermissionsFragment.get().get()?.setSubjectForPermission(permission, subject)
            }
            list.add(subject)
        }
        if (unrequestedPermissions.isNotEmpty()) {
            val unrequestedPermissionsArray = unrequestedPermissions.toTypedArray()

            val activity = getActivity()
            if (activity != null && (permissionConfig.isShowInstructionDialog)) {
                if (mRxPermissionsFragment.get().get()?.dialog != null) {
                    mRxPermissionsFragment.get().get()?.dialog?.dismiss()
                    mRxPermissionsFragment.get().get()?.dialog = null
                }
                mRxPermissionsFragment.get().get()?.dialog = OverlayPopup.build(activity)
                mRxPermissionsFragment.get().get()?.dialog?.initData(
                    PermissionDialogUtil.getDialogTitle(activity, unrequestedPermissionsArray),
                    if (TextUtils.isEmpty(permissionConfig.explanation))
                        PermissionDialogUtil.getDialogContent(activity, unrequestedPermissionsArray)
                    else
                        permissionConfig.explanation
                )
                mRxPermissionsFragment.get().get()?.dialog?.showDelay(500)
            }

            requestPermissionsFromFragment(unrequestedPermissionsArray)
        }
        return Observable.concat(Observable.fromIterable(list))
    }

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps
     * the returned value in an observable.
     *
     *
     * In case of multiple permissions, only emits true if
     * Activity.shouldShowRequestPermissionRationale returned true for
     * all revoked permissions.
     *
     *
     * You shouldn't call this method if all permissions have been granted.
     *
     *
     * For SDK &lt; 23, the observable will always emit false.
     */
    fun shouldShowRequestPermissionRationale(
        activity: Activity,
        vararg permissions: String
    ): Observable<Boolean> {
        return if (!isMarshmallow) {
            Observable.just(false)
        } else Observable.just(
            shouldShowRequestPermissionRationaleImplementation(activity, *permissions)
        )
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun shouldShowRequestPermissionRationaleImplementation(
        activity: Activity,
        vararg permissions: String
    ): Boolean {
        for (p in permissions) {
            if (!isGranted(p) && !activity.shouldShowRequestPermissionRationale(p)) {
                return false
            }
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsFromFragment(permissions: Array<String?>?) {
        mRxPermissionsFragment.get().get()
            ?.log("requestPermissionsFromFragment " + TextUtils.join(", ", permissions))
        mRxPermissionsFragment.get().get()?.requestPermissions(permissions)
    }

    /**
     * Returns true if the permission is already granted.
     *
     *
     * Always true if SDK &lt; 23.
     */
    private fun isGranted(permission: String): Boolean {
        return PermissionUtils.check(getActivity(), permission)
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     *
     *
     * Always false if SDK &lt; 23.
     */
    private fun isRevoked(permission: String?): Boolean {
        val fragment = mRxPermissionsFragment.get().get()
        return isMarshmallow && (fragment != null && fragment.isRevoked(permission))
    }

    val isMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    @FunctionalInterface
    interface Lazy<V> {
        fun get(): V
    }

    companion object {
        val TAG = RxPermissions::class.java.simpleName
        val TRIGGER = Any()
    }
}