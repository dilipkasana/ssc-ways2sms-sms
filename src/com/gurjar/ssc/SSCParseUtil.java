package com.gurjar.ssc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class SSCParseUtil {

	public static String getLastDay(String html) throws IOException {
		Document doc = Jsoup.parse(html);
		Element htmlNode = getNode("html", doc);
		Element bodyNode = getNode("body", htmlNode);
		Element centerNode = getNode("center", bodyNode);
		Element tableNode = getNode("table", centerNode);
		Element tableBodyNode = getNode("tbody", tableNode);
		Element trNode = getNode("tr", tableBodyNode);
		Element tdNode = getNode("td", trNode);
		Element finalTableNode = getNode("table", tdNode);
		Element finalTableBodyNode = getNode("tbody", finalTableNode);
		List<Element> allRows = getNodeList("tr", finalTableBodyNode);
		for (int i = 1; i < allRows.size() - 5; i++) {
			Element row = allRows.get(i);
			List<Element> allColumns = getNodeList("td", row);
			if (allColumns.size() == 6) {
				Element date = getNode("div", allColumns.get(5));
				String text = getNode("div", allColumns.get(1)).text();
				if (date != null) {
					try {
						if (text.length() > 90) {
							text = text.substring(0, 90);
						}
						if (!date.text().startsWith("Date(dd/mm/yyyy)")) {
							return getDay(date.text()) + " "
									+ DateUtil.getMonth(date.text())
									+ " is lates Update in SSC\n" + text;
						}
					} catch (Exception e) {
						throw new IOException(" " + date.text(), e);
					}
				}
			}

		}
		throw new RuntimeException("Unable to parse SSC");

	}

	private static Integer getDay(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= '0' && c <= '9') {
				int ret = c - '0';
				if (i + 1 < str.length() && str.charAt(i + 1) >= '0'
						&& str.charAt(i + 1) <= '9') {
					ret = ret * 10 + str.charAt(i + 1) - '0';
				}
				if (ret > 0 && ret < 32) {
					return ret;
				}
			}
		}
		return null;
	}

	private static List<Element> getNodeList(String string, Node nn) {
		List<Element> elements = new ArrayList<Element>();
		for (Node n : nn.childNodes()) {
			if (n instanceof Element) {
				Element element = (Element) n;
				if (element.tagName().equalsIgnoreCase(string)) {
					elements.add(element);
				}
			}
		}
		if (elements.size() == 0) {
			throw new RuntimeException("Empty Node in SSC");
		}
		return elements;
	}

	private static Element getNode(String string, Node nn) {
		for (Node n : nn.childNodes()) {
			if (n instanceof Element) {
				Element element = (Element) n;
				if (element.tagName().equalsIgnoreCase(string)) {
					return element;
				}
			}
		}
		if (nn instanceof Element) {
			Element element = (Element) nn;
			for (Element e : element.getAllElements()) {
				if (e.tagName().equalsIgnoreCase(string)) {
					return e;
				}
			}
		}
		return null;
	}

	public List<Node> getTables(Node node) {
		List<Node> nodes = new ArrayList<Node>();
		for (Node n : node.childNodes()) {
			n.getClass().getName();
		}
		return nodes;
	}

	private static void printTags(Node d) {

		System.out.println("#######################################");
		for (Node p : d.childNodes()) {
			if (p instanceof Element) {

				Element e = (Element) p;
				System.out.print(e.tagName() + " ");
			}
		}
		System.out.println("\n#######################################");
	}

	private static void printNode(Node d) {
		System.out.println("*******************************");
		System.out.println(d.childNodeSize());
		for (Node dd : d.childNodes()) {
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			String str = dd.toString();
			if (str.length() > 100) {
				{
					System.out.println(str.substring(0, 100));
				}
			} else {
				System.out.println(str);
			}
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		}
		System.out.println("**********************************");
	}

}
