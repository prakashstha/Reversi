package edu.uab.cis.reversi.strategy.group10;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class Group3Strategy implements Strategy {
	
	List<Square> corners;
	int depth = 4;
	
	public Group3Strategy() {
		corners = Lists.newArrayList();
        int size = 7; // 8 - 1
        
		corners.add(new Square(0, 0));
        corners.add(new Square(0, size));
        corners.add(new Square(size, 0));
        corners.add(new Square(size, size));
	}
	
	@Override
	public Square chooseSquare(Board board) {
		
		for (Square s : board.getCurrentPossibleSquares()){
            if (corners.contains(s)){
            	return s;
            }
        }
		
		Player me = board.getCurrentPlayer();
		ReversiMiniMax minimax = new ReversiMiniMax(board, me, depth);
		
		return minimax.getBestSquareToMove();
	}
	
	@Override
	public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
		if(unit.toMillis(time) < 1000){
			depth = 3;
		}else{
			depth = 4;
		}
		
		//System.out.println(depth);
	}
}


 class ReversiState{
	private Board board;

	private int depth; //how deep the state is in the minimax tree
	private Square creatingMove;
	
	private Iterator<Square> squareIterator;
	
	private static CornerStats cornerStats = new CornerStats();
	
	public ReversiState(Board board, int depth) {		
		this.board = board;
		this.depth = depth;
		
		squareIterator = board.getCurrentPossibleSquares().iterator();
		
	}

	public int getUtlity(Player maxAgent){		
		int squaresOccupied = board.getPlayerSquareCounts().get(maxAgent);
				
		int mobility = board.getCurrentPossibleSquares().size();
				
		if(board.isComplete()){			
			int opponentSquares = board.getPlayerSquareCounts().get(maxAgent.opponent());
			
			if(squaresOccupied > opponentSquares){ //win
				return squaresOccupied * 100;
			}else if(squaresOccupied == opponentSquares){ //draw
				return squaresOccupied * -200;
			}else{ //loss
				return opponentSquares * -200;
			}
		}else if(mobility == 0){
			if(board.getCurrentPlayer().equals(maxAgent)){
				return -1000;
			}else{
				return 500;
			}
		}
		
		Map<Square, Player> squareOccupied = board.getSquareOwners();
		
		cornerStats.initStats(squareOccupied, maxAgent);
		
		int cornersOccupied =  cornerStats.getCornersOccupied();
				
		int nearCornersSuitableForOpponent = cornerStats.getNearCornersBadForMax();		
		
		//int[][] edgeCount = cornerStats.getEdgeCount();
		
		int utility = (int) (squaresOccupied * 3
				+ mobility * 12 
				+ (cornersOccupied - cornerStats.getMinCornerCount()) * 50  
				+ nearCornersSuitableForOpponent * -10
//				+ (edgeCount[0][0] - edgeCount[0][1]) * 6
//				+ (edgeCount[1][0] - edgeCount[1][1]) * 6
//				+ (edgeCount[2][0] - edgeCount[2][1]) * 6
//				+ (edgeCount[3][0] - edgeCount[3][1]) * 6
				);
		
		return utility;
	}
	
	public Board getBoard(){
		return this.board;
	}
	
	public boolean hasNoValidMoves(){
		return board.getCurrentPossibleSquares().size() == 0;
	}
		
	public ReversiState getNextSuccessor(){
		Square s;
		Board playedBoard;
		
		try{
			
			s = squareIterator.next();
		}catch(NullPointerException ex){
			
			return null;
		}
			
		playedBoard = board.play(s);
		
		ReversiState child = new ReversiState(playedBoard, depth + 1);

		child.setCreatingMove(s);

		return child;
		
	}
	
	/*
	 * The move that created the current state
	 */
	private void setCreatingMove(Square s) {
		this.creatingMove = s;
	}

	/*
	 * Returns the move (i.e. the Square) that created the current State
	 */
	public Square getCreatingMove() {
		return this.creatingMove;
	}

	public int getDepth() {
		return depth;
	}
	
//	@Override
//	public boolean equals(Object obj) {
//		if(obj instanceof ReversiState){
//			ReversiState other = (ReversiState) obj;
//			return this.toString().equals(other.toString()) && this.depth == other.depth;
//		}
//		
//		return false;
//	}
//	
//	@Override
//	public int hashCode() 
//	{
//		return Objects.hash(this.toString(), this.depth);
//	};
	
	@Override
	public String toString() {
		return board.toString();
	}
}
 
class CornerStats{
	
	Square[] corners;
	private List<Square[]> cornerNeighbors;
	
	List<Square> maxAgentSquares;
	
	//List<List<Square>> edges;
	
	Map<Square, Player> squaresOccupied;
	
	int nearCornerCount;
	int maxCornerCount, minCornerCount;
	int boardSize = 7;

	int edgeCount[][] = new int[4][2];
	
