package com.zaitunlabs.zlcore.interfaces;

/**
 * Created by ahsai on 5/27/2018.
 */

public interface LoginCallbackResult{
    public void setSuccess(String token, String name, String phone, String email, String photoUrl);
    public void setFailed();
}
