package com.androidmontreal.tododetector;

import org.apache.http.HttpResponse;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.androidmontreal.tododetector.datatype.Elements;
import com.androidmontreal.tododetector.json.DataExtractor;
import com.androidmontreal.tododetector.network.NetworkChatter;
import com.androidmontreal.tododetector.network.interfaces.INetworkResponse;
import com.androidmontreal.tododetector.ui.toaster;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class TodoDetectorUIActivity extends SherlockActivity implements INetworkResponse {
    

	/*******************************************************
	 *                                                     *
	 *                   Class Variables                   *
	 *                                                     *
	 *******************************************************/
	// Inter-thread Message Handler
	private final Handler mMessageChannel = new Handler();
	
	/*******************************************************
	 *                                                     *
	 *                  Application Code                   *
	 *                                                     *
	 *******************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem prefsMenu = menu.add("Preferences");
		prefsMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		prefsMenu.setIcon(R.drawable.ic_prefs_enabled_scaled);
		prefsMenu.setOnMenuItemClickListener(getPrefsInvokeClickListener());
		

		return super.onCreateOptionsMenu(menu);
	}	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
    }

	/*******************************************************
	 *                                                     *
	 *                   Data Requesting                   *
	 *                                                     *
	 *******************************************************/
	// JSON Data decryption happens in a network-approach-coded library (from google)
	// basically, we need to process that data in another thread and keep any laggy
	// stuff from the UI thread.
	private void requestDataSync() {
		toaster.printMessage(this, "attempting datasync");
		String lBaseURL = getValueFromPreference(getString(R.string.strServerBaseURL));
		if(lBaseURL.equalsIgnoreCase("")){
			toaster.printMessage(this, "You have not configured the base URL correctly. RTFM or GTFO");
			return;
		}
		NetworkChatter.getRemoteData(lBaseURL, this);
	}
	private void processResponseInThread(HttpResponse response){

		final HttpResponse argument = response;
		
		new Thread(new Runnable() {
			public void run() {
				JsonObject lNetResponse = null;
				lNetResponse = DataExtractor.getJsonObjectFromEntity(argument.getEntity());
				Elements returnData = 
						(new Gson()).fromJson(lNetResponse, Elements.class);
				mMessageChannel.post(new ServerDataRunnable(returnData));
			}
		}).start();
	}
	private class ServerDataRunnable implements Runnable {
		ServerDataRunnable(Elements pObject) {
			communicatedObject = pObject;
		}
		protected Elements communicatedObject = null;
		public void run() {
			processServerData(communicatedObject);
		}
	}
	protected void processServerData(Elements communicatedObject) {
		toaster.printMessage(this, "did it! "+communicatedObject.getListElements().get(0).getImageurl());
	}
	
	/*******************************************************
	 *                                                     *
	 *                  UI Event Handlers                  *
	 *                                                     *
	 *******************************************************/
	private OnMenuItemClickListener mPrefsInvokeClickListener = new OnMenuItemClickListener() {

		public boolean onMenuItemClick(MenuItem item) {
			Intent i = new Intent(getActivity(), TodoDetectorPrefs.class);
			startActivity(i);
			return false;
		}
	};
	private OnMenuItemClickListener getPrefsInvokeClickListener() {
		return mPrefsInvokeClickListener;
	}

	/*******************************************************
	 *                                                     *
	 *                   Utility Methods                   *
	 *                                                     *
	 *******************************************************/
	private TodoDetectorUIActivity getActivity() {return this;}
	private String getValueFromPreference(String pKey) {
		return PreferenceManager.getDefaultSharedPreferences(this).getString(pKey, "");
	}

	@Override
	public void onNetworkResponseReceived(HttpResponse response) {
		processResponseInThread(response);
	}


}