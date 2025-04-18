package com.baidu.flutter_bmflocation;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.flutter_bmflocation.handlers.HandlersFactory;
import com.baidu.location.LocationClient;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterBmflocationPlugin */
public class FlutterBmflocationPlugin implements FlutterPlugin, MethodCallHandler {

  private static MethodChannel locationChannel;

  private static MethodChannel geofenceChannel;
  private static Context mContext = null;

   /* 新版接口 */
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    if (null == flutterPluginBinding) {
      return;
    }
    if (null == mContext) {
      mContext = flutterPluginBinding.getApplicationContext();
    }
    initMethodChannel(flutterPluginBinding.getBinaryMessenger());
  }

  private void initMethodChannel(BinaryMessenger binaryMessenger) {
    if (null == binaryMessenger) {
      return;
    }

    locationChannel = new MethodChannel(binaryMessenger, Constants.MethodChannelName.LOCATION_CHANNEL);
    locationChannel.setMethodCallHandler(this);

    MethodChannelManager.getInstance().putLocationChannel(locationChannel);

    geofenceChannel = new MethodChannel(binaryMessenger, Constants.MethodChannelName.GEOFENCE_CHANNEL);
    geofenceChannel.setMethodCallHandler(this);

    MethodChannelManager.getInstance().putGeofenceChannel(geofenceChannel);
  }


  private static void initStaticMethodChannel(BinaryMessenger binaryMessenger) {
    if (null == binaryMessenger) {
      return;
    }

    FlutterBmflocationPlugin flutterBmfPlugin = new FlutterBmflocationPlugin();

    locationChannel = new MethodChannel(binaryMessenger, Constants.MethodChannelName.LOCATION_CHANNEL);
    locationChannel.setMethodCallHandler(flutterBmfPlugin);
    MethodChannelManager.getInstance().putLocationChannel(locationChannel);

    geofenceChannel = new MethodChannel(binaryMessenger, Constants.MethodChannelName.GEOFENCE_CHANNEL);
    geofenceChannel.setMethodCallHandler(flutterBmfPlugin);
    MethodChannelManager.getInstance().putLocationChannel(geofenceChannel);
  }


  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (mContext == null) {
      result.error("-1", "context is null", null);
    }

    if (call.method.equals(Constants.MethodID.LOCATION_SETAGREEPRIVACY)) {
      try {
        boolean isAgreePrivacy = (Boolean) call.arguments;
        LocationClient.setAgreePrivacy(isAgreePrivacy);
      } catch (Exception e) {
      }
    }
    
    HandlersFactory.getInstance(mContext).dispatchMethodHandler(mContext, call, result);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (locationChannel != null) {
      locationChannel.setMethodCallHandler(null);
      locationChannel = null;
    }
    if (geofenceChannel != null) {
      geofenceChannel.setMethodCallHandler(null);
      geofenceChannel = null;
    }
  }
}
