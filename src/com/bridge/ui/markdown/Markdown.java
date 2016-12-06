package com.bridge.ui.markdown;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import com.vaadin.ui.TextArea;

public class Markdown {

	/***
	 * insertMarkdown inserts a string at the cursor position 
	 * of the text area 
	 * */

	public static void insert(TextArea area, String input) {
		int pos = area.getCursorPosition();
		String oldValue = area.getValue();
		String prefix = oldValue.substring(0, pos);
		String suffix = "";
		if (pos < oldValue.length()) {
			suffix = oldValue.substring(pos+1, oldValue.length());
		}
		area.setValue(prefix+input+suffix);
		area.focus();
		area.setCursorPosition(prefix.length()+input.length());
	}
	
	public static String buildLink(String text, String link) {
		if (text != null && !text.trim().isEmpty() && link != null && !link.trim().isEmpty()) {
			StringBuilder b = new StringBuilder();
			b.append('[');
			b.append(text);
			b.append(']');
			b.append('(');
			b.append(link);
			b.append(')');
			return b.toString();
		} else return "";
	}
	
	public static PegDownProcessor pegProcessor() {
		int p = Extensions.ABBREVIATIONS;
		int t = Extensions.TABLES;
		int q = Extensions.QUOTES;
		int ab = Extensions.ABBREVIATIONS;
		int hw = Extensions.HARDWRAPS;
		int sm = Extensions.SMARTS;
		int sty  = Extensions.SMARTYPANTS;
		int al = Extensions.AUTOLINKS;
		return new PegDownProcessor(p|t|q|ab|hw|sm|sty|al);
	}
	
}

