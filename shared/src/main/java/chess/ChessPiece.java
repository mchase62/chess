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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> chessMoves= new ArrayList<>();
        ChessPosition endPosition;
        if (getPieceType() == PieceType.BISHOP) {
            //ChessMove(ChessPosition startPosition, ChessPosition endPosition,ChessPiece.PieceType promotionPiece)
            for (int i = 1; i < 9; i++) {
                if (myPosition.getRow() + i < 9 && myPosition.getColumn() + i < 9) {
                    endPosition = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn()+i);

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
            for (int i = 1; i < 9; i++) {
                if (myPosition.getRow() - i > 0 && myPosition.getColumn() + i < 9) {
                    endPosition = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn()+i);
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
            for (int i = 1; i < 9; i++) {
                if (myPosition.getRow() - i > 0 && myPosition.getColumn() - i > 0) {
                    endPosition = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn()-i);
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
            for (int i = 1; i < 9; i++) {
                if (myPosition.getRow() + i < 9 && myPosition.getColumn() - i > 0) {
                    endPosition = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn()-i);
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

        }
        for (ChessMove move: chessMoves) {
            System.out.println(move.getEndPosition().getRow() + " " + move.getEndPosition().getColumn());
        }
        return chessMoves;
    }
}
