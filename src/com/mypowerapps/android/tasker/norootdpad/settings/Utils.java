package com.mypowerapps.android.tasker.norootdpad.settings;

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.util.Log;

public class Utils  {
	private static final String LOGGING_CLASS_NAME = Utils.class.getSimpleName();
	
	public final static String getInstalledPackage(Context context, String packageName) {
		String message = Utils.logTextFormat(LOGGING_CLASS_NAME, "getInstalledPackage - Before check", packageName);
		Log.i(Constants.LOG_TAG, message);
		
		String foundPackage = null;
		
		try {
			context.getPackageManager().getPackageInfo(packageName, 0);
			foundPackage = packageName;
		}
		catch (PackageManager.NameNotFoundException e) { }
		
		message = Utils.logTextFormat(LOGGING_CLASS_NAME, "getInstalledPackage - Result", String.valueOf(foundPackage != null));
		Log.i(Constants.LOG_TAG, message);
		return foundPackage;
	}
	
	public final static String logTextFormat(String className, String method, String message) {
		return String.format(Locale.US, "%1$s.%2$s(): %3$s", className, method, message);
	}
	
	public final static boolean contentProviderExists(Context context, String contentProviderAuthority) {
		String message = Utils.logTextFormat(LOGGING_CLASS_NAME, "contentProviderExists - Before check", contentProviderAuthority);
		Log.i(Constants.LOG_TAG, message);
		
		boolean contentProviderExists = false; 
		for (PackageInfo pack : context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS)) {
				ProviderInfo[] providers = pack.providers;
				if (providers != null) {
					for (ProviderInfo provider : providers) {
						
						if(provider.authority.equalsIgnoreCase(contentProviderAuthority)) {
							contentProviderExists = true;
							
							message = Utils.logTextFormat(LOGGING_CLASS_NAME, "contentProviderExists - ContentProvider found", contentProviderAuthority);
							Log.i(Constants.LOG_TAG, message);
							
							break;
						}
					}
				}
		}
		
		message = Utils.logTextFormat(LOGGING_CLASS_NAME, "contentProviderExists - Result", String.valueOf(contentProviderExists));
		Log.i(Constants.LOG_TAG, message);
		
		return contentProviderExists;
	}
}
