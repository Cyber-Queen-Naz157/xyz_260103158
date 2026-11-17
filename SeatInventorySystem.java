public class SeatInventorySystem {
    private boolean connected = false;
    public void connectToReservationServer() {
        this.connected = true;
        System.out.println("[SeatInventory] Connected to central seat server.");
    }
    public boolean isSeatAvailable(String flightCode, String seatNumber) {
        if (!connected) throw new IllegalStateException("SeatInventorySystem is not connected.");
        if (seatNumber == null || seatNumber.trim().isEmpty()) return false;
        return !seatNumber.toUpperCase().startsWith("13");
    }
    public String lockSeat(String flightCode, String seatNumber, String passengerName) {
        if (!isSeatAvailable(flightCode, seatNumber)) throw new IllegalArgumentException("Seat " + seatNumber + " cannot be locked.");
        String lockToken = "LOCK-" + flightCode.toUpperCase() + "-" + seatNumber.toUpperCase();
        System.out.println("[SeatInventory] Locked seat " + seatNumber + " for " + passengerName + " (" + lockToken + ")");
        return lockToken;
    }
    public void releaseSeatLock(String lockToken) {
        if (connected && lockToken != null) System.out.println("[SeatInventory] Released lock: " + lockToken);
    }
    public void disconnect() {
        this.connected = false;
        System.out.println("[SeatInventory] Disconnected from seat server.");
    }
}
