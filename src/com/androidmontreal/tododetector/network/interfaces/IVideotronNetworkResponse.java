package com.androidmontreal.tododetector.network.interfaces;

import org.apache.http.HttpResponse;

import com.google.gson.JsonObject;

public interface IVideotronNetworkResponse {
	public void onVideotronNetworkResponseReceived(HttpResponse response);
}
