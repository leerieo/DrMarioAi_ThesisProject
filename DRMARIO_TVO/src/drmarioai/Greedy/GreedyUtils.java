package drmarioai.Greedy;
import static drmarioai.Bottle.*;
import drmarioai.*;
import nintaco.api.*;

public class GreedyUtils {

    private final API api = ApiSource.getAPI();

    public void readTopItems(ItemColumn[] top_items){

        //Loop through every column
        for(int x=0; x<WIDTH; x++){
          ItemColumn it = new ItemColumn(); 


          boolean has_virus = false; 
          int top_item = EMPTY_TILE; 
          int second_item = EMPTY_TILE; 

          boolean top_two_match = false; 
          boolean top_three_match = false; 


          int second_item_compare = EMPTY_TILE; 


          for(int y=0; y<HEIGHT; y++){
            final int value = api.readCPU(Address.P1_PLAYFIELD | (y << 3) | x);
    
            // once you find the highest non-empty tile
            if(value != EMPTY_TILE && top_item==EMPTY_TILE){
              // record the depth
              int depth = y; 
              top_item = value;
              second_item_compare = top_item&COLOR_MASK; 
              boolean is_hanging_pill = false; 

              if((value&TILE_ID_MASK)!=VIRUS && y<(HEIGHT-1)){
                int value_2 = api.readCPU(Address.P1_PLAYFIELD | (y+1 << 3) | x);
                if(value_2==EMPTY_TILE){
                  is_hanging_pill = true; 
                }
              }
              it.setTop(top_item, depth, is_hanging_pill);

              if(y<(HEIGHT-1)){
                int value_2 = api.readCPU(Address.P1_PLAYFIELD | (y+1 << 3) | x);
                
                if((top_item&COLOR_MASK) == (value_2&COLOR_MASK)){
                  top_two_match = true; 
                }
              }

              if(top_two_match && y<(HEIGHT-2)){
                int value_3 = api.readCPU(Address.P1_PLAYFIELD | (y+2 << 3) | x);
                if((top_item&COLOR_MASK) == (value_3&COLOR_MASK)){
                  top_three_match = true; 
                }
              }

            }

          
            if(top_item!=EMPTY_TILE && second_item==EMPTY_TILE){
              if (value != EMPTY_TILE){
                if(second_item_compare!=(value&COLOR_MASK)){
                  second_item = value; 
                }
              }else{
                second_item_compare = BLACK; 
              }
            }

            if((value&TILE_ID_MASK) == VIRUS){
              has_virus = true; 
            }

          }

          it.setColumnConditions(second_item, has_virus, top_three_match, top_two_match);
          top_items[x] = it; 
          
        }
      }

      public void printTopItems(ItemColumn[] top_items){
        System.out.println("TOP ITEMS: ");
        for(int x=0; x<WIDTH; x++){
          System.out.print(String.format("0x%02X", top_items[x].top_item())+" ");
        }
        System.out.println();

        System.out.println("DEPTHS: ");
        for(int x=0; x<WIDTH; x++){
          System.out.print(top_items[x].depth()+" ");
        }
        System.out.println();

        System.out.println("SECOND_ITEM: ");
        for(int x=0; x<WIDTH; x++){
          System.out.print(String.format("0x%02X", top_items[x].second_item())+" ");
        }
        System.out.println();


        System.out.println("HAS VIRUS: ");
        for(int x=0; x<WIDTH; x++){
          System.out.print(top_items[x].has_virus()+" ");
        }
        System.out.println();


        System.out.println("TOP THREE MATCH: ");
        for(int x=0; x<WIDTH; x++){
          System.out.print(top_items[x].top_three_match()+" ");
        }
        System.out.println();

        System.out.println("TOP TWO MATCH: ");
        for(int x=0; x<WIDTH; x++){
          System.out.print(top_items[x].top_two_match()+" ");
        }

        System.out.println("IS HANGING PILL: ");
        for(int x=0; x<WIDTH; x++){
          System.out.print(top_items[x].is_hanging_pill()+" ");
        }

        System.out.println();

      }

  
}
