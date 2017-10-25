package com.limin.delivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import com.limin.delivery.R;
import com.limin.delivery.bean.HistoryData;


//快递查询实体类
public class HistoryAdapter extends BaseAdapter {
    private Context mContext; //上下文
    private List<HistoryData> mList; //数据列表
    private LayoutInflater inflater; //布局加载器
    private HistoryData data; //快递历史实体类临时变量


    public HistoryAdapter(Context mContext, List<HistoryData> mList) {
        this.mContext = mContext;
        this.mList = mList;
        //获取系统服务
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;

        if (convertView == null) { //第一次加载
            viewHolder = new ViewHolder(); //新建ViewHolder
            convertView = inflater.inflate(R.layout.layout_history_item, null); //加载布局

            //为ViewHolder中的组件绑定布局
            viewHolder.tvCompany = (TextView) convertView.findViewById(R.id.tv_deliveryName);
            viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_deliveryNumber);
            viewHolder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            viewHolder.tvDatetime = (TextView) convertView.findViewById(R.id.tv_date);

            convertView.setTag(viewHolder); //把viewHolder设置进convertView标签
        } else { //非第一次加载
            viewHolder = (ViewHolder) convertView.getTag(); //从convertView标签中取出viewHolder
        }

        //从列表中获取当前位置的数据
        data = mList.get(position);

        //为相应控件设置文字
        viewHolder.tvCompany.setText(data.getCompany());
        viewHolder.tvNumber.setText(data.getNumber());
        viewHolder.tvStatus.setText(data.getStatus());
        viewHolder.tvDatetime.setText(data.getDatetime());

        return convertView;
    }

    class ViewHolder { //保存控件状态的内部类
        private TextView tvCompany; //快递公司名称
        private TextView tvNumber; //快递单号
        private TextView tvStatus; //快递状态改变时间
        private TextView tvDatetime; //快递状态改变时间
    }

}
