package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
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

    /**
     *
     * @param newType for a promoted pawn
     */
    public void setPieceType(PieceType newType) {
        this.type = newType;
    }

    public ArrayList<ChessMove> pawnMoves(ChessPosition myPosition, ChessBoard board, ArrayList<ChessMove> chessMoves, int row, int col, PieceType promotionPiece) {
        ChessPosition endPosition;
        if ((myPosition.getRow() != 7 && getTeamColor() == ChessGame.TeamColor.WHITE) || (myPosition.getRow() != 2 && getTeamColor() == ChessGame.TeamColor.BLACK)) // if not the last row, can't promote
            promotionPiece = null;
        if (((myPosition.getRow() != 2 && getTeamColor() == ChessGame.TeamColor.WHITE) || (myPosition.getRow() != 7 && getTeamColor() == ChessGame.TeamColor.BLACK)) // if not initial spot
        || ((getTeamColor() == ChessGame.TeamColor.WHITE && board.getPiece(new ChessPosition(3, myPosition.getColumn()))!= null) || (getTeamColor() == ChessGame.TeamColor.BLACK && board.getPiece(new ChessPosition(6, myPosition.getColumn()))!= null))) // or there's a piece blocking the 2 square jump
            row = 1;
        if (getTeamColor() == ChessGame.TeamColor.BLACK) // change directions for black
            row *= -1;
        endPosition = new ChessPosition(myPosition.getRow() + row, myPosition.getColumn() + col);
        if (board.getPiece(endPosition) == null && col == 0) // empty space in front
            chessMoves.add(new ChessMove(myPosition, endPosition, promotionPiece));
        else if (board.getPiece(endPosition) != null && col == 0) { // if there is a piece in front
            return chessMoves;
        }
        else if (board.getPiece(endPosition) != null && col != 0 && !board.getPiece(endPosition).color.equals(getTeamColor())) // if there is a diagonal piece on the other team
            chessMoves.add(new ChessMove(myPosition, endPosition, promotionPiece));
        System.out.println(endPosition.getRow() + " " + endPosition.getColumn());
        return chessMoves;
    }

    public ArrayList<ChessMove> kingKnightMoves(ChessPosition myPosition, ChessBoard board, ArrayList<ChessMove> chessMoves, int row, int col) {
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
    public ArrayList<ChessMove> bishopMoves(ChessPosition myPosition, ChessBoard board, ArrayList<ChessMove> chessMoves, int rowDirection, int colDirection) {
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
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> chessMoves= new ArrayList<>();

        if (getPieceType() == PieceType.BISHOP) {
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
        return chessMoves;
    }
}
