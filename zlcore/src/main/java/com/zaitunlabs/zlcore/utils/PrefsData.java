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
      return Lookup.getS(NAME,"");
   }

   public static boolean isAccountLoggedIn(){
      return isLoggedIn() && !TextUtils.isEmpty(getToken());
   }

   public static  boolean isLoggedIn(){
      return Lookup.getS(ISLOGIN,false);
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
      return Lookup.getS(PHOTO, null);
   }

   public static String getPhoto(String defaultValue){
      return Lookup.getS(PHOTO, defaultValue);
   }

   public static String getName(String defaultValue){
      return Lookup.getS(NAME, defaultValue);
   }

   public static String getUserID(){
      return Lookup.getS(USERID,"");
   }

   public static String getUserID(String defaultValue){
      return Lookup.getS(USERID, defaultValue);
   }

   public static String getEmail(){
      return Lookup.getS(EMAIL,"");
   }

   public static String getEmail(String defaultValue){
      return Lookup.getS(EMAIL, defaultValue);
   }

   public static String getPhone(){
      return Lookup.getS(PHONE,"");
   }

   public static String getPhone(String defaultValue){
      return Lookup.getS(PHONE, defaultValue);
   }

   public static String getToken(){
      return Lookup.getS(TOKEN,"");
   }
   public static String getToken(String defaultValue){
      return Lookup.getS(TOKEN, defaultValue);
   }


   public static String getSecret(){
      return Lookup.getS(SECRET,"");
   }
   public static String getSecret(String defaultValue){
      return Lookup.getS(SECRET, defaultValue);
   }

   public static String getPushyToken(){
      return Lookup.getS(PUSHY_TOKEN,"");
   }

   public static boolean getPushyTokenSent(){
      return Lookup.getS(PUSHY_TOKEN_SENT,true);
   }

   public static boolean getPushyTokenLoginSent(){
      return Lookup.getS(PUSHY_TOKEN_LOGIN_SENT,true);
   }

   public static void setName(String value){
      Lookup.setS(NAME,value);
   }

   public static void setUserID(String value){
      Lookup.setS(USERID,value);
   }

   public static void setEmail(String value){
      Lookup.setS(EMAIL,value);
   }
   public static void setPhone(String value){
      Lookup.setS(PHONE,value);
   }

   public static void setSecret(String value){
      Lookup.setS(SECRET,value);
   }

   public static void setToken(String value){
      Lookup.setS(TOKEN,value);
   }

   public static void setLogin(boolean value){
      Lookup.setS(ISLOGIN,value);
   }


   public static String getLoginType() {
      return Lookup.getS(LOGINTYPE,"");
   }

   public static void setLoginType(String loginType) {
      Lookup.setS(LOGINTYPE,loginType);
   }



   public static void setPhoto(String value){
      Lookup.setS(PHOTO,value);
   }

   public static void setPushyToken(String value){
      Lookup.setS(PUSHY_TOKEN,value);
   }
   public static void setPushyTokenSent(boolean value){
      Lookup.setS(PUSHY_TOKEN_SENT,value);
   }


   public static void setPushyTokenLoginSent(boolean value){
      Lookup.setS(PUSHY_TOKEN_LOGIN_SENT,value);
   }


   public static void clearAllData(){
      Lookup.removeS(NAME);
      Lookup.removeS(USERID);
      Lookup.removeS(EMAIL);
      Lookup.removeS(PHONE);
      Lookup.removeS(PHOTO);
      Lookup.removeS(SECRET);
      Lookup.removeS(TOKEN);
      Lookup.removeS(ISLOGIN);
      Lookup.removeS(LOGINTYPE);
      Lookup.removeS(PUSHY_TOKEN);
      Lookup.removeS(PUSHY_TOKEN_SENT);
      Lookup.removeS(PUSHY_TOKEN_LOGIN_SENT);
   }


}
