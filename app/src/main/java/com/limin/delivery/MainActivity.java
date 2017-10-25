package com.limin.delivery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.limin.delivery.activity.DetailActivity;
import com.limin.delivery.activity.LoginActivity;
import com.limin.delivery.activity.MoreHistoryActivity;
import com.limin.delivery.activity.MyActivity;
import com.limin.delivery.adapter.HistoryAdapter;
import com.limin.delivery.bean.HistoryData;
import com.limin.delivery.utils.CompanyInfo;
import com.limin.delivery.utils.Database;
import com.limin.delivery.utils.ShareUtils;

/**
 * 主界面（快递查询界面）
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private Button btnLogin; //跳转登陆界面按钮
    private EditText etNumber; //快递单号输入框
    private Spinner spName; //快递公司名下拉选择框
    private Button btnSearch; //查询按钮按钮
    private ListView lvHistory; //搜索历史列表
    private TextView tvNoHistory; //暂无搜索历史提示文字
    private Button btnMore; //查看更多搜索历史按钮

    private Database mDatabase; //数据库
    private HistoryAdapter mAdapter; //查询历史适配器

    private List<HistoryData> historyList = new ArrayList<HistoryData>(); //搜索历史列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews(); //初始化View
        initDatas(); //初始化数据
        initEvents(); //初始化事件
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        //给跳转登陆界面按钮设置点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent; //页面跳转意图

                if (!ShareUtils.checkIsHaveLogin(MainActivity.this)) { //如果未登录
                    //跳转到登陆界面
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                } else {
                    //跳转到我的界面
                    intent = new Intent(MainActivity.this, MyActivity.class);
                }

                startActivityForResult(intent, 100); //开启页面跳转
            }
        });

        //给搜索按钮设置点击事件
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从输入框中获取快递公司名和快递单号
                String deliveryName = spName.getSelectedItem().toString();
                String deliveryNumber = etNumber.getText().toString();

                if ("".equals(deliveryNumber)) { //订单号为空
                    Toast.makeText(MainActivity.this, "快递单号不能为空", Toast.LENGTH_SHORT).show();
                    return; //结束方法
                }

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("deliveryName", CompanyInfo.chinaToCode(deliveryName)); //存放快递公司代号
                intent.putExtra("deliveryNumber", deliveryNumber); //存放快递单号
                startActivityForResult(intent, 100); //跳转到详情页
            }
        });

        //为搜索历史列表设置子项点击事件
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryData data = historyList.get(position); //获取当前位置的搜索历史

                String deliveryName = CompanyInfo.chinaToCode(data.getCompany()); //获取快递公司代号
                String deliveryNumber = data.getNumber(); //获取快递单号

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("deliveryName", deliveryName); //存放快递公司代号
                intent.putExtra("deliveryNumber", deliveryNumber); //存放快递单号
                startActivityForResult(intent, 100); //跳转到详情页
            }
        });

        //为搜索历史列表设置子项长按事件
        lvHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position); //弹出是否删除对话框对话框
                return true;
            }
        });

        //为查看更多搜索历史按钮设置点击事件
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MoreHistoryActivity.class);
                startActivityForResult(intent, 100); //跳转到查看更多搜索历史界面
            }
        });
    }

    @Override //从详情页返回时调用
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        initDatas(); //重新初始化数据
    }

    /**
     * 弹出是否删除对话框对话框
     * @param position 当前子项的位置
     */
    private void showDeleteDialog(int position){
        //获取当前位置的历史记录
        HistoryData data = historyList.get(position);
        final String number = data.getNumber(); //获取快递单号

        //新建弹出对话框
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("提示"); //设置标题
        normalDialog.setMessage("是否删除快递单号\n" + number + "？"); //设置内容
        normalDialog.setPositiveButton("确定", //设置确定按钮相关信息
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.deleteHistory(number); //删除该快递单号的历史
                        initDatas(); //初始化数据
                    }
                });
        normalDialog.setNegativeButton("取消", //设置取消按钮相关信息
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //设置对话框消失
                    }
                });
        // 显示
        normalDialog.show(); //显示对话框
    }



    /**
     * 初始化数据
     */
    private void initDatas() {
        mDatabase = Database.getInstance(this); //初始化数据库
        historyList.clear(); //清空历史记录表

        if (!ShareUtils.checkIsHaveLogin(this)) { //当前未登录
            historyList = mDatabase.getHistoryNotBindUser(); //从数据库中获取未绑定用户的查询记录

            btnLogin.setText("登陆"); //设置登陆按钮上的文字
        } else { //已登录
            String loginUserName = ShareUtils.getLoginUserName(this); //获取当前登陆用户名

            Log.e(TAG, "loginUserName=" + loginUserName);
            historyList = mDatabase.getUserHistory(loginUserName); //从数据库中获取该用户名相关的记录

            btnLogin.setText(loginUserName); //给登陆按钮设置用户名
        }

//        HistoryData data = new HistoryData();
//        data.setCompany("王适当k");
//        data.setNumber("264665165");
//        data.setStatus("已到达");
//        data.setDatetime("2017-02-17");
//        historyList.add(data);

        int listSize = historyList.size(); //获取搜索历史列表大小

        Log.e(TAG, "historyList.size()=" + historyList.size());
        Log.e(TAG, "historyList=" + historyList);

        if (listSize >= 4) { //记录超过四个
            for (int i = 4; i < listSize; i++) { //只保留四个历史记录，其他删掉
                historyList.remove(i); //移除列表中第i个位置的数据
            }

            btnMore.setVisibility(View.VISIBLE); //显示查看更多查询历史按钮
        } else { //记录不超过四个
            btnMore.setVisibility(View.GONE); //隐藏查看更多查询历史按钮
        }

        if (listSize <= 0) { //搜索历史列表无数据
            tvNoHistory.setVisibility(View.VISIBLE); //显示暂无搜索历史文字
        } else {
            tvNoHistory.setVisibility(View.GONE); //隐藏暂无搜索历史文字
        }

        Log.e(TAG, "initDatas: historyList=" + historyList);

        mAdapter = new HistoryAdapter(this, historyList); //新建历史消息适配器
        lvHistory.setAdapter(mAdapter); //为历史消息展示列表设置适配器
    }

    //初始化View

    private void initViews() {
        //绑定视图
        btnLogin = (Button) findViewById(R.id.btn_login);
        etNumber = (EditText) findViewById(R.id.et_number);
        spName = (Spinner) findViewById(R.id.sp_name);
        btnSearch = (Button) findViewById(R.id.btn_search);
        lvHistory = (ListView) findViewById(R.id.lv_history);
        tvNoHistory = (TextView) findViewById(R.id.tv_noHistory);
        btnMore = (Button) findViewById(R.id.btn_more);
    }

}
