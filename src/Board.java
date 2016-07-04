
/**
 * Models a board in the 8-puzzle game or its generalization.
 */
import java.util.ArrayList;

public class Board {

    //holds the current board situation
    private int[] tiles;
    // Blank position in row-major 
    private int blank = -1;
    // Row length
    private int N = 0;
    // number of tiles out of place.
    private int outOfPlace = 0;
    // Manhattan sum
    private int manny = 0;
    // blank row number
    private int blankrow;

    /**
     * Constructs a board from an N-by-N array of tiles, where tiles[i][j] =
     * tile at row i and column j, and 0 represents the blank square.
     *
     * @param tiles N-by-N array of tiles.
     */
    public Board(int[][] tiles) {
        // I'm just going to do everything in the creation.
        this.N = tiles.length;        
        this.tiles = new int[N * N];
        int count = 0;
        for (int i = 0; i < N; i++) { // If I have time I can come back to this nested loop
            for (int j = 0; j < N; j++) { // And turn it into one loop using modulous
                this.tiles[count++] = tiles[i][j];
                if (tiles[i][j] == 0) {
                    blankrow = i;
                    blank = (i * this.N) + j; // Because we are always one step ahead after count++                    
                } else if (tiles[i][j] != 0 && tiles[i][j] != (count)) { // No blank spot so this math works out. We don't count 0.
                    outOfPlace += 1;

                    int val = tiles[i][j];
                    int col = (val - 1) % N;
                    int row = ((val - 1) / N);
                    int sum = Math.abs(i - row) + Math.abs(j - col);
                    manny += sum; // So glad I thought of this one haha

                }
            }
        }
    }

    /**
     * Returns tile at row i and column j.
     *
     * @param i row number.
     * @param j column number.
     * @return tile at row i and column j.
     */
    public int tileAt(int i, int j) {
        int converted = i * N + j;
        if (converted >= this.tiles.length) {
            throw new java.lang.ArrayIndexOutOfBoundsException("Index out of bounds error");
        }
        return this.tiles[converted];

    }

    /**
     * Returns the size of this board.
     *
     * @return the size of this board.
     */
    public int size() {
        if (this.tiles == null) {
            throw new java.lang.NullPointerException("Class is null");
        }
        return N * N;
    }

    /**
     * Returns the number of tiles out of place.
     *
     * @return the number of tiles out of place.
     */
    public int hamming() {
        return outOfPlace;
    }

    /**
     * Returns the sum of Manhattan distances between tiles and goal.
     *
     * @return the sum of Manhattan distances between tiles and goal.
     */
    public int manhattan() {
        return manny;

    }

    /**
     * Returns true if this board is the goal board, and false otherwise.
     *
     * @return true if this board is the goal board, and false otherwise.
     */
    public boolean isGoal() {
        return (outOfPlace == 0); // If nothing is out of place, we win.
    }

    /**
     * Returns true if this board is solvable, and false otherwise.
     *
     * @return true if this board is solvable, and false otherwise.
     */
    public boolean isSolvable() {
        int inversions = this.inversions();
        if (N % 2 != 0 && inversions % 2 == 0) {
            return true;
        }
        if (N % 2 == 0 && (inversions + blankrow) % 2 != 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if this board is the same as that, and false otherwise.
     *
     * @param that board to compare with.
     * @return true if this board is the same as that, and false otherwise.
     */
    public boolean equals(Board that) {
        if (this.size() == that.size() && this.hamming() == that.hamming()) {
            if (this.manhattan() == that.manhattan()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return all neighboring boards.
     *
     * @return all neighboring boards.
     */
    public Iterable<Board> neighbors() {
        int[][] bizarro = this.cloneTiles();
        Board Bizarro;
        int zero = this.blank;
        int row = this.blankrow;
        int col = zero - (row * this.N);
        int holder;
        ArrayList<Board> results = new ArrayList<Board>();

        if (row + 1 < N) {
            bizarro = this.cloneTiles();

            holder = bizarro[row + 1][col];
            bizarro[row + 1][col] = bizarro[row][col];
            bizarro[row][col] = holder;
            Bizarro = new Board(bizarro);
            results.add(Bizarro);
        }
        if (col + 1 < N) {
            bizarro = this.cloneTiles();

            holder = bizarro[row][col + 1];
            bizarro[row][col + 1] = bizarro[row][col];
            bizarro[row][col] = holder;
            Bizarro = new Board(bizarro);
            results.add(Bizarro);
        }
        if (row - 1 >= 0) {
            bizarro = this.cloneTiles();

            holder = bizarro[row - 1][col];
            bizarro[row - 1][col] = bizarro[row][col];
            bizarro[row][col] = holder;
            Bizarro = new Board(bizarro);
            results.add(Bizarro);
        }
        if (col - 1 >= 0) {
            bizarro = this.cloneTiles();

            holder = bizarro[row][col - 1];
            bizarro[row][col - 1] = bizarro[row][col];
            bizarro[row][col] = holder;
            Bizarro = new Board(bizarro);
            results.add(Bizarro);
        }
        return results;

    }

    /**
     * Returns a string representation of this board.
     *
     * @return a string representation of this board.
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                str.append(String.format("%2d ", this.tileAt(i, j)));
            }
            str.append("\n");
        }
        return str.toString();

    }

    // Helper method that returns the position (in row-major order) of the 
    // blank (zero) tile.
    private int blankPos() {
        return blank;
    }

    // Helper method that returns the number of inversions.
    private int inversions() {
        int inverz = 0;
        int max = this.N;

        for (int i = 0; i < max; i++) {
            for (int j = i + 1; j < max; j++) {
                if (this.tiles[j] < this.tiles[i]) {
                    inverz++;
                }
            }
        }
        return inverz;

    }

    // Helper method that clones the tiles[][] array in this board and 
    // returns it.
    private int[][] cloneTiles() {
        int[][] clone = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                clone[i][j] = this.tileAt(i, j);
            }
        }
        return clone;
    }

    /**
     * Test client. [DO NOT EDIT]
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board board = new Board(tiles);
        StdOut.println(board.hamming());
        StdOut.println(board.manhattan());
        StdOut.println(board.isGoal());
        StdOut.println(board.isSolvable());
        for (Board neighbor : board.neighbors()) {
            StdOut.println(neighbor);
        }
    }
}
