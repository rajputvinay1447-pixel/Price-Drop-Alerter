package pricedrop.model;

public class Expense {
    public int id;
    public String title;
    public double amount;
    public String type;
    public String date;
    public String platform;
    public String payment;

    public Expense(int id, String title, double amount, String type,
                   String date, String platform, String payment) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.platform = platform;
        this.payment = payment;
    }
}
