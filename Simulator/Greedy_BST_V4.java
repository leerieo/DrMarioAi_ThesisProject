

import java.util.ArrayList;



public class Greedy_BST_V4 {

    private final GreedyBSTUtils u = new GreedyBSTUtils();
    private final PlayfieldEvaluator evaluator = new PlayfieldEvaluator();
    ProcessBottle p = new ProcessBottle(); 

    public Placement ai(int pill_l, int pill_r, Playfield playfield){

        Placement best_placement = null; 

        ArrayList<Placement> placements = u.getAllLandings(playfield.playfield);

        if(placements.size()>0){
            double max_score = -Double.MAX_VALUE; 
            for(int i=0; i<placements.size(); i++){
                int row = placements.get(i).getRow(); 
                int col = placements.get(i).getCol();
                int orientation = placements.get(i).getOrientation(); 
                
                p.setPlayfield(playfield.playfield,playfield.num_viruses,playfield.num_items);
                boolean removed_item = p.placeVitaminProcess(pill_l, pill_r, row, col, orientation);

               
               
                final double score = evaluator.evaluate(p, removed_item);
    
                if(score>max_score){
                    max_score = score; 
                    best_placement = placements.get(i); 
                }
                

            }
        }

        if(best_placement == null){
            System.out.println(placements.size());
        }
        

        return best_placement; 

    }

}
