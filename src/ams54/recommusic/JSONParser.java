package ams54.recommusic;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
* JSONParser has two static methods: parse(Context ctx, String jsonStr) and getBitmap(String imageURL).
* Both are used to transform a given JSON string in a set of songs with useful information in it.
* The former takes a String and convert it in a Array of Songs. The latter supports the former when it comes to getting image files from a given url.
* @author Andrei Martins Silva <br/>
* @since 17/05/2014 <br/>
* @version 1.0
*/
public class JSONParser {

	/**
	 * Parses a Json String into a ArrayList of Songs.
	 * @param ctx <br/>
	 * 		  Context in which the method is being executed.
	 * @param jsonStr <br/>
	 * 		  String to be parsed.
	 * @return
	 * 		  An ArrayList(Song) object containing the songs retrieved from the JSON String.
	 * 
	 * @throws JSONException
	 */
	protected static ArrayList<Song> parse(Context ctx, String jsonStr) throws JSONException{
		JSONArray tracks;
		int len = 0;
		JSONObject json = new JSONObject(jsonStr);
		JSONObject root = json.getJSONObject("similartracks");

		tracks = root.getJSONArray("track");

		ArrayList<Song> songs = new ArrayList<Song>();
		len = tracks.length();

		for(int i = 0; i < len; i++){
			JSONObject track = tracks.getJSONObject(i);
			Song song = new Song(track.getString("name"), track.getJSONObject("artist").getString("name"));

			try{
				song.cover = getBitmap(track.getJSONArray("image").getJSONObject(1).getString("#text"));
			} catch (JSONException j){
				Bitmap noIcon = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_no_icon);
				song.cover = noIcon;
			}

			songs.add(song);
		}

		return songs;
	}

	/**
	 * Returns the correspondent Bitmap given its URL.
	 * @param imageURL
	 * @return
	 * 		  Bitmap for the given URL
	 */
	private static Bitmap getBitmap(String imageURL){
		Bitmap bmp = null;

		try {
			bmp = BitmapFactory.decodeStream((InputStream) new URL(imageURL).getContent());
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}

		return bmp;
	}

}
