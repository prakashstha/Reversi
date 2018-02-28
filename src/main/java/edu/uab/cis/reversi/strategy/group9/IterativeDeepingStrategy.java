
package edu.uab.cis.reversi.strategy.group9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;
public class IterativeDeepingStrategy implements Strategy {
 
    public static  MinMaxNode root;
    public static int Maxdepth = 6;
    public static int MaxSearchTime = 1000;
 
    
    
    @Override
    public Square chooseSquare(Board board) {
 
     try{
     IterativeDeeping id = new IterativeDeeping(false, MaxSearchTime, Maxdepth);
      root = new  MinMaxNode(null, new Square(0, 0), board);
         Player playa = board.getCurrentPlayer();
        Square move = id.decide(root);
          return move;
     }
     catch(Exception ex){
      return null;
     }
     
    }
 
 
//    @Override
//    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
//        // TODO Auto-generated method stub
//     if(time>900 && unit ==TimeUnit.MILLISECONDS)
//      Maxdepth = 2;
//    }
public enum EntryType {
  EXACT_VALUE, LOWERBOUND, UPPERBOUND;
}
 
public class IterativeDeeping  {
// These are the different types of values which could be stored in our
// transposition table
// Thrown if we are out of time
private class OutOfTimeException extends Exception {
}
// Helper class for storing several items in the transposition table
private class SearchNode {
  EntryType type;
  int value;
  int depth;
}
 
 // Helper class for storing several items in the transposition table
private class SearchStatistics {
  int searchDepth;
  int timeSpent;
  int nodesEvaluated;
}
// These are easier to see than maxint and minint
public static final int LOSE = -10000000;
public static final int WIN = 10000000;
private static final boolean DEBUG = false;
private boolean USE_MTDF;
// Time we have to compute a move in milliseconds
private int searchTime;
// Time we have left to search
private long startTimeMillis;
// Are we maximizing the heuristic?
private boolean maximizer;
// The maximum search depth to use
private int maxdepth;
// A transposition table for caching repeated states. Critical since
// iterative deepening hits states over and over
private Map<MinMaxNode, SearchNode> transpositionTable;
// Counter to see how many nodes we touch
private int checkedNodes;
 
 private List<SearchStatistics> statsList;
// Should we use the secondary heuristic?
private boolean useAltHeuristic;
private int mtdCalls;
private int cacheHits;
private int leafNodes;
/**
  * 
  *
  * @param maximizer
  *            Is this player maximizing the heuristic score?
  * @param searchTimeSec
  *            How much time per move can we get
  * @param maxdepth
  *            What is the maximum depth we should ever search to?
  */
public IterativeDeeping(boolean maximizer, int searchTimeSec, int maxdepth) {
  this(maximizer, searchTimeSec, maxdepth, false, false);// TODO: change this last true to false to remove mtd
}
/**
  * 
  * compare
  *
  * @param maximizer
  *            Is this player maximizing the heuristic score?
  * @param searchTimeSec
  *            How much time per move can we get
  * @param maxdepth
  *            What is the maximum depth we should ever search to?
  * @param useAltHeuristic
  *            Use an alternative heuristic for the game simpler heuristic if going deeper
  */
public IterativeDeeping(boolean maximizer, int searchTimeMSec, int maxdepth,
   boolean useAltHeuristic, boolean usemtd) {
 
  searchTime = searchTimeMSec;
  this.maximizer = maximizer;
  this.maxdepth = maxdepth;
  this.useAltHeuristic = useAltHeuristic;
  USE_MTDF = usemtd;
  statsList = new ArrayList<SearchStatistics>();
}
 
