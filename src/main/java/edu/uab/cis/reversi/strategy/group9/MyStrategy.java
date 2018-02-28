package edu.uab.cis.reversi.strategy.group9;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class MyStrategy implements Strategy{

	// Strategy factors multipliers
    
	 double wtScoreFacter = 5.0;
     double mobilityScoreFactor = 1.5;
     double discGainScoreFactor = 1.5;
     double frontierScoreFactor = 1;
     double opponentScoreFactor = 0.5;
     
     double lowestWeightScore;
    
	Board thisBoard;
	List<Double>weightScoresList;
	List<Double>mobilityScoresList;
	List<Double>discGainScoreList;
	List<Double>frontierScoresList;
	List<Double>opponetAvailableMaxWeightScoresList;
	@Override
	public Square chooseSquare(Board board) {
		// TODO Auto-generated method stub
		this.thisBoard = board;
		return bestSquare(board);
		
	}
	private Square bestSquare(Board board) {
		// TODO Auto-generated method stub
		weightScoresList = new ArrayList<Double>();
		mobilityScoresList = new ArrayList<Double>();
		discGainScoreList = new ArrayList<Double>();
		frontierScoresList = new ArrayList<Double>();
		opponetAvailableMaxWeightScoresList = new ArrayList<Double>();
		
		double tempWeightScore;
		double tempMobilityScore;
		double tempDiscGainScore;
		double tempFrontierScore;
		double tempOpponentAvailableMaxScore;
	
		
		
		List<Square> possibleSquareList = new ArrayList<>(board.getCurrentPossibleSquares());
		generateWeightScores(possibleSquareList);
		generateMobilityScores(possibleSquareList);
	
		//System.out.println("Opponent scores:" + opponetAvailableMaxWeightScores);
		double bestScore = -100, tempTotalScore;
		List<Square> bestSquareList = new ArrayList<Square>();
		
		for(int i = 0;i<possibleSquareList.size();i++)
		{
			tempWeightScore = weightScoresList.get(i) * wtScoreFacter;
			tempMobilityScore = mobilityScoresList.get(i) * mobilityScoreFactor;
			tempDiscGainScore = discGainScoreList.get(i) *discGainScoreFactor;
			tempOpponentAvailableMaxScore = opponetAvailableMaxWeightScoresList.get(i)*opponentScoreFactor;
			tempFrontierScore = frontierScoresList.get(i)*frontierScoreFactor;
			tempTotalScore = tempDiscGainScore + tempMobilityScore + tempWeightScore+tempOpponentAvailableMaxScore+tempFrontierScore;
			if(bestScore<tempTotalScore){
				bestScore = tempTotalScore;
				//bestSquare = possibleSquareList.get(i);
			}
		}
		//making list of best squares
		for(int i = 0;i<possibleSquareList.size();i++)
		{
			tempWeightScore = weightScoresList.get(i)*wtScoreFacter;
			tempMobilityScore = mobilityScoresList.get(i)*mobilityScoreFactor;
			tempDiscGainScore = discGainScoreList.get(i)*discGainScoreFactor;
			tempOpponentAvailableMaxScore = opponetAvailableMaxWeightScoresList.get(i)*opponentScoreFactor;
			tempFrontierScore = frontierScoresList.get(i)*frontierScoreFactor;
			tempTotalScore = tempDiscGainScore + tempMobilityScore + tempWeightScore + tempOpponentAvailableMaxScore+tempFrontierScore;
			if(bestScore == tempTotalScore){
				bestSquareList.add(possibleSquareList.get(i));
				//bestSquare = possibleSquareList.get(i);
			}
		}
		//System.out.println("best square list" + bestSquareList);
		return bestSquareList.get(new Random().nextInt(bestSquareList.size()));
        
	}
	private void generateMobilityScores(List<Square> squareList) {
		// TODO Auto-generated method stub
		Board tempBoard = thisBoard;
		int blankSquareCount;
		double score = 0, discGain, opponentMaxWeightScore, frontierScore;
	    
	    //for each square in square list
		for(Square sq: squareList)
		{
			tempBoard = thisBoard.play(sq);
			
			//opponent mobility score
			blankSquareCount = 64 - tempBoard.getPlayerSquareCounts().get(tempBoard.getCurrentPlayer())  -
					tempBoard.getPlayerSquareCounts().get(tempBoard.getCurrentPlayer().opponent());
			if(blankSquareCount!=0)
				score = (blankSquareCount - tempBoard.getCurrentPossibleSquares().size())/(double)blankSquareCount;
			else
				score = 0;
			//System.out.println("opponent mobility score: "+score);
			mobilityScoresList.add(score);
			
			//disc gain score
			discGain = (tempBoard.getPlayerSquareCounts().get(thisBoard.getCurrentPlayer()) - 
					thisBoard.getPlayerSquareCounts().get(thisBoard.getCurrentPlayer()))/64.0;
			//System.out.println("disc gain: "+discGain);
			discGainScoreList.add(discGain);
			

			//computing opponent maximum weight score
			if(tempBoard.getCurrentPossibleSquares().size()>0){
				List<Square> tempPossibleSquare = new ArrayList<>(tempBoard.getCurrentPossibleSquares());
				opponentMaxWeightScore = getWeightOfSquare(getMaxWeightSquare(tempPossibleSquare));
			}
			else{
				opponentMaxWeightScore = lowestWeightScore;
			}
			//System.out.println("opponent wt score: "+opponentMaxWeightScore*(-1));
			opponetAvailableMaxWeightScoresList.add(opponentMaxWeightScore * (-1));
			
			frontierScore = getFrontierScore(tempBoard, thisBoard.getCurrentPlayer());
			//System.out.println("frontier score: "+frontierScore);
			frontierScoresList.add(frontierScore);
			
		}
		
	}
	private double getFrontierScore(Board tempBoard, Player currentPlayer) {
		// TODO Auto-generated method stub
		double score = 0;
		Square tempSquare, left, right, up, down;
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
					up = new Square(row-1, col);
					if(tempBoard.getSquareOwners().containsKey(up))
					{
						score += 0.25;
					}
				}
				if(row<8)
				{
					down = new Square(row+1, col);
					if(tempBoard.getSquareOwners().containsKey(down))
					{
						score += 0.25;
					}
				}
				if(col>0)
				{
					left = new Square(row, col-1);
					if(tempBoard.getSquareOwners().containsKey(left))
					{
						score += 0.25;
					}
				}
				if(col<8)
				{
					right = new Square(row, col+1);
					if(tempBoard.getSquareOwners().containsKey(right))
					{
						score += 0.25;
					}
				}
			}
		}
		//System.out.println(score);
		return (score/squareOwner.size());
	}
	private void generateWeightScores(List<Square> squareList) {
		// TODO Auto-generated method stub
		for(Square square:squareList)
		{
			//computing weight score of each possible square in list
			//System.out.println("Square: " + square + "-->"+getWeightOfSquare(square.getRow(), square.getColumn()));
			weightScoresList.add(getWeightOfSquare(square));
			
			
		}
		
	}
	private Square getMaxWeightSquare(List<Square> squareList)
	{
		Square maxWeightSquare = squareList.get(0);
		for(Square sq: squareList)
		{
			if(getWeightOfSquare(maxWeightSquare)<getWeightOfSquare(sq))
				maxWeightSquare = sq;
		}
		return maxWeightSquare;
	}
	private Double getWeightOfSquare(Square square) {
		// TODO Auto-generated method stub
		lowestWeightScore = 0;
		double[][] squareWeight = {
	            {1, 0.15f, 0.25f, 0.2f, 0.2f, 0.25f, 0.15f, 0.1f},
	            {0.15f, 0, 0.183f, 0.183f, 0.183f, 0.183f, 0, 0.15f},
	            {0.25f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.25f},
	            {0.2f, 0.183f, 0.183f, 0.167f, 0.167f, 0.183f, 0.183f, 0.2f},
	            {0.2f, 0.183f, 0.183f, 0.167f, 0.167f, 0.183f, 0.183f, 0.2f},
	            {0.25f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.183f, 0.25f},
	            {0.15f, 0, 0.183f, 0.183f, 0.183f, 0.183f, 0, 0.15f},
	            {1, 0.15f, 0.25f, 0.2f, 0.2f, 0.25f, 0.15f, 1}};
		return squareWeight[square.getRow()][square.getColumn()];
	}

}
