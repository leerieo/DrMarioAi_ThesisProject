package drmarioai.Simulator_Test;

import nintaco.api.*;
import nintaco.mappers.henggedianzi.Henggedianzi177;

import static drmarioai.Bottle.*;
import drmarioai.Placement;
import drmarioai.Address;
import drmarioai.Playfield;

import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;



public class test_sim {

    private level sim_level = new level(); 

    public void init(int seed, int level){
        sim_level.generate_level(seed, level);
    }

    private final API api = ApiSource.getAPI();
    private int pillCounter = 1;
    private int vita_idx = 0; 

    private int end_delay = 0;
    private static final int DELAY_TIME_END = 90 * 60;
  
    private int move_delay = 0;
    private static final int DELAY_TIME_MOVE = 1;
  
    private int target_y = api.readCPU(Address.CURRENT_Y);

    Queue<String> moveQueue = new LinkedList<String>();

    ArrayList<Placement> placements = new ArrayList<Placement>(); 
  
    private int pill_l = EMPTY_ID; 
    private int pill_r = EMPTY_ID; 
  
    private boolean press_down = false; 
    private boolean is_game_mode = false; 
  
    private int players=1; 

    private final sim_test_placement getPlacement = new sim_test_placement();


    public void launch(int seed1, int seed2, int level) {
        System.out.println("SIM TEST");
        int seed =256*seed1 + seed2; 
        init(seed, level);
        api.addFrameListener(() -> renderFinished(seed1,seed2,level));
        // api.addFrameListener(this::renderFinished(ver));
        api.run();
    }
    
    private void renderFinished( int seed1, int seed2, int level) {
    
        // Writes to the seed
        api.writeCPU(Address.SEED_1, seed1);
        api.writeCPU(Address.SEED_2, seed2);
        api.writeCPU(Address.LEVEL, level);

    
        // Check the current game mode 
        final int mode = api.readCPU(Address.MODE);
        if (mode != 0x04) {
          // if its not in gameplay mode don't run the AI
          pillCounter = 1;
          vita_idx = 0; 
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
        int new_pillCount = api.readCPU(Address.NUM_PILLS); 
        
        if(pillCounter<new_pillCount && is_game_mode){
          //System.out.println("new_pillCount: "+new_pillCount);
          pillCounter = new_pillCount; 
          vita_idx++; 
          int seed = 0xFF*seed1 + seed2; 
          pillSpawned(players, level, seed);
          
        }
    
      }  
      
      private void pillSpawned(int players, int level, int seed) {
        moveQueue.clear();

        System.out.println("----------------------------");
        System.out.println("NEW PILL #:"+pillCounter);
        System.out.println("vita_idx #:"+vita_idx);
    
        target_y = api.readCPU(Address.CURRENT_Y);
        press_down = false; 
        
        pill_l = api.readCPU(Address.CURRENT_COLOR_1); 
        pill_r = api.readCPU(Address.CURRENT_COLOR_2);
    
        System.out.println("pill_l: "+getColor(pill_l));
        System.out.println("pill_r: "+getColor(pill_r));
      
        Playfield playfield = new Playfield(); 
        playfield.readPlayfield();


        //Compare states 
        boolean c = compareStates(playfield,pill_l,pill_r);
        System.out.println("compareStates: "+c);
        



        if(c){

            Placement p = getPlacement.ai(pill_l, pill_r, playfield); 

            for(int i=0; i<p.move_list.size(); i++){
                moveQueue.add(p.move_list.get(i)); 
            }

            sim_level.step_level(vita_idx, p);

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

    public boolean compareStates(Playfield playfield, int pill_l, int pill_r){


        int[][] vitamins = sim_level.get_vitamins(); 
        int i = vita_idx; 
        if(i>=vitamins.length) {
            i-=vitamins.length; 
        }
     
        int[] vitamin = vitamins[i]; 
        int vita_l = vitamin[0]; 
        int vita_r = vitamin[1]; 

        if(pill_l != vita_l || pill_r != vita_r){
            System.out.println("NOT MATCH");
            System.out.println("SIM VITAMIN: "+getColor(vita_l)+"|"+getColor(vita_r));
            System.out.println("TRUE VITAMIN: "+getColor(pill_l)+"|"+getColor(pill_r));
            return false;
        }

        for(int y=0; y<HEIGHT; y++){
            for(int x=0; x<WIDTH; x++){
                int sim_item = sim_level.get_playfield()[y][x]; 
                int true_item = playfield.playfield[y][x]; 

                if(sim_item != true_item){
                    System.out.println("NOT MATCH");
                    System.out.println("ROW: "+y+" COL: "+x);
                    
                    System.out.println("SIM BOTTLE: ");
                    sim_level.printPlayfield();

                    System.out.println("TRUE BOTTLE: ");
                    playfield.printPlayfield();
                    return false; 
                }
            }
        }
        return true; 
    }

    public static void main(final String... args) throws Throwable {
        ApiSource.initRemoteAPI("localhost", 9999);
        new test_sim().launch(0x99,0x99,3);
    }

}
