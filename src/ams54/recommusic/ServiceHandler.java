package ams54.recommusic;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONException;

/**
 * Class responsible for querying the last.fm api and retrieve the json response with the set of recommended songs.
 * @author Andrei Martins Silva
 * @since 17/05/2014
 * @version 1.0
 */
public class ServiceHandler {

	// Service Input Parameters
	private String path;
	private String SONG, ARTIST;
	private final String LIMIT = "10";

	// Service Output Parameters
	private static String response;
	private ArrayList<Song> songs;
	private HttpURLConnection httpCon;

	public ServiceHandler(Song song){
		SONG = song.name;
		ARTIST = song.artist;
	}

	/**
	 * Submits a query to the <a href="http://www.last.fm/api">Last.fm</a> api which returns a set of recommended songs.
	 * @param ctx <br/>
	 * 		  The activity in which the method is being called.
	 * @return
	 * 		  An ArrayList(Song) containing the recommendation from last.fm api.
	 * 		  
	 */
	protected ArrayList<Song> callWebService(MainActivity ctx) {
		try{
			response = "";
			
			// Setting up the connection
			SONG = URLEncoder.encode(SONG, "UTF-8");
			ARTIST = URLEncoder.encode(ARTIST, "UTF-8");

			path = "http://ws.audioscrobbler.com/2.0/?method=track.getsimilar&artist=" 
					+ ARTIST + "&track=" + SONG 
					+ "&api_key=79e948a578829291fe81afa6d581eb5d&format=json&limit=" + LIMIT;
			
			URL url = new URL(path);
			httpCon = (HttpURLConnection) url.openConnection();

			System.setProperty("http.keepAlive", "false");
			httpCon.setUseCaches(false);
			httpCon.setRequestMethod("POST");

			httpCon.connect();

			InputStream in = new BufferedInputStream(httpCon.getInputStream());
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
			String s = "";
			do{
				s = buffer.readLine();
				response += s;				
			}while(s != null);
		}
		catch(IOException e){		
		}		
		finally{
			httpCon.disconnect();
		}

		try{
			songs = JSONParser.parse(ctx, response);
		} catch(JSONException j){
			ArrayList<Song> err = new ArrayList<Song>();
			err.add(new Song("Unable to find recommendations for you.", "try again!"));
			songs = err;
		}
		
		return songs;		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSONG() {
		return SONG;
	}

	public void setSONG(String song) {
		SONG = song;
	}

	public String getARTIST() {
		return ARTIST;
	}

	public void setARTIST(String artist) {
		ARTIST = artist;
	}
}
