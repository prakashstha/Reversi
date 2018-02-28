package edu.uab.cis.reversi.strategy.group10;

import java.util.*;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class Group6Strategy_2 implements Strategy {
	Player currentPlayer;
	@Override
	public Square chooseSquare(Board board) {
		currentPlayer = board.getCurrentPlayer();
		Node parentNode = new Node();
		parentNode.maxPosition = false;
		parentNode.currentBoard = board;
		parentNode.depth = 0;
		
		Node chosenNode = this.workWithSquare(parentNode);
		System.out.println();
		System.out.println();
		System.out.println("Last;");
		System.out.println(chosenNode.position.getRow() + "," + chosenNode.position.getColumn());
		
		return chosenNode.position;
	}
	
	public Node workWithSquare(Node parentNode){
		
		Set<Square> possible_moves = parentNode.currentBoard.getCurrentPossibleSquares();
		Boolean currentMaxPosition = !parentNode.maxPosition;
		Node chosenNode = new Node();
		
		if(currentMaxPosition)
			chosenNode.hx = Integer.MIN_VALUE;
		else
			chosenNode.hx = Integer.MAX_VALUE;
		
		chosenNode.upper_bound = parentNode.upper_bound;
		chosenNode.lower_bound = parentNode.lower_bound;
		
		
		for (Square square : possible_moves) {
			Node currentNode = new Node(square, parentNode);
			
			Board newBoard = parentNode.currentBoard.play(square);
			currentNode.currentBoard = newBoard;
			currentNode.maxPosition = currentMaxPosition;
			currentNode.upper_bound = chosenNode.upper_bound;
			currentNode.lower_bound = chosenNode.lower_bound;
			System.out.println();
			
			System.out.print(square.getRow() + "," + square.getColumn());
			System.out.print(" , Depth : " + parentNode.depth);
			
			if(parentNode != null && parentNode.depth == 1 && currentMaxPosition){
				currentNode.hx = this.getCountHeuristic(newBoard);
				currentNode.depth = parentNode.depth + 1;
				
				System.out.print(" Inside: " + currentNode.hx);
				if(parentNode.upper_bound == Integer.MAX_VALUE){
					if(chosenNode.hx < currentNode.hx){
						chosenNode = currentNode;
					}	
				}
				else {
					if(parentNode.upper_bound > currentNode.hx) {
						if(chosenNode.hx < currentNode.hx){
							chosenNode = currentNode;
						}					
					}
					else{
						return currentNode;
					}
				}
			}
			else{
				
				System.out.print(" Outside: ,");
				currentNode.depth = currentMaxPosition ? (parentNode.depth + 1): parentNode.depth;
				
				Node heuristicNode = this.workWithSquare(currentNode);
				System.out.print(";;; Current: ");
				System.out.print(heuristicNode.position.getRow() + " , " + heuristicNode.position.getColumn() + "   ,");
				System.out.print("Heuristic : " + heuristicNode.hx + ";;;");
				
				if(currentMaxPosition && chosenNode.hx < heuristicNode.hx){
					chosenNode = currentNode;
					chosenNode.hx = heuristicNode.hx;
					chosenNode.lower_bound = heuristicNode.hx;
					
					System.out.println();
					System.out.println("-----MAXER---------");
					System.out.println("depth : " + chosenNode.depth + ", Square: " + chosenNode.position.getRow() + ", " + chosenNode.position.getColumn());
					System.out.println();
				}
				
				if(!currentMaxPosition && chosenNode.hx > heuristicNode.hx){
					if(heuristicNode.hx < chosenNode.lower_bound){
						chosenNode.hx = parentNode.hx;
						
						return chosenNode;
					}
					
					chosenNode = currentNode;
					chosenNode.hx = heuristicNode.hx;
					chosenNode.upper_bound = heuristicNode.hx;
					
					System.out.println();
					System.out.println("-----MINN---------");
					System.out.println("depth : " + chosenNode.depth + ", Square: " + chosenNode.position.getRow() + ", " + chosenNode.position.getColumn());
					System.out.println();
				}
			}
		}
		
		return chosenNode;
	}
	
	public Integer getCountHeuristic(Board board){
		Map<Player, Integer> mapper = board.getPlayerSquareCounts();
		return mapper.get(currentPlayer);
		
	}
	
	public class Node {
		public Square position;
		public Board currentBoard;
		public int hx;
		public Boolean maxPosition;
		public Node parentNode;
		public int depth;
		public int upper_bound;
		public int lower_bound;
		
		public Node(Square pos, Node parent){
			this.position = pos;
			this.parentNode = parent;
			this.upper_bound = Integer.MAX_VALUE;
			this.lower_bound = Integer.MIN_VALUE;
		}

		public Node() {
			// TODO Auto-generated constructor stub
		}
	}

}
