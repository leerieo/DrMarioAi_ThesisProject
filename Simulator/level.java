
import java.util.*;
import java.util.ArrayList;

import SOLVERS.Bottle;
import SOLVERS.Placement;
import SOLVERS.Playfield;
import SOLVERS.ALS.GreedyBSTUtils;
import SOLVERS.ALS.Greedy_BST_V1;
import SOLVERS.ALS.Greedy_BST_V2;
import SOLVERS.ALS.Greedy_BST_V3;
import SOLVERS.ALS.Greedy_BST_V4;
import SOLVERS.TEST.Evaluator1;
import SOLVERS.TVO.GreedyUtils;
import SOLVERS.TVO.GreedyV1;
import SOLVERS.TVO.GreedyV2;
import SOLVERS.TVO.GreedyV3;
import SOLVERS.TVO.GreedyV4;
import SOLVERS.TVO.GreedyV5;
import SOLVERS.TVO.ItemColumn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


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


    private static int num_ver_clears; 
    private static int num_hor_clears; 
    private static int num_vita_placed_top_row; 
    
    
    public static boolean is_vitamin(int tile){
        int id = tile&Bottle.TILE_ID_MASK; 
        if(id==Bottle.VIRUS || id==Bottle.EMPTY_ID){
            return false; 
        }
        return true; 
    }

    public static boolean is_game_over(){
        // System.out.println(Integer.toHexString(playfield[0][3]));
        // System.out.println(Integer.toHexString(playfield[0][4]));
        return playfield[0][3]!=Bottle.EMPTY_TILE || playfield[0][4]!=Bottle.EMPTY_TILE; 
    }

    public static void generate_level(int seed, int level){
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

    public static ArrayList<Placement> getAllLandingSpots(){
        GreedyBSTUtils u = new GreedyBSTUtils();
        return u.getAllLandings(playfield);
    }


    public static void placeVitamin(int vita_l, int vita_r, Placement placement, int[][] p, boolean true_placement){
        int row = placement.getRow(); 
        int col = placement.getCol(); 
        int orientation = placement.getOrientation(); 

        
        switch(orientation){
            case 0:
                p[row][col] = vita_l | Bottle.PILL_LEFT; 
                if(col<(Bottle.WIDTH-1)){
                    p[row][col+1] = vita_r | Bottle.PILL_RIGHT; 
                }
                break; 
            case 2:
                p[row][col] = vita_r | Bottle.PILL_LEFT; 
                if(col<(Bottle.WIDTH-1)){
                    p[row][col+1] = vita_l | Bottle.PILL_RIGHT; 
                }
                break; 
            case 3: 
                if(row>0){
                    p[row][col] = vita_r | Bottle.PILL_BOTTOM; 
                    p[row-1][col] = vita_l | Bottle.PILL_TOP; 
                }else{
                    p[row][col] = vita_r | Bottle.PILL_UNIT; 
                }
                break; 
            case 1: 
                
                if(row>0){
                    p[row][col] = vita_l | Bottle.PILL_BOTTOM; 
                    p[row-1][col] = vita_r | Bottle.PILL_TOP; 
                }else{
                    p[row][col] = vita_l | Bottle.PILL_UNIT; 
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


        if(orientation%2==1 && row<=1){
            if(p[0][col] != Bottle.EMPTY_TILE){
                num_vita_placed_top_row++;
            }

        }else if(orientation%2==0 && row==0){
            if(p[0][col] != Bottle.EMPTY_TILE || p[0][col+1] != Bottle.EMPTY_TILE){
                num_vita_placed_top_row++;
            }
        }

    }

    public static boolean remove_matches(int[][] p, boolean true_placement){
        boolean[][] to_remove = new boolean[Bottle.HEIGHT][Bottle.WIDTH]; 
        // Initialize the removed array to all false
        for(int y=0; y<Bottle.HEIGHT; y++) {
            for(int x=0; x<Bottle.WIDTH; x++) {
                to_remove[y][x] = false;
            }
        }

        //find all horizontal matches 
        for(int y=Bottle.HEIGHT-1; y>=0; y--){
            int counter=0; 
            int curr_color = Bottle.BLACK; 
            for(int x=Bottle.WIDTH-1; x>=0; x--){
                int new_color = p[y][x] & Bottle.COLOR_MASK;
                if(new_color!=curr_color){
                    if(counter>=4 && curr_color != Bottle.BLACK){
                        for(int i=1; i<=counter; i++){
                            to_remove[y][x+i] = true; 
                            num_hor_clears++;
                        }
                    }
                    counter = 1; 
                    curr_color = new_color; 
                }else{
                    counter++; 
                }
            }

            if(counter>=4 && curr_color != Bottle.BLACK){
                for(int i=0; i<counter; i++){
                    to_remove[y][i] = true; 
                    num_hor_clears++;
                }
            }
        }

       //find all vertical matches 
       for(int x=Bottle.WIDTH-1; x>=0; x--){
            int counter=0; 
            int curr_color = Bottle.BLACK; 
            for(int y=Bottle.HEIGHT-1; y>=0; y--){
                int new_color = p[y][x] & Bottle.COLOR_MASK;
                if(new_color!=curr_color){
                    if(counter>=4 && curr_color != Bottle.BLACK){
                        for(int i=1; i<=counter; i++){
                            to_remove[y+i][x] = true; 
                            num_ver_clears++;
                        }
                    }
                    counter = 1; 
                    curr_color = new_color; 
                }else{
                    counter++;
                }
            }

            if(counter>=4 && curr_color != Bottle.BLACK){
                for(int i=0; i<counter; i++){
                    to_remove[i][x] = true; 
                    num_ver_clears++;
                }
            }
        }

        boolean removed_item = false; 
        // remove marked items 
        for(int y = Bottle.HEIGHT - 1; y >= 0; y--) {
            for(int x = Bottle.WIDTH - 1; x >= 0; x--) {
                if (to_remove[y][x]) {
                    remove_item(y, x, p, true_placement);
                    removed_item = true; 
                }
            }
        }
        
        return removed_item; 

    }

  
    public static void remove_item(int row, int col, int[][] p, boolean true_placement){
        int id = p[row][col]&Bottle.TILE_ID_MASK; 

        if(id==Bottle.PILL_LEFT){
            if ((p[row][col + 1] & Bottle.TILE_ID_MASK) == Bottle.PILL_RIGHT) {
                p[row][col + 1] = (p[row][col + 1] & Bottle.COLOR_MASK) | Bottle.PILL_UNIT;
            }
        }else if(id==Bottle.PILL_RIGHT){
            if ((p[row][col - 1] & Bottle.TILE_ID_MASK) == Bottle.PILL_LEFT) {
                p[row][col - 1] = (p[row][col - 1] & Bottle.COLOR_MASK) | Bottle.PILL_UNIT;
            }
        }else if(id==Bottle.PILL_TOP){
            if ((p[row+1][col] & Bottle.TILE_ID_MASK) == Bottle.PILL_BOTTOM) {
                p[row+1][col] = (p[row+1][col] & Bottle.COLOR_MASK) | Bottle.PILL_UNIT;
            }
        }else if(id==Bottle.PILL_BOTTOM){
            if (row>0 && (p[row-1][col] & Bottle.TILE_ID_MASK) == Bottle.PILL_TOP) {
                p[row-1][col] = (p[row-1][col] & Bottle.COLOR_MASK) | Bottle.PILL_UNIT;
            }
        }else if(id==Bottle.VIRUS && true_placement){
            num_viruses--; 
        }

        p[row][col] = Bottle.EMPTY_TILE; 

    }

    public static void settle_bottle(int[][] p){

        for(int col=Bottle.WIDTH-1; col>=0; col--){
            for(int row=Bottle.HEIGHT-2; row>=0; row--){
                if(is_vitamin(p[row][col]) && p[row+1][col]==Bottle.EMPTY_TILE){
                    int id = p[row][col] & Bottle.TILE_ID_MASK;
                    if(id == Bottle.PILL_LEFT){
                        if(p[row+1][col+1]==Bottle.EMPTY_TILE){
                            dropItemHor(col, row,p);
                        }
                    }else if(id != Bottle.PILL_RIGHT){
                        drop_item(col, row,p);
                    }
                }
            }
        }
    }


    public static void drop_item(int col, int row, int[][] p){
        int new_row = row;
        
        for(int i=(row+1); i<Bottle.HEIGHT; i++){
            if(p[i][col] == Bottle.EMPTY_TILE){
                new_row = i; 
            }else{
                break; 
            }
        }

        p[new_row][col] = p[row][col]; 
        p[row][col] = Bottle.EMPTY_TILE; 
    }

    public static void dropItemHor(int col, int row, int[][] p){
        int new_row_l = row;
        
        for(int i=(row+1); i<Bottle.HEIGHT; i++){
            if(p[i][col] == Bottle.EMPTY_TILE){
                new_row_l = i; 
            }else{
                break; 
            }
        }

        int new_row_r = row;
        
        for(int i=(row+1); i<Bottle.HEIGHT; i++){
            if(p[i][col+1] == Bottle.EMPTY_TILE){
                new_row_r = i; 
            }else{
                break; 
            }
        }

        int new_row = Math.min(new_row_l, new_row_r); 


        p[new_row][col] = p[row][col]; 
        p[new_row][col+1] = p[row][col+1]; 

        p[row][col] = Bottle.EMPTY_TILE; 
        p[row][col+1] = Bottle.EMPTY_TILE; 
    }

    public static void process_level(int seed, int l, Boolean print_b, Boolean record, String category, int ver, StringBuilder sb){

        int print_c = 20;

        generate_level(seed, l);

        if(record){
            sb.append(Integer.toHexString(seed)); 
            sb.append(',');
            sb.append(Integer.toString(l));
            sb.append(','); 
        }

        int vitamin_idx = 1; 
        int vitamin_counter = 1; 

        num_hor_clears = 0; 
        num_ver_clears = 0; 
        num_vita_placed_top_row = 0; 
   
        while(!is_game_over() && num_viruses>0){

            if(vitamin_counter>10000){
                break; 
            }

            if(vitamin_idx>=vitamins.length) {
                vitamin_idx = 0; 
            }
            int[] vitamin = vitamins[vitamin_idx]; 
            int vita_l = vitamin[0]; 
            int vita_r = vitamin[1]; 

            Placement loc = null; 

            if(category.equals("TVO")){

                switch(ver){
                    case 1:
                        loc = v1_TVO_evaluator(vita_l, vita_r); 
                        break;
                    case 2:
                        loc = v2_TVO_evaluator(vita_l, vita_r); 
                        break;
                    case 3:
                        loc = v3_TVO_evaluator(vita_l, vita_r); 
                        break;
                    case 4: 
                        loc = v4_TVO_evaluator(vita_l, vita_r); 
                        break;
                    case 5: 
                        loc = v5_TVO_evaluator(vita_l, vita_r); 
                        break;
                    default: 
                        System.out.println("Invalid Version");
                        return; 
                }

            }else if(category.equals("ALS")){

                switch(ver){
                    case 1:
                        loc = v1_evaluator(vita_l, vita_r); 
                        break;
                    case 2:
                        loc = v2_evaluator(vita_l, vita_r); 
                        break;
                    case 3:
                        loc = v3_evaluator(vita_l, vita_r); 
                        break;
                    case 4: 
                        loc = v4_evaluator(vita_l, vita_r); 
                        break;
                    default: 
                        System.out.println("Invalid Version");
                        return; 
                }

            }else{
                System.out.println("Invalid Category");
                return; 
            }

            if(loc == null){
                System.out.println("Null Location Returned");
                print_bottle();
                return; 
            }

            placeVitamin(vita_l, vita_r, loc, playfield,true);
            vitamin_counter++; 
            

            if(print_b && print_c>0){
                // System.out.println("num viruses: "+num_viruses);
                // System.out.println("row: "+loc.getRow()+", col: "+loc.getCol()+", orien: "+loc.getOrientation());
                // System.out.println("pill_num: "+vitamin_counter);
                // System.out.println("num_ver_clears: "+num_ver_clears);
                // System.out.println("num_hor_clears: "+num_hor_clears);
                // System.out.println("num_vita_placed_top_row: "+num_vita_placed_top_row); 

                print_bottle();
                
                print_c--; 
            }
            
            vitamin_idx++; 
        }

        if(record){

            if(seed ==2 && l==6){
                System.out.println(vitamin_counter);
            } 
            sb.append(Integer.toString(vitamin_counter)); 
            sb.append(','); 

            if(num_viruses>0){
                sb.append(0);
            }else{
                sb.append(1);
            }

            sb.append(','); 

            sb.append(num_ver_clears);
            sb.append(','); 

            sb.append(num_hor_clears);
            sb.append(','); 

            sb.append(num_vita_placed_top_row);

            sb.append('\n'); 
        }
        
    }

    public static void print_bottle(){
        for(int r=0; r<Bottle.HEIGHT; r++){
            for(int c=0; c<Bottle.WIDTH; c++){
                int value = playfield[r][c]; 
                int color = value&Bottle.COLOR_MASK; 
                // System.out.print(Integer.toHexString(value)); 
                if(value == Bottle.EMPTY_TILE){
                    System.out.print("-"); 
                }else{
                    if((value&Bottle.TILE_ID_MASK) == Bottle.VIRUS){        
                        if(color==Bottle.YELLOW){
                            System.out.print(ANSI_YELLOW + 'V' + ANSI_RESET);
                        }else if(color==Bottle.RED){
                            System.out.print(ANSI_RED + 'V' + ANSI_RESET);
                        }else if(color==Bottle.BLUE){
                            System.out.print(ANSI_BLUE + 'V' + ANSI_RESET);
                        }
                    }else{
                        char id = 'U';
                        if((value&Bottle.TILE_ID_MASK) == Bottle.PILL_LEFT){
                            id = 'L'; 
                        }else if((value&Bottle.TILE_ID_MASK) == Bottle.PILL_RIGHT){
                            id = 'R'; 
                        }else if((value&Bottle.TILE_ID_MASK) == Bottle.PILL_BOTTOM){
                            id = 'B'; 
                        }else if((value&Bottle.TILE_ID_MASK) == Bottle.PILL_TOP){
                            id = 'T'; 
                        }
                        
                        if(color==Bottle.YELLOW){
                            System.out.print(ANSI_YELLOW_BACKGROUND + ANSI_BLACK + id + ANSI_RESET);
                        }else if(color==Bottle.RED){
                            System.out.print(ANSI_RED_BACKGROUND + ANSI_BLACK + id + ANSI_RESET);
                        }else if(color==Bottle.BLUE){
                            System.out.print(ANSI_BLUE_BACKGROUND + ANSI_BLACK + id + ANSI_RESET);
                        }
                    }

                }
                
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public static Placement dummy_ai(int vita_l, int vita_r, ArrayList<Placement> landingSpots){
        if(landingSpots.size()==0){
            return null; 
        }
        Placement best = landingSpots.get(0);
        int best_d = landingSpots.get(0).getRow();
        for(Placement p : landingSpots){
            if(p.getRow()>best_d){
                best = p; 
                best_d = p.getRow(); 
            }
        }

        return best; 
    }

    public static Placement ai_evaluator1(int vita_l, int vita_r){
        ArrayList<Placement> landingSpots = getAllLandingSpots(); 

        if(landingSpots.size()==0){
            return null; 
        }

        int[][] temp_playfield = new int[Bottle.HEIGHT][Bottle.WIDTH]; 

        Placement best = null; 
        double best_score = Double.MIN_VALUE;
        
        for(Placement p: landingSpots){
            for(int i=0; i<Bottle.HEIGHT; i++){
                System.arraycopy(playfield[i], 0, temp_playfield[i], 0, Bottle.WIDTH);
            }

            placeVitamin(vita_l, vita_r, p, temp_playfield,false);

            double temp_score = Evaluator1.evaluate(temp_playfield, num_viruses);

            if(temp_score>best_score){
                best = p; 
                best_score = temp_score; 
            }
        }

        return best; 

    }

    public static Placement v1_evaluator(int vita_l, int vita_r){

        Playfield p = new Playfield(); 
        p.readPlayfield(playfield);

        Greedy_BST_V1 v1 = new Greedy_BST_V1(); 
        Placement best = v1.ai(vita_l, vita_r,p);

        return best; 
    }


    public static Placement v2_evaluator(int vita_l, int vita_r){

        Playfield p = new Playfield(); 
        p.readPlayfield(playfield);

        Greedy_BST_V2 v2 = new Greedy_BST_V2(); 
        Placement best = v2.ai(vita_l, vita_r,p);

        return best; 
    }

    public static Placement v3_evaluator(int vita_l, int vita_r){

        Playfield p = new Playfield(); 
        p.readPlayfield(playfield);

        Greedy_BST_V3 v3 = new Greedy_BST_V3(); 
        Placement best = v3.ai(vita_l, vita_r,p);

        return best; 
    }

    public static Placement v4_evaluator(int vita_l, int vita_r){

        Playfield p = new Playfield(); 
        p.readPlayfield(playfield);

        Greedy_BST_V4 v4 = new Greedy_BST_V4(); 
        Placement best = v4.ai(vita_l, vita_r,p);

        return best; 
    }



    public static Placement v1_TVO_evaluator(int vita_l, int vita_r){

        // GreedyV1 v1 = new GreedyV1(); 
        GreedyV1 v1 = new GreedyV1();
        
        ItemColumn[] top_items = new ItemColumn[Bottle.WIDTH];
        GreedyUtils gu = new GreedyUtils(); 
        gu.readTopItems(top_items, playfield);

        Placement best =  v1.ai(vita_l, vita_r, top_items);

        return best; 
    }
    public static Placement v2_TVO_evaluator(int vita_l, int vita_r){

        GreedyV2 v2 = new GreedyV2(); 
        
        ItemColumn[] top_items = new ItemColumn[Bottle.WIDTH];
        GreedyUtils gu = new GreedyUtils(); 
        gu.readTopItems(top_items, playfield);

        Placement best =  v2.ai(vita_l, vita_r, top_items);

        return best; 
    }

    public static Placement v3_TVO_evaluator(int vita_l, int vita_r){

        GreedyV3 v3 = new GreedyV3(); 
        
        ItemColumn[] top_items = new ItemColumn[Bottle.WIDTH];
        GreedyUtils gu = new GreedyUtils(); 
        gu.readTopItems(top_items, playfield);

        Placement best =  v3.ai(vita_l, vita_r, top_items);

        return best; 
    }

    public static Placement v4_TVO_evaluator(int vita_l, int vita_r){

        GreedyV4 v4 = new GreedyV4(); 
        
        ItemColumn[] top_items = new ItemColumn[Bottle.WIDTH];
        GreedyUtils gu = new GreedyUtils(); 
        gu.readTopItems(top_items, playfield);

        Playfield p = new Playfield(); 
        p.readPlayfield(playfield);
        
        Placement best =  v4.ai(vita_l, vita_r, p, top_items,0);

        return best; 
    }

    public static Placement v5_TVO_evaluator(int vita_l, int vita_r){

        GreedyV5 v5 = new GreedyV5(); 

        Playfield p = new Playfield(); 
        p.readPlayfield(playfield);

        Placement best =  v5.ai(vita_l, vita_r, p);

        return best; 
    }



}
