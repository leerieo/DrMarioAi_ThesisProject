package SOLVERS.TVO;

import java.util.Queue;

import SOLVERS.Bottle;
import SOLVERS.Placement;

import java.util.LinkedList;

public class TVOUtils {

    public static Placement getPlacement(int col_idx, int orientation, ItemColumn[] top_items){
        int move_hor = col_idx-3; 
        int true_hor_idx = 3;
    
        if(move_hor<0){
            int num_moves = move_hor*-1; 
            for(int i=0; i<num_moves;i++){
                if(top_items[true_hor_idx-1].depth()<=0){
                    // System.out.println("BLOCK");
                    break; 
                }
                true_hor_idx--; 
            }
        }else{
            for(int i=0; i<move_hor; i++){

                if(orientation%2==0){
                    if(top_items[true_hor_idx+2].depth()<=0){
                        break; 
                    }

                }else{
                    if(top_items[true_hor_idx+1].depth()<=0){
                        break; 
                    }

                }

                true_hor_idx++; 
            }
        }

        int row_idx = top_items[true_hor_idx].depth()-1;
        if(orientation%2==0){
            row_idx = Math.min(top_items[true_hor_idx].depth(), top_items[true_hor_idx+1].depth())-1;
        }

        return new Placement(row_idx, true_hor_idx, orientation);


    }

    public static Queue<String> getMoveQueue(int col_idx, int orientation){

        Queue<String> moveQueue = new LinkedList<String>();

        // Add moves 
        if(orientation==Bottle.PILL_90_CC){
            // VERTICAL_CCW
            moveQueue.add("ccw");
        }else if(orientation==Bottle.PILL_REV){
            // REVERSE 
            moveQueue.add("ccw");
            moveQueue.add("ccw");
        }else if(orientation==Bottle.PILL_90_C){
            // VERTICAL_CW
            moveQueue.add("cw"); 
        }

        int move_hor = col_idx-3; 

        // Move left 
        if(move_hor<0){
            int num_moves = move_hor*-1; 
            for(int i=0; i<num_moves;i++){
                moveQueue.add("l");
            }
        }else{
            for(int i=0; i<move_hor; i++){
                moveQueue.add("r");
            }
        }

        moveQueue.add("dp");

        return moveQueue; 
    }

    public static boolean reachesTop(int orientation, int row){
        if(orientation%2==0){
            //horizontal 
            return ((row-1)<=0);
        }else{
            //vertical 
            return ((row-2)<=0); 
        }
    }

    public static int Vertical_1_Match(int pill_l, int pill_r, ItemColumn[] top_items){

        int col_idx = -1; 

        int min_depth = 16; 
        for(int i=0; i<Bottle.WIDTH; i++){
            int item = top_items[i].top_item(); 
            int depth = top_items[i].depth(); 

            if(!reachesTop(Bottle.PILL_90_C,depth) || top_items[i].top_two_match()){
                // Check that top item is the same color 
                if((item&Bottle.COLOR_MASK)==pill_l){
                    // Place vitamin at lowest depth (highest in the bottle)
                    if(depth<min_depth){
                        col_idx = i; 
                        min_depth = depth; 
                    }
                }
            }
        }

        // if(col_idx>=0){
        //     System.out.println("Vertical_1_Match");
        // }

        return col_idx;
    }

    // Find lowest + leftmost placement for a 1 color vertical vitamin 
    public static int Vertical_1_Non_Match(int pill_l, int pill_r, ItemColumn[] top_items){
        int max_depth = -1; 
        int col_idx = -1; 
        for(int i=0; i<Bottle.WIDTH; i++){
            int depth = top_items[i].depth(); 
            if(depth>max_depth){
                col_idx = i; 
                max_depth = depth; 
            }
        }

        // System.out.println("Vertical_1_Non_Match");
        return col_idx; 

    }

