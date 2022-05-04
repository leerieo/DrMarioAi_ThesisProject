package drmarioai.Greedy_BST;
import java.util.Queue;

import drmarioai.Placement;
import drmarioai.Playfield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static drmarioai.Bottle.*;

public class Greedy_BST_V2 {

    private final GreedyBSTUtils u = new GreedyBSTUtils();

    public Queue<String> ai(int pill_l, int pill_r, Playfield playfield){

        Queue<String> moveQueue = new LinkedList<String>();

        ArrayList<Placement> placements = u.getAllLandings(playfield.playfield); 

        if(placements.size()>0){
            double max_score = -Double.MAX_VALUE; 
            Placement best_placement = placements.get(0); 
            for(int i=0; i<placements.size(); i++){
                int row = placements.get(i).getRow(); 
                int col = placements.get(i).getCol();
                int orientation = placements.get(i).getOrientation(); 
    
                // System.out.println("ROW: "+row+" COL: "+col+" ORIEN: "+orientation); 
                // System.out.println("MOVELIST: "+placements.get(i).move_list.toString());
                playfield.placeVitamin(pill_l, pill_r, row, col, orientation);
    
                double ver_score = scorePlacementVer(playfield, row, col, orientation,false); 
                double hor_score = scorePlacementHor(playfield, row, col, orientation); 
    
                if(ver_score>max_score){
                    System.out.println("ver_score: "+ver_score);
                    playfield.printPlayfield();
                    max_score = ver_score; 
                    best_placement = placements.get(i); 
                }else if(hor_score>max_score){
                    System.out.println("hor_score: "+hor_score);
                    playfield.printPlayfield();
                    max_score = hor_score; 
                    best_placement = placements.get(i);
                }

                // playfield.printPlayfield();
                playfield.removeVitamin(row, col, orientation);
            }
            System.out.println("");
            System.out.println("BEST SCORE: "+max_score);
            System.out.println("BEST POS: "+Arrays.toString(best_placement.pos));
            System.out.println("BEST MOVE LIST: "+best_placement.move_list.toString());
            
            
            // int d_counter = 0; 
            for(int i=0; i<best_placement.move_list.size(); i++){
                moveQueue.add(best_placement.move_list.get(i)); 
            }
            
            System.out.println("BEST MOVE QUEUE: "+moveQueue.toString()); 
        }

        return moveQueue; 

    }


    // Returns the length of the matching sequence after the given tile at row,col
    // If there is no matching sequence the length is 0 
    private int getMatchingSeqLength(int[][] playfield, int row, int col, int color){
        boolean start_seq = false; 
        int seq_count = 0; 
        for(int i=(row+1); i<HEIGHT; i++){
            int tile = playfield[i][col];
            //if we encounter a nonmatching item 
            if(tile!=EMPTY_TILE && (tile&COLOR_MASK)!=color){
                return seq_count; 
            }
            //if we encounter an empty tile after the sequence has started 
            if(tile==EMPTY_TILE && start_seq){
                return seq_count; 
            }
            // if we encounter a matching item 
            if((tile&COLOR_MASK)==color){
                start_seq = true; // start the sequence count 
                seq_count++; 
            }
        }
        return seq_count; 
    }

    private int getSecondItemBeneath(int[][] playfield, int row, int col){
        boolean found_first = false; 
        int first_col = EMPTY_ID; 

        for(int i=(row+1); i<HEIGHT; i++){
            int tile = playfield[i][col];
            //if we encounter a nonmatching item after finding the first tile 
            if(found_first && tile!=EMPTY_TILE && (tile&COLOR_MASK)!=first_col){
                return tile; 
            }

            //if we encounter an empty tile after the sequence has started 
            if(tile==EMPTY_TILE && found_first){
                first_col = EMPTY_ID; // gaps break the sequence 
            }

            // if we encounter the first non empty item 
            if(!found_first && tile!=EMPTY_TILE){
                found_first = true; // start the sequence count 
                first_col = tile&COLOR_MASK; 
            }
        }

        return EMPTY_TILE; 
    }

    // Returns the item value of the first non-empty tile after the given tile at row,col
    // If there is non non-empty tile after, returns EMPTY_TILE 
    private int getNextItemBeneath(int[][] playfield, int row, int col){
        for(int i=(row+1); i<HEIGHT; i++){
            int tile = playfield[i][col];
            if(tile != EMPTY_TILE){
                return tile; 
            }
        }
        return EMPTY_TILE; 
    }


