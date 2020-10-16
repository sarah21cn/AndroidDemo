package com.ys.androiddemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ys.androiddemo.bean.UserBean;

import static org.junit.Assert.*;

/**
 * Created by yinshan on 2020/10/16.
 */
public class ProtobufTest {

  @Test
  public void testProtobuf(){
    UserBean.User.Builder builder = UserBean.User.newBuilder();
    UserBean.User user = builder.setId(123).setName("Sarah").build();

    // 序列化
    byte[] bytes = user.toByteArray();
    //反序列化
    try{
      UserBean.User user1 = UserBean.User.parseFrom(bytes);

      assertEquals(user1.getId(), 123);
      assertEquals(user1.getName(), "Sarah");
    }catch(InvalidProtocolBufferException e){
      e.printStackTrace();
    }

    //序列化
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try{
      user.writeTo(output);
      byte[] bytes1 = output.toByteArray();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
