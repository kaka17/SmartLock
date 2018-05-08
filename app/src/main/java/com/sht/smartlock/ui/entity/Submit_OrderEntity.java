package com.sht.smartlock.ui.entity;

/**
 * Created by Administrator on 2015/10/13.
 */
public class Submit_OrderEntity {
    private String  ID;//订餐id
    private String  hotel_id;//酒店id
    private String  hotel_caption;//	酒店名称
    private String  create_time;//订单创建时间
    private String  pay_type;//支付方式//    1-现金//    2-刷卡//    3-房费
    private String items;//订餐，购物 数据

//   state 订单状态：
//      -1-取消订单
//    0-客户未确认
//    1-客户已经确认
//    2-服务商已经响应
//    3-服务已经完成
    private String  state;
    private String  total;//总价格

    private String quantity;//数量
    private String  price;//单价
    private String  discount;//折扣
    private String  name;//联系人
    private String  note;//备注
//    支付状态
    //0-未支付
//    1-已支付
    private String  pay_state;//
    private String  room_no;//房号
    private String phone_no;
    private String pay_channel;

    public String getPay_channel() {
        return pay_channel;
    }

    public void setPay_channel(String pay_channel) {
        this.pay_channel = pay_channel;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPay_state() {
        return pay_state;
    }

    public void setPay_state(String pay_state) {
        this.pay_state = pay_state;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getHotel_caption() {
        return hotel_caption;
    }

    public void setHotel_caption(String hotel_caption) {
        this.hotel_caption = hotel_caption;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
