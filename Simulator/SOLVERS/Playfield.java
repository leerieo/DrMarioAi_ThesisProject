package SOLVERS;
public class Playfield {

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

        this.playfield = new int[Bottle.HEIGHT+1][Bottle.WIDTH]; 

        this.top_item_depths = new int[Bottle.WIDTH]; 
        this.second_item_depths = new int[Bottle.WIDTH]; 

        this.col_has_virus = new boolean[Bottle.WIDTH];
        this.row_has_virus = new boolean[Bottle.HEIGHT];
        this.is_hanging_pill = new boolean[Bottle.WIDTH];
        this.top_items_three_match = new boolean[Bottle.WIDTH];
        this.top_items_two_match = new boolean[Bottle.WIDTH];

        for(int i=0; i<Bottle.WIDTH; i++){
            this.playfield[Bottle.HEIGHT][i] = Bottle.EMPTY_TILE; 
            this.top_item_depths[i] = Bottle.HEIGHT; 
            this.second_item_depths[i] = Bottle.HEIGHT; 
            this.col_has_virus[i] = false; 
            this.top_items_three_match[i] = false; 
            this.top_items_two_match[i] = false; 
        }

        for(int i=0; i<Bottle.HEIGHT; i++){
            this.row_has_virus[i] = false; 
        }
    
    }

    // Reads the bottle bottom-to-top and right-to-left
    public void readPlayfield(int[][] p){
        
        for(int x = Bottle.WIDTH - 1; x >= 0; x--) {
            for(int y = Bottle.HEIGHT - 1; y >= 0; y--) {

                final int tile = p[y][x];
                this.playfield[y][x] = tile; 

                if((tile&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                    this.col_has_virus[x] = true; 
                    this.row_has_virus[y] = true; 

                    this.num_viruses++; 
                }

                if(tile!=Bottle.EMPTY_TILE){
                    this.num_items++; 
                }

                int curr_top_depth = this.top_item_depths[x]; 

                if(tile!=Bottle.EMPTY_TILE && y<curr_top_depth){
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

        if(tile==Bottle.EMPTY_TILE || (tile&Bottle.TILE_ID_MASK) == Bottle.VIRUS){
            this.is_hanging_pill[x] = false; 
        }else{
            int row = y+1; 
            while(row<16){
    
                if(this.playfield[row][x] == Bottle.EMPTY_TILE){
                    this.is_hanging_pill[x] = true; 
                    return; 
                }
    
                if((this.playfield[row][x] &Bottle.TILE_ID_MASK) == Bottle.VIRUS){
                    this.is_hanging_pill[x]= false; 
                    return; 
                }
                row++; 
            }
        }
    }

    public void checkThreeMatchVer(int x){
        int y = this.top_item_depths[x]; 

        if(this.playfield[y][x]!=Bottle.EMPTY_TILE){   
            int curr_col = this.playfield[y][x]&Bottle.COLOR_MASK; 
            int count = 1; 
            y++; 
            while(y<16){
                if(curr_col==(this.playfield[y][x]&Bottle.COLOR_MASK)){
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

        if(this.playfield[y][x]!=Bottle.EMPTY_TILE){   
            int curr_col = this.playfield[y][x]&Bottle.COLOR_MASK; 
            int count = 1; 
            y++; 
            while(y<16){
                if(curr_col==(this.playfield[y][x]&Bottle.COLOR_MASK)){
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

        if(this.playfield[y][x]!=Bottle.EMPTY_TILE){   
            int curr_col = this.playfield[y][x]&Bottle.COLOR_MASK; 
            y++; 

            while(y<16){

                int new_col = this.playfield[y][x]&Bottle.COLOR_MASK; 
                if(curr_col!=new_col){
                    if(this.playfield[y][x]==Bottle.EMPTY_TILE){
                        curr_col = Bottle.EMPTY_ID; 
                    }else{
                        second_item_depths[x] = y; 
                        return; 
                    }
                }
                y++; 
            }
        }
        second_item_depths[x] = Bottle.HEIGHT; 
    }


    public void printPlayfield(){

        System.out.println("PLAYFIELD: ");

        System.out.println("--------------------------");
        for(int y=0; y<Bottle.HEIGHT; y++) {
            System.out.print("| ");
            for(int x=0; x<Bottle.WIDTH; x++) {
      
                System.out.print(String.format("%x ",this.playfield[y][x]));
      
            }
            System.out.println("|");
            System.out.println("--------------------------");
            
        }
        
    }

    public void placeVitamin(int pill_l, int pill_r, int row, int col, int orientation){

        switch(orientation){
            case 0:
                //PILL_ORIG
                this.playfield[row][col] = pill_l | Bottle.PILL_LEFT; 
                if(col<(Bottle.WIDTH-1)){
                    this.playfield[row][col+1] = pill_r | Bottle.PILL_RIGHT; 
                }
                break; 
            case 2:
                //PILL_REV
                this.playfield[row][col] = pill_r | Bottle.PILL_LEFT; 
                if(col<(Bottle.WIDTH-1)){
                    this.playfield[row][col+1] = pill_l | Bottle.PILL_RIGHT; 
                }
                break; 
            case 3: 
                //PILL_90_C
                this.playfield[row][col] = pill_r | Bottle.PILL_BOTTOM; 
                if(row>0){
                    this.playfield[row-1][col] = pill_l | Bottle.PILL_TOP; 
                }
                break; 
            case 1: 
                //PILL_90_CC
                this.playfield[row][col] = pill_l | Bottle.PILL_BOTTOM; 
                if(row>0){
                    this.playfield[row-1][col] = pill_r | Bottle.PILL_TOP; 
                }
                break;
        }
    }

    public void removeVitamin(int row, int col, int orientation){
        if(orientation%2==0){
            this.playfield[row][col] = Bottle.EMPTY_TILE; 
            if(col<(Bottle.WIDTH-1)){
                this.playfield[row][col+1] = Bottle.EMPTY_TILE; 
            }
        }else{
            this.playfield[row][col] = Bottle.EMPTY_TILE; 
            if(row>0){
                this.playfield[row-1][col] = Bottle.EMPTY_TILE; 
            }
            
        }
    }
    
 
}
