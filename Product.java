package pricedrop.model;

public class Product {
    public int id;
    public String name;
    public String url;
    public String platform;
    public String category;
    public double current;
    public double original;
    public double target;
    public double lowest;
    public double highest;

    public Product(int id, String name, String url, String platform, String category,
                   double current, double original, double target) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.platform = platform;
        this.category = category;
        this.current = current;
        this.original = original;
        this.target = target;
        this.lowest = current;
        this.highest = original;
    }

    public int getDiscount() {
        if (original <= 0) return 0;
        return (int) Math.round((original - current) / original * 100);
    }

    public String getStatus() {
        if (target > 0 && current <= target) return "TARGET HIT";
        if (current < original) return "DROPPED";
        return "NORMAL";
    }
}
