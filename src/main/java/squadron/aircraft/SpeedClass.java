package squadron.aircraft;

public enum SpeedClass {
    VERY_FAST(100, 400),
    FAST(150, 800),
    UNKNOWN(100,200);

    private final int minPause;
    private final int maxPause;

    SpeedClass(int minPause, int maxPause) {
        this.minPause = minPause;
        this.maxPause = maxPause;
    }

    public int getMinPause() {
        return minPause;
    }

    public int getMaxPause() {
        return maxPause;
    }

}
