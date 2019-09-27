/**
* CreateVideoRunable.java 2019/9/26 9:45
* Copyright ©2019 www.bmsoft.com.cn All rights reserved.
* PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package com.bmsoft;

/**
 * File：CreateVideoRunable.java<br>
 * Title: <br>
 * Description: <br>
 * Company: www.bmsoft.com.cn <br>
 * @author heyouchi
 */
public class CreateVideoRunable implements Runnable {

  private static byte[] firstaudio = new byte[0];
  private static byte[] firstvideo = new byte[0];
  @Override
  public void run() {
    byte[] outfile = new byte[0];
    int maxtag = 1000;
    int nowtag = 0;
    int times = 0;
    boolean isout = true;
    boolean isfirst = true;
    while (true){
      try {
        TagData tagData = HttpClientInboundHandler.tags.take();
        byte[] msg = tagData.getDatas();
        if(tagData.getIndex() == 1L){
          firstaudio = msg;
        }
        if(tagData.getIndex() == 2L){
          firstvideo = msg;
        }
        if(isout){
          outfile = new byte[0];
          isout = false;
        }
        if(tagData.getType() != 18){
          outfile = ConmmonUtil.byteMerger(outfile, msg);
        }
        nowtag++;
        if(nowtag > maxtag){
          System.out.println("outfile bytes:");
          ConmmonUtil.printByte(outfile);
          byte[] header = new byte[13];
          header[0] = 70;
          header[1] = 76;
          header[2] = 86;
          header[3] = 1;
          header[4] = 5;
          header[5] = 0;
          header[6] = 0;
          header[7] = 0;
          header[8] = 9;
          header[9] = 0;
          header[10] = 0;
          header[11] = 0;
          header[12] = 0;
          byte[] metadata = ConmmonUtil.getmetadata(new Double("10"), new Double("368.00"),
              new Double("640.00"), new Double("183.00"), new Double("20.00"), new Double("7.00"),
              new Double("125.00"), new Double("44100.00"), new Double("16.00"), 1,new Double("120.00"), new Double(outfile.length+12));
          metadata = ConmmonUtil.byteMerger(metadata, ConmmonUtil.intToBytes(metadata.length, 4));

          header = ConmmonUtil.byteMerger(header, metadata);
          if(isfirst){
            isfirst = false;
          }else {
            header = ConmmonUtil.byteMerger(header, firstaudio);
            header = ConmmonUtil.byteMerger(header, firstvideo);
          }
          outfile = ConmmonUtil.byteMerger(header, outfile);
          ConmmonUtil.getFile(outfile, "E:\\flvout\\", "outvideo"+(times++)+".flv");
          isout = true;
          nowtag = 0;
        }
            /*System.out.println("-------------------tag--------------------");
            for (int i = 0; i < msg.length; i++) {
              byte b = msg[i];
              if (i % 20 == 0) {
                System.out.println(b + " ");
              } else {
                System.out.print(b + " ");
              }
            }*/
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
