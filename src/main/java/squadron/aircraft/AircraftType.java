package squadron.aircraft;

public enum AircraftType {
    MIG_31(SpeedClass.VERY_FAST, RadarClass.LONG_RANGE),
    F_15(SpeedClass.FAST, RadarClass.STANDARD),
    F_22(SpeedClass.FAST, RadarClass.LONG_RANGE),
    F_35(SpeedClass.FAST, RadarClass.LONG_RANGE),
    SU_30(SpeedClass.FAST, RadarClass.STANDARD),
    SU_35(SpeedClass.FAST, RadarClass.LONG_RANGE),
    SU_57(SpeedClass.FAST, RadarClass.LONG_RANGE),
    UNKNOWN(SpeedClass.UNKNOWN, RadarClass.UNKNOWN);
    private final SpeedClass speedClass;
    private final RadarClass radarClass;

    AircraftType(SpeedClass speedClass, RadarClass radarClass) {
        this.speedClass = speedClass;
        this.radarClass = radarClass;
    }
    public SpeedClass getSpeedClass() {
        return speedClass;
    }

    public RadarClass getRadarClass() {
        return radarClass;
    }
}
