
import java.util.Random;

public class algHV{

    private static Random r = new Random();
    // list of binary bits 
    private static int[] state; 

    private static int[][] bottle; 
    private static int rows; 
    private static int cols; 
    private static int virus_rows; 
    private static int virus_goal; 

    public static int[][] vitamins; 



    public static void random_init(int seed){

        state = new int[16];

        String bin = Integer.toBinaryString(seed);
        
        int start_i = 16 - bin.length(); 

        for(int i=0;i<bin.length();i++){
            state[start_i+i] = Character.getNumericValue(bin.charAt(i));  ; 
        }

    }

    public static int[][] getBottle(){
        return bottle; 
    }


    public static void random_inc(){
        int bit7 = state[6]; 
        int bint15 = state[14];

        int newbit = bit7 ^ bint15; 
        int prevbit = state[0]; 
        for(int i=1; i<16; i++){
            int temp = state[i];
            state[i] = prevbit; 
            prevbit = temp; 
        }
        state[0] = newbit; 
        
    }

    //Print the current state as two hexadecimal bytes.
    public static void random_state_print(){

        int upper1 = 8*state[0] + 4*state[1] + 2*state[2] + 1*state[3]; 
        int upper2 = 8*state[4] + 4*state[5] + 2*state[6] + 1*state[7];
        
        int lower1 = 8*state[8]  + 4*state[9]  + 2*state[10] + 1*state[11]; 
        int lower2 = 8*state[12] + 4*state[13] + 2*state[14] + 1*state[15]; 
        
        System.out.print(Integer.toHexString(upper1).toUpperCase()+Integer.toHexString(upper2).toUpperCase());
        System.out.print(", ");
        System.out.println(Integer.toHexString(lower1).toUpperCase()+Integer.toHexString(lower2).toUpperCase());

    }

    public static int random_row(int max_val){
        random_inc();
        int value = 8*state[4] + 4*state[5] + 2*state[6] + 1*state[7]; 

        while(value > max_val){
            random_inc();
            value = 8*state[4] + 4*state[5] + 2*state[6] + 1*state[7]; 
        }

        return value; 
    }

    public static int random_col(){
        int value = 4*state[13] + 2*state[14] + 1*state[15];
        return value; 
    }

    public static int random_idx(){
        random_inc();
        int value = 8*state[12] + 4*state[13] + 2*state[14] + 1*state[15]; 
        return value; 
    }

