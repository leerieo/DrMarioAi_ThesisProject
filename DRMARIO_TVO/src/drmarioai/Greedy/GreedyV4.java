package drmarioai.Greedy;
import java.util.Queue;

import drmarioai.Playfield;

import java.lang.Math;


import static drmarioai.Bottle.*;

public class GreedyV4 {

    private int orientation = PILL_ORIG; 
    private int col_idx = -1; 
    public void processChecks(int pill_l, int pill_r, Playfield playfield, ItemColumn[] top_items){
        col_idx = -1; 
        

        if(pill_l == pill_r){
            orientation = PILL_ORIG; 
            int min_depth = 17; 
            // If there is a horizontal match 
            for(int x=0; x<WIDTH-1; x++){
                int d_l = playfield.top_item_depths[x]; 
                int d_r = playfield.top_item_depths[x+1]; 
                int depth = Math.min(d_l,d_r); 

                //the pill must be able to be placed in that location 
                if(depth>0){
                    int row = depth-1; 
                    boolean left_clear = false; 
                    boolean right_clear = false; 

                    //check left 
                    if(x>=2){
                        int left_1 = playfield.playfield[row][x-1]&COLOR_MASK; 
                        int left_2 = playfield.playfield[row][x-2]&COLOR_MASK; 
                        // the two tiles to the left are the same color as the pill 
                        if(left_1==pill_l && left_1==left_2){
                            left_clear = true; 
                        }
                    }
                    //check right 
                    if(x<(WIDTH-3)){
                        int right_1 = playfield.playfield[row][x+2]&COLOR_MASK; 
                        int right_2 = playfield.playfield[row][x+3]&COLOR_MASK; 
                        // the two tiles to the left are the same color as the pill 
                        if(right_1==pill_l && right_1==right_2){
                            right_clear = true; 
                        }

                    }
                    if(left_clear || right_clear){
        
                        if(depth<min_depth){
                            col_idx = x; 
                            min_depth = depth; 
                        }
                    }
                }
            }

            if(col_idx>=0){
                // System.out.println("TEST: "+col_idx);
                System.out.println("HORIZONTAL CLEAR");
                orientation = PILL_ORIG; 
                return; 
            }
            orientation = PILL_ORIG; 
            min_depth = 17; 
            for(int x=0; x<WIDTH-1; x++){
                int d_l = playfield.top_item_depths[x]; 
                int d_r = playfield.top_item_depths[x+1]; 
                int depth = Math.min(d_l,d_r); 

                //the pill must be able to be placed in that location 
                if(depth>0){
                    int c_l = playfield.playfield[d_l][x]&COLOR_MASK; 
                    int c_r = playfield.playfield[d_r][x+1]&COLOR_MASK; 

                    // the colors match the pill and are the same color 
                    if(c_l==pill_l && c_l==c_r){

                        // one of the columns has a virus 
                        if(playfield.col_has_virus[x] || playfield.col_has_virus[x+1]){
                            System.out.println("TEST TTEST 1 "+reachesTop(orientation, depth));
                            //if the new depth does NOT touch the top of the bottle
                            if(!reachesTop(orientation, depth)){
                                // if neither are hanging pills
                                if(!playfield.is_hanging_pill[x] && !playfield.is_hanging_pill[x+1]){
                                    if(depth<min_depth){
                                        col_idx = x; 
                                        min_depth = depth; 
                                    }
                                }else if(playfield.top_items_three_match[x]){
                                    //if left side can be cleared 
                                    if(depth<min_depth){
                                        col_idx = x; 
                                        min_depth = depth; 
                                    }
                                }else if(playfield.top_items_three_match[x+1]){
                                    //if right side can be cleared 
                                    if(depth<min_depth){
                                        col_idx = x; 
                                        min_depth = depth; 
                                    }
                                }
                            }else{
                                //if the new depth does touch the top of the bottle check if both items can be cleared vertically 
                                if(playfield.top_items_three_match[x] && playfield.top_items_three_match[x+1]){
                                    if(depth<min_depth){
                                        col_idx = x; 
                                        min_depth = depth; 
                                    }
                                }
                            }
                        }
                    }

                }
            }

            if(col_idx>=0){
                System.out.println("HORIZONTAL MATCH");
                orientation = PILL_ORIG; 
                return; 
            }

            orientation = PILL_90_C; 

            col_idx = TVO_Utils.Vertical_1_Match_With_Virus_Not_Hanging(pill_l, pill_r, top_items); 
            if(col_idx>=0){
                return; 
            }

            col_idx = TVO_Utils.Vertical_1_Match_Not_Hanging(pill_l, pill_r, top_items);
            if(col_idx>=0){
                return; 
            }

            col_idx = TVO_Utils.Vertical_1_Match_With_Virus(pill_l, pill_r, top_items); 
            if(col_idx>=0){
                return; 
            }

            col_idx = TVO_Utils.Vertical_1_Match(pill_l,pill_r,top_items);

            if(col_idx>=0){
                return; 
            }

            col_idx = TVO_Utils.Vertical_1_Non_Match(pill_l, pill_r, top_items); 
            

        }else{

            int[] idx_orientation; 

            idx_orientation = TVO_Utils.Horizontal_Perfect_2_Match_with_Virus_Not_Hanging(pill_l, pill_r, top_items);
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return; 
            }

            idx_orientation = TVO_Utils.Vertical_Half_Color_Complete_Fall(pill_l, pill_r, top_items); 
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return; 
            }

            idx_orientation = TVO_Utils.Horizontal_Perfect_2_Match_Not_Hanging(pill_l, pill_r, top_items);
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return; 
            }

            idx_orientation = TVO_Utils.Horizontal_Perfect_2_Match(pill_l, pill_r, top_items);
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return; 
            }

            idx_orientation = TVO_Utils.Horizontal_Half_2_Match_With_Virus_Not_Hanging(pill_l, pill_r, top_items); 
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return; 
            }
            

            idx_orientation = TVO_Utils.Horizontal_Half_2_Match_With_Virus(pill_l, pill_r, top_items); 
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return; 
            }

            idx_orientation = TVO_Utils.Horizontal_Half_2_Match(pill_l, pill_r, top_items); 
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return; 
            }

            orientation = PILL_ORIG; 
            col_idx = TVO_Utils.Horizontal_2_Non_Match(pill_l, pill_r, top_items); 
        }

    }


    public Queue<String> ai(int pill_l, int pill_r,  Playfield playfield, ItemColumn[] top_items){
        processChecks(pill_l, pill_r, playfield, top_items);
        return TVO_Utils.getMoveQueue(col_idx, orientation); 
    }

    // returns true if the pill will touch the top of the bottle 
    private boolean reachesTop(int orientation, int row){
        if(orientation%2==0){
            //horizontal 
            return ((row-1)<=0);
        }else{
            //vertical 
            return ((row-2)<=0); 
        }
    }
    
    
}
