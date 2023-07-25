package deliveryservice;

import sqlhandler.sqlHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Line {
    private String item_name;
    private int quantity;
    private int weight;
    private int price;
    private int order_id;
    private String store_name;

    public Line(String item_name, int quantity, int weight, int price, int order_id, String store_name){
        this.item_name = item_name;
        this.order_id = order_id;
        this.store_name = store_name;
        this.quantity =   quantity;
        this.weight = weight * quantity;
        this.price = price *  quantity;
    }

    public int getOrder_id() {
        return this.order_id;
    }

    public String getStore_name() {
        return this.store_name;
    }
    public String getItem_name() {
        return this.item_name;
    }
    public int getQuantity() {
        return this.quantity;
    }

    public int getWeight() {
        return  this.weight;
    }

    public int getPrice() {
        return this.price;
    }
    public int getTotalWeight(){
        return this.weight;
    }
    public int getTotalCost(){
        return this.price;
    }

    public void printLineInfo(){
        System.out.println("item_name:"+this.item_name+",total_quantity:"+Integer.toString(this.quantity)+
                ",total_cost:"+Integer.toString(this.getTotalCost())+",total_weight:"+Integer.toString(this.getTotalWeight()));
    }

}
