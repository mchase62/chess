package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.deepEquals(board, chessGame.board) && teamTurn == chessGame.teamTurn && Objects.equals(lastStartPosition, chessGame.lastStartPosition) && Objects.equals(lastEndPosition, chessGame.lastEndPosition) && lastPromotionPiece == chessGame.lastPromotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, lastStartPosition, lastEndPosition, lastPromotionPiece);
    }

    private TeamColor teamTurn;
    private ChessPosition lastStartPosition;
    private ChessPosition lastEndPosition;
    ChessPiece.PieceType lastPromotionPiece;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
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
        ChessPiece replacingPiece;
        if (board.getPiece(startPosition) == null)  // if there is not a piece
            return null;

        // get all moves
        Collection<ChessMove> chessMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);

        ArrayList<ChessMove> movesToRemove = new ArrayList<>();

        for (ChessMove move : chessMoves) {
            replacingPiece = board.getPiece(move.getEndPosition());
            try {
                makeMove(move);  // try making the move
            } catch (InvalidMoveException e) {
                movesToRemove.add(move);
            } finally {
                undoMove(move, replacingPiece); // always undo the move, whether it's valid or not
            }
        }
        chessMoves.removeAll(movesToRemove);
        return chessMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        System.out.println("Beginning of move");
        System.out.println(board.toString());
        Collection<ChessMove> moves;
        // make move
        ChessPiece movingPiece = board.getPiece(move.getStartPosition()); // copy the piece
        if(movingPiece != null)
            moves = movingPiece.pieceMoves(board,move.getStartPosition());
        else
            moves = null;
        board.addPiece(move.getStartPosition(), null); // make its old location null
        board.addPiece(move.getEndPosition(), movingPiece); // move it to the new spot
        System.out.println("After moving");
        System.out.println(board.toString());
        // throw exception if piece can't move there, if move leaves king in danger, or not your turn
        if (movingPiece == null || isInCheck(movingPiece.getTeamColor()) || !getTeamTurn().equals(movingPiece.getTeamColor())) {// see if the king is now in danger
            System.out.println("A");
            System.out.println(move.getStartPosition().toString() + " " + move.getEndPosition().toString());
            undoMove(move,movingPiece);
            throw new InvalidMoveException("Invalid move: " + move);
        }
        else if (!moves.contains(move)) {
            System.out.println("B");
            System.out.println(move.getStartPosition().toString() + " " + move.getEndPosition().toString());
            undoMove(move,movingPiece);
            throw new InvalidMoveException("Invalid move: " + move);
        }
        System.out.println("After move");
        System.out.println(board.toString());
        if(getTeamTurn()==TeamColor.WHITE)
            setTeamTurn(TeamColor.BLACK);
        else
            setTeamTurn(TeamColor.WHITE);
    }

    public void undoMove(ChessMove move, ChessPiece replacingPiece) {
        board.addPiece(move.getStartPosition(), board.getPiece(move.getEndPosition()));
        board.addPiece(move.getEndPosition(), replacingPiece);
        if(getTeamTurn()==TeamColor.WHITE)
            setTeamTurn(TeamColor.BLACK);
        else
            setTeamTurn(TeamColor.WHITE);
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPiece checkingPiece;
        ChessPosition checkingPosition;
        Collection<ChessMove> checkingMoves;
        ChessPiece king = new ChessPiece(teamColor, ChessPiece.PieceType.KING); // king of teamColor
        // go through every piece of opposite color and see if piece moves' end position has king
        for(int row = 1; row < 9; row++) {
            for(int col = 1; col < 9; col++) {
                checkingPosition = new ChessPosition(row,col);
                checkingPiece = board.getPiece(checkingPosition);
                if(checkingPiece!=null) { // if there is a piece
                     if(!checkingPiece.getTeamColor().equals(teamColor)){ // if the piece is not our color, check
                        checkingMoves = checkingPiece.pieceMoves(board,checkingPosition); // get all the moves
                        for( ChessMove move : checkingMoves) { // go through the piece moves and see if there's a king at end position
                            if (board.getPiece(move.getEndPosition()) != null && board.getPiece(move.getEndPosition()).equals(king)) // if there's a king
                                return true;
                            }
                    }
                }
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
        boolean isChecked;
        ChessPosition checkingPosition;
        ChessPiece checkingPiece;
        Collection<ChessMove> checkingMoves;
        if(!isInCheck(teamColor))
            return false;
        for(int row = 1; row < 9; row++) {
            for(int col = 1; col < 9; col++) {
                checkingPosition = new ChessPosition(row,col);
                checkingPiece = board.getPiece(checkingPosition);
                if(checkingPiece!=null) { // if there is a piece
                    if(checkingPiece.getTeamColor().equals(teamColor)){ // if the piece is our color, check
                        checkingMoves = checkingPiece.pieceMoves(board,checkingPosition); // get all the moves
                        isChecked = false;
                        for( ChessMove move : checkingMoves) { // go through the piece moves and see if still in check
                            try { // make the move
                                makeMove(move);  // try making the move
                            } catch (InvalidMoveException e) { // if move leaves king in check
                                isChecked = true;
                            } finally { // Always undo the move, whether it's valid or not
                                undoMove(move, checkingPiece);
                            }
                            if (!isChecked) // if not in check
                                return false;
                        }
                    }
                }
            }
        }
        return true;
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
        return board;
    }
}