 public Square decide(MinMaxNode root) {
  startTimeMillis = System.currentTimeMillis();
  transpositionTable = new HashMap<MinMaxNode, SearchNode>(10000);
  return iterative_deepening(root);
}
/**
  * Helper to iteratively recurse down the search tree, getting deeper each
  * time
  *
  */
private Square iterative_deepening(MinMaxNode root) {
 
  // Create MoveValuePairs so that we can order Actions
  List<MoveValuePair> actions = buildAVPList(root.getBoard().getCurrentPossibleSquares());
  checkedNodes = 0; mtdCalls = 0; cacheHits=0; leafNodes = 0;
 
  int d;
  for (d = 1; d < maxdepth; d++) {
   int alpha = LOSE; int beta = WIN; int actionsExplored = 0;
   for (MoveValuePair a : actions) {
    MinMaxNode n;
    try {
     n =  root.ApplyMove(a.action);
     
     int value;
     if (USE_MTDF)
      value = MTDF(n, (int) a.value, d);
     else {
      int flag = maximizer ? 1 : -1;
      value = -AlphaBetaWithMemory(n, -beta , -alpha, d - 1, -flag);
     }
     actionsExplored++;
     // Store the computed value for move ordering
     a.value = value;
 
    }  catch (OutOfTimeException e) {
 
     boolean resetBest = true;
     if (actionsExplored > 1) {// If we have looked at more than one possible action
      MoveValuePair bestAction = actions.get(0);
      // check to see if the best action is worse than another action
      for (int i=0; i < actionsExplored; i++) {
       if (bestAction.value < actions.get(i).value) {
        // don't reset the first choice
        resetBest = false;
        break;
       }
      }
     }
    
     if (resetBest) {
      for (MoveValuePair ac: actions) {
       ac.value = ac.previousValue;
      }
     } else {
      for (int i=1; i < actionsExplored; i++) {
       actions.get(i).value = actions.get(i).previousValue;
      }
     }
     break;
    }
   }
   // Sort the actions for move ordering on the next iteration
   Collections.sort(actions, Collections.reverseOrder());
  
   // And update the previous value field
   for (MoveValuePair a: actions) {
    a.previousValue = a.value;
   }
  
  //  System.out.printf("time left : %2.2f",0.001*(System.currentTimeMillis() - startTimeMillis));
  //  System.out.println("current depth : " + d );
  
   if (times_up()) {
     break;
   }
  }
 
  SearchStatistics s = new SearchStatistics();
  s.nodesEvaluated = leafNodes;
  s.timeSpent = (int) (System.currentTimeMillis() - startTimeMillis);
  s.searchDepth = d;
  statsList.add(s);
 
  double nodesPerSec = (1000.0*s.nodesEvaluated) / s.timeSpent;
  double EBF = Math.log(s.nodesEvaluated)/Math.log(s.searchDepth);
  double searchEfficiency = (1.0 * leafNodes) / checkedNodes;
  
 // System.out.printf("NPS:%.2f EBF:%.2f eff:%.2f\n", nodesPerSec, EBF, searchEfficiency);
// System.out.println("Cache hits:"+cacheHits);
  
  // System.out.println("Available actions:"+actions);
  return getRandomBestAction(actions);
}
/**
  * Helper to check if we are out of time for our search
  *
  * @return true if we are out of time, false otherwise
  */
private boolean times_up() {
     long tleft = (System.currentTimeMillis() - startTimeMillis) ;
  return tleft > (searchTime - 10);
}
/**
  * Main MTD(f) search algorithm which recursively uses alpha-beta with
  * varying bounds to compute the minimax value of the root
  *
  * @param root
  *            The root of the search tree
  * @param firstGuess
  *            An initial guess as to what the minimax value could be
  * @param depth
  *            The maximum depth to search
  * @return The best guess for the minimax value given the available search
  *         depth
  * @throws OutOfTimeException
  *             If we ran out of search time during the search.
  */
private int MTDF(MinMaxNode root, int firstGuess, int depth)
   throws OutOfTimeException {
  mtdCalls++;
  int g = firstGuess;
  int beta;
  int upperbound = WIN;
  int lowerbound = LOSE;
  int flag = maximizer ? 1 : -1;
  while (lowerbound < upperbound) {
   if (g == lowerbound) {
    beta = g + 1;
   } else {
    beta = g;
   }
   // Traditional NegaMax call, just with different bounds
   g = -AlphaBetaWithMemory(root, beta - 1, beta, depth, -flag);
   if (g < beta) {
    upperbound = g;
   } else {
    lowerbound = g;
   }
  }
  return g;
}
 
