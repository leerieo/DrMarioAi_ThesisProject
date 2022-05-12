package SOLVERS.TVO;
import SOLVERS.Bottle;

public class GreedyUtils {

    public void readTopItems(ItemColumn[] top_items, int[][] Playfield){

        //Loop through every column
        for(int x=0; x<Bottle.WIDTH; x++){
          ItemColumn it = new ItemColumn(); 


          boolean has_virus = false; 
          int top_item = Bottle.EMPTY_TILE; 
          int second_item = Bottle.EMPTY_TILE; 

          boolean top_two_match = false; 
          boolean top_three_match = false; 

          int second_item_compare = Bottle.EMPTY_TILE; 

          for(int y=0; y<Bottle.HEIGHT; y++){
            final int value = Playfield[y][x];
    
            // once you find the highest non-empty tile
            if(value != Bottle.EMPTY_TILE && top_item==Bottle.EMPTY_TILE){
              // record the depth
              int depth = y; 
              top_item = value;
              second_item_compare = top_item&Bottle.COLOR_MASK; 
              boolean is_hanging_pill = false; 

              if((value&Bottle.TILE_ID_MASK)!=Bottle.VIRUS && y<(Bottle.HEIGHT-1)){
                int value_2 = Playfield[y+1][x];
                if(value_2==Bottle.EMPTY_TILE){
                  is_hanging_pill = true; 
                }
              }
              it.setTop(top_item, depth, is_hanging_pill);

              if(y<(Bottle.HEIGHT-1)){
                int value_2 = Playfield[y+1][x];
                if((top_item&Bottle.COLOR_MASK) == (value_2&Bottle.COLOR_MASK)){
                  top_two_match = true; 
                }
              }

              if(y<(Bottle.HEIGHT-2)){
                int value_3 = Playfield[y+2][x];
                if((top_item&Bottle.COLOR_MASK) == (value_3&Bottle.COLOR_MASK)){
                  top_three_match = true; 
                }
              }

            }

            if(top_item!=Bottle.EMPTY_TILE && second_item==Bottle.EMPTY_TILE){
              if (value != Bottle.EMPTY_TILE){
                if(second_item_compare!=(value&Bottle.COLOR_MASK)){
                  second_item = value; 
                }
              }else{
                second_item_compare = Bottle.BLACK; 
              }
            }

            if((value&Bottle.TILE_ID_MASK) == Bottle.VIRUS){
              has_virus = true; 
            }

          }

          it.setColumnConditions(second_item, has_virus, top_three_match, top_two_match);
          top_items[x] = it; 
          
        }
      }

      public void printTopItems(ItemColumn[] top_items){
        System.out.println("TOP ITEMS: ");
        for(int x=0; x<Bottle.WIDTH; x++){
          System.out.print(String.format("0x%02X", top_items[x].top_item())+" ");
        }
        System.out.println();

        System.out.println("DEPTHS: ");
        for(int x=0; x<Bottle.WIDTH; x++){
          System.out.print(top_items[x].depth()+" ");
        }
        System.out.println();

        System.out.println("SECOND_ITEM: ");
        for(int x=0; x<Bottle.WIDTH; x++){
          System.out.print(String.format("0x%02X", top_items[x].second_item())+" ");
        }
        System.out.println();


        System.out.println("HAS VIRUS: ");
        for(int x=0; x<Bottle.WIDTH; x++){
          System.out.print(top_items[x].has_virus()+" ");
        }
        System.out.println();


        System.out.println("TOP THREE MATCH: ");
        for(int x=0; x<Bottle.WIDTH; x++){
          System.out.print(top_items[x].top_three_match()+" ");
        }
        System.out.println();

        System.out.println("TOP TWO MATCH: ");
        for(int x=0; x<Bottle.WIDTH; x++){
          System.out.print(top_items[x].top_two_match()+" ");
        }
        System.out.println();

      }

  
}
