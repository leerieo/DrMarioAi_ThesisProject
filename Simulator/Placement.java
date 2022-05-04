

public class Placement{

    private final Integer[] pos; // [row, col, orientation]

    public Placement(int row, int col, int orien) { 
      this.pos = new Integer[3]; 
      this.pos[0] = row; 
      this.pos[1] = col; 
      this.pos[2] = orien; 
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
