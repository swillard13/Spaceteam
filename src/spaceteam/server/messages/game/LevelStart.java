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
  private boolean first;

  public LevelStart(List<Widget> widgetList, int secondsPerCommand, boolean first) {
    this.widgetList = widgetList;
    this.secondsPerCommand = secondsPerCommand;
    this.first = first;
  }
  
  public int getSeconds() {
	  return secondsPerCommand;
  }
  
  public List<Widget> getWidgetList() {
	  return widgetList;
  }
  
  public boolean getIfFirst() {
	  return first;
  }
}
