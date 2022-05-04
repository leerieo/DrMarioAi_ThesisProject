package drmarioai;

import nintaco.api.*;
import static drmarioai.Bottle.*;
import java.util.ArrayList;
import java.util.Queue;

import drmarioai.Greedy_BST.Greedy_BST_V1;
import drmarioai.Greedy_BST.Greedy_BST_V2;
import drmarioai.Greedy_BST.Greedy_BST_V3;
import drmarioai.Greedy_BST.Greedy_BST_V4;


import java.util.LinkedList;


public class DrMarioAI {
  
  private final API api = ApiSource.getAPI();

  //playfield[0][0] would be the top-left-most tile 

  private int pillCounter = 1;

  // So the player can watch the ending  
  private int end_delay = 0;
  private static final int DELAY_TIME_END = 90 * 60;

  private int move_delay = 0;
  private static final int DELAY_TIME_MOVE = 1;

  private int target_y = api.readCPU(Address.CURRENT_Y);

  private int read_delay = 0;
  private static final int DELAY_TIME_READ = 10;

  // private ArrayList<String> moveList; 
  // private int move_idx; 

  Queue<String> moveQueue = new LinkedList<String>();

  ArrayList<Placement> placements = new ArrayList<Placement>(); 
  private int p_idx = 0; 

  private int pill_l = EMPTY_ID; 
  private int pill_r = EMPTY_ID; 

  private boolean press_down = false; 
  private boolean is_game_mode = false; 

  private int players=1; 

  private boolean fast;

  private final Greedy_BST_V1 gBSTV1 = new Greedy_BST_V1();
  private final Greedy_BST_V2 gBSTV2 = new Greedy_BST_V2();
  private final Greedy_BST_V3 gBSTV3 = new Greedy_BST_V3();
  private final Greedy_BST_V4 gBSTV4 = new Greedy_BST_V4();


  public void launch(int ver) {
    System.out.println("BST VER: "+ver);
    api.addFrameListener(() -> renderFinished(ver));
    // api.addFrameListener(this::renderFinished(ver));
    api.run();
  }

  private void renderFinished(int ver) {

    // Writes to the seed
    api.writeCPU(Address.SEED_1, 0x10);
    api.writeCPU(Address.SEED_2, 0x00);


    // Check the current game mode 
    final int mode = api.readCPU(Address.MODE);
    if (mode != 0x04) {
      // if its not in gameplay mode don't run the AI
      pillCounter = 1;
      return;
    }
    
    // Handles the number of players 
    // TODO: Modify this to support 2-player 
    players = api.readCPU(Address.NUMBER_OF_PLAYERS);
    if(players > 1){
      return; 
    }
    
    // handles when a stage is finished 
    if (api.readCPU(Address.STAGE_CLEARED) == 0x01) {
      final boolean ending = api.readCPU(Address.ENDING) != 0x0A;
      
      //if it's the end of the game 
      if (ending && end_delay==0) {
        end_delay = DELAY_TIME_END;
      }
      
      if (end_delay > 0) {
        end_delay--;
      } else {
        api.writeGamepad(players - 1, GamepadButtons.Start, true);

        // reset stuff for next game 
        pillCounter = 1; 
        press_down = false; 
        moveQueue.clear();
        
      }
      is_game_mode = false; 
    }else{
      is_game_mode = true; 
    }

    // If the player is pressing the down button 
    if(press_down){
      api.writeGamepad(players - 1, GamepadButtons.Down, true);
    }
  
    //process the moveQueue 
    int curr_y = api.readCPU(Address.CURRENT_Y); 
    if(move_delay<=0 && curr_y<=target_y){
      if(!moveQueue.isEmpty()){
        // String move = moveList.get(move_idx);
        String move = moveQueue.remove(); 
        switch(move){
          case "l":
            api.writeGamepad(players - 1, GamepadButtons.Left, true);
            break;
          case "r":
            api.writeGamepad(players - 1, GamepadButtons.Right, true);
            break;
          case "cw":
            api.writeGamepad(players - 1, GamepadButtons.A, true);
            break;
          case "ccw":
            api.writeGamepad(players - 1, GamepadButtons.B, true);
            break;
          case "d":
            System.out.println("DOWN: "+String.format("%x", curr_y));
            // forceDrop();
            api.writeGamepad(players - 1, GamepadButtons.Down, true);
            target_y=curr_y-1; 
            // int curr_y = api.readCPU(Address.CURRENT_Y)-1; 
            // System.out.println("DOWN: "+String.format("%x", curr_y));
            // api.writeCPU(Address.CURRENT_Y, curr_y);
            
            break;
          case "dp":
            press_down = true; 
            break;
        }
        move_delay = DELAY_TIME_MOVE;
      }
    }else if(move_delay<=0){
      //adjust this to check if we're at a landing position 
      // if(checkIsLandingSpot()){
      //   api.writeGamepad(players - 1, GamepadButtons.Down, true);
      // }
      

    }else{
      move_delay--; 
    }


    // Check if a new pill has entered the bottle 
    

    //show placements 
    // if(p_idx<placements.size()){
    //   api.writeCPU(Address.FRAMES_UNTIL_DROP, 10);
    //   if(read_delay<=0){
    //     if(api.readGamepad(players-1, GamepadButtons.Up)){
  
    //       Integer[] coor = placements.get(p_idx).pos; 
    //       System.out.print("PLACEMENT: [");
    //       for(int k=0; k<3; k++){
    //           System.out.print(coor[k]+", ");
    //       }
    //       System.out.println("]");
  
    //       writePlacement(coor, pill_l, pill_r);
    //       api.writeCPU(0x86,0);
  
    //       p_idx++; 
    //       read_delay = DELAY_TIME_READ; 
    //     }
    //   }else{
    //     read_delay--;
    //   }
    // }

    


    int new_pillCount = api.readCPU(Address.NUM_PILLS); 
    
    if(pillCounter<new_pillCount && is_game_mode){
      // System.out.println("new_pillCount: "+new_pillCount);
      pillCounter = new_pillCount; 
      pillSpawned(players, ver);
      
    }

  }  
  
