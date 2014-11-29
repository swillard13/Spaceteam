package spaceteam.server.messages.game;

import spaceteam.server.messages.Message;
import spaceteam.shared.AbstractWidget;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ananth on 11/28/2014.
 */
public class LevelStart implements Message
{
  private List<AbstractWidget> widgetList;
  private int secondsPerCommand;

  public LevelStart(List<AbstractWidget> widgetList, int secondsPerCommand) {
    this.widgetList = widgetList;
    this.secondsPerCommand = secondsPerCommand;
  }
  
  public int getSeconds() {
	  return secondsPerCommand;
  }
}
