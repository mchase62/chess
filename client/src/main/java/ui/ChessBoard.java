package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class ChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";
    private static Random rand = new Random();


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String teamColor;

        out.print(ERASE_SCREEN);

        // draw white's board
        teamColor = "WHITE";
        drawHeaders(out, teamColor);

        drawTicTacToeBoard(out, teamColor);

        drawHeaders(out, teamColor);

        out.println();
        // draw black's board
        teamColor = "BLACK";
        drawHeaders(out, teamColor);

        drawTicTacToeBoard(out, teamColor);

        drawHeaders(out, teamColor);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, String teamColor) {
        String[] white_headers = { "h", "g", "f", "e", "d", "c", "b", "a" };
        String[] black_headers = { "a", "b", "c", "d", "e", "f", "g", "h" };
        String[] headers;
        setBlack(out);
        if (teamColor.equals("WHITE"))
            headers = white_headers;
        else
            headers = black_headers;

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

    private static void drawTicTacToeBoard(PrintStream out, String teamColor) {

        String[] sides = { "1", "2", "3", "4", "5", "6", "7", "8" };
        if (teamColor.equals("WHITE")) {
            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) { // loop throw each row and draw squares

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + sides[boardRow] + " ");

                drawRowOfSquares(out, boardRow);

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + sides[boardRow] + " ");

                setBlack(out);
                out.println();
            }
        }
        else {
            for (int boardRow = BOARD_SIZE_IN_SQUARES - 1; boardRow >= 0 ; --boardRow) {

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + sides[boardRow] + " ");

                drawRowOfSquares(out, boardRow);

                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + sides[boardRow] + " ");

                setBlack(out);
                out.println();
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) { // size is 1
            if (boardRow % 2 != 0)
                setWhite(out);
            else
                setBlack(out);
            if (boardCol % 2 == 0) { // every other column
                if (boardRow % 2 != 0)
                    setBlack(out);
                else
                    setWhite(out);
                out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));
            } else {
                out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));
            }
            setBlack(out);
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }
}
