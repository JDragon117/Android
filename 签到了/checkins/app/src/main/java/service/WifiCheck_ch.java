package service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.b140414.njupt.checkins.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bmob_table.Checkin_table;
import bmob_table.Leave_table;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SaveListener;

public class WifiCheck_ch extends Service {

    private WifiManager wifiM;
    private WifiCheckBinder wificheckbinder = new WifiCheckBinder();
    private static final int NOTIFICATION_FLAG = 1;
    private String account;
    private String name;
    private String BSSID;



    public class WifiCheckBinder extends Binder{
        public boolean quit = false;
        public boolean hasscanresult = false;
        public void ScanCheck(){
            Context context = getApplicationContext();
            final NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent();
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);
            final Notification notify3 = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.duidui)
                    .setTicker("怼怼签到提示:" + "您的Wifi不见啦！")
                    .setContentTitle("怼怼签到提示")
                    .setContentText("无法扫描到指定Wifi,请检查网络,请勿离场!")
                    .setContentIntent(contentIntent).setNumber(1).build();
            notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。

            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    while (!quit) {

                        wifiM = (WifiManager) getSystemService(WIFI_SERVICE);
                        wifiM.startScan();
                        List<ScanResult> mData= wifiM.getScanResults();
                        for (ScanResult a : mData) {
                            if(a.BSSID.equals(BSSID)){
                                hasscanresult = true;
                                break;
                            }
                        }

                        if (hasscanresult) {

                        } else {
                            notificationManager.notify(1, notify3);
                            Date date=new Date();
                            SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
                            final String ltime=sdf.format(date);
                            final String ltime2=sdf2.format(date);
                            Leave_table leave = new Leave_table();
                            leave.setAccount(account);
                            leave.setRealName(name);
                            leave.setLeaveTime(ltime);
                            leave.setLeaveType("中途离场"+ltime2);
                            leave.setBSSID(BSSID);
                            leave.save(WifiCheck_ch.this, new SaveListener(){
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(WifiCheck_ch.this, "中途离场信息已被记录！\n 姓名:"+name+"\n账号:"+account+"\n" +
                                            "时间："+ ltime, Toast.LENGTH_LONG).show();

                                }
                                @Override
                                public void onFailure(int code, String arg0) {
                                    Toast.makeText(WifiCheck_ch.this, "中途离场信息记录失败!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        try {
                            hasscanresult = false;
                            Thread.sleep(1000*10);    //每1分钟检查一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.start();


        }
        public void startCheck(){
            Context context = getApplicationContext();
            final NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent();
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);
           final Notification notify3 = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.duidui)
                    .setTicker("怼怼签到提示:" + "您的Wifi不见啦！")
                    .setContentTitle("怼怼签到提示")
                    .setContentText("Wifi连接已中断,请检查网络,请勿离场!")
                    .setContentIntent(contentIntent).setNumber(1).build();
            notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。

             new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    while (!quit) {

                        wifiM = (WifiManager) getSystemService(WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiM.getConnectionInfo();
                        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
                        String nowBSSID =wifiInfo == null ? "Scan": wifiInfo.getBSSID();
                        if (wifiM.isWifiEnabled() && ipAddress != 0 &&nowBSSID.equals(BSSID)) {

                        } else {
                            notificationManager.notify(1, notify3);
                            Date date=new Date();
                            SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
                            final String ltime=sdf.format(date);
                            final String ltime2=sdf2.format(date);
                            Leave_table leave = new Leave_table();
                            leave.setAccount(account);
                            leave.setRealName(name);
                            leave.setLeaveTime(ltime);
                            leave.setLeaveType("中途离场"+ltime2);
                            leave.setBSSID(BSSID);
                            leave.save(WifiCheck_ch.this, new SaveListener(){
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(WifiCheck_ch.this, "中途离场信息已被记录！\n 姓名:"+name+"\n账号:"+account+"\n" +
                                            "时间："+ ltime, Toast.LENGTH_LONG).show();

                                }
                                @Override
                                public void onFailure(int code, String arg0) {
                                    Toast.makeText(WifiCheck_ch.this, "中途离场信息记录失败!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        try {
                            Thread.sleep(1000*10);    //每1分钟检查一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.start();


        }
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Bundle bundle = (Bundle)intent.getExtras();
        account=bundle.getString("account");
        name = bundle.getString("name");
        BSSID = bundle.getString("BSSID");
        return wificheckbinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }
}
