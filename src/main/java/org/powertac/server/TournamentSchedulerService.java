package org.powertac.server;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.apache.log4j.Logger;
import org.powertac.common.Competition;
import org.powertac.common.config.ConfigurableValue;
import org.powertac.common.interfaces.InitializationService;
import org.powertac.common.interfaces.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TournamentSchedulerService
{
  static private Logger log = 
      Logger.getLogger(TournamentSchedulerService.class.getName());

  private String tournamentSchedulerUrl = "";

  // URL offsets
  // These cannot be set by initialization, because they are need to get
  // initialization data.
  private String interfaceUrl = "faces/serverInterface.jsp";
  
  private String propertiesUrl = "faces/properties.jsp";

  private int gameId = 0;


  public int getGameId()
  {
    return gameId;
  }

  public void setGameId(int gameId)
  {
    this.gameId = gameId;
  }

  public String getTournamentSchedulerUrl()
  {
    return tournamentSchedulerUrl;
  }

  public void setTournamentSchedulerUrl(String tournamentSchedulerUrl)
  {
    this.tournamentSchedulerUrl = tournamentSchedulerUrl;
  }
  
  public URL getBootUrl ()
  {
    URL result = null;
    String urlString = tournamentSchedulerUrl
            + interfaceUrl
            + "?action=boot"
            + "&gameId=" + gameId;
    try {
      result = new URL(urlString);
    }
    catch (MalformedURLException e) {
      log.error("Bad URL: " + urlString);
      e.printStackTrace();
    }
    return result;
  }
  
  public URL getConfigUrl ()
  {
    URL result = null;
    String urlString = tournamentSchedulerUrl
            + propertiesUrl
            + "?gameId=" + gameId;
    try {
      result = new URL(urlString);
    }
    catch (MalformedURLException e) {
      log.error("Bad URL: " + urlString);
      e.printStackTrace();
    }    
    return result;
  }

  public void ready()
  {
    if (tournamentSchedulerUrl.isEmpty())
      return;
    String finalUrl = tournamentSchedulerUrl + interfaceUrl 
        + "?action=status"
        + "&gameId=" + gameId
        + "&status=game_ready";
    log.info("Sending game_ready to controller at: " + finalUrl);

    try {
      URL url = new URL(finalUrl);
      URLConnection conn = url.openConnection();
      // Get the response
      InputStream input = conn.getInputStream();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Jenkins failure");
    }
  }

  public void inProgress(int gameLength)
  {
    if (tournamentSchedulerUrl.isEmpty())
      return;
    String finalUrl = tournamentSchedulerUrl + interfaceUrl 
        + "?action=status"
        + "&gameId=" + gameId
        + "&status=game_in_progress"
        + "&gameLength=" + gameLength;
    log.info("Sending game_in_progress message to controller at: " + finalUrl);

    try {
      URL url = new URL(finalUrl);
      URLConnection conn = url.openConnection();
      // Get the response
      InputStream input = conn.getInputStream();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Jenkins failure");
    }
  }
  
  public void heartbeat (int timeslotIndex, String standings)
  {
    if (tournamentSchedulerUrl.isEmpty())
      return;

    try {
			String finalUrl = tournamentSchedulerUrl + interfaceUrl 
					+ "?action=heartbeat"
          + "&gameId=" + gameId
          + "&message=" + timeslotIndex
          + "&standings=" + URLEncoder.encode(standings, "UTF-8");

      URL url = new URL(finalUrl);
      URLConnection conn = url.openConnection();
      // Get the response
      InputStream input = conn.getInputStream();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("heartbeat failure");
    }
  }
  
  public void sendResults (String results)
  {
    if (tournamentSchedulerUrl.isEmpty())
      return;

    try {
      String finalUrl = tournamentSchedulerUrl + interfaceUrl;
      String postData = "action=gameresults"
          + "&gameId=" + gameId
          + "&message=" + URLEncoder.encode(results, "UTF-8");

      URL url = new URL(finalUrl);
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
      wr.write(postData);
      wr.flush();
      // Get the response
      InputStream input = conn.getInputStream();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("heartbeat failure");
    }
  }
}
