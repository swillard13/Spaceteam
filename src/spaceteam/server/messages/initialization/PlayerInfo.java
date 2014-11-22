package spaceteam.server.messages.initialization;

import java.io.Serializable;

/**
 * Created by Ananth on 11/22/2014.
 */
public class PlayerInfo implements Serializable
{
  private String name;

  public PlayerInfo(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
