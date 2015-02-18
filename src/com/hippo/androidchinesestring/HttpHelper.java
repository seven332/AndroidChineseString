/*
 * Copyright (C) 2014-2015 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.androidchinesestring;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * It is not thread safe
 */
public class HttpHelper {

    private static final int MAX_RETRY = 3;
    private static final int MAX_REDIRECTS = 3;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    public static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/39.0.2171.95 Safari/537.36";
    public static final String USER_AGENT =
            System.getProperty("http.agent", DEFAULT_USER_AGENT);

    private static final int HTTP_TEMP_REDIRECT = 307;

    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String CHARSET_KEY = "charset=";

    private int mResponseCode = -1;

    public void reset() {
        mResponseCode = -1;
    }

    public int getResponseCode() {
        return mResponseCode;
    }

    /**
     * Get cookie for the url
     *
     * @param url the URL
     * @return the cookie for the URL
     */
    @SuppressWarnings("UnusedParameters")
    protected String getCookie(URL url) {
        return null;
    }

    /**
     * Store cookie for the url
     *
     * @param url   the URL
     * @param value the cookie for the URL
     */
    @SuppressWarnings("UnusedParameters")
    protected void storeCookie(URL url, String value) {
        // Empty
    }

    /**
     * Prepare before connecting
     *
     * @param conn the connection
     */
    protected void onBeforeConnect(HttpURLConnection conn) {
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
    }

