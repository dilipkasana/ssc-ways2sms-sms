package com.gurjar.test;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

import com.gurjar.one60by2sms.My160By2SMS;
import com.gurjar.ssc.SSCParseUtil;
import com.gurjar.ssc.WebPage;
import com.sms.way2sms.Way2Sms;

public class Main {

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sss = new SimpleDateFormat("MMM-yy hh:mm aaa");
		sss.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
		boolean flag = true;
		while (true) {
			try {
				String url = "http://ssc.nic.in/results/home.html";
				WebPage webPage = new WebPage(url);
				String message = "default";
				try {
					message = SSCParseUtil.getLastDay(webPage.getWebPage());
				} catch (Exception e) {
					message = e.getMessage();
				}
				if (message.length() > 120) {
					message = message.substring(0, 120);
				}
				sendSMS(message, sss, flag);
				flag = !flag;
				System.out.println("Message has been sent successfully!"
						+ message);

			} catch (Throwable t) {
				t.printStackTrace();
			}
			int minutes = 30;

			try {
				Thread.sleep(1000 * 60 * minutes);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private static void sendSMS(String message, SimpleDateFormat sss,
			boolean flag) {
		String ways2smsId = "";
		String ways2SMSPasswd = "";
		String ways2anotherUser = "";
		String ways2anotherpasswd = "";
		String one60by2username = "";
		String one60by2Passwd = "";
		String receiver[] = { "", "", "" };



		SimpleDateFormat ss = new SimpleDateFormat("HH");
		ss.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
		System.out.println(ss.format(new Date()));
		SimpleDateFormat ssst = new SimpleDateFormat("MMM-yy hh:mm aaa");
		ssst.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

		String xxx;
		try {
			xxx = "my Ip is "
					+ getFirstNonLoopbackAddress(true, false)
					+ " and  status is RUNNING.sent from GOOGLE CLOUD BY DILIP SINGH @"
					+ ssst.format(new Date());
		} catch (Exception e1) {
			xxx = "my Ip is "
					+ "127.0.0.1"
					+ " and  status is RUNNING.sent from GOOGLE CLOUD BY DILIP SINGH @"
					+ ssst.format(new Date());

		}
		if (Integer.parseInt(ss.format(new Date())) < 22
				&& Integer.parseInt(ss.format(new Date())) > 7) {
			System.out.println("Time is "
					+ Integer.parseInt(ss.format(new Date())));
			if (flag) {
				try {
					new Way2Sms(ways2smsId, ways2SMSPasswd).sendSMS(
							receiver[0],message + ":" + sss.format(new Date()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					new My160By2SMS(one60by2username, one60by2Passwd)
							.sendSMS(receiver[1],
									message + ":" + sss.format(new Date()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					new Way2Sms(ways2smsId, ways2SMSPasswd)
							.sendSMS(receiver[1],
									message + ":" + sss.format(new Date()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					new My160By2SMS(one60by2username, one60by2Passwd).sendSMS(
							receiver[0], message + ":" + sss.format(new Date()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4,
			boolean preferIPv6) throws Exception {
		Enumeration en = NetworkInterface.getNetworkInterfaces();
		while (en.hasMoreElements()) {
			NetworkInterface i = (NetworkInterface) en.nextElement();
			for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
				InetAddress addr = (InetAddress) en2.nextElement();
				if (!addr.isLoopbackAddress()) {
					if (addr instanceof Inet4Address) {
						if (!addr.toString().contains("192.168")) {
							if (preferIPv6) {
								continue;
							}
							return addr;
						}
					}
					if (addr instanceof Inet6Address) {
						if (preferIpv4) {
							continue;
						}
						return addr;
					}
				}
			}
		}
		return null;
	}
}
