/**
 * Main.java 2019/9/25 10:14 Copyright ©2019 www.bmsoft.com.cn All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import com.bmsoft.ConmmonUtil;

/**
 * File：Main.java<br>
 * Title: <br>
 * Description: <br>
 * Company: www.bmsoft.com.cn <br>
 * @author heyouchi
 */
public class Main {

  public static void main(String[] args) {
    /*String str = "audiocodecid";
    byte[] strBytes = str.getBytes();
    byte[] intBytes = ConmmonUtil.intToBytes(8, 2);
    byte[] bytes = ConmmonUtil.byteMerger(ConmmonUtil.intToBytes(8, 2), strBytes);
    bytes = ConmmonUtil.byteMerger(bytes, ConmmonUtil.intToBytes(8, 1));
    //ConmmonUtil.print16(ConmmonUtil.doubleToByte(Double.valueOf("248.51")));
    ConmmonUtil.print16(ConmmonUtil.getmetadata(new Double("248.51"), new Double("1280.00"),
        new Double("800.00"), new Double("390.63"), new Double("20.00"), new Double("7.00"),
        new Double("125.00"), new Double("44100.00"), new Double("16.00"), 1,new Double("10.00"), new Double("39048968.00")));*/
    /*Integer i = 0xaf;
    String typ =  Integer.toBinaryString((i & 0xFF) + 0x100).substring(1);
    Integer geshi = Integer.valueOf(typ.substring(0,4),2);
    Integer cyl = Integer.valueOf(typ.substring(4,6),2);
    Integer cylenth = Integer.valueOf(typ.substring(6,7),2);
    Integer yptype = Integer.valueOf(typ.substring(7,8),2);
    System.out.println(i);
    System.out.println(geshi);
    System.out.println(cyl);
    System.out.println(cylenth);
    System.out.println(yptype);*/

    byte[] dataSize = new byte[4];
    dataSize[0] = 0;
    dataSize[1] = 0;
    dataSize[2] = 0;
    dataSize[3] = (byte)0x2e;
    int tagheader_datasize = ConmmonUtil.byteArrayToInt(dataSize);
    System.out.println(tagheader_datasize);
  }

  public static int bytes2Int(byte[] bytes) {
    //如果不与0xff进行按位与操作，转换结果将出错，有兴趣的同学可以试一下。
    int int1 = bytes[0] & 0xff;
    int int2 = (bytes[1] & 0xff) << 8;
    int int3 = (bytes[2] & 0xff) << 16;
    int int4 = (bytes[3] & 0xff) << 24;

    return int1 | int2 | int3 | int4;
  }
}
