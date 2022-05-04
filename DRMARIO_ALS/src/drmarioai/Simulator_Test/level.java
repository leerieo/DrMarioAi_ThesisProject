package drmarioai.Simulator_Test;
import static drmarioai.Bottle.*;
import java.util.*;
import drmarioai.Placement;

public class level {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001b[43m";


    private static int[][] playfield;
    public static int[][] vitamins; 
    
    private static int num_viruses; 

    public int[][] get_playfield(){
        return playfield; 
    }


    public int[][] get_vitamins(){
        return vitamins; 
    }
    
    
    public boolean is_vitamin(int tile){
        int id = tile&TILE_ID_MASK; 
        if(id==VIRUS || id==EMPTY_ID){
            return false; 
        }
        return true; 
    }

    public boolean is_game_over(){
        // System.out.println(Integer.toHexString(playfield[0][3]));
        // System.out.println(Integer.toHexString(playfield[0][4]));
        return playfield[0][3]!=EMPTY_TILE || playfield[0][4]!=EMPTY_TILE; 
    }

    public void generate_level(int seed, int level){
        algHV.random_init(seed);
        algHV.intit_vitamins();
        algHV.generate_vitamins();
        vitamins = algHV.getVitamins(); 
        algHV.init_bottle(level);
        algHV.fill_bottle(); 
        playfield = algHV.getBottle(); 
        Collections.reverse(Arrays.asList(playfield));
        num_viruses = algHV.getNumViruses(); 
    }

    public void placeVitamin(int vita_l, int vita_r, Placement placement, int[][] p, boolean true_placement){
        int row = placement.getRow(); 
        int col = placement.getCol(); 
        int orientation = placement.getOrientation(); 

        
        switch(orientation){
            case 0:
                p[row][col] = vita_l | PILL_LEFT; 
                if(col<(WIDTH-1)){
                    p[row][col+1] = vita_r | PILL_RIGHT; 
                }
                break; 
            case 2:
                p[row][col] = vita_r | PILL_LEFT; 
                if(col<(WIDTH-1)){
                    p[row][col+1] = vita_l | PILL_RIGHT; 
                }
                break; 
            case 3: 
                if(row>0){
                    p[row][col] = vita_r | PILL_BOTTOM; 
                    p[row-1][col] = vita_l | PILL_TOP; 
                }else{
                    p[row][col] = vita_r | PILL_UNIT; 
                }
                break; 
            case 1: 
                
                if(row>0){
                    p[row][col] = vita_l | PILL_BOTTOM; 
                    p[row-1][col] = vita_r | PILL_TOP; 
                }else{
                    p[row][col] = vita_l | PILL_UNIT; 
                }
                break;
        }
        if(true_placement){
            // print_bottle();
        }
        // ArrayList<Integer> removed_items_cols = resolveMatches_Vitamin(placement, vita_l, vita_r);

        while(remove_matches(p,true_placement)){
            // settle bottle 
            settle_bottle(p);
            settle_bottle(p);
        }
    }

    public boolean remove_matches(int[][] p, boolean true_placement){
        boolean[][] to_remove = new boolean[HEIGHT][WIDTH]; 
        // Initialize the removed array to all false
        for(int y=0; y<HEIGHT; y++) {
            for(int x=0; x<WIDTH; x++) {
                to_remove[y][x] = false;
            }
        }

        //find all horizontal matches 
        for(int y=HEIGHT-1; y>=0; y--){
            int counter=0; 
            int curr_color = BLACK; 
            for(int x=WIDTH-1; x>=0; x--){
                int new_color = p[y][x] & COLOR_MASK;
                if(new_color!=curr_color){
                    if(counter>=4 && curr_color != BLACK){
                        for(int i=1; i<=counter; i++){
                            to_remove[y][x+i] = true; 
                        }
                    }
                    counter = 1; 
                    curr_color = new_color; 
                }else{
                    counter++; 
                }
            }

            if(counter>=4 && curr_color != BLACK){
                for(int i=0; i<counter; i++){
                    to_remove[y][i] = true; 
                }
            }
        }

       //find all vertical matches 
       for(int x=WIDTH-1; x>=0; x--){
            int counter=0; 
            int curr_color = BLACK; 
            for(int y=HEIGHT-1; y>=0; y--){
                int new_color = p[y][x] & COLOR_MASK;
                if(new_color!=curr_color){
                    if(counter>=4 && curr_color != BLACK){
                        for(int i=1; i<=counter; i++){
                            to_remove[y+i][x] = true; 
                        }
                    }
                    counter = 1; 
                    curr_color = new_color; 
                }else{
                    counter++;
                }
            }

            if(counter>=4 && curr_color != BLACK){
                for(int i=0; i<counter; i++){
                    to_remove[i][x] = true; 
                }
            }
        }

        boolean removed_item = false; 
        // remove marked items 
        for(int y = HEIGHT - 1; y >= 0; y--) {
            for(int x = WIDTH - 1; x >= 0; x--) {
                if (to_remove[y][x]) {
                    remove_item(y, x, p, true_placement);
                    removed_item = true; 
                }
            }
        }
        
        return removed_item; 

    }

  
    public void remove_item(int row, int col, int[][] p, boolean true_placement){
        int id = p[row][col]&TILE_ID_MASK; 

        if(id==PILL_LEFT){
            if ((p[row][col + 1] & TILE_ID_MASK) == PILL_RIGHT) {
                p[row][col + 1] = (p[row][col + 1] & COLOR_MASK) | PILL_UNIT;
            }
        }else if(id==PILL_RIGHT){
            if ((p[row][col - 1] & TILE_ID_MASK) == PILL_LEFT) {
                p[row][col - 1] = (p[row][col - 1] & COLOR_MASK) | PILL_UNIT;
            }
        }else if(id==PILL_TOP){
            if ((p[row+1][col] & TILE_ID_MASK) == PILL_BOTTOM) {
                p[row+1][col] = (p[row+1][col] & COLOR_MASK) | PILL_UNIT;
            }
        }else if(id==PILL_BOTTOM){
            if (row>0 && (p[row-1][col] & TILE_ID_MASK) == PILL_TOP) {
                p[row-1][col] = (p[row-1][col] & COLOR_MASK) | PILL_UNIT;
            }
        }else if(id==VIRUS && true_placement){
            num_viruses--; 
        }

        p[row][col] = EMPTY_TILE; 

    }

    public void settle_bottle(int[][] p){

        for(int col=WIDTH-1; col>=0; col--){
            for(int row=HEIGHT-2; row>=0; row--){
                if(is_vitamin(p[row][col]) && p[row+1][col]==EMPTY_TILE){
                    int id = p[row][col] & TILE_ID_MASK;
                    if(id == PILL_LEFT){
                        if(p[row+1][col+1]==EMPTY_TILE){
                            dropItemHor(col, row,p);
                        }
                    }else if(id != PILL_RIGHT){
                        drop_item(col, row,p);
                    }
                }
            }
        }
    }


    public void drop_item(int col, int row, int[][] p){
        int new_row = row;
        
        for(int i=(row+1); i<HEIGHT; i++){
            if(p[i][col] == EMPTY_TILE){
                new_row = i; 
            }else{
                break; 
            }
        }

        p[new_row][col] = p[row][col]; 
        p[row][col] = EMPTY_TILE; 
    }

    public void dropItemHor(int col, int row, int[][] p){
        int new_row_l = row;
        
        for(int i=(row+1); i<HEIGHT; i++){
            if(p[i][col] == EMPTY_TILE){
                new_row_l = i; 
            }else{
                break; 
            }
        }

        int new_row_r = row;
        
        for(int i=(row+1); i<HEIGHT; i++){
            if(p[i][col+1] == EMPTY_TILE){
                new_row_r = i; 
            }else{
                break; 
            }
        }

        int new_row = Math.min(new_row_l, new_row_r); 


        p[new_row][col] = p[row][col]; 
        p[new_row][col+1] = p[row][col+1]; 

        p[row][col] = EMPTY_TILE; 
        p[row][col+1] = EMPTY_TILE; 
    }

    // public void process_level(int seed, int l, Placement loc){
    //     //generate_level(seed, l);
    //     int vitamin_idx = 0; 
    //     // print_bottle();
    //     // System.out.println("is_game_over: "+is_game_over());
    //     // System.out.println("num_viruses: "+num_viruses);
    //     while(!is_game_over() && num_viruses>0){
    //         if(vitamin_idx>=vitamins.length) {
    //             vitamin_idx = 0; 
    //         }
    //         int[] vitamin = vitamins[vitamin_idx]; 
    //         int vita_l = vitamin[0]; 
    //         int vita_r = vitamin[1]; 

    //         placeVitamin(vita_l, vita_r, loc, playfield,true);

    //         vitamin_idx++; 
    //     }
    // }

    public void step_level(int vitamin_idx, Placement loc){
        if(vitamin_idx>=vitamins.length) {
            vitamin_idx-=vitamins.length; 
        }
        int[] vitamin = vitamins[vitamin_idx]; 
        int vita_l = vitamin[0]; 
        int vita_r = vitamin[1]; 

        placeVitamin(vita_l, vita_r, loc, playfield, true);
    }


    public void printPlayfield(){

        System.out.println("PLAYFIELD: ");

        System.out.println("--------------------------");
        for(int y=0; y<HEIGHT; y++) {
            System.out.print("| ");
            for(int x=0; x<WIDTH; x++) {
      
                System.out.print(String.format("%x ",playfield[y][x]));
      
            }
            System.out.println("|");
            System.out.println("--------------------------");
            
        }
        
    }

    public void print_bottle(){
        for(int r=0; r<HEIGHT; r++){
            for(int c=0; c<WIDTH; c++){
                int value = playfield[r][c]; 
                int color = value&COLOR_MASK; 
                // System.out.print(Integer.toHexString(value)); 
                if(value == EMPTY_TILE){
                    System.out.print("-"); 
                }else{
                    if((value&TILE_ID_MASK) == VIRUS){        
                        if(color==YELLOW){
                            System.out.print(ANSI_YELLOW + 'V' + ANSI_RESET);
                        }else if(color==RED){
                            System.out.print(ANSI_RED + 'V' + ANSI_RESET);
                        }else if(color==BLUE){
                            System.out.print(ANSI_BLUE + 'V' + ANSI_RESET);
                        }
                    }else{
                        char id = 'U';
                        if((value&TILE_ID_MASK) == PILL_LEFT){
                            id = 'L'; 
                        }else if((value&TILE_ID_MASK) == PILL_RIGHT){
                            id = 'R'; 
                        }else if((value&TILE_ID_MASK) == PILL_BOTTOM){
                            id = 'B'; 
                        }else if((value&TILE_ID_MASK) == PILL_TOP){
                            id = 'T'; 
                        }
                        
                        if(color==YELLOW){
                            System.out.print(ANSI_YELLOW_BACKGROUND + ANSI_BLACK + id + ANSI_RESET);
                        }else if(color==RED){
                            System.out.print(ANSI_RED_BACKGROUND + ANSI_BLACK + id + ANSI_RESET);
                        }else if(color==BLUE){
                            System.out.print(ANSI_BLUE_BACKGROUND + ANSI_BLACK + id + ANSI_RESET);
                        }
                    }

                }
                
            }
            System.out.println("");
        }
        System.out.println("");
    }

}
