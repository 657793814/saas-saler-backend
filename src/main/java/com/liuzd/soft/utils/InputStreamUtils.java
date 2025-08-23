package com.liuzd.soft.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: liuzd
 * @date: 2025/8/22
 * @email: liuzd2025@qq.com
 * @desc
 */
public class InputStreamUtils {

    public static ByteArrayOutputStream readByteArrayOutputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer;
    }
}
