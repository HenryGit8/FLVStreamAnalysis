/**
* HttpServerInboundHandler.java 2019/9/25 10:56
* Copyright ©2019 www.bmsoft.com.cn All rights reserved.
* PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package com.bmsoft;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;

/**
 * File：HttpServerInboundHandler.java<br>
 * Title: <br>
 * Description: <br>
 * Company: www.bmsoft.com.cn <br>
 * @author heyouchi
 */
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {

  private static Log log = LogFactory.getLog(HttpServerInboundHandler.class);

  private HttpRequest request;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception {
    if (msg instanceof HttpRequest) {
      request = (HttpRequest) msg;

      String uri = request.getUri();
      System.out.println("Uri:" + uri);
    }
    if (msg instanceof HttpContent) {
      HttpContent content = (HttpContent) msg;
      ByteBuf buf = content.content();
      System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
      buf.release();

      String res = "I am OK";
      FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
          OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
      response.headers().set(CONTENT_TYPE, "text/plain");
      response.headers().set(CONTENT_LENGTH,
          response.content().readableBytes());
      if (HttpUtil.isKeepAlive(request)) {
        response.headers().set(CONNECTION, Values.KEEP_ALIVE);
      }
      ctx.write(response);
      ctx.flush();
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error(cause.getMessage());
    ctx.close();
  }

}

