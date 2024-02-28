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
    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return color == that.color && type == that.type;
    }


    private final ChessGame.TeamColor color;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
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
        return type;
    }

    public HashSet<ChessMove> pawnMoves(ChessPosition myPosition, ChessBoard board, HashSet<ChessMove> chessMoves, int row, int col, PieceType promotionPiece) {
        ChessPosition endPosition;
        if ((myPosition.getRow() != 7 && getTeamColor() == ChessGame.TeamColor.WHITE) || (myPosition.getRow() != 2 && getTeamColor() == ChessGame.TeamColor.BLACK)) // if not the last row, can't promote
            promotionPiece = null;
        if ((row == 2 && ((myPosition.getRow() != 2 && getTeamColor() == ChessGame.TeamColor.WHITE) || myPosition.getRow() != 7 && getTeamColor() == ChessGame.TeamColor.BLACK))) // if move is to jump two, but we're not at intitial spot
            return chessMoves;
        if (((myPosition.getRow() != 2 && getTeamColor() == ChessGame.TeamColor.WHITE) || (myPosition.getRow() != 7 && getTeamColor() == ChessGame.TeamColor.BLACK)) // if not initial spot
        || ((getTeamColor() == ChessGame.TeamColor.WHITE && board.getPiece(new ChessPosition(3, myPosition.getColumn()))!= null) || (getTeamColor() == ChessGame.TeamColor.BLACK && board.getPiece(new ChessPosition(6, myPosition.getColumn()))!= null))) // or there's a piece blocking the 2 square jump
            row = 1;
        if (getTeamColor() == ChessGame.TeamColor.BLACK) // change directions for black
            row *= -1;
        endPosition = new ChessPosition(myPosition.getRow() + row, myPosition.getColumn() + col);
        if(endPosition.getRow() < 1 || endPosition.getColumn() < 1 || endPosition.getRow() > 8 || endPosition.getColumn() > 8)
            return chessMoves;
        if (board.getPiece(endPosition) == null && col == 0) {
            // empty space in front
            chessMoves.add(new ChessMove(myPosition, endPosition, promotionPiece));
        }

        else if (board.getPiece(endPosition) != null && col == 0) { // if there is a piece in front
            return chessMoves;
        }
        else if (board.getPiece(endPosition) != null && col != 0 && !board.getPiece(endPosition).color.equals(getTeamColor())) { // if there is a diagonal piece on the other team
            chessMoves.add(new ChessMove(myPosition, endPosition, promotionPiece));
        }
        return chessMoves;
    }

    public HashSet<ChessMove> kingKnightMoves(ChessPosition myPosition, ChessBoard board, HashSet<ChessMove> chessMoves, int row, int col) {
        ChessPosition endPosition;
        // check if in bounds
        if (myPosition.getRow() + row < 9 && myPosition.getRow() + row > 0 && myPosition.getColumn() + col > 0 && myPosition.getColumn() + col < 9) {
            endPosition = new ChessPosition(myPosition.getRow() + row, myPosition.getColumn() + col);
            if (board.getPiece(endPosition) == null) // empty space
                chessMoves.add(new ChessMove(myPosition, endPosition, null));
            if (board.getPiece(endPosition) != null) { // if there is a piece
                if (board.getPiece(endPosition).color.equals(getTeamColor())) // the piece is our color
                    return chessMoves;
                else {
                    chessMoves.add(new ChessMove(myPosition, endPosition, null));
                }
            }
        }
        return chessMoves;
    }
    public HashSet<ChessMove> bishopMoves(ChessPosition myPosition, ChessBoard board, HashSet<ChessMove> chessMoves, int rowDirection, int colDirection) {
        ChessPosition endPosition;
        boolean condition = false;
        for (int i = 1; i < 9; i++) {
            // find the board limit conditions
            if(rowDirection > 0 && colDirection > 0)
                condition = myPosition.getRow() + i < 9 && myPosition.getColumn() + i < 9;
            else if(rowDirection > 0 && colDirection < 0)
                condition = myPosition.getRow() + i < 9 && myPosition.getColumn() - i > 0;
            else if(rowDirection < 0 && colDirection > 0)
                condition = myPosition.getRow() - i > 0 && myPosition.getColumn() + i < 9;
            else
                condition = myPosition.getRow() - i > 0 && myPosition.getColumn() - i > 0;
            if (condition) {
                endPosition = new ChessPosition(myPosition.getRow()+(i*rowDirection), myPosition.getColumn()+(i*colDirection));

                if(board.getPiece(endPosition) == null) // empty space
                    chessMoves.add(new ChessMove(myPosition, endPosition, null));
                if(board.getPiece(endPosition) != null ) { // if there is a piece
                    if (board.getPiece(endPosition).color.equals(getTeamColor())) // the piece is our color
                        break;
                    else {
                        chessMoves.add(new ChessMove(myPosition, endPosition, null));
                        break;
                    }
                }
            }
        }
        return chessMoves;
    }

    public HashSet<ChessMove> rookMoves(ChessPosition myPosition, ChessBoard board, HashSet<ChessMove> chessMoves, int rowDirection, int colDirection) {
        ChessPosition endPosition;
        boolean condition = false;
        for (int i = 1; i < 9; i++) {
            // find the board limit conditions
            if(rowDirection == 1 && colDirection == 0)
                condition = myPosition.getRow() + i < 9;
            else if(rowDirection == -1 && colDirection == 0)
                condition = myPosition.getRow() - i > 0;
            else if(colDirection == 1)
                condition = myPosition.getColumn() + i < 9;
            else
                condition = myPosition.getColumn() - i > 0;
            if (condition) {
                endPosition = new ChessPosition(myPosition.getRow()+(i*rowDirection), myPosition.getColumn()+(i*colDirection));

                if(board.getPiece(endPosition) == null) // empty space
                    chessMoves.add(new ChessMove(myPosition, endPosition, null));

                if(board.getPiece(endPosition) != null ) { // if there is a piece
                    if (board.getPiece(endPosition).color.equals(getTeamColor())) // the piece is our color
                        break;
                    else {
                        chessMoves.add(new ChessMove(myPosition, endPosition, null));
                        break;
                    }
                }
            }
        }
        return chessMoves;
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> chessMoves= new HashSet<>();

        if (getPieceType() == PieceType.BISHOP || getPieceType() == PieceType.QUEEN) {
            chessMoves = bishopMoves(myPosition, board, chessMoves,1,1);
            chessMoves = bishopMoves(myPosition, board, chessMoves,-1,1);
            chessMoves = bishopMoves(myPosition, board, chessMoves,-1,-1);
            chessMoves = bishopMoves(myPosition, board, chessMoves,1,-1);
        }
        else if (getPieceType() == PieceType.KING) {
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,1, 1);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,1, -1);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,1, 0);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,0, 1);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,0, -1);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,-1, 1);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,-1, -1);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,-1, 0);
        }
        else if (getPieceType() == PieceType.KNIGHT) {
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,2, 1);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,2, -1);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,1, 2);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,1, -2);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,-1, 2);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,-1, -2);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,-2, 1);
            chessMoves = kingKnightMoves(myPosition, board, chessMoves,-2, -1);
        }
        else if (getPieceType() == PieceType.PAWN) {
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, 0, PieceType.QUEEN);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, 1, PieceType.QUEEN);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, -1, PieceType.QUEEN);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, 1, PieceType.BISHOP);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, 0, PieceType.BISHOP);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, -1, PieceType.BISHOP);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, 0, PieceType.KNIGHT);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, 1, PieceType.KNIGHT);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, -1, PieceType.KNIGHT);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, 0, PieceType.ROOK);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, 1, PieceType.ROOK);
            chessMoves = pawnMoves(myPosition, board, chessMoves,1, -1, PieceType.ROOK);
            chessMoves = pawnMoves(myPosition, board, chessMoves, 2, 0, null);
        }
        if (getPieceType() == PieceType.ROOK || getPieceType() == PieceType.QUEEN) {
            chessMoves = rookMoves(myPosition, board, chessMoves,1,0);
            chessMoves = rookMoves(myPosition, board, chessMoves,-1,0);
            chessMoves = rookMoves(myPosition, board, chessMoves,0,1);
            chessMoves = rookMoves(myPosition, board, chessMoves,0,-1);
        }
        return chessMoves;
    }
}
