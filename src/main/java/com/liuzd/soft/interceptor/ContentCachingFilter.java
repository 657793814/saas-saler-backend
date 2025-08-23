package com.liuzd.soft.interceptor;

import com.liuzd.soft.utils.InputStreamUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 读取请求体后，原始的InputStream被消耗了无法二次读取，这个拦截器就是加载原始请求内容到内存中，支持多次读取
 * 但是要注意的时文件上传不需要做这样的处理
 *
 * @author: liuzd
 * @date: 2025/8/20
 * @email: liuzd2025@qq.com
 * @desc
 */
public class ContentCachingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest wrappedRequest = request;
        if (("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod()))) {
            // 检查是否为文件上传请求，如果是则不包装
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                // 对于文件上传请求，不进行包装，直接传递原始请求
                wrappedRequest = request;
            } else {
                // 对于非文件上传请求，进行包装以支持多次读取
                wrappedRequest = new MultiReadHttpServletRequest(request);
            }
        }
        filterChain.doFilter(wrappedRequest, response);
    }

    public static class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
        private byte[] body;

        public MultiReadHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            // 使用InputStream读取请求体，避免字符编码问题
            InputStream inputStream = request.getInputStream();
            ByteArrayOutputStream buffer = InputStreamUtils.readByteArrayOutputStream(inputStream);
            body = buffer.toByteArray();
        }


        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new ServletInputStream() {
                private ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);

                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    // Not implemented
                }

                @Override
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }

        public String getBody() {
            return new String(body, StandardCharsets.UTF_8);
        }
    }
}
