public class Task3 {

    interface DispatchStrategy {
        boolean shouldDispatch(int severityLevel, int currentHour);
        String getName();
    }

    static class ImmediateStrategy implements DispatchStrategy {
        @Override
        public boolean shouldDispatch(int severityLevel, int currentHour) {
            return true;
        }

        @Override
        public String getName() {
            return "IMMEDIATE_DISPATCH";
        }
    }

    static class BusinessHoursStrategy implements DispatchStrategy {
        @Override
        public boolean shouldDispatch(int severityLevel, int currentHour) {
            if (severityLevel >= 5) return true;
            return currentHour >= 9 && currentHour < 17;
        }

        @Override
        public String getName() {
            return "BUSINESS_HOURS_ONLY_9TO17";
        }
    }

    interface MessageChannel {
        void transmit(String destination, String message);
    }

    static class EmailChannel implements MessageChannel {
        @Override
        public void transmit(String destination, String message) {
            System.out.println("EMAIL PROTOCOL -> Mail to: " +
                    destination + " | " + message);
        }
    }

    static class SMSChannel implements MessageChannel {
        @Override
        public void transmit(String destination, String message) {
            System.out.println("SMS PROTOCOL -> SMS to: " +
                    destination + " | " + message);
        }
    }

    static abstract class Alert {
        protected final MessageChannel channel;
        protected final DispatchStrategy strategy;

        public Alert(MessageChannel channel, DispatchStrategy strategy) {
            if (channel == null || strategy == null)
                throw new IllegalArgumentException("Dependencies cannot be null");

            this.channel = channel;
            this.strategy = strategy;
        }

        public void sendAlert(String recipient, String rawContent,
                              int severity, int currentHour) {

            if (strategy.shouldDispatch(severity, currentHour)) {
                String formatted = formatAlertBody(rawContent);
                channel.transmit(recipient, formatted);
            } else {
                System.out.println("DISPATCH SUPPRESSED: Alert blocked by policy");
            }
        }

        protected abstract String getSeverityLevel();
        protected abstract String formatAlertBody(String raw);
    }

    static class CriticalSystemAlert extends Alert {
        public CriticalSystemAlert(MessageChannel c, DispatchStrategy s) {
            super(c, s);
        }

        @Override
        protected String getSeverityLevel() {
            return "CRITICAL PRIORITY";
        }

        @Override
        protected String formatAlertBody(String raw) {
            return "*** CRITICAL PRODUCTION ALERT ***\n"
                    + "Incident Report: " + raw + "\n"
                    + "ACTION REQUIRED: SRE on-call immediate intervention.";
        }
    }

    static class DailySummaryAlert extends Alert {
        public DailySummaryAlert(MessageChannel c, DispatchStrategy s) {
            super(c, s);
        }

        @Override
        protected String getSeverityLevel() {
            return "INFO";
        }

        @Override
        protected String formatAlertBody(String raw) {
            return "Daily Digest: " + raw;
        }
    }

    public static void main(String[] args) {
        MessageChannel email = new EmailChannel();
        MessageChannel sms = new SMSChannel();

        DispatchStrategy immediate = new ImmediateStrategy();
        DispatchStrategy businessHours = new BusinessHoursStrategy();

        Alert critical = new CriticalSystemAlert(sms, immediate);
        Alert daily = new DailySummaryAlert(email, businessHours);

        System.out.println(">>> 1. Critical Alert via SMS at Night (02:30)");
        critical.sendAlert(
                "+77001234567",
                "Database primary node kernel panic",
                10, 2);

        System.out.println(">>> 2. Daily Summary via Email at Night (23:00)");
        daily.sendAlert(
                "manager@company.com",
                "System metrics steady",
                1, 23);

        System.out.println(">>> 3. Daily Summary during Working Hours (11:00)");
        daily.sendAlert(
                "manager@company.com",
                "System metrics steady",
                1, 11);
    }
}