 /**
  * Implementation of NegaMax with Alpha-Beta pruning and transposition-table
  * lookup
  *
  * @param state
  *            The State we are currently parsing.
  * @param alpha
  *            The alpha bound for alpha-beta pruning.
  * @param beta
  *            The beta bound for alpha-beta pruning.
  * @param depth
  *            The current depth we are at.
  * @param maximize
  *            Are we maximizing? If not, we are minimizing.
  * @return The best point count we can get on this branch of the state space
  *         to the specified depth.
  * @throws OutOfTimeException
  *             If we ran out of time during the search
  */
@SuppressWarnings( { "rawtypes", "unchecked" })
private int AlphaBetaWithMemory(MinMaxNode state, int alpha, int beta,
   int depth, int color) throws OutOfTimeException {
  /**
   * If we are not at a low depth (have at least more recursive calls
   * below us) then we are called infrequently enough that we can afford
   * to check if we are out of time
   */
  if (depth > 4) {
   if (times_up())
    throw new OutOfTimeException();
  }
  // Note that we checked a new node
  checkedNodes++;
 
  // Has this state already been computed?
  SearchNode node = transpositionTable.get(state.brd);
 
  if (node != null && node.depth >= depth) {
 
   cacheHits++;
   switch (node.type) {
   case EXACT_VALUE:
    return node.value;
    case UPPERBOUND:
    if (node.value > alpha)
     alpha = node.value;
    break;
   case LOWERBOUND:
    if (node.value < beta)
     beta = node.value;
    break;
   }
  }
  // Is this state/our search done?
  if (depth == 0 || state.getBoard().isComplete() ) {
   int h;
   leafNodes++;
   if (useAltHeuristic)
    h = (int) state.getScore();
   else
    h = (int) state.getScore();
   int value = color * h;
   return saveAndReturnState(state, alpha, beta, depth, value, color);
  }
  int bestValue = LOSE;
  // Partial move ordering. Check value up to depth D-3 and order by that
  int[] depthsToSearch;
  if (depth > 4) {
   depthsToSearch = new int[2];
   depthsToSearch[0] = depth - 2; // TODO: this should be easily adjustable
   depthsToSearch[1] = depth;
  } else {
   depthsToSearch = new int[1];
   depthsToSearch[0] = depth;
  }
  List<MoveValuePair> actions = buildAVPList(state.getBoard().getCurrentPossibleSquares());
  // Do our shorter depth search first to order moves on the longer search
  for (int i = 0; i < depthsToSearch.length; i++) {
   for (MoveValuePair a : actions) {
    int newValue;
    try {
    
        MinMaxNode childState = state.ApplyMove(a.action);
     // Traditional NegaMax call
     newValue = -AlphaBetaWithMemory(childState, -beta, -alpha,
       depthsToSearch[i] - 1, -color);
     // Store the value in the ActionValuePair for action ordering
     a.value = newValue;
    } catch (Exception e) {
     throw new RuntimeException("Invalid action!");
    }
    if (newValue > bestValue)
     bestValue = newValue;
    if (bestValue > alpha)
     alpha = bestValue;
    if (bestValue >= beta)
     break;
   }
   // Sort the actions to order moves on the deeper search
   Collections.sort(actions, Collections.reverseOrder());
  }
  return saveAndReturnState(state, alpha, beta, depth, bestValue, color);
}
/**
  * Helper to save a given search state to the transposition table
  *
  * @param state
  *            The node to act as a key to this search node
  * @param alpha
  *            The current alpha bound
  * @param beta
  *            The current beta bound
  * @param depth
  *            The current search depth
  * @param value
  *            The computed value of the state
  * @param color
  *            The current "color" i.e. whether we are maximizing the
  *            heuristic or the negative heuristic
  * @return The value that we stored
  */
private int saveAndReturnState(MinMaxNode state, int alpha, int beta, int depth,
   int value, int color) {
  // Store so we don't have to compute it again.
  SearchNode saveNode = new SearchNode();
  if (value <= alpha) {
   saveNode.type = EntryType.LOWERBOUND;
  } else if (value >= beta) {
   saveNode.type = EntryType.UPPERBOUND;
  } else {
   saveNode.type = EntryType.EXACT_VALUE;
  }
  saveNode.depth = depth;
  saveNode.value = value;
// transpositionTable.put(state, saveNode);
  return value;
}
/**
  * Helper to create a list of ActionValuePairs with value of 0 from a list
  * of actions
  *
  * @param set
  *            The actions to convert
  * @return A list of actionvaluepairs
  */
private List<MoveValuePair> buildAVPList(Set<Square> set) {
  List<MoveValuePair> res = new ArrayList<MoveValuePair>();
  for (Square a : set) {
   MoveValuePair p = new MoveValuePair(a, 0);
   res.add(p);
  }
  return res;
}
/**
  * Returns a random action from among the best actions in the given list
  * NOTE: this assumes the list is already sorted with the best move first,
  * and that the list is nonempty!
  *
  * @param actions
  *            The actions to examine
  * @return The selected action
  */
 
