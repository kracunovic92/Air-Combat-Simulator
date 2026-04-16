package common;

public enum Side {
    BLUE(new GridCell(0, 0)),
    RED(new GridCell(7, 7));

    private final GridCell base;

    Side(GridCell base) {
        this.base = base;
    }

    public GridCell base() {
        return base;
    }
}
