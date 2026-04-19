package radar;

public record HitResult(boolean hitConfirmed, String hitTargetId) {
    static HitResult noHit() {
        return new HitResult(false, null);
    }

    static HitResult hit(String targetId) {
        return new HitResult(true, targetId);
    }
}
