package com.gurjar.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class URLConnector {
	private HttpURLConnection connection;
	private Proxy proxy;

	public void setProxy(String host, int port) {
		proxy = new Proxy(Proxy.Type.HTTP,
				java.net.InetSocketAddress.createUnresolved(host, port));
	}

	public void connect(String urlPath, boolean redirect, String method,
			String cookie, String credentials) throws Exception {
		try {
			URL url = new URL(urlPath);

			if (null != proxy)
				connection = (HttpURLConnection) url.openConnection(proxy);
			else
				connection = (HttpURLConnection) url.openConnection();

			connection.setInstanceFollowRedirects(redirect);

			if (cookie != null)
				connection.setRequestProperty("Cookie", cookie);

			if (method != null && method.equalsIgnoreCase("POST")) {
				connection.setRequestMethod(method);
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
			}

			connection
					.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0.4) Gecko/20100101 Firefox/10.0.4");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			if (credentials != null) {
				DataOutputStream wr = new DataOutputStream(
						connection.getOutputStream());
				wr.writeBytes(credentials);
				wr.flush();
				wr.close();
			}
		} catch (Exception exception) {
			throw new Exception("Connection Error", exception);
		}
	}

	public String getCookie(String str) {
		String cookie = str;

		if (connection != null) {
			String headerName = null;

			for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
				if (headerName.equals("Set-Cookie")) {
					cookie = connection.getHeaderField(i).split(";")[0];
					break;
				}
			}
		}

		return cookie;
	}

	public String getLocation() {
		String location = null;

		if (connection != null) {
			String headerName = null;

			for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
				if (headerName.equals("Location")) {
					location = connection.getHeaderField(i);
					String temp = "";
					if (location.indexOf("?") != -1) {
						temp = location.substring(location.indexOf("?"));
						location = location.substring(0, location.indexOf("?"));
					}
					location = location.split(";")[0];
					location = location + temp;
					break;
				}
			}
		}

		return location;
	}

	public int getResponseCode() throws Exception {
		if (connection != null) {
			try {
				return connection.getResponseCode();
			} catch (Exception exception) {
				throw new Exception("Response code error", exception);
			}
		} else {
			throw new Exception(new NullPointerException("connection is  null"));
		}
	}

	public String getResponse() {
		StringBuilder response = new StringBuilder();

		if (connection != null) {
			try {
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(is));

				String line;
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}

				rd.close();
			} catch (Exception exception) {
				throw new RuntimeException("Response error" + exception);
			}
		}

		return response.toString();
	}

	public String getErrorMessage() {
		StringBuilder errorMessage = new StringBuilder();

		if (connection != null) {
			try {
				InputStream es = connection.getErrorStream();
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(es));

				String line;
				while ((line = rd.readLine()) != null) {
					errorMessage.append(line);
					errorMessage.append('\r');
				}

				rd.close();
			} catch (Exception exception) {
				System.err.println("Error in getting error message");
			}
		}

		return errorMessage.toString();
	}

	public void disconnect() {
		if (connection != null)
			connection.disconnect();
	}
}