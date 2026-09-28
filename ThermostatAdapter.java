public class ThermostatAdapter implements SmartDevice {
    private final LegacyThermostat thermostat;

    public ThermostatAdapter(LegacyThermostat thermostat) {
        if (thermostat == null) {
            throw new IllegalArgumentException("LegacyThermostat must not be null");
        }
        this.thermostat = thermostat;
    }

    @Override
    public void turnOn() {
        String state = safeReadState();
        if ("IDLE".equals(state)) {
            thermostat.rotateDial("LOW");
        }
        // LOW, MEDIUM, MAX, unknown, and null states are left unchanged.
    }

    @Override
    public void turnOff() {
        thermostat.rotateDial("IDLE");
    }

    @Override
    public boolean isOn() {
        String state = safeReadState();
        return "LOW".equals(state)
                || "MEDIUM".equals(state)
                || "MAX".equals(state);
    }

    @Override
    public int getPowerPercent() {
        String state = safeReadState();

        if ("IDLE".equals(state)) return 0;
        if ("LOW".equals(state)) return 33;
        if ("MEDIUM".equals(state)) return 66;
        if ("MAX".equals(state)) return 100;

        // Sentinel required by Stage 4 for corrupt or null dial states.
        return -1;
    }

    private String safeReadState() {
        try {
            return thermostat.checkDial();
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
