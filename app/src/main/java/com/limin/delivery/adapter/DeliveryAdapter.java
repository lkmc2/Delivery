package com.limin.delivery.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import com.limin.delivery.R;
import com.limin.delivery.bean.DeliveryData;

//快递查询实体类
public class DeliveryAdapter extends BaseAdapter {
    private static final String TAG = "DeliveryAdapter";

    private Context mContext; //上下文
    private List<DeliveryData> mList; //数据列表
    private LayoutInflater inflater; //布局加载器

    public DeliveryAdapter(Context mContext, List<DeliveryData> mList) {
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
            convertView = inflater.inflate(R.layout.layout_delivery_item, null); //加载布局

            //为ViewHolder中的组件绑定布局
            viewHolder.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
            viewHolder.tv_zone = (TextView) convertView.findViewById(R.id.tv_zone);
            viewHolder.tv_datetime = (TextView) convertView.findViewById(R.id.tv_datetime);
            viewHolder.upLine = (View) convertView.findViewById(R.id.up_line);
            viewHolder.downLine = (View) convertView.findViewById(R.id.down_line);

            convertView.setTag(viewHolder); //把viewHolder设置进convertView标签
        } else { //非第一次加载
            viewHolder = (ViewHolder) convertView.getTag(); //从convertView标签中取出viewHolder
        }

        setWidgetText(position, viewHolder); //设置控件上的文字

        setUpAndDownLineVisible(position, viewHolder); //设置顶部底部线条可见性

        return convertView;
    }

    /**
     * 设置顶部底部线条可见性
     * @param position 当前的位置
     * @param viewHolder 控件信息保存器
     */
    private void setUpAndDownLineVisible(int position, ViewHolder viewHolder) {
        if (position == 0) { //当前是第一个位置
            viewHolder.upLine.setVisibility(View.INVISIBLE); //设置顶部线条消失
        } else { //其他位置
            viewHolder.upLine.setVisibility(View.VISIBLE); //设置顶部线条显示
        }

        if (position == mList.size() - 1) { //当前是最后一个位置
            viewHolder.downLine.setVisibility(View.INVISIBLE); //设置底部线条消失
        } else { //其他位置
            viewHolder.downLine.setVisibility(View.VISIBLE); //设置底部线条显示
        }
    }

    /**
     * 设置控件上的文字
     * @param position 当前的位置
     * @param viewHolder 控件信息保存器
     */
    private void setWidgetText(int position, ViewHolder viewHolder) {
        //从列表中获取当前位置的数据
        DeliveryData data = mList.get(position);

        //为相应控件设置文字
        viewHolder.tv_remark.setText(data.getRemark());
        viewHolder.tv_zone.setText(data.getZone());
        viewHolder.tv_datetime.setText(data.getDatetime());
    }

    class ViewHolder { //保存控件状态的内部类
        private TextView tv_remark; //快递状态
        private TextView tv_zone; //已到达城市
        private TextView tv_datetime; //快递状态改变时间
        private View upLine; //顶部线条
        private View downLine; //底部线条
    }

}
