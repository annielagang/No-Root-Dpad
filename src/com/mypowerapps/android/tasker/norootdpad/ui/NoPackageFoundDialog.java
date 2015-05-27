package com.mypowerapps.android.tasker.norootdpad.ui;

import com.mypowerapps.android.tasker.norootdpad.R;
import com.mypowerapps.android.tasker.norootdpad.settings.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Provides a simple dialog UI for the user to understand what a plug-in is when tapping on the "open" button from the Android
 * Market.
 */
public class NoPackageFoundDialog extends DialogFragment {
	 public static final String EXTRA_TITLE = "dialogIntent.TITLE";
	 public static final String EXTRA_MSG = "dialogIntent.MSG";
	 private static String title;
	 private static String msg;
	 private static OnDialogResultListener mCallback;
	 
	 public interface OnDialogResultListener {
	        public void onSetResult(int resultCode);
	 }
	 
	 public static NoPackageFoundDialog newInstance(String title, String msg, OnDialogResultListener callback) {
		 mCallback = callback;
		 
		 Bundle args = new Bundle();
		 args.putString(EXTRA_TITLE, title);
		 args.putString(EXTRA_MSG, msg);
		 
		 NoPackageFoundDialog fragment = new NoPackageFoundDialog();
		 fragment.setArguments(args);
		 return fragment;		 
	 }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        title = args.getString(EXTRA_TITLE, "");
        msg = args.getString(EXTRA_MSG, "");
        
        return new AlertDialog.Builder(getActivity())
        				.setTitle(title)
        				.setMessage(msg)
        				.setPositiveButton(R.string.dialog_button_okay, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mCallback.onSetResult(Activity.RESULT_CANCELED);
							}        					
        				})
        				.setNegativeButton(R.string.dialog_button_uninstall, new DialogInterface.OnClickListener() {
        					@Override
							public void onClick(DialogInterface dialog, int which) {
        						Intent intent = new Intent(Intent.ACTION_DELETE);
        						intent.setData(Uri.parse("package:" + Constants.NOROOT_DPAD_PACKAGE));
        						startActivity(intent);
							}
        				})
        				.create();
    }
}