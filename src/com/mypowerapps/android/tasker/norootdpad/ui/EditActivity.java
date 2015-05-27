package com.mypowerapps.android.tasker.norootdpad.ui;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.mypowerapps.android.tasker.norootdpad.R;
import com.mypowerapps.android.tasker.norootdpad.settings.Constants;
import com.mypowerapps.android.tasker.norootdpad.settings.Utils;
import com.mypowerapps.android.tasker.norootdpad.ui.NoPackageFoundDialog.OnDialogResultListener;

public class EditActivity extends AbstractPluginActivity {
	private static final String LOGGING_CLASS_NAME = EditActivity.class.getSimpleName();
	
	@Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Bundle resultBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        final Spinner spinner = (Spinner) findViewById(R.id.dpad_spinner);
        final SeekBar seekBar = (SeekBar) findViewById(R.id.dpad_seekBar);  
        final TextView seekBarCount = (TextView) findViewById(R.id.seekBar_count);   
        Boolean savedSettingsRestored = false;
        final int defaultMin = 1;
        final int max = 10;
        
        if(!isHackersKeyboardModdedInstalled(savedInstanceState)) {
        	seekBar.setProgress(defaultMin);
        	seekBarCount.setText(String.valueOf(defaultMin));
        	return;
        } else if(!isTaskerIntegrationEnabled()) {
        	seekBar.setProgress(defaultMin);
        	seekBarCount.setText(String.valueOf(defaultMin));
        	finish();        	
        	return;
        }
        
        // Sets the state of the widget from the saved instance state or from
     	// the extra provided from Locale. If savedInstanceState is not null, then
     	// the state is automatically restored.
        if (savedInstanceState == null && resultBundle != null) {
        	String valueSaved = resultBundle.getString(Constants.PLUGIN_KEY); 
        	String[] valueSplit = valueSaved.split("-");
        	
        	String message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
        										 Constants.PLUGIN_KEY + ".keycodes.String: " + valueSaved);
        	Log.i(Constants.LOG_TAG, message);
        	
        	int dpadKeyCode = Integer.parseInt(valueSplit[0]);
        	String dpadDirection = getKeyCodeStrDirMapping().get(dpadKeyCode);
        	int selectedIndex = getIndex(spinner, dpadDirection);
        	spinner.setSelection(selectedIndex);
        	
