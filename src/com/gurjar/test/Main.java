package com.gurjar.test;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

import com.gurjar.ssc.SSCParseUtil;
import com.gurjar.ssc.WebPage;
import com.sms.way2sms.Way2Sms;

public class Main {

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sss = new SimpleDateFormat("MMM-yy hh:mm aaa");
		sss.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
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
				// new Way2Sms("9413102070", "1733").sendSMS("9958378173",
				// message
				// + ":" + new SimpleDateFormat("MMM-yy hh:mm aaa").format(new
				// Date()));
				// SimpleDateFormat ss = new SimpleDateFormat("HH");
				// ss.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
				// System.out.println(ss.format(new Date()));
				// if (Integer.parseInt(ss.format(new Date())) < 21
				// && Integer.parseInt(ss.format(new Date())) > 8) {
				new Way2Sms("9958378173", "173314").sendSMS("9958378173",
						message + ":" + sss.format(new Date()));
				// }

				// Thread.sleep(1000 * 60 * 1);
				// new Way2Sms("9958378173", "173314")
				// .sendSMS(
				// "7406262136",
				// "my Ip is "1
				// + getFirstNonLoopbackAddress(true,
				// false)
				// +
				// " and  status is RUNNING.sent from GOOGLE CLOUD BY DILIP SINGH @"
				// + new SimpleDateFormat(
				// "MMM-yy hh:mm aaa")
				// .format(new Date()));
				// way2Sms.sendSMS("9958378173", message);

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