  private void pillSpawned(int players, int ver) {

    System.out.println("----------------------------");
    System.out.println("NEW PILL #:"+pillCounter);


   
    p_idx = 0; 
    target_y = api.readCPU(Address.CURRENT_Y);
    press_down = false; 
    
    pill_l = api.readCPU(Address.CURRENT_COLOR_1); 
    pill_r = api.readCPU(Address.CURRENT_COLOR_2);

    System.out.println("pill_l: "+getColor(pill_l));
    System.out.println("pill_r: "+getColor(pill_r));
  
    Playfield playfield = new Playfield(); 
    playfield.readPlayfield();

    switch(ver){
      case 1: 
        moveQueue = gBSTV1.ai(pill_l, pill_r, playfield);
        break; 
      case 2: 
        moveQueue = gBSTV2.ai(pill_l, pill_r, playfield);
      break;
      case 3:
        moveQueue = gBSTV3.ai(pill_l, pill_r, playfield); 
      break;
      case 4:
        moveQueue = gBSTV4.ai(pill_l, pill_r, playfield);
      break; 
    }
    
  }


  
  
  public void writePlacement(Integer[] p, int pill_l, int pill_r){

    int row = p[0]; 
    int col = p[1]; 
    int orientation = p[2]; 

    if(orientation%2==0){

      int address_l = Address.P1_PLAYFIELD | (row << 3) | col; 
      int address_r = Address.P1_PLAYFIELD | (row << 3) | (col+1); 

      int tile_l = api.readCPU(address_l); 
      int tile_r = api.readCPU(address_r); 

      if(tile_l!=EMPTY_TILE || tile_r!=EMPTY_TILE){
        System.out.println("ERROR ERROR: INVALID PLACEMENT");
      }

      if(orientation==PILL_ORIG){
        tile_l = PILL_LEFT | pill_l; 
        tile_r = PILL_RIGHT | pill_r; 
      }else{
        tile_l = PILL_LEFT | pill_r; 
        tile_r = PILL_RIGHT | pill_l; 
      }

      api.writeCPU(address_l, tile_l);
      api.writeCPU(address_r, tile_r);

    }else{

      int address_b = Address.P1_PLAYFIELD | (row << 3) | col; 
      int address_t = Address.P1_PLAYFIELD | ((row-1) << 3) | col; 

      int tile_b = api.readCPU(address_b); 
      int tile_t = api.readCPU(address_t); 

      if(tile_b!=EMPTY_TILE || tile_t!=EMPTY_TILE){
        System.out.println("ERROR ERROR: INVALID PLACEMENT");
      }

      if(orientation==PILL_90_C){
        tile_t = PILL_TOP| pill_l; 
        tile_b = PILL_BOTTOM | pill_r; 
      }else{
        tile_t = PILL_TOP| pill_r; 
        tile_b = PILL_BOTTOM | pill_l; 
      }

      api.writeCPU(address_b, tile_b);
      api.writeCPU(address_t, tile_t);


    }

  }

  public String getColor(int tile){
    int color_id = tile & COLOR_MASK; 
    switch(color_id){
      case YELLOW:
        return "YELLOW"; 
      case RED:
        return "RED"; 
      case BLUE: 
        return "BLUE"; 
      default:
        return "EMPTY";
    }
  }


  
  private void stallDrop() {
    api.writeCPU(Address.FRAMES_UNTIL_DROP, 0xFF);
  }
  
  private void forceDrop() {
    api.writeCPU(Address.FRAMES_UNTIL_DROP, 0x01);
  }

  private boolean checkIsLandingSpot(){
    int y = api.readCPU(Address.CURRENT_Y);
    int x = api.readCPU(Address.CURRENT_X); 

    if(y<=0){
      return true;
    }

    int orientation = api.readCPU(Address.CURRENT_ORIENTATION);  

    if(orientation%2==0){
      //horizontal 
      int tile_l = api.readCPU(Address.P1_PLAYFIELD | ((y-1) << 3) | x);
      int tile_r = api.readCPU(Address.P1_PLAYFIELD | ((y-1) << 3) | (x+1));

      if(tile_l!=EMPTY_TILE || tile_r!=EMPTY_TILE){
        return true; 
      }

    }else{
      //vertical 
      int tile = api.readCPU(Address.P1_PLAYFIELD | ((y-1) << 3) | x);

      if(tile!=EMPTY_TILE){
        return true; 
      }
    }

    return false; 
  }
}
