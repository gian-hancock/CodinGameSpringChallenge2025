import java.io.InputStream;
import java.util.*;

class Player {
    final static boolean DEBUG = false;

    public static void main(String[] args) {
        var problemInput = ProblemInput.parse(System.in);

        if (DEBUG) {
            System.err.printf(
                    """
                            #Problem
                            Depth=%d
                            Board=%s
                            """, problemInput.maxDepth, problemInput.board.toString());
        }

        var result = Run(problemInput);
        System.out.println(result);
    }

    public static int Run(ProblemInput input) {
        return input.board.solve(input.maxDepth);
    }
}

class Board {
    public final int WIDTH = 3;
    public final int MOD = 1 << 30;

    private long state;

    public Board(int[] board, int depth) {
        assert board.length == 9;
        assert depth <= 1 << 6;

        var s = depth;

        // shift in board state
        for (var cell : board) {
            assert cell >= 0 && cell <= 6;
            s <<= 3;
            s += cell;
        }
        state = s;
    }

    public int hash() {
        // TODO: Make this more efficient?
        int result = 0;
        for (int row = 0; row < WIDTH; row++) {
            {
                for (int col = 0; col < WIDTH; col++) {
                    result *= 10;
                    result += get(row, col);
                }
            }
        }
        return result;
    }

    public int get(int row, int col) {
        int bitShift = getBitShift(row, col);
        return (int) (state >> bitShift) & 0b111;
    }

    public int get(Coordinate coord) {
        return get(coord.row(), coord.col());
    }

    public int get(int cell) {
        int bitShift = getBitShift(cell);
        return (int) (state >> bitShift) & 0b111;
    }

    public void set(int row, int col, int val) {
        // TODO: Is there a better way of doing this?
        int bit = getBitShift(row, col);
        long mask = (long) 0b111 << bit;
        // clear
        state &= ~mask;
        // set new value
        state |= (long) val << bit;
    }

    public void set(Coordinate coord, int value) {
        set(coord.row(), coord.col(), value);
    }

    public void set(int cell, int value) {
        set(cell / WIDTH, cell % WIDTH, value);
    }

    int getDepth() {
        return (int) (state >> 9 * 3) & 0b111_111_111;
    }

    void setDepth(int depth) {
        long mask = 0b111_111_111L << 9 * 3;

        // reset depth
        state &= ~mask;

        // set depth
        state += (long) depth << 9 * 3;
    }

    public int solve(int maxDepth) {
        return solveInner(maxDepth, new HashMap<>());
    }

    int solveInner(int maxDepth, HashMap<Long, Integer> memo) {
        // Diagnostic
        if (Player.DEBUG) {
            System.err.printf("solutionCountInner(depth=%d, maxDepth=%d)\nBoard:%s", getDepth(), maxDepth, this);
        }

        // Memoized result
        if (memo.containsKey(state)) {
            return memo.get(state);
        }

        // Base case
        if (getDepth() >= maxDepth) {
            if (Player.DEBUG) {
                System.err.printf("Board (MaxDepth): %s", this);
            }
            return memoizeResult(state, hash() % MOD, memo);
        }

        // Build adjacent coord lookup table
        // TODO: Optimisation idea: avoid heap allocation of Coordinate
        int[][] adjacentCoords = getAdjacentCellMap();

        // Simulate turn
        boolean isEnd = true;
        int result = 0;

        // For each cell, traverse board states that can be created by placing a dice there
        for (int cell = 0; cell < WIDTH * WIDTH; cell++) {
            int row = cell / 3;
            int col = cell % 3;

            // Only consider unoccupied squares. Can't place dice on occupied square
            if (get(row, col) != 0) {
                continue;
            }

            List<Integer> adjCells = Arrays.stream(adjacentCoords[cell])
                    .filter(coord -> get(coord) != 0)
                    .boxed()
                    .toList();
            // TODO: Only perform this if necessary
            // Get all combinations of adjacent coordinates with size >= 2 and sum <= 6
            List<List<Integer>> combinations =
                    Combinations.getCombinations(adjCells, 2)
                            .stream()
                            .filter(combo -> combo.stream().mapToLong(this::get).sum() <= 6)
                            .toList();

            // Try to perform capturing placement. If this fails, perform non-capturing instead.

            if (combinations.isEmpty()) {
                // Non capturing placement
                isEnd = false;

                // Store current state
                var prevState = state;

                // Update state
                set(row, col, 1);
                setDepth(getDepth() + 1);

                // Recurse
                int sol = solveInner(maxDepth, memo);
                result = (result + sol) % MOD;

                // Reset state
                state = prevState;

            } else {
                // Capturing placement:
                for (List<Integer> combo : combinations) {
                    // Store current state
                    long prevState = state;

                    // Recurse
                    set(row, col, combo.stream().mapToInt(this::get).sum());
                    setDepth(getDepth() + 1);
                    for (int adjCell : combo) {
                        set(adjCell, 0);
                    }
                    int sol = solveInner(maxDepth, memo);
                    result = (result + sol) % MOD;
                    isEnd = false;

                    // Restore state
                    state = prevState;
                }
            }
        }

        if (isEnd) {
            result = hash() % MOD;
            if (Player.DEBUG) {
                System.err.printf("Board (Terminal): %s", this);
            }
        }

        return memoizeResult(state, result, memo);
    }

