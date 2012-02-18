package com.androidmontreal.tododetector.network;

import org.apache.http.HttpResponse;

import com.androidmontreal.tododetector.network.interfaces.IAnsweringMachine;
import com.androidmontreal.tododetector.network.interfaces.IVideotronNetworkResponse;

public class VideotronMessageMachine implements IAnsweringMachine {

	private IVideotronNetworkResponse callbackActivity;
	private HttpResponse response;
 
	public VideotronMessageMachine(IVideotronNetworkResponse callbackActivity) {
		this.callbackActivity = callbackActivity;
	}
 
	public void run() {
		callbackActivity.onVideotronNetworkResponseReceived(response);
	}
 
	public void setResponse(HttpResponse response) {
		this.response = response;
	}
}