	public CornerStats() {
		
		//System.out.println("Stats init");
		
		corners = new Square[]{new Square(0, 0), 
				new Square(0, boardSize),
				new Square(boardSize, 0),
				new Square(boardSize, boardSize)
		};
		
		cornerNeighbors = Lists.newArrayList();
		
		cornerNeighbors.add(new Square[] { new Square(0, 1), new Square(1, 0), new Square(1, 1) });
		
		cornerNeighbors.add( 
				new Square[] { new Square(0, boardSize - 1), new Square(1, boardSize), new Square(1, boardSize - 1) });
		
		cornerNeighbors.add( 
				new Square[] { new Square(boardSize - 1 , 0), new Square(boardSize, 1), new Square(boardSize - 1, 1) } );
		
		cornerNeighbors.add(
				new Square[] { new Square(boardSize, boardSize - 1), new Square(boardSize - 1, boardSize), new Square(boardSize - 1, boardSize - 1) } );
				
//		List<Square> topEdge = Lists.newArrayList();
//		List<Square> bottomEdge = Lists.newArrayList();
//
//		for(int col = 2; col < boardSize - 1; col++){
//			topEdge.add(new Square(0, col));
//			bottomEdge.add(new Square(boardSize, col));
//		}
//
//		List<Square> leftEdge = Lists.newArrayList();
//		List<Square> rightEdge = Lists.newArrayList();
//
//
//		for(int row = 2; row < boardSize - 1; row++){
//			leftEdge.add(new Square(row, 0));
//			rightEdge.add(new Square(row, boardSize));
//		}
//		
//		edges = Lists.newArrayList();
//		
//		edges.add(topEdge);
//		edges.add(bottomEdge);
//		edges.add(leftEdge);
//		edges.add(rightEdge);
	}
	
	public void initStats(Map<Square, Player> squareOccupied, Player maxAgent){
		
		this.squaresOccupied = squareOccupied;
		
		nearCornerCount = 0;
		maxCornerCount = 0;
		minCornerCount = 0;
		
		for(int i = 0; i < 4; i++){
			Square corner = corners[i];
			
			Player player  = squaresOccupied.get(corner);
			
			if(player == null){ //don't proceed if the corner is not occupied
				continue;
			}
			
			if( player != maxAgent){ //if the corner is not occupied by maxAgent
				
				minCornerCount++;
				
				for(Square neighbor : cornerNeighbors.get(i)){ //if the neighboring corners are occupied..we're screwed
					if(squaresOccupied.get(neighbor) == maxAgent){
						nearCornerCount++;
						break;
					}
				}
			} else {
				maxCornerCount++;
			}
		}
		
		
		
//		for(int i = 0; i < 4; i++){
//			edgeCount[i][0] = 0;
//			edgeCount[i][1] = 0;
//			
//			for(Square sq : edges.get(i)){
//				Player p = squaresOccupied.get(sq);
//				if(p == maxAgent){
//					edgeCount[i][0]++;
//				}
////				} else if(p == maxAgent.opponent()){
////					edgeCount[i][1]++;
////				}
//			}
//		}
	}	

	
	public int getMinCornerCount() {
		return minCornerCount;
	}
	
	public int getCornersOccupied(){
		return maxCornerCount;
	}
	
	public int getNearCornersBadForMax(){
		return nearCornerCount;
	}
	
	public int[][] getEdgeCount(){
		return edgeCount;
	}
	
}
 
 class ReversiMiniMax {

		private Player maxAgent;
		private Board board;
		private int maxDepth;
		private int bestAlpha;
				
		public ReversiMiniMax(Board board, Player maxAgent, int depth) {
			this.board = board;
			this.maxAgent = maxAgent;
			this.maxDepth = depth;					
		}

		public Square getBestSquareToMove()
		{
			return getBestState().getCreatingMove();
		}

		public ReversiState getBestState()
		{
			ReversiState currentState = new ReversiState(board, 0); //initial State has depth 0		

			ReversiState bestState = null;
			int max = Integer.MIN_VALUE;
			int value;
			
			bestAlpha = Integer.MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			
			ReversiState successor;
			while( (successor =  currentState.getNextSuccessor()) != null){
			
				value = alphaBeta(successor, bestAlpha, beta, false);		
				
				if( value > max){
					max = value;
					bestAlpha = max;
					bestState = successor;
				}
			}
			
			return bestState;
		}
		
		public Board getOptimalBoardToMove(){
			
			ReversiState bestState = getBestState();						
			
			System.out.println(bestState.getCreatingMove());
			
			return bestState.getBoard();
		}
		
		private int alphaBeta(ReversiState state, int alpha, int beta, boolean isMaximizingAgent){
			if(state.getDepth() >= maxDepth || state.hasNoValidMoves() || state.getBoard().isComplete()){	
				return state.getUtlity(this.maxAgent);
			}
			
			ReversiState successor;
			int v;
			
			if(isMaximizingAgent){
				v = Integer.MIN_VALUE;
				
				while( (successor =  state.getNextSuccessor()) != null){
					v = Math.max(v, alphaBeta(successor, alpha, beta, false));
					
					if(v >= beta){
						break;
					}				
					alpha = Math.max(alpha, v);
					
				}
			}else{
				v = Integer.MAX_VALUE;
				
				while( (successor =  state.getNextSuccessor()) != null){
					v = Math.min(v, alphaBeta(successor, alpha, beta, true));
					
					if(v <= alpha){
						break;
					}
					
					beta = Math.min(beta, v);
				}
			}
			
			this.bestAlpha = alpha;
			
			return v;
		}
	}

