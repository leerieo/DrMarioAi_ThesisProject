package SOLVERS.TVO;

import java.lang.Math;

import SOLVERS.Bottle;
import SOLVERS.Placement;
import SOLVERS.Playfield;

public class GreedyV5 {

    // TODO: Need to deprioritize colunmns containing no viruses 

    public Placement ai(int pill_l, int pill_r,  Playfield playfield){

   
        int orientation = Bottle.PILL_ORIG; 

        // This should end as a value btwn 0 and 7
        int col_indx = -1;  
        
        if(pill_l==pill_r){
            // If the pill is the same color 

            //Prioritize matches with the smallest depth 
            int min_depth = 17; 

            // If there is a horizontal match 
            for(int x=0; x<Bottle.WIDTH-1; x++){
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
                        int left_1 = playfield.playfield[row][x-1]&Bottle.COLOR_MASK; 
                        int left_2 = playfield.playfield[row][x-2]&Bottle.COLOR_MASK; 
                        // the two tiles to the left are the same color as the pill 
                        if(left_1==pill_l && left_1==left_2){
                            left_clear = true; 
                        }
                    }
                    //check right 
                    if(x<(Bottle.WIDTH-3)){
                        int right_1 = playfield.playfield[row][x+2]&Bottle.COLOR_MASK; 
                        int right_2 = playfield.playfield[row][x+3]&Bottle.COLOR_MASK; 
                        // the two tiles to the left are the same color as the pill 
                        if(right_1==pill_l && right_1==right_2){
                            right_clear = true; 
                        }

                    }
                    if(left_clear || right_clear){
                        if(depth<min_depth){
                            col_indx = x; 
                            min_depth = depth; 
                        }
                    }
                }
            }
        

