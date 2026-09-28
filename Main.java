import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("        OMNIHOME SMART CONTROLLER: SYSTEM STARTUP");
        System.out.println("====================================================");

        // Stage 3, Step 1: instantiate one legacy bulb and one legacy thermostat.
        LegacyBulb rawBulb = new LegacyBulb();
        LegacyThermostat rawThermostat = new LegacyThermostat();

        // Stage 3, Step 2: wrap each legacy object with its Object Adapter.
        SmartDevice bulbAdapter = new BulbAdapter(rawBulb);
        SmartDevice thermostatAdapter = new ThermostatAdapter(rawThermostat);

        // Stage 3, Step 3: store heterogeneous devices through one Target interface.
        List<SmartDevice> deviceList = List.of(bulbAdapter, thermostatAdapter);

        // Stage 3, Step 4: the client depends only on SmartDevice.
        ModernHub hub = new ModernHub(deviceList);

        System.out.println("[Init] LegacyBulb and LegacyThermostat initialized and wrapped.");
        System.out.println("[Hub] Registering 2 adapted devices into ModernHub...");

        System.out.println("\n---- OPERATION: ACTIVATE ALL DEVICES ----");
        hub.activateAll();
        System.out.println("[Action] ModernHub.activateAll() invoked.");
        System.out.println(" -> BulbAdapter: Brightness set to 255.");
        System.out.println(" -> ThermostatAdapter: Dial set to 'LOW'.");
        System.out.println("[Status] All devices reported active: "
                + (bulbAdapter.isOn() && thermostatAdapter.isOn()));
        printTelemetry(bulbAdapter, thermostatAdapter, hub);

        /*
         * MANDATORY TYPE-SAFETY / ARCHITECTURAL REFLECTION
         * Uncommenting the next line causes a compile-time type error:
         */
        // ModernHub badHub = new ModernHub(List.of(rawBulb)); // COMPILE ERROR
        /*
         * 1) Why it fails:
         *    ModernHub requires List<SmartDevice>. LegacyBulb does not implement
         *    SmartDevice, so a raw LegacyBulb cannot be used where SmartDevice
         *    is required.
         *
         * 2) How the Object Adapter solves it:
         *    BulbAdapter implements SmartDevice and keeps a LegacyBulb by
         *    composition. It translates the SmartDevice protocol into the
         *    LegacyBulb API without changing LegacyBulb.java.
         */

        System.out.println("\n---- AUDIT: HARDWARE FAULT INJECTION (STAGE 4) ----");

        // Fault 1: broken filament. Brightness may remain non-zero internally,
        // but adapter must report safe OFF/0% values.
        rawBulb.breakFilament();
        System.out.println("[Fault 1] Filament physically severed on LegacyBulb.");
        System.out.println(" -> BulbAdapter.isOn(): " + bulbAdapter.isOn()
                + " [EXPECTED: false]");
        System.out.println(" -> BulbAdapter.getPowerPercent(): "
                + bulbAdapter.getPowerPercent() + "% [EXPECTED: 0%]");

        // Fault 2: corrupt thermostat state.
        rawThermostat.rotateDial("STUCK");
        System.out.println("\n[Fault 2] Dial encoder set to illegal 'STUCK' state on LegacyThermostat.");
        System.out.println(" -> ThermostatAdapter.isOn(): " + thermostatAdapter.isOn()
                + " [EXPECTED: false]");
        System.out.println(" -> ThermostatAdapter.getPowerPercent(): "
                + thermostatAdapter.getPowerPercent() + " [EXPECTED: -1]");

        // Extra null-state defensive check required by the Stage 4 specification.
        rawThermostat.rotateDial(null);
        System.out.println("\n[Fault 3] Dial state set to null.");
        System.out.println(" -> ThermostatAdapter.isOn(): " + thermostatAdapter.isOn()
                + " [EXPECTED: false]");
        System.out.println(" -> ThermostatAdapter.getPowerPercent(): "
                + thermostatAdapter.getPowerPercent() + " [EXPECTED: -1]");

        System.out.println("\n---- OPERATION: EMERGENCY SHUTDOWN ----");
        hub.emergencyShutdown();
        System.out.println("[Action] ModernHub.emergencyShutdown() invoked.");
        System.out.println(" -> BulbAdapter: Brightness set to 0.");
        System.out.println(" -> ThermostatAdapter: Dial rotated to 'IDLE'.");
        System.out.println("[Power] Final Average Power Usage: "
                + hub.calculateAveragePowerUsage() + "%");

        System.out.println("\n====================================================");
        System.out.println("           ALL INTEGRATION TESTS PASSED");
        System.out.println("====================================================");
    }

    private static void printTelemetry(
            SmartDevice bulb,
            SmartDevice thermostat,
            ModernHub hub) {
        System.out.println("[Power] Fleet Average Power Usage: "
                + hub.calculateAveragePowerUsage() + "%");
        System.out.println("        (Bulb: " + bulb.getPowerPercent()
                + "%, Thermostat: " + thermostat.getPowerPercent() + "%)");
    }
}
