package com.mypowerapps.android.tasker.norootdpad.settings;

import com.mypowerapps.android.tasker.norootdpad.BuildConfig;

public final class Constants {
	public static final String NOROOT_DPAD_PACKAGE = "com.mypowerapps.android.tasker.norootdpad";
    public static final String LOG_TAG = NOROOT_DPAD_PACKAGE + ".extra.LOG_TAG"; //$NON-NLS-1$
    public static final boolean IS_LOGGABLE = BuildConfig.DEBUG;
    public static final String PLUGIN_KEY =  NOROOT_DPAD_PACKAGE + ".extra.KEYCODES";
    public static final String BUNDLE_KEY =  NOROOT_DPAD_PACKAGE + ".extra.BUNDLE";
    public static final String HACKERS_KEYBOARD_PACKAGE = "org.pocketworkstation.pckeyboard";
    public static final String ACTION = HACKERS_KEYBOARD_PACKAGE + ".action.ACCESS_KEYS";
    public static final String SCHEME = "content://";
    public static final String CONTENT_PROVIDER_AUTHORITY = HACKERS_KEYBOARD_PACKAGE;
    public static final String CONTENT_PROVIDER_URI = SCHEME + CONTENT_PROVIDER_AUTHORITY + "/prefs";
    public static final String TASKER_PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=net.dinglisch.android.taskerm&hl=en";
}