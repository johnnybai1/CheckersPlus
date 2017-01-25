import org.junit.Test;
import static org.junit.Assert.*;

public class Board {

    Piece[][] pieces;
    Piece selected;
    boolean isFireTurn, hasMoved;
    int firePieces, waterPieces;
    int movedX, movedY;
    public static final int ROWS = 8;
    public static final int COLS = 8;

    public static void main(String[] args) {
        //jh61b.junit.textui.runClasses(BoardTest.class);
        Board game = new Board(false);
        game.drawBoard();
        while(game.winner() == null) {
            if (StdDrawPlus.mousePressed()) {
                int x = (int) StdDrawPlus.mouseX();
                int y = (int) StdDrawPlus.mouseY();
                if (game.canSelect(x, y)) {
                    game.select(x, y);
                }
            }
            if (game.canEndTurn() && StdDrawPlus.isSpacePressed()) {
                game.endTurn();
            }
            StdDrawPlus.show(100);
        }
        System.out.println(game.winner());
    }

    public Board(boolean shouldBeEmpty) {
        pieces = new Piece[COLS][ROWS];
        isFireTurn = true;
        selected = null;
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                if (!shouldBeEmpty) {
                    firePieces = 12;
                    waterPieces = 12;
                    if (x % 2 == 0) {
                        if (y == 0) {
                            pieces[x][y] = new Piece(true, this, x, y, "pawn");
                        }
                        else if (y == 2) {
                            pieces[x][y] = new Piece(true, this, x, y, "bomb");
                        }
                        else if (y == 6) {
                            pieces[x][y] = new Piece(false, this, x, y, "shield");
                        }
                    }
                    else if (x % 2 == 1) {
                        if (y == 1) {
                            pieces[x][y] = new Piece(true, this, x, y, "shield");
                        }
                        else if (y == 5) {
                            pieces[x][y] = new Piece(false, this, x, y, "bomb");
                        }
                        else if (y == 7) {
                            pieces[x][y] = new Piece(false, this, x, y, "pawn");
                        }
                    }
                }
            }
        }
    }

    private void drawBoard() {
        StdDrawPlus.setXscale(0, COLS);
        StdDrawPlus.setYscale(0, ROWS);
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                drawSquare(x, y);
            }
        }
    }

    private void drawSquare(int x, int y) {
        if ((x + y) % 2 == 0) {
            StdDrawPlus.setPenColor(StdDrawPlus.GRAY);
        }
        else StdDrawPlus.setPenColor(StdDrawPlus.RED);
        StdDrawPlus.filledSquare(x + .5, y + .5, .5);
        if (pieceAt(x, y) != null) {
            StdDrawPlus.picture(x + .5, y + .5, pieceAt(x, y).imgFile, 1, 1);
        }
    }

    private MoveList getValidMoves() {
        MoveList validMoves = new MoveList();
        if (selected == null) {
            return null;
        }
        int x = selected.x;
        int y = selected.y;
        if (selected.isFire() || (!selected.isFire() && selected.isKing())) {
            if (pieceAt(x - 1, y + 1) == null) {
                validMoves.insertFront(x - 1, y + 1);
            }
            else if (pieceAt(x - 1, y + 1).isFire() != selected.isFire() && pieceAt(x - 2, y + 2) == null) {
                validMoves.insertFront(x - 2, y + 2);
            }
            if (pieceAt(x + 1, y + 1) == null) {
                validMoves.insertFront(x + 1, y + 1);
            }
            else if (pieceAt(x + 1, y + 1).isFire() != selected.isFire() && pieceAt(x + 2, y + 2) == null) {
                validMoves.insertFront(x + 2, y + 2);
            }
        }
        if (!selected.isFire() || (selected.isFire() && selected.isKing())) {
            if (pieceAt(x - 1, y - 1) == null) {
                validMoves.insertFront(x - 1, y - 1);
            }
            else if (pieceAt(x - 1, y - 1).isFire() != selected.isFire() && pieceAt(x - 2, y - 2) == null) {
                validMoves.insertFront(x - 2, y - 2);
            }
            if (pieceAt(x + 1, y - 1) == null) {
                validMoves.insertFront(x + 1, y - 1);
            }
            else if (pieceAt(x + 1, y - 1).isFire() != selected.isFire() && pieceAt(x + 2, y - 2) == null) {
                validMoves.insertFront(x + 2, y - 2);
            }
        }
        return validMoves;
    }

    public Piece pieceAt(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            return null;
        }
        return pieces[x][y];
    }

    public boolean canSelect(int x, int y) {
        Piece test = pieceAt(x, y);
        // Can't select red tiles
        if ((x + y) % 2 == 1) {
            return false;
        }
        // If player hasn't made a move yet
        else if (!hasMoved) {
            // Allow player to select his own tiles
            if (test != null) {
                return test.isFire() == isFireTurn;
            }
            // If player wants to select an empty tile..
            else if (selected != null && test == null) {
                return isValidMove(x, y);
            }
        }
        else if (hasMoved && selected != null) {
            return isValidMove(x, y);
        }
        return false;
    }

    private boolean isValidMove(int x, int y) {
        MoveList validMoves = getValidMoves();
        while (validMoves.head != null) {
            if (validMoves.getX() == x && validMoves.getY() == y) {
                return true;
            }
            else validMoves.head = validMoves.head.next;
        }
        return false;
    }


    public void select(int x, int y) {
        if (!hasMoved && selected != null) {
            drawSquare(selected.x, selected.y);
            if (pieceAt(x, y) == null && isValidMove(x, y)) {
                selected.move(x, y);
                movedX = x;
                movedY = y;
            }
        }
        if (pieceAt(x, y) != null) {
            selected = pieceAt(x, y);
            StdDrawPlus.setPenColor(StdDrawPlus.WHITE);
            StdDrawPlus.filledSquare(x + .5, y + .5, .5);
            StdDrawPlus.picture(x + .5, y + .5, selected.imgFile, 1, 1);
        }
        if (hasMoved && selected != null) {
            if (pieceAt(x, y) == null && isValidMove(x, y)) {
                selected.move(x,y);
            }
        }
    }

    public void place(Piece p, int x, int y) {
        if ((p.isFire() && y == 7) || (!p.isFire() && y == 0) || p.isKing()) {
            p.isKing = true;
            p.imgFile = p.getImgFile();
        }
        pieces[x][y] = p;
        p.x = x;
        p.y = y;
        drawSquare(x, y);
        if (p.isFire()) {
            firePieces ++;
        }
        else waterPieces ++;
    }

    public Piece remove(int x, int y) {
        Piece removed = pieceAt(x, y);
        removed = new Piece(removed.isFire, this, removed.x, removed.y, removed.type);
        if (pieceAt(x, y).isKing()) {
            removed.isKing = true;
            removed.imgFile = removed.getImgFile();
        }
        pieces[x][y] = null;
        drawSquare(x, y);
        if (removed.isFire()) {
            firePieces --;
        }
        else waterPieces --;
        return removed;
    }

    public boolean canEndTurn() {
        return hasMoved;
    }

    public void endTurn() {
        drawSquare(movedX, movedY);
        selected = null;
        hasMoved = false;
        isFireTurn = !isFireTurn;
    }


    public String winner() {
        if (waterPieces == 0 && firePieces == 0) {
            return "No one";
        }
        if (waterPieces == 0) {
            return "Fire";
        }
        if (firePieces == 0) {
            return "Water";
        }
        else return null;
    }

    public static class BoardTest {
        @Test
        public void testCanSelect() {
            Board test = new Board(true);
            test.pieces[3][3] = new Piece(true, test, 3, 3, "pawn");
            test.pieces[3][3].isKing = true;
            test.selected = test.pieceAt(3, 3);
            //this king piece should be able to select an empty tile in all 4 directions
            assertTrue(test.selected.isKing());
            assertTrue(test.canSelect(2, 2));
            assertTrue(test.canSelect(4, 2));
            assertTrue(test.canSelect(4, 4));
            assertTrue(test.canSelect(2, 4));
        }

        @Test
        public void testValidMoves() {
            Board test = new Board(true);
            test.pieces[3][3] = new Piece(true, test, 3, 3, "pawn");
            test.pieces[3][3].isKing = true;
            test.selected = test.pieceAt(3, 3);
            test.pieces[4][4] = new Piece(false, test, 4, 4, "pawn");
            MoveList validMoves = test.getValidMoves();
            System.out.println(validMoves);
            assertTrue(test.isValidMove(2, 2));
            assertTrue(test.isValidMove(4, 2));
            assertTrue(test.isValidMove(5, 5));
            assertTrue(test.isValidMove(2, 4));

            assertTrue(test.canSelect(2, 2));
            assertTrue(test.canSelect(4, 2));
            assertTrue(test.canSelect(5, 5));
            assertTrue(test.canSelect(2, 4));
        }

        @Test
        public void testPlaceRemove() {
            Board test = new Board(true);
            Piece p1 = new Piece(true, test, 0, 0, "pawn");
            Piece p2 = new Piece(true, test, 2, 2, "pawn");
            Piece p3 = new Piece(false, test, 3, 3, "pawn");
            test.place(p1, 0, 0);
            test.place(p2, 0, 0);
        }
    }

}