    // Try to find a matching color for a 1 color vertical vitamin 
    public static int Vertical_1_Match_With_Virus(int pill_l, int pill_r, ItemColumn[] top_items){
        int min_depth = 16; 
        int col_idx = -1; 
        for(int i=0; i<Bottle.WIDTH; i++){
            int item = top_items[i].top_item(); 
            int depth = top_items[i].depth(); 
           
            if(!reachesTop(Bottle.PILL_90_C,depth) || top_items[i].top_two_match()){
                // Check that top item is the same color 
                if((item&Bottle.COLOR_MASK)==pill_l){
                    // Place vitamin at lowest depth (highest in the bottle)
                    if(top_items[i].has_virus() && depth<min_depth){
                        col_idx = i; 
                        min_depth = depth; 
                    }
                }
            }
        }

        // if(col_idx>=0){
        //     System.out.println("Vertical_1_Match_With_Virus");
        // }
        return col_idx;
    }



    // Try to find a matching color for a 1 color vertical vitamin 
    public static int Vertical_1_Match_With_Virus_Not_Hanging(int pill_l, int pill_r, ItemColumn[] top_items){
        int min_depth = 16; 
        int col_idx = -1; 
        for(int i=0; i<Bottle.WIDTH; i++){
            int item = top_items[i].top_item(); 
            int depth = top_items[i].depth(); 
           
            if(!reachesTop(Bottle.PILL_90_C,depth) || top_items[i].top_two_match()){
                // Check that top item is the same color 
                if((item&Bottle.COLOR_MASK)==pill_l && !top_items[i].is_hanging_pill()){
                    // Place vitamin at lowest depth (highest in the bottle)
                    if(top_items[i].has_virus() && depth<min_depth){
                        col_idx = i; 
                        min_depth = depth; 
                    }
                }
            }
        }

        // if(col_idx>=0){
        //     System.out.println("Vertical_1_Match_With_Virus_Not_Hanging");
        // }
        return col_idx;
    }
    
    // Try to find a matching color for a 1 color vertical vitamin 
    public static int Vertical_1_Match_Not_Hanging(int pill_l, int pill_r, ItemColumn[] top_items){
        int min_depth = 16; 
        int col_idx = -1; 
        for(int i=0; i<Bottle.WIDTH; i++){
            int item = top_items[i].top_item(); 
            int depth = top_items[i].depth(); 
            if(!reachesTop(Bottle.PILL_90_C,depth) || top_items[i].top_two_match()){
                // Check that top item is the same color 
                if((item&Bottle.COLOR_MASK)==pill_l && !top_items[i].is_hanging_pill()){
                    // Place vitamin at lowest depth (highest in the bottle)
                    if(depth<min_depth){
                        col_idx = i; 
                        min_depth = depth; 
                    }
                }
            }
        }

        // if(col_idx>=0){
        //     System.out.println("Vertical_1_Match_Not_Hanging");
        // }
        return col_idx;
    }


    // Find lowest + leftmost placement for a 1 color vertical vitamin 
    public static int Vertical_1_Lowest_Match(int pill_l, int pill_r, ItemColumn[] top_items){
        int max_depth = -1; 
        int col_idx = -1; 
        for(int i=0; i<Bottle.WIDTH; i++){
            int depth = top_items[i].depth(); 
            if(!reachesTop(Bottle.PILL_90_C,depth) || top_items[i].top_two_match()){
                if((top_items[i].top_item()&Bottle.COLOR_MASK)==pill_l){
                    if(depth>max_depth){
                        col_idx = i; 
                        max_depth = depth; 
                    }
                }
            }
        }
        // System.out.println("Vertical_1_Lowest_Match");
        return col_idx; 
    }



    public static int[] Horizontal_Perfect_2_Match(int pill_l, int pill_r, ItemColumn[] top_items){
        int min_depth = 16; 
        int col_idx = -1; 
        int orientation = Bottle.PILL_ORIG; 

        for(int i=0; i<Bottle.WIDTH-1; i++){
            int item = top_items[i].top_item(); 
            int item2 = top_items[i+1].top_item(); 

            int depth_l = top_items[i].depth(); 
            int depth_r = top_items[i+1].depth(); 
            int depth = Math.min(depth_l,depth_r); 

            int color_l = item&Bottle.COLOR_MASK; 
            int color_r = item2&Bottle.COLOR_MASK; 

            if(pill_l==color_l && pill_r==color_r){

                if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                    if(!top_items[i].top_three_match()){
                        continue; 
                    }
                }

                if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                    if(!top_items[i+1].top_three_match()){
                        continue; 
                    }
                }

                if(depth<min_depth){
                    col_idx = i; 
                    min_depth = depth; 
                    orientation = Bottle.PILL_ORIG;  
                }
            }else if(pill_l==color_r && pill_r==color_l){

                if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                    if(!top_items[i].top_three_match()){
                        continue; 
                    }
                }

