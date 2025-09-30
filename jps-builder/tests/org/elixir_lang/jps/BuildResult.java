package org.elixir_lang.jps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.MessageHandler;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.DoneSomethingNotification;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zyuyou on 15/7/17.
 */
public class BuildResult implements MessageHandler {
  private final List<BuildMessage> myErrorMessages;
  private final List<BuildMessage> myWarnMessages;
  private final List<BuildMessage> myInfoMessages;

  private boolean myUpToDate = true;

  public BuildResult(){
    myErrorMessages = new ArrayList<BuildMessage>();
    myWarnMessages = new ArrayList<BuildMessage>();
    myInfoMessages = new ArrayList<BuildMessage>();
  }

  @Override
  public void processMessage(BuildMessage msg) {
    if(msg.getKind() == BuildMessage.Kind.ERROR){
      myErrorMessages.add(msg);
      myUpToDate = false;
    }else if(msg.getKind() == BuildMessage.Kind.WARNING){
      myWarnMessages.add(msg);
    }else {
      myInfoMessages.add(msg);
    }

    if(msg instanceof DoneSomethingNotification){
      myUpToDate = false;
    }
  }

  public void assertUpToDate() {
    Assert.assertTrue("Project sources weren't up to date", myUpToDate);
  }

  public void assertFailed() {
    Assert.assertFalse("Build not failed as expected", isSuccessful());
  }

  public boolean isSuccessful(){
    return myErrorMessages.isEmpty();
  }

  public void assertSuccessful(){
      String errors = myErrorMessages.stream()
              .map(BuildMessage::toString)
              .collect(Collectors.joining("\n"));

      String infos = myInfoMessages.stream()
              .map(BuildMessage::toString)
              .collect(Collectors.joining("\n"));

      Assert.assertTrue("Build failed. \nErrors:\n" + errors +
              "\nInfo messages:\n" + infos, isSuccessful());
  }

  @NotNull
  public List<BuildMessage> getMessages(@NotNull BuildMessage.Kind kind){
    if(kind == BuildMessage.Kind.ERROR) return myErrorMessages;
    else if(kind == BuildMessage.Kind.WARNING) return  myWarnMessages;
    else return myInfoMessages;
  }
}
