public class PaymentProcessor {
    private boolean gatewayReady = false;
    private String activePromoCode = null;
    public void initializeGateway() {
        this.gatewayReady = true;
        System.out.println("[PaymentProcessor] Payment gateway initialized.");
    }
    public void applyPromoCode(String promoCode) {
        if (!gatewayReady) throw new IllegalStateException("Initialize gateway before applying promo codes.");
        this.activePromoCode = promoCode;
        System.out.println("[PaymentProcessor] Applied promo code: " + promoCode);
    }
    public String chargeCard(String cardNumber, double amount, String currency) {
        if (!gatewayReady) throw new IllegalStateException("Payment gateway is not initialized.");
        if (cardNumber == null || cardNumber.replaceAll("\\s+", "").length() < 12)
            throw new IllegalArgumentException("Invalid card number provided.");
        double finalAmount = amount;
        if ("SILVER-FREE-BAG".equalsIgnoreCase(activePromoCode)) {
            finalAmount = amount * 0.90;
            System.out.printf("[PaymentProcessor] Promo discount applied. New total: %.2f %s%n", finalAmount, currency);
        }
        this.activePromoCode = null;
        String txId = "TX-" + Math.abs((cardNumber + finalAmount).hashCode());
        System.out.printf("[PaymentProcessor] Charged %.2f %s | Receipt: %s%n", finalAmount, currency, txId);
        return txId;
    }
}
