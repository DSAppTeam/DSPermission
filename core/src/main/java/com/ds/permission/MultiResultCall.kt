package com.ds.permission

interface MultiResultCall {
    fun multiGranted(permission: String?)
    fun multiDenied(permission: String?, never: Boolean)
}

private typealias multiGranted = (permission: String?) -> Unit
private typealias multiDenied = (permission: String?, never: Boolean) -> Unit

class MultiResultCallBuilder : MultiResultCall {

    private var multiGranted: multiGranted? = null
    private var multiDenied: multiDenied? = null

    override fun multiGranted(permission: String?) {
        multiGranted?.invoke(permission)
    }

    override fun multiDenied(permission: String?, never: Boolean) {
        multiDenied?.invoke(permission, never)
    }

    fun granted(multiGranted: multiGranted) {
        this.multiGranted = multiGranted
    }

    fun denied(multiDenied: multiDenied) {
        this.multiDenied = multiDenied
    }
}