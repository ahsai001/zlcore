package com.zaitunlabs.zlcore.api;

/**
 * Created by ahsai on 6/9/2017.
 */

public class APIResponse {
    public static class HTTPCode{
        public static final int OK = 200;
        public static final int INVALID_METHOD = 405;
    }

    public static class GENERIC_RESPONSE{
        public static int OK = 1;
        public static int NEED_UPDATE = 2;
        public static int NEED_SHOW_MESSAGE = 3;
        public static int FAILED = -1;
        public static int NEED_LOGIN = -10;
    }
}
