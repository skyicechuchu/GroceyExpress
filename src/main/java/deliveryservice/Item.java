package deliveryservice;

// TODO: need to implement the database version or review and approve as is
public class Item {
    private String name = null;
    private int weight = 0;
    private Store store = null;

    public Item(Store store, String name, int weight){
        this.name = name;
        this.store = store;
        this.weight = weight;
    }

    String getName(){ return this.name; }
    int getWeight(){ return this.weight; }
    Store getStore(){ return this.store; }

}
