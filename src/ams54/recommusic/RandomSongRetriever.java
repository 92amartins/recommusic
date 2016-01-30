package ams54.recommusic;

import java.util.Random;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

/**
 * Retrieves a random song from the user's devices. It looks up in both, internal and external storages.
 * It makes use of the Media Content Provider to query for songs.
 * @author Andrei Martins Silva
 * @since 17/05/2014
 * @version 1.0
 *
 */
public class RandomSongRetriever {
	protected static Song retrieve(Activity activity) {
		ContentResolver cr;
		Cursor cursor;
		Song randomSong;

		//Output Fields
		String title, artist;

		//Query Fields
		final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		final String[] proj = {MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE};
		final String where = MediaStore.Audio.Media.IS_MUSIC + " <> ?";
		final String sel[] = {"0"};

		// Obtaining the quantity of songs in the user's device
		cr = activity.getContentResolver();
		cursor = cr.query(uri, new String[]{"count(*) AS count"}, where, sel, null);
		cursor.moveToFirst();
		final int cursorSize = cursor.getInt(0);

		// If the device does not contains any songs, return null.
		if(cursorSize == 0){
			randomSong = null;
		}
		else{
			
			cursor.close();
			
			//Random Index between [0,cursorSize) to select the song.
			final Random random = new Random();
			int index = random.nextInt(cursorSize);
			final String sortOrder = BaseColumns._ID + " limit " + (index+1); 
			
			// Querying the MediaStore Content Provider and obtaining the set of songs in the phone
			cursor = cr.query(uri, proj, where, sel, sortOrder);
			
			// Move to the random row
			cursor.moveToFirst();
			cursor.move(index);			

			// Get the title and artist from the provider
			title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			randomSong = new Song(title, artist);	
		}
		
		cursor.close();		
		return randomSong;
	}
}
