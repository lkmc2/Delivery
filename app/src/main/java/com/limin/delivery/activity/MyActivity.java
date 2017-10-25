package com.limin.delivery.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.limin.delivery.R;
import com.limin.delivery.utils.Database;
import com.limin.delivery.utils.ShareUtils;

/**
 * 用户信息界面（我的界面）
 */
public class MyActivity extends Activity {

    private TextView tvUserName; //已登录用户名显示控件
    private TextView tvNoDelivery; //未发货快递数量
    private TextView tvHaveDelivery; //已发货快递数量
    private TextView tvArrive; //已到达快递数量

    private Button btnLogout; //退出登陆按钮

    private Database mDatabase; //数据库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        initViews(); //初始化界面
        initDatas(); //初始化数据
        initEvents(); //初始化事件
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        //给退出登陆按钮设置点击事件
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.deleteLoginUser(MyActivity.this); //删除当前登陆的用户

                Toast.makeText(MyActivity.this, "退出登陆", Toast.LENGTH_SHORT).show();
                MyActivity.this.finish(); //关闭当前界面
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        mDatabase = Database.getInstance(this); //初始化数据库

        String loginUserName = ShareUtils.getLoginUserName(this); //获取已登录的用户名
        tvUserName.setText(loginUserName); //设置已登录的用户名到控件上

        int haveDeliveryCount = mDatabase.getHaveDeliveryCount();// 获取已发货快递的数量
        tvHaveDelivery.setText(String.valueOf(haveDeliveryCount)); //设置已发货数量到控件上

        int arriveCount = mDatabase.getArriveCount(); //获取已到达快递的数量
        tvArrive.setText(String.valueOf(arriveCount)); //设置已到达数量到控件上
    }

    /**
     * 初始化界面
     */
    private void initViews() {
        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvNoDelivery = (TextView) findViewById(R.id.tv_no_delivery);
        tvHaveDelivery = (TextView) findViewById(R.id.tv_have_delivery);
        tvArrive = (TextView) findViewById(R.id.tv_arrive);
        btnLogout = (Button) findViewById(R.id.btn_logout);
    }
}
