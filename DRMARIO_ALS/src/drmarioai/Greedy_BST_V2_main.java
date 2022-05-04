package drmarioai;
import nintaco.api.*;

public class Greedy_BST_V2_main {
    public static void main(final String... args) throws Throwable {
        ApiSource.initRemoteAPI("localhost", 9999);
        new DrMarioAI().launch(2);
        // new DrMarioAI().launch();
      }
    
}
