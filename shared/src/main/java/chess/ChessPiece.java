package chess;
import java.util.*;
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
            case KING -> kingMove(board, myPosition);
            case QUEEN -> queenMove(board, myPosition);
            case BISHOP -> bishopMove(board, myPosition);
            case KNIGHT -> knightMove(board, myPosition);
            case ROOK -> rookMove(board, myPosition);
            case PAWN -> pawnMove(board, myPosition);
        };
    }
    private Collection<ChessMove> kingMove(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> moves = new HashSet<>();
        int row = position.getRow();
        int column = position.getColumn();
        // up
        row++;
        if (row <= 8){
            singleCollisionCheck(board, position, moves, row, column);
        }
        // up and right
        column++;
        if (row <= 8) {
            if (column <= 8) {
                singleCollisionCheck(board, position, moves, row, column);
            }
        }
        // right
        row--;
        if (column <= 8){
            singleCollisionCheck(board, position, moves, row, column);
        }
        // down and right
        row--;
        if (row >= 1) {
            if (column <= 8) {
                singleCollisionCheck(board, position, moves, row, column);
            }
        }
        // down
        column--;
        if (row >= 1) {
            singleCollisionCheck(board, position, moves, row, column);
        }
        // down and left
        column--;
        if (row >= 1) {
            if (column >= 1) {
                singleCollisionCheck(board, position, moves, row, column);
            }
        }
        // left
        row++;
        if (column >= 1) {
            singleCollisionCheck(board, position, moves, row, column);
        }
        // up and left
        row++;
        if (row <= 8) {
            if (column >= 1) {
                singleCollisionCheck(board, position, moves, row, column);
            }
        }
        return moves;
    }
    private Collection<ChessMove> queenMove(ChessBoard board, ChessPosition position){
        // gets the diagonal move set by calling bishopMove
        HashSet<ChessMove> queenMoves = (HashSet<ChessMove>) bishopMove(board, position);
        // gets the straight move set by calling rookMove
        HashSet<ChessMove> straightMoves = (HashSet<ChessMove>) rookMove(board, position);
        // combines the two move sets
        queenMoves.addAll(straightMoves);
        return queenMoves;
    }
    private Collection<ChessMove> bishopMove(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> moves = new HashSet<>();
        int row = position.getRow();
        int column = position.getColumn();

        // up and to the right
        while (row < 8 && column < 8) {
            row++;
            column++;
            if (moveCalc(board, position, row, column, moves)) break;
        }

        // down and to the right
        row = position.getRow();
        column = position.getColumn();
        while (row > 1 && column < 8) {
            row--;
            column++;
            if (moveCalc(board, position, row, column, moves)) break;
        }

        // down and to the left
        row = position.getRow();
        column = position.getColumn();
        while (row > 1 && column > 1) {
            row--;
            column--;
            if (moveCalc(board, position, row, column, moves)) break;
        }

        // up and to the right
        row = position.getRow();
        column = position.getColumn();
        while (row < 8 && column > 1) {
            row++;
            column--;
            if (moveCalc(board, position, row, column, moves)) break;
        }

        return moves;
    }

    private boolean moveCalc(ChessBoard board, ChessPosition position, int row, int column, HashSet<ChessMove> moves) {
        ChessPosition end = new ChessPosition(row, column);

        if (board.getPiece(end) == null) {
            ChessMove pieceMove = new ChessMove(position, end, null);
            moves.add(pieceMove);
        } else if (color != board.getPiece(end).color) {
            ChessMove pieceMove = new ChessMove(position, end, null);
            moves.add(pieceMove);
            return true;
        } else return color == board.getPiece(end).color;
        return false;
    }

    private Collection<ChessMove> knightMove(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> moves = new HashSet<>();
        int row = position.getRow();
        int column = position.getColumn();

        // up and right/left
        row += 2;
        if (row <= 8) {
            column ++;
            if (column <= 8){
                singleCollisionCheck(board, position, moves, row, column);
            }

            column -= 2;
            if (column >= 1){
                singleCollisionCheck(board, position, moves, row, column);
            }
        }

        // right and up/down
        row = position.getRow();
        column = position.getColumn();

        column += 2;
        if (column <= 8){
            row++;
            if (row <= 8) {
                singleCollisionCheck(board, position, moves, row, column);
            }

            row -= 2;
            if (row >= 1) {
                singleCollisionCheck(board, position, moves, row, column);
            }
        }

        // down and right/left
        row = position.getRow();
        column = position.getColumn();

        row -= 2;
        if (row >= 1){
            column++;
            if (column <= 8) {
                singleCollisionCheck(board, position, moves, row, column);
            }

            column -= 2;
            if (column >= 1) {
                singleCollisionCheck(board, position, moves, row, column);
            }
        }

        // left and up/down
        row = position.getRow();
        column = position.getColumn();

        column -= 2;
        if (column >= 1){
            row++;
            if (row <= 8) {
                singleCollisionCheck(board, position, moves, row, column);
            }

            row -= 2;
            if (row >= 1) {
                singleCollisionCheck(board, position, moves, row, column);
            }
        }

        return moves;
    }
    private void singleCollisionCheck(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves, int row, int column) {
        ChessPosition end = new ChessPosition(row, column);

        if (board.getPiece(end) == null) {
            ChessMove pieceMove = new ChessMove(position, end, null);
            moves.add(pieceMove);
        } else if (color != board.getPiece(end).color) {
            ChessMove pieceMove = new ChessMove(position, end, null);
            moves.add(pieceMove);
        }
    }
    private Collection<ChessMove> rookMove(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> moves = new HashSet<>();
        int row = position.getRow();
        int column = position.getColumn();
        // up
        while (row < 8) {
            row++;
            if (moveCalc(board, position, row, column, moves)) break;
        }
        // down
        row = position.getRow();
        column = position.getColumn();
        while (row > 1) {
            row--;
            if (moveCalc(board, position, row, column, moves)) break;
        }
        // left
        row = position.getRow();
        column = position.getColumn();
        while (column > 1) {
            column--;
            if (moveCalc(board, position, row, column, moves)) break;
        }
        // right
        row = position.getRow();
        column = position.getColumn();
        while (column < 8) {
            column++;
            if (moveCalc(board, position, row, column, moves)) break;
        }
        return moves;
    }
    private Collection<ChessMove> pawnMove(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> moves = (HashSet<ChessMove>) pawnWhiteMove(board, position);

        HashSet<ChessMove> pawnBlackMoves = (HashSet<ChessMove>) pawnBlackMove(board, position);

        moves.addAll(pawnBlackMoves);

        return moves;
    }
    private Collection<ChessMove> pawnWhiteMove(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        int row = position.getRow();
        int column = position.getColumn();
        boolean canMove;
        if (color == ChessGame.TeamColor.WHITE){
            row++;
            // check for promotion
            if (row == 8){
                ChessPosition end = new ChessPosition(row, column);
                pawnPromotion(position, moves, end);
            } else { // normal move
                canMove = pawnAdvanceCheck(board, position, moves, row, column);
                // initial start can move 2 spots
                if (position.getRow() == 2 && canMove) {
                    row++;
                    pawnAdvanceCheck(board, position, moves, row, column);
                }
            }
            // pawn attacks
            row = position.getRow();
            row++;
            column++;
            if (column <= 8) {
                // pawn attack promotion
                pawnAttackPromotionWhite(board, position, moves, row, column);
            }
            column -= 2;
            if (column >= 1) {
                // pawn attack promotion
                pawnAttackPromotionWhite(board, position, moves, row, column);
            }
        }
        return moves;
    }

    private void pawnAttackPromotionWhite(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves, int row, int column) {
        ChessPosition end = new ChessPosition(row, column);
        if (row == 8) {
            if (board.getPiece(end) != null) {
                if (color != board.getPiece(end).color) {
                    pawnPromotion(position, moves, end);
                }
            }
        } else {
            if (board.getPiece(end) != null) {
                if (color != board.getPiece(end).color) {
                    ChessMove pieceMove = new ChessMove(position, end, null);
                    moves.add(pieceMove);
                }
            }
        }
    }

    private Collection<ChessMove> pawnBlackMove(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        int row = position.getRow();
        int column = position.getColumn();
        boolean canMove;
        if (color == ChessGame.TeamColor.BLACK) {
            row--;
            // check for promotion
            if (row == 1){
                ChessPosition end = new ChessPosition(row, column);
                pawnPromotion(position, moves, end);
            } else { // normal move
                canMove = pawnAdvanceCheck(board, position, moves, row, column);
                // initial start can move 2 spots
                if (position.getRow() == 7) {
                    if (canMove) {
                        row--;
                        pawnAdvanceCheck(board, position, moves, row, column);
                    }
                }
            }
            // pawn attacks
            row = position.getRow();
            row--;
            column++;
            if (column <= 8) {
                // pawn attack promotion
                pawnAttackPromotionBlack(board, position, moves, row, column);
            }
            column -= 2;
            if (column >= 1) {
                // pawn attack promotion
                pawnAttackPromotionBlack(board, position, moves, row, column);
            }
        }
        return moves;
    }

    private void pawnAttackPromotionBlack(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves, int row, int column) {
        ChessPosition end = new ChessPosition(row, column);
        if (row == 1) {
            if (board.getPiece(end) != null) {
                if (color != board.getPiece(end).color) {
                    pawnPromotion(position, moves, end);
                }
            }
        } else {
            if (board.getPiece(end) != null) {
                if (color != board.getPiece(end).color) {
                    ChessMove pieceMove = new ChessMove(position, end, null);
                    moves.add(pieceMove);
                }
            }
        }
    }

    private void pawnPromotion(ChessPosition position, HashSet<ChessMove> moves, ChessPosition end) {
        ChessMove pieceMove = new ChessMove(position, end, PieceType.QUEEN);
        moves.add(pieceMove);

        pieceMove = new ChessMove(position, end, PieceType.BISHOP);
        moves.add(pieceMove);

        pieceMove = new ChessMove(position, end, PieceType.ROOK);
        moves.add(pieceMove);

        pieceMove = new ChessMove(position, end, PieceType.KNIGHT);
        moves.add(pieceMove);
    }
    private boolean pawnAdvanceCheck(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves, int row, int column) {
        ChessPosition end = new ChessPosition(row, column);
        if (board.getPiece(end) == null) {
            ChessMove pieceMove = new ChessMove(position, end, null);
            moves.add(pieceMove);
            return true;
        }
        return false;
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