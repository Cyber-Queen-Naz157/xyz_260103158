public class BulbAdapter implements SmartDevice {
    private final LegacyBulb bulb;

    /*
     * Student-ID calibration digit K.
     * The worksheet's worked example uses K = 4 (student ID ending in 4).
     * Change only this value if your own student ID ends in another digit.
     */
    private static final int K = 4;

    public BulbAdapter(LegacyBulb bulb) {
        if (bulb == null) {
            throw new IllegalArgumentException("LegacyBulb must not be null");
        }
        this.bulb = bulb;
    }

    @Override
    public void turnOn() {
        bulb.setBrightness(255);
    }

    @Override
    public void turnOff() {
        bulb.setBrightness(0);
    }

    @Override
    public boolean isOn() {
        try {
            return bulb.hasPower() && bulb.readBrightness() > 0;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    @Override
    public int getPowerPercent() {
        try {
            if (!bulb.hasPower()) {
                return 0;
            }

            int rawPercent = (bulb.readBrightness() * 100) / 255;
            int calibratedPercent = rawPercent + K;
            return Math.min(calibratedPercent, 100);
        } catch (RuntimeException ex) {
            return 0;
        }
    }
}
