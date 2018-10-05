package com.project.uberauto;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionManager {

        private SharedPreferences prefs;

        public SessionManager(Context cntx) {
            // TODO Auto-generated constructor stub
            prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
        }

        public void set(String key,String value) {
            prefs.edit().putString(key, value).commit();
        }

        public String get(String key) {
            String value = prefs.getString(key,"");
            return value;
        }
}
