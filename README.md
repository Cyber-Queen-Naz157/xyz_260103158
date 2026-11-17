# Flight Booking Facade Pattern — Java Lab

Seven Java source files, no external libraries and no package declaration.

## Run

```bash
javac *.java
java Main
```

## Tests

1. Standard booking: baggage fee $71, total $391 converted to EUR 359.72.
2. Blocked seat 13C: booking fails cleanly.
3. Invalid card: seat lock `LOCK-KC101-07D` is released before disconnect.
4. SILVER tier: baggage engine is skipped; 320 USD converts to 294.40 EUR; 10% discount charges 264.96 EUR.

`Main.java` interacts only with `FlightBookingFacade`. The original five subsystem classes are preserved as shown in the assignment screenshots.
