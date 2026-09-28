package pricedrop.model;

public class Budget {
    public int id;
    public String category;
    public double limit;
    public double spent;

    public Budget(int id, String category, double limit, double spent) {
        this.id = id;
        this.category = category;
        this.limit = limit;
        this.spent = spent;
    }

    public int getPercent() {
        if (limit <= 0) return 0;
        return (int) Math.min(Math.round(spent / limit * 100), 100);
    }

    public boolean isOver() {
        return spent > limit;
    }
}