    public static void init_bottle(int level){
        set_globals(level);

        bottle = new int[rows][cols]; 
        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                bottle[r][c] = Bottle.EMPTY_TILE; 
            }
        }
    }

    public static void set_globals(int level){
        rows = 16; 
        cols = 8;

        virus_rows = 10;
        if(level>=15){
            virus_rows++; 
        }
        if(level>=17){
            virus_rows++; 
        }
        if(level>=19){
            virus_rows++; 
        }

        virus_goal = (level+1)*4;  
        if(virus_goal>84){
            virus_goal = 84;
        }
    }

    public static void print_bottle(){
        for(int r=(rows-1); r>=0; r--){
            for(int c=0; c<cols; c++){
                int value = bottle[r][c]; 
                // System.out.print(Integer.toHexString(value)); 
                if(value == Bottle.EMPTY_TILE){
                    System.out.print("-"); 
                }else if((value&Bottle.COLOR_MASK)==Bottle.YELLOW){
                    System.out.print("Y");
                }else if((value&Bottle.COLOR_MASK)==Bottle.RED){
                    System.out.print("R");
                }else if((value&Bottle.COLOR_MASK)==Bottle.BLUE){
                    System.out.print("B");
                }
            }
            System.out.println("");
        }
    }

    public static boolean is_maximal(){
        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                int[] available = available(r, c); 
                for (int i : available) {
                    if(i!=-1){
                        return false; 
                    }
                }
            }
        }
        return true; 
    }

    public static int[] available(int row, int col){
        
        if(bottle[row][col]!=Bottle.EMPTY_TILE){
            int[] available = {-1,-1,-1};
            return available; 
        }

        int[] available = {0,1,2}; 

        if(row-2>=0){
            if(bottle[row-2][col]!=Bottle.EMPTY_TILE){
                available[(bottle[row-2][col]&0xF)] = -1; 
            }
        }
        if(col-2>=0){
            if(bottle[row][col-2]!=Bottle.EMPTY_TILE){
                available[(bottle[row][col-2]&0xF)] = -1; 
            }
        }
        if(row+2<virus_rows){
            if(bottle[row+2][col]!=Bottle.EMPTY_TILE){
                available[(bottle[row+2][col]&0xF)] = -1; 
            }
        }
        if(col+2<cols){
            if(bottle[row][col+2]!=Bottle.EMPTY_TILE){
                available[(bottle[row][col+2]&0xF)] = -1; 
            }
        }

        return available; 
      
    }

    public static int add_virus(int row, int col, int preferred_color){
        
        int[] color_cycle; 

        if(preferred_color == 0){
            int[] preferred_colors = {0,2,1}; 
            color_cycle = preferred_colors; 
        }else if(preferred_color == 1){
            int[] preferred_colors = {1,0,2}; 
            color_cycle = preferred_colors; 
        }else{
            int[] preferred_colors = {2,1,0}; 
            color_cycle = preferred_colors; 
        }
       
        int[] available_colors = available(row, col);

        for (int color : color_cycle) {
            if(available_colors[color] !=-1){
                bottle[row][col] = Bottle.VIRUS | color;
                return color; 
            }
        }
        return -1; 

    }

    public static int getNumViruses(){
        return virus_goal; 
    }

    public static int fill_bottle(){
        int num_remaining = virus_goal;

        while(num_remaining > 0){
            if(is_maximal()){
                break; 
            }

            int row = random_row(virus_rows-1);
            int col = random_col(); 

            int preferred_color = num_remaining%4; 
            if(preferred_color ==3){
                int[] color_table = {0,1,2,2,1,0,0,1,2,2,1,0,0,1,2,1}; 
                int i = random_idx();
                preferred_color = color_table[i]; 
            }

            while(true){
                int color_added = add_virus(row, col, preferred_color); 

                if(color_added != -1){
                    num_remaining--; 

                   
                    break; 
                }

                if(row==0 && col ==cols-1){
                    break; 
                }

                col++; 
                if(col == cols){
                    row--;
                    col = 0; 
                }
            }

        }

        return num_remaining; 

    }

    public static void intit_vitamins(){
        vitamins = new int[128][2];
        // for(int i=0; i<128; i++){
        //     vitamins[i][0] = -1; 
        //     vitamins[i][1] = -1; 
        // }
    }

    public static void generate_vitamins(){
        int [][] vitamin_types = {{0,0}, {0,1}, {0,2}, {1,0}, {1,1}, {1,2}, {2,0}, {2,1}, {2,2}};

        int type = 0; 

        for(int i=127; i>=0; i--){
            random_inc();

            type += 8*state[4] + 4*state[5] + 2*state[6] + 1*state[7]; 
            type%=9; 

            vitamins[i] = vitamin_types[type]; 
        }
    }

    public static int[][] getVitamins(){
        return vitamins; 
    }

    public static void print_vitamins(){
        for (int[] vitamin : vitamins) {
            int left = vitamin[0]; 
            int right = vitamin[1]; 

            if(left == 0){
                System.out.print("Y");
            }else if(left==1){
                System.out.print("R");
            }else{
                System.out.print("B");
            }

            if(right == 0){
                System.out.println("Y");
            }else if(right==1){
                System.out.println("R");
            }else{
                System.out.println("B");
            }
        }
    }

    public static void main (String[] args){
        // If 1 parameter are given, then the 1st is the level.
        // If 2 parameters are given, then the 1st + 2nd are the seed (level is 20).
        // If 3 parameters are given, then the 1st is level, and 2nd + 3rd the seed.
        
        int level; 
        if(args.length == 1 || args.length == 3){
            level = Integer.parseInt(args[0]);
        }else{
            level = 20; 
        }
        int s0; 
        int s1; 
        // Seed: Read from command-line or randomly generate.
        if(args.length == 2){
            s0 = Integer.valueOf(args[0], 16);
            s1 = Integer.valueOf(args[1], 16);
        }else if(args.length == 3){
            s0 = Integer.valueOf(args[1], 16);
            s1 = Integer.valueOf(args[2], 16);
        }else{
            s0 = r.nextInt(255); 
            s1 = r.nextInt(255); 
        }

        int seed = 256*s0 + s1; 
        random_init(seed);
        random_state_print();

        intit_vitamins();
        generate_vitamins();
        print_vitamins();

        System.out.println("");
        random_state_print();

        init_bottle(level);
        fill_bottle(); 
        print_bottle();

      }

}