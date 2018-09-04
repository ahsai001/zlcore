package com.zaitunlabs.zlcore.utils;


import android.text.TextUtils;

/**
 * Created by ahmad s on 9/5/2015.
 */

public class PrefsData {
   private static final String NAME = "name";
   private static final String USERID = "userid";
   private static final String SECRET = "secret";
   private static final String TOKEN = "token";
   private static final String EMAIL = "email";
   private static final String PHONE = "phone";
   private static final String PHOTO = "photo";
   private static final String ISLOGIN = "islogin";
   private static final String LOGINTYPE = "logintype";
   private static final String PUSHY_TOKEN = "pushy_token";
   private static final String PUSHY_TOKEN_SENT = "pushy_token_sent";
   private static final String PUSHY_TOKEN_LOGIN_SENT = "pushy_token_login_sent";


   public static String getName(){
      return Hawk.get(NAME,"");
   }

   public static boolean isAccountLogin(){
      return Hawk.get(ISLOGIN,false) && !TextUtils.isEmpty(getSecret()) && !TextUtils.isEmpty(getToken());
   }

   public static  boolean isLogin(){
      return Hawk.get(ISLOGIN,false);
   }

   public static void setLogout(){
      String userId = getUserID();
      String loginType = getLoginType();
      String pushToken = getPushyToken();
      boolean pushTokenSent = getPushyTokenSent();
      clearAllData();
      setUserID(userId);
      setLoginType(loginType);
      setPushyToken(pushToken);
      setPushyTokenSent(pushTokenSent);
   }

   public static String getPhoto(){
      return Hawk.get(PHOTO, null);
   }

   public static String getPhoto(String defaultValue){
      return Hawk.get(PHOTO, defaultValue);
   }

   public static String getName(String defaultValue){
      return Hawk.get(NAME, defaultValue);
   }

   public static String getUserID(){
      return Hawk.get(USERID,"");
   }

   public static String getUserID(String defaultValue){
      return Hawk.get(USERID, defaultValue);
   }

   public static String getEmail(){
      return Hawk.get(EMAIL,"");
   }

   public static String getEmail(String defaultValue){
      return Hawk.get(EMAIL, defaultValue);
   }

   public static String getPhone(){
      return Hawk.get(PHONE,"");
   }

   public static String getPhone(String defaultValue){
      return Hawk.get(PHONE, defaultValue);
   }

   public static String getToken(){
      return Hawk.get(TOKEN,"");
   }
   public static String getToken(String defaultValue){
      return Hawk.get(TOKEN, defaultValue);
   }


   public static String getSecret(){
      return Hawk.get(SECRET,"");
   }
   public static String getSecret(String defaultValue){
      return Hawk.get(SECRET, defaultValue);
   }

   public static String getPushyToken(){
      return Hawk.get(PUSHY_TOKEN,"");
   }

   public static boolean getPushyTokenSent(){
      return Hawk.get(PUSHY_TOKEN_SENT,true);
   }

   public static boolean getPushyTokenLoginSent(){
      return Hawk.get(PUSHY_TOKEN_LOGIN_SENT,true);
   }

   public static void setName(String value){
      Hawk.put(NAME,value);
   }

   public static void setUserID(String value){
      Hawk.put(USERID,value);
   }

   public static void setEmail(String value){
      Hawk.put(EMAIL,value);
   }
   public static void setPhone(String value){
      Hawk.put(PHONE,value);
   }

   public static void setSecret(String value){
      Hawk.put(SECRET,value);
   }

   public static void setToken(String value){
      Hawk.put(TOKEN,value);
   }

   public static void setLogin(boolean value){
      Hawk.put(ISLOGIN,value);
   }


   public static String getLoginType() {
      return Hawk.get(LOGINTYPE,"");
   }

   public static void setLoginType(String loginType) {
      Hawk.put(LOGINTYPE,loginType);
   }



   public static void setPhoto(String value){
      Hawk.put(PHOTO,value);
   }

   public static void setPushyToken(String value){
      Hawk.put(PUSHY_TOKEN,value);
   }
   public static void setPushyTokenSent(boolean value){
      Hawk.put(PUSHY_TOKEN_SENT,value);
   }


   public static void setPushyTokenLoginSent(boolean value){
      Hawk.put(PUSHY_TOKEN_LOGIN_SENT,value);
   }


   public static void clearAllData(){
      Hawk.remove(NAME);
      Hawk.remove(USERID);
      Hawk.remove(EMAIL);
      Hawk.remove(PHONE);
      Hawk.remove(PHOTO);
      Hawk.remove(SECRET);
      Hawk.remove(TOKEN);
      Hawk.remove(ISLOGIN);
      Hawk.remove(LOGINTYPE);
      Hawk.remove(PUSHY_TOKEN);
      Hawk.remove(PUSHY_TOKEN_SENT);
      Hawk.remove(PUSHY_TOKEN_LOGIN_SENT);
   }


}