 private Square getRandomBestAction(List<MoveValuePair> actions) {
  List<Square> bestActions = new LinkedList<Square>();
  int bestV = (int) actions.get(0).value;
  for (MoveValuePair avp : actions) {
    if (avp.value != bestV)
     break;
   bestActions.add(avp.action);
  }
  // Collections.sort( bestActions, Collections.reverseOrder());
  return bestActions.get(0);
}
 
 
 
 public void printSearchStatistics() {
  double avgNodesPerSec = 0; double avgEBF = 0;
  for (SearchStatistics s: statsList) {
   double nodesPerSec = (1000.0*s.nodesEvaluated) / s.timeSpent;
   avgNodesPerSec += nodesPerSec;
   double EBF = Math.log(s.nodesEvaluated)/Math.log(s.searchDepth);
   avgEBF += EBF;
  }
 
  avgNodesPerSec /= statsList.size();
  avgEBF /= statsList.size();
 
  System.out.printf("Average Nodes Per Second:%.2f\n", avgNodesPerSec);
  System.out.printf("Average EBF:%.2f\n", avgEBF);
}
 
   
   public class MoveValuePair implements Comparable<MoveValuePair> {
    Square action;
   MoveValuePair principalVariation;
   int value, previousValue;
  
   public MoveValuePair(Square a, int v) {
    this.action = a;
    this.value = v;
    this.previousValue = 0;
    this.principalVariation = null;
   }
   @Override
   public int compareTo(MoveValuePair other) {
    return Float.compare(this.value, other.value);
   }
  
   @Override
   public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(Action : ");
    sb.append(action);
    MoveValuePair pv = this.principalVariation;
    while (pv.action != null) {
     sb.append("->");
     sb.append(pv.action);
     pv = pv.principalVariation;
    }
    sb.append(" Value: " + value + ")");
    return sb.toString();
   }
  
  }
  
  
}
 
class MinMaxNode implements Comparable<MinMaxNode>, Cloneable {
 
    Player player;
    private Board board;
    public MinMaxNode parent;
    public List<MinMaxNode> childern;
    public Square move;
    public double utility;
    public  int[][] brd = new int[8][8];
    public  Player[][] otherbrd = new Player[8][8];
    private int hashValue;
    int mypieces = 0;
    int otherpieces = 0;
// Lookup table storing the random long corresponding to each state of each square on the board
  private  int[][][] zobristHashes = new int[8][8][3];
 
  /**
  * The static constructor to generate our tables.
  */
 
  
    
    public MinMaxNode(){
        childern = new ArrayList<MinMaxNode>();
    }
    public MinMaxNode(MinMaxNode parent, Square move, Board board){
        childern = new ArrayList<MinMaxNode>();
        this.parent = parent;
        this.move = move;
        this.board = board;
        generateHashValues();
        computeHashValue();
    }
    public double getScore(){
     /*
        Player[][] brd = new Player[8][8];
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++)  {
                brd[i][j] = null;
            }
       for(Square s :board.getSquareOwners().keySet()){
          Player p =  board.getSquareOwners().get(s);
           brd[s.getRow()][s.getColumn()] = p;
       }
       */
    //    UtilityFunction func = new UtilityFunction();
    //    Double utility =   func.heuristic_evaluation_function(otherbrd, player, board);
      
   //     return   utility;
    
     return simpleheuristic(otherbrd, player, board);
     // Map<Player, Integer> mp =board.getPlayerSquareCounts();
    //  return mp.get(player);
    }
   
