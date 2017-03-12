package com.b140414.njupt.checkins;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import bmob_table.Checkin_table;
import bmob_table.Leave_table;
import service.WifiCheck_ch;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity {

    private TextView hello;
    public static String IP;
    public static String MAC;
    public static String BSSID;
    public String realName ;
    public String account ;
    private WifiCheck_ch.WifiCheckBinder wificheckbinder;
    private Boolean hasChecked = false;
    private List<Leave_table> leave_half = new ArrayList<Leave_table>();
    private Intent bindIntent;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            wificheckbinder = (WifiCheck_ch.WifiCheckBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hello = (TextView) findViewById(R.id.hello);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        realName = bundle.getString("realName");
        account = bundle.getString("account");
        hello.setText("你好，" + realName);
        //BindService传入账号与姓名信息
        bindIntent = new Intent(MainActivity.this,WifiCheck_ch.class);
        bindIntent.putExtra("account",account);
        bindIntent.putExtra("name",realName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(hasChecked){
            Date date=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
            final String ltime=sdf.format(date);
            final String ltime2=sdf2.format(date);
            Leave_table leave = new Leave_table();
            leave.setAccount(account);
            leave.setRealName(realName);
            leave.setLeaveTime(ltime);
            leave.setLeaveType("签退离场"+ltime2);
            leave.setBSSID(BSSID);
            leave.save(MainActivity.this, new SaveListener(){
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "签退离场信息已被记录！\n 姓名:"+realName+"\n账号:"+account+"\n时间："+ ltime, Toast.LENGTH_LONG).show();

                }
                @Override
                public void onFailure(int code, String arg0) {
                    Toast.makeText(MainActivity.this, "签退离场信息记录失败!", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    //检查连接的是什么网络
    public  Integer checkWifi(Context context) {
        ConnectivityManager ConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo =  ConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
            if (mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return 2;  //返回1，连接的是移动网络
            } else if (mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return 2;  //返回2，连接的是wifi
            }
        } else {
            return 3; //返回3，没有连接。
        }
        return 3;
    }

    //获取IP
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("IP 地址为：", ex.toString());
        }
        return null;
    }


    //获取MAC
    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    //获取链接的wifi的MAC地址
    public String getLinkMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getBSSID();
    }
    //连着wifi的签到按钮事件
    public void Dao(View view) {
        if (checkWifi(MainActivity.this) == 1) {
            Toast.makeText(MainActivity.this, "您连接的是移动网络，签到失败！", Toast.LENGTH_LONG).show();
        } else if (checkWifi(MainActivity.this) == 3) {
            Toast.makeText(MainActivity.this, "您没有连接网络，签到失败！", Toast.LENGTH_LONG).show();
        } else if (checkWifi(MainActivity.this) == 2) {
            Date date=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            final String stime=sdf.format(date);
            MAC=getLocalMacAddress();
            IP=getLocalIpAddress();
            BSSID = getLinkMacAddress();
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            realName = bundle.getString("realName");
            account = bundle.getString("account");

            Checkin_table qiandao=new Checkin_table();
            qiandao.setAccount(account);
            qiandao.setRealName(realName);
            qiandao.setDaoTime(stime);
            qiandao.setIP(IP);
            qiandao.setMAC(MAC);
            qiandao.setBSSID(BSSID);
            //BindService传入BSSID参数
            bindIntent.putExtra("BSSID",BSSID);
            bindService(bindIntent,connection,BIND_AUTO_CREATE);
            if(!hasChecked) {
                qiandao.save(MainActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "签到成功！\n IP:" + IP + "\nMAC 地址:" + MAC + "\n时间：" + stime, Toast.LENGTH_LONG).show();
                        wificheckbinder.startCheck();
                        hasChecked = true;
                    }

                    @Override
                    public void onFailure(int code, String arg0) {
                        Toast.makeText(MainActivity.this, "签到失败!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                Toast.makeText(MainActivity.this, "你已经签过到了!", Toast.LENGTH_LONG).show();
            }
        }
    }
    //本人签到信息按钮事件
    public void Info_Dao(View view) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String account = bundle.getString("account");
        BmobQuery<Checkin_table> query=new BmobQuery<>();
        query.addWhereEqualTo("account",account);
        query.findObjects(MainActivity.this,new FindListener<Checkin_table>() {
            @Override
            public void onSuccess(List<Checkin_table> qianDaos) {

                String str="";
                for(Checkin_table a:qianDaos){

                    str+="时间:"+a.getDaoTime()+"\nMAC:"+a.getMAC()+"\nIP:"+a.getIP()+"\n\n";
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("签到详情");
                builder.setMessage(str);
                builder.create().show();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MainActivity.this,"查询失败！"+s,Toast.LENGTH_LONG);
            }
        });
    }

    //点名按钮事件
    public void call(View view){
        BmobQuery<Checkin_table> query1 = new BmobQuery<>();
        BmobQuery<Checkin_table> query2 = new BmobQuery<>();
        //获取当前时间
        Date todaydate=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String s = format.format(todaydate);
        BSSID = getLinkMacAddress();
        if(BSSID ==null) {
            BSSID = getLocalMacAddress();
        }
        //当前时间与check_table表中的签到时间进行匹配
            query1.addWhereEqualTo("DaoTime", s);
            query2.addWhereEqualTo("BSSID", BSSID);
            List<BmobQuery<Checkin_table>> andQuerys = new ArrayList<BmobQuery<Checkin_table>>();
            andQuerys.add(query1);
            andQuerys.add(query2);
            BmobQuery<Checkin_table> query_and = new BmobQuery<>();
            query_and.and(andQuerys);
            query_and.findObjects(MainActivity.this, new FindListener<Checkin_table>() {
                @Override
                public void onSuccess(List<Checkin_table> qianDao) {

                    String str = "";
                    for (Checkin_table a : qianDao) {
                        str += a.getRealName() + "\n\n";
                    }
                    String str1 = "查询成功：共" + qianDao.size() + "个人签到。";
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    Date todaydate = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String s = format.format(todaydate);
                    builder.setTitle(s + "的签到人员详情");
                    builder.setMessage(str + str1);
                    builder.create().show();
                }

                @Override
                public void onError(int i, String s) {
                    Toast.makeText(MainActivity.this, "查询失败！" + s, Toast.LENGTH_LONG);
                }
            });

    }

    //查看离场信息
    public void LiChang(View view) {
        BmobQuery<Leave_table> query2 = new BmobQuery<>();
        BmobQuery<Leave_table> query1 = new BmobQuery<>();
        //获取当前时间
        Date todaydate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String s = format.format(todaydate);
        //当前时间与leave_table表中的leavetype+时间 && BSSID 进行匹配
        query2.addWhereEqualTo("LeaveType", "中途离场"+s);
        BSSID = getLinkMacAddress();
        query1.addWhereEqualTo("BSSID", BSSID);

        List<BmobQuery<Leave_table>> andQuerys = new ArrayList<BmobQuery<Leave_table>>();
        andQuerys.add(query1);
        andQuerys.add(query2);
        BmobQuery<Leave_table> query_and = new BmobQuery<>();
        query_and.and(andQuerys);
        query_and.findObjects(MainActivity.this, new FindListener<Leave_table>() {
            @Override
            public void onSuccess(List<Leave_table> leave) {
                String str = "";
                for (Leave_table sjk : leave) {
                    Boolean hasName = false;
                    for(Leave_table bd : leave_half){
                        if(sjk.getRealName().equals(bd.getRealName())) {
                            hasName = true;
                            break;
                        }
                    }
                    if(!hasName){
                        leave_half.add(sjk);
                    }

                }
                for (Leave_table a : leave_half) {
                    str += a.getRealName() + "\n\n";
                }
                String str1 = "查询成功：共" + leave_half.size() + "个人中途离场。";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                Date todaydate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String s = format.format(todaydate);
                builder.setTitle(s + "的中途离场人员详情");
                builder.setMessage(str + str1);
                builder.create().show();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MainActivity.this, "查询失败！" + s, Toast.LENGTH_LONG);
            }
        });
    }

    public void Quit(View view) {
        finish();
    }
    public void ScanA (View view){
        Intent intent =new Intent();
        intent.putExtra("account",account);
        intent.putExtra("realName",realName);
        intent.setClass(MainActivity.this,ScanActivity.class);
        startActivity(intent);
        finish();
    }
}
