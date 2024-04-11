package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor team = TeamColor.WHITE;
    private ChessBoard board;

    private boolean game_over = false;

    public boolean isGame_over() {
        return game_over;
    }

    public void setGame_over(boolean game_over) {
        this.game_over = game_over;
    }

    public ChessGame() {
        board = new ChessBoard();
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

            // for each possible move, make the move and check if it will result in your team being in check

            for (ChessMove move : moves) {
                board.addPiece(move.getStartPosition(), null);
                board.addPiece(move.getEndPosition(), piece);

                // if team in check, remove that move from the set of valid moves
                if (isInCheck(piece.getTeamColor())) {
                    validMoveSet.remove(move);
                }
                // reset board and go on to the next move
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

        // checks to make sure it is your turn to make a move
        if (piece.getTeamColor() != team) {
            throw new InvalidMoveException("Invalid Make Move");
        }

        // if move is valid...
        if (validMoveSet.contains(move)) {
            // make current location null
            board.addPiece(move.getStartPosition(), null);

            // checks to see if it will be promoted or not (for pawns only)
            if (move.getPromotionPiece() != null) {
                piece.setPiece(move.getPromotionPiece());
            }

            // set the new location of the piece
            board.addPiece(move.getEndPosition(), piece);

            // swap turn
            if (team == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
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

        // gets the possible moves of all opposing team's pieces
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


        // if any move can attack the kings current position, team is in check
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

        // if currently in check and cant make any possible moves, is in checkmate
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
        ChessPosition kingPosition = getBoard().getPiece(ChessPiece.PieceType.KING, teamColor);
        HashSet<ChessMove> possibleMoves = (HashSet<ChessMove>) validMoves(kingPosition);
        boolean currentlyInCheck = isInCheck(teamColor);

        // if not currently in check but cant make moves, it's a stalemate
        return !currentlyInCheck && possibleMoves.isEmpty();
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

//    @Override
//    public String toString() {
//        return this.board.toString();
//    }

}
