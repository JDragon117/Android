package com.example.dell.calculate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements View.OnClickListener {

    Button btn_0;
    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;
    Button btn_5;
    Button btn_6;
    Button btn_7;
    Button btn_8;
    Button btn_9;
    Button btn_point;
    Button btn_clear;
    Button btn_del;
    Button btn_plus;
    Button btn_minus;
    Button btn_multiply;
    Button btn_divide;
    Button btn_equal;
    EditText ed_input;
    boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_0 = (Button) findViewById(R.id.btn_0);
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_6 = (Button) findViewById(R.id.btn_6);
        btn_7 = (Button) findViewById(R.id.btn_7);
        btn_8 = (Button) findViewById(R.id.btn_8);
        btn_9 = (Button) findViewById(R.id.btn_9);
        btn_point = (Button) findViewById(R.id.btn_point);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_del = (Button) findViewById(R.id.btn_del);
        btn_plus = (Button) findViewById(R.id.btn_plus);
        btn_minus = (Button) findViewById(R.id.btn_minus);
        btn_multiply = (Button) findViewById(R.id.btn_multiply);
        btn_divide = (Button) findViewById(R.id.btn_divide);
        btn_equal = (Button) findViewById(R.id.btn_equal);
        ed_input = (EditText) findViewById(R.id.et_input);


        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_point.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        btn_plus.setOnClickListener(this);
        btn_minus.setOnClickListener(this);
        btn_multiply.setOnClickListener(this);
        btn_divide.setOnClickListener(this);
        btn_equal.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        String str = ed_input.getText().toString();
        switch (view.getId()){
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
            case R.id.btn_point:
                if(flag){
                    flag = false;
                    str = "";
                    ed_input.setText("");
                }
                ed_input.setText(str+((Button)view).getText());
                break;
            case R.id.btn_plus:
            case R.id.btn_minus:
            case R.id.btn_multiply:
            case R.id.btn_divide:
                if(flag){
                    flag = false;
                    str = "";
                    ed_input.setText("");
                }
                ed_input.setText(str+" "+((Button)view).getText()+" ");
                break;
            case R.id.btn_clear:
                flag = false;
                ed_input.setText("");
                break;
            case R.id.btn_del:
                if(flag){
                    flag = false;
                    str = "";
                    ed_input.setText("");
                }
                else if(str!=null&&!str.equals("")){
                    ed_input.setText(str.substring(0,str.length()-1));
                }
                break;
            case R.id.btn_equal:
                GetResult();
                break;
        }
    }
    public void GetResult(){
        String str2 = ed_input.getText().toString();
        double result=0;
        if(flag){
            flag = false;
        }
        flag = true;
        if(str2==null|| str2.equals("")){
            return;
        }
        else if(!str2.contains(" ")){
            return;
        }
        else {
            String s1 = str2.substring(0,str2.indexOf(" "));
            String op = str2.substring(str2.indexOf(" ")+1,str2.indexOf(" ")+2);
            String s2 = str2.substring(str2.indexOf(" ")+3);
            if(!s1.equals("")&&!s2.equals("")) {
                double d1 = Double.parseDouble(s1);
                double d2 = Double.parseDouble(s2);
                if (op.equals("+")) {
                    result = d1 + d2;
                } else if (op.equals("－")) {
                    result = d1 - d2;
                } else if (op.equals("x")) {
                    result = d1 * d2;
                } else {
                    if (d2 == 0) {
                        result = 0;
                    } else {
                        result = d1 / d2;
                    }
                }
                if (!s1.contains(".")&&!s2.contains(".")&&!op.equals("÷")){
                    int r = (int)result;
                    ed_input.setText(r+"");
                }else{
                    ed_input.setText(result+"");
                }
            }
        }
    }
}
