package com.androidmontreal.tododetector.network;

import org.apache.http.client.methods.HttpGet;

import android.os.Handler;

import com.androidmontreal.tododetector.network.interfaces.IVideotronNetworkResponse;

public class VideotronNetworkChatter {

	public static void getVideotronData(String userCode, IVideotronNetworkResponse listeningActivity) {
		HttpGet reqData = new HttpGet(generateCommandURL(userCode));
		(new AsynchronousSender(reqData, new Handler(), new VideotronMessageMachine(listeningActivity))).start();
	}

	private static String generateCommandURL(String userCode) {
		return (getBaseURL().concat(userCode).concat(secondPart()));
	}

	private static String secondPart() {
		return ".json?caller=bandwidth.videotron.cybikbase.com&lang=en";
	}

	private static String getBaseURL() {
		return "https://www.videotron.com/api/1.0/internet/usage/wired/";
	}
}
