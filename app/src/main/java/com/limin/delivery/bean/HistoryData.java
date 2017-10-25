package com.limin.delivery.bean;

/**
 * 快递历史实体类
 */
public class HistoryData {
    private String company; //快递公司名称
    private String number; //快递单号
    private String status; //快递状态（1代表结束，0代表还会有变化）
    private String datetime; //快递更新时间

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "HistoryData{" +
                "company='" + company + '\'' +
                ", number='" + number + '\'' +
                ", status='" + status + '\'' +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
