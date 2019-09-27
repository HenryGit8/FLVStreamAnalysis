/**
* TagData.java 2019/9/25 16:13
* Copyright ©2019 www.bmsoft.com.cn All rights reserved.
* PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package com.bmsoft;

import java.util.Arrays;

/**
 * File：TagData.java<br>
 * Title: <br>
 * Description: <br>
 * Company: www.bmsoft.com.cn <br>
 * @author heyouchi
 */
public class TagData {

  private int type;

  private byte[] datas;

  private Long index;

  public TagData(int type, byte[] datas) {
    this.type = type;
    this.datas = datas;
  }

  public TagData(int type, byte[] datas, Long index) {
    this.type = type;
    this.datas = datas;
    this.index = index;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public byte[] getDatas() {
    return datas;
  }

  public void setDatas(byte[] datas) {
    this.datas = datas;
  }

  public Long getIndex() {
    return index;
  }

  public void setIndex(Long index) {
    this.index = index;
  }

  @Override
  public String toString() {
    return "TagData{" +
        "type=" + type +
        ", datas=" + Arrays.toString(datas) +
        ", index=" + index +
        '}';
  }
}
