package ams54.recommusic;

import android.graphics.Bitmap;
/**
 * This class represents a Song. It contains the necessary fields to represent it such as name and artist.
 * It also has fields for storing additional data like ImageUrl, Stream Link and Buy Link.
 * 
 * All the information about songs used by the application are stored in objects of this class.
 * 
 * Three constructors are provided: Song(), Song(String name, String artist) and 
 * Song(String songName, String artist, String imageURL).
 *  
 * @author Andrei Martins Silva <br/>
 * @since 17/05/2014 <br/>
 * @version 1.0
 * 
 */
public class Song {
	protected String name;
	protected String artist;
	protected Bitmap cover;
	protected String streamLink = "http://www.youtube.com/results?search_query=";
	protected String buyLink = "http://www.amazon.co.uk/s/ref=nb_sb_noss?url=search-alias%3Ddigital-music&field-keywords=";

	public Song() {
		name = "";
		artist = "";
	}

	public Song(String songName, String artistName){
		name = songName;
		artist = artistName;
		streamLink += artist + "-" + name;
		buyLink += artist + "-" + name;
	}

	public Song(String songName, String artistName, Bitmap coverImage){
		name = songName;
		artist = artistName;
		cover = coverImage;
		streamLink += artist + "-" + name;
		buyLink += artist + "-" + name;
	}
}