package com.zaitunlabs.zlcore.utils


/**
 * Created by ahsai on 7/28/2018.
 */

abstract class ReachLostUtils {
    protected var targetedDoneTotal = 0
    protected var progressDoneTotal = 0
    protected var actionWhenReachTarget: Runnable
    protected var actionWhenLostTarget: Runnable
    abstract fun init()

    fun setTargetedDoneTotal(targetedDoneTotal: Int): ReachLostUtils {
        this.targetedDoneTotal = targetedDoneTotal
        return this
    }

    fun setActionWhenReachTarget(actionWhenReachTarget: Runnable): ReachLostUtils {
        this.actionWhenReachTarget = actionWhenReachTarget
        return this
    }

    fun setActionWhenLostTarget(actionWhenLostTarget: Runnable): ReachLostUtils {
        this.actionWhenLostTarget = actionWhenLostTarget
        return this
    }

    fun done(): Boolean {
        if (progressDoneTotal + 1 <= targetedDoneTotal) {
            progressDoneTotal++
            if (progressDoneTotal == targetedDoneTotal) {
                actionWhenReachTarget.run()
            }
            return true
        }
        return false
    }

    fun unDone(): Boolean {
        if (progressDoneTotal - 1 >= 0) {
            progressDoneTotal--
            if (progressDoneTotal == targetedDoneTotal - 1) {
                actionWhenLostTarget.run()
            }
            return true
        }
        return false
    }
}
