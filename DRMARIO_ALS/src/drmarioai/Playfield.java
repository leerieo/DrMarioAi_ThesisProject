package drmarioai;
import static drmarioai.Bottle.*;
import nintaco.api.*;

public class Playfield {

    private final API api = ApiSource.getAPI();

    public int[][] playfield; 

    public int[] top_item_depths;
    public int[] second_item_depths; 

    public boolean[] col_has_virus; 
    public boolean[] row_has_virus; 
    public boolean[] is_hanging_pill; 
    public boolean[] top_items_three_match; 
    public boolean[] top_items_two_match; 


    public int num_viruses; 
    public int num_items; 

    public Playfield(){

        this.num_items=0;
        this.num_viruses=0; 

        this.playfield = new int[HEIGHT+1][WIDTH]; 

        this.top_item_depths = new int[WIDTH]; 
        this.second_item_depths = new int[WIDTH]; 

        this.col_has_virus = new boolean[WIDTH];
        this.row_has_virus = new boolean[HEIGHT];
        this.is_hanging_pill = new boolean[WIDTH];
        this.top_items_three_match = new boolean[WIDTH];
        this.top_items_two_match = new boolean[WIDTH];

        for(int i=0; i<WIDTH; i++){
            this.playfield[HEIGHT][i] = EMPTY_TILE; 
            this.top_item_depths[i] = HEIGHT; 
            this.second_item_depths[i] = HEIGHT; 
            this.col_has_virus[i] = false; 
            this.top_items_three_match[i] = false; 
            this.top_items_two_match[i] = false; 
        }

        for(int i=0; i<HEIGHT; i++){
            this.row_has_virus[i] = false; 
        }
    
    }

    // Reads the bottle bottom-to-top and right-to-left
    public void readPlayfield(){
        
        for(int x = WIDTH - 1; x >= 0; x--) {
            for(int y = HEIGHT - 1; y >= 0; y--) {

                final int tile = api.readCPU(Address.P1_PLAYFIELD | (y << 3) | x);
                this.playfield[y][x] = tile; 

                if((tile&TILE_ID_MASK)==VIRUS){
                    this.col_has_virus[x] = true; 
                    this.row_has_virus[y] = true; 

                    this.num_viruses++; 
                }

                if(tile!=EMPTY_TILE){
                    this.num_items++; 
                }

                int curr_top_depth = this.top_item_depths[x]; 

                if(tile!=EMPTY_TILE && y<curr_top_depth){
                    this.top_item_depths[x] = y; 
                }
    
            }
            checkSecondItemDepth(x);
            checkHangingPill(x);
            checkThreeMatchVer(x);
            checkTwoMatchVer(x);
        }

        
    }

    public void checkHangingPill(int x){

        int y = this.top_item_depths[x]; 

        
        int tile = this.playfield[y][x]; 

        if(tile==EMPTY_TILE || (tile&TILE_ID_MASK) == VIRUS){
            this.is_hanging_pill[x] = false; 
        }else{
            int row = y+1; 
            while(row<16){
    
                if(this.playfield[row][x] == EMPTY_TILE){
                    this.is_hanging_pill[x] = true; 
                    return; 
                }
    
                if((this.playfield[row][x] &TILE_ID_MASK) == VIRUS){
                    this.is_hanging_pill[x]= false; 
                    return; 
                }
                row++; 
            }
        }
    }

    public void checkThreeMatchVer(int x){
        int y = this.top_item_depths[x]; 

        if(this.playfield[y][x]!=EMPTY_TILE){   
            int curr_col = this.playfield[y][x]&COLOR_MASK; 
            int count = 1; 
            y++; 
            while(y<16){
                if(curr_col==(this.playfield[y][x]&COLOR_MASK)){
                    count++; 
                    if(count>=3){
                        this.top_items_three_match[x] = true; 
                        return; 
                    } 
                }else{
                    this.top_items_three_match[x] = false; 
                    return; 
                }
                y++; 
            }
        }
        this.top_items_three_match[x] = false; 
    }


    public void checkTwoMatchVer(int x){
        int y = this.top_item_depths[x]; 

        if(this.playfield[y][x]!=EMPTY_TILE){   
            int curr_col = this.playfield[y][x]&COLOR_MASK; 
            int count = 1; 
            y++; 
            while(y<16){
                if(curr_col==(this.playfield[y][x]&COLOR_MASK)){
                    count++; 
                    if(count>=2){
                        this.top_items_two_match[x] = true; 
                        return; 
                    } 
                }else{
                    this.top_items_two_match[x] = false; 
                    return; 
                }
                y++; 
            }
        }
        this.top_items_two_match[x] = false; 
    }

    public void checkSecondItemDepth(int x){ 
        int y = this.top_item_depths[x]; 

        if(this.playfield[y][x]!=EMPTY_TILE){   
            int curr_col = this.playfield[y][x]&COLOR_MASK; 
            y++; 

            while(y<16){

                int new_col = this.playfield[y][x]&COLOR_MASK; 
                if(curr_col!=new_col){
                    if(this.playfield[y][x]==EMPTY_TILE){
                        curr_col = EMPTY_ID; 
                    }else{
                        second_item_depths[x] = y; 
                        return; 
                    }
                }
                y++; 
            }
        }
        second_item_depths[x] = HEIGHT; 
    }


    public void printPlayfield(){

        System.out.println("PLAYFIELD: ");

        System.out.println("--------------------------");
        for(int y=0; y<HEIGHT; y++) {
            System.out.print("| ");
            for(int x=0; x<WIDTH; x++) {
      
                System.out.print(String.format("%x ",this.playfield[y][x]));
      
            }
            System.out.println("|");
            System.out.println("--------------------------");
            
        }
        
    }

    public void placeVitamin(int pill_l, int pill_r, int row, int col, int orientation){
        switch(orientation){
            case PILL_ORIG:
                this.playfield[row][col] = pill_l | PILL_LEFT; 
                if(col<(WIDTH-1)){
                    this.playfield[row][col+1] = pill_r | PILL_RIGHT; 
                }
                break; 
            case PILL_REV:
                this.playfield[row][col] = pill_r | PILL_LEFT; 
                if(col<(WIDTH-1)){
                    this.playfield[row][col+1] = pill_l | PILL_RIGHT; 
                }
                break; 
            case PILL_90_C: 
                this.playfield[row][col] = pill_r | PILL_BOTTOM; 
                if(row>0){
                    this.playfield[row-1][col] = pill_l | PILL_TOP; 
                }
                break; 
            case PILL_90_CC: 
                this.playfield[row][col] = pill_l | PILL_BOTTOM; 
                if(row>0){
                    this.playfield[row-1][col] = pill_r | PILL_TOP; 
                }
                break;
        }
    }

    public void removeVitamin(int row, int col, int orientation){
        if(orientation%2==0){
            this.playfield[row][col] = EMPTY_TILE; 
            if(col<(WIDTH-1)){
                this.playfield[row][col+1] = EMPTY_TILE; 
            }
        }else{
            this.playfield[row][col] = EMPTY_TILE; 
            if(row>0){
                this.playfield[row-1][col] = EMPTY_TILE; 
            }
            
        }
    }
    
 
}
