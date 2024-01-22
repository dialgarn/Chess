package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType piece = null;
    private ChessGame.TeamColor color = null;



    public void setPiece(PieceType piece) {
        this.piece = piece;
    }

    public void setColor(ChessGame.TeamColor color) {
        this.color = color;
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        setPiece(type);
        setColor(pieceColor);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return piece;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        return switch (piece) {
            case KING -> kingMove();
            case QUEEN -> queenMove();
            case BISHOP -> bishopMove(board, myPosition);
            case KNIGHT -> knightMove();
            case ROOK -> rookMove();
            case PAWN -> pawnMove();
        };
    }

    private Collection<ChessMove> kingMove(){
        return null;
    }

    private Collection<ChessMove> queenMove(){
        return null;
    }

    private Collection<ChessMove> bishopMove(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> moves = new HashSet<>();
        int row = position.getRow();
        int column = position.getColumn();

        // up and to the right
        while (row < 8 && column < 8) {
            row++;
            column++;
            ChessPosition end = new ChessPosition(row, column);

            if (board.getPiece(end) == null) {
                ChessMove pieceMove = new ChessMove(position, end, null);
                moves.add(pieceMove);
                continue;
            }
            if (color != board.getPiece(end).color) {
                ChessMove pieceMove = new ChessMove(position, end, null);
                moves.add(pieceMove);
                break;
            }

            if (color == board.getPiece(end).color) {
                break;
            }
        }

        // down and to the right
        row = position.getRow();
        column = position.getColumn();
        while (row > 1 && column < 8) {
            row--;
            column++;
            ChessPosition end = new ChessPosition(row, column);

            if (board.getPiece(end) == null) {
                ChessMove pieceMove = new ChessMove(position, end, null);
                moves.add(pieceMove);
                continue;
            }
            if (color != board.getPiece(end).color) {
                ChessMove pieceMove = new ChessMove(position, end, null);
                moves.add(pieceMove);
                break;
            }

            if (color == board.getPiece(end).color) {
                break;
            }
        }

        // down and to the left
        row = position.getRow();
        column = position.getColumn();
        while (row > 1 && column > 1) {
            row--;
            column--;
            ChessPosition end = new ChessPosition(row, column);

            if (board.getPiece(end) == null) {
                ChessMove pieceMove = new ChessMove(position, end, null);
                moves.add(pieceMove);
                continue;
            }
            if (color != board.getPiece(end).color) {
                ChessMove pieceMove = new ChessMove(position, end, null);
                moves.add(pieceMove);
                break;
            }

            if (color == board.getPiece(end).color) {
                break;
            }
        }

        // up and to the right
        row = position.getRow();
        column = position.getColumn();
        while (row < 8 && column > 1) {
            row++;
            column--;
            ChessPosition end = new ChessPosition(row, column);

            if (board.getPiece(end) == null) {
                ChessMove pieceMove = new ChessMove(position, end, null);
                moves.add(pieceMove);
                continue;
            }
            if (color != board.getPiece(end).color) {
                ChessMove pieceMove = new ChessMove(position, end, null);
                moves.add(pieceMove);
                break;
            }

            if (color == board.getPiece(end).color) {
                break;
            }
        }

        return moves;
    }

    private Collection<ChessMove> knightMove(){
        return null;
    }

    private Collection<ChessMove> rookMove(){
        return null;
    }

    private Collection<ChessMove> pawnMove(){
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return piece == that.piece && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, color);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "piece=" + piece +
                ", color=" + color +
                '}';
    }
}
