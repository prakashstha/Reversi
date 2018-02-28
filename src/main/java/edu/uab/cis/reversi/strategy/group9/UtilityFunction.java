

package edu.uab.cis.reversi.strategy.group9;

 

import edu.uab.cis.reversi.Board;

import edu.uab.cis.reversi.Player;

 

/**

 * Created by godaibo on 4/4/2015.

 */

public class UtilityFunction {

 

   private   boolean canmove(Player self,  Player[] str)  {

        if (str[0] == self || str[0] == null  ) return false;

        for (int ctr = 1; ctr < 8; ctr++) {

            if (str[ctr] == null) return false;

            if (str[ctr] == self) return true;

        }

        return false;

    }

 

    boolean isLegalMove(Player self,  Player[][] grid, int startx, int starty)   {

        if (grid[startx][starty] != null) return false;

        Player[] str = new Player[10];

        int x, y, dx, dy, ctr;

        for (dy = -1; dy <= 1; dy++)

            for (dx = -1; dx <= 1; dx++)    {

                // keep going if both velocities are zero

                if (dy ==0 && dx==0) continue;

                str[0] = null;

                for (ctr = 1; ctr < 8; ctr++)   {

                    x = startx + ctr*dx;

                    y = starty + ctr*dy;

                    if (x >= 0 && y >= 0 && x<8 && y<8) str[ctr-1] = grid[x][y];

                    else str[ctr-1] = null;

                }

                if (canmove(self, str)) return true;

            }

        return false;

    }

 

    int num_valid_moves(Player self, Player[][] grid)   {

        int count = 0, i, j;

        for(i=0; i<8; i++)

            for(j=0; j<8; j++)

                if(isLegalMove(self,  grid, i, j)) count++;

        return count;

    }

 

        public double  heuristic_evaluation_function(Player[][] grid, Player currentPlayer, Board board)  {

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

        if(my_tiles > opp_tiles)

            p = (100.0 * my_tiles)/(my_tiles + opp_tiles);

        else if(my_tiles < opp_tiles)

            p = -(100.0 * opp_tiles)/(my_tiles + opp_tiles);

        else p = 0;

 

        if(my_front_tiles > opp_front_tiles)

            f = -(100.0 * my_front_tiles)/(my_front_tiles + opp_front_tiles);

        else if(my_front_tiles < opp_front_tiles)

            f = (100.0 * opp_front_tiles)/(my_front_tiles + opp_front_tiles);

        else f = 0;

 

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

 

        Player opp;

        if(currentPlayer == Player.BLACK){

            opp = Player.WHITE;

        }

        else{

            opp = Player.BLACK;

        }

// Mobility

        my_tiles = num_valid_moves(currentPlayer, grid);

        opp_tiles = num_valid_moves(opp, grid);

        if(my_tiles > opp_tiles)

            m = (100.0 * my_tiles)/(my_tiles + opp_tiles);

        else if(my_tiles < opp_tiles)

            m = -(100.0 * opp_tiles)/(my_tiles + opp_tiles);

        else m = 0;

 

// final weighted score

     //   double score = (10 * p) + (801.724 * c) + (382.026 * l) + (78.922 * m) + (74.396 * f) + (10 * d);

 

            double score;

           if(   board.getMoves().size() <= 24) {

               // opening strategy

                score = (10 * p) + (600 * c) + (180 * l) + (278 * m) + (274 * f) + (10 * d);

           }

            else if(   board.getMoves().size() <= 40){

               // mid game strategy

               score = (10 * p) + (700 * c) + (280 * l) + (178 * m) + (174 * f) + (10 * d);

           }

            else{

                // late  game strategy

                score = (10 * p) + (800 * c) + (380 * l) + (78 * m) + (74 * f) + (10 * d);

            }

 

            return score;

    }

}