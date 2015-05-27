package com.mypowerapps.android.tasker.norootdpad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Locale;

import com.mypowerapps.android.tasker.norootdpad.settings.Constants;

public final class FireReceiver extends BroadcastReceiver
{
	@Override
    public void onReceive(final Context context, final Intent intent) {
		/*
         * Always be strict on input parameters! A malicious third-party app could send a malformed Intent.
         */

        if (!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
            if (Constants.IS_LOGGABLE) {
                Log.e(Constants.LOG_TAG,
                      String.format(Locale.US, "Received unexpected Intent action %s", intent.getAction())); //$NON-NLS-1$
            }
            return;
        }
		
        Bundle intentExtraBundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        if(intentExtraBundle == null) {
        	Log.i(Constants.LOG_TAG, "FireReceiver.onReceive() - intent.getBundleExtra NULL");
        } else {
        	 if(intentExtraBundle.getString(Constants.PLUGIN_KEY) == null) {
        		 Log.i(Constants.LOG_TAG, "FireReceiver.onReceive() - getString NULL");
             }
        }
        
        final Bundle resultBundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        if (resultBundle != null) {
        	String keyCodes = resultBundle.getString(Constants.PLUGIN_KEY);
        	
        	if(keyCodes != null) {
        		Log.i(Constants.LOG_TAG, "FireReceiver.keycodes.String: " + keyCodes);
        		
        		final Intent intentToBeForwarded = new Intent(Constants.ACTION);	
        		final Bundle bundleToBeForwarded = new Bundle();    		
        		bundleToBeForwarded.putString(Constants.PLUGIN_KEY, keyCodes);
        		intentToBeForwarded.putExtra(Constants.BUNDLE_KEY, bundleToBeForwarded);
        		
        		intentExtraBundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        	    if(intentExtraBundle == null) {
        	    	Log.i(Constants.LOG_TAG, "FireReceiver.onReceive() before resending intent - intent.getBundleExtra NULL");
        	    } else {
        	    	if(intentExtraBundle.getString(Constants.PLUGIN_KEY) == null) {
        	    		Log.i(Constants.LOG_TAG, "FireReceiver.onReceive() before resending intent - getString NULL");
                    } 
        	    }
        		
        		context.sendBroadcast(intentToBeForwarded);
        	}
        }   
    }
}