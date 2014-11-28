package spaceteam.server.messages.game;

import spaceteam.server.messages.Message;

/**
 * Created by Ananth on 11/28/2014.
 */
public class Command implements Message
{
  private int widgetId;
  private int value;

  public Command(int widgetId, int value) {
    this.widgetId = widgetId;
    this.value = value;
  }

  public int getWidgetId() {
    return widgetId;
  }

  public int getValue() {
    return value;
  }
}