    private Object doRequst(RequestHelper rh) throws Exception {
        URL url;
        HttpURLConnection conn = null;
        int redirectionCount = 0;
        try {
            url = rh.getUrl();
            while (redirectionCount++ < MAX_REDIRECTS) {
                Log.d("Request: " + url.toString());
                conn = (HttpURLConnection) url.openConnection();
                // Prepare before connecting
                onBeforeConnect(conn);
                // Set cookie
                String cookie = getCookie(url);
                if (cookie != null) {
                    conn.setRequestProperty("Cookie", cookie);
                }
                // Do custom staff
                rh.onBeforeConnect(conn);

                conn.connect();
                // Store cookie
                List<String> cookieList = conn.getHeaderFields().get("Set-Cookie");
                if (cookieList != null) {
                    for (String cookieTemp : cookieList) {
                        if (cookieTemp != null) {
                            storeCookie(url, cookieTemp);
                        }
                    }
                }
                final int responseCode = conn.getResponseCode();
                mResponseCode = responseCode;
                Log.d("Response code: " + responseCode);
                switch (responseCode) {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                    case HttpURLConnection.HTTP_SEE_OTHER:
                    case HTTP_TEMP_REDIRECT:
                        final String location = conn.getHeaderField("Location");
                        Log.d("New location: " + location);
                        conn.disconnect();
                        url = new URL(url, location);
                        break;
                    default:
                        return rh.onAfterConnect(conn);
                }
            }
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        throw new RedirectionException();
    }

    private Object requst(RequestHelper rh) throws Exception {
        Exception exception = null;
        for (int times = 0;
             times < MAX_RETRY && (times == 0 || rh.onRetry(exception));
             times++) {
            try {
                return doRequst(rh);
            } catch (Exception e) {
                exception = e;
            }
        }

        rh.onRequestFailed(exception);

        throw exception;
    }

    public static interface RequestHelper {

        /**
         * Get the URL to connect
         *
         * @return the URL to connect
         */
        public URL getUrl() throws MalformedURLException;

        /**
         * Add header or do something else for HttpURLConnection before connect
         *
         * @param conn the connection
         * @throws Exception
         */

        public void onBeforeConnect(HttpURLConnection conn) throws Exception;

        /**
         * Get what do you need from HttpURLConnection after connect
         * Return null means get error
         *
         * @param conn the connection
         * @return what you want to return
         * @throws Exception
         */
        public Object onAfterConnect(HttpURLConnection conn) throws Exception;

        /**
         * Retry http connecting, or stop
         *
         * @param previousException previous thrown
         * @return true for retry, false for stop
         */
        public boolean onRetry(Exception previousException);

        /**
         * Called when request failed by exception
         */
        public void onRequestFailed(Exception exception);
    }

    public static abstract class GetStringHelper implements RequestHelper {
        private final String mUrl;

        public GetStringHelper(String url) {
            mUrl = url;
        }

        @Override
        public URL getUrl() throws MalformedURLException {
            return new URL(mUrl);
        }

        @Override
        public void onBeforeConnect(HttpURLConnection conn)
                throws Exception {
            conn.addRequestProperty("Accept-Encoding", "gzip");
        }

        private String getCharsetFromContentType(String contentType) {
            if (contentType == null) {
                return null;
            }

            String[] values = contentType.split(";");
            for (String value : values) {
                value = value.trim();

                if (value.toLowerCase().startsWith(CHARSET_KEY)) {
                    return value.substring(CHARSET_KEY.length());
                }
            }

            return null;
        }

        private String getBody(HttpURLConnection conn)
                throws Exception {
            String body = null;
            InputStream is = null;
            FastByteArrayOutputStream fbaos = null;
            try {
                try {
                    // First try to get input stream
                    is = conn.getInputStream();
                } catch (Exception t){
                    // If we get error, get error stream
                    is = conn.getErrorStream();
                }
                String encoding = conn.getContentEncoding();
                if (encoding != null && encoding.equalsIgnoreCase("gzip"))
                    is = new GZIPInputStream(is);

                int length = conn.getContentLength();
                if (length >= 0)
                    fbaos = new FastByteArrayOutputStream(length);
                else
                    fbaos = new FastByteArrayOutputStream();

                Utils.copy(is, fbaos);

                // Get charset
                String charset = getCharsetFromContentType(conn.getContentType());
                if (charset == null) {
                    // We need to detect charset by Ourselves
                    UniversalDetector ud = new UniversalDetector(null);
                    byte[] bs = fbaos.getBuffer();
                    ud.handleData(bs, 0, bs.length);
                    ud.dataEnd();
                    charset = ud.getDetectedCharset();
                    if (charset == null) {
                        // WTF?
                        charset = DEFAULT_CHARSET;
                    }
                }

                body = fbaos.toString(charset);

            } finally {
                Utils.closeQuietly(is);
                Utils.closeQuietly(fbaos);
            }

            return body;
        }

        @Override
        public Object onAfterConnect(HttpURLConnection conn)
                throws Exception {
            return getBody(conn);
        }

        @Override
        public boolean onRetry(Exception previousException) {
            // Do not care about exception, just retry
            return true;
        }

        @Override
        public void onRequestFailed(Exception exception) {
            // Empty
        }
    }

    /**
     * RequstHelper for GET method
     */
    public static class GetHelper extends GetStringHelper {

        public GetHelper(String url) {
            super(url);
        }

        @Override
        public void onBeforeConnect(HttpURLConnection conn)
                throws Exception {
            super.onBeforeConnect(conn);
            conn.setRequestMethod("GET");
        }
    }

    /**
     * RequstHelper for post form data, use POST method
     */
    public static class PostFormHelper extends GetStringHelper {
        private final String[][] mArgs;

        public PostFormHelper(String url, String[][] args) {
            super(url);
            mArgs = args;
        }

        @Override
        public void onBeforeConnect(HttpURLConnection conn)
                throws Exception {
            super.onBeforeConnect(conn);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String[] arg : mArgs) {
                if (i != 0)
                    sb.append("&");
                sb.append(URLEncoder.encode(arg[0], "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(arg[1], "UTF-8"));
                i++;
            }
            out.writeBytes(sb.toString());
            out.flush();
            out.close();
        }
    }

    /**
     * Http GET method
     * @param url the url to get
     * @return body
     */
    public String get(String url) throws Exception {
        return (String) requst(new GetHelper(url));
    }

    /**
     * Post form data
     * @param url the url to post
     * @param args the form to post
     * @return body
     */
    public String postForm(String url, String[][] args) throws Exception {
        return (String) requst(new PostFormHelper(url, args));
    }
}
