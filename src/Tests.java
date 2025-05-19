import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Tests {
    @Test
    void boardGet() {
        Board uut = new Board(
                new int[]{
                        0, 1, 2,
                        3, 4, 5,
                        6, 6, 6,
                },
                0);
        assertEquals(0, uut.get(0, 0));
        assertEquals(1, uut.get(0, 1));
        assertEquals(2, uut.get(0, 2));
        assertEquals(3, uut.get(1, 0));
        assertEquals(4, uut.get(1, 1));
        assertEquals(5, uut.get(1, 2));
        assertEquals(6, uut.get(2, 0));
        assertEquals(6, uut.get(2, 1));
        assertEquals(6, uut.get(2, 2));
    }

    @Test
    void boardSet() {
        Board uut = new Board(
                new int[]{
                        0, 0, 0,
                        0, 0, 0,
                        0, 0, 0,
                },
                0);
        uut.set(0, 0, 0);
        uut.set(0, 1, 1);
        uut.set(0, 2, 2);
        uut.set(1, 0, 3);
        uut.set(1, 1, 4);
        uut.set(1, 2, 5);
        uut.set(2, 0, 6);
        uut.set(2, 1, 0);
        uut.set(2, 2, 1);
        assertEquals(0, uut.get(0, 0));
        assertEquals(1, uut.get(0, 1));
        assertEquals(2, uut.get(0, 2));
        assertEquals(3, uut.get(1, 0));
        assertEquals(4, uut.get(1, 1));
        assertEquals(5, uut.get(1, 2));
        assertEquals(6, uut.get(2, 0));
        assertEquals(0, uut.get(2, 1));
        assertEquals(1, uut.get(2, 2));
    }

    @Test
    void boardGetSetDepth() {
        Board uut = new Board(
                new int[]{
                        0, 1, 2,
                        3, 4, 5,
                        6, 6, 6,
                },
                8);
        assertEquals(8, uut.getDepth());

        uut.setDepth(64);
        assertEquals(64, uut.getDepth());
    }

    @Test
    void adjacentToTestAllInBounds() {
        Board uut = new Board(
                new int[]{
                        1, 2, 3,
                        4, 5, 6,
                        0, 1, 2,
                },
                0);
        var result = uut.adjacentTo(1, 1);
        assertTrue(Arrays.stream(result).anyMatch(i -> i.equals(new Coordinate(0, 1))));
        assertTrue(Arrays.stream(result).anyMatch(i -> i.equals(new Coordinate(1, 0))));
        assertTrue(Arrays.stream(result).anyMatch(i -> i.equals(new Coordinate(1, 2))));
        assertTrue(Arrays.stream(result).anyMatch(i -> i.equals(new Coordinate(2, 1))));
        assertEquals(4, result.length);
    }

    @Test
    void adjacentToTestIgnoreZeroes() {
        Board uut = new Board(
                new int[]{
                        1, 0, 3,
                        0, 5, 0,
                        6, 0, 1,
                },
                0);
        var result = uut.adjacentTo(1, 1);
        assertEquals(0, result.length);
    }

    @Test
    void adjacentToTestOutOfBounds() {
        Board uut = new Board(
                new int[]{
                        1, 2, 3,
                        4, 5, 6,
                        0, 1, 2,
                }, 0);
        var result = uut.adjacentTo(0, 1);
        assertTrue(Arrays.stream(result).anyMatch(i -> i.equals(new Coordinate(0, 0))));
        assertTrue(Arrays.stream(result).anyMatch(i -> i.equals(new Coordinate(0, 2))));
        assertTrue(Arrays.stream(result).anyMatch(i -> i.equals(new Coordinate(1, 1))));
        assertEquals(3, result.length);
    }

    @Test
    void testRun2States() {
        Board board = new Board(
                new int[]{
                        0, 6, 0,
                        2, 2, 2,
                        1, 6, 1,
                },
                0);
        ProblemInput problemInput = new ProblemInput(board, 20);
        var result = Player.Run(problemInput);
        assertEquals(322444322, result);
    }

    @Test
    void testRun6States() {
        Board board = new Board(
                new int[]{
                        5, 0, 6,
                        4, 5, 0,
                        0, 6, 4,
                },
                0);
        ProblemInput problemInput = new ProblemInput(board, 20);
        var result = Player.Run(problemInput);
        assertEquals(951223336, result);
    }

    @Test
    void testRun2UniqueStates() {
        Board board = new Board(
                new int[]{
                        5, 5, 5,
                        0, 0, 5,
                        5, 5, 5,
                },
                0);
        ProblemInput problemInput = new ProblemInput(board, 1);
        var result = Player.Run(problemInput);
        assertEquals(36379286, result);
    }

    @Test
    void testRun11UniqueStates() {
        Board board = new Board(
                new int[]{
                        6, 1, 6,
                        1, 0, 1,
                        6, 1, 6,
                },
                0);
        ProblemInput problemInput = new ProblemInput(board, 1);
        var result = Player.Run(problemInput);
        assertEquals(264239762, result);
    }

    @Test
    void testRun241UniqueStates() {
        Board board = new Board(
                new int[]{
                        3, 0, 0,
                        3, 6, 2,
                        1, 0, 2,
                },
                0);
        ProblemInput problemInput = new ProblemInput(board, 24);
        var result = Player.Run(problemInput);
        assertEquals(661168294, result);
    }

    @Test
    void testRunLarge() {
        Board board = new Board(
                new int[]{
                        1, 0, 0,
                        3, 5, 2,
                        1, 0, 0,
                },
                0);
        ProblemInput problemInput = new ProblemInput(board, 20);
        var result = Player.Run(problemInput);
        assertEquals(808014757, result);
    }

    @Test
    void testGetCombinations() {
        List<Integer> input = List.of(1, 2, 3);
        List<List<Integer>> result = Combinations.getCombinations(input, 2);
        System.out.printf("Counter: %d", Combinations.counter);

        assertEquals(4, result.size());

        assertTrue(result.contains(List.of(1, 2)));
        assertTrue(result.contains(List.of(1, 3)));
        assertTrue(result.contains(List.of(2, 3)));
        assertTrue(result.contains(List.of(1, 2, 3)));
    }

    @Test
    void boardHash() {
        Board board = new Board(
                new int[]{
                        0, 6, 0,
                        2, 2, 2,
                        1, 6, 1,
                },
                0);
        assertEquals(60222161, board.hash());
    }
}