package ams54.recommusic;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
/**
 * This class was implemented to deal with the problem of <a href="http://developer.android.com/guide/topics/resources/runtime-changes.html">Handling Runtime Changes</a>.
 * Specially the one where the orientation is changed from portrait to landscape and vice versa.
 * Two methods are available. setSongList is used to save the status of the listview. So, when the orientation changes (the activity is destroyed and re-created), we can
 * get the previous status by using the getSongList. This prevents the app from "forgetting what was being displayed when the orientation was changed".
 * @author Andrei Martins Silva
 * @since 17/05/2014
 * @version 1.0
 *
 */
public class SongFragment extends Fragment {
	private ArrayList<Song> songList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	public ArrayList<Song> getSongList() {
		return songList;
	}

	public void setSongList(ArrayList<Song> songList) {
		this.songList = songList;
	}	
}
