/**
* CreateAudioRunable.java 2019/9/26 9:47
* Copyright ©2019 www.bmsoft.com.cn All rights reserved.
* PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package com.bmsoft;

import java.util.Arrays;

/**
 * File：CreateAudioRunable.java<br>
 * Title: <br>
 * Description: <br>
 * Company: www.bmsoft.com.cn <br>
 * @author heyouchi
 */
public class CreateAudioRunable implements Runnable {

  private static byte[] firstaudio = new byte[0];
  private static int[] audiohead = new int[3];
  @Override
  public void run() {
    System.out.println("音频线程启动");
    byte[] outfile = new byte[0];
    int times = 0;
    boolean isout = true;
    while (true) {
      try {
        TagData tagData = HttpClientInboundHandler.tags.take();
        System.out.println("此tag为："+tagData);
        byte[] msg = tagData.getDatas();
        if(tagData.getIndex() == 1L){
          audiohead = AacEncode.getHeadConfig(msg);
          continue;
        }else if(tagData.getType() == 8){
          System.out.println("tag类型为音频");
          if(isout){
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
          String typ =  Integer.toBinaryString((dataType & 0xFF) + 0x100).substring(1);
          Integer geshi = Integer.valueOf(typ.substring(0,4),2);
          System.out.println("格式："+geshi);
          /*Integer cyl = Integer.valueOf(typ.substring(4,6),2);
          Integer cylenth = Integer.valueOf(typ.substring(6,7),2);
          Integer yptype = Integer.valueOf(typ.substring(7,8),2);*/
          System.out.println("msg为：");
          //ConmmonUtil.printByte(msg);
          byte[] audioData = Arrays.copyOfRange(msg, 13, msg.length-4);
          System.out.println("audioData为：");
          //ConmmonUtil.printByte(audioData);
          if(geshi == 10){
            byte[] tagaudiohead = AacEncode.getAllThreeConfig(audiohead, tagheader_datasize);
            audioData = ConmmonUtil.byteMerger(tagaudiohead, audioData);
            outfile = ConmmonUtil.byteMerger(outfile, audioData);
            //ConmmonUtil.printByte(outfile);
          }

          if(outfile.length > 100000){
            System.out.println("outfile bytes:");
            //ConmmonUtil.printByte(outfile);
            ConmmonUtil.getFile(outfile, "E:\\flvout\\", "outaudio"+(times++)+".aac");
            isout = true;
          }
        }

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
