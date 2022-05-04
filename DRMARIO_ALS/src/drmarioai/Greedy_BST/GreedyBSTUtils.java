package drmarioai.Greedy_BST;
import static drmarioai.Bottle.*;
import java.util.Queue;

import drmarioai.Placement;

import java.util.ArrayList;
import java.util.LinkedList;

public class GreedyBSTUtils {

    public static boolean reachesTop(int orientation, int row){
        if(orientation%2==0){
            //horizontal 
            return ((row-1)<=0);
        }else{
            //vertical 
            return ((row-2)<=0); 
        }
    }

    public boolean isVer(int orientation){
        return (orientation % 2) == 1; 
    }

    public int rotate_cw(int orientation){

        if(orientation == PILL_ORIG){
            return PILL_90_C; 
        }
        return orientation-1; 
    }

    public int rotate_ccw(int orientation){
        return (orientation+1)%4; 
    }

    public boolean isLandingSpot(int[][] playfield, Placement placement){
        Integer[] pos = placement.pos; 
        int row = pos[0]; 
        int col = pos[1]; 
        int orientation = pos[2]; 

        if(row==HEIGHT-1){
            return true; 
        }

        int below_tile = playfield[row+1][col]; 

        // if(row == 0){
        //     System.out.println("row: "+row+" col: "+col+" orien: "+orientation);
        //     System.out.println(below_tile);
        // }

        if(below_tile==EMPTY_TILE){
            if(!isVer(orientation)){
                if( col+1<WIDTH && playfield[row+1][col+1]!=EMPTY_TILE){
                    // System.out.println("row: "+row+" col: "+col+" orien: "+orientation);
                    // System.out.println(below_tile);
                    return true; 
                }
            }
            return false; 
        }
        // System.out.println("row: "+row+" col: "+col+" orien: "+orientation);
        // System.out.println(below_tile);
        return true; 

    }


