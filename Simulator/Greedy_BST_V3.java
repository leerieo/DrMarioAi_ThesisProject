
import java.util.Queue;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;



public class Greedy_BST_V3 {

    private final GreedyBSTUtils u = new GreedyBSTUtils();

    public Placement ai(int pill_l, int pill_r, Playfield playfield){

        Placement best_placement = null; 

        ArrayList<Placement> placements = u.getAllLandings(playfield.playfield); 

        if(placements.size()>0){
            double max_score = -Double.MAX_VALUE; 
            best_placement = placements.get(0); 
            for(int i=0; i<placements.size(); i++){
                int row = placements.get(i).getRow(); 
                int col = placements.get(i).getCol();
                int orientation = placements.get(i).getOrientation(); 
    
                // System.out.println("ROW: "+row+" COL: "+col+" ORIEN: "+orientation); 
                // System.out.println("MOVELIST: "+placements.get(i).move_list.toString());
                playfield.placeVitamin(pill_l, pill_r, row, col, orientation);
    
                double ver_score = scorePlacementVer(playfield, row, col, orientation); 
                double hor_score = scorePlacementHor(playfield, row, col, orientation); 
    
                if(ver_score>max_score){
                    // System.out.println("ver_score: "+ver_score);
                    // playfield.printPlayfield();
                    max_score = ver_score; 
                    best_placement = placements.get(i); 
                }else if(hor_score>max_score){
                    // System.out.println("hor_score: "+hor_score);
                    // playfield.printPlayfield();
                    max_score = hor_score; 
                    best_placement = placements.get(i);
                }

                // playfield.printPlayfield();
                playfield.removeVitamin(row, col, orientation);
            }
          
        }

        return best_placement; 

    }



    private double scorePlacementVer(Playfield playfield, int row, int col, int orientation){

        double score = 0; 
        boolean is_match = false;

        if(orientation%2==0){

            //horizontal 
            int color_l = playfield.playfield[row][col] & Bottle.COLOR_MASK; 
            int color_r = playfield.playfield[row][col+1] & Bottle.COLOR_MASK; 

            int match_left_counter = 0; 
            int match_left_virus_counter = 0; 

            //check beneath left pill 
            for(int i=(row+1); i<Bottle.HEIGHT; i++){
                int tile = playfield.playfield[i][col]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_l){
                    match_left_counter++; 
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        match_left_virus_counter++;
                    }
                }else if(tile!=Bottle.EMPTY_TILE){
                    // stop scoring if we encounter a non-empty tile of a non-matching color 
                    break; 
                }
            }

            int match_right_counter = 0; 
            int match_right_virus_counter = 0; 

