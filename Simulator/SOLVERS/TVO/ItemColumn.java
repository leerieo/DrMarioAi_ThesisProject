package SOLVERS.TVO;
import SOLVERS.Bottle;

public class ItemColumn {

    private int top_item; 
    private int second_item; 

    private int depth; 
    private boolean has_virus; 
    private boolean is_hanging_pill; 
    private boolean top_three_match; 
    private boolean top_two_match; 

    public ItemColumn() { 
      this.depth = 16;   
      this.top_item = Bottle.EMPTY_TILE; 
      this.second_item = Bottle.EMPTY_TILE; 
      this.has_virus = false; 
      this.is_hanging_pill = false; 
      this.top_three_match = false;
      this.top_two_match = false; 
    } 

    public void setTop(int top_item, int depth, boolean is_hanging_pill){
        this.top_item = top_item; 
        this.depth = depth; 
        this.is_hanging_pill = is_hanging_pill;
    }


    public void setColumnConditions(int second_item, boolean has_virus, boolean top_three_match, boolean top_two_match){
        this.second_item = second_item;
        this.has_virus = has_virus; 
        this.top_three_match = top_three_match; 
        this.top_two_match = top_two_match; 
    }

    public int top_item(){
        return this.top_item; 
    }

    public int second_item(){
        return this.second_item; 
    }

    public int depth(){
        return this.depth;
    }

    public boolean has_virus(){
        return this.has_virus;
    }

    public boolean is_hanging_pill(){
        return this.is_hanging_pill; 
    }

    public boolean top_three_match(){
        return this.top_three_match; 
    }

    public boolean top_two_match(){
        return this.top_two_match; 
    }



 




}
