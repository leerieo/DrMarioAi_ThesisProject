package drmarioai;
import nintaco.api.*;

public class Greedy_BST_V3_main {
    public static void main(final String... args) throws Throwable {
        ApiSource.initRemoteAPI("localhost", 9999);
        new DrMarioAI().launch(3);
      }
    
}
