package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public PieceType piece = null;
    public ChessGame.TeamColor color = null;


    public void setPiece(PieceType piece) {
        this.piece = piece;
    }

    public void setColor(ChessGame.TeamColor color) {
        this.color = color;
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {

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
            case BISHOP -> bishopMove();
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

    private Collection<ChessMove> bishopMove(){
        return null;
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

}
