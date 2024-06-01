package com.example.notetaking.function;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionPref {

    public static final String USER_PREFS = "Taking_notes";
    public SharedPreferences appSharedPref;
    public SharedPreferences.Editor prefEditor;

    public SessionPref(Context context) {
        appSharedPref = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
    }

    public String IsLogin = "IsLogin";
    public final String UserEmail = "UserEmail";
    public final String UserProviderId = "UserProviderId";
    public final String UserPassword = "UserPassword";
    public final String SwitchSideType = "SwitchSideType";

    public String getString(String key) {
        return appSharedPref.getString(key, "");
    }

    public void setString(String key, String value) {
        prefEditor = appSharedPref.edit();
        prefEditor.putString(key, value).apply();
    }

    public void removeKey(String key) {
        prefEditor = appSharedPref.edit();
        prefEditor.remove(key).apply();
    }

    public boolean getBoolean(String key) {
        return appSharedPref.getBoolean(key, false);
    }

    public void setBoolean(String key, boolean val) {
        prefEditor = appSharedPref.edit();
        prefEditor.putBoolean(key, val);
        prefEditor.commit();
    }

    public boolean isLogin() {
        return getBoolean(IsLogin);
    }

    public void setIsLogin(boolean isLogin) {
        setBoolean(IsLogin, isLogin);
    }


    public boolean isSideOrganization() {
        return getBoolean(SwitchSideType);
    }

    public void setIsSideOrganization(boolean switchSideType) {
        setBoolean(SwitchSideType, switchSideType);
    }


    public void setUserEmail(String userEmail) {
        setString(UserEmail, userEmail);
    }

    public String getUserEmail() {
        return getString(UserEmail);
    }

    public void setUserProviderId(String userProviderId) {
        setString(UserProviderId, userProviderId);
    }

    public String getUserProviderId() {
        return getString(UserProviderId);
    }

    public void setUserPassword(String userPassword) {//encrypted store
        setString(UserPassword, userPassword);
    }

    public String getUserPassword() {
        return getString(UserPassword);
    }


    public void logOutPref() {
        removeKey(IsLogin);
        removeKey(UserEmail);
        removeKey(UserPassword);
    }

}
