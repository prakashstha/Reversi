package edu.uab.cis.reversi.strategy.group10;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class Group1Strategy implements Strategy {

  private Square theChosenOne;
  private Player thisPlayer;
  int depth = 0;
  EvaluationFunction evalFunction = new EvaluationFunction();
  private int DEPTH_LIMIT = 4;
  Calendar calendar;
  Calendar tempCalendar;
  long currentTime = 0;
  long branches = 0;
  int v = 0;
  TreeNode root = null;

  @Override
  public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
      // TODO Auto-generated method stub
  	 if(time > 900 && unit == TimeUnit.MILLISECONDS){
  	       DEPTH_LIMIT = 4;
  	    }
  	   else
  	   {
  	       DEPTH_LIMIT = 2;
  	   }
  	 System.out.println("Time : " + time + "\nDepth Limit: " + DEPTH_LIMIT);

  }
  
  @Override
  public Square chooseSquare(Board board) {
    theChosenOne = Square.PASS;
    thisPlayer = board.getCurrentPlayer();

    root = new TreeNode(board);
    v = minMaxTree(root, thisPlayer, depth);

    for (TreeNode child : root.children) {
      if (child.getUtility() == v) {
        theChosenOne = child.square;
        break;
      }
    }
    return theChosenOne;
  }

  private int minMaxTree(TreeNode root, Player player, int depth) {

    Board board = root.getBoard();

    if (root.getDepth() == DEPTH_LIMIT || board.isComplete()) {
      root.setUtility(evaluationFunction(root.getBoard(), thisPlayer, root.square));
      return root.getUtility();
    }

    int value = player.ordinal() == thisPlayer.ordinal() ? Integer.MIN_VALUE : Integer.MAX_VALUE;

    for (Square square : board.getCurrentPossibleSquares()) {

      Board newBoard = board.play(square);
      TreeNode child = new TreeNode(root.getAlpha(), root.getBeta(), root, newBoard, square);
      root.children.add(child);

      int v = minMaxTree(child, player.opponent(), depth);

      if (player.ordinal() == thisPlayer.ordinal()) {
        value = Math.max(value, v);
        if (value >= root.getBeta()) {
          root.setUtility(value);
          return value;
        }
        root.setAlpha(Math.max(value, root.getAlpha()));
      } else {
        value = Math.min(value, v);
        if (value <= root.getAlpha()) {
          root.setUtility(value);
          return value;
        }
        root.setBeta(Math.min(value, root.getBeta()));
      }

    }
    root.setUtility(value);
    return value;
  }

  private int evaluationFunction(Board board, Player player, Square lastMove) {
    int[] mobility = evalFunction.calculateMobility(board, player);

    int moveNumber = EvaluationFunction.getCurrentMoveNo(board);
    int value =
        EvaluationFunction.currentMobilityScaleFactor(moveNumber)
            * mobility[EvaluationFunction.CURRENT_MOBILITY]
            + mobility[EvaluationFunction.POTENTIAL_MOBILITY];
    return value + evalFunction.positionalStrategy(board, player, lastMove);
  }

  private class TreeNode {

    private int alpha;
    private int beta;
    private int utility;
    private TreeNode parent;
    private Board board;
    private int depth;
    public Square square;
    public List<TreeNode> children = new ArrayList<TreeNode>();

    public TreeNode(Board board) {
      alpha = Integer.MIN_VALUE;
      beta = Integer.MAX_VALUE;
      parent = null;
      depth = 0;
      this.board = board;
      utility = 0;
      square = null;
    }

    public TreeNode(int alpha, int beta, TreeNode parent, Board board, Square square) {
      this.alpha = alpha;
      this.beta = beta;
      this.parent = parent;
      this.board = board;
      this.depth = parent != null ? parent.getDepth() + 1 : 0;
      utility = 0;
      this.square = square;
    }

    public void setAlpha(int a) {
      this.alpha = a;
    }

    public void setBeta(int b) {
      this.beta = b;
    }

    public int getAlpha() {
      return this.alpha;
    }

    public int getBeta() {
      return this.beta;
    }

    public Board getBoard() {
      return this.board;
    }

    public int getDepth() {
      return this.depth;
    }

    public int getUtility() {
      return this.utility;
    }

    public void setUtility(int utility) {
      this.utility = utility;
    }

  }

}