/**
* ConmmonUtil.java 2019/9/25 15:36
* Copyright ©2019 www.bmsoft.com.cn All rights reserved.
* PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package com.bmsoft;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.omg.CORBA.Object;

/**
 * File：ConmmonUtil.java<br>
 * Title: <br>
 * Description: <br>
 * Company: www.bmsoft.com.cn <br>
 * @author heyouchi
 */
public class ConmmonUtil {

  public static void printByte(byte[] bytes){
    System.out.println();
    for (int i = 0; i < bytes.length; i++) {
      byte b = bytes[i];
      if (i % 20 == 0 && i != 0) {
        System.out.println(b + " ");
      } else {
        System.out.print(b + " ");
      }
    }
  }
  public static void print16(byte[] bytes){
    System.out.println();
    for (int i = 0; i < bytes.length; i++) {
      byte b = bytes[i];
      if (i % 20 == 0 && i != 0) {
        System.out.println(Integer.toHexString(b) + " ");
      } else {
        System.out.print(Integer.toHexString(b) + " ");
      }
    }
  }
  public static byte[] byteMerger(byte[] bt1, byte[] bt2){
    byte[] bt3 = new byte[bt1.length+bt2.length];
    System.arraycopy(bt1, 0, bt3, 0, bt1.length);
    System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
    return bt3;
  }
  public static byte[] intToBytes( int value , int size)
  {
    byte[] src = new byte[size];
    if (size == 1) {
      src[0] =  (byte) (value & 0xFF);
    }
    if (size == 2) {
      src[0] = (byte) ((value >> 8) & 0xFF);
      src[1] = (byte) (value & 0xFF);
    }
    if (size == 3) {
      src[0] =  (byte) ((value>>16) & 0xFF);
      src[1] =  (byte) ((value>>8) & 0xFF);
      src[2] =  (byte) (value & 0xFF);
    }
    if (size == 4) {
      src[0] =  (byte) ((value>>24) & 0xFF);
      src[1] =  (byte) ((value>>16) & 0xFF);
      src[2] =  (byte) ((value>>8) & 0xFF);
      src[3] =  (byte) (value & 0xFF);
    }
    return src;
  }
  public static int bytesToInt(byte[] src, int offset) {
    int value;
    value = (int) ((src[offset] & 0xFF)
        | ((src[offset+1] & 0xFF)<<8)
        | ((src[offset+2] & 0xFF)<<16)
        | ((src[offset+3] & 0xFF)<<24));
    return value;
  }
  /**
   * double到字节数组的转换.
   */
  public static byte[] doubleToByte(double num) {
    byte[] b = new byte[8];
    long l = Double.doubleToLongBits(num);
    for (int i = 7; i >= 0; i--) {
      b[i] = new Long(l).byteValue();
      l = l >> 8;
    }
    return b;
  }

  public static byte[] getmetadata(Double duration, Double width, Double height, Double videodatarate, Double framerate, Double videocodecid, Double audiodatarate,
      Double audiosamplerate, Double audiosamplesize, Integer stereo, Double audiocodecid, Double filesize){
    byte[] resultByte = new byte[0];
    resultByte = byteMerger(resultByte,  ConmmonUtil.intToBytes(2, 1));
    resultByte = byteMerger(resultByte,  ConmmonUtil.intToBytes(10, 2));
    resultByte = byteMerger(resultByte,  "onMetaData".getBytes());
    resultByte = byteMerger(resultByte,  ConmmonUtil.intToBytes(8, 1));
    resultByte = byteMerger(resultByte,  ConmmonUtil.intToBytes(12, 4));
    resultByte = byteMerger(resultByte, getOneArrayBytes("duration", duration));
    resultByte = byteMerger(resultByte, getOneArrayBytes("width", width));
    resultByte = byteMerger(resultByte, getOneArrayBytes("height", height));
    resultByte = byteMerger(resultByte, getOneArrayBytes("videodatarate", videodatarate));
    resultByte = byteMerger(resultByte, getOneArrayBytes("framerate", framerate));
    resultByte = byteMerger(resultByte, getOneArrayBytes("videocodecid", videocodecid));
    resultByte = byteMerger(resultByte, getOneArrayBytes("audiodatarate", audiodatarate));
    resultByte = byteMerger(resultByte, getOneArrayBytes("audiosamplerate", audiosamplerate));
    resultByte = byteMerger(resultByte, getOneArrayBytes("audiosamplesize", audiosamplesize));
    resultByte = byteMerger(resultByte, getOneArrayBytesInteger("stereo", stereo));
    resultByte = byteMerger(resultByte, getOneArrayBytes("audiocodecid", audiocodecid));
    resultByte = byteMerger(resultByte, getOneArrayBytes("filesize", filesize));
    resultByte = byteMerger(resultByte, ConmmonUtil.intToBytes(9, 4));
    byte[] header = new byte[0];
    header = byteMerger(header,  ConmmonUtil.intToBytes(18, 1));
    header = byteMerger(header,  ConmmonUtil.intToBytes(resultByte.length, 3));
    header = byteMerger(header,  ConmmonUtil.intToBytes(0, 4));
    header = byteMerger(header,  ConmmonUtil.intToBytes(0, 3));
    resultByte = byteMerger(header, resultByte);
    return resultByte;

  }

  private static byte[] getOneArrayBytes(String name, Double data){
    byte[] resultByte = new byte[0];
    int size = name.length();
    byte[] sizebyte = ConmmonUtil.intToBytes(size, 2);
    byte[] namebyte = name.getBytes();
    byte[] typebyte = ConmmonUtil.intToBytes(0, 1);
    byte[] valuebyte = ConmmonUtil.doubleToByte(data);
    resultByte = byteMerger(resultByte, sizebyte);
    resultByte = byteMerger(resultByte, namebyte);
    resultByte = byteMerger(resultByte, typebyte);
    resultByte = byteMerger(resultByte, valuebyte);
    return resultByte;
  }
  private static byte[] getOneArrayBytesInteger(String name, Integer data){
    byte[] resultByte = new byte[0];
    int size = name.length();
    byte[] sizebyte = ConmmonUtil.intToBytes(size, 2);
    byte[] namebyte = name.getBytes();
    byte[] typebyte = ConmmonUtil.intToBytes(1, 1);
    byte[] valuebyte = ConmmonUtil.intToBytes(data, 1);
    resultByte = byteMerger(resultByte, sizebyte);
    resultByte = byteMerger(resultByte, namebyte);
    resultByte = byteMerger(resultByte, typebyte);
    resultByte = byteMerger(resultByte, valuebyte);
    return resultByte;
  }
  public static int byteArrayToInt(byte[] bytes) {
    int value=0;
    for(int i = 0; i < 4; i++) {
      int shift= (3-i) * 8;
      value +=(bytes[i] & 0xFF) << shift;
    }
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

  public static byte[] getNewByte(int index, byte[] bytes){
    byte[] result = Arrays.copyOfRange(bytes, index, bytes.length);
    return result;
  }
}
