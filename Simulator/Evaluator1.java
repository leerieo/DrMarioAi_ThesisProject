


public class Evaluator1 {
    
  
       
    public static double evaluate(int[][] playfield, int v_count) {


    
            double score = 0.0; 
            
            // minimize the number of viruses
            score += ((128.0 - v_count)/128.0)*10; 

            // System.out.println("num virus score: "+score);
    
            // maximize the number of empty tiles 
            score += numEmptyTiles(playfield); 

            //maximize virus depths 
            score += virusDepthSum(playfield);

            //minimize number of color changes in columns 
            score += numColorChanges_Col(playfield);

            //minimize number of empty tiles in top row
            score += numItemsInTopRow(playfield); 

            // score += matchingColorsAboveViruses(playfield);

            // score +=matchingColorsBelowVitamins(playfield); 

            return score; 
        }

        private static double numEmptyTiles(int[][] playfield){
            double count = 0; 

            for(int x=0; x<Bottle.WIDTH; x++){
                for(int y=0; y<Bottle.HEIGHT; y++){
                    if(playfield[y][x]==Bottle.EMPTY_TILE){       
                        count++;           
                    }
                }
            }

            return count/128.0; 
        }
    
    
        private static double virusDepthSum(int[][] playfield){
            double sum = 0; 
    
            for(int x=0; x<Bottle.WIDTH; x++){
                int counter = 0; 
                for(int y=0; y<Bottle.HEIGHT; y++){
                    if((playfield[y][x]&Bottle.TILE_ID_MASK)==Bottle.VIRUS){       
                        break;              
                    }
                    counter++; 
                }
                sum+=counter; 
            }
    
            return sum/128.0; 
        }

        private static double matchingColorsAboveViruses(int[][] playfield){
            double count = 0; 
            double virus_count = 0; 

           
            for(int y=1; y<Bottle.HEIGHT; y++){

                for(int x=0; x<Bottle.WIDTH; x++){

                    if((playfield[y][x]&Bottle.TILE_ID_MASK)==Bottle.VIRUS){  
                        virus_count++;     
                        if((playfield[y][x]&Bottle.COLOR_MASK) == (playfield[y-1][x]&Bottle.COLOR_MASK)){
                            count++;  
                        }      
                    }
                }
            }
    
            return count/virus_count; 
        }




        private static double matchingColorsBelowVitamins(int[][] playfield){
            double count = 0; 

            double vitamin_count = 0; 


           
            for(int y=0; y<Bottle.HEIGHT-1; y++){

                for(int x=0; x<Bottle.WIDTH; x++){

                    int id = playfield[y][x]&Bottle.TILE_ID_MASK;
                    if(id!=Bottle.VIRUS && id!=Bottle.EMPTY_ID){ 
                        vitamin_count++; 


                        if((playfield[y][x]&Bottle.COLOR_MASK) == (playfield[y+1][x]&Bottle.COLOR_MASK)){
                            count++;  
                        }      

                   

                    }
                }
            }
    
            return count/vitamin_count; 
        }


        private static double numColorChanges_Col(int[][] playfield){
            double num_changes = 0; 

            for(int x=0; x<Bottle.WIDTH; x++){
                int curr_color = playfield[0][x] & Bottle.COLOR_MASK; 

                for(int y=1; y<Bottle.HEIGHT; y++){
                    int new_color = playfield[y][x] & Bottle.COLOR_MASK; 
                    if(new_color != curr_color){
                        num_changes++; 
                        curr_color = new_color; 
                    }
                }
            }

            return ((128.0-num_changes)/128.0); 
        }

        private static double numItemsInTopRow(int[][] playfield){
            int count = 0; 
            for(int x=0; x<Bottle.WIDTH; x++){
                if(playfield[0][x] != Bottle.EMPTY_TILE){
                    count++; 
                }
            }

            return ((Bottle.WIDTH-count)/Bottle.WIDTH); 
        }
    
        
}
