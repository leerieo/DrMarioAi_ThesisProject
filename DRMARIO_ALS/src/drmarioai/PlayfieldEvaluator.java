package drmarioai;
import static drmarioai.Bottle.*;

public class PlayfieldEvaluator {
   
    public double evaluate(final ProcessBottle playfield, boolean removed_item) {

        double score = 0.0; 
        double v_count = playfield.numViruses(); 

        // minimize the number of viruses
        
        score += ((128.0 - v_count)/128.0)*200; 

        if(v_count == 0){
            return 1000; 
        }else if(v_count <= 2){
            score+=100; 
        }else if(v_count<=1){
            score+=200; 
        }

        // System.out.println("num virus score: "+score);

        // maximize the number of empty tiles 
        score += playfield.numEmptyTiles()/128.0; 
        // System.out.println("num empty tiles score: "+(playfield.numEmptyTiles()/128.0));


        double item_count = 128.0 - playfield.numEmptyTiles(); 

        double new_seq_ver = 
            numItemsNIAR_Ver(playfield.getPlayfield(), 3, item_count, v_count) + 
            numItemsNIAR_Ver(playfield.getPlayfield(), 2, item_count,  v_count);
        double new_seq_hor = 
            numItemsNIAR_Hor(playfield.getPlayfield(), 3, item_count, v_count) + 
            numItemsNIAR_Hor(playfield.getPlayfield(), 2, item_count, v_count);

        new_seq_hor*=0.5; 
        score += Math.max(new_seq_ver, new_seq_hor); 

        // System.out.println("seq score: "+Math.max(new_seq_ver, new_seq_hor));

        score -= evaluateClustersAboveViruses(playfield.getPlayfield())*20; 
        // System.out.println("eval clusters above viruses score: "+ (evaluateClustersAboveViruses(playfield.getPlayfield())*-20));

        score += evaluateSameColorVitaminsAboveViruses(playfield.getPlayfield(),v_count)*10;
        // System.out.println("same color vitamins above viruses score: "+ evaluateSameColorVitaminsAboveViruses(playfield.getPlayfield(),v_count));

        // Punish items that touch the top
        score += numItemsTouchingTop(playfield.getPlayfield());
        // System.out.println("numItemsTouchingTop score: "+numItemsTouchingTop(playfield.getPlayfield()));       

        // reward lower viruses
        score += evaluateVirusDepths(playfield.getPlayfield()); 
        // System.out.println("evaluateVirusDepths score: "+evaluateVirusDepths(playfield.getPlayfield()));       

        // if we've removed an item or the placement isn't a match
        // if(removed_item || !is_match){
        //     System.out.println("HERE");
        //     score += evaluateDepths(playfield.getPlayfield()); 
        // }else{
        //     score += 1.0-evaluateDepths(playfield.getPlayfield()); 
        // }
    
        return score; 
    }


    private double evaluateVirusDepths(int[][] playfield){
        double sum = 0; 

        for(int x=0; x<WIDTH; x++){
            int counter = 0; 
            for(int y=0; y<HEIGHT; y++){
                if((playfield[y][x]&TILE_ID_MASK)==VIRUS){       
                    break;              
                }
                counter++; 
            }
            sum+=counter; 
        }

        return sum/120.0; 
    }

    private double virusDepthsStd(int[][] playfield){

        double[] depths = new double[WIDTH]; 

        for(int x=0; x<WIDTH; x++){
            for(int y=0; y<HEIGHT; y++){
                if((playfield[y][x]&TILE_ID_MASK)== VIRUS){
                    depths[x] = y; 
                    break;      
                }
            }
        }

        // The mean average
        double mean = 0.0;
        for (int i = 0; i < depths.length; i++) {
                mean += depths[i];
        }
        mean /= depths.length;

        // The variance
        double variance = 0;
        for (int i = 0; i < depths.length; i++) {
            variance += Math.pow(depths[i] - mean, 2);
        }
        variance /= depths.length;

        double std = Math.sqrt(variance);

        return std;

    }

    private double numNonEmptyTiles(int[][] playfield){
        double sum = 0; 

        for(int x=0; x<WIDTH; x++){
            for(int y=0; y<HEIGHT; y++){
                if(playfield[y][x] != EMPTY_TILE){

                    if(y<=0){
                        sum+=100; 
                    }else if(y<=3){
                        sum+=20; 
                    }else{
                        sum++; 
                    }
                    
                }
            }
        }

        return sum; 
    }

    private double numItemsTouchingTop(int[][] playfield){
        double sum = 0; 

        for(int x=0; x<WIDTH; x++){
            if(playfield[0][x] != EMPTY_TILE){
                sum++;  
            }
        }

        return (WIDTH-sum)/WIDTH; 
    }


