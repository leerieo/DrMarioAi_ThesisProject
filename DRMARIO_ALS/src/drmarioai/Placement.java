package drmarioai;
import java.util.ArrayList;

public class Placement{

    public final Integer[] pos; // [row, col, orientation]
    public final ArrayList<String> move_list; 

    public Placement(Integer[] pos, ArrayList<String> move_list) { 
      this.pos = pos; 
      this.move_list = move_list; 
    } 

    public int getRow(){
      return pos[0]; 
    }

    public int getCol(){
      return pos[1]; 
    }

    public int getOrientation(){
      return pos[2]; 
    }
}
