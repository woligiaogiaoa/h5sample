package com.jiuzhou.oversea.ldxy.offical.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

public abstract class ResponseBodyInterceptor implements Interceptor {

  @NotNull
  @Override
  public Response intercept(@NotNull Chain chain) throws IOException {
    Request request = chain.request();
    String url = request.url().toString();
    Response response = chain.proceed(request);
    ResponseBody responseBody = response.body();
    if (responseBody != null) {
      long contentLength = responseBody.contentLength();
      BufferedSource source = responseBody.source();
      source.request(Long.MAX_VALUE);
      Buffer buffer = source.buffer();

      if ("gzip".equals(response.headers().get("Content-Encoding"))) {
        GzipSource gzippedResponseBody = new GzipSource(buffer.clone());
        buffer = new Buffer();
        buffer.writeAll(gzippedResponseBody);
      }

      MediaType contentType = responseBody.contentType();
      Charset charset;

      if (contentType == null || contentType.charset(Charset.forName("UTF-8")) == null) {
        charset = Charset.forName("UTF-8");
      } else {
        charset = contentType.charset(Charset.forName("UTF-8"));
      }

      if (charset != null && contentLength != 0L) {
        return intercept(response,url, buffer.clone().readString(charset));
      }
    }
    return response;
  }

  abstract Response intercept(@NotNull Response response, String url, String body) throws IOException;
}
