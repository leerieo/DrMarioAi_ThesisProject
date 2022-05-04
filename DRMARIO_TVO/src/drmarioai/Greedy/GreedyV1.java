package drmarioai.Greedy;
import java.util.Queue;
import java.util.LinkedList;

import static drmarioai.Bottle.*;

public class GreedyV1 {


    private int orientation = PILL_ORIG; 
    private int col_idx = -1; 

    public void processChecks(int pill_l, int pill_r, ItemColumn[] top_items){
        col_idx = -1; 
        
        if(pill_l == pill_r){
            orientation = PILL_90_C; 
  
            col_idx = TVO_Utils.Vertical_1_Match(pill_l,pill_r,top_items);

            if(col_idx>=0){
                return; 
            }

            col_idx = TVO_Utils.Vertical_1_Non_Match(pill_l, pill_r, top_items); 

        }else{

            int[] idx_orientation; 

            idx_orientation = TVO_Utils.Horizontal_Perfect_2_Match(pill_l, pill_r, top_items);
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

    public Queue<String> ai(int pill_l, int pill_r, ItemColumn[] top_items){

        processChecks(pill_l, pill_r, top_items);
        return TVO_Utils.getMoveQueue(col_idx, orientation); 

    }

    
}
