package com.limin.delivery.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.limin.delivery.bean.HistoryData;

import java.util.ArrayList;
import java.util.List;

//数据库帮助类
public class Database extends SQLiteOpenHelper {

    private static final String TAG = "Database";

    private static Database mInstance = null; //数据库实例

    private static final String HISTORY_TABLE = "history_table"; //查询历史表名
    private static final String DELIVERY_COMPANY = "delivery_company"; //快递公司名
    private static final String DELIVERY_NUMBER = "delivery_number"; //快递单号
    private static final String DELIVERY_STATUS = "delivery_status"; //快递状态
    private static final String DELIVERY_DATE = "delivery_info"; //快递更新日期
    private static final String INSERT_TIME = "insert_time"; //记录插入时间

    private static final String USER_TABLE = "user_table"; //用户表
    private static final String USER_NAME = "user_name"; //用户名
    private static final String USER_PASSWORD = "user_password"; //用户密码

    private static final String USER_HISTORY_TABLE = "user_history_table"; //用户查询历史表
    private static final String USER_ID = "user_id"; //用户id
    private static final String HISTORY_ID = "history_id"; //查询历史id

    public Database(Context context) {
        super(context, "DataBase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //新建查询历史表
        db.execSQL("create table if not exists " + HISTORY_TABLE + "(" +
                "id integer primary key," + //id号
                DELIVERY_COMPANY + " text," + //快递公司名
                DELIVERY_NUMBER + " text," + //快递单号
                DELIVERY_STATUS + " text," + //快递状态
                DELIVERY_DATE + " text," + //快递日期
                INSERT_TIME + " long)"); //插入时间

        //新建用户表
        db.execSQL("create table if not exists " + USER_TABLE + "(" +
                "id integer primary key," + //id号
                USER_NAME + " text," + //用户名
                USER_PASSWORD + " text)"); //用户密码

        //新建用户查询历史表
        db.execSQL("create table if not exists " + USER_HISTORY_TABLE + "(" +
                "id integer primary key," + //id号
                USER_ID + " integer," + //用户名
                HISTORY_ID + " integer)"); //用户密码
    }

    /**
     * 获取数据库实例对象
     * @param context 上下文
     * @return 数据库实例对象
     */
    public static Database getInstance(Context context) {
        if (mInstance == null) { //数据库实例为空
            synchronized (Database.class) { //同步锁，防止多个线程同时访问
                if (mInstance == null) { //数据库实例为空
                    mInstance = new Database(context.getApplicationContext()); //新建数据库实例对象
                }
            }
        }
        return mInstance; //返回数据库实例
    }

    /**
     * 获取已发货快递的数量
     * @return 已发货快递的数量
     */
    public int getHaveDeliveryCount() {
        SQLiteDatabase database = getWritableDatabase(); //获取可写的数据库

        //查询历史表中已发货记录的指针
        Cursor cursor = database.rawQuery("select * from " + HISTORY_TABLE
                + " where " + DELIVERY_STATUS + "!=1", null);

        int totalCount  = 0; //已发货快递的数量

        if (cursor != null) {
            while (cursor.moveToNext()) {
                totalCount++;
            }
            cursor.close();
        }
        return totalCount;
    }

    /**
     * 获取已到达快递的数量
     * @return 已发货快递的数量
     */
    public int getArriveCount() {
        SQLiteDatabase database = getWritableDatabase(); //获取可写的数据库

        //查询历史表中已到达记录的指针
        Cursor cursor = database.rawQuery("select * from " + HISTORY_TABLE
                + " where " + DELIVERY_STATUS + "=1", null);

        int totalCount  = 0; //已发货快递的数量

        if (cursor != null) {
            while (cursor.moveToNext()) {
                totalCount++;
            }
            cursor.close();
        }
        return totalCount;
    }

    /**
     * 获取没有与用户进行绑定的搜索历史列表
     * @return 没有与用户进行绑定的搜索历史列表
     */
    public List<HistoryData> getHistoryNotBindUser() {
        SQLiteDatabase database = getWritableDatabase(); //获取可写的数据库

        //获取在用户历史表中无记录的搜索历史记录
        Cursor cursor = database.rawQuery("select * from " + HISTORY_TABLE + " h where h.id not in (select u."
                + HISTORY_ID + " from " + USER_HISTORY_TABLE + " u) order by " + INSERT_TIME + " desc", null);

        //根据传入的指针将数据转换成历史记录列表
        return useCursorToHistoryList(cursor);
    }

    /**
     * 绑定用户与查询历史
     * @param username 用户名
     * @param deliveryNumber 订单号
     */
    public void bindUserAndHistory(String username, String deliveryNumber) {
        SQLiteDatabase database = getWritableDatabase(); //获取可写的数据库

        //从用户表中找到该用户名相关的记录
        Cursor userCursor = database.rawQuery("select * from " + USER_TABLE + " where "
                + USER_NAME + "=?", new String[]{ username });

        //根据快递单号查询历史表获取指针
        Cursor cursor = database.rawQuery("select * from " + HISTORY_TABLE
                + " where " + DELIVERY_NUMBER + "=?", new String[]{ deliveryNumber });

        if (userCursor == null || cursor == null) { //如果用户指针或者历史指针为空
            return; //结束当前方法
        }

        userCursor.moveToNext(); //用户指针移动到下一个位置
        int userId = userCursor.getInt(userCursor.getColumnIndex("id")); //从指针获取用户id

        while (cursor.moveToNext()) {
            int historyId = cursor.getInt(cursor.getColumnIndex("id")); //从指针获取查询历史id

            //往数据库中插入用户查询历史数据
            database.execSQL("insert into " + USER_HISTORY_TABLE + "(" + USER_ID + "," + HISTORY_ID + ")"
                    + " values(" + userId + "," + historyId + ")");
        }

        userCursor.close(); //关闭用户指针
        cursor.close(); //关闭查询历史指针
    }

    /**
     * 根据传入的用户名获取相关的搜索历史记录列表
     * @param username 用户名
     * @return 用户名相关的搜索历史记录列表
     */
    public List<HistoryData> getUserHistory(String username) {
        SQLiteDatabase database = getWritableDatabase(); //获取可写的数据库

        //根据用户名查询用户表中的记录
        Cursor userCursor = database.rawQuery("select * from " + USER_TABLE
                + " where " + USER_NAME + "=?", new String[]{ username });

        if (userCursor == null) { //当用户指针为空
            return new ArrayList<HistoryData>(); //返回空的新数据
        }
        userCursor.moveToNext(); //用户指针移动到下一个位置

        int userId = userCursor.getInt(userCursor.getColumnIndex("id")); //获取用户id
        userCursor.close(); //关闭用户指针

        //根据用户id查找用户历史表中与该用户相关的查询历史
        Cursor cursor = database.rawQuery("select * from " + USER_HISTORY_TABLE
                        + " u," + HISTORY_TABLE + " h where u." + USER_ID + "=? and u."
                        + HISTORY_ID + "=h.id order by h." + INSERT_TIME + " desc",
                new String[]{ String.valueOf(userId) });

        //根据传入的指针将数据转换成历史记录列表
        return useCursorToHistoryList(cursor);
    }

    /**
     * 查询数据库中是否该有用户的记录
     * @param username 用户名
     * @return 是否有该用户记录
     */
    public boolean checkIsHaveUserName(String username) {
        SQLiteDatabase database = getReadableDatabase();

        //查询数据库中是否该有用户名的记录
        Cursor cursor = database.rawQuery("select * from " + USER_TABLE + " where "
                + USER_NAME + "=?", new String[]{ username });

        if (cursor != null) { //指针非空，数据库中有该记录
            if (cursor.moveToNext()) { //移动到下一个位置有值
                return true; //返回值为真
            }
            cursor.close(); //关闭指针
        }
        return false; //返回值为假
    }

    /**
     * 查询数据库中是否有该用户名和密码的记录
     * @param username 用户名
     * @param password 密码
     * @return 是否有该用户记录
     */
    public boolean checkIsHaveUser(String username, String password) {
        SQLiteDatabase database = getReadableDatabase(); //获取可读的数据库

        //查询数据库中是否该有用户的记录
        Cursor cursor = database.rawQuery("select * from " + USER_TABLE + " where "
                + USER_NAME + "=? and " + USER_PASSWORD + "=?", new String[]{ username, password });

        if (cursor != null) { //指针非空，数据库中有该记录
            if (cursor.moveToNext()) { //移动到下一个位置有值
                return true; //返回值为真
            }
            cursor.close(); //关闭指针
        }
        return false; //返回值为假
    }

    /**
     * 插入用户到数据库（注册）
     * @param username 用户名
     * @param password 密码
     */
    public void addUser(String username, String password) {
        SQLiteDatabase database = getWritableDatabase(); //获取可写的数据库

        //添加用户记录到数据库
        database.execSQL("insert into " + USER_TABLE + "(" + USER_NAME + "," + USER_PASSWORD + ")"
                + " values('" + username + "','" + password + "')");
    }

    /**
     * 插入一条查询历史数据到数据库
     * @param data 历史消息类
     */
    public void addHistory(HistoryData data) {
        SQLiteDatabase database = getWritableDatabase(); //获取可写入的数据库

        //添加查询记录的数据库
        database.execSQL("insert into " + HISTORY_TABLE
                + "(" + DELIVERY_COMPANY + "," + DELIVERY_NUMBER + ","
                + DELIVERY_STATUS + "," + DELIVERY_DATE + "," + INSERT_TIME + ")"
                + " values("
                + "'" + data.getCompany()  + "',"
                + "'" + data.getNumber()   + "',"
                + "'" + data.getStatus()   + "',"
                + "'" + data.getDatetime() + "',"
                + "'" + System.currentTimeMillis() + "')");

    }

    /**
     * 更新一条查询历史数据到数据库
     * @param data 历史消息类
     */
    public void updateHistory(HistoryData data) {
        SQLiteDatabase database = getWritableDatabase(); //获取可写入的数据库

        //根据快递单号更新快递数据
        database.execSQL("update " + HISTORY_TABLE + " set "
                + DELIVERY_COMPANY + "='" + data.getCompany() + "',"
                +  DELIVERY_NUMBER + "='" + data.getNumber() + "',"
                +  DELIVERY_STATUS + "='" + data.getStatus() + "',"
                +  DELIVERY_DATE + "='" + data.getDatetime() + "',"
                +  INSERT_TIME + "='" + System.currentTimeMillis() + "'"
                + " where " +  DELIVERY_NUMBER + "='" + data.getNumber() + "'");
    }

    /**
     * 根据快递单号删除相应的快递数据
     * @param deliveryNumber 快递单号
     */
    public void deleteHistory(String deliveryNumber) {
        SQLiteDatabase database = getWritableDatabase(); //获取可写入的数据库

        //删除用户历史表中与该快递单号相关的记录
        database.execSQL("delete from " + USER_HISTORY_TABLE
                + " where " + HISTORY_ID + " in(select h.id from " + HISTORY_TABLE
                + " h where " + DELIVERY_NUMBER + "='" + deliveryNumber + "')");

        //删除历史表中与该快递单号相关的记录
        database.execSQL("delete from " + HISTORY_TABLE + " where " + DELIVERY_NUMBER + "='" + deliveryNumber + "'");
    }

    /**
     * 检查数据库中是否有该快递信息
     * @param deliveryNumber 快递单号
     * @return true为数据库中有该快递信息
     */
    public boolean checkIfHasDelivery(String deliveryNumber) {
        SQLiteDatabase database = getReadableDatabase(); //获取可读的数据库

        //根据快递单号查询历史表中的相关记录
        Cursor cursor = database.rawQuery("select * from " + HISTORY_TABLE
                + " where " + DELIVERY_NUMBER + "=?", new String[]{ deliveryNumber });  //等价于上面的cursor

        if (cursor != null) { //指针非空，数据库中有该记录
            if (cursor.moveToNext()) { //移动到下一个位置有值
                return true; //返回值为真
            }
            cursor.close(); //关闭指针
        }
        return false; //返回值为真
    }

    /**
     * 获取所有搜索历史表信息
     */
    public List<HistoryData> getAllHistory() {
        SQLiteDatabase database = getReadableDatabase(); //获取可读的数据库

        //根据插入时间降序排列历史表中的记录
        Cursor cursor = database.rawQuery("select * from " + HISTORY_TABLE
                + " order by " + INSERT_TIME + " desc", null); //等价于上面的query语句

        //根据传入的指针将数据转换成历史记录列表
        return useCursorToHistoryList(cursor);
    }

    /**
     * 根据传入的指针将数据转换成历史记录列表
     * @param cursor 数据指针
     * @return 历史记录列表
     */
    public List<HistoryData> useCursorToHistoryList(Cursor cursor) {
        List<HistoryData> historyList = new ArrayList<HistoryData>(); //查询历史列表

        HistoryData data; //搜索历史实体类
        if (cursor != null) { //当指针非空
            while (cursor.moveToNext()) { //指针移动到下一个位置
                data = new HistoryData(); //新建搜索历史实体类

                //从指针中获取公司名、快递单号、快递状态和更新时间
                String company = cursor.getString(cursor.getColumnIndex(DELIVERY_COMPANY));
                String number = cursor.getString(cursor.getColumnIndex(DELIVERY_NUMBER));
                String status = cursor.getString(cursor.getColumnIndex(DELIVERY_STATUS));
                String datetime = cursor.getString(cursor.getColumnIndex(DELIVERY_DATE));

                data.setCompany(company); //设置快递公司名
                data.setNumber(number); //设置快递单号
                data.setStatus("1".equals(status) ? "已送达" : "已发货"); //设置快递状态
                data.setDatetime(datetime); //设置更新日期

                historyList.add(data); //将搜索历史添加到列表中
            }
            cursor.close(); //关闭指针
        }
        return historyList;
    }

    /**
     * 删除所有搜索历史表信息
     */
    public void deleteAllHistory() {
        SQLiteDatabase database = getReadableDatabase(); //获取可读的数据库
        //删除历史中的数据
        database.execSQL("delete from " + HISTORY_TABLE); //等价于上面的delete语句
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
