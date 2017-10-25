package com.limin.delivery.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.limin.delivery.utils.CompanyInfo;
import com.limin.delivery.utils.Database;
import com.limin.delivery.R;
import com.limin.delivery.adapter.HistoryAdapter;
import com.limin.delivery.bean.HistoryData;
import com.limin.delivery.utils.ShareUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 更多查询历史界面
 */
public class MoreHistoryActivity extends Activity {

    private static final String TAG = "MoreHistoryActivity";

    private Button btnClearList; //清空查询历史列表
    private ListView lvHistory; //查询历史展示控件

    private HistoryAdapter mAdapter; //历史记录适配器

    private Database mDatabase; //数据库

    private List<HistoryData> historyList = new ArrayList<HistoryData>(); //搜索历史列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_history);

        initViews(); //初始化界面
        initDatas(); //初始化数据
        initEvents(); //初始化事件
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        //给清空列表按钮设置点击事件
        btnClearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.deleteAllHistory(); //清空数据库中快递历史表所有数据
                historyList.clear(); //清空搜索历史列表
                mAdapter.notifyDataSetChanged(); //用适配器通知数据集合改变

                Toast.makeText(MoreHistoryActivity.this, "已清空查询历史", Toast.LENGTH_SHORT).show();
            }
        });

        //为搜索历史列表设置子项点击事件
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryData data = historyList.get(position); //获取当前位置的搜索历史

                String deliveryName = CompanyInfo.chinaToCode(data.getCompany()); //获取快递公司代号
                String deliveryNumber = data.getNumber(); //获取快递单号

                Intent intent = new Intent(MoreHistoryActivity.this, DetailActivity.class);
                intent.putExtra("deliveryName", deliveryName); //存放快递公司代号
                intent.putExtra("deliveryNumber", deliveryNumber); //存放快递单号
                startActivity(intent); //跳转到详情页
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
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MoreHistoryActivity.this);
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

//        historyList = mDatabase.getAllHistory(); //从数据库中获取数据指针

        if (!ShareUtils.checkIsHaveLogin(this)) { //当前未登录
            historyList = mDatabase.getHistoryNotBindUser(); //从数据库中获取未绑定用户的查询记录
        } else { //已登录
            String loginUserName = ShareUtils.getLoginUserName(this); //获取当前登陆用户名

            Log.e(TAG, "loginUserName=" + loginUserName);
            historyList = mDatabase.getUserHistory(loginUserName); //从数据库中获取该用户名相关的记录
        }

        Log.e(TAG, "historyList.size()=" + historyList.size());
        Log.e(TAG, "historyList=" + historyList);

//        Collections.reverse(historyList); //将搜索历史列表逆序排列

        mAdapter = new HistoryAdapter(this, historyList); //新建历史消息适配器
        lvHistory.setAdapter(mAdapter); //为历史消息展示列表设置适配器
    }

    /**
     * 初始化界面
     */
    private void initViews() {
        btnClearList = (Button) findViewById(R.id.btn_clear_list);
        lvHistory = (ListView) findViewById(R.id.lv_history);
    }
}
