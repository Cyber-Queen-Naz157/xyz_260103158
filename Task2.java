import java.util.HashMap;
import java.util.Map;

public class Task2 {

    static class LegacyStockFeed {
        public String fetchRawTickerPacket(String ticker) {
            System.out.println("NETWORK FETCH: Calling upstream adapter for " + ticker);

            if (ticker.equals("AAPL"))
                return "TICKER=AAPL;BID=182.50;ASK=183.10;STATUS=OK;TIMESTAMP=1700000";

            if (ticker.equals("GOOG"))
                return "TICKER=GOOG;BID=140.10;ASK=141.00;STATUS=OK;TIMESTAMP=1700000";

            return "TICKER=" + ticker + ";BID=0.00;ASK=0.00;STATUS=UNKNOWN";
        }
    }

    interface StockProvider {
        double getMarketPrice(String ticker);
        String getStockStatus(String ticker);
    }

    static class StockFeedAdapter implements StockProvider {
        private final LegacyStockFeed legacyFeed;

        public StockFeedAdapter(LegacyStockFeed feed) {
            this.legacyFeed = feed;
        }

        private Map<String, String> parsePacket(String ticker) {
            String raw = legacyFeed.fetchRawTickerPacket(ticker);
            Map<String, String> data = new HashMap<>();

            for (String item : raw.split(";")) {
                String[] pair = item.split("=", 2);
                if (pair.length == 2)
                    data.put(pair[0], pair[1]);
            }
            return data;
        }

        @Override
        public double getMarketPrice(String ticker) {
            Map<String, String> data = parsePacket(ticker);

            double bid = Double.parseDouble(data.get("BID"));
            double ask = Double.parseDouble(data.get("ASK"));

            return (bid + ask) / 2.0;
        }

        @Override
        public String getStockStatus(String ticker) {
            return parsePacket(ticker).get("STATUS");
        }
    }

    static class CachedStockProxy implements StockProvider {
        private final StockProvider realProvider;
        private final long ttlMillis;

        private final Map<String, Double> priceCache = new HashMap<>();
        private final Map<String, Long> cacheTimes = new HashMap<>();
        private final Map<String, Long> lastFetchTimes = new HashMap<>();

        public CachedStockProxy(StockProvider provider, long ttlMillis) {
            if (provider == null)
                throw new IllegalArgumentException("Provider cannot be null");

            this.realProvider = provider;
            this.ttlMillis = ttlMillis;
        }

        @Override
        public double getMarketPrice(String ticker) {
            long now = System.currentTimeMillis();

            if (priceCache.containsKey(ticker)) {
                long cacheAge = now - cacheTimes.get(ticker);

                if (cacheAge < ttlMillis) {
                    System.out.println("[CACHE HIT] Returning cached price for " + ticker);
                    return priceCache.get(ticker);
                }
            }

            long lastFetch = lastFetchTimes.getOrDefault(ticker, 0L);

            if (now - lastFetch < 2000) {
                System.out.println("[RATE LIMIT] Request throttled for " + ticker);
                return priceCache.getOrDefault(ticker, Double.NaN);
            }

            double price = realProvider.getMarketPrice(ticker);

            priceCache.put(ticker, price);
            cacheTimes.put(ticker, now);
            lastFetchTimes.put(ticker, now);

            return price;
        }

        @Override
        public String getStockStatus(String ticker) {
            return realProvider.getStockStatus(ticker);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        StockProvider provider = new CachedStockProxy(
                new StockFeedAdapter(new LegacyStockFeed()), 2000);

        System.out.println(">>> 1. Initial Request (Cold Cache)");
        System.out.printf("AAPL Price: $%.2f%n", provider.getMarketPrice("AAPL"));

        System.out.println(">>> 2. Immediate Repeat Request (Hot Cache)");
        System.out.printf("AAPL Price: $%.2f%n", provider.getMarketPrice("AAPL"));

        System.out.println(">>> 3. Sleeping 2100ms to invalidate cache");
        Thread.sleep(2100);

        System.out.println(">>> 4. Subsequent Request (Expired Cache)");
        System.out.printf("AAPL Price: $%.2f%n", provider.getMarketPrice("AAPL"));
    }
}
