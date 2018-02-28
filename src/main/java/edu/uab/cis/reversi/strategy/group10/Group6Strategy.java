package edu.uab.cis.reversi.strategy.group10;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class Group6Strategy implements Strategy {
	Player currentPlayer;
	int max_depth = 1; 
	private static int[][] boardValues = new int[][] {
	    {1000,   -100,    500,  300,  300,    500,   -100,  1000},
	     {-100,   -300, -30, -50, -50, -30,   -300,  -100},
	     { 500, -30,   30,  50,  50,   30, -30,   500},
	     {300,  -50,   10,   0,   0,   10,  -50,  300},
	     {300,  -50,   10,   0,   0,   10,  -50,  300},
	     { 500, -30,   30,  50,  50,   30, -30,   500},
	     {-100,   -300, -30, -50, -50, -30,   -300,  -100},
	     {1000,   -100,    500,  300,  300,    500,   -100,  1000}
	  };
	
	@Override
	public Square chooseSquare(Board board) {
		Square bestMove = null;
		currentPlayer = board.getCurrentPlayer();
		
		Map<Player, Integer> playerCounts = board.getPlayerSquareCounts();
	    if (playerCounts.get(currentPlayer) < 4) //resets board for each game
	    {
	      boardValues = new int[][] {
	        {1000,   -100,    500,  300,  300,    500,   -100,  1000},
	        {-100,   -300, -30, -50, -50, -30,   -300,  -100},
	        { 500, -30,   30,  50,  50,   30, -30,   500},
	        {300,  -50,   10,   0,   0,   10,  -50,  300},
	        {300,  -50,   10,   0,   0,   10,  -50,  300},
	        { 500, -30,   30,  50,  50,   30, -30,   500},
	        {-100,   -300, -30, -50, -50, -30,   -300,  -100},
	        {1000,   -100,    500,  300,  300,    500,   -100,  1000}
	      };
	    }
		
	    Boolean count_moves = false;
	    Set<Square> possible_moves = board.getCurrentPossibleSquares();
	    
	    if(possible_moves.size() < 5)
	    	count_moves = true;
	    
	    int bestScore = Integer.MIN_VALUE;
	    
	    for (Square child_square : possible_moves) {
	    	int child_value = boardValues[child_square.getRow()][child_square.getColumn()];
	    	
	        int alpha = implement_min_max(new Node(board, child_square), 0, bestScore, Integer.MAX_VALUE, count_moves, 2 * child_value);
	        if (alpha > bestScore || bestMove == null) {
	            bestMove = child_square;
	            bestScore = alpha;
	        }
	    }
	    
	    if (bestMove.getRow() == 0 && bestMove.getColumn()==0)
	    {
	      boardValues[0][1] = 800;
	      boardValues[1][0] = 800;
	      boardValues[1][1]= 100;
	    }
	    else if (bestMove.getRow() == 0 && bestMove.getColumn()==7)
	    {
	      boardValues[0][6] =800;
	      boardValues[1][7] = 800;
	      boardValues[1][6]= 100;
	    }
	    else if (bestMove.getRow() == 7 && bestMove.getColumn()==0)
	    {
	      boardValues[7][1] = 800;
	      boardValues[6][0] = 800;
	      boardValues[6][1]= 100;
	    }
	    else if (bestMove.getRow() == 7 && bestMove.getColumn()==7)
	  {
	    boardValues[7][6] = 800;
	    boardValues[6][7] = 800;
	    boardValues[6][6]= 100;
	  }
	    
	    
	    return bestMove;
	}
	
	@Override
	public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
		max_depth = (int)time/1000 + 1;
	}

	private int implement_min_max(Node currentNode, int depth, int alpha, int beta, Boolean count_moves, int node_value) {
	    Board newBoard = new Board(); 
	    newBoard = currentNode.board.play(currentNode.position);
	    
		if (depth == max_depth || currentNode.board.getCurrentPossibleSquares().size() == 0) {
	        return getCountHeuristic(newBoard, count_moves, node_value);
	    }
	    
	    if (newBoard.getCurrentPlayer() == currentPlayer) {
	        int current_max = Integer.MIN_VALUE;
	        
	        for (Square child_square : newBoard.getCurrentPossibleSquares()) {
	        	current_max = Math.max(current_max, implement_min_max(new Node(newBoard, child_square),depth, alpha, beta, count_moves, node_value));
	            alpha = Math.max(alpha, current_max);
	            if (alpha >= beta) {
	                return alpha;
	            }
	        }
	        
	        return current_max;
	    }
	    else{
		    int current_min = Integer.MAX_VALUE;
		    int num_of_opp_moves = newBoard.getCurrentPossibleSquares().size();
		    for (Square child_square : newBoard.getCurrentPossibleSquares()) {
		    	current_min = Math.min(current_min, implement_min_max(new Node(newBoard, child_square), depth + 1, alpha, beta, count_moves, node_value - boardValues[child_square.getRow()][child_square.getColumn()] - 15 * num_of_opp_moves));
		        beta = Math.min(beta, current_min);
		        if (beta <= alpha) {
		            return beta;
		        }
		    }
		    
		    return current_min;
	    }
	}
	
	public Integer getCountHeuristic(Board board, Boolean count_moves, int node_value){
		Map<Player, Integer> mapper = board.getPlayerSquareCounts();
		return  node_value + mapper.get(currentPlayer) - mapper.get(currentPlayer.opponent())+ ((count_moves && board.getCurrentPlayer() == currentPlayer) ? 40 * board.getCurrentPossibleSquares().size() : 0);
		
	}
	
	public class Node{
		Board board;
		Square position;
		
		public Node(Board t_Board, Square t_square){
			this.board = t_Board;
			this.position = t_square;
		}
	}
}
