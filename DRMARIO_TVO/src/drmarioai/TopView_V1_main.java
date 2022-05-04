package drmarioai;
import nintaco.api.*;

public class TopView_V1_main {

  public static void main(final String... args) throws Throwable {
    ApiSource.initRemoteAPI("localhost", 9999);
    new DrMarioAI().launch(1);
    // new DrMarioAI().launch();
  }
}
