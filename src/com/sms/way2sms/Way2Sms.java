package com.sms.way2sms;

import java.net.HttpURLConnection;

import com.gurjar.util.Credentials;
import com.gurjar.util.URLConnector;

public class Way2Sms {
	private String userCredentials = null;
	private String cookie = null;
	private String site = null;
	private Credentials credentials;
	private URLConnector urlConnector;

	public Way2Sms(String username, String password) throws Exception {
		credentials = new Credentials();
		urlConnector = new URLConnector();
		doLogin(username, password);
	}

	private void doLogin(String uid, String pwd) throws Exception {
		getSite();
		preHome();
		index();
		actualLogin(uid, pwd);
	}

	private void setProxy(String host, int port) {
		urlConnector.setProxy(host, port);
	}

	private void getSite() throws Exception {
		urlConnector.connect("http://www.way2sms.com/", false, "GET", null,
				null);
		int responseCode = urlConnector.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK) {
			throw new Exception("getSite failed!" + responseCode);
		} else {
			site = urlConnector.getLocation();
			if (site != null)
				site = site.substring(7, site.length() - 1);
		}
		urlConnector.disconnect();
	}

	private void preHome() throws Exception {
		urlConnector.connect("http://" + site + "/content/prehome.jsp", false,
				"GET", null, null);
		int responseCode = urlConnector.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK)
			exit("preHome failed");
		else
			cookie = urlConnector.getCookie(cookie);
		urlConnector.disconnect();
	}

	public void actualLogin(String uid, String pwd) throws Exception {
		String location = null;

		credentials.set("username", uid);
		credentials.append("password", pwd);
		userCredentials = credentials.getUserCredentials();

		urlConnector.connect("http://" + site + "/Login1.action", false,
				"POST", cookie, userCredentials);
		int responseCode = urlConnector.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK) {
			throw new Exception("authentication failed!");
		} else {
			location = urlConnector.getLocation();
		}
		urlConnector.disconnect();

		urlConnector.connect(location, false, "GET", cookie, null);
		responseCode = urlConnector.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK) {
			throw new Exception("redirection failed!");
		}
		urlConnector.disconnect();
	}

	private void index() throws Exception {

		urlConnector.connect("http://" + site + "/content/index.html", false,
				"GET", null, null);
		int responseCode = urlConnector.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK)
			exit("index failed");
		else
			cookie = urlConnector.getCookie(cookie);
		urlConnector.disconnect();

	}

	public boolean sendSMS(String receiversMobNo, String msg) throws Exception {
		if (msg.length() > 140) {
			msg = msg.substring(0, 140);
		}
		getToken(receiversMobNo);
		credentials.reset();
		credentials.set("Token",
				cookie.substring(cookie.indexOf("~") + 1, cookie.length()));
		credentials.append("mobile", receiversMobNo);
		credentials.append("name", "");
		credentials.append("ssaction", "qs");
		credentials.append("message", msg);
		credentials.append("msgLen", (140 - msg.length()) + "");

		userCredentials = credentials.getUserCredentials();

		urlConnector.connect("http://" + site + "/smstoss.action", true,
				"POST", cookie, userCredentials);
		int responseCode = urlConnector.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK) {
			throw new Exception("sendSMS failed!");
		} else {
			urlConnector.disconnect();
			return true;
		}
	}

	private void getToken(String receiversMobNo) throws Exception {
		credentials.reset();
		credentials.set("Token",
				cookie.substring(cookie.indexOf("~") + 1, cookie.length()));
		credentials.append("toMob", receiversMobNo);
		urlConnector.connect("http://" + site + "/quicksms.action?"
				+ credentials.getUserCredentials(), false, "GET", cookie, null);
		int responseCode = urlConnector.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK) {
			throw new Exception("get Token failed");
		} else {
			cookie = urlConnector.getCookie(cookie);
		}
		urlConnector.disconnect();
	}

	private void sendSMS(String[] receiversMobNos, String msg) throws Exception {
		int noOfReceivers = receiversMobNos.length;

		for (int i = 0; i < noOfReceivers; i++)
			sendSMS(receiversMobNos[i], msg);
	}

	private void exit(String errorMsg) {
		System.err.println(errorMsg);
		System.exit(1);
	}
}