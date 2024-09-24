/*
 * Copyright (C)  2024 The RisingOS Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.risingos.wallpaperpicker.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.risingos.wallpaperpicker.MainApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class NetworkUtils {
    public static Bitmap getBitmapFromUrl(String url) {
        InputStream inputStream = getInputStreamFromUrl(url);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static String getStringFromUrl(String url) {
        InputStream inputStream = getInputStreamFromUrl(url);
        String string = new BufferedReader(new InputStreamReader(inputStream))
                .lines().parallel().collect(Collectors.joining("\n"));

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return string;
    }

    public interface onFetchCompleteCallback {
        void onFetchComplete(Object fetchedData);
    }

    private static InputStream getInputStreamFromUrl(String url) {
        InputStream input;

        if (url.isEmpty()) {
            Log.e("NetworkUtils", "url is empty");
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla/4.0");
            connection.connect();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            input = connection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return input;
    }

    public static class NetworkRunnable implements Runnable {
        private String localformat;
        private String localurl;
        private onFetchCompleteCallback localcallback;

        public NetworkRunnable(String format, String url, onFetchCompleteCallback callback) {
            localformat = format;
            localurl = url;
            localcallback = callback;
        }

        @Override
        public void run() {
            Object fetchedData = null;
            try {
                Method workMethod = NetworkUtils.class.getMethod("get" + localformat + "FromUrl", String.class);
                fetchedData = workMethod.invoke(NetworkUtils.class, localurl);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }

            final Object callbackObject = fetchedData;

            MainApplication.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    localcallback.onFetchComplete(callbackObject);
                }
            });
        }
    }
}
