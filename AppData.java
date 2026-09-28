package pricedrop.data;

import pricedrop.model.*;
import java.util.*;

public class AppData {
    public static final List<Product> products = new ArrayList<>();
    public static final List<Expense> expenses = new ArrayList<>();
    public static final List<Budget> budgets = new ArrayList<>();
    public static final List<Alert> alerts = new ArrayList<>();

    public static int nextProductId = 7;
    public static int nextExpenseId = 8;
    public static int nextBudgetId = 6;
    public static int nextAlertId = 5;

    static {
        // Sample products
        products.add(new Product(1, "Samsung Galaxy S24 Ultra 12GB",
                "https://amazon.in/dp/B0CRDHKBCQ", "Amazon", "Electronics", 109999, 134999, 99999));
        products.add(new Product(2, "Apple iPhone 15 128GB Blue",
                "https://flipkart.com/apple-iphone-15", "Flipkart", "Electronics", 69999, 79900, 65000));
        products.add(new Product(3, "Sony WH-1000XM5 Headphones",
                "https://amazon.in/dp/B09XS7JWHH", "Amazon", "Electronics", 24990, 34990, 22000));
        products.add(new Product(4, "Nike Air Max 270 Running Shoes",
                "https://myntra.com/nike/air-max-270", "Myntra", "Fashion", 7495, 11995, 6000));
        products.add(new Product(5, "LG 43\" 4K Smart TV",
                "https://flipkart.com/lg-43up7500", "Flipkart", "Electronics", 32999, 42990, 30000));
        products.add(new Product(6, "boAt Airdopes 141 TWS Earbuds",
                "https://amazon.in/dp/B09TDLNQ9T", "Amazon", "Electronics", 1299, 2990, 999));

        // Sample expenses
        expenses.add(new Expense(1, "boAt Earbuds Purchase", 1299, "Purchase", "2025-03-01", "Amazon", "UPI"));
        expenses.add(new Expense(2, "Electricity Bill", 1850, "Bill", "2025-03-05", "Other", "Net Banking"));
        expenses.add(new Expense(3, "Zomato Food Order", 450, "Food", "2025-03-08", "Other", "UPI"));
        expenses.add(new Expense(4, "Ola Cab Ride", 320, "Transport", "2025-03-10", "Other", "UPI"));
        expenses.add(new Expense(5, "Netflix Subscription", 649, "Entertainment", "2025-03-12", "Other", "Card"));
        expenses.add(new Expense(6, "T-Shirt from Myntra", 799, "Purchase", "2025-03-14", "Myntra", "UPI"));
        expenses.add(new Expense(7, "Grocery Shopping", 2340, "Food", "2025-03-15", "Other", "UPI"));

        // Sample budgets
        budgets.add(new Budget(1, "Purchase", 15000, 2098));
        budgets.add(new Budget(2, "Food", 5000, 2790));
        budgets.add(new Budget(3, "Transport", 2000, 320));
        budgets.add(new Budget(4, "Bill", 3000, 2499));
        budgets.add(new Budget(5, "Entertainment", 1000, 649));

        // Sample alerts
        alerts.add(new Alert(1, "PRICE_DROP", "Price Drop on Sony Headphones!",
                "Price dropped by \u20b92,000 \u2014 now \u20b924,990 on Amazon", 2000, "2 hours ago", false));
        alerts.add(new Alert(2, "TARGET_REACHED", "Target Price Reached!",
                "boAt Airdopes 141 is now \u20b91,299 \u2014 your target was \u20b9999", 1691, "1 day ago", false));
        alerts.add(new Alert(3, "BUDGET_WARNING", "Budget Warning \u2014 Bills",
                "You've used 83% of your Bills budget this month", 0, "3 hours ago", false));
        alerts.add(new Alert(4, "PRICE_DROP", "iPhone 15 Price Dropped",
                "Price went from \u20b974,900 \u2192 \u20b969,999 on Flipkart", 4901, "2 days ago", true));
    }

    public static String fmt(double n) {
        if (n >= 100000) return String.format("\u20b9%.1fL", n / 100000);
        if (n >= 1000) return String.format("\u20b9%.1fK", n / 1000);
        return String.format("\u20b9%,.0f", n);
    }

    public static String fmtFull(double n) {
        return String.format("\u20b9%,.0f", n);
    }

    public static String getPlatformUrl(String url) {
        if (url == null || url.isEmpty()) return "Amazon";
        if (url.contains("amazon")) return "Amazon";
        if (url.contains("flipkart")) return "Flipkart";
        if (url.contains("myntra")) return "Myntra";
        return "Amazon";
    }

    public static int getUnreadAlertCount() {
        return (int) alerts.stream().filter(a -> !a.read).count();
    }

    public static double getTotalSavings() {
        return products.stream().mapToDouble(p -> Math.max(0, p.original - p.current)).sum();
    }

    public static double getMonthExpenses(String month) {
        return expenses.stream()
                .filter(e -> e.date.startsWith(month))
                .mapToDouble(e -> e.amount).sum();
    }

    public static String getCategoryEmoji(String cat) {
        switch (cat) {
            case "Purchase":
                return "\uD83D\uDECD\uFE0F";
            case "Food":
                return "\uD83C\uDF54";
            case "Transport":
                return "\uD83D\uDE97";
            case "Bill":
                return "\uD83D\uDCA1";
            case "Entertainment":
                return "\uD83C\uDFAC";
            case "Healthcare":
                return "\uD83C\uDFE5";
            case "Education":
                return "\uD83D\uDCDA";
            case "Savings":
                return "\uD83D\uDCB0";
            default:
                return "\uD83D\uDCE6";
        }
    }
}