    public ArrayList<Placement> getAllFreeNeighbors(int[][] playfield, Placement placement){

        Integer[] pos = placement.pos; 
        ArrayList<String> curr_movelist = placement.move_list;  

        int row = pos[0]; 
        int col = pos[1]; 
        int orientation = pos[2]; 

        ArrayList<Placement> neighbors = new ArrayList<Placement> ();


        //ArrayList<Integer[]> neighbors = new ArrayList<Integer[]>(); 

        if(isVer(orientation)){
            
            // move left  
            if(col>0 && playfield[row][col-1]==EMPTY_TILE){
                // check top half 
                if(row<=0|| playfield[row-1][col-1]==EMPTY_TILE){
                    Integer[] left = {row,col-1,orientation};
                    ArrayList<String> new_movelist_l = new ArrayList<String>(curr_movelist);
                    new_movelist_l.add("l");

                    Placement new_placement = new Placement(left,new_movelist_l); 
                    neighbors.add(new_placement);
                    
                }
            }

            //right movements + rotate 

            if(col<WIDTH-1 && playfield[row][col+1]==EMPTY_TILE){
                if(row<=0 || playfield[row-1][col+1] == EMPTY_TILE){
                    Integer[] right = {row,col+1,orientation};
                    
                    ArrayList<String> new_movelist_r = new ArrayList<String>(curr_movelist);
                    new_movelist_r.add("r");

                    Placement new_placement = new Placement(right,new_movelist_r); 
                    neighbors.add(new_placement);
                 
                }

                //if we can move right then we can rotate 
                Integer[] cw = {row,col,rotate_cw(orientation)};
                ArrayList<String> new_movelist_cw = new ArrayList<String>(curr_movelist);
                new_movelist_cw.add("cw");
    
                Placement new_placement = new Placement(cw,new_movelist_cw); 
                neighbors.add(new_placement);

                Integer[] ccw = {row,col,rotate_ccw(orientation)};
                ArrayList<String> new_movelist_ccw = new ArrayList<String>(curr_movelist);
                new_movelist_ccw.add("ccw");
                    
                Placement new_placement_2 = new Placement(ccw,new_movelist_ccw); 
                neighbors.add(new_placement_2 );
                
               
            }
            
            // drop down 
            if(row != HEIGHT-1 && playfield[row+1][col]==EMPTY_TILE){ 
                Integer[] down = {row+1,col,orientation};
                ArrayList<String> new_movelist_d = new ArrayList<String>(curr_movelist);
                new_movelist_d.add("d");
                Placement new_placement = new Placement(down,new_movelist_d); 
                neighbors.add(new_placement);
            }

            // wall kick rotation
            if(col>=WIDTH-1 || playfield[row][col+1] != EMPTY_TILE){
                if(col>0 && playfield[row][col-1] == EMPTY_TILE){
                    Integer[] cw = {row,col-1,rotate_cw(orientation)};
                    ArrayList<String> new_movelist_cw = new ArrayList<String>(curr_movelist);
                    new_movelist_cw.add("cw");
                    Placement new_placement = new Placement(cw,new_movelist_cw); 
                    neighbors.add(new_placement);

                    Integer[] ccw = {row,col-1,rotate_ccw(orientation)};
                    ArrayList<String> new_movelist_ccw = new ArrayList<String>(curr_movelist);
                    new_movelist_ccw.add("ccw");
                    Placement new_placement_2 = new Placement(ccw,new_movelist_ccw); 
                    neighbors.add(new_placement_2);
                }
            }

        }else{

            // move left 
            if(col>0 && playfield[row][col-1]==EMPTY_TILE){
                Integer[] left = {row,col-1,orientation};
                ArrayList<String> new_movelist_l = new ArrayList<String>(curr_movelist);
                new_movelist_l.add("l");
                Placement new_placement = new Placement(left,new_movelist_l); 
                neighbors.add(new_placement);
            }

            //move right 
            if(col<WIDTH-2 && playfield[row][col+2]==EMPTY_TILE){
                Integer[] right = {row,col+1,orientation};
                ArrayList<String> new_movelist_r = new ArrayList<String>(curr_movelist);
                new_movelist_r.add("r");
                Placement new_placement = new Placement(right,new_movelist_r); 
                neighbors.add(new_placement);
            }

            //move down 
            if(row != HEIGHT-1 && playfield[row+1][col]==EMPTY_TILE  && playfield[row+1][col+1]==EMPTY_TILE){ 
                Integer[] down = {row+1,col,orientation};
                ArrayList<String> new_movelist_d = new ArrayList<String>(curr_movelist);
                new_movelist_d.add("d");
                Placement new_placement = new Placement(down,new_movelist_d); 
                neighbors.add(new_placement);      
            }

            //rotate 
            if (row == 0 || playfield[row-1][col] == EMPTY_TILE){
                Integer[] cw = {row,col,rotate_cw(orientation)};
                ArrayList<String> new_movelist_cw = new ArrayList<String>(curr_movelist);
                new_movelist_cw.add("cw");
                Placement new_placement = new Placement(cw,new_movelist_cw); 
                neighbors.add(new_placement);

                Integer[] ccw = {row,col,rotate_ccw(orientation)};
                ArrayList<String> new_movelist_ccw = new ArrayList<String>(curr_movelist);
                new_movelist_ccw.add("ccw");
                Placement new_placement_2 = new Placement(ccw,new_movelist_ccw); 
                neighbors.add(new_placement_2);     
            }
        }

        return neighbors; 
    }

    //returns a list of all possible landings in a playfield
    // a landing is a list of length 3 [row,col,orientaion]

    public ArrayList<Placement> getAllLandings(int[][] playfield){

        ArrayList<Placement> landing_spots = new ArrayList<Placement>(); 

        boolean[][][] visited = new boolean[HEIGHT][WIDTH][4]; 

        for(int y=0; y<HEIGHT; y++){
            for(int x=0; x<WIDTH; x++){
                for(int k=0; k<4; k++){
                    visited[y][x][k] = false; 
                }
            }
        }
        
        // get pill starting position 
        Integer[] sp= {0,3,PILL_ORIG};
        ArrayList<String> start_move_list = new ArrayList<String>();
        Placement start_pos = new Placement(sp,start_move_list); 

        visited[0][3][PILL_ORIG] = true; 
        

        Queue<Placement> vistQueue = new LinkedList<Placement>();
      

        vistQueue.add(start_pos); 
       

        while(!vistQueue.isEmpty()){
            Placement currPos = vistQueue.remove();
    

            if(isLandingSpot(playfield, currPos)){
                landing_spots.add(currPos);
            }
            ArrayList<Placement> neighbors = new ArrayList<Placement>(); 
            neighbors = getAllFreeNeighbors(playfield, currPos); 

            for(int i=0; i<neighbors.size(); i++){
                Placement check = neighbors.get(i);
                int row = check.pos[0];
                int col = check.pos[1];
                int orien = check.pos[2];

                // System.out.println("row: "+row+" col: "+col+" orien: "+orien);

                if(!visited[row][col][orien]){
                    vistQueue.add(check); 
                    visited[row][col][orien] = true; 
                }
            }
        }
        return landing_spots; 
    }
    
}
