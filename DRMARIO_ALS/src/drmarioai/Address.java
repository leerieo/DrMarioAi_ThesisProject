package drmarioai;

public interface Address {
  int MODE = 0x0046;
  int NUMBER_OF_PLAYERS = 0x0727;
  int STAGE_CLEARED = 0x0055;
  int ENDING = 0x0053;
  int CURRENT_X = 0x0305;
  int CURRENT_Y = 0x0306;
  int LEVEL = 0x0316; 
  int CURRENT_ORIENTATION = 0x0325;
  int NUM_PILLS = 0x0310;
  int CURRENT_COLOR_1 = 0x0301;
  int CURRENT_COLOR_2 = 0x0302;
  int NEXT_COLOR_1 = 0x031A;
  int NEXT_COLOR_2 = 0x031B;
  int P1_PLAYFIELD = 0x0400;
  int P2_PLAYFIELD = 0x0500;
  int FRAMES_UNTIL_DROP = 0x0312;

  int SEED_1 = 0x0819; 
  int SEED_2 = 0x081A;

  // int SEED_1 = 0x081B; 
  // int SEED_2 = 0x081C;

  // writing 6 here will have mario throw in the next pill without placing the current one 
  int TOSS_IN_NEXT_PILL = 0x0317;
  int CURRENT_PILL_HEIGHT = 0x0306;
}