                if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                    if(!top_items[i+1].top_three_match()){
                        continue; 
                    }
                }

                if(depth<min_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_REV; 
                    min_depth = depth; 
                    
                }
            }
            
        }


        // if(col_idx>=0){
        //     System.out.println("Horizontal_Perfect_2_Match");
        // }

        return new int[] {col_idx,orientation};
    }

    public static int[] Horizontal_Perfect_2_Match_with_Virus(int pill_l, int pill_r, ItemColumn[] top_items){
        int min_depth = 16; 
        int col_idx = -1; 
        int orientation = Bottle.PILL_ORIG; 

        for(int i=0; i<Bottle.WIDTH-1; i++){

            int depth_l = top_items[i].depth(); 
            int depth_r = top_items[i+1].depth(); 
            int depth = Math.min(top_items[i].depth(),top_items[i+1].depth()); 

            int color_l = top_items[i].top_item()&Bottle.COLOR_MASK; 
            int color_r = top_items[i+1].top_item()&Bottle.COLOR_MASK; 

            if(top_items[i].has_virus() || top_items[i+1].has_virus()){
                if(pill_l==color_l && pill_r==color_r){
                    if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                        if(!top_items[i].top_three_match()){
                            continue; 
                        }
                    }
                    if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                        if(!top_items[i+1].top_three_match()){
                            continue; 
                        }
                    }
                    if(depth<min_depth){
                        col_idx = i; 
                        min_depth = depth; 
                        orientation = Bottle.PILL_ORIG;  
                    }
                }else if(pill_l==color_r && pill_r==color_l){
                    if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                        if(!top_items[i].top_three_match()){
                            continue; 
                        }
                    }
                    if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                        if(!top_items[i+1].top_three_match()){
                            continue; 
                        }
                    }

                    if(depth<min_depth){
                        col_idx = i; 
                        orientation = Bottle.PILL_REV; 
                        min_depth = depth; 
                      
                    }
                }
            }
    
        }

        // if(col_idx>=0){
        //     System.out.println("Horizontal_Perfect_2_Match_with_Virus");
        // }

        return new int[] {col_idx,orientation};
    }



    public static int[] Horizontal_Perfect_2_Match_with_Virus_Not_Hanging(int pill_l, int pill_r, ItemColumn[] top_items){
        int min_depth = 16; 
        int col_idx = -1; 
        int orientation = Bottle.PILL_ORIG; 

        for(int i=0; i<Bottle.WIDTH-1; i++){
            int depth_l = top_items[i].depth(); 
            int depth_r = top_items[i+1].depth(); 
            int depth = Math.min(top_items[i].depth(),top_items[i+1].depth()); 

            int color_l = top_items[i].top_item()&Bottle.COLOR_MASK; 
            int color_r = top_items[i+1].top_item()&Bottle.COLOR_MASK; 

            if(top_items[i].is_hanging_pill() && top_items[i+1].is_hanging_pill()){
                if(top_items[i].has_virus() || top_items[i+1].has_virus()){
                    if(pill_l==color_l && pill_r==color_r){
                        if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                            if(!top_items[i].top_three_match()){
                                continue; 
                            }
                        }
                        if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                            if(!top_items[i+1].top_three_match()){
                                continue; 
                            }
                        }
                        if(depth<min_depth){
                            col_idx = i; 
                            min_depth = depth; 
                            orientation = Bottle.PILL_ORIG;  
                        }
                    }else if(pill_l==color_r && pill_r==color_l){
                        if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                            if(!top_items[i].top_three_match()){
                                continue; 
                            }
                        }
                        if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                            if(!top_items[i+1].top_three_match()){
                                continue; 
                            }
                        }
                        if(depth<min_depth){
                            col_idx = i; 
                            orientation = Bottle.PILL_REV; 
                            min_depth = depth; 
                          
                        }
                    }
                }
            }
        }

        // if(col_idx>=0){
        //     System.out.println("Horizontal_Perfect_2_Match_with_Virus");
        // }

        return new int[] {col_idx,orientation};
    }


    public static int[] Vertical_Half_Color_Complete_Fall(int pill_l, int pill_r, ItemColumn[] top_items){

        int min_depth = 16; 
        int col_idx = -1; 
        int orientation = Bottle.PILL_ORIG; 

        for(int i=0; i<Bottle.WIDTH; i++){
            ItemColumn c = top_items[i]; 
            int top_color = c.top_item()&Bottle.COLOR_MASK; 

            if(c.top_three_match()){

                int item_2 = c.second_item(); 
                int bottom_color = item_2&Bottle.COLOR_MASK;
            
                if(pill_l == top_color && pill_r==bottom_color){
                    if(c.depth()<min_depth){
                        col_idx = i; 
                        orientation = Bottle.PILL_90_CC; 
                        min_depth = c.depth(); 
                    }
                }else if(pill_r ==top_color && pill_l==bottom_color){
                    if(c.depth()<min_depth){
                        col_idx = i; 
                        orientation = Bottle.PILL_90_C; 
                        min_depth = c.depth(); 
                    }
                }
            }
        }
        
        // if(col_idx>=0){
        //     System.out.println("Vertical_Half_Color_Complete_Fall");
        // }

        return new int[] {col_idx,orientation};

    }

    public static int[] Horizontal_Perfect_2_Match_Not_Hanging(int pill_l, int pill_r, ItemColumn[] top_items){
        int min_depth = 16; 
        int col_idx = -1; 
        int orientation = Bottle.PILL_ORIG; 

        for(int i=0; i<Bottle.WIDTH-1; i++){
            int depth_l = top_items[i].depth(); 
            int depth_r = top_items[i+1].depth(); 
            int depth = Math.min(top_items[i].depth(),top_items[i+1].depth()); 
            int color_l = top_items[i].top_item()&Bottle.COLOR_MASK; 
            int color_r = top_items[i+1].top_item()&Bottle.COLOR_MASK; 

            if(top_items[i].is_hanging_pill() && top_items[i+1].is_hanging_pill()){
                
                if(pill_l==color_l && pill_r==color_r){
                    if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                        if(!top_items[i].top_three_match()){
                            continue; 
                        }
                    }
                    if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                        if(!top_items[i+1].top_three_match()){
                            continue; 
                        }
                    }
                    if(depth<min_depth){
                        col_idx = i; 
                        min_depth = depth; 
                        orientation = Bottle.PILL_ORIG;  
                    }
                }else if(pill_l==color_r && pill_r==color_l){
                    if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                        if(!top_items[i].top_three_match()){
                            continue; 
                        }
                    }
                    if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                        if(!top_items[i+1].top_three_match()){
                            continue; 
                        }
                    }
                    if(depth<min_depth){
                        col_idx = i; 
                        orientation = Bottle.PILL_REV; 
                        min_depth = depth; 
                          
                    }
                }
            }
        }

        // if(col_idx>=0){
        //     System.out.println("Horizontal_Perfect_2_Match_Not_Hanging");
        // }

        return new int[] {col_idx,orientation};
    }




    public static int[] Horizontal_Lowest_Perfect_2_Match(int pill_l, int pill_r, ItemColumn[] top_items){
        int max_depth = -1; 
        int col_idx = -1; 
        int orientation = Bottle.PILL_ORIG; 

        for(int i=0; i<Bottle.WIDTH-1; i++){
            int item = top_items[i].top_item(); 
            int item2 = top_items[i+1].top_item(); 

            int depth_l = top_items[i].depth(); 
            int depth_r = top_items[i+1].depth(); 
            int depth = Math.min(depth_l,depth_r); 
            
            int color_l = item&Bottle.COLOR_MASK; 
            int color_r = item2&Bottle.COLOR_MASK; 

            if(pill_l==color_l && pill_r==color_r){
                if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                    if(!top_items[i].top_three_match()){
                        continue; 
                    }
                }
                if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                    if(!top_items[i+1].top_three_match()){
                        continue; 
                    }
                }
                if(depth>max_depth){
                    col_idx = i; 
                    max_depth = depth; 
                    orientation = Bottle.PILL_ORIG;  
                }
            }else if(pill_l==color_r && pill_r==color_l){
                if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                    if(!top_items[i].top_three_match()){
                        continue; 
                    }
                }
                if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                    if(!top_items[i+1].top_three_match()){
                        continue; 
                    }
                }
                if(depth>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_REV; 
                    max_depth = depth; 
                }
            }
            
        }

        // if(col_idx>=0){
        //     System.out.println("Horizontal_Lowest_Perfect_2_Match");
        // }

        return new int[] {col_idx,orientation};
    }



    public static int[] Horizontal_Half_2_Match(int pill_l, int pill_r, ItemColumn[] top_items){
        int max_depth = -1; 
        int col_idx = -1; 
        int orientation = Bottle.PILL_ORIG; 

        for(int i=0; i<Bottle.WIDTH-1; i++){

            int depth_l = top_items[i].depth(); 
            int depth_r = top_items[i+1].depth(); 

            int color_l = top_items[i].top_item()&Bottle.COLOR_MASK; 
            int color_r = top_items[i+1].top_item()&Bottle.COLOR_MASK; 

            if(pill_l==color_l){
                if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                    if(!top_items[i].top_three_match()){
                        continue; 
                    }
                }
                if(depth_r>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_ORIG; 
                    max_depth = depth_r; 
                }
            }else if(pill_r==color_r){
                if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                    if(!top_items[i+1].top_three_match()){
                        continue; 
                    }
                }
                if(depth_l>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_ORIG; 
                    max_depth = depth_l; 
                
                }
            }else if(pill_l==color_r){
                if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                    if(!top_items[i+1].top_three_match()){
                        continue; 
                    }
                }
                if(depth_l>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_REV; 
                    max_depth = depth_l; 
              
                }
            }else if(pill_r==color_l){
                if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                    if(!top_items[i].top_three_match()){
                        continue; 
                    }
                }
                if(depth_r>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_REV; 
                    max_depth = depth_r; 
                }
            }
        }

        // if(col_idx>=0){
        //     System.out.println("Horizontal_Half_2_Match");
        // }
        return new int[] {col_idx,orientation};
    }


    public static int[] Horizontal_Half_2_Match_With_Virus(int pill_l, int pill_r, ItemColumn[] top_items){

        int max_depth = -1; 
        int col_idx = -1; 
        int orientation = Bottle.PILL_ORIG; 

        for(int i=0; i<Bottle.WIDTH-1; i++){

            int depth_l = top_items[i].depth(); 
            int depth_r = top_items[i+1].depth(); 

            int color_l = top_items[i].top_item()&Bottle.COLOR_MASK; 
            int color_r = top_items[i+1].top_item()&Bottle.COLOR_MASK; 

            boolean v_l = top_items[i].has_virus();
            boolean v_r = top_items[i+1].has_virus();

            if(pill_l==color_l && v_l){
                if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                    if(!top_items[i].top_three_match()){
                        continue; 
                    }
                }
                if(depth_r>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_ORIG; 
                    max_depth = depth_r; 
                }
            }else if(pill_r==color_r && v_r){
                if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                    if(!top_items[i+1].top_three_match()){
                        continue; 
                    }
                }
                if(depth_l>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_ORIG; 
                    max_depth = depth_l; 
                
                }
            }else if(pill_l==color_r && v_r){
                if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                    if(!top_items[i+1].top_three_match()){
                        continue; 
                    }
                }
                if(depth_l>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_REV; 
                    max_depth = depth_l; 
              
                }
            }else if(pill_r==color_l && v_l){
                if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                    if(!top_items[i].top_three_match()){
                        continue; 
                    }
                }
                if(depth_r>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_REV; 
                    max_depth = depth_r; 
                }
            }
        }

        // if(col_idx>=0){
        //     System.out.println("Horizontal_Half_2_Match_With_Virus");
        // }
        return new int[] {col_idx,orientation};
    }



    public static int[] Horizontal_Half_2_Match_With_Virus_Not_Hanging(int pill_l, int pill_r, ItemColumn[] top_items){

        int max_depth = -1; 
        int col_idx = -1; 
        int orientation = Bottle.PILL_ORIG; 

        for(int i=0; i<Bottle.WIDTH-1; i++){

            int depth_l = top_items[i].depth(); 
            int depth_r = top_items[i+1].depth(); 

            int color_l = top_items[i].top_item()&Bottle.COLOR_MASK; 
            int color_r = top_items[i+1].top_item()&Bottle.COLOR_MASK; 

            boolean v_l = top_items[i].has_virus();
            boolean v_r = top_items[i+1].has_virus();

            if(!top_items[i].is_hanging_pill()&&!top_items[i+1].is_hanging_pill()){
                if(pill_l==color_l && v_l){
                    if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                        if(!top_items[i].top_three_match()){
                            continue; 
                        }
                    }
                    if(depth_r>max_depth){
                        col_idx = i; 
                        orientation = Bottle.PILL_ORIG; 
                        max_depth = depth_r; 
                    }
                }else if(pill_r==color_r && v_r){
                    if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                        if(!top_items[i+1].top_three_match()){
                            continue; 
                        }
                    }
                    if(depth_l>max_depth){
                        col_idx = i; 
                        orientation = Bottle.PILL_ORIG; 
                        max_depth = depth_l; 
                    
                    }
                }else if(pill_l==color_r && v_r){
                    if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                        if(!top_items[i+1].top_three_match()){
                            continue; 
                        }
                    }
                    if(depth_l>max_depth){
                        col_idx = i; 
                        orientation = Bottle.PILL_REV; 
                        max_depth = depth_l; 
                
                    }
                }else if(pill_r==color_l && v_l){
                    if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                        if(!top_items[i].top_three_match()){
                            continue; 
                        }
                    }
                    if(depth_r>max_depth){
                        col_idx = i; 
                        orientation = Bottle.PILL_REV; 
                        max_depth = depth_r; 
                    }
                }
            }

        }

        // if(col_idx>=0){
        //     System.out.println("Horizontal_Half_2_Match_With_Virus_Not_Hanging");
        // }
        return new int[] {col_idx,orientation};
    }


    public static int[] Horizontal_Lowest_Half_2_Match(int pill_l, int pill_r, ItemColumn[] top_items){
        int max_depth = -1; 
        int col_idx = -1; 
        int orientation = Bottle.PILL_ORIG; 

        for(int i=0; i<Bottle.WIDTH-1; i++){

            int depth_l = top_items[i].depth(); 
            int depth_r = top_items[i+1].depth();
            
            int d = Math.min(depth_l, depth_r);

            int color_l = top_items[i].top_item()&Bottle.COLOR_MASK; 
            int color_r = top_items[i+1].top_item()&Bottle.COLOR_MASK; 

            if(pill_l==color_l){
                if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                    if(!top_items[i].top_three_match()){
                        continue; 
                    }
                }
                if(d>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_ORIG; 
                    max_depth = d; 
                }
            }else if(pill_r==color_r){
                if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                    if(!top_items[i+1].top_three_match()){
                        continue; 
                    }
                }
                if(d>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_ORIG; 
                    max_depth = d; 
                
                }
            }else if(pill_l==color_r){
                if(reachesTop(Bottle.PILL_ORIG, depth_r)){
                    if(!top_items[i+1].top_three_match()){
                        continue; 
                    }
                }
                if(d>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_REV; 
                    max_depth = d; 
              
                }
            }else if(pill_r==color_l){
                if(reachesTop(Bottle.PILL_ORIG, depth_l)){
                    if(!top_items[i].top_three_match()){
                        continue; 
                    }
                }
                if(d>max_depth){
                    col_idx = i; 
                    orientation = Bottle.PILL_REV; 
                    max_depth = d; 
                }
            }
        }

        // if(col_idx>=0){
        //     System.out.println("Horizontal_Lowest_Half_2_Match");
        // }
        return new int[] {col_idx,orientation};
    }



    public static int Horizontal_2_Non_Match(int pill_l, int pill_r, ItemColumn[] top_items){
        int max_depth = -1; 
        int col_idx = -1;
        for(int i=0; i<Bottle.WIDTH-1; i++){
            int d = Math.min(top_items[i].depth(),top_items[i+1].depth());
            if(d>max_depth){
                col_idx =i; 
                max_depth = d; 
            }
        }

        // if(col_idx>=0){
        //     System.out.println("Horizontal_2_Non_Match");
        // }
        return col_idx; 
    }
    
}
