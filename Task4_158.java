import java.util.Locale;

public class Task4 {

    static class LegacyForeignOrder {
        private final int orderId;
        private final double gbpAmount;

        public LegacyForeignOrder(int orderId, double gbpAmount) {
            this.orderId = orderId;
            this.gbpAmount = gbpAmount;
        }

        public int getOrderId() {
            return orderId;
        }

        public double getGbpAmount() {
            return gbpAmount;
        }
    }

    static class LegacyCurrencyConverter {
        public String convert(String amount, String from, String to) {
            if (from.equals("GBP") && to.equals("USD")) {
                double converted = Double.parseDouble(amount) * 1.25;
                return String.format(Locale.US, "%.2f USD", converted);
            }
            return "UNSUPPORTED";
        }
    }

    interface PricingService {
        double calculateTotal();
        String getReceiptBreakdown();
    }

    static class CurrencyOrderAdapter implements PricingService {
        private final LegacyForeignOrder order;
        private final LegacyCurrencyConverter converter;

        public CurrencyOrderAdapter(LegacyForeignOrder order,
                                    LegacyCurrencyConverter converter) {
            this.order = order;
            this.converter = converter;
        }

        @Override
        public double calculateTotal() {
            String result = converter.convert(
                    String.valueOf(order.getGbpAmount()), "GBP", "USD");

            return Double.parseDouble(result.split(" ")[0]);
        }

        @Override
        public String getReceiptBreakdown() {
            return String.format(Locale.US,
                    "Base: $%.2f (Converted from %.2f GBP)",
                    calculateTotal(), order.getGbpAmount());
        }
    }

    static abstract class PricingDecorator implements PricingService {
        protected final PricingService wrappedService;

        public PricingDecorator(PricingService service) {
            if (service == null)
                throw new IllegalArgumentException("Service cannot be null");

            this.wrappedService = service;
        }

        @Override
        public double calculateTotal() {
            return wrappedService.calculateTotal();
        }

        @Override
        public String getReceiptBreakdown() {
            return wrappedService.getReceiptBreakdown();
        }
    }

    static class GiftWrapDecorator extends PricingDecorator {
        public GiftWrapDecorator(PricingService service) {
            super(service);
        }

        @Override
        public double calculateTotal() {
            return super.calculateTotal() + 5.00;
        }

        @Override
        public String getReceiptBreakdown() {
            return super.getReceiptBreakdown() + " | Gift Wrap: $5.00";
        }
    }

    static class ExpressCourierDecorator extends PricingDecorator {
        public ExpressCourierDecorator(PricingService service) {
            super(service);
        }

        @Override
        public double calculateTotal() {
            return super.calculateTotal() + 15.00;
        }

        @Override
        public String getReceiptBreakdown() {
            return super.getReceiptBreakdown() + " | Express Courier: $15.00";
        }
    }

    static class CustomsDutyDecorator extends PricingDecorator {
        public CustomsDutyDecorator(PricingService service) {
            super(service);
        }

        @Override
        public double calculateTotal() {
            return super.calculateTotal() * 1.085;
        }

        @Override
        public String getReceiptBreakdown() {
            return super.getReceiptBreakdown() + " | Customs Duty: 8.5%";
        }
    }

    public static void main(String[] args) {
        LegacyForeignOrder order = new LegacyForeignOrder(8081, 100.00);

        PricingService base = new CurrencyOrderAdapter(
                order, new LegacyCurrencyConverter());

        System.out.printf(Locale.US,
                "Base Converted: $%.2f%n", base.calculateTotal());

        PricingService checkout = new CustomsDutyDecorator(
                new ExpressCourierDecorator(
                        new GiftWrapDecorator(base)
                )
        );

        System.out.println(">>> INVOICE BREAKDOWN");
        System.out.println(checkout.getReceiptBreakdown());

        System.out.printf(Locale.US,
                "FINAL CHARGE TOTAL: $%.2f%n", checkout.calculateTotal());
    }
}