            // If there are two columns with matching top colors with viruses (neither are hanging)
            if(col_indx<0){
                min_depth = 17; 
                for(int x=0; x<Bottle.WIDTH-1; x++){
                    int d_l = playfield.top_item_depths[x]; 
                    int d_r = playfield.top_item_depths[x+1]; 
                    int depth = Math.min(d_l,d_r); 

                    //the pill must be able to be placed in that location 
                    if(depth>0){
                        int c_l = playfield.playfield[d_l][x]&Bottle.COLOR_MASK; 
                        int c_r = playfield.playfield[d_r][x+1]&Bottle.COLOR_MASK; 

                        // the colors match the pill and are the same color 
                        if(c_l==pill_l && c_l==c_r){

                            // one of the columns has a virus 
                            if(playfield.col_has_virus[x] || playfield.col_has_virus[x+1]){

                                //if the new depth does NOT touch the top of the bottle
                                if(!reachesTop(orientation, depth)){
                                    // if neither are hanging pills
                                    if(!playfield.is_hanging_pill[x] && !playfield.is_hanging_pill[x+1]){
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            min_depth = depth; 
                                        }
                                    }else if(playfield.top_items_three_match[x]){
                                        //if left side can be cleared 
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            min_depth = depth; 
                                        }
                                    }else if(playfield.top_items_three_match[x+1]){
                                        //if right side can be cleared 
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            min_depth = depth; 
                                        }
                                    }
                                }else{
                                    //if the new depth does touch the top of the bottle check if both items can be cleared vertically 
                                    if(playfield.top_items_three_match[x] && playfield.top_items_three_match[x+1]){
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            min_depth = depth; 
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }

            // Find column with matching top color with viruses (not hanging)
            if(col_indx<0){

                // for every check past this, the pill should be vertical 
                orientation = Bottle.PILL_90_CC; 
                min_depth = 17; 

                for(int x=0; x<Bottle.WIDTH; x++){
               
                    int depth = playfield.top_item_depths[x]; 
                    
                    //the pill must be able to be placed in that location 
                    if(depth>0){
                        int top_item = playfield.playfield[depth][x]; 
                        // Check top color and column has virus and is not hanging 
                        if((top_item&Bottle.COLOR_MASK)==pill_l && playfield.col_has_virus[x] && !playfield.is_hanging_pill[x]){
                            //if the new depth does NOT touch the top of the bottle
                            if(!reachesTop(orientation,depth)){
                                if(depth<min_depth){
                                    col_indx = x; 
                                    min_depth = depth; 
                                }
                            }else{
                                if(depth>1){
                                    //two halves 
                                    if(playfield.top_items_two_match[x]){
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            min_depth = depth; 
                                        }
                                    }
                                }else{
                                    //one half
                                    if(playfield.top_items_two_match[x]){
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            min_depth = depth; 
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }

            // Find column with matching top color (not hanging)
            if(col_indx<0){
                min_depth = 17; 

                for(int x=0; x<Bottle.WIDTH; x++){
                    int depth = playfield.top_item_depths[x];  
                    //the pill must be able to be placed in that location 
                    if(depth>0){
                        int top_item = playfield.playfield[depth][x]; 
                        // Check top color and is not hanging 
                        if((top_item&Bottle.COLOR_MASK)==pill_l && !playfield.is_hanging_pill[x]){
                            //if the new depth does NOT touch the top of the bottle
                            if(!reachesTop(orientation,depth)){
                                if(depth<min_depth){
                                    col_indx = x; 
                                    min_depth = depth; 
                                }
                            }else{
                                if(depth>1){
                                    //two halves 
                                    if(playfield.top_items_two_match[x]){
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            min_depth = depth; 
                                        }
                                    }
                                }else{
                                    //one half
                                    if(playfield.top_items_two_match[x]){
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            min_depth = depth; 
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int max_depth = -1; 

            // Find lowest + leftmost match that does touch the top of the bottle
            if(col_indx<0){

                for(int x=0; x<Bottle.WIDTH; x++){
                    int depth = playfield.top_item_depths[x];  

                    if(depth>0){
                    
                        int top_item = playfield.playfield[depth][x]; 

                        // Check top color 
                        if((top_item&Bottle.COLOR_MASK)==pill_l){
                            //if the new depth does NOT touch the top of the bottle
                            if(!reachesTop(orientation,depth)){
                                if(depth<min_depth){
                                    col_indx = x; 
                                    min_depth = depth; 
                                }
                            }else{
                                if(depth>1){
                                    //two halves 
                                    if(playfield.top_items_two_match[x]){
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            min_depth = depth; 
                                        }
                                    }
                                }else{
                                    //one half
                                    if(playfield.top_items_two_match[x]){
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            min_depth = depth; 
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Find lowest + leftmost 
            if(col_indx<0){
                max_depth = -1; 
                for(int x=0; x<Bottle.WIDTH; x++){
                    int depth = playfield.top_item_depths[x];  
                    if(depth>max_depth){
                        col_indx = x; 
                        max_depth = depth; 
                    }
                }
            }


        }else{
            // If the pill is two different colors 
            
            //Prioritize matches with the smallest depth 
            int min_depth = 17; 

            //Find a vertical match where one half completes a full match so the other half can fall onto a matching color. 
            for(int x=0; x<Bottle.WIDTH; x++){

                int depth = playfield.top_item_depths[x];  

                if(depth>1){
                    
                    // check that the top items are a three-in-a-row match 
                    if(playfield.top_items_three_match[x]){
                        int first_color = playfield.playfield[depth][x]&Bottle.COLOR_MASK; 
                        
                        //two halves 
                        int second_depth = playfield.second_item_depths[x]; 
                        int second_color = playfield.playfield[second_depth][x]&Bottle.COLOR_MASK; 

                        if(pill_l==first_color && pill_r==second_color){
                            if(depth<min_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_90_CC; 
                                min_depth = depth;
                            }
                        }else if(pill_r==first_color && pill_l==second_color){
                            if(depth<min_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_90_C; 
                                min_depth = depth;
                            }
                        }
                       
                    }
                }
            }
                
            if(col_indx<0){
                min_depth = 17; 
                // Find a perfect horizontal match with at least 1 seq containing a virus.
                for(int x=0; x<Bottle.WIDTH-1; x++){

                    int d_l = playfield.top_item_depths[x]; 
                    int d_r = playfield.top_item_depths[x+1]; 

                    int depth = Math.min(d_l,d_r); 

                    if(depth>0){                                    
                        int color_l = playfield.playfield[d_l][x]&Bottle.COLOR_MASK; 
                        int color_r = playfield.playfield[d_r][x+1]&Bottle.COLOR_MASK; 

               
                     
                        // one of the columns has a virus 
                        if(playfield.col_has_virus[x] || playfield.col_has_virus[x+1]){
                            if(pill_l==color_l && pill_r==color_r){
                                if(depth<min_depth){
                                    col_indx = x; 
                                    orientation = Bottle.PILL_ORIG; 
                                    min_depth = depth; 
                                }
                                        
                            }else if(pill_l==color_r && pill_r==color_l){
                                if(depth<min_depth){
                                    col_indx = x; 
                                    orientation = Bottle.PILL_REV; 
                                    min_depth = depth; 

                                }
                            }
                        }
                        
                    }
                }
            }

           //Try to find half match (seq containing a virus) + lowest non-match
           int max_depth = -1; 
            if(col_indx<0){
                for(int x=0; x<Bottle.WIDTH-1; x++){

                    int d_l = playfield.top_item_depths[x]; 
                    int d_r = playfield.top_item_depths[x+1]; 

                    int depth = Math.min(d_l,d_r); 

                    if(depth>0){   

                        int color_l = playfield.playfield[d_l][x]&Bottle.COLOR_MASK; 
                        int color_r = playfield.playfield[d_r][x+1]&Bottle.COLOR_MASK;

                        if(pill_l==color_l && playfield.col_has_virus[x] && !playfield.col_has_virus[x+1]){
                            if(d_r>max_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_ORIG; 
                                max_depth = d_r; 
                            }
                        }else if(pill_r==color_r && playfield.col_has_virus[x+1] && !playfield.col_has_virus[x]){
                            if(d_l>max_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_ORIG; 
                                max_depth = d_l; 
                            }
                        }else if(pill_l==color_r && playfield.col_has_virus[x+1] && !playfield.col_has_virus[x]){
                            if(d_l>max_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_REV; 
                                max_depth = d_l;
                            }
                        }else if(pill_r==color_l && playfield.col_has_virus[x] && !playfield.col_has_virus[x+1]){
                            if(d_r>max_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_REV; 
                                max_depth = d_r; 
                            }
                        }
                
                    }

                }
            } 


            //Find a vertical match where one half completes a full match so the other half can fall onto an empty space 
            if(col_indx<0){
                min_depth = 17; 
                for(int x=0; x<Bottle.WIDTH; x++){
                    int depth = playfield.top_item_depths[x];  
    
                    if(depth>0){
                        
                        // check that the top items are a three-in-a-row match 
                        if(playfield.top_items_three_match[x]){
                            int first_color = playfield.playfield[depth][x]&Bottle.COLOR_MASK; 
                            
                            if(depth>1){
                                int second_depth = playfield.second_item_depths[x]; 

                                // the second item should be an empty item 
                                if(playfield.playfield[second_depth][x] == Bottle.EMPTY_TILE){
                                    if(pill_l==first_color){
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            orientation = Bottle.PILL_90_CC; 
                                            min_depth = depth;
                                        }
                                    }else if(pill_r==first_color){
                                        if(depth<min_depth){
                                            col_indx = x; 
                                            orientation = Bottle.PILL_90_C; 
                                            min_depth = depth;
                                        }
                                    }
                                }
                            }else{
                                //one half 
                                if(pill_l==first_color){
                                    if(depth<min_depth){
                                        col_indx = x; 
                                        orientation = Bottle.PILL_90_CC; 
                                        min_depth = depth;
                                    }
                                }else if(pill_r==first_color){
                                    if(depth<min_depth){
                                        col_indx = x; 
                                        orientation = Bottle.PILL_90_C; 
                                        min_depth = depth;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Try to find non-hanging perfect matches
            if(col_indx<0){
                min_depth = 17; 
                for(int x=0; x<Bottle.WIDTH-1; x++){

                    int d_l = playfield.top_item_depths[x]; 
                    int d_r = playfield.top_item_depths[x+1]; 

                    int depth = Math.min(d_l,d_r); 

                    if(depth>0){
                        int color_l = playfield.playfield[d_l][x]&Bottle.COLOR_MASK; 
                        int color_r = playfield.playfield[d_r][x+1]&Bottle.COLOR_MASK; 
                    
                        // neither are hanging pills 
                        if(!playfield.is_hanging_pill[x] && !playfield.is_hanging_pill[x+1]){
                            if(pill_l==color_l && pill_r==color_r){
                                if(depth<min_depth){
                                    col_indx = x; 
                                    orientation = Bottle.PILL_ORIG; 
                                    min_depth = depth; 
                                }
                                    
                            }else if(pill_l==color_r && pill_r==color_l){
                                if(depth<min_depth){
                                    col_indx = x; 
                                    orientation = Bottle.PILL_REV; 
                                    min_depth = depth; 

                                }
                            }
                        }
                    }
                }
           
            }

            // Try to find the lowest perfect match 
            if(col_indx<0){ 
                max_depth = -1; 
                for(int x=0; x<Bottle.WIDTH-1; x++){

                    int d_l = playfield.top_item_depths[x]; 
                    int d_r = playfield.top_item_depths[x+1]; 

                    int depth = Math.min(d_l,d_r); 

                    if(depth>0){    
                        int color_l = playfield.playfield[d_l][x]&Bottle.COLOR_MASK; 
                        int color_r = playfield.playfield[d_r][x+1]&Bottle.COLOR_MASK; 

                        if(pill_l==color_l && pill_r==color_r){
                            if(depth>max_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_ORIG; 
                                max_depth = depth; 
                            }
                                
                        }else if(pill_l==color_r && pill_r==color_l){
                            if(depth>max_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_REV; 
                                max_depth = depth; 
                            }
                        }
                    }   
                }
            }

            // Try to find half match (non-hanging) + lowest non-match 
            if(col_indx<0){
                max_depth = -1; 

                for(int x=0; x<Bottle.WIDTH-1; x++){

                    int d_l = playfield.top_item_depths[x]; 
                    int d_r = playfield.top_item_depths[x+1]; 

                    int depth = Math.min(d_l,d_r); 

                    if(depth>0){

                        int color_l = playfield.playfield[d_l][x]&Bottle.COLOR_MASK; 
                        int color_r = playfield.playfield[d_r][x+1]&Bottle.COLOR_MASK; 

                        if(!playfield.is_hanging_pill[x] && !playfield.is_hanging_pill[x+1]){
                            if(pill_l==color_l){
                                if(d_r>max_depth){
                                    col_indx = x; 
                                    orientation = Bottle.PILL_ORIG; 
                                    max_depth = d_r; 
                                }
                            }else if(pill_r==color_r){
                                if(d_l>max_depth){
                                    col_indx = x; 
                                    orientation = Bottle.PILL_ORIG; 
                                    max_depth = d_l; 
                                }
                            }else if(pill_l==color_r){
                                if(d_l>max_depth){
                                    col_indx = x; 
                                    orientation = Bottle.PILL_REV; 
                                    max_depth = d_l; 
                                }
                            }else if(pill_r==color_l){
                                if(d_r>max_depth){
                                    col_indx = x; 
                                    orientation = Bottle.PILL_REV; 
                                    max_depth = d_r; 
                                }
                            }
                        }
                    }
                }

            }

            // Try to find half match + lowest non-match 
            if(col_indx<0){
                max_depth = -1; 

                for(int x=0; x<Bottle.WIDTH-1; x++){

                    int d_l = playfield.top_item_depths[x]; 
                    int d_r = playfield.top_item_depths[x+1]; 

                    int depth = Math.min(d_l,d_r); 

                    if(depth>0){

                        int color_l = playfield.playfield[d_l][x]&Bottle.COLOR_MASK; 
                        int color_r = playfield.playfield[d_r][x+1]&Bottle.COLOR_MASK; 

                        if(pill_l==color_l){
                            if(d_r>max_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_ORIG; 
                                max_depth = d_r; 
                            }
                        }else if(pill_r==color_r){
                            if(d_l>max_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_ORIG; 
                                max_depth = d_l; 
                            }
                        }else if(pill_l==color_r){
                            if(d_l>max_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_REV; 
                                max_depth = d_l; 
                            }
                        }else if(pill_r==color_l){
                            if(d_r>max_depth){
                                col_indx = x; 
                                orientation = Bottle.PILL_REV; 
                                max_depth = d_r; 
                            }
                        }
                    }
                }
            }

            // Try to find lowest non match
            if(col_indx<0){
                max_depth = -1; 
                for(int x=0; x<Bottle.WIDTH-1; x++){
                    int d_l = playfield.top_item_depths[x]; 
                    int d_r = playfield.top_item_depths[x+1]; 
                    int depth = Math.min(d_l,d_r); 
                    if(depth>max_depth){
                        col_indx =x; 
                        max_depth = depth; 
                    }
                }
            }

        }
    

        int move_hor = col_indx-3; 
        int true_hor_idx = 3;
    
        if(move_hor<0){
            int num_moves = move_hor*-1; 
            for(int i=0; i<num_moves;i++){
                if(playfield.top_item_depths[true_hor_idx-1]<=0){
                    break; 
                }
                true_hor_idx--; 
            }
        }else{
            for(int i=0; i<move_hor; i++){

                if(orientation%2==0){
                    if(playfield.top_item_depths[true_hor_idx+2]<=0){
                        break; 
                    }

                }else{
                    if(playfield.top_item_depths[true_hor_idx+1]<=0){
                        break; 
                    }

                }

                true_hor_idx++; 
            }
        }


        int row_idx = playfield.top_item_depths[true_hor_idx]-1;

        if(orientation%2==0){
            row_idx = Math.min(playfield.top_item_depths[true_hor_idx], playfield.top_item_depths[true_hor_idx+1])-1;
        }
  
        Placement p = new Placement(row_idx, true_hor_idx, orientation);

        return p; 

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
