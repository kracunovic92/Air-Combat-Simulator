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

    public static GridCell fromLabel(String label) {

        label = label.trim().toUpperCase();

        char colChar = label.charAt(0);
        int column = colChar - 'A';

        int row;
        try {
            row = Integer.parseInt(label.substring(1)) - 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in label: " + label);
        }
        return new GridCell(column, row);
    }
    public static String toLabel(int row, int column){
        validate(column,row);
        char col = (char) ('A' + column);
        return col + Integer.toString(row + 1);
    }
    public Position toPosition(){

        return new Position(column, row);
    }
}
