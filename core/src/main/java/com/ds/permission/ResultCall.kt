package com.ds.permission

/**
 * 兼容android全版本权限请求结果
 * created by yummylau on 2019/09/04
 */
interface ResultCall {
    fun granted()
    fun denied(never: Boolean)
}

private typealias granted = () -> Unit
private typealias denied = (never: Boolean) -> Unit

class ResultCallBuilder : ResultCall {

    private var granted: granted? = null
    private var denied: denied? = null

    override fun granted() {
        granted?.invoke()
    }

    override fun denied(never: Boolean) {
        denied?.invoke(never)
    }

    fun granted(granted: granted) {
        this.granted = granted
    }

    fun denied(denied: denied) {
        this.denied = denied
    }
}