/*
 * Copyright (C) 2015 Hippo Seven
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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Utils {

    /**
     * Close is. Don't worry about anything.
     *
     * @param is
     */
    public static void closeQuietly(Closeable is) {
        try {
            if (is != null)
                is.close();
        } catch (IOException e) {
            // Empty
        }
    }

    /**
     * Copy from is to os
     *
     * @param is
     * @param os
     * @throws java.io.IOException
     */
    public static void copy(InputStream is, OutputStream os) throws IOException {
        copy(is, os, 512 * 1024);
    }

    /**
     * Copy from is to os
     *
     * @param is
     * @param os
     * @param size Buffer size
     * @throws java.io.IOException
     */
    public static void copy(InputStream is, OutputStream os, int size) throws IOException {
        byte[] buffer = new byte[size];
        int bytesRead;
        while((bytesRead = is.read(buffer)) !=-1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
    }
}
