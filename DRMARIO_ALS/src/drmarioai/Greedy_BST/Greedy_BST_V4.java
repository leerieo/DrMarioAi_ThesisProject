package drmarioai.Greedy_BST;
import java.util.Queue;

import drmarioai.Placement;
import drmarioai.Playfield;
import drmarioai.PlayfieldEvaluator;
import drmarioai.ProcessBottle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Greedy_BST_V4 {

    private final GreedyBSTUtils u = new GreedyBSTUtils();
    private final PlayfieldEvaluator evaluator = new PlayfieldEvaluator();
    ProcessBottle p = new ProcessBottle(); 

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

                // System.out.println("POS: "+ Arrays.toString(placements.get(i).pos));
                
                p.setPlayfield(playfield.playfield,playfield.num_viruses,playfield.num_items);
                boolean removed_item = p.placeVitaminProcess(pill_l, pill_r, row, col, orientation);

   

                    // p.printPlayfield();
                final double score = evaluator.evaluate(p, removed_item);
                // System.out.println("SCORE: "+ score);
    
                if(score>max_score){
                    // System.out.println("NUM VIRUSES: "+p.numViruses()); 
                    // System.out.println("NUM EMPTY SPACES: "+p.numEmptyTiles()); 
                        

                    max_score = score; 
                    best_placement = placements.get(i);
                }
                

            }
            System.out.println("");
            System.out.println("BEST SCORE: "+max_score);
            System.out.println("BEST POS: "+Arrays.toString(best_placement.pos));

            int row = best_placement.getRow(); 
            int col = best_placement.getCol();
            int orientation = best_placement.getOrientation(); 

            ProcessBottle p = new ProcessBottle(); 
            p.setPlayfield(playfield.playfield,playfield.num_viruses,playfield.num_items);
            p.placeVitaminProcess(pill_l, pill_r, row, col, orientation);
            // p.printPlayfield();
        
            // int d_counter = 0; 
            for(int i=0; i<best_placement.move_list.size(); i++){
                moveQueue.add(best_placement.move_list.get(i)); 
            }
            
            System.out.println("BEST MOVE QUEUE: "+moveQueue.toString()); 
        }

        return moveQueue; 

    }


    // private boolean isVerMatch(int[][] playfield, int row, int col, int orientation, boolean[] col_has_virus){

    //     if(orientation%2==0){

    //         if(!col_has_virus[col] && !col_has_virus[col+1]){
    //             return false; 
    //         }

    //         //horizontal 
    //         int color_l = playfield[row][col] & COLOR_MASK; 
    //         int color_r = playfield[row][col+1] & COLOR_MASK; 

    //         boolean match_l = false; 

    //         //check beneath left pill 
    //         for(int i=(row+1); i<HEIGHT; i++){
    //             int tile = playfield[i][col]; 
    //             // same color 
    //             if((tile&COLOR_MASK)==color_l){
    //                 match_l = true; 
    //                 break; 
    //             }else if(tile!=EMPTY_TILE){
    //                 // stop scoring if we encounter a non-empty tile of a non-matching color 
    //                 break; 
    //             }
    //         }

    //         boolean match_r = false; 
    //         //check beneath right pill 
    //         for(int i=(row+1); i<HEIGHT; i++){
    //             int tile = playfield[i][col+1]; 
    //             // same color 
    //             if((tile&COLOR_MASK)==color_r){
    //                 match_r = true; 
    //                 break; 
    //             }else if(tile!=EMPTY_TILE){
    //                 // stop scoring if we encounter a non-empty tile of a non-matching color 
    //                 break; 
    //             }
    //         }

    //         if(match_l && match_r){
    //             return true; 
    //         }else if(match_l && !col_has_virus[col+1]){
    //             return true; 
    //         }else if(match_r && !col_has_virus[col]){
    //             return true; 
    //         }

    //         return false;  

    //     }else if(row>0){

    //         if(!col_has_virus[col]){
    //             return false; 
    //         }

    //         //vertical 
    //         int color_b = playfield[row][col] & COLOR_MASK; 
    //         int color_t = playfield[row-1][col] & COLOR_MASK; 

    //         boolean match_bottom = false; 

    //         // check under the vitamin 
    //         if((row<HEIGHT-1)){
    //             int tile = playfield[row+1][col]; 
    //             if((tile&COLOR_MASK)==color_b){
    //                 match_bottom = true; 
    //             }
    //         }

    //         if(color_b==color_t && match_bottom){
    //             return true;
    //         }

    //         //check above the vitamin 
    //         boolean match_top = false; 
    //         if(row>1){
    //             if((playfield[row-2][col]&COLOR_MASK)==color_t){
    //                 match_top = true; 
    //             }

    //         }

    //         return match_bottom && match_top; 

    //     }else{

    //         if(!col_has_virus[col]){
    //             return false; 
    //         }

    //         int color_b = playfield[row][col] & COLOR_MASK; 

    //         // check under the vitamin 
    //         int tile = playfield[row+1][col]; 
    //         if((tile&COLOR_MASK)==color_b){
    //             return true; 
    //         }
    //         return false; 
          
    //     }

    // }
    
}
