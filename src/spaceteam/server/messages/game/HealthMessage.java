package spaceteam.server.messages.game;

import spaceteam.server.messages.Message;

/**
 * Created by Ananth on 11/24/2014.
 */
public class HealthMessage implements Message
{
  private int currHealth;

  public HealthMessage(int currHealth) {
    this.currHealth = currHealth;
  }

  public int getCurrHealth() {
    return currHealth;
  }
}
