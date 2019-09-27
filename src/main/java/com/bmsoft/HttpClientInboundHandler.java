/**
 * HttpClientInboundHandler.java 2019/9/25 10:58 Copyright ©2019 www.bmsoft.com.cn All rights
 * reserved. PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.bmsoft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * File：HttpClientInboundHandler.java<br> Title: <br> Description: <br> Company: www.bmsoft.com.cn
 * <br>
 *
 * @author heyouchi
 */
public class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {

  private static int times = 0;

  private byte[] outByte = new byte[0];

  public static byte[] header = new byte[13];

  public static BlockingQueue<TagData> tags = new ArrayBlockingQueue<TagData>(100);

  private static Long tagindex = 0L;

  byte[] outfile = new byte[0];
  boolean isout = true;
  private static int[] audiohead = new int[3];
  private Long datafilelenth = 0L;

  private OutputStream os = null;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msgs) throws Exception {
    if (msgs instanceof HttpResponse) {
      HttpResponse response = (HttpResponse) msgs;
      System.out.println("CONTENT_TYPE:" + response.headers().get(HttpHeaders.Names.CONTENT_TYPE));
    }
    if (msgs instanceof HttpContent) {
      openOutputStream();
      //System.out.println("times :" + times);
      times++;
      HttpContent content = (HttpContent) msgs;
      ByteBuf buf = content.content();
      byte[] req = new byte[buf.readableBytes()];
      buf.readBytes(req);
      buf.release();
      if (times == 1) {
        header = Arrays.copyOfRange(req, 0, 13);
        //putQuene(Arrays.copyOfRange(req, 0, 13));
        outByte = getNewByte(13, req);
      } else {
        outByte = byteMerger(outByte, req);
      }
      while (true) {
        if (outByte.length >= 10) {
          byte[] datalenth = new byte[4];
          datalenth[0] = outByte[3];
          datalenth[1] = outByte[2];
          datalenth[2] = outByte[1];
          datalenth[3] = 0;
          int lenth = bytesToInt(datalenth, 0);
          System.out.println("data lenth :" + outByte.length);
          System.out.println("need lenth :" + lenth);
          if (outByte.length >= 11 + lenth + 4) {
            System.out.println("analysisData");
            analysisData(lenth);
            //ConmmonUtil.printByte(outByte);
            System.out.println("last lenth :" + outByte.length);

          } else {
            break;
          }
        } else {
          break;
        }
      }
    }
  }

  private void analysisData(int lenth) throws IOException {
    byte[] msg = Arrays.copyOfRange(outByte, 0, 11 + lenth + 4);
    //System.out.println("类型为："+(outByte[0]+0));
    //putQuene(new TagData(outByte[0]+0, thisTag, tagindex++));
    outByte = getNewByte(11 + lenth + 4, outByte);
    if (tagindex == 1L) {
      audiohead = AacEncode.getHeadConfig(msg);
    } else if (msg[0] + 0 == 8) {
      if (isout) {
        outfile = new byte[0];
        isout = false;
      }
      byte[] dataSize = new byte[4];
      dataSize[0] = 0;
      dataSize[1] = msg[1];
      dataSize[2] = msg[2];
      dataSize[3] = msg[3];
      int tagheader_datasize = ConmmonUtil.byteArrayToInt(dataSize);
      byte dataType = msg[11];
      String typ = Integer.toBinaryString((dataType & 0xFF) + 0x100).substring(1);
      Integer geshi = Integer.valueOf(typ.substring(0, 4), 2);
      byte[] audioData = Arrays.copyOfRange(msg, 13, msg.length - 4);
      if (geshi == 10) {
        byte[] tagaudiohead = AacEncode.getAllThreeConfig(audiohead, tagheader_datasize);
        os.write(tagaudiohead);
        os.write(audioData);
        datafilelenth = datafilelenth + audioData.length + tagaudiohead.length;
      }
      /*if (datafilelenth > 200000) {
      }*/
      os.flush();
      closeOutputStream();
      isout = true;
      datafilelenth = 0L;
    }

    tagindex++;
  }

  private void openOutputStream() {
    if (os == null) {
      try {
        File desFile = new File("E:\\flvout\\", "outaudio" + times + ".aac");
        os = new BufferedOutputStream(new FileOutputStream(desFile));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void closeOutputStream() {
    if (os != null) {
      try {
        os.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    os = null;
  }

  private void putQuene(TagData thisTag) {
    //System.out.println();
    //System.out.println("-------------------tag--------------------");
    tags.offer(thisTag);
    //ConmmonUtil.printByte(thisTag.getDatas());
  }

  private byte[] getNewByte(int index, byte[] bytes) {
    byte[] result = Arrays.copyOfRange(bytes, index, bytes.length);
    return result;
  }

  public static byte[] intToBytes(int value) {
    byte[] src = new byte[4];
    src[3] = (byte) ((value >> 24) & 0xFF);
    src[2] = (byte) ((value >> 16) & 0xFF);
    src[1] = (byte) ((value >> 8) & 0xFF);
    src[0] = (byte) (value & 0xFF);
    return src;
  }

  public static int bytesToInt(byte[] src, int offset) {
    int value;
    value = (int) ((src[offset] & 0xFF)
        | ((src[offset + 1] & 0xFF) << 8)
        | ((src[offset + 2] & 0xFF) << 16)
        | ((src[offset + 3] & 0xFF) << 24));
    return value;
  }

  public static void getFile(byte[] bfile, String filePath, String fileName) {
    BufferedOutputStream bos = null;
    FileOutputStream fos = null;
    File file = null;
    try {
      File dir = new File(filePath);
      if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
        dir.mkdirs();
      }
      file = new File(filePath + "\\" + fileName);
      fos = new FileOutputStream(file);
      bos = new BufferedOutputStream(fos);
      bos.write(bfile);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (bos != null) {
        try {
          bos.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
  }

  public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
    byte[] bt3 = new byte[bt1.length + bt2.length];
    System.arraycopy(bt1, 0, bt3, 0, bt1.length);
    System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
    return bt3;
  }
}
