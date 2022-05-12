package SOLVERS.TVO;
import SOLVERS.Bottle;
import SOLVERS.Placement;

public class GreedyV2 {

    private int orientation = Bottle.PILL_ORIG; 
    private int col_idx = -1; 


    public void processChecks(int pill_l, int pill_r, ItemColumn[] top_items){
        col_idx = -1; 
        
        if(pill_l == pill_r){
            orientation = Bottle.PILL_90_C; 

            col_idx = TVOUtils.Vertical_1_Match_With_Virus(pill_l, pill_r, top_items);

            if(col_idx>=0){
                return; 
            }
  
            col_idx = TVOUtils.Vertical_1_Match(pill_l,pill_r,top_items);

            if(col_idx>=0){
                return; 
            }

            col_idx = TVOUtils.Vertical_1_Non_Match(pill_l, pill_r, top_items); 

        }else{

            int[] idx_orientation; 

            idx_orientation = TVOUtils.Horizontal_Perfect_2_Match_with_Virus(pill_l, pill_r, top_items);
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return; 
            }

            idx_orientation = TVOUtils.Horizontal_Half_2_Match_With_Virus(pill_l, pill_r, top_items);
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return; 
            }

            idx_orientation = TVOUtils.Horizontal_Perfect_2_Match(pill_l, pill_r, top_items);
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return; 
            }
            
            idx_orientation = TVOUtils.Horizontal_Half_2_Match(pill_l, pill_r, top_items); 
            if(idx_orientation[0]>=0){
                col_idx = idx_orientation[0];
                orientation = idx_orientation[1]; 
                return;
            }

            orientation = Bottle.PILL_ORIG; 
            col_idx = TVOUtils.Horizontal_2_Non_Match(pill_l, pill_r, top_items); 
        }

    }

    public Placement ai(int pill_l, int pill_r,  ItemColumn[] top_items){
        processChecks(pill_l, pill_r, top_items);
        return TVOUtils.getPlacement(col_idx, orientation, top_items);
    }
    
}
