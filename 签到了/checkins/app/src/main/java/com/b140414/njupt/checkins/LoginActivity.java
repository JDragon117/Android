package com.b140414.njupt.checkins;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import bmob_table.User;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import service.WifiCheck_ch;

public class LoginActivity extends AppCompatActivity {

    private EditText account_et;
    private EditText password_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this, "502aa80df90be300b2af61b48838cc90");

        account_et=(EditText)findViewById(R.id.account_et);
        password_et=(EditText)findViewById(R.id.password_et);
    }

    //登录按钮响应事件
    public void login_btn(View view){
        final String account=account_et.getText().toString();
        final String password=password_et.getText().toString();

        if(account.equals("")){
            Toast.makeText(LoginActivity.this, "请输入您的手机号！", Toast.LENGTH_LONG).show();
        }
        else if(password.equals("")){
            Toast.makeText(LoginActivity.this,"请输入您的密码！",Toast.LENGTH_LONG).show();
        }else {
            BmobQuery<User> query=new BmobQuery<>();
            query.addWhereEqualTo("account",account);
            query.findObjects(LoginActivity.this,new FindListener<User>() {
                @Override
                public void onSuccess(List<User> users) {
                    if(users.size()==0){
                        Toast.makeText(LoginActivity.this,"账户不存在！",Toast.LENGTH_LONG).show();
                        return;
                    }
                    for(User a:users){
                        if(!a.getPassword().equals(password)) {
                            Toast.makeText(LoginActivity.this, "账户或密码有误，请重新输入！", Toast.LENGTH_LONG).show();
                            return;
                        }

                        else{
                            Intent intent =new Intent();
                            intent.setClass(LoginActivity.this,MainActivity.class);
                            intent.putExtra("account",account);
                            intent.putExtra("realName",a.getRealName());

                            startActivity(intent);
                        }
                    }


                }

                @Override
                public void onError(int i, String s) {
                    Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //注册按钮响应事件
    public void register_btn(View view) {
        Intent intent=new Intent();
        intent.setClass(LoginActivity.this,Register.class);
        startActivity(intent);
    }


    //退出按钮响应事件
    public void quit_login_btn(View view) {
        finish();
    }
}
