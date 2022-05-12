package drmarioai;

import nintaco.api.*;
import static drmarioai.Bottle.*;
import java.util.Queue;

import drmarioai.Greedy.*;

import java.util.LinkedList;


public class DrMarioAI {
  
  private final API api = ApiSource.getAPI();

  //keeps track of the number of pills generated
  private int pillCounter = 1;

  // So the player can watch the ending  
  private int end_delay = 0;
  private static final int DELAY_TIME_END = 90 * 60;

  private int move_delay = 0;
  private static final int DELAY_TIME_MOVE = 1;

  private int target_y = api.readCPU(Address.CURRENT_Y);

  Queue<String> moveQueue = new LinkedList<String>();

  private int pill_l = EMPTY_ID; 
  private int pill_r = EMPTY_ID; 

  private boolean press_down = false; 
  private boolean is_game_mode = false; 

  private int players=1; 


  //AI's 
  private final GreedyV1 gV1 = new GreedyV1();
  private final GreedyV2 gV2 = new GreedyV2();
  private final GreedyV3 gV3 = new GreedyV3();
  private final GreedyV4 gV4 = new GreedyV4();

  public void launch(int ver) {
    System.out.println("TOP VIEW ONLY VER: "+ver);
    api.addFrameListener(() -> renderFinished(ver));
    // api.addFrameListener(this::renderFinished(ver));
    api.run();
  }

  
  private void renderFinished(int ver) {

    // Writes to the seed
    api.writeCPU(Address.SEED_1, 0xFF);
    api.writeCPU(Address.SEED_2, 0xFF);

    // Check the current game mode 
    final int mode = api.readCPU(Address.MODE);
    if (mode != 0x04) {
      // if its not in gameplay mode don't run the AI
      pillCounter = 1;
      return;
    }


    
    // Handles the number of players 
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
            api.writeGamepad(players - 1, GamepadButtons.Down, true);
            target_y=curr_y-1; 
            break;
          case "dp":
            press_down = true; 
            break;
        }
        move_delay = DELAY_TIME_MOVE;
      }
    }else{
      move_delay--; 
    }

    int new_pillCount = api.readCPU(Address.NUM_PILLS); 
    
    if(pillCounter<new_pillCount && is_game_mode){
      pillCounter = new_pillCount; 
      pillSpawned(players, ver);
    }

  }  
  
  private void pillSpawned(int players, int ver) {

    System.out.println("----------------------------");
    System.out.println("NEW PILL #:"+String.format("0x%08X", pillCounter));
 

    target_y = api.readCPU(Address.CURRENT_Y);
    press_down = false; 
    
    pill_l = api.readCPU(Address.CURRENT_COLOR_1); 
    pill_r = api.readCPU(Address.CURRENT_COLOR_2);

    System.out.println("pill_l: "+getColor(pill_l));
    System.out.println("pill_r: "+getColor(pill_r));

    ItemColumn[] top_items = new ItemColumn[WIDTH];
    GreedyUtils gu = new GreedyUtils(); 

    gu.readTopItems(top_items);
    gu.printTopItems(top_items);

    // possible moves: l,r,cw,ccw
    switch(ver){
      case 1:
        moveQueue = gV1.ai(pill_l,pill_r,top_items);
        break; 
      case 2: 
        moveQueue = gV2.ai(pill_l,pill_r,top_items);
        break; 
      case 3: 
        moveQueue = gV3.ai(pill_l,pill_r,top_items);
        break; 
      case 4: 
        System.out.println("test");
        Playfield p = new Playfield();
        p.readPlayfield();
        moveQueue = gV4.ai(pill_l,pill_r,p,top_items);
        break; 
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

}