    private double scorePlacementVer(Playfield playfield, int row, int col, int orientation, boolean print){

        double score = 0; 
        boolean place_high = false;

        if(orientation%2==0){

            //horizontal 
            int color_l = playfield.playfield[row][col] & COLOR_MASK; 
            int color_r = playfield.playfield[row][col+1] & COLOR_MASK; 

            boolean left_match = false; 
            boolean right_match = false; 

            // check the tiles below the vitamin 
            if(row<HEIGHT-1){

                int tile_l = getNextItemBeneath(playfield.playfield, row, col); 
                // if the left below tile is the same color +1 
                if((tile_l&COLOR_MASK) == color_l){
                    score++; 
                    // if the left column has a virus +1
                    if(playfield.col_has_virus[col]){
                        score++; 
                    }
                    // left side is a match 
                    left_match = true; 
                }else if(tile_l!=EMPTY_TILE){
                    //punish non-matches in columns with viruses 
                    if(playfield.col_has_virus[col]){
                        score--; 
                    }
                }
                
                // check below right vitamin half 
                int tile_r = getNextItemBeneath(playfield.playfield, row, col+1); 
                // if the right below tile is the same color +1 
                if((tile_r&COLOR_MASK) == color_r){
                    score++; 

                    // if the right column has a virus +1
                    if(playfield.col_has_virus[col+1]){
                        score++; 
                    }
                    //right side match is true 
                    right_match = true; 
                }else if(tile_r!=EMPTY_TILE){
                    //punish non-matches in columns with viruses 
                    if(playfield.col_has_virus[col]){
                        score--; 
                    }
                }

                if(print){
                    System.out.println("tile_l: "+String.format("%x ",tile_l));
                    System.out.println("tile_r: "+String.format("%x ",tile_r));
                }
            }

            // if items beneath both the left and right vitamin are matching 
            // we want to place the vitamin high in the bottle 
            if(left_match && right_match){
                place_high = true; 
            }

            //bonus points for matching above items 
            if(row>0){
                int tile_l = playfield.playfield[row-1][col];
                if((tile_l&COLOR_MASK) == color_l){
                    score+=0.5; 
                }
                
                int tile_r = playfield.playfield[row-1][col+1];
                if((tile_r&COLOR_MASK) == color_r){
                    score+=0.5; 
                }
            }

            
            // check if the placement blocks the top 
            if(row==0){
                int depth_l = playfield.top_item_depths[col]; 
                int depth_r = playfield.top_item_depths[col+1]; 

                if(depth_l==1){
                    // if there is a non match
                    // or item sequence beneath the vitamin that can't be completed
                    // punish severely 
                    if(!left_match || !playfield.top_items_three_match[col]){
                        score-=10; 
                        place_high = false; 
                    }
                }
                if(depth_r==1){
                    // punish severely 
                    if(!right_match || !playfield.top_items_three_match[col+1]){
                        score-=10; 
                        place_high = false; 
                    }
                }
            }

        }else if(row>0){
            //vertical 
            int color_b = playfield.playfield[row][col] & COLOR_MASK; 
            int color_t = playfield.playfield[row-1][col] & COLOR_MASK; 

            //check tiles below the vitamin if the vitamin is 1 color 
            if(row<HEIGHT-1 && color_b==color_t){
                int tile = getNextItemBeneath(playfield.playfield, row, col); 
                // if the below tile is the same color +1 
                if((tile&COLOR_MASK)==color_b){
                    score++; 
                    // if the right column has a virus +1
                    if(playfield.col_has_virus[col]){
                        score++; 
                    }
                    // place 1 color vitamin matches high in the bottle 
                    place_high = true; 
                }else if(tile!=EMPTY_TILE){
                    //punish non-matches in columns with viruses 
                    if(playfield.col_has_virus[col]){
                        score--; 
                    }
                }


            }else if(color_b!=color_t){
                int match_len = getMatchingSeqLength(playfield.playfield, row, col, color_b);

                // if the bottom vitamin half can clean a sequence +1 
                if(match_len>=3){
                    score++; 
                    // if the column has a virus +1
                    if(playfield.col_has_virus[col]){
                        score++; 
                    }

                    //get the second item beneath 
                    int second_item = getSecondItemBeneath(playfield.playfield, row, col); 

                    // if the top vitamin half falls onto a matching item +1
                    if((second_item&COLOR_MASK)==color_t){
                        score++; 
                        if(playfield.col_has_virus[col]){
                            score++; 
                        }    
                    }else{
                        //punish non-matches in columns with viruses 
                        if(playfield.col_has_virus[col]){
                            score--; 
                        }
                    }

                }else{
                    //punish non-matches in columns with viruses 
                    if(playfield.col_has_virus[col]){
                        score--; 
                    }
                }
            }
            
            if(row>1){
                //bonus points for matching above items 
                int tile_t = playfield.playfield[row-2][col];
                if((tile_t&COLOR_MASK) == color_t){
                    score+=0.5; 
                }
                //bonus points for matching beneath items
                if(row<HEIGHT-1){
                    int tile_b = playfield.playfield[row+1][col];
                    if((tile_b&COLOR_MASK) == color_b){
                        score+=0.5; 
                    }else if(tile_b!=EMPTY_TILE){
                        //remember to punish non matches 
                        if(playfield.col_has_virus[col]){
                            score--; 
                        }
                    }
                }
            }

            //check if placement blocks the top 
            if(row==1){
                if(color_b==color_t){
                    // if not a match or does not complete a sequence 
                    if(!place_high ||!playfield.top_items_two_match[col]){
                        score-=10; 
                        place_high = false; 
                    }
                }else{
                    //punish 2 color vertical vitamin placements 
                    score-=10; 
                    place_high = false; 
                }             
            }

        }else{
            //vertical vitamin placements on the top row
            // This means that only the bottom vitamin half remains in the bottle 
            int color_b = playfield.playfield[row][col] & COLOR_MASK; 
            int tile = playfield.playfield[row+1][col]; 

            // if the below tile is the same color +1 
            if((tile&COLOR_MASK)==color_b){
                score++; 
                if(playfield.col_has_virus[col]){
                    score++; 
                }
                place_high = true; 
            }

            // if not a match or does not complete a sequence 
            if(!place_high || !playfield.top_items_three_match[col]){
                score-=10; 
                place_high=false; 
            }
        }

        // if it is a match we want to place it as high as possible, otherwise place it as low as possible
        if(place_high){
            score += ((double)(HEIGHT-row))/10; 
        }else{
            score+=((double)row)/10; 
        }

        return score; 

    }

