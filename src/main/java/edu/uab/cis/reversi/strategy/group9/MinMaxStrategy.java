package edu.uab.cis.reversi.strategy.group9;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class MinMaxStrategy implements Strategy {

	Player currentPlayer;
	int MAX_DEPTH = 2;
	@Override
	public Square chooseSquare(Board board) {
		// TODO Auto-generated method stub
		Node root = new Node(board, null);
		Square bestMove = null;
		currentPlayer = board.getCurrentPlayer();
		Set<Square> possibleSquares = board.getCurrentPossibleSquares();
	    Double bestScore = Double.NEGATIVE_INFINITY;
		    
		    for (Square childSquare : possibleSquares) {
		    	//int child_value = boardValues[child_square.getRow()][child_square.getColumn()];
		    	Node child = new Node(board, childSquare);
		        Double alpha = minmaxAlgorithm(child, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
		        if (alpha > bestScore || bestMove == null) {
		            bestMove = childSquare;
		            bestScore = alpha;
		        }
		    }
		//minmaxAlgorithm(node, depth, alpha, beta, isOpponent)
		   // System.out.println("Square:" + bestMove);
		return bestMove;
	}
	
	@Override
	public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
		// TODO Auto-generated method stub
		System.out.println("Max Depth : "+MAX_DEPTH);
	}
	
	public double getScore(Board board, Player currentPlayer){
		 
        Player[][] brd = new Player[8][8];
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++)  {
                brd[i][j] = null;
            }
       for(Square s :board.getSquareOwners().keySet()){
          Player p =  board.getSquareOwners().get(s);
           brd[s.getRow()][s.getColumn()] = p;
       }

        UtilityFunction func = new UtilityFunction();
        return     func.heuristic_evaluation_function(brd, currentPlayer, board);
      //  Map<Player, Integer> mp =board.getPlayerSquareCounts();
     //   return mp.get(player);
    }


	private Double minmaxAlgorithm(Node node, int depth, Double alpha, Double beta, boolean maximizingPlayer) {
	    Board newBoard = new Board(); 
	    newBoard = node.board.play(node.move);
	    
		if (depth == MAX_DEPTH || node.board.getCurrentPossibleSquares().size() == 0 || node.board.isComplete()) {
//	        ScoreEvaluation eval = new ScoreEvaluation();
//	        return eval.getScore(node.board, node.move);
			return getScore(node.board.play(node.move), maximizingPlayer?currentPlayer:currentPlayer.opponent());
	    }
	    
	    if (maximizingPlayer) { //maximizing player
	        Double max = Double.NEGATIVE_INFINITY;
	        for (Square childSquare : newBoard.getCurrentPossibleSquares()) {
	        	max = Math.max(max, minmaxAlgorithm(new Node(newBoard, childSquare),depth+1, alpha, beta, false));
	            alpha = Math.max(alpha, max);
	            if (alpha >= beta) {
	                break;
	            }
	        }
	        return max;
	    }
	    else{
		    Double min = Double.POSITIVE_INFINITY;
		    for (Square childSquare : newBoard.getCurrentPossibleSquares()) {
		    	min = Math.min(min, minmaxAlgorithm(new Node(newBoard, childSquare), depth + 1, alpha, beta, true));
		        beta = Math.min(beta, min);
		        if (beta <= alpha) {
		           break;
		        }
		    }
		    return min;
	    }
	}

	
	private class Node{
		Board board;
		Square move;
		public Node(Board brd, Square mv)
		{
			this.board = brd;
			this.move = mv;
		}
	}
}