            //check beneath right pill 
            for(int i=(row+1); i<Bottle.HEIGHT; i++){
                int tile = playfield.playfield[i][col+1]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_r){
                    match_right_counter++;
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        match_right_virus_counter++;
                    }
                }else if(tile!=Bottle.EMPTY_TILE){
                    // stop scoring if we encounter a non-empty tile of a non-matching color 
                    break; 
                }

            }

            score += (match_left_counter + match_right_counter);

            if(playfield.col_has_virus[col]){
                score+=match_left_counter; 
            }
            if(playfield.col_has_virus[col+1]){
                score+=match_right_counter; 
            }
            
            score += (match_left_virus_counter + match_right_virus_counter)*10; 

            if (match_left_counter>0 && match_right_counter>0){
                is_match = true; 
            }

            // if we're on the top row 
            if(row==0){
                if(match_left_counter<3 && match_right_counter<3){
                    score -= 100; 
                }else if(match_left_counter<3 && playfield.playfield[1][col]!=Bottle.EMPTY_TILE){
                    score -= 100; 
                }else if(match_right_counter<3 && playfield.playfield[1][col+1]!=Bottle.EMPTY_TILE){
                    score -= 100; 
                }
            }

            // punish mismatches if there are no matching above colors 
            if(match_left_counter==0){
                boolean color_match_above = false; 

                for(int i=row-1; i>=0; i--){
                    int tile = playfield.playfield[i][col]; 
                    if((tile&Bottle.COLOR_MASK)==color_l){
                        color_match_above=true; 
                        break; 
                    }else if(tile != Bottle.EMPTY_TILE){
                        break;
                    }
                }

                if(!color_match_above && (playfield.top_item_depths[col]<Bottle.HEIGHT)){
                    score-=20;
                    if(playfield.col_has_virus[col]){
                        score -=10; 
                    }
                }
                
            }
            if(match_right_counter==0){
                boolean color_match_above = false; 

                for(int i=row-1; i>=0; i--){
                    int tile = playfield.playfield[i][col+1]; 
                    if((tile&Bottle.COLOR_MASK)==color_l){
                        color_match_above=true; 
                        break; 
                    }else if(tile != Bottle.EMPTY_TILE){
                        break;
                    }
                }

                if(!color_match_above && (playfield.top_item_depths[col+1]<Bottle.HEIGHT)){
                    score-=20;
                    if(playfield.col_has_virus[col]){
                        score -=10; 
                    }
                }
            }

            //check above left pill 
            boolean match_left = false; 

            if(((row-2)>=0) && (playfield.playfield[row-2][col]&Bottle.COLOR_MASK)==color_l){
                score++; 
                if((playfield.playfield[row-2][col]&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                    score++; 
                }
            }
            if(row>0 && (playfield.playfield[row-1][col]&Bottle.COLOR_MASK)==color_l){
                score++;
                match_left = true; 
                if((playfield.playfield[row-1][col]&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                    score++; 
                }
            }

            //check above right pill 
            boolean match_right = false; 
            if(((row-2)>=0) && (playfield.playfield[row-2][col+1]&Bottle.COLOR_MASK)==color_l){
                score++; 
                if((playfield.playfield[row-2][col+1]&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                    score++; 
                }
            }
            if(row>0 && (playfield.playfield[row-1][col+1]&Bottle.COLOR_MASK)==color_l){
                score++;
                match_right = true; 
                if((playfield.playfield[row-1][col+1]&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                    score++; 
                }
            }

            if(match_left && match_right){ 
                is_match = true; 
            }

            //prevent stacking? 
            // if(playfield.is_hanging_pill[col] && playfield.is_hanging_pill[col+1]){
            //     is_match = false; 
            //     score-=20; 
            // }


        }else if(row>0){

            //vertical 
            int color_b = playfield.playfield[row][col] & Bottle.COLOR_MASK; 
            int color_t = playfield.playfield[row-1][col] & Bottle.COLOR_MASK; 

            boolean match_bottom = false; 
            // check under the vitamin 
            int match_counter = 0; 
            int match_virus_counter = 0; 

            for(int i=row+1; i<Bottle.HEIGHT; i++){
                int tile = playfield.playfield[i][col]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_b){
                    match_counter++; 
                    match_bottom = true; 
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        match_virus_counter++;
                    }
                }else{
                    break; 
                }
            }

            if(color_b==color_t && match_bottom){
                is_match = true; 
                score++; 
                score += match_virus_counter; 
                if(playfield.col_has_virus[col]){
                    score++; 
                }
            }else if(match_counter>=3 && color_b!=color_t){
                score++; 

                if(playfield.col_has_virus[col]){
                    score++; 
                }

                int new_row = row+1+match_counter; 

                boolean match_top = false; 
                for(int i=new_row; i<Bottle.HEIGHT; i++){
                    int tile = playfield.playfield[i][col]; 
                    // same color 
                    if((tile&Bottle.COLOR_MASK)==color_t){
                        score++;
                        if(playfield.col_has_virus[col]){
                            score++; 
                        }
                        match_top = true; 
                        break; 

                    }else if(tile!=Bottle.EMPTY_TILE){
                        break; 
                    }
                } 

                if(match_top){
                    is_match = true; 
                }else if(playfield.col_has_virus[col]){
                    score-=20; 
                }

            }

            if(!match_bottom){
                score-=10; 
                if(playfield.col_has_virus[col]){
                    score-=10; 
                }
            }
            if(match_counter<3 && color_b!=color_t){
                score-=10; 
                if(playfield.col_has_virus[col]){
                    score-=10;
                }
                match_bottom = false;
            }

            // if we're on the top row 
            if(row<=1){
                if(!match_bottom){
                    score -= 100; 
                }else if(color_b==color_t && match_counter<2){
                    score -= 100; 
                }else if(color_b!=color_t && match_counter<3){
                    score -= 100; 
                }
            }

            //check above the vitamin 
            boolean match_top = false; 

            if(((row-3)>=0) && (playfield.playfield[row-3][col]&Bottle.COLOR_MASK)==color_t){
                score++; 
                if((playfield.playfield[row-3][col]&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                    score++; 
                }
            }
            if(row>1 && (playfield.playfield[row-2][col]&Bottle.COLOR_MASK)==color_t){
                score++; 
                if((playfield.playfield[row-2][col]&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                    score++; 
                }
                match_top = true; 
            }

            if(match_bottom || match_top){
                is_match = true; 
            }


            //prevent stacking? 
            // if(playfield.is_hanging_pill[col]){
            //     is_match = false; 
            //     score -=20;
            // }
            
        }else{

            int color_b = playfield.playfield[row][col] & Bottle.COLOR_MASK; 
            // check under the vitamin 

            boolean match_bottom = false; 
            int match_counter = 0; 
            int match_virus_counter = 0; 
            for(int i=row+1; i<Bottle.HEIGHT; i++){
                int tile = playfield.playfield[i][col]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_b){
                    match_counter++; 
                    match_bottom = true; 
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        match_virus_counter++;
                    }
                }else{
                    break; 
                }
            }

            // does not clear 
            if(match_counter<3){
                score-=100; 
            }

            score += match_counter + match_virus_counter; 

            if(match_bottom){
                if(playfield.col_has_virus[col]){
                    score++; 
                }
                is_match = true; 
            }


            // if(playfield.is_hanging_pill[col]){
            //     is_match = false; 
            //     score-=20; 
            // }

        }

        // if it is a match we want to place it as high as possible, otherwise place it as low as possible
        if(is_match){
            score += ((Bottle.HEIGHT-row)/10.0); 
        }else{
            score+=(row/10.0); 
        }

        return score; 

    }

    private double scorePlacementHor(Playfield playfield, int row, int col, int orientation){

        double score = 0; 
        boolean is_match = false;

        if(orientation%2==0){

            //horizontal 
            int color_l = playfield.playfield[row][col] & Bottle.COLOR_MASK; 
            int color_r = playfield.playfield[row][col+1] & Bottle.COLOR_MASK; 


            int num_cleared_l = 0; 
            int num_cleared_r = 0; 
            int match_virus_counter_l = 0; 
            int match_virus_counter_r = 0; 

            boolean match_l = false; 
            boolean match_r = false; 

            //check left of left pill 
            for(int i=(col-1); i>=0; i--){
                int tile = playfield.playfield[row][i]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_l){
                    num_cleared_l++; 
                    match_l = true; 
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        match_virus_counter_l++;
                    }
                }else{
                    // for horizontal matches we want the colors to be in-a-row
                    break; 
                }
            }

            //check right of right pill 
            for(int i=(col+2); i<Bottle.WIDTH; i++){
                int tile = playfield.playfield[row][i]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_r){
                    num_cleared_r++; 
                    match_r = true; 
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        match_virus_counter_r++;
                    }
                }else{
                    // for horizontal matches we want the colors to be in-a-row
                    break; 
                }

            }  

            if(color_l==color_r){
                // if we're on the top row 
                if(row==0 && (num_cleared_l + num_cleared_r)<2){
                    score -= 100; 
                }
                if((num_cleared_l + num_cleared_r)>=2){
                    is_match = true; 
                    score+=(num_cleared_l + num_cleared_r);
                    score+=(match_virus_counter_l + match_virus_counter_r); 
                   
                }
                if((num_cleared_l + num_cleared_r)<2){
                    score -=20;
                }
            }else{

                //if we're on the top row 
                if(row==0 && (num_cleared_l + num_cleared_r)<6){
                    score -= 100; 
                }

                if(match_l && num_cleared_l<3){
                    score += num_cleared_l; 
                    score += match_virus_counter_l; 
                }
                
                if(match_r && num_cleared_r<3){
                    score += num_cleared_r; 
                    score += match_virus_counter_r; 
                }

                if((match_l && num_cleared_l<3)&&(match_r && num_cleared_r<3)){
                    is_match = true; 
                }

                if(!match_l){
                    score-=10; 
                }
                if(!match_r){
                    score-=10; 
                }
            }

            // punish non complete matches? 
            // if(!is_match){
            //     score -=10; 
            // }

            // if(playfield.is_hanging_pill[col] && playfield.is_hanging_pill[col+1]){
            //     is_match = false; 
            //     score-=20; 
            // }

        }else if(row>0){
            //vertical 
            int color_b = playfield.playfield[row][col] & Bottle.COLOR_MASK; 
            int color_t = playfield.playfield[row-1][col] & Bottle.COLOR_MASK; 

            //check left of the bottom vitamin 
            int match_counter_b = 0; 
            int virus_counter_b = 0; 
            for(int i=(col-1); i>=0; i--){
                int tile = playfield.playfield[row][i]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_b){
                    match_counter_b++; 
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        virus_counter_b++;
                    }
                }else{
                    // for horizontal matches we want the colors to be in-a-row
                    break; 
                }

            }

            //check right of the bottom vitamin 
            for(int i=(col+1); i<Bottle.WIDTH; i++){
                int tile = playfield.playfield[row][i]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_b){
                    match_counter_b++; 
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        virus_counter_b++;
                    }
                }else{
                    // for horizontal matches we want the colors to be in-a-row
                    break; 
                }
            }

            if(match_counter_b==0){
                score-=10; 
            }else{
                score+=match_counter_b;
                score+=virus_counter_b; 
            }

            //check left of the top vitamin 
            int match_counter_2 = 0; 
            int virus_counter_t = 0; 
            for(int i=(col-1); i>=0; i--){
                int tile = playfield.playfield[row-1][i]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_t){
                    match_counter_2++;
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        virus_counter_t++;
                    }
                }else{
                    // for horizontal matches we want the colors to be in-a-row
                    break; 
                }
            }

            //check right of the top vitamin 
            for(int i=(col+1); i<Bottle.WIDTH; i++){
                int tile = playfield.playfield[row-1][i]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_t){
                    match_counter_2++;
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        virus_counter_t++;
                    }
                }else{
                    // for horizontal matches we want the colors to be in-a-row
                    break; 
                }
            }

            // if we're on the top row and the bottom is not cleared and top is not cleared 
            if(row==1){
                if(match_counter_b<3 && match_counter_2<3){
                    score-=100; 
                }
            }

            // top is cleared 
            if(match_counter_2>=3){

                if(match_counter_2==0){
                    score-=20; 
                }else{
                    score+=match_counter_2;
                    score+=virus_counter_t; 
                }

            }else if(match_counter_b>=3){

                // bottom is cleared and top is not cleared 
                boolean match_bottom_top = false; 

                if(row<(Bottle.HEIGHT-1)){
                    int tile = playfield.playfield[row+1][col]; 
                    if((tile&Bottle.COLOR_MASK)==color_t){
                        score++; 
                        match_bottom_top = true; 
                    }
                }

                if(match_bottom_top){
                    is_match = true; 
                }else{
                    score-=10; 
                }
                
            }

            if(match_counter_b>0 && match_counter_2>0){
                is_match = true; 
            }


            // punish non complete matches? 
            // if(match_counter_b<3 && match_counter_2<3){
            //     score -=10; 
            //     // is_match = false; 
            // }

            // if(playfield.is_hanging_pill[col]){
            //     is_match = false; 
            //     score-=20; 
            // }

        }else{

            int color_b = playfield.playfield[row][col] & Bottle.COLOR_MASK; 
            
            boolean match_l = false; 
            boolean match_r = false; 

            //check left of the bottom vitamin 
            int match_counter = 0; 
            int virus_counter_b = 0; 
            for(int i=(col-1); i>=0; i--){
                int tile = playfield.playfield[row][i]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_b){
                    match_counter++;
                    match_l = true; 
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        virus_counter_b++;
                    }
                }else{
                    // for horizontal matches we want the colors to be in-a-row
                    break; 
                }
            }


            //check right of the bottom vitamin 
            for(int i=(col+1); i<Bottle.WIDTH; i++){
                int tile = playfield.playfield[row][i]; 
                // same color 
                if((tile&Bottle.COLOR_MASK)==color_b){
                    match_counter++;
                    match_r = true; 
                    if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                        virus_counter_b++;
                    }
                }else{
                    // for horizontal matches we want the colors to be in-a-row
                    break; 
                }
            }

            if(match_counter>=3){
                score+=match_counter; 
                score+=virus_counter_b;     
            }

            if(match_counter<3){
                score-=100; 
            }


            // if(playfield.is_hanging_pill[col]){
            //     is_match = false; 
            //     score-=20; 
            // }

        }


        // if it is a match we want to place it as high as possible, otherwise place it as low as possible
        if(is_match){
            score += ((Bottle.HEIGHT-row)/10.0); 
        }else{
            score+=(row/10.0); 
        }

        return score; 

    }

    
}
