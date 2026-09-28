package pricedrop.model;

public class Alert {
    public int id;
    public String type;
    public String title;
    public String message;
    public double savings;
    public String time;
    public boolean read;

    public Alert(int id, String type, String title, String message,
                 double savings, String time, boolean read) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.savings = savings;
        this.time = time;
        this.read = read;
    }

    public String getEmoji() {
        switch (type) {
            case "PRICE_DROP":
                return "📉";
            case "TARGET_REACHED":
                return "🎯";
            case "BUDGET_WARNING":
                return "⚠️";
            case "BUDGET_EXCEEDED":
                return "🚨";
            case "PRICE_INCREASE":
                return "📈";
            default:
                return "🔔";
        }
    }
}
