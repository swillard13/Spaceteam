package spaceteam.server.messages.game;

import java.io.Serializable;
import java.util.List;

import spaceteam.server.messages.Message;
import spaceteam.shared.Widget;

/**
 * Created by Ananth on 11/28/2014.
 */
public class LevelStart implements Message
{
  private List<Widget> widgetList;
  private int secondsPerCommand;
  private boolean first;

  public <T extends List<Widget> & Serializable> LevelStart(T widgetList, int secondsPerCommand, boolean first) {
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
