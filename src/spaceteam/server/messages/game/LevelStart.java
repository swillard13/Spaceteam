package spaceteam.server.messages.game;

import spaceteam.server.messages.Message;
import spaceteam.shared.AbstractWidget;
import spaceteam.shared.Widget;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ananth on 11/28/2014.
 */
public class LevelStart implements Message
{
  private List<Widget> widgetList;
  private int secondsPerCommand;

  public LevelStart(List<Widget> widgetList, int secondsPerCommand) {
    this.widgetList = widgetList;
    this.secondsPerCommand = secondsPerCommand;
  }
  
  public int getSeconds() {
	  return secondsPerCommand;
  }
  
  public List<Widget> getWidgetList() {
	  return widgetList;
  }
}
