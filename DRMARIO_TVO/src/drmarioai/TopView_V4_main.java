package drmarioai;
import nintaco.api.*;

public class TopView_V4_main {

  public static void main(final String... args) throws Throwable {
    ApiSource.initRemoteAPI("localhost", 9999);
    new DrMarioAI().launch(4);
    // new DrMarioAI().launch();
  }
}
