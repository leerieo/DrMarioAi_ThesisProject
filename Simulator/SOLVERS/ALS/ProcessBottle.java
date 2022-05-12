package SOLVERS.ALS;
import SOLVERS.Bottle;

public class ProcessBottle {


        private int[][] playfield = new int[Bottle.HEIGHT][Bottle.WIDTH];
        private int num_viruses; 
        private int num_items; 


        public void setPlayfield(final int[][] playfield, int n_viruses, int n_items) {
            for(int i = Bottle.HEIGHT - 1; i >= 0; i--) {
            System.arraycopy(playfield[i], 0, this.playfield[i], 0, Bottle.WIDTH);
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
            for(int y=0; y<Bottle.HEIGHT; y++) {
                System.out.print("| ");
                for(int x=0; x<Bottle.WIDTH; x++) {
        
                    System.out.print(String.format("%x ",this.playfield[y][x]));
        
                }
                System.out.println("|");
                System.out.println("--------------------------");
                
            }
        }

        public boolean placeVitaminProcess(int pill_l, int pill_r, int row, int col, int orientation){
            switch(orientation){
                case 0:
                    this.playfield[row][col] = pill_l | Bottle.PILL_LEFT; 
                    if(col<(Bottle.WIDTH-1)){
                        this.playfield[row][col+1] = pill_r | Bottle.PILL_RIGHT; 
                    }
                    break; 
                case 2:
                    this.playfield[row][col] = pill_r | Bottle.PILL_LEFT; 
                    if(col<(Bottle.WIDTH-1)){
                        this.playfield[row][col+1] = pill_l | Bottle.PILL_RIGHT; 
                    }
                    break; 
                case 3: 
                    this.playfield[row][col] = pill_r | Bottle.PILL_BOTTOM; 
                    if(row>0){
                        this.playfield[row-1][col] = pill_l | Bottle.PILL_TOP; 
                    }
                    break; 
                case 1: 
                    this.playfield[row][col] = pill_l | Bottle.PILL_BOTTOM; 
                    if(row>0){
                        this.playfield[row-1][col] = pill_r | Bottle.PILL_TOP; 
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
            boolean[][] removed = new boolean[Bottle.HEIGHT][Bottle.WIDTH];
            
            // Initialize the removed array to all false
            for(int y=0; y<Bottle.HEIGHT; y++) {
                for(int x=0; x<Bottle.WIDTH; x++) {
                    removed[y][x] = false;
                }
            }
            

            // look for consecutive horizontal sequences (starting at the top left of the playfield)
            for(int y=0; y<Bottle.HEIGHT; y++) {
                int curr_color = playfield[y][0]&Bottle.COLOR_MASK;
                int start_x = 0;
                for(int x=1; x<Bottle.WIDTH; x++) {
                    int new_color = playfield[y][x] & Bottle.COLOR_MASK;
                    if (new_color != curr_color) {
                        int length = x-start_x;
                        if (length >= 4 && curr_color != Bottle.BLACK) {
                            removed_item = true;
                            for(int i=0; i<length; i++) {
                                removed[y][start_x + i] = true;
                            }
                        }
                        curr_color = new_color;
                        start_x = x;
                        }
                }

                int length = Bottle.WIDTH-start_x; 
                if(length>=4 && curr_color != Bottle.BLACK){
                    removed_item = true;
                    for(int i=0; i<length; i++) {
                        removed[y][start_x + i] = true;
                    }
                }
            }
            
            // Look for consecutive vertical matches 
            for(int x=0; x<Bottle.WIDTH; x++) {
                int curr_color = playfield[0][x]&Bottle.COLOR_MASK;
                int start_y = 0;
                for(int y=1; y<Bottle.HEIGHT; y++) {
                    int new_color = playfield[y][x] & Bottle.COLOR_MASK;
                    if (new_color != curr_color) {
                        int length = y-start_y;
                        if (length >= 4 && curr_color != Bottle.BLACK) {
                            removed_item = true;
                            for(int i=0; i<length; i++) {
                                removed[start_y + i][x] = true;
                            }
                    }
                        curr_color = new_color;
                        start_y = y;
                    }
                }

                int length = Bottle.HEIGHT-start_y; 
                if(length>=4 && curr_color != Bottle.BLACK){
                    removed_item = true;
                    for(int i=0; i<length; i++) {
                        removed[start_y + i][x] = true;
                    }
                }

            }
            
            // remove marked items 
            for(int y = Bottle.HEIGHT - 1; y >= 0; y--) {
                for(int x = Bottle.WIDTH - 1; x >= 0; x--) {
                    if (removed[y][x]) {

                        if((playfield[y][x]&Bottle.TILE_ID_MASK)==Bottle.VIRUS){
                            this.num_viruses--; 
                        }

                        if(playfield[y][x]!=Bottle.EMPTY_TILE){
                            this.num_items--; 
                        }

                        playfield[y][x] = Bottle.EMPTY_TILE;
                    }
                }
            }
            
            convertPillUnits();
            
            return removed_item;
        }


        private void convertPillUnits() {
            for(int y=0; y<Bottle.HEIGHT; y++) {
                for(int x=0; x<Bottle.WIDTH; x++) {
                    switch(playfield[y][x] & Bottle.TILE_ID_MASK) {
                        case 0x60:
                            if ((playfield[y][x + 1] & Bottle.TILE_ID_MASK) != Bottle.PILL_RIGHT) {
                                playfield[y][x] = (playfield[y][x] & Bottle.COLOR_MASK) | Bottle.PILL_UNIT;
                            }
                            break;
                        case 0x70:
                            if ((playfield[y][x - 1] & Bottle.TILE_ID_MASK) != Bottle.PILL_LEFT) {
                                playfield[y][x] = (playfield[y][x] & Bottle.COLOR_MASK) | Bottle.PILL_UNIT;
                            }
                            break;
                        case 0x40:
                            if ((playfield[y + 1][x] & Bottle.TILE_ID_MASK) != Bottle.PILL_BOTTOM) {
                                playfield[y][x] = (playfield[y][x] & Bottle.COLOR_MASK) | Bottle.PILL_UNIT;
                            }
                            break;
                        case 0x50:
                            
                            if (y==0 || ((playfield[y - 1][x] & Bottle.TILE_ID_MASK) != Bottle.PILL_TOP)) {
                                playfield[y][x] = (playfield[y][x] & Bottle.COLOR_MASK) | Bottle.PILL_UNIT;
                            }
                            break;
                    }
                }
            }
        }



        private void settleBottle() {
            for(int y = Bottle.HEIGHT - 2; y >= 0; y--) {
                for(int x = Bottle.WIDTH - 1; x >= 0; x--) {
                    switch(playfield[y][x] & Bottle.TILE_ID_MASK) {
                        case 0x80:
                        case 0x50:
                        case 0x40:
                            if (playfield[y + 1][x] == Bottle.EMPTY_TILE) {
                                dropItem(x, y);
                            }
                            break;
                        case 0x60:
                            if (playfield[y + 1][x] == Bottle.EMPTY_TILE && playfield[y + 1][x + 1] == Bottle.EMPTY_TILE) {
                                dropItemHor(x, y);
                            }
                            break;
                    }
                }
            }
        }

        private void dropItem(int col, int row){
            int new_row = row;
            
            for(int i=(row+1); i<Bottle.HEIGHT; i++){
                if(playfield[i][col] == Bottle.EMPTY_TILE){
                    new_row = i; 
                }else{
                    break; 
                }
            }

            playfield[new_row][col] = playfield[row][col]; 
            playfield[row][col] = Bottle.EMPTY_TILE; 
        }

        private void dropItemHor(int col, int row){
            int new_row_l = row;
            
            for(int i=(row+1); i<Bottle.HEIGHT; i++){
                if(playfield[i][col] == Bottle.EMPTY_TILE){
                    new_row_l = i; 
                }else{
                    break; 
                }
            }

            int new_row_r = row;
            
            for(int i=(row+1); i<Bottle.HEIGHT; i++){
                if(playfield[i][col+1] == Bottle.EMPTY_TILE){
                    new_row_r = i; 
                }else{
                    break; 
                }
            }

            int new_row = Math.min(new_row_l, new_row_r); 


            playfield[new_row][col] = playfield[row][col]; 
            playfield[new_row][col+1] = playfield[row][col+1]; 

            playfield[row][col] = Bottle.EMPTY_TILE; 
            playfield[row][col+1] = Bottle.EMPTY_TILE; 
        }

    }
