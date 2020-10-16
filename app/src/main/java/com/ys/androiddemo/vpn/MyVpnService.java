package com.ys.androiddemo.vpn;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import android.content.Intent;
import android.net.VpnService;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * Created by yinshan on 2020/10/16.
 */
public class MyVpnService extends VpnService {

  private ParcelFileDescriptor localTunnel;

  @Override
  public void onCreate() {
    super.onCreate();

    localTunnel = config();
    newThread.start();
    //out.write(packet.array(), 0, length);

  }

  Thread newThread = new Thread() {
    @Override
    public void run() {
      try{
        while (true){
          FileInputStream in = new FileInputStream(localTunnel.getFileDescriptor());
          FileOutputStream out = new FileOutputStream(localTunnel.getFileDescriptor());

          ByteBuffer packet = ByteBuffer.allocate(32767);

          int length = in.read(packet.array());
          Log.d("test", "length: " + length);

//          Log.d("test", StandardCharsets.UTF_8.decode(packet).toString());
//
//          packet.flip(); // flip the buffer for reading
//          byte[] bytes = new byte[packet.remaining()]; // create a byte array the length of the number of bytes written to the buffer
//          packet.get(bytes); // read the bytes that were written
//          Log.d("test", new String(bytes));

          Thread.sleep(1000);
        }
      }catch (Exception e){
        e.printStackTrace();
      }
    }
  };

  private ParcelFileDescriptor config(){
    ParcelFileDescriptor localTunnel = new Builder()
        .setMtu(1024)
        .setSession("test")
        .addAddress("192.168.2.2", 24)
        .addRoute("0.0.0.0", 0)
//        .addDnsServer("192.168.1.1")
        .establish();
    return localTunnel;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }
}
