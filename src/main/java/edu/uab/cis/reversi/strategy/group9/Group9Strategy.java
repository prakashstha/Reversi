
package edu.uab.cis.reversi.strategy.group9;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;
 
public class Group9Strategy implements Strategy {
 
    public static MinMaxNode root;
    public static int Maxdepth = 2;
    @Override
    public Square chooseSquare(Board board) {
 
        root = new MinMaxNode();
        Player playa = board.getCurrentPlayer();
        root.player =  playa;
        root.board = board;
        AlphaBetaSearcher engine = new AlphaBetaSearcher();
        engine.eval(root);
        Square move = engine.bestMove.move;
        return move;
    }
 

//    @Override
//    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
//        // TODO Auto-generated method stub
//    	if(time>900 && unit ==TimeUnit.MILLISECONDS)
//    		Maxdepth = 2;
//    }

 
    class MinMaxNode {
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
          //  Map<Player, Integer> mp =board.getPlayerSquareCounts();
         //   return mp.get(player);
        }
 
        public List<MinMaxNode> expand(){
            Set<Square> moves = board.getCurrentPossibleSquares();
            for(Square aMove: moves)
            {
                MinMaxNode aNode = new MinMaxNode( this, aMove);
                // Make a copy of the game board.
                // Make a possible prospective move.
                Board newb =    board.play(aMove);
                //String boardstr =  newb.toString();
                Player playa = newb.getCurrentPlayer();
                aNode.player = playa;
                aNode.board = newb;
                this.childern.add(aNode);
                
 
            }
            board = null;
            return this.childern;
 
        }
 
    }
 
    class AlphaBetaSearcher {
 
       //private int depthLimit;
        //private int nodeCount;
        MinMaxNode  bestMove = null;
 
        Player originalPlayer;
 
 
//        public AlphaBetaSearcher(int depthLimit){
//            this.depthLimit = depthLimit;
//        }
// 
        public double eval(MinMaxNode node ){
            //nodeCount = 0;
            originalPlayer = node.player;
            return alphaBetaEval(node, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
 
        public double alphaBetaEval(MinMaxNode node, int depthLeft, double alpha, double beta){
            MinMaxNode localBestMove = null;
            boolean maximizing =( node.player == originalPlayer);
            //double bestUtility = maximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            //nodeCount ++;
 
            if(node.board.isComplete() || depthLeft == Maxdepth){
                bestMove = node;
                return node.getScore();
            }
 
            List<MinMaxNode> children = node.expand();
 
            for(MinMaxNode child : children){
                if(maximizing) {
                    double childUtility = alphaBetaEval(child, depthLeft + 1, alpha, beta);
                    child.utility = childUtility;
                    if (childUtility > alpha) {
                        alpha = childUtility;
                        localBestMove = child;
                    }
 
                }
                else{
                    double childUtility = alphaBetaEval(child, depthLeft + 1, alpha, beta);
                    child.utility = childUtility;
                    if (childUtility < beta) {
                        beta = childUtility;
                        localBestMove = child;
                    }
                }
 
                // cut-off
                if (alpha >= beta) break;
            }
 
            bestMove = localBestMove;
            return (maximizing) ? alpha : beta ;
        }
    }
}
