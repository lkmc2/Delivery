package com.limin.delivery.utils;

import com.limin.delivery.R;

/**
 * 公司信息
 */
public class CompanyInfo {
    private static final String TAG = "CompanyInfo";

    public static final int SHUN_FENG = 0; //顺丰
    public static final int SHEN_TONG = 1; //申通
    public static final int YUAN_TONG = 2; //圆通
    public static final int YUN_DA = 3; //韵达
    public static final int TIAN_TIAN = 4; //天天
    public static final int EMS = 5; //EMS
    public static final int ZHONG_TONG = 6; //中通
    public static final int HUI_TONG = 7; //汇通
    public static final int JINGDONG = 8; //京东快递
    public static final int ZHAI_JI_SONG = 9; //宅急送
    public static final int EMS_IN = 10; //EMS国际

    public static final String[] name = new String[] { //公司名称
            "顺丰", "申通", "圆通",
            "韵达", "天天", "EMS",
            "中通", "汇通", "京东快递",
            "宅急送", "EMS国际"
    };

    public static final String[] code = new String[] { //公司代码
            "sf", "sto", "yt",
            "yd", "tt", "ems",
            "zto", "ht", "jd",
            "zjs", "emsg"
    };
    public static final int[] pic = new int[] { //图片编号代码
            R.drawable.shunfeng, R.drawable.shentong, R.drawable.yuantong,
            R.drawable.yunda, R.drawable.tiantian, R.drawable.ems,
            R.drawable.zhongtong, R.drawable.huitong, R.drawable.jingdong,
            R.drawable.zhaijisong, R.drawable.ems_in
    };

    /**
     * 根据传入的公司代码返回相应的图片id
     * @param companyCode 公司代码
     * @return 该公司的图片id
     */
    public static int getCompanyPicture(String companyCode) {
        int pictureId = -1; //公司图片的id

        for (int i = 0; i < code.length; i++) {
            if (companyCode.equals(code[i])) {
                pictureId = pic[i];
                break;
            }
        }

        return pictureId; //返回图片id
    }

    /**
     * 根据传入的公司代码返回相应的中文名
     * @param companyCode 公司代码
     * @return 该公司的中文名
     */
    public static String getCompanyName(String companyCode) {
        String companyName = "顺丰"; //公司图片的id

        for (int i = 0; i < code.length; i++) {
            if (companyCode.equals(code[i])) {
                companyName = name[i];
                break;
            }
        }

        return companyName; //返回公司中文名
    }

    /**
     * 根据传入的公司名称返回相应的英文代号
     * @param companyName 公司名称
     * @return 该公司的英文代号
     */
    public static String chinaToCode(String companyName) {
        String companyCode = "sf"; //公司图片的id

        for (int i = 0; i < name.length; i++) {
            if (companyName.equals(name[i])) {
                companyCode = code[i];
                break;
            }
        }

        return companyCode; //返回公司中文名
    }
}