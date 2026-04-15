package common;

public record GridCell(int column, int row) {

    public GridCell{
        validate(column, row);
    }

    private static void validate(int column, int row){
        if (column < 0 || column > 7){
            throw  new IllegalArgumentException("Column fail");
        }
        if(row < 0 || row > 7){
            throw  new IllegalArgumentException("Row fail");
        }
    }

    public String label() {
        char col = (char) ('A' + column);
        return col + Integer.toString(row + 1);
    }

    @Override
    public String toString() {
        return label();
    }
}
