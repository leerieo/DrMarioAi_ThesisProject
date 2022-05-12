package SOLVERS;


public class Bottle {

  public static final int HEIGHT = 16; 
  public static final int WIDTH = 8; 

  
  public static int EMPTY_TILE = 0xFF;
  public static int EMPTY_ID = 0xF0;

  public static int PILL_LEFT = 0x60;
  public static int PILL_RIGHT = 0x70;
  public static int PILL_TOP = 0x40;
  public static int PILL_BOTTOM = 0x50;
  public static int PILL_UNIT = 0x80;

  // pill orientations 
  public static int PILL_ORIG = 0;
  public static int PILL_90_CC = 1;
  public static int PILL_REV= 2;
  public static int PILL_90_C =3;

  public static int VIRUS = 0xD0;

  public static int YELLOW = 0x00;
  public static int RED = 0x01;
  public static int BLUE = 0x02;
  public static int BLACK = 0x0F; 
  
  public static int TILE_ID_MASK = 0xF0;  
  public static int COLOR_MASK = 0x0F;
}
