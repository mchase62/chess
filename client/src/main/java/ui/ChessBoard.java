package ui;

import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class ChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";
    private static Random rand = new Random();


    public static void main(String[] args) {
        chess.ChessBoard chessBoard = new chess.ChessBoard();
        chessBoard.resetBoard();

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String teamColor = args[0];

        out.print(ERASE_SCREEN);

        // draw white's board

        drawHeaders(out, teamColor);

        drawChessBoard(out, teamColor, chessBoard);

        drawHeaders(out, teamColor);

        out.println();
//        // draw black's board
//        teamColor = "BLACK";
//        drawHeaders(out, teamColor);
//
//        drawChessBoard(out, teamColor, chessBoard);
//
//        drawHeaders(out, teamColor);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public static void drawBoard(String teamColor, chess.ChessBoard board) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        drawHeaders(out, teamColor);

        drawChessBoard(out, teamColor, board);

        drawHeaders(out, teamColor);
    }

    private static void drawHeaders(PrintStream out, String teamColor) {
        String[] whiteHeaders = { "h", "g", "f", "e", "d", "c", "b", "a" };
        String[] blackHeaders = { "a", "b", "c", "d", "e", "f", "g", "h" };
        String[] headers;
        setBlack(out);
        if (teamColor.equals("WHITE"))
            headers = whiteHeaders;
        else
            headers = blackHeaders;

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print("   ");

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, " " + headers[boardCol] + " ");
        }

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print("   ");

        setBlack(out);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText); // prints out the text
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setBlack(out);
    }

    public static void drawChessBoard(PrintStream out, String teamColor, chess.ChessBoard board) {
        String[] sides = { "1", "2", "3", "4", "5", "6", "7", "8" };

        if (teamColor.equals("WHITE")) {
            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) { // loop throw each row and draw squares
                drawSquares(boardRow, sides, out, board);
            }
        }
        else {
            for (int boardRow = BOARD_SIZE_IN_SQUARES - 1; boardRow >= 0 ; --boardRow) {
                drawSquares(boardRow, sides, out, board);
            }
        }
    }

    private static void drawSquares(int boardRow, String[] sides, PrintStream out, chess.ChessBoard board) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(" " + sides[boardRow] + " ");

        drawRowOfSquares(out, boardRow, board);

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(" " + sides[boardRow] + " ");

        setBlack(out);
        out.println();
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow, chess.ChessBoard board) {
        ChessPosition position;

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) { // size is 1
            position = new ChessPosition(boardRow + 1, boardCol + 1);
            if (boardRow % 2 != 0)
                out.print(SET_BG_COLOR_WHITE);
            else
                out.print(SET_BG_COLOR_BLACK);
            if (boardCol % 2 == 0) { // every other column
                if (boardRow % 2 != 0)
                    out.print(SET_BG_COLOR_BLACK);
                else
                    out.print(SET_BG_COLOR_WHITE);

//                out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));
                printCharacter(out, position, board);
            } else {
//                out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));
                printCharacter(out, position, board);
            }
            setBlack(out);
        }
    }

    private static void printCharacter(PrintStream out, ChessPosition position, chess.ChessBoard board) {
        ChessPiece piece = board.getPiece(position);
        if(piece != null) {
            String color = piece.getTeamColor().toString();
            if(color.equals("WHITE"))
                out.print(SET_TEXT_COLOR_RED);
            else
                out.print(SET_TEXT_COLOR_BLUE);
        }
        if (piece == null)
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));
        else if (piece.getPieceType().equals(ChessPiece.PieceType.KING))
            out.print(" K ");
        else if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN))
            out.print(" Q ");
        else if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP))
            out.print(" B ");
        else if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT))
            out.print(" N ");
        else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK))
            out.print(" R ");
        else if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN))
            out.print(" P ");
    }
    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
