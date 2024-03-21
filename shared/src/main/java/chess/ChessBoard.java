package chess;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {
    private ChessPiece[][] board;

    Map<ChessPiece.PieceType, String> whitePieceCharacters = new EnumMap<>(ChessPiece.PieceType.class);
    Map<ChessPiece.PieceType, String> blackPieceCharacters = new EnumMap<>(ChessPiece.PieceType.class);

    Map<ChessGame.TeamColor, Map<ChessPiece.PieceType, String>> pieceCharacters = new EnumMap<>(ChessGame.TeamColor.class);

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
        whitePieceCharacters.put(ChessPiece.PieceType.KING, EscapeSequences.WHITE_KING);
        whitePieceCharacters.put(ChessPiece.PieceType.QUEEN, EscapeSequences.WHITE_QUEEN);
        whitePieceCharacters.put(ChessPiece.PieceType.BISHOP, EscapeSequences.WHITE_BISHOP);
        whitePieceCharacters.put(ChessPiece.PieceType.KNIGHT, EscapeSequences.WHITE_KNIGHT);
        whitePieceCharacters.put(ChessPiece.PieceType.ROOK, EscapeSequences.WHITE_ROOK);
        whitePieceCharacters.put(ChessPiece.PieceType.PAWN, EscapeSequences.WHITE_PAWN);

        blackPieceCharacters.put(ChessPiece.PieceType.KING, EscapeSequences.BLACK_KING);
        blackPieceCharacters.put(ChessPiece.PieceType.QUEEN, EscapeSequences.BLACK_QUEEN);
        blackPieceCharacters.put(ChessPiece.PieceType.BISHOP, EscapeSequences.BLACK_BISHOP);
        blackPieceCharacters.put(ChessPiece.PieceType.KNIGHT, EscapeSequences.BLACK_KNIGHT);
        blackPieceCharacters.put(ChessPiece.PieceType.ROOK, EscapeSequences.BLACK_ROOK);
        blackPieceCharacters.put(ChessPiece.PieceType.PAWN, EscapeSequences.BLACK_PAWN);

        pieceCharacters.put(ChessGame.TeamColor.WHITE, whitePieceCharacters);
        pieceCharacters.put(ChessGame.TeamColor.BLACK, blackPieceCharacters);
    }

    /**
     * Adds a
     * chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Gets the position of the first instance of a piece
     *
     * @param piece The type of the piece to be found
     * @param color The color of the piece you want to find
     * @return The location of the piece, or null if no piece of that type exists
     */
    public ChessPosition getPiece(ChessPiece.PieceType piece, ChessGame.TeamColor color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition pieceLocation = new ChessPosition(i + 1, j + 1);
                ChessPiece targetPiece = getPiece(pieceLocation);
                if (targetPiece == null) {
                    continue;
                }
                if (targetPiece.getPieceType() == piece && targetPiece.getTeamColor() == color) {
                    return pieceLocation;
                }
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        wipeBoard();

        // Team White
        addPiece(new ChessPosition(1,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(2,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

        // Team Black
        addPiece(new ChessPosition(8,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(7,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
    }

    private void wipeBoard() {
        board = new ChessPiece[8][8];
    }

    public String realToString() {
        StringBuilder output = new StringBuilder();
        output.append("     h     g     f     e     d     c     b     a\n");
        // Add top border
        output.append("  +-----------------------------------------------+\n");

        // Add rows with pieces
        // Print the board with white at the bottom
        for (int i = 7; i >= 0; i--) {
            // Add row label
            output.append(i + 1).append(" |");

            for (int j = 0; j < 8; j++) {
                ChessPosition pieceLocation = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = getPiece(pieceLocation);
                output.append(piece == null ? "     |" : " " + pieceCharacters.get(piece.getTeamColor()).get(piece.getPieceType()) + " |");
            }

            // Add row label again at the end of the row
            output.append(" ").append(i + 1).append("\n");
        }

        // Add bottom border
        output.append("  +-----------------------------------------------+\n");

        // Add column labels
        output.append("     h     g     f     e     d     c     b     a\n\n\n");

        output.append("     a     b     c     d     e     f     g     h\n");


        // Print the board with black at the bottom
        for (int i = 0; i < 8; i++) {
            // Add row label
            output.append(8 - i).append(" |");

            for (int j = 7; j >= 0; j--) {
                ChessPosition pieceLocation = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = getPiece(pieceLocation);
                output.append(piece == null ? "     |" : " " + pieceCharacters.get(piece.getTeamColor()).get(piece.getPieceType()) + " |");
            }

            // Add row label again at the end of the row
            output.append(" ").append(8 - i).append("\n");
        }

        // Add bottom border
        output.append("  +-----------------------------------------------+\n");

        // Add column labels
        output.append("     a     b     c     d     e     f     g     h\n");

        return output.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.deepToString(board) +
                '}';
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();
            clone.board = new ChessPiece[this.board.length][this.board[0].length];
            for (int i = 0; i < this.board.length; i++) {
                clone.board[i] = Arrays.copyOf(this.board[i], this.board[i].length);
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
