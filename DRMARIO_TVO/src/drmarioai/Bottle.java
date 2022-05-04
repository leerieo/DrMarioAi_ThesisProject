package drmarioai;

public interface Bottle {

  public static final int HEIGHT = 16; 
  public static final int WIDTH = 8; 

  
  public int EMPTY_TILE = 0xFF;
  public int EMPTY_ID = 0xF0;

  public int PILL_LEFT = 0x60;
  public int PILL_RIGHT = 0x70;
  public int PILL_TOP = 0x40;
  public int PILL_BOTTOM = 0x50;
  public int PILL_UNIT = 0x80;

  // pill orientations 
  public int PILL_ORIG = 0;
  public int PILL_90_CC = 1;
  public int PILL_REV= 2;
  public int PILL_90_C =3;

  public int VIRUS = 0xD0;

  public int YELLOW = 0x00;
  public int RED = 0x01;
  public int BLUE = 0x02;
  public int BLACK = 0x0F; 
  
  public int TILE_ID_MASK = 0xF0;  
  public int COLOR_MASK = 0x0F;
}
