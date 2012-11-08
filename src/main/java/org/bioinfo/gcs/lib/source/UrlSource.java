package org.bioinfo.gcs.lib.source;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class UrlSource implements Source{
 
	@Override
	public InputStream getInputStream(String path) {
		try {
			URL url = new URL(path);
			if("https" == url.getProtocol()){
				HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
				return con.getInputStream();
			}else{
				HttpURLConnection con = (HttpURLConnection)url.openConnection();
				return con.getInputStream();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