    private int[][] getAdjacentCellMap() {
        /*
        0 1 2
        3 4 5
        6 7 8
        */
        return new int[][]{
                {1, 3},
                {0, 2, 4},
                {1, 5},
                {0, 4, 6},
                {1, 3, 5, 7},
                {2, 4, 8},
                {3, 7},
                {4, 6, 8},
                {5, 7},
        };
    }

    int memoizeResult(long state, int result, Map<Long, Integer> memo) {
        memo.put(state, result);
        return result;
    }

    Coordinate[] adjacentTo(int row, int col) {
        var result = new ArrayList<Coordinate>();
        Coordinate[] offsets = {new Coordinate(-1, 0),
                new Coordinate(1, 0),
                new Coordinate(0, -1),
                new Coordinate(0, 1),};
        for (var offset : offsets) {
            var adjCoordinate = new Coordinate(row + offset.row(), col + offset.col());
            boolean inBounds = adjCoordinate.row() >= 0
                    && adjCoordinate.row() < WIDTH
                    && adjCoordinate.col() >= 0
                    && adjCoordinate.col() < WIDTH;
            if (inBounds && get(adjCoordinate) != 0) {
                result.add(adjCoordinate);
            }
        }
        return result.toArray(Coordinate[]::new);
    }

    int getBitShift(int cellIdx) {
        int baseShift = 3 * 8; // amount to right shift to shift out all but the first cell
        return baseShift - cellIdx * 3;
    }

    int getBitShift(int row, int col) {
        return getBitShift(row * 3 + col);
    }

    @Override public String toString() {
        var result = new StringBuilder();
        for (int row = 0; row < WIDTH; row++) {
            result.append("\n");
            for (int col = 0; col < WIDTH; col++) {
                result.append(get(row, col));
                result.append(" ");
            }
        }
        result.append("\n");
        return result.toString();
    }
}

record Coordinate(int row, int col) {
}

class ProblemInput {
    public Board board;
    public int maxDepth;

    public ProblemInput(Board board, int maxDepth) {
        this.board = board;
        this.maxDepth = maxDepth;
    }

    /// Parse from stream:
    /// - First line: one integer depth for the max number of player moves to simulate.
    /// -  Next 3 lines: 3 die_value integers representing each space of one row of the board.
    ///     - die_value: the value of the die on this space or 0 if this space is empty
    public static ProblemInput parse(InputStream stream) {
        var scanner = new Scanner(stream);

        int maxDepth = scanner.nextInt();

        // TODO: Is there a streaming way to do this?
        int[] board = new int[9];
        for (int i = 0; i < 9; i++) {
            board[i] = scanner.nextInt();
        }

        return new ProblemInput(new Board(board, 0), maxDepth);
    }
}

class Combinations {

    public static int counter = 0;

    // TODO: Use a generator here?
    public static <T> List<List<T>> getCombinations(List<T> nums, int minSize) {
        List<List<T>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result, minSize);
        return result;
    }

    private static <T> void backtrack(List<T> nums, int start, List<T> path, List<List<T>> result, int minSize) {
        counter++;
        if (path.size() >= minSize) {
            result.add(new ArrayList<>(path));
        }

        for (int i = start; i < nums.size(); i++) {
            path.add(nums.get(i));
            backtrack(nums, i + 1, path, result, minSize);
            path.remove(path.size() - 1);
        }
    }
}