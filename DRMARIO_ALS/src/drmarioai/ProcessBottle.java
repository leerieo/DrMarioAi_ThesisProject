    package drmarioai;
    import static drmarioai.Bottle.*;

    public class ProcessBottle {


        private int[][] playfield = new int[HEIGHT][WIDTH];
        private int num_viruses; 
        private int num_items; 


        public void setPlayfield(final int[][] playfield, int n_viruses, int n_items) {
            for(int i = HEIGHT - 1; i >= 0; i--) {
            System.arraycopy(playfield[i], 0, this.playfield[i], 0, WIDTH);
            }   

            this.num_viruses = n_viruses; 
            this.num_items = n_items; 
        }
        
        public int[][] getPlayfield() {
            return playfield;
        }

        public int numViruses(){
            return num_viruses; 
        }

        public int numEmptyTiles(){ 
            return 128-num_items; 
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

        public boolean placeVitaminProcess(int pill_l, int pill_r, int row, int col, int orientation){
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

            // System.out.println("BEFORE: ");
            // printPlayfield();

            boolean removed_items = false; 

            while(removeMatches()) {
                settleBottle();
                removed_items = true; 
            } 

            return removed_items; 

            // System.out.println("AFTER: ");
            // printPlayfield();
        }


        private boolean removeMatches() {
        
            boolean removed_item = false;
            boolean[][] removed = new boolean[HEIGHT][WIDTH];
            
            // Initialize the removed array to all false
            for(int y=0; y<HEIGHT; y++) {
                for(int x=0; x<WIDTH; x++) {
                    removed[y][x] = false;
                }
            }
            

            // look for consecutive horizontal sequences (starting at the top left of the playfield)
            for(int y=0; y<HEIGHT; y++) {
                int curr_color = playfield[y][0]&COLOR_MASK;
                int start_x = 0;
                for(int x=1; x<WIDTH; x++) {
                    int new_color = playfield[y][x] & COLOR_MASK;
                    if (new_color != curr_color) {
                        int length = x-start_x;
                        if (length >= 4 && curr_color != BLACK) {
                            removed_item = true;
                            for(int i=0; i<length; i++) {
                                removed[y][start_x + i] = true;
                            }
                        }
                        curr_color = new_color;
                        start_x = x;
                        }
                }

                int length = WIDTH-start_x; 
                if(length>=4 && curr_color != BLACK){
                    removed_item = true;
                    for(int i=0; i<length; i++) {
                        removed[y][start_x + i] = true;
                    }
                }
            }
            
            // Look for consecutive vertical matches 
            for(int x=0; x<WIDTH; x++) {
                int curr_color = playfield[0][x]&COLOR_MASK;
                int start_y = 0;
                for(int y=1; y<HEIGHT; y++) {
                    int new_color = playfield[y][x] & COLOR_MASK;
                    if (new_color != curr_color) {
                        int length = y-start_y;
                        if (length >= 4 && curr_color != BLACK) {
                            removed_item = true;
                            for(int i=0; i<length; i++) {
                                removed[start_y + i][x] = true;
                            }
                    }
                        curr_color = new_color;
                        start_y = y;
                    }
                }

                int length = HEIGHT-start_y; 
                if(length>=4 && curr_color != BLACK){
                    removed_item = true;
                    for(int i=0; i<length; i++) {
                        removed[start_y + i][x] = true;
                    }
                }

            }
            
            // remove marked items 
            for(int y = HEIGHT - 1; y >= 0; y--) {
                for(int x = WIDTH - 1; x >= 0; x--) {
                    if (removed[y][x]) {

                        if((playfield[y][x]&TILE_ID_MASK)==VIRUS){
                            this.num_viruses--; 
                        }

                        if(playfield[y][x]!=EMPTY_TILE){
                            this.num_items--; 
                        }

                        playfield[y][x] = EMPTY_TILE;
                    }
                }
            }
            
            convertPillUnits();
            
            return removed_item;
        }


        private void convertPillUnits() {
            for(int y=0; y<HEIGHT; y++) {
                for(int x=0; x<WIDTH; x++) {
                    switch(playfield[y][x] & TILE_ID_MASK) {
                        case PILL_LEFT:
                            if ((playfield[y][x + 1] & TILE_ID_MASK) != PILL_RIGHT) {
                                playfield[y][x] = (playfield[y][x] & COLOR_MASK) | PILL_UNIT;
                            }
                            break;
                        case PILL_RIGHT:
                            if ((playfield[y][x - 1] & TILE_ID_MASK) != PILL_LEFT) {
                                playfield[y][x] = (playfield[y][x] & COLOR_MASK) | PILL_UNIT;
                            }
                            break;
                        case PILL_TOP:
                            if ((playfield[y + 1][x] & TILE_ID_MASK) != PILL_BOTTOM) {
                                playfield[y][x] = (playfield[y][x] & COLOR_MASK) | PILL_UNIT;
                            }
                            break;
                        case PILL_BOTTOM:
                            
                            if (y==0 || ((playfield[y - 1][x] & TILE_ID_MASK) != PILL_TOP)) {
                                playfield[y][x] = (playfield[y][x] & COLOR_MASK) | PILL_UNIT;
                            }
                            break;
                    }
                }
            }
        }



        private void settleBottle() {
            for(int y = HEIGHT - 2; y >= 0; y--) {
                for(int x = WIDTH - 1; x >= 0; x--) {
                    switch(playfield[y][x] & TILE_ID_MASK) {
                        case PILL_UNIT:
                        case PILL_BOTTOM:
                        case PILL_TOP:
                            if (playfield[y + 1][x] == EMPTY_TILE) {
                                dropItem(x, y);
                            }
                            break;
                        case PILL_LEFT:
                            if (playfield[y + 1][x] == EMPTY_TILE && playfield[y + 1][x + 1] == EMPTY_TILE) {
                                dropItemHor(x, y);
                            }
                            break;
                    }
                }
            }
        }

        private void dropItem(int col, int row){
            int new_row = row;
            
            for(int i=(row+1); i<HEIGHT; i++){
                if(playfield[i][col] == EMPTY_TILE){
                    new_row = i; 
                }else{
                    break; 
                }
            }

            playfield[new_row][col] = playfield[row][col]; 
            playfield[row][col] = EMPTY_TILE; 
        }

        private void dropItemHor(int col, int row){
            int new_row_l = row;
            
            for(int i=(row+1); i<HEIGHT; i++){
                if(playfield[i][col] == EMPTY_TILE){
                    new_row_l = i; 
                }else{
                    break; 
                }
            }

            int new_row_r = row;
            
            for(int i=(row+1); i<HEIGHT; i++){
                if(playfield[i][col+1] == EMPTY_TILE){
                    new_row_r = i; 
                }else{
                    break; 
                }
            }

            int new_row = Math.min(new_row_l, new_row_r); 


            playfield[new_row][col] = playfield[row][col]; 
            playfield[new_row][col+1] = playfield[row][col+1]; 

            playfield[row][col] = EMPTY_TILE; 
            playfield[row][col+1] = EMPTY_TILE; 
        }

    }