    private double scorePlacementHor(Playfield playfield, int row, int col, int orientation){

        int score = 0; 
        boolean place_high = false;

        if(orientation%2==0){

            // horizontal 
            // only score horizontal oriented vitamins 
            int color_l = playfield.playfield[row][col] & COLOR_MASK; 
            int color_r = playfield.playfield[row][col+1] & COLOR_MASK; 

            int num_cleared = 0; 

            boolean match_l = false; 
            boolean match_r = false; 

            //check left of left pill 
            for(int i=(col-1); i>=0; i--){
                int tile = playfield.playfield[row][i]; 
                // same color 
                if((tile&COLOR_MASK)==color_l){
                    num_cleared++; 
                    match_l = true; 
                }else{
                    // for horizontal matches we want the colors to be in-a-row
                    break; 
                }
            }

            //check right of right pill 
            for(int i=(col+2); i<WIDTH; i++){
                int tile = playfield.playfield[row][i]; 
                // same color 
                if((tile&COLOR_MASK)==color_r){
                    num_cleared++; 
                    match_r = true; 
                }else{
                    // for horizontal matches we want the colors to be in-a-row
                    break; 
                }
            }  

            // if the vitamin is 1 color 
            if(color_l==color_r){
                // if we're on the top row 
                //check if placement blocks the top 
                if(row==0 && num_cleared<2){
                    score -= 10; 
                }

                // if there is a completed horizontal clear +1 
                if(num_cleared>=2){
                    place_high = true; 
                    score++;
                    //if the row has a virus +1 
                    if(playfield.row_has_virus[row]){
                        score++; 
                    }
                }else{
                    // punish non matches 
                    score--; 
                }

            }else{ 
                // if we're on the top row 
                //check if placement blocks the top 
                if(row==0 && num_cleared<6){
                    score -= 10; 
                }
                // if there is a completed horizontal clear +1 
                if(num_cleared>=6){
                    place_high = true; 
                    score++;
                    //if the row has a virus +1 
                    if(playfield.row_has_virus[row]){
                        score++; 
                    }
                }else{
                    // punish non matches 
                    score--; 
                }
            }
        }

        // if it is a match we want to place it as high as possible, otherwise place it as low as possible
        if(place_high){
            score += ((double)(HEIGHT-row))/10; 
        }else{
            score+=((double)row)/10; 
        }

        return score; 

    }

    
}
