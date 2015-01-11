package com.gurjar.one60by2sms;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gurjar.util.Credentials;
import com.gurjar.util.URLConnector;

public class My160By2SMS {
	private String userCredentials = null;
	private String cookie = null;
	private String site = null;
	private Credentials credentials;
	private URLConnector urlConnector;
	private String Referer = null;

	public My160By2SMS(String username, String password) throws Exception {
		credentials = new Credentials();
		urlConnector = new URLConnector();
		doLogin(username, password);
	}

	private void doLogin(String uid, String pwd) throws Exception {
		Referer = site = "www.160by2.com";
		// getSite();
		// // preHome();
		index();
		actualLogin(uid, pwd);
	}

	private void setProxy(String host, int port) {
		urlConnector.setProxy(host, port);
	}

	public void actualLogin(String uid, String pwd) throws Exception {
		String location = null;
		credentials.set("rssData", "");

		credentials.append("username", uid);
		credentials.append("password", pwd);
		userCredentials = credentials.getUserCredentials();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Host", "www.160by2.com");
		map.put("Origin", "http://www.160by2.com");
		map.put("Referer", "http://www.160by2.com/Index");
		urlConnector.connect("http://" + site + "/re-login", false, "POST",
				cookie, userCredentials, map);
		int responseCode = urlConnector.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK) {
			throw new Exception("authentication failed!");
		} else {
			Referer = "http://" + site + "/re-login";
			cookie = urlConnector.getCookie(cookie);
			location = urlConnector.getLocation();
		}
		urlConnector.disconnect();

		if (location != null && !location.endsWith("Login")) {
			urlConnector.connect(location, false, "GET", cookie, null);
			responseCode = urlConnector.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
					&& responseCode != HttpURLConnection.HTTP_OK) {
				throw new Exception("redirection failed!");
			} else {
				Referer = location;
				cookie = urlConnector.getCookie(cookie);
				location = urlConnector.getLocation();
			}
			urlConnector.disconnect();
		}
	}

	private void index() throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Host", "www.160by2.com");
		map.put("Origin", "http://www.160by2.com");
		map.put("Referer", Referer);
		urlConnector.connect("http://" + site + "/Index", false, "GET", null,
				null, map);
		int responseCode = urlConnector.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK)
			exit("index failed");
		else {
			Referer = "http://" + site + "/Index";
			cookie = urlConnector.getCookie(cookie);
		}
		urlConnector.disconnect();

	}

	static LinkedHashMap<String, String> phoneMap = new LinkedHashMap<String, String>();

	public static Map<String, String> get(Document document, List<String> list,
			String phone) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Elements elements = document.getElementById("frm_sendsms")
				.getElementsByTag("input");
		for (String str : list) {
			map.put(str, "");
		}
		int i = 0;
		for (Element element : elements) {
			if (i == 1 || i == 2) {
				if (i == 1) {
					phoneMap.put(element.id(), element.val());
				}
				if (i == 2) {
					phoneMap.put(element.id(), phone);
				}
				i++;
			}
			if (element.val() != null && map.containsKey(element.id())) {
				if (element.id().equals("feb2by2action")) {
					i = 1;
				}
				map.put(element.id(), element.val());
			}
		}
		return map;

	}

	public static Map<String, String> getParse(String html, String phone)
			throws IOException {
		Document doc = Jsoup.parse(html);
		List<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(new String[] { "hid_exists", "fkapps",
				"newsUrl", "pageContext", "linkrs", "hidSessionId", "msgLen",
				"maxwellapps", "feb2by2action", "sendSMSMsg", "newsExtnUrl",
				"ulCategories", "messid_0", "messid_1", "messid_2", "messid_3",
				"messid_4", "reminderDate", "sel_hour", "sel_minute" }));

		// for (Entry<String, String> entry : get(doc, list).entrySet()) {
		// map.put(entry.getKey() , entry.getValue());
		// }
		Map<String, String> map = get(doc, list, phone);
		map.put("ulCategories", "32");
		SimpleDateFormat ss = new SimpleDateFormat("DD-MM-yyyy");
		ss.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
		map.put("reminderDate", ss.format(new Date()));
		map.put("maxwellapps", map.get("hidSessionId"));
		return map;
	}

	public boolean sendSMS(String receiversMobNo, String msg) throws Exception {
		Map<String, String> map = getTokens(receiversMobNo);
		if (msg.length() > 140) {
			msg = msg.substring(0, 140);
		}
		map.put("sendSMSMsg", msg);

		credentials.reset();
		for (Entry<String, String> entry : map.entrySet()) {
			if (credentials.isEmpty()) {
				credentials.set(entry.getKey(), entry.getValue());
			} else {
				if (entry.getKey().equals("feb2by2action")) {
					credentials.append(entry.getKey(), entry.getValue());
					for (Entry<String, String> entry1 : phoneMap.entrySet()) {
						credentials.append(entry1.getKey(), entry1.getValue());
					}
				} else {
					credentials.append(entry.getKey(), entry.getValue());
				}
			}
		}
		userCredentials = credentials.getUserCredentials();
		HashMap<String, String> tMap = new HashMap<String, String>();

		tMap.put("Referer", Referer);
		urlConnector.connect("http://" + site + "/" + map.get("fkapps"), true,
				"POST", cookie, userCredentials, tMap);
		int responseCode = urlConnector.getResponseCode();
		String location = null;
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK) {
			throw new Exception("sendSMS failed!");
		} else {
			location = urlConnector.getLocation();
			urlConnector.disconnect();
		}

		if (location != null && location.contains("SendSMSConfirm")) {
			tMap = new HashMap<String, String>();

			tMap.put("Referer", Referer);
			urlConnector.connect(location, false, "GET", cookie, null, tMap);
			responseCode = urlConnector.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
					&& responseCode != HttpURLConnection.HTTP_OK) {
				throw new Exception("redirection failed!");
			}
			urlConnector.disconnect();
			return true;
		} else if (urlConnector.getConnection().getURL().toExternalForm()
				.contains("SendSMSConfirm")) {
			return true;
		}
		throw new Exception("failed");
	}

	private Map<String, String> getTokens(String phone) throws Exception {
		credentials.reset();
		String id = null;
		String arr[] = cookie.split(";");
		for (String str : arr) {
			if (str.contains("JSESSIONID=")) {
				id = str.substring("JSESSIONID=".length(), str.length());
			}
		}
		if (id != null) {
			credentials.set(
					"id",
					id.contains("~") ? id.substring(id.indexOf("~") + 1,
							id.length()) : id);
		}
		urlConnector.connect(
				"http://" + site + "/SendSMS?"
						+ credentials.getUserCredentials(), false, "GET",
				cookie, null);
		int responseCode = urlConnector.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP
				&& responseCode != HttpURLConnection.HTTP_OK) {
			throw new Exception("get Token failed");
		} else {
			Referer = "http://" + site + "/SendSMS?"
					+ credentials.getUserCredentials();
			cookie = urlConnector.getCookie(cookie);

		}
		Map<String, String> tempMap = new LinkedHashMap<String, String>();
		tempMap = getParse(urlConnector.getResponse(), phone);
		urlConnector.disconnect();
		return tempMap;
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