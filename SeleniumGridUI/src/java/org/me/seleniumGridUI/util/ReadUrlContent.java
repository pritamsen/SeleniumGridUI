/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 *
 * @author vkumar
 */
public class ReadUrlContent {

    private final String _url;

    public ReadUrlContent(String url) {
        _url = url;
    }

    public int getStatusCode() throws Throwable {
        URL url = new URL(_url);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        return http.getResponseCode();
    }

    public String getCompleteContent() throws Throwable {
        InputStream is = new URL(_url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        return readAll(rd);
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
