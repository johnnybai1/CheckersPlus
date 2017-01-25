public class MoveList {

    MoveNode head;

    public MoveList() {
        head = null;
    }

    public void insertFront(int x, int y) {
        head = new MoveNode(x, y, head);
    }

    public void clear() {
        head = null;
    }

    public int getX() {
        return head.x;
    }

    public int getY() {
        return head.y;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        MoveNode temp = head;
        while (temp.next != null) {
            sb.append(temp);
            sb.append(" ");
            temp = temp.next;
        }
        sb.append(temp);
        sb.append("}");
        return sb.toString();
    }


    class MoveNode {

        MoveNode next;
        int x;
        int y;

        public MoveNode(int x, int y) {
            this(x, y, null);
        }

        public MoveNode(int x, int y, MoveNode n) {
            this.x = x;
            this.y = y;
            next = n;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("(");
            sb.append(x);
            sb.append(",");
            sb.append(y);
            sb.append(")");
            return sb.toString();
        }

    }

}
