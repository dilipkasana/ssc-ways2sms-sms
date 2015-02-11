package com.gurjar.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("test.html"))));
		StringBuffer stringBuffer = new StringBuffer();
		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine + "\n");
		}
		in.close();
		String st = response.toString();
		Document doc = Jsoup.parse(st);
		System.out.println(doc);
	}

}
