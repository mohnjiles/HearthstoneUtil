package com.jt.hearthstone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;

// Gary's useful JsonUtil class
public class JsonUtil {

	private final static Gson GSON = new Gson();

	public static <T> T fromJson(InputStream is, Class<T> clazz) {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		char[] buff = new char[2048];
		int read;
		try {
			while((read = in.read(buff)) >= 0) {
				sb.append(new String(buff, 0, read));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			return GSON.fromJson(sb.toString(), clazz);

		} finally {
			try {
				in.close();
			} catch (Exception e) {
				System.out.println("oops" + e.getMessage()); 
			}
		}
	}
	
	public static <T> T fromJson(URL url, Class<T> clazz) {
		try {
			return fromJson(url.openStream(), clazz);
		} catch (IOException e) {
			return null;
		}
	}
	
	public static <T> T fromJsonUrl(String url, Class<T> clazz) {
		try {
			return fromJson(new URL(url), clazz);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
