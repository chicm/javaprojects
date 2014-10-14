package com.chicm.hieroglyph;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

public class String2Hieroglyph implements Iterator <Object>{
	
	private static String URLprefix = "http://localhost:8080/hieroglyph" ;
	
	private static HashMap<String, URL> map;
	
	private String english = ""; 
	
	private int index = -1;
	
	public String2Hieroglyph( String english, HttpServletRequest request) {
		this.english = english;
		index = 0;
		
		if(request != null)
			URLprefix = "http://" + request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath(); 
		init();
	}
	
	
	private synchronized void init() {
		if (map != null)
			return;
		map = new HashMap<String, URL> ();
		try {
			map.put("a", new URL(URLprefix + "/alphabet/a.gif"));
			map.put("b", new URL(URLprefix + "/alphabet/b.gif"));
			map.put("c", new URL(URLprefix + "/alphabet/c.gif"));
			map.put("d", new URL(URLprefix + "/alphabet/d.gif"));
			map.put("e", new URL(URLprefix + "/alphabet/e.gif"));
			map.put("f", new URL(URLprefix + "/alphabet/f.gif"));
			map.put("g", new URL(URLprefix + "/alphabet/g.gif"));
			map.put("h", new URL(URLprefix + "/alphabet/h.gif"));
			map.put("i", new URL(URLprefix + "/alphabet/i.gif"));
			map.put("j", new URL(URLprefix + "/alphabet/j.gif"));
			map.put("k", new URL(URLprefix + "/alphabet/k.gif"));
			map.put("l", new URL(URLprefix + "/alphabet/l.gif"));
			map.put("m", new URL(URLprefix + "/alphabet/m.gif"));
			map.put("n", new URL(URLprefix + "/alphabet/n.gif"));
			map.put("o", new URL(URLprefix + "/alphabet/o.gif"));
			map.put("p", new URL(URLprefix + "/alphabet/p.gif"));
			map.put("q", new URL(URLprefix + "/alphabet/q.gif"));
			map.put("r", new URL(URLprefix + "/alphabet/r.gif"));
			map.put("s", new URL(URLprefix + "/alphabet/s.gif"));
			map.put("t", new URL(URLprefix + "/alphabet/t.gif"));
			map.put("u", new URL(URLprefix + "/alphabet/u.gif"));
			map.put("v", new URL(URLprefix + "/alphabet/v.gif"));
			map.put("w", new URL(URLprefix + "/alphabet/w.gif"));
			map.put("x", new URL(URLprefix + "/alphabet/x.gif"));
			map.put("y", new URL(URLprefix + "/alphabet/y.gif"));
			map.put("z", new URL(URLprefix + "/alphabet/z.gif"));
			map.put("ch", new URL(URLprefix + "/alphabet/ch.gif"));
			map.put("sh", new URL(URLprefix + "/alphabet/sh.gif"));
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public boolean hasNext() {
		if(english.length() < 1 || index <0 || index >= english.length())
			return false;
		return true;
	}
	
	public Object next() {
		Object value = null;
		if(english.length() >= index +2 ) {
			String key = english.substring(index, index + 2) ;
			value = map.get(key);
			
			if(value != null)
				index ++;
		}
		
		if(value == null) {
			String key = english.substring(index, index+1);
			value = map.get(key);
		}
		
		if(value == null)
			value = english.substring(index, index+1);
		
		index ++;
		
		return value;
	}
	
	public void remove() {
		
	}

	public static void main(String[] args) {
		String s = "≥Ÿ≥–√Ùabc55";
		String pinyin = Chinese2Pinyin.convert(s);
		String2Hieroglyph convertor = new String2Hieroglyph(pinyin, null);
		while(convertor.hasNext()) {
			Object obj = convertor.next();
			System.out.println(obj.toString());
			System.out.println(obj.getClass());
			System.out.println(obj instanceof URL);
		}
	}

}
