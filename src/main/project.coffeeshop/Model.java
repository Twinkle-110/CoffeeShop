package project.coffeeshop;

public class Model {

    private int order_id;
    private int user_id;
    private String coffee_name; // Now supports multiple coffee names as CSV
    private int quantity;
    private boolean whipped_cream;
    private boolean chocolate;
    private boolean vanilla;
    private int total_amount;
    private String payment_status;
    private boolean delivery_status;

    public Model(int order_id, int user_id, String coffee_name, int quantity,
                 boolean whipped_cream, boolean chocolate, boolean vanilla,
                 int total_amount, String payment_status, boolean delivery_status) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.coffee_name = coffee_name;
        this.quantity = quantity;
        this.whipped_cream = whipped_cream;
        this.chocolate = chocolate;
        this.vanilla = vanilla;
        this.total_amount = total_amount;
        this.payment_status = payment_status;
        this.delivery_status = delivery_status;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCoffee_name() {
        return coffee_name;
    }

    public void setCoffee_name(String coffee_name) {
        this.coffee_name = coffee_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isWhipped_cream() {
        return whipped_cream;
    }

    public void setWhipped_cream(boolean whipped_cream) {
        this.whipped_cream = whipped_cream;
    }

    public boolean isChocolate() {
        return chocolate;
    }

    public void setChocolate(boolean chocolate) {
        this.chocolate = chocolate;
    }

    public boolean isVanilla() {
        return vanilla;
    }

    public void setVanilla(boolean vanilla) {
        this.vanilla = vanilla;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public boolean isDelivery_status() {
        return delivery_status;
    }

    public void setDelivery_status(boolean delivery_status) {
        this.delivery_status = delivery_status;
    }
}
