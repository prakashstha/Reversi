package edu.uab.cis.reversi.strategy.group10;

import java.util.Map;
import java.util.Set;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;

public class EvaluationFunction {

  public final static int CURRENT_MOBILITY = 0;
  public final static int POTENTIAL_MOBILITY = 1;

  private static int[][] preference;

  public EvaluationFunction() {
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        squares[i][j] = new Square(i, j);
      }
    }

    preference = new int[4][4];
    preference[0][0] = 99;
    preference[0][1] = -8;
    preference[0][2] = 8;
    preference[0][3] = 6;
    preference[1][1] = -24;
    preference[1][2] = -4;
    preference[1][3] = -3;
    preference[2][2] = 7;
    preference[2][3] = 4;
    preference[3][3] = 0;

  }

  private Board currentBoard;

  public void registerBoard(Board board) {
    this.currentBoard = board;
  }

  public static int getCurrentMoveNo(Board board) {
    return board.getPlayerSquareCounts().get(Player.WHITE)
        + board.getPlayerSquareCounts().get(Player.BLACK) - 4;
  }

  public int positionalStrategy(Board board, Player player, Square lastMove) {
    int value1 =
        board.getPlayerSquareCounts().get(board.getCurrentPlayer())
            - board.getPlayerSquareCounts().get(board.getCurrentPlayer().opponent());
    int value2 = positionEvaluation(board, player);
    return (int) (value2) + value1; // between 2-4
  }

  public static int currentMobilityScaleFactor(int moveNo) {
    return 2;
  }

  public static Square[][] squares = new Square[8][8];

  public int[] calculateMobility(Board board, Player player) {
    int current = board.getCurrentPossibleSquares().size();
    int potential = current;
    // Set<Square> emptySquares = new HashSet<Square>();
    int countEmptySquares = 0;
    Set<Square> occupiedSquares = board.getSquareOwners().keySet();

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (occupiedSquares.contains(squares[i][j])
            || board.getCurrentPossibleSquares().contains(squares[i][j]))
          continue;
        // check if free everywhere
        if ((i - 1) >= 0
            && occupiedSquares.contains(squares[i - 1][j])
            && board.getSquareOwners().get(squares[i - 1][j]).ordinal() == player.opponent().ordinal()) {
          countEmptySquares++;
          // emptySquares.add(squares[i][j]);
          continue;
        }

        if ((i - 1) >= 0
            && (j - 1) >= 0
            && occupiedSquares.contains(squares[i - 1][j - 1])
            && board.getSquareOwners().get(squares[i - 1][j - 1]).ordinal() == player.opponent().ordinal()) {
          countEmptySquares++;
          // emptySquares.add(squares[i][j]);
          continue;
        }

        if ((i - 1) >= 0
            && (j + 1) < 8
            && occupiedSquares.contains(squares[i - 1][j + 1])
            && board.getSquareOwners().get(squares[i - 1][j + 1]).ordinal() == player.opponent().ordinal()) {
          countEmptySquares++;
          // emptySquares.add(squares[i][j]);
          continue;
        }

        if ((i + 1) < 8
            && occupiedSquares.contains(squares[i + 1][j])
            && board.getSquareOwners().get(squares[i + 1][j]).ordinal() == player.opponent().ordinal()) {
          countEmptySquares++;
          // emptySquares.add(squares[i][j]);
          continue;
        }

        if ((i + 1) < 8
            && (j - 1) >= 0
            && occupiedSquares.contains(squares[i + 1][j - 1])
            && board.getSquareOwners().get(squares[i + 1][j - 1]).ordinal() == player.opponent().ordinal()) {
          countEmptySquares++;
          // emptySquares.add(squares[i][j]);
          continue;
        }

        if ((i + 1) < 8
            && (j + 1) < 8
            && occupiedSquares.contains(squares[i + 1][j + 1])
            && board.getSquareOwners().get(squares[i + 1][j + 1]).ordinal() == player.opponent().ordinal()) {
          countEmptySquares++;
          // emptySquares.add(squares[i][j]);
          continue;
        }

        if ((j - 1) >= 0
            && occupiedSquares.contains(squares[i][j - 1])
            && board.getSquareOwners().get(squares[i][j - 1]).ordinal() == player.opponent().ordinal()) {
          countEmptySquares++;
          // emptySquares.add(squares[i][j]);
          continue;
        }

        if ((j + 1) < 8
            && occupiedSquares.contains(squares[i][j + 1])
            && board.getSquareOwners().get(squares[i][j + 1]).ordinal() == player.opponent().ordinal()) {
          countEmptySquares++;
          // emptySquares.add(squares[i][j]);
          continue;
        }
        // check over here
      }
    }

    return new int[] { current, potential + countEmptySquares };
  }

  private int positionalValue = 0;

  public void squarePlaced(Square square) {
    positionalValue +=
        preference[flipPosition(square.getRow(), 8)][flipPosition(square.getColumn(), 8)];
  }

  public int positionEvaluation(Board board) {
    int temp = positionalValue;

    Set<Square> squares = board.getSquareOwners().keySet();

    for (Square square : squares) {
      if (!board.getSquareOwners().containsKey(square)
          && board.getSquareOwners().get(square).ordinal() == currentBoard.getCurrentPlayer().ordinal())
        temp += preference[flipPosition(square.getRow(), 8)][flipPosition(square.getColumn(), 8)];
    }
    return temp;
  }

  private int flipPosition(int x, int size) {
    if (x > size / 2 - 1) {
      x = size - 1 - x;
    }
    return x;
  }

  public int positionEvaluation(Board board, Player player) {
    int value = 0;
    int i = 0;
    int j = 0;
    int sizeofBoard = board.size();

    Map<Square, Player> playerSquareMap = board.getSquareOwners();
    for (int x = 0; x < 8; x++) {
      for (int y = 0; y < 8; y++) {
        if (playerSquareMap.containsKey(squares[x][y])) {
          i = flipPosition(x, sizeofBoard);
          j = flipPosition(y, sizeofBoard);

          if (j > i) {
            int temp = i;
            i = j;
            j = temp;
          }

          if (playerSquareMap.get(squares[x][y]).ordinal() == player.ordinal()) {
            value += preference[i][j];
          } else
            value -= preference[i][j];
        }

      }
    }
    return player.equals(board.getCurrentPlayer()) ? value : -value;
  }
}
