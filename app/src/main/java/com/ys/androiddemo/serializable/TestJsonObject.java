package com.ys.androiddemo.serializable;

import java.io.Serializable;

import com.google.gson.JsonObject;

/**
 * Created by yinshan on 2021/1/10.
 */
public class TestJsonObject implements Serializable {

  // JsonObject没有实现Serializable接口，遇到需要序列化的场景，如通过intent传递，必崩
  // Parcel.readSerializable & Parcel.writeSerializable 会崩溃
  public JsonObject object;

  public TestJsonObject() {
    object = new JsonObject();
  }
}
