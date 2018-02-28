package edu.uab.cis.reversi.strategy.group9;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;

public class ScoreEvaluation {

	Board thisBoard,tempBoard;
	//Board tempBoard;
	Double lowestWeightScore;
	
	final double wtScoreFacter = 5.0;
    final double mobilityScoreFactor = 1.5;
    final double discGainScoreFactor = 2;
    final double frontierScoreFactor = 1;
    final double opponentScoreFactor = 1;
	

	public double getScore(Board board, Square sq)
	{
		int noOfMoves = board.getMoves().size();
		lowestWeightScore = 0.0;
		double[][] squareWeight = {
	            {1, 0.15f, 0.25f, 0.2f, 0.2f, 0.25f, 0.15f, 0.1f},
	            {0.15f, 0, 0.183f, 0.183f, 0.183f, 0.183f, 0, 0.15f},
	            {0.25f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.25f},
	            {0.2f, 0.183f, 0.183f, 0.167f, 0.167f, 0.183f, 0.183f, 0.2f},
	            {0.2f, 0.183f, 0.183f, 0.167f, 0.167f, 0.183f, 0.183f, 0.2f},
	            {0.25f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.25f},
	            {0.15f, 0, 0.183f, 0.183f, 0.183f, 0.183f, 0, 0.15f},
	            {1, 0.15f, 0.25f, 0.2f, 0.2f, 0.25f, 0.15f, 1}};
		
		
		
		double weightScore = 0.0;
		double mobilityScore = 0.0;
	    double discGainScore = 0.0;
	    double frontierScore = 0.0;
	    double opponentMaxWeightScore = 0.0;
	    
		thisBoard = board;//(board, sq);
		//System.out.println(sq.toString());
		
		//System.out.println(thisBoard.toString());
		tempBoard = thisBoard.play(sq);
		
		/**
		 * Square Weight Score
		 */
		if(sq!=null)
		 weightScore = squareWeight[sq.getRow()][sq.getColumn()] * wtScoreFacter;
		
		/**
		 * mobility score calculation
		 */
		int blankSquareCount;
		blankSquareCount = 64 - tempBoard.getPlayerSquareCounts().get(tempBoard.getCurrentPlayer())  -
				tempBoard.getPlayerSquareCounts().get(tempBoard.getCurrentPlayer().opponent());
		if(blankSquareCount!=0)
			mobilityScore = (blankSquareCount - tempBoard.getCurrentPossibleSquares().size())/(double)blankSquareCount;
		else
			mobilityScore = 0.0;
		
		/**
		 * Disc Gain Score 
		 */
		discGainScore = (tempBoard.getPlayerSquareCounts().get(thisBoard.getCurrentPlayer()) - 
				thisBoard.getPlayerSquareCounts().get(thisBoard.getCurrentPlayer()))/64.0;
		
		
		/**
		 * Oppenent Max Weight Score
		 */
		List<Square> tempPossibleSquare = new ArrayList<>(tempBoard.getCurrentPossibleSquares());
		if(tempPossibleSquare.size()>0){
			//find max weight square
			Square maxWeightSquare = tempPossibleSquare.get(0);
			for(Square sqr: tempPossibleSquare)
			{
				if(squareWeight[maxWeightSquare.getRow()][maxWeightSquare.getColumn()]<squareWeight[sqr.getRow()][sqr.getColumn()])
					maxWeightSquare = sqr;
			}
			opponentMaxWeightScore = squareWeight[maxWeightSquare.getRow()][maxWeightSquare.getColumn()];
		}
		else{
			opponentMaxWeightScore = lowestWeightScore;
		}
		opponentMaxWeightScore = opponentMaxWeightScore *(-1);
		
		/**
		 * Frontier Score 
		 */
		if(noOfMoves<40)
		{
		double score = 0;
		Player currentPlayer = thisBoard.getCurrentPlayer();
		Square tempSquare, tempFrontierSquare;
		int row, col;
		
		Map<Square,Player> squareOwner = tempBoard.getSquareOwners();
		for(Entry<Square, Player>entry: squareOwner.entrySet())
		{
			if(entry.getValue().ordinal() == currentPlayer.ordinal())
			{
				tempSquare = entry.getKey();
				row = tempSquare.getRow();
				col = tempSquare.getColumn();
				if(row>0)
				{
					tempFrontierSquare = new Square(row-1, col);
					if(squareOwner.containsKey(tempFrontierSquare))
					{
						score += 0.25;
					}
				}
				if(row<8)
				{
					tempFrontierSquare = new Square(row+1, col);
					if(squareOwner.containsKey(tempFrontierSquare))
					{
						score += 0.25;
					}
				}
				if(col>0)
				{
					tempFrontierSquare = new Square(row, col-1);
					if(squareOwner.containsKey(tempFrontierSquare))
					{
						score += 0.25;
					}
				}
				if(col<8)
				{
					tempFrontierSquare = new Square(row, col+1);
					if(squareOwner.containsKey(tempFrontierSquare))
					{
						score += 0.25;
					}
				}
			}
		}
		//System.out.println(score);
		frontierScore = score/squareOwner.size();
		}

//		if(noOfMoves<24)
//		{
//			weightScore *= wtScoreFacter*2;
//			mobilityScore *= mobilityScoreFactor*2;
//			discGainScore *= discGainScoreFactor*2;
//			opponentMaxWeightScore *= opponentScoreFactor*2;
//			frontierScore *= frontierScoreFactor*2;
//		}
//		else if(noOfMoves<40)
//		{
//			weightScore *= wtScoreFacter*2;
//			mobilityScore *= mobilityScoreFactor;
//			discGainScore *= discGainScoreFactor*2;
//			opponentMaxWeightScore *= opponentScoreFactor*2;
//			frontierScore *= frontierScoreFactor;
//		}
		weightScore = weightScore* wtScoreFacter;
		mobilityScore = mobilityScore * mobilityScoreFactor;
		discGainScore = discGainScore*discGainScoreFactor;
		opponentMaxWeightScore = opponentMaxWeightScore*opponentScoreFactor;
		frontierScore =  frontierScore* frontierScoreFactor;		
//		mobilityScore = getMobilityScore() * mobilityScoreFactor;
//		discGainScore = getDiscGainScore()*discGainScoreFactor;
//		opponentMaxWeightScore = getOpponentMaxWeightScore()*opponentScoreFactor;
//		frontierScore =  getFrontierScore() * frontierScoreFactor;
//		System.out.println("weightScore: " + weightScore);
//		System.out.println("mobility: " + mobilityScore);
//		System.out.println("discGain: " + discGainScore);
//		System.out.println("opponetmax: " + opponentMaxWeightScore);
//		System.out.println("frontier: " + frontierScore);
		double totalScore = weightScore + mobilityScore + discGainScore + opponentMaxWeightScore+frontierScore;
//		System.out.println(totalScore);
		return totalScore;
	}
	
/*	private Board getThisBoard(Board playedBoard, Square sq) {
		// TODO Auto-generated method stub
		playedBoard.getSquareOwners().remove(sq);
		return playedBoard;
		//return null;
	}

	private Double getFrontierScore() {
		// TODO Auto-generated method stub
		double score = 0;
		Player currentPlayer = thisBoard.getCurrentPlayer();
		Square tempSquare, tempFrontierSquare;
		int row, col;
		
		Map<Square,Player> squareOwner = tempBoard.getSquareOwners();
		for(Entry<Square, Player>entry: squareOwner.entrySet())
		{
			if(entry.getValue().ordinal() == currentPlayer.ordinal())
			{
				tempSquare = entry.getKey();
				row = tempSquare.getRow();
				col = tempSquare.getColumn();
				if(row>0)
				{
					tempFrontierSquare = new Square(row-1, col);
					if(squareOwner.containsKey(tempFrontierSquare))
					{
						score += 0.25;
					}
				}
				if(row<8)
				{
					tempFrontierSquare = new Square(row+1, col);
					if(squareOwner.containsKey(tempFrontierSquare))
					{
						score += 0.25;
					}
				}
				if(col>0)
				{
					tempFrontierSquare = new Square(row, col-1);
					if(squareOwner.containsKey(tempFrontierSquare))
					{
						score += 0.25;
					}
				}
				if(col<8)
				{
					tempFrontierSquare = new Square(row, col+1);
					if(squareOwner.containsKey(tempFrontierSquare))
					{
						score += 0.25;
					}
				}
			}
		}
		//System.out.println(score);
		return (score/squareOwner.size());

	}
	private Double getOpponentMaxWeightScore() {
		// TODO Auto-generated method stub
		double opponentMaxWeightScore;
		List<Square> tempPossibleSquare = new ArrayList<>(tempBoard.getCurrentPossibleSquares());
		if(tempPossibleSquare.size()>0){
			opponentMaxWeightScore = getWeightScore(getMaxWeightSquare(tempPossibleSquare));
		}
		else{
			opponentMaxWeightScore = lowestWeightScore;
		}
		return opponentMaxWeightScore *(-1);
	}
	private Square getMaxWeightSquare(List<Square> squareList)
	{
		Square maxWeightSquare = squareList.get(0);
		for(Square sq: squareList)
		{
			if(getWeightScore(maxWeightSquare)<getWeightScore(sq))
				maxWeightSquare = sq;
		}
		return maxWeightSquare;
	}
	private Double getDiscGainScore() {
		// TODO Auto-generated method stub
		Double discGain;
		discGain = (tempBoard.getPlayerSquareCounts().get(thisBoard.getCurrentPlayer()) - 
				thisBoard.getPlayerSquareCounts().get(thisBoard.getCurrentPlayer()))/64.0;
		//System.out.println("disc gain: "+discGain);
		return discGain;
	}
	private Double getMobilityScore() {
		// TODO Auto-generated method stub
		int blankSquareCount;
		Double score;
		blankSquareCount = 64 - tempBoard.getPlayerSquareCounts().get(tempBoard.getCurrentPlayer())  -
				tempBoard.getPlayerSquareCounts().get(tempBoard.getCurrentPlayer().opponent());
		if(blankSquareCount!=0)
			score = (blankSquareCount - tempBoard.getCurrentPossibleSquares().size())/(double)blankSquareCount;
		else
			score = 0.0;
		//System.out.println("opponent mobility score: "+score);
		
		return score;
	}
	private double getWeightScore(Square sq) {
		// TODO Auto-generated method stub
		lowestWeightScore = 0.0;
		double[][] squareWeight = {
	            {1, 0.15f, 0.25f, 0.2f, 0.2f, 0.25f, 0.15f, 0.1f},
	            {0.15f, 0, 0.183f, 0.183f, 0.183f, 0.183f, 0, 0.15f},
	            {0.25f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.25f},
	            {0.2f, 0.183f, 0.183f, 0.167f, 0.167f, 0.183f, 0.183f, 0.2f},
	            {0.2f, 0.183f, 0.183f, 0.167f, 0.167f, 0.183f, 0.183f, 0.2f},
	            {0.25f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.25f},
	            {0.15f, 0, 0.183f, 0.183f, 0.183f, 0.183f, 0, 0.15f},
	            {1, 0.15f, 0.25f, 0.2f, 0.2f, 0.25f, 0.15f, 1}};
		return squareWeight[sq.getRow()][sq.getColumn()];
	}
*/
} //end of class

