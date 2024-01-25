package com.zaitunlabs.zlcore.views;

/**
 * Created by ahmad s on 13/09/20.
 */
public interface TalkyCounter {

    /**
     * Reset the counter.
     */
    void reset();

    /**
     * Increment the counter.
     */
    void increment();

    /**
     * @return The current count of the counter.
     */
    int getCount();

    /**
     * Set the counter value.
     */
    void setCount(int count);

}
