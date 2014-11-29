package spaceteam.database;

import spaceteam.server.GameThread;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class GetRandomControl extends SQLCommand
{
  private ArrayList<Control> controlInfoList;

  public ArrayList<Control> getList() {
    return controlInfoList;
  }

  @SuppressWarnings("static-access")
  @Override
  public boolean execute() {
    try {
      Class.forName(super.DRIVER).newInstance();
      Connection conn = DriverManager.getConnection(super.DB_ADDRESS + super.DB_NAME, super.USER, super.PASSWORD);

      String controlQuery = "SELECT * FROM controls ORDER BY RAND() LIMIT " + 2*GameThread.DASH_PIECES_PER_PLAYER;
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(controlQuery);

      controlInfoList = new ArrayList<>();

      while(rs.next()) {
        String control = rs.getString("control");
        String verb = rs.getString("command_verb");
        controlInfoList.add(new Control(control, verb));
      }
    }
    catch(InstantiationException e) {
      e.printStackTrace();
    }
    catch(IllegalAccessException e) {
      e.printStackTrace();
    }
    catch(ClassNotFoundException e) {
      e.printStackTrace();
    }
    catch(SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public class Control {
    private String controlName;
    private String commandVerb;

    public Control(String controlName, String commandVerb) {
      this.controlName = controlName;
      this.commandVerb = commandVerb;
    }

    public String getControlName() {
      return controlName;
    }

    public String getCommandVerb() {
      return commandVerb;
    }
  }

}
