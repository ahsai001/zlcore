package com.zaitunlabs.zlcore.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahsai on 7/28/2018.
 */

public class ReachLostUtil {
    protected int targetedDoneTotal = 0;
    protected Runnable actionWhenReachTarget;
    protected Runnable actionWhenLostTarget;
    protected List<String> listOfKey;

    public void init(){
        listOfKey.clear();
    }


    public ReachLostUtil(){
        listOfKey = new ArrayList<>();
    }

    public ReachLostUtil setTargetedDoneTotal(int targetedDoneTotal) {
        this.targetedDoneTotal = targetedDoneTotal;
        return this;
    }

    public ReachLostUtil setActionWhenReachTarget(Runnable actionWhenReachTarget) {
        this.actionWhenReachTarget = actionWhenReachTarget;
        return this;
    }

    public ReachLostUtil setActionWhenLostTarget(Runnable actionWhenLostTarget) {
        this.actionWhenLostTarget = actionWhenLostTarget;
        return this;
    }

    public synchronized boolean done(String key){
        if(!listOfKey.contains(key)) {
            listOfKey.add(key);
            if (listOfKey.size() == targetedDoneTotal) {
                actionWhenReachTarget.run();
            }
            return true;
        }
        return false;
    }

    public synchronized boolean unDone(String key){
        if(listOfKey.contains(key)) {
            listOfKey.remove(key);
            if (listOfKey.size() == targetedDoneTotal - 1) {
                actionWhenLostTarget.run();
            }
            return true;
        }
        return false;
    }
}
