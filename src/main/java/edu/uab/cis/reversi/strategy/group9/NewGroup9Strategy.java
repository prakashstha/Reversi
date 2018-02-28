package edu.uab.cis.reversi.strategy.group9;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
 

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;
 
public class NewGroup9Strategy implements Strategy {
 
    public static MinMaxNode root;
    public static int Maxdepth = 3;
    @Override
    public Square chooseSquare(Board board) {
 
        root = new MinMaxNode();
        Player playa = board.getCurrentPlayer();
        root.player =  playa;
        root.board = board;
        AlphaBetaSearcher engine = new AlphaBetaSearcher(Maxdepth);
        double result =  engine.eval(root);
       
        for(MinMaxNode child : root.childern)
        {
         if(child.utility == result){
            return child.move;
         }
        }
       
     //   Square move = engine.bestMove.move;
        return root.childern.get(0).move;
    }
 
    @Override
    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
        // TODO Auto-generated method stub
     if(time>900 && unit ==TimeUnit.MILLISECONDS){
      Maxdepth = 3;
     }
     else{
      Maxdepth = 2;
     }
    }
 
    class MinMaxNode  implements Comparable<MinMaxNode> {
        Player player;
        public Board board;
        public MinMaxNode parent;
        public List<MinMaxNode> childern;
        public Square move;
        public double utility;
        public MinMaxNode(){
            childern = new ArrayList<MinMaxNode>();
        }
 
        public MinMaxNode(MinMaxNode parent, Square move){
            childern = new ArrayList<MinMaxNode>();
            this.parent = parent;
            this.move = move;
 
        }
        public double getScore(){
 
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
            return     func.heuristic_evaluation_function(brd, player, board);
        }
       
    
        public List<MinMaxNode> expand(){
            Set<Square> moves = board.getCurrentPossibleSquares();
 
            for(Square aMove: moves)
            {
                MinMaxNode aNode = new MinMaxNode( this, aMove);
                // Make a copy of the game board.
                // Make a possible prospective move.
                Board newb =    board.play(aMove);
                Player playa = newb.getCurrentPlayer();
                aNode.player = playa;
                aNode.board = newb;
                this.childern.add(aNode);
 
 
            }
           
            board = null;
            return this.childern;
 
        }
  @Override
  public int compareTo(MinMaxNode arg0) {
   // TODO Auto-generated method stub
   if(this.utility > arg0.utility)
   {
    return 1;
   }
   if(this.utility < arg0.utility)
   {
    return -1;
   }
   return 0;
  }
 
    }
 
    class AlphaBetaSearcher {
 
        private int depthLimit;
        //private int nodeCount;
        MinMaxNode  bestMove = null;
 
        Player originalPlayer;
 
 
        public AlphaBetaSearcher(int depthLimit){
            this.depthLimit = depthLimit;
        }
 
        public double eval(MinMaxNode node ){
            //nodeCount = 0;
            originalPlayer = node.player;
           // return alphaBetaEval(node, depthLimit, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            return Iterate(node, depthLimit, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
       
        public double Iterate(MinMaxNode node, int depthLeft, double alpha, double beta)
        {
         
              boolean maximizing =  false;
             
              if(Maxdepth % 2 == 0){
               maximizing = ( node.player == originalPlayer);
              }
              else
              {
               maximizing = !( node.player == originalPlayer);
              }
             
              if(node.board.isComplete() || depthLeft == 0){
                  bestMove = node;
                  return node.getScore();
                 // return node.utility;
              }
    
            if(maximizing)
            {
                for(MinMaxNode child : node.expand())
                {
                    alpha =  Math.max(alpha, Iterate(child, depthLeft - 1, alpha, beta ));
                    child.utility = alpha;
                    if (beta < alpha)
                    {
                        break;
                    }
    
                }
    
                return alpha;
            }
            else
            {
                for  (MinMaxNode child : node.expand())
                {
                    beta = Math.min(beta, Iterate(child, depthLeft - 1, alpha, beta ));
                    child.utility = beta;
                    if (beta < alpha)
                    {
                        break;
                    }
                }
    
                return beta;
            }
        }
 
        public double alphaBetaEval(MinMaxNode node, int depthLeft, double alpha, double beta){
            MinMaxNode localBestMove = null;
            boolean maximizing =( node.player == originalPlayer);
            //double bestUtility = maximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            //nodeCount ++;
 
            if(node.board.isComplete() || depthLeft == 0){
                bestMove = node;
                return node.getScore();
               // return node.utility;
            }
 
      
            List<MinMaxNode> children = node.expand();
        
            for(MinMaxNode child : children){
          //  double childUtility = child.getScore();
           // child.utility = childUtility;
            }
           
 
            if(maximizing){
          //  Collections.sort(children, Collections.reverseOrder());
            }
            else{
          //   Collections.sort(children);
            }
            for(MinMaxNode child : children){
                if(maximizing) {
                   double childUtility = alphaBetaEval(child, depthLeft - 1, alpha, beta);
                    child.utility = childUtility;
                    if ( child.utility  > alpha) {
                        alpha =  child.utility ;
                        localBestMove = child;
                        if( beta <= alpha){
                                  break ; //(* beta cut-off *)
                        }
                    }
 
                }
                else{
                   double childUtility = alphaBetaEval(child, depthLeft - 1, alpha, beta);
                   child.utility = childUtility;
                    if ( child.utility  < beta) {
                        beta =  child.utility ;
                        localBestMove = child;
                        if( beta <= alpha){
                            break ; //(* alpha cut-off *)
                        }
                    }
                }
 
                // cut-off
              //  if (alpha >= beta) break;
            }
 
            if(node.parent == null){
            bestMove = localBestMove;
            }
            return (maximizing) ? alpha : beta ;
        }
    }
}