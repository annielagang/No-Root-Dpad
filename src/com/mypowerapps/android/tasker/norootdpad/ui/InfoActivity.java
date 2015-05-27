/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.mypowerapps.android.tasker.norootdpad.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.twofortyfouram.locale.PackageUtilities;
import com.mypowerapps.android.tasker.norootdpad.settings.Constants;
import com.mypowerapps.android.tasker.norootdpad.settings.TaskerIntent;
import com.mypowerapps.android.tasker.norootdpad.settings.Utils;

/**
 * If the user tries to launch the plug-in via the "Open" button in Google Play, this will redirect the user
 * to Tasker.
 */
public final class InfoActivity extends Activity {
	
	private static final String LOGGING_CLASS_NAME = InfoActivity.class.getSimpleName();	
	 
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PackageManager manager = getPackageManager();

        final String localePackage = PackageUtilities.getCompatiblePackage(manager, null);
        final String taskerPackage = TaskerIntent.getInstalledTaskerPackage(this);
        
        if (taskerPackage == null || localePackage == null) {
        	String message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
        										 "Locale-compatible package is not installed");
        	Log.i(Constants.LOG_TAG, message);
        	
            try {
               	startActivity(TaskerIntent.getTaskerInstallIntent(true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));          
            } catch (android.content.ActivityNotFoundException anfe) {
            	startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(Constants.TASKER_PLAYSTORE_URL)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); 
            
            } catch (final Exception e) {
            	startActivity(TaskerIntent.getTaskerInstallIntent(false).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));    
            	
            	message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
						  "Tasker Direct-purchase version offered.");
            	Log.i(Constants.LOG_TAG, message, e); 
            }
        }

        finish();
    }
}