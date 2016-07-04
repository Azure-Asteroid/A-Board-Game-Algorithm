
import java.util.Comparator;

/**
 * A solver based on the A* algorithm for the 8-puzzle and its generalizations.
 */
public class Solver {

    private int moves = 0;
    private Board begin;
    private ManhattanOrder mannyHeu = new ManhattanOrder(); // Our heuristic comparator 
    private LinkedStack<SearchNode> traversed = new LinkedStack<SearchNode>();
    private HeapMinPQ<SearchNode> unexplored = new HeapMinPQ<SearchNode>(mannyHeu);
    private LinkedStack<Board> gpsPath = new LinkedStack<Board>(); // Holds the best path to goal.

    // Helper search node class
    private class SearchNode {

        SearchNode pastStep;
        int manhat; // our main heuristic
        int hamm;   // You could use this too...
        Board currentPath; // Where are we? On this path preceeded with forks in the road.
        int steps; // How many steps it took our adventurer to get here.
        boolean goal; // Is it the END of our journey?!?!?

        public SearchNode(Board board, int moves, SearchNode previous) {
            hamm = board.hamming() + moves;
            manhat = board.manhattan() + moves;            
            pastStep = previous;
            this.steps = moves;
            this.goal = board.isGoal(); 
            this.currentPath = board;
        }
    }

    /**
     * Finds a solution to the initial board (using the A* algorithm).
     *
     * @param initial initial board.
     */
    public Solver(Board initial) {
        if(initial == null)
            throw new java.lang.NullPointerException("Argument can not be a null board.");
        
        this.moves = 0;
        begin = initial;
        SearchNode start = new SearchNode(begin, this.moves, null);
        unexplored.insert(start);

        if (begin.isSolvable()) {
            
            while (!unexplored.isEmpty()) {                
                SearchNode currentArea = unexplored.delMin(); // Best path guess pop it off. 
                
                if(currentArea.goal){ // We did it! We won!!
                    for(SearchNode backTrack = currentArea; backTrack != null; backTrack = backTrack.pastStep){
                        gpsPath.push(backTrack.currentPath); // This is for unpacking later.
                    }
                    break; // Leave the loop. The game is over.
                }
                
                
                // Get all the possible paths and let MinPQ organize it for us.
                for(Board nextPath : currentArea.currentPath.neighbors()){
                    SearchNode newPath = new SearchNode(nextPath, moves, currentArea); // Current area is the parent of the nextPath
                    unexplored.insert(newPath);

                }
                traversed.push(currentArea); // Been there done that.
                moves += 1; // Alright time to voyage to the next best path. Move forward.
                
            } // End of while loop
            
        }else{           
            throw new java.lang.IllegalArgumentException("Board is not solvable. ");
        }

    }

    /**
     * Returns the minimum number of moves to solve the initial board.
     *
     * @return the minimum number of moves to solve the initial board.
     */
    public int moves() {
        return moves;
    }

    /**
     * Returns the sequence of boards in a shortest solution.
     *
     * @Return the sequence of boards in a shortest solution.
     */
    public Iterable<Board> solution() {
        return gpsPath;
    }

    /**
     * Returns a hamming priority function comparator.
     *
     * @return a hamming priority function comparator.
     */
    public static Comparator<SearchNode> hammingOrder() {
        return new HammingOrder();
    }

    // Helper hamming priority function comparator.
    private static class HammingOrder implements Comparator<SearchNode> {

        public int compare(SearchNode one, SearchNode two) {
            if (one == null || two == null) {
                throw new java.lang.NullPointerException("Arguments can not be null");
            }
            int oneHam = one.hamm;
            int twoHam = two.hamm;
            int oneG = one.steps;
            int twoG = two.steps;

            if (oneHam < twoHam) {
                return -1;
            }
            if (oneHam > twoHam) {
                return +1;
            }             
            if(oneHam == twoHam){
                return (oneG - twoG); // Test for best optimality
            }
            else 
                return 0; // Only here because IDE wants it to be happy.           
        }
    }

    /**
     * Returns a Manhattan priority function comparator.
     *
     * @return a Manhattan priority function comparator.
     */
    public static Comparator<SearchNode> manhattanOrder() {
        return new ManhattanOrder();
    }

    // Helper manhattan priority function comparator.
    private static class ManhattanOrder implements Comparator<SearchNode> {

        public int compare(SearchNode one, SearchNode two) {
            if (one == null || two == null) {
                throw new java.lang.NullPointerException("Arguments can not be null");
            }
            int oneManh = one.manhat;
            int twoManh = two.manhat;
            int oneG = one.steps;
            int twoG = two.steps;

            if (oneManh < twoManh) {
                return -1;
            }
            if (oneManh > twoManh) {
                return +1;
            }
            if(oneManh == twoManh) {
                return (oneG - twoG); // Both priorities are equal see which took less steps.
            }
            else 
                return 0; // ONly here to satisfy the IDE.
        }
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
        Board initial = new Board(tiles);
        if (initial.isSolvable()) {
            Solver solver = new Solver(initial);
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        } else {
            StdOut.println("Unsolvable puzzle");
        }
    }
}
