import org.junit.Test;
import static org.junit.Assert.*;

public class Piece {

    boolean isFire, isKing, hasCaptured;
    Board b;
    int x, y;
    String type;
    String imgFile;

    public Piece(boolean isFire, Board b, int x, int y, String type) {
        isKing = false;
        hasCaptured = false;
        this.isFire = isFire;
        this.b = b;
        this.x = x;
        this.y = y;
        this.type = type;
        imgFile = getImgFile();
    }

    protected String getImgFile() {
        StringBuilder sb = new StringBuilder();
        sb.append("img/");
        sb.append(type);
        if (isFire) {
            sb.append("-fire");
        }
        else sb.append("-water");
        if (isKing) {
            sb.append("-crowned");
        }
        sb.append(".png");
        return sb.toString();
    }

    public boolean isFire() {
        return isFire;
    }

    public int side() {
        if (isFire) {
            return 0;
        }
        else return 1;
    }

    public boolean isKing() {
        return isKing;
    }

    public boolean isBomb() {
        return type == "bomb";
    }

    public boolean isShield() {
        return type == "shield";
    }

    public void move(int x, int y) {
        if (Math.abs(this.x - x) == 2) {
            hasCaptured = true;
        }
        b.place(b.remove(this.x, this.y), x, y);
        if (hasCaptured()) {
            b.remove((this.x + x) / 2, (this.y + y) / 2); // jumped piece
            if (isBomb()) {
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        Piece removed = b.pieceAt(x + i, y + j);
                        if (removed != null && !removed.isShield()) {
                            b.remove(x + i, y + j);
                        }
                    }
                }
            }
            doneCapturing();
        }
        b.selected = b.pieceAt(x, y);
        b.hasMoved = true;
    }

    public boolean hasCaptured() {
        return hasCaptured;
    }

    public void doneCapturing() {
        hasCaptured = false;
    }

    public static class PieceTest {
        @Test
        public void testPieceConstructor() {
            Board b = new Board(false);
            Piece p = new Piece(true, b, 0, 0, "pawn");
            assertTrue(p.isFire());
            assertFalse(p.isKing());
            assertFalse(p.isBomb());
            assertFalse(p.isShield());
            assertEquals(0, p.side());
        }
    }

    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(PieceTest.class);

    }

}
