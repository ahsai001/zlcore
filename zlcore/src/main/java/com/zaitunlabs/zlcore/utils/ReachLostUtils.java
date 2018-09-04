package com.zaitunlabs.zlcore.utils;

/**
 * Created by ahsai on 7/28/2018.
 */

public abstract class ReachLostUtils {
    protected int targetedDoneTotal = 0;
    protected int progressDoneTotal = 0;
    protected Runnable actionWhenReachTarget;
    protected Runnable actionWhenLostTarget;
    public abstract void init();


    public ReachLostUtils(){
    }

    public ReachLostUtils setTargetedDoneTotal(int targetedDoneTotal) {
        this.targetedDoneTotal = targetedDoneTotal;
        return this;
    }

    public ReachLostUtils setActionWhenReachTarget(Runnable actionWhenReachTarget) {
        this.actionWhenReachTarget = actionWhenReachTarget;
        return this;
    }

    public ReachLostUtils setActionWhenLostTarget(Runnable actionWhenLostTarget) {
        this.actionWhenLostTarget = actionWhenLostTarget;
        return this;
    }

    public boolean done(){
        if(progressDoneTotal +1 <= targetedDoneTotal) {
            progressDoneTotal++;
            if (progressDoneTotal == targetedDoneTotal) {
                actionWhenReachTarget.run();
            }
            return true;
        }
        return false;
    }

    public boolean unDone(){
        if(progressDoneTotal -1 >= 0) {
            progressDoneTotal--;
            if (progressDoneTotal == targetedDoneTotal -1) {
                actionWhenLostTarget.run();
            }
            return true;
        }
        return false;
    }
}
