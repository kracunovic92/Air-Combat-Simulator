package radar;

import java.util.List;

public record RadarScanResult(
        List<RadarContact> contacts,
        boolean selfDestroyed,
        boolean hitConfirmed,
        String hitTargetId
) {}