    public double simpleheuristic(Player[][] grid, Player currentPlayer, Board board){
      int my_tiles = 0, opp_tiles = 0, i, j, k, my_front_tiles = 0, opp_front_tiles = 0, x, y;
      double p = 0, c = 0, l = 0, m = 0, f = 0, d = 0;
     
       int X1[] = {-1, -1, 0, 1, 1, 1, 0, -1};
          int Y1[] = {0, 1, 1, 1, 0, -1, -1, -1};
          int[][] V = {
                      {20, -3, 11, 8, 8, 11, -3, 20},
                      {-3, -7, -4, 1, 1, -4, -7, -3},
                      {11, -4, 2, 2, 2, 2, -4, 11},
                       {8, 1, 2, -3, -3, 2, 1, 8},
                       {8, 1, 2, -3, -3, 2, 1, 8},
                       {11, -4, 2, 2, 2, 2, -4, 11},
                      {-3, -7, -4, 1, 1, -4, -7, -3},
                      {20, -3, 11, 8, 8, 11, -3, 20}};
     
     // Piece difference, frontier disks and disk squares
          /*
         for(i=0; i<8; i++)
             for(j=0; j<8; j++)  {
                 if(grid[i][j] == currentPlayer)  {
                     d += V[i][j];
                     my_tiles++;
                 } else if(grid[i][j] != null)  {
                     d -= V[i][j];
                     opp_tiles++;
                 }
                 if(grid[i][j] != null)   {
                     for(k=0; k<8; k++)  {
                         x = i + X1[k]; y = j + Y1[k];
                         if(x >= 0 && x < 7 && y >= 0 && y < 7 && grid[x][y] == null) {
                             if(grid[i][j] == currentPlayer)  my_front_tiles++;
                             else opp_front_tiles++;
                             break;
                         }
                     }
                 }
             }
             */
    //     if(my_tiles > opp_tiles)
    //         p = (100.0 * my_tiles)/(my_tiles + opp_tiles);
   //      else if(my_tiles < opp_tiles)
    //        p = -(100.0 * opp_tiles)/(my_tiles + opp_tiles);
    //     else p = 0;
 
    //     if(my_front_tiles > opp_front_tiles)
    //         f = -(100.0 * my_front_tiles)/(my_front_tiles + opp_front_tiles);
  //       else if(my_front_tiles < opp_front_tiles)
   //          f = (100.0 * opp_front_tiles)/(my_front_tiles + opp_front_tiles);
     //    else f = 0;
         
          
         
          my_tiles = mypieces;
          opp_tiles =  otherpieces;
         if(my_tiles > opp_tiles)
                 p = (100.0 * my_tiles)/(my_tiles + opp_tiles);
       //    else if(my_tiles < opp_tiles)
      //      p = -(100.0 * opp_tiles)/(my_tiles + opp_tiles);
             else p = 0;
    
          
       // Corner occupancy
          my_tiles = opp_tiles = 0;
          if(grid[0][0] == currentPlayer) my_tiles++;
          else if(grid[0][0] != null) opp_tiles++;
          if(grid[0][7] == currentPlayer) my_tiles++;
          else if(grid[0][7] != null) opp_tiles++;
          if(grid[7][0] == currentPlayer) my_tiles++;
          else if(grid[7][0] != null) opp_tiles++;
          if(grid[7][7] == currentPlayer) my_tiles++;
          else if(grid[7][7] != null) opp_tiles++;
          c = 25 * (my_tiles - opp_tiles);
 
     
      // Corner closeness
             my_tiles = opp_tiles = 0;
             if(grid[0][0] == null)   {
                 if(grid[0][1] == currentPlayer) my_tiles++;
                 else if(grid[0][1] != null) opp_tiles++;
                 if(grid[1][1] == currentPlayer) my_tiles++;
                 else if(grid[1][1] != null) opp_tiles++;
                 if(grid[1][0] == currentPlayer) my_tiles++;
                 else if(grid[1][0] != null) opp_tiles++;
             }
             if(grid[0][7] == null)   {
                 if(grid[0][6] == currentPlayer) my_tiles++;
                 else if(grid[0][6] != null) opp_tiles++;
                 if(grid[1][6] == currentPlayer) my_tiles++;
                 else if(grid[1][6] != null) opp_tiles++;
                 if(grid[1][7] == currentPlayer) my_tiles++;
                 else if(grid[1][7] != null) opp_tiles++;
             }
             if(grid[7][0] == null)   {
                 if(grid[7][1] == currentPlayer) my_tiles++;
                 else if(grid[7][1] != null) opp_tiles++;
                 if(grid[6][1] == currentPlayer) my_tiles++;
                 else if(grid[6][1] != null) opp_tiles++;
                 if(grid[6][0] == currentPlayer) my_tiles++;
                 else if(grid[6][0] != null) opp_tiles++;
             }
             if(grid[7][7] == null)   {
                 if(grid[6][7] == currentPlayer) my_tiles++;
                 else if(grid[6][7] != null) opp_tiles++;
                 if(grid[6][6] == currentPlayer) my_tiles++;
                 else if(grid[6][6] != null) opp_tiles++;
                 if(grid[7][6] == currentPlayer) my_tiles++;
                 else if(grid[7][6] != null) opp_tiles++;
             }
             l = -12.5 * (my_tiles - opp_tiles);
            
            
             
             double score = (10 * p) + (801.724 * c) + (382.026 * l) + (78.922 * m) + (74.396 * f) + (10 * d);
             return score;
    }
    public List<MinMaxNode> expand(){
        Set<Square> moves = board.getCurrentPossibleSquares();
        for(Square aMove: moves)
        {
          
            // Make a copy of the game board.
            // Make a possible prospective move.
            Board newb =    board.play(aMove);
            MinMaxNode aNode = new MinMaxNode( this, aMove, newb);
            //String boardstr =  newb.toString();
            Player playa = newb.getCurrentPlayer();
            aNode.player = playa;
            aNode.board = newb;
            this.childern.add(aNode);
 
        }
        board = null;
        return this.childern;
    }
   