    public double numItemsNIAR_Ver(int[][] playfield, int n, double item_count, double virus_count){
        
        double count = 0; 
        // Look for consecutive vertical matches
        for(int x=0; x<WIDTH; x++) {
            int prev_color = playfield[HEIGHT-1][x]&COLOR_MASK;
            int curr_color = playfield[HEIGHT-1][x]&COLOR_MASK;
            int counter = 1; 
            for(int y=HEIGHT-2; y>=0; y--) {

                int new_color = playfield[y][x] & COLOR_MASK;

                if(new_color!=curr_color){
                    if (counter >= n && curr_color!=BLACK) {
                        if(new_color == BLACK || prev_color == BLACK){
                            count+=counter;
                        }
                    }
                    prev_color = curr_color; 
                    curr_color = new_color;
                    counter = 1;  
                }else{
                    if((playfield[y][x]&TILE_ID_MASK)==VIRUS){
                        counter+=10; 
                    }else{
                        counter++; 
                    }
                }
              
            }
            if(counter >=n && curr_color!=BLACK){
                if(prev_color == BLACK){
                    count+=counter;
                }
            }
        }
        
        double ret_score = count/item_count; 
    
        return ret_score/(virus_count*10); 

    }

    public double numItemsNIAR_Hor(int[][] playfield, int n, double item_count, double virus_count){
        
        double count = 0; 
        // look for consecutive horizontal sequences (starting at the top left of the playfield)
        for(int y=0; y<HEIGHT; y++) {
            int prev_color = playfield[y][WIDTH-1]&COLOR_MASK;
            int prev_color_i = WIDTH-1; 
            int curr_color = playfield[y][WIDTH-1]&COLOR_MASK;
            int counter = 1; 

            for(int x=WIDTH-2; x>=0; x--) {
                int new_color = playfield[y][x] & COLOR_MASK;
                if (new_color != curr_color) {
                    if (counter >= n && curr_color != BLACK) {

                        if(new_color == BLACK){
                            if(y==(HEIGHT-1) || playfield[y+1][x]!=EMPTY_TILE){
                                count+=counter;
                            }
                            
                        }else if(prev_color == BLACK){
                            if(y==(HEIGHT-1) || playfield[y+1][prev_color_i]!=EMPTY_TILE){
                                count+=counter;
                            }
                        }
                    }
                    prev_color = curr_color; 
                    prev_color_i = x; 
                    curr_color = new_color;
                    counter = 1;
                }else{
                    if((playfield[y][x]&TILE_ID_MASK)==VIRUS){
                        counter+=10; 
                    }else{
                        counter++; 
                    }
              
                }
            }
            
            if(counter>=n && curr_color != BLACK){
                if(y==(HEIGHT-1) || playfield[y+1][prev_color_i]!=EMPTY_TILE){
                    count+=counter;
                }
            }
        }

        double ret_score = count/item_count; 
        return ret_score/(virus_count*10); 
        
    }


    private double evaluateClustersAboveViruses(int[][] playfield){

        double count = 0; 

        for(int y=0; y<HEIGHT; y++){
            for(int x=0; x<WIDTH; x++){

                // for every virus 
                if((playfield[y][x]&TILE_ID_MASK)==VIRUS){

                    int curr_color = playfield[y][x]&COLOR_MASK; 

                    for(int i=(y-1); i>=0; i--){
                        int tile = playfield[i][x]; 
                        
                        // if we encounter a virus stop counting 
                        if((tile&TILE_ID_MASK)==VIRUS){
                            break; 
                        }

                        if(tile!=EMPTY_TILE){
                            if(curr_color!=(tile&COLOR_MASK)){
                                // higher clusters are worth more 
                                count+=(HEIGHT-i); 
                                curr_color = tile&COLOR_MASK; 
                            }
                        }
              
                    }
                }
            }
        }

        // System.out.println("numDiffColorVitaminsAboveViruses: "+count);

        // 136 * 8 
        return count/1088.0; 
    }

    private double evaluateSameColorVitaminsAboveViruses(int[][] playfield, double num_viruses){

        if(num_viruses == 0){
            return 1.0; 
        }

        double count = 0; 

        for(int y=0; y<HEIGHT; y++){
            for(int x=0; x<WIDTH; x++){
                if((playfield[y][x]&TILE_ID_MASK)==VIRUS){
                    

                    for(int i=(y-1); i>=0; i--){
                        int tile = playfield[i][x]; 
                        if((tile&TILE_ID_MASK)==VIRUS){
                            break; 
                        }

                        if(tile!=EMPTY_TILE){
                            if((tile&COLOR_MASK)==(playfield[y][x]&COLOR_MASK)){
                                count++; 
                            }else{     
                                break; 
                            }
                        }
                    }
                }
            }
        }

        // System.out.println("numDiffColorVitaminsAboveViruses: "+count);

        return count/(num_viruses*4); 
    }
 
}
