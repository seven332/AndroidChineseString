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

import java.util.HashMap;
import java.util.Map;

public class ResponseCodeException extends Exception {

    private static final Map<Integer, String> ERROR_MESSAGE_ARRAY;
    private static final String DEFAULT_ERROR_MESSAGE = "Error response code";

    private final int mResponseCode;
    private final String mMessage;

    static {
        ERROR_MESSAGE_ARRAY = new HashMap<Integer, String>(24);

        ERROR_MESSAGE_ARRAY.put(400, "Bad Request");
        ERROR_MESSAGE_ARRAY.put(401, "Unauthorized");
        ERROR_MESSAGE_ARRAY.put(402, "Payment Required");
        ERROR_MESSAGE_ARRAY.put(403, "Forbidden");
        ERROR_MESSAGE_ARRAY.put(404, "Not Found");
        ERROR_MESSAGE_ARRAY.put(405, "Method Not Allowed");
        ERROR_MESSAGE_ARRAY.put(406, "Not Acceptable");
        ERROR_MESSAGE_ARRAY.put(407, "Proxy Authentication Required");
        ERROR_MESSAGE_ARRAY.put(408, "Request Timeout");
        ERROR_MESSAGE_ARRAY.put(409, "Conflict");
        ERROR_MESSAGE_ARRAY.put(410, "Gone");
        ERROR_MESSAGE_ARRAY.put(411, "Length Required");
        ERROR_MESSAGE_ARRAY.put(412, "Precondition Failed");
        ERROR_MESSAGE_ARRAY.put(413, "Request Entity Too Large");
        ERROR_MESSAGE_ARRAY.put(414, "Request-URI Too Long");
        ERROR_MESSAGE_ARRAY.put(415, "Unsupported Media Type");
        ERROR_MESSAGE_ARRAY.put(416, "Requested Range Not Satisfiable");
        ERROR_MESSAGE_ARRAY.put(417, "Expectation Failed");

        ERROR_MESSAGE_ARRAY.put(500, "Internal Server Error");
        ERROR_MESSAGE_ARRAY.put(501, "Not Implemented");
        ERROR_MESSAGE_ARRAY.put(502, "Bad Gateway");
        ERROR_MESSAGE_ARRAY.put(503, "Service Unavailable");
        ERROR_MESSAGE_ARRAY.put(504, "Gateway Timeout");
        ERROR_MESSAGE_ARRAY.put(505, "HTTP Version Not Supported");
    }

    public ResponseCodeException(int responseCode) {
        mResponseCode = responseCode;
        String message = ERROR_MESSAGE_ARRAY.get(responseCode);
        if (message != null) {
            mMessage = message;
        } else {
            mMessage = DEFAULT_ERROR_MESSAGE;
        }
    }

    public ResponseCodeException(int responseCode, String message) {
        mResponseCode = responseCode;
        mMessage = message;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

}
