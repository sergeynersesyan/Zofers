package com.zofers.zofers.messenger.messenger.service;


import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

	MessengerService messengerService;
	//    final CountDownLatch countDownLatch = new CountDownLatch(1);
	final String[] responseHolder = new String[1];

	public void setMessengerService(MessengerService messengerService) {
		this.messengerService = messengerService;
	}

	@Override
	public Request authenticate(Route route, Response response) throws IOException {
		if (messengerService == null)
			return null;
		String[] newAccessToken = new String[1];
//        boolean[] responded = new boolean[1];
//        messengerService.getWebService().request(AuthResult.class, "GetMessengerAccessToken", null, volleyResponse -> {
//            responded[0] = true;
//            if (!volleyResponse.isSuccessful()) {
//                return;
//            }
//            newAccessToken[0] = volleyResponse.getAccessToken();
//        });
//
//        try {
//            while (!responded[0])
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            return null;
//        }
//        if (newAccessToken[0] == null) return null;
//
//        messengerService.setAccessToken(newAccessToken[0]);
		// Add new header to rejected request and retry it
		return response.request().newBuilder()
				.header("Authorization", "Bearer " + newAccessToken[0])
				.build();
	}
}