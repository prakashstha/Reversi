# Project: Reversi

The goal of this class project was to give us some experience designing strategies for a competitive game. It assumes you have already familiarized yourself with Git and Maven.

## Implementing our part of the code

Our task was to design a strategy for playing [Reversi](http://en.wikipedia.org/wiki/Reversi). The [Wikipedia article](http://en.wikipedia.org/wiki/Reversi) gives a clear description of the rules. You can try playing a few times in [a version of the game online](http://briannickel.brinkster.net/html5-reversi/).

We created a new implementation of `edu.uab.cis.reversi.Strategy`. The class should be named edu.uab.cis.reversi.strategy.group<n>.Group<n>Strategy and should be placed in a corresponding subfolder under the `src/main/java` directory. The group number, `<n>` will be assigned by your instructor.

Our `Strategy` had to inspect the `edu.uab.cis.reversi.Board` that it is given, and decide upon a `Square` where we want to place a piece. Take a look at the documentation for the `Strategy` class, as well as the API of the `Board` class for details.

## Test the code

No tests were provided in this project. Instead, we had to compare our strategy to some baseline strategies. One example baseline strategy was -- `edu.uab.cis.reversi.strategy.baseline.RandomStrategy`.

To test whether the strategy outperforms `RandomStrategy`, you can run `edu.uab.cis.reversi.Reversi`. See that class for the full set of options, but if you run something like:

    java edu.uab.cis.reversi.Reversi --strategies \
    edu.uab.cis.reversi.strategy.baseline.RandomStrategy \
    edu.uab.cis.reversi.strategy.group<n>.Group<n>Strategy

Then the class will have the strategies play several games against each other, and then print out the number of wins for each strategy. You can run the class with more than 2 strategies to see how your strategy would do in a round-robin style tournament.

