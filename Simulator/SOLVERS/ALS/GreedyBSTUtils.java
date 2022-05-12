package SOLVERS.ALS;

import java.util.Queue;

import SOLVERS.Bottle;
import SOLVERS.Placement;

import java.util.ArrayList;
import java.util.LinkedList;

public class GreedyBSTUtils {


    public boolean isVer(int orientation){
        return (orientation % 2) == 1; 
    }

    public int rotate_cw(int orientation){
        if(orientation == Bottle.PILL_ORIG){
            return Bottle.PILL_90_C; 
        }
        return orientation-1; 
    }

    public int rotate_ccw(int orientation){
        return (orientation+1)%4; 
    }

    // Checks if a placement is a landing spot (supported by non-empty tiles)
    public boolean isLandingSpot(int[][] playfield, Placement placement){
        
        int row = placement.getRow(); 
        int col = placement.getCol(); 
        int orientation = placement.getOrientation(); 

        if(row==Bottle.HEIGHT-1){
            return true; 
        }

        int below_tile = playfield[row+1][col]; 

        if(below_tile==Bottle.EMPTY_TILE){
            if(!isVer(orientation)){
                if( col+1<Bottle.WIDTH && playfield[row+1][col+1]!=Bottle.EMPTY_TILE){
                    return true; 
                }
            }
            return false; 
        }

        return true; 
    }


    public ArrayList<Placement> getAllFreeNeighbors(int[][] playfield, Placement placement){

        int row = placement.getRow(); 
        int col = placement.getCol(); 
        int orientation = placement.getOrientation(); 

        ArrayList<Placement> neighbors = new ArrayList<Placement> ();


        //ArrayList<Integer[]> neighbors = new ArrayList<Integer[]>(); 

        if(isVer(orientation)){
            
            // move left  
            if(col>0 && playfield[row][col-1]==Bottle.EMPTY_TILE){
                // check top half 
                if(row<=0|| playfield[row-1][col-1]==Bottle.EMPTY_TILE){
                    Placement new_placement = new Placement(row,col-1,orientation); 
                    neighbors.add(new_placement);
                    
                }
            }

            //right movements + rotate 

            if(col<Bottle.WIDTH-1 && playfield[row][col+1]==Bottle.EMPTY_TILE){
                if(row<=0 || playfield[row-1][col+1] == Bottle.EMPTY_TILE){
                    Placement new_placement = new Placement(row,col+1,orientation); 
                    neighbors.add(new_placement);
                }

                //if we can move right then we can rotate 
                Placement new_placement = new Placement(row,col,rotate_cw(orientation)); 
                neighbors.add(new_placement);
                    
                Placement new_placement_2 = new Placement(row,col,rotate_ccw(orientation)); 
                neighbors.add(new_placement_2 );
                
               
            }
            
            // drop down 
            if(row != Bottle.HEIGHT-1 && playfield[row+1][col]==Bottle.EMPTY_TILE){ 
                Placement new_placement = new Placement(row+1,col,orientation); 
                neighbors.add(new_placement);
            }

            // wall kick rotation
            if(col>=Bottle.WIDTH-1 || playfield[row][col+1] != Bottle.EMPTY_TILE){
                if(col>0 && playfield[row][col-1] == Bottle.EMPTY_TILE){
                    Placement new_placement = new Placement(row,col-1,rotate_cw(orientation)); 
                    neighbors.add(new_placement);

                    Placement new_placement_2 = new Placement(row,col-1,rotate_ccw(orientation)); 
                    neighbors.add(new_placement_2);
                }
            }

        }else{

            // move left 
            if(col>0 && playfield[row][col-1]==Bottle.EMPTY_TILE){
                Placement new_placement = new Placement(row,col-1,orientation); 
                neighbors.add(new_placement);
            }

            //move right 
            if(col<Bottle.WIDTH-2 && playfield[row][col+2]==Bottle.EMPTY_TILE){
                Placement new_placement = new Placement(row,col+1,orientation); 
                neighbors.add(new_placement);
            }

            //move down 
            if(row != Bottle.HEIGHT-1 && playfield[row+1][col]==Bottle.EMPTY_TILE  && playfield[row+1][col+1]==Bottle.EMPTY_TILE){ 
                Placement new_placement = new Placement(row+1,col,orientation); 
                neighbors.add(new_placement);      
            }

            //rotate 
            if (row == 0 || playfield[row-1][col] == Bottle.EMPTY_TILE){
                Placement new_placement = new Placement(row,col,rotate_cw(orientation)); 
                neighbors.add(new_placement);

                Placement new_placement_2 = new Placement(row,col,rotate_ccw(orientation)); 
                neighbors.add(new_placement_2);     
            }
        }

        return neighbors; 
    }

    //returns a list of all possible landings in a playfield
    // a landing is a list of length 3 [row,col,orientaion]

    public ArrayList<Placement> getAllLandings(int[][] playfield){

        ArrayList<Placement> landing_spots = new ArrayList<Placement>(); 

        boolean[][][] visited = new boolean[Bottle.HEIGHT][Bottle.WIDTH][4]; 

        for(int y=0; y<Bottle.HEIGHT; y++){
            for(int x=0; x<Bottle.WIDTH; x++){
                for(int k=0; k<4; k++){
                    visited[y][x][k] = false; 
                }
            }
        }
        
        // get pill starting position 
        Placement start_pos = new Placement(0,3,Bottle.PILL_ORIG); 

        visited[0][3][Bottle.PILL_ORIG] = true; 
        

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
                int row = check.getRow(); 
                int col = check.getCol();
                int orien = check.getOrientation(); 

                if(!visited[row][col][orien]){
                    vistQueue.add(check); 
                    visited[row][col][orien] = true; 
                }
            }
        }
        return landing_spots; 
    }
    
}
