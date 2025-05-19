# CodinGame Spring Challenge 2025
This is my entry for [CodinGame Spring Challenge 2025](https://www.codingame.com/contests/spring-challenge-2025). The competition is over but you can still try the puzzle [here](https://www.codingame.com/ide/726597168f85bf90c70bbbe11dd33c039f5b8f41).

This challenge was an optimisation challenge, scoring was based on how quickly the submitted code could solve the suite of tests.

I only managed to implement a couple of optimisations in time, but it was enough to pass all the tests without hitting the time limit, and to place 980th out of 2,864 entries.

## Solution
The solution is a basically a brute force depth first search over all of the possible board states. The search evaluates every single possible move from each board state recursively.

## Optimisations
- Hardcode adjacent cell lookup table
- Memoize results for each board state
- Pack boards state and search depth into a single long