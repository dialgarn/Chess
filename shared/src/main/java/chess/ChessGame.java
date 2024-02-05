package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor team;
    private ChessBoard board;

    public ChessGame() {
        // setTeamTurn(TeamColor.WHITE);

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if(piece == null){
            return null;
        } else {
            HashSet<ChessMove> moves = (HashSet<ChessMove>) piece.pieceMoves(getBoard(), startPosition);
            HashSet<ChessMove> validMoveSet = (HashSet<ChessMove>) moves.clone();
            ChessBoard currentBoard = board.clone();
            // boolean inCheck = isInCheck(piece.getTeamColor());

            for (ChessMove move : moves) {
                board.addPiece(move.getStartPosition(), null);
                board.addPiece(move.getEndPosition(), piece);

                if (isInCheck(piece.getTeamColor())) {
                    validMoveSet.remove(move);
                }
                board = currentBoard.clone();
            }
            
            return validMoveSet;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());
        HashSet<ChessMove> validMoveSet = (HashSet<ChessMove>) validMoves(move.getStartPosition());

        if (piece.getTeamColor() != team) {
            throw new InvalidMoveException("Invalid Make Move");
        }

        if (validMoveSet.contains(move)) {
            board.addPiece(move.getStartPosition(), null);
            if (move.getPromotionPiece() != null) {
                piece.setPiece(move.getPromotionPiece());
            }
            board.addPiece(move.getEndPosition(), piece);
            if (team == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
            return;
        } else {
            throw new InvalidMoveException("Invalid Make Move");
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getBoard().getPiece(ChessPiece.PieceType.KING, teamColor);
        HashSet<ChessMove> attackMoves = new HashSet<>();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = getBoard().getPiece(new ChessPosition(i, j));
                if (piece == null) {
                    continue;
                }
                if (piece.getTeamColor() != teamColor) {
                    attackMoves.addAll(piece.pieceMoves(getBoard(), new ChessPosition(i, j)));
                }
            }
        }


        for (ChessMove attack: attackMoves) {
            if (attack.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = getBoard().getPiece(ChessPiece.PieceType.KING, teamColor);
        HashSet<ChessMove> possibleMoves = (HashSet<ChessMove>) validMoves(kingPosition);
        boolean currentlyInCheck = isInCheck(teamColor);

        return currentlyInCheck && possibleMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
