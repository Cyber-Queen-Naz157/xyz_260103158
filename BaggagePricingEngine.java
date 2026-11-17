public class BaggagePricingEngine {
    public double calculateBaggageFeeUSD(int bagCount, double totalWeightKg) {
        if (bagCount < 0 || totalWeightKg < 0) throw new IllegalArgumentException("Bag count and weight cannot be negative.");
        if (bagCount == 0) {
            System.out.println("[BaggageEngine] No checked bags. Fee: $0.00");
            return 0.0;
        }
        double fee = 0.0;
        if (bagCount > 1) fee += (bagCount - 1) * 35.0;
        if (totalWeightKg > 20.0) fee += (totalWeightKg - 20.0) * 12.0;
        System.out.printf("[BaggageEngine] Calculated baggage fee: $%.2f%n", fee);
        return fee;
    }
}