        	seekBar.setProgress(Integer.parseInt(valueSplit[1])); 
            seekBarCount.setText(valueSplit[1]);
            savedSettingsRestored = true;
        } 
        
        if (resultBundle == null) {
        	String message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
					                             "resultBundle NULL");
        	Log.i(Constants.LOG_TAG, message);	
        } 
                
        seekBar.setMax(max);
        
        if(!savedSettingsRestored) {
        	seekBar.setProgress(defaultMin);
        	seekBarCount.setText(String.valueOf(defaultMin));	
        }
        
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(progress == 0) {
					progress = defaultMin;
					seekBar.setProgress(defaultMin); 
				}
				
				seekBarCount.setText(String.valueOf(progress));	
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}        	
        });
    }
	
	private boolean isHackersKeyboardModdedInstalled(Bundle savedInstanceState) {
		String message = "";
        boolean isHackersKeyboardModdedInstalled = false;
        
        try {
        	if (Utils.getInstalledPackage(this, Constants.HACKERS_KEYBOARD_PACKAGE) != null) {
    			if(Utils.contentProviderExists(this, Constants.CONTENT_PROVIDER_AUTHORITY)) {
    			
    				message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
            			 					   "This device has Hacker's Keyboard Modded installed.");
    				Log.i(Constants.LOG_TAG, message);
             	 
    				isHackersKeyboardModdedInstalled = true;
    			} else {
    				message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
		 					   					  "This device has Hacker's Keyboard Original installed.");
    				Log.i(Constants.LOG_TAG, message);
    				
    				Boolean dialogExists = getFragmentManager().findFragmentByTag("dialog_frag") != null;
        			Boolean orientationHasChanged = savedInstanceState != null;
        			
        			if(!dialogExists && !orientationHasChanged) {
        		    	String title = getStringResource(R.string.dialog_title);
        		    	String msg =  getStringResource(R.string.dialog_message_hkoriginal);
        				showDialog(title, msg);
        			}
    			}
    		} else {
    			message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
    					  					  "This device doesn't have Hacker's Keyboard (original or modded) installed.");
    			Log.i(Constants.LOG_TAG, message);   
    			
    			Boolean dialogExists = getFragmentManager().findFragmentByTag("dialog_frag") != null;
    			Boolean orientationHasChanged = savedInstanceState != null;
    			
    			if(!dialogExists && !orientationHasChanged) {
    		    	String title = getStringResource(R.string.dialog_title);
    		    	String msg =  getStringResource(R.string.dialog_message_hkmodded);
    				showDialog(title, msg);
    			}
    		}
        }
        catch (final Exception e)
        {
            /*
             * Under normal circumstances, this shouldn't happen. Potential causes would be a TOCTOU error
             * where the application is uninstalled or the application enforcing permissions that it
             * shouldn't be.
             */
            message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
            							  "Error launching Activity");
        	Log.e(Constants.LOG_TAG, message, e); 
        }
        
        return isHackersKeyboardModdedInstalled;
	}
	
	private boolean isTaskerIntegrationEnabled() {
		String message = "";
        boolean isTaskerIntegrationEnabled = false;
        
        try {
        	Cursor cursor = getContentResolver().query(Uri.parse(Constants.CONTENT_PROVIDER_URI), null, null, null, null);

			if (cursor != null && cursor.moveToFirst()) {
				message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
	 					   "ContentProvider cursor NOT NULL");
				Log.i(Constants.LOG_TAG, message);
				
				cursor.moveToLast();
				int prefNameCol = cursor.getColumnIndex("prefName");
				int enabledCol = cursor.getColumnIndex("enabled");						
					
				isTaskerIntegrationEnabled = Boolean.parseBoolean(cursor.getString(enabledCol));
				
				message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
		 					   				"ContentProvider content: " + cursor.getString(prefNameCol)  + "/" 
		 					   						                    + cursor.getString(enabledCol));
				Log.i(Constants.LOG_TAG, message);
				
				cursor.close(); 
			} else {
				message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
	 					   "ContentProvider cursor NULL");
				Log.i(Constants.LOG_TAG, message);
			}
        } catch (final Exception e) {
            /*
             * Under normal circumstances, this shouldn't happen. Potential causes would be a TOCTOU error
             * where the application is uninstalled or the application enforcing permissions that it
             * shouldn't be.
             */
            message = Utils.logTextFormat(LOGGING_CLASS_NAME, "onCreate", 
            							  "Error launching Activity");
        	Log.e(Constants.LOG_TAG, message, e); 
        }
		
		if(!isTaskerIntegrationEnabled) {
			String msg = getStringResource(R.string.toast_message_hkmodded);
			Toast.makeText(EditActivity.this, msg, Toast.LENGTH_LONG).show();
		}
        
        return isTaskerIntegrationEnabled;
	}
	
    @Override
    public void finish() {
    	final Intent resultIntent = new Intent();
    	final Spinner spinner = (Spinner) findViewById(R.id.dpad_spinner);
    	final TextView seekBarCount = (TextView) findViewById(R.id.seekBar_count);
    	
    	/*
         * This extra is the data to ourselves: either for the Activity or the BroadcastReceiver. Note
         * that anything placed in this Bundle must be available to Locale's class loader. So storing
         * String, int, and other standard objects will work just fine. Parcelable objects are not
         * acceptable, unless they also implement Serializable. Serializable objects must be standard
         * Android platform objects (A Serializable class private to this plug-in's APK cannot be
         * stored in the Bundle, as Locale's classloader will not recognize it).
         */
		
    	String keyCodeStr = String.valueOf(spinner.getSelectedItem());
    	int keyEvent = getStrDirkeyCodeMapping().get(keyCodeStr);
    	String keyCode = String.valueOf(keyEvent);
    	String repeatCount = seekBarCount.getText().toString();  
    	String valueToBeSentToKeyBoard = keyCode + "-" + repeatCount;
    	
    	String message = Utils.logTextFormat(LOGGING_CLASS_NAME, "finish", 
    			"EditActivity.keycodes.String: " + keyCode + " " + repeatCount + "x");
        Log.i(Constants.LOG_TAG, message);
    	
		final Bundle resultBundle = new Bundle();    		
		resultBundle.putString(Constants.PLUGIN_KEY, valueToBeSentToKeyBoard);
		resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
		
		// The blurb is concise status text to be displayed in the host's UI.
		final String blurb = generateBlurb(getApplicationContext(), keyCodeStr, repeatCount);
        resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, blurb);
        
        setResult(RESULT_OK, resultIntent);        
        super.finish();
    }
    
    /* package */String generateBlurb(final Context context, final String keyCodeStr, final String repeatCount) {
    	String message = "";
    	
    	if(repeatCount.equals("1")) {
    		message = keyCodeStr;
    	} else {
    		message = keyCodeStr + " " + repeatCount + "x";
    	}
        
        final int maxBlurbLength =
                context.getResources().getInteger(R.integer.twofortyfouram_locale_maximum_blurb_length);

        if (message.length() > maxBlurbLength) {
            return message.substring(0, maxBlurbLength);
        }

        return message;
    }
    
    private SparseArray<String> getKeyCodeStrDirMapping() {
    	 SparseArray<String> keyCodeStrDirMapping = new  SparseArray<String>();
    	 keyCodeStrDirMapping.put(KeyEvent.KEYCODE_DPAD_UP, getStringResource(R.string.keycode_dpad_up));
    	 keyCodeStrDirMapping.put(KeyEvent.KEYCODE_DPAD_DOWN, getStringResource(R.string.keycode_dpad_down));
    	 keyCodeStrDirMapping.put(KeyEvent.KEYCODE_DPAD_LEFT, getStringResource(R.string.keycode_dpad_left));  	 
    	 keyCodeStrDirMapping.put(KeyEvent.KEYCODE_DPAD_RIGHT, getStringResource(R.string.keycode_dpad_right));
    	 keyCodeStrDirMapping.put(KeyEvent.KEYCODE_DPAD_CENTER, getStringResource(R.string.keycode_dpad_center));
    	 keyCodeStrDirMapping.put(KeyEvent.KEYCODE_HOME, getStringResource(R.string.keycode_dpad_home));
    	 keyCodeStrDirMapping.put(KeyEvent.KEYCODE_BACK, getStringResource(R.string.keycode_dpad_back));
    	 
    	 return keyCodeStrDirMapping;
    }
    
    private Map<String, Integer> getStrDirkeyCodeMapping() {
    	Map<String, Integer> strDirkeyCodeMapping = new HashMap<String, Integer>();
    	strDirkeyCodeMapping.put(getStringResource(R.string.keycode_dpad_up), KeyEvent.KEYCODE_DPAD_UP);
    	strDirkeyCodeMapping.put(getStringResource(R.string.keycode_dpad_down), KeyEvent.KEYCODE_DPAD_DOWN);
    	strDirkeyCodeMapping.put(getStringResource(R.string.keycode_dpad_left), KeyEvent.KEYCODE_DPAD_LEFT);  	 
    	strDirkeyCodeMapping.put(getStringResource(R.string.keycode_dpad_right), KeyEvent.KEYCODE_DPAD_RIGHT);
    	strDirkeyCodeMapping.put(getStringResource(R.string.keycode_dpad_center), KeyEvent.KEYCODE_DPAD_CENTER);
    	strDirkeyCodeMapping.put(getStringResource(R.string.keycode_dpad_home), KeyEvent.KEYCODE_HOME);
    	strDirkeyCodeMapping.put(getStringResource(R.string.keycode_dpad_back), KeyEvent.KEYCODE_BACK);
    	
    	return strDirkeyCodeMapping;
   }
    
    private String getStringResource(int id) {
    	return getResources().getString(id);
    }
    
    private void showDialog(String title, String msg) {
    	NoPackageFoundDialog dialog = NoPackageFoundDialog.newInstance(title, msg, new OnDialogResultListener() {
			@Override
			public void onSetResult(int resultCode) {
				if (resultCode == Activity.RESULT_CANCELED) {
		    		 finish();
		    	}
			}
    	});
    	
    	dialog.show(getFragmentManager(), "dialog_frag");
    }
    
    private int getIndex(Spinner spinner, String myString) {
    	int index = -1;

    	for (int i=0; i < spinner.getCount(); i++) {
    		if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
    			index = i;
    			break;
    		}
    	}
    	
    	return index;
    }
}