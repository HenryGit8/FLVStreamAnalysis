/**
* HttpClient.java 2019/9/25 10:57
* Copyright ©2019 www.bmsoft.com.cn All rights reserved.
* PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package com.bmsoft;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * File：HttpClient.java<br>
 * Title: <br>
 * Description: <br>
 * Company: www.bmsoft.com.cn <br>
 * @author heyouchi
 */
public class HttpClient {
  public void connect(String host, int port, String path) throws Exception {
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      Bootstrap b = new Bootstrap();
      b.group(workerGroup);
      b.channel(NioSocketChannel.class);
      b.option(ChannelOption.SO_KEEPALIVE, true);
      b.handler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
          ch.pipeline().addLast(new HttpResponseDecoder());
          ch.pipeline().addLast(new HttpRequestEncoder());
          ch.pipeline().addLast(new HttpClientInboundHandler());
        }
      });

      // Start the client.
      ChannelFuture f = b.connect(host, port).sync();

      URI uri = new URI(path);
      //String msg = "Are you ok?";
      DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
          uri.toASCIIString()/*, Unpooled.wrappedBuffer(msg.getBytes("UTF-8"))*/);

      // 构建http请求
      request.headers().set(HttpHeaders.Names.HOST, host);
      request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
      request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
      request.headers().set(Names.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
      // 发送http请求
      f.channel().write(request);
      f.channel().flush();
      f.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
    }

  }

  private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(10, 11, 10L, TimeUnit.SECONDS,
      new LinkedBlockingQueue<Runnable>(10000));
  public static void main(String[] args) throws Exception {
    /*Runnable task = new CreateVideoRunable() ;
    executor.execute(task);*/
    /*Runnable task1 = new CreateAudioRunable() ;
    executor.execute(task1);*/
    HttpClient client = new HttpClient();
    //client.connect("3891.liveplay.myqcloud.com", 80, "/live/3891_user_b0e3e419_4796.flv");
    client.connect("3891.liveplay.myqcloud.com", 80, "/live/3891_user_95b590a0_b9fd.flv");
  }

}
