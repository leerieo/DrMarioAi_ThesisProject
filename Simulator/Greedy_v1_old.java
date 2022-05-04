
public class Greedy_v1_old {

    private boolean ver; 
    private boolean hor_reverse; 


    public Placement ai(int pill_l, int pill_r, ItemColumn[] top_items){

        ver = false;
        hor_reverse = false; 

        // This should end as a value btwn 0 and 7
        int col_indx = -1;  

        //if vitamin is one color, orient vertical 

        if(pill_l==pill_r){
            ver = true; 
        }

        if(ver){
        
            //Handles one color vertical pills 

            // Try to find a matching color
            
          
            int min_depth = 16; 
            for(int i=0; i<Bottle.WIDTH; i++){
                int item = top_items[i].top_item(); 
                int depth = top_items[i].depth(); 
              
                    if((item&Bottle.COLOR_MASK)==pill_l){
                        if(depth<min_depth){
                            col_indx = i; 
                            min_depth = depth; 
                        }
                    }
                
            }
            

            // Find lowest + leftmost 
            if(col_indx<0){
                int max_depth = -1; 
                for(int i=0; i<Bottle.WIDTH; i++){
                    int depth = top_items[i].depth(); 
                    if(depth>max_depth){
                        col_indx = i; 
                        max_depth = depth; 
                    }
                }
                
            }

        }else{

            // Handles horizontal pills 

            // Try to find any perfect matches
            int min_depth = 16; 
            for(int i=0; i<Bottle.WIDTH-1; i++){
                int item = top_items[i].top_item(); 
                int item2 = top_items[i+1].top_item(); 

                int depth_l = top_items[i].depth(); 
                int depth_r = top_items[i+1].depth(); 
                int depth = Math.min(depth_l,depth_r); 

                if(item != Bottle.EMPTY_TILE){

                    int color_l = item&Bottle.COLOR_MASK; 
                    int color_r = item2&Bottle.COLOR_MASK; 


                    if(pill_l==color_l && pill_r==color_r){
                        if(depth<min_depth){
                            col_indx = i; 
                            min_depth = depth; 
                            hor_reverse = false; 
 
                        }
                    }else if(pill_l==color_r && pill_r==color_l){
                        if(depth<min_depth){
                            col_indx = i; 
                            hor_reverse = true; 
                            min_depth = depth; 
              
                        }
                    }
                }
            }

            if(col_indx<0){
                int max_depth = -1; 

                for(int i=0; i<Bottle.WIDTH-1; i++){

                    int depth_l = top_items[i].depth(); 
                    int depth_r = top_items[i+1].depth(); 

                    int color_l = top_items[i].top_item()&Bottle.COLOR_MASK; 
                    int color_r = top_items[i+1].top_item()&Bottle.COLOR_MASK; 

                    if(pill_l==color_l){
                        if(depth_r>max_depth){
                            col_indx = i; 
                            hor_reverse = false; 
                            max_depth = depth_r; 
                         
                        }
                    }else if(pill_r==color_r){
                        if(depth_l>max_depth){
                            col_indx = i; 
                            hor_reverse = false; 
                            max_depth = depth_l; 
                            
                        }
                    }else if(pill_l==color_r){
                        if(depth_l>max_depth){
                            col_indx = i; 
                            hor_reverse = true; 
                            max_depth = depth_l; 
                          
                        }
                    }else if(pill_r==color_l){
                        if(depth_r>max_depth){
                            col_indx = i; 
                            hor_reverse = true; 
                            max_depth = depth_r; 
            
                        }
                    }
            
                }
            }

            // Try to find lowest non match
            if(col_indx<0){
                int max_depth = -1; 
                for(int i=0; i<Bottle.WIDTH-1; i++){
                    int d = Math.min(top_items[i].depth(),top_items[i+1].depth());
                    if(d>max_depth){
                        col_indx =i; 
                        max_depth = d; 

                    }
                }
            }
        }

        int orientation = Bottle.PILL_ORIG;

        
        if(ver){
            orientation = Bottle.PILL_90_C;
        }else if(hor_reverse){
            orientation = Bottle.PILL_REV; 
        }



        return TVOUtils.getPlacement(col_indx, orientation, top_items);

    }

    
}