package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    public int row;
    public int column;

    public ChessPosition(int row, int col) {
        this.setRow(row);
        this.setColumn(col);
    }

    public void setRow(int row) {
        this.row = row - 1;
    }

    public void setColumn(int column) {
        this.column = column - 1;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.column;
    }
}