    public  MinMaxNode ApplyMove(Square aMove){
      
            
            // Make a copy of the game board.
            // Make a possible prospective move.
            Board newb =    board.play(aMove);
            MinMaxNode aNode = new MinMaxNode( this, aMove, newb);
            //String boardstr =  newb.toString();
            Player playa = newb.getCurrentPlayer();
            aNode.player = playa;
            aNode.board = newb;
             return aNode;
 
    }
   
    private void computeHashValue() {
    
  int hc = 0;
 
          for(int i=0; i<8; i++)
              for(int j=0; j<8; j++)  {
                  brd[i][j] = 0;
                  otherbrd[i][j] = null;
              }
         for(Square s : board.getSquareOwners().keySet()){
            Player p =  board.getSquareOwners().get(s);
            if(p == Player.WHITE){
             brd[s.getRow()][s.getColumn()] = 1;
            
            }
            else{
              brd[s.getRow()][s.getColumn()] = 2;
            }
            otherbrd[s.getRow()][s.getColumn()] = p;
            if(p == player){
             ++mypieces;
            }
            else{
             ++otherpieces;
            }
         }
        
         /*
        
  //00 = empty; 01 = ignored; 10 = white; 11 = black.
  for (int i=0; i < 8; i++) {
   for (int j=0; j < 8; j++) {
    // get the value from the board
   
    // and convert it into 0, 1 or 2 (the state as an index into the zobristHash table
    int state = 0;
    switch (brd[i][j]) {
    case 0:
     state = 0;
     break;
    case 1:
     state = 1;
     break;
    case 2:
     state = 2;
     break;
    default:
     break;
    }
   
    if (i==0 && j==0) {
     hc = zobristHashes[i][j][state];
    } else {
     hc ^= zobristHashes[i][j][state];
    }
   }
  }
  
     */
    
  hashValue = 0; //board.hashCode();
}
   
    
    private  void generateHashValues() {
  /*
  Random r = new Random(45111);
  for (int i=0; i < 8; i++) {
   for (int j=0; j < 8; j++) {
    for (int state=0; state < 3; state++) {
     zobristHashes[i][j][state] = r.nextInt();
    }
   }
  }
  */
}
   
    @Override
public int hashCode() {
 
   return hashValue;
 
  
 }
   
    @Override
public Object clone() {
    
  MinMaxNode state = new MinMaxNode(parent, move, board);
  return state;
}
@Override
public int compareTo(MinMaxNode arg0) {
 
  if (this.move.getRow() != arg0.move.getRow()  || this.move.getColumn() != arg0.move.getColumn()) return -1;
  if(Arrays.equals(this.brd, arg0.brd)){
   return 0;
  }
  else{
   return -1;
  }
}
 
 @Override
public boolean equals(Object other) {
  MinMaxNode state = (MinMaxNode)other;
  if (this.move.getRow() != state.move.getRow()  || this.move.getColumn() != state.move.getColumn()) return false;
  return Arrays.equals(this.brd, state.brd);
}
 
 public Board getBoard(){
  return board;
}
}
 
}