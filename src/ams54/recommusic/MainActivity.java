package ams54.recommusic;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

/**
 * The MainActivity controls the flow of data across the app. It receives data from the user's device, it submits this data to a web server, gets the response from it, requests the
 * displaying to a Adapter, Manage the Contextual Menu and Handles all the interaction between the user and the app.
 * @author Andrei Martins Silva
 * @since 17/05/2014
 * @version 1.0
 *
 */
public class MainActivity extends Activity {

	private ListView lView;
	private ProgressBar pBar;
	private Object actionMode;
	private Song listItem;
	private SongFragment songFragment;
	private ArrayList<Song> songsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lView = (ListView) findViewById(R.id.suggestedSongs);
		lView.setOnItemClickListener(listener);
		lView.setSelector(R.drawable.selector);
		pBar = (ProgressBar) findViewById(R.id.pBar);

		// find the retained fragment on activity restarts
		FragmentManager fm = getFragmentManager();
		songFragment = (SongFragment) fm.findFragmentByTag("songs");

		// create the fragment and data the first time
		if (songFragment == null) {
			songFragment = new SongFragment();
			fm.beginTransaction().add(songFragment, "songs").commit();
			fm.executePendingTransactions();
			songTask();
		}
		// otherwise get the existing songFragment
		else{
			displayList(songFragment.getSongList());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Restore the list view content
		ArrayList<Song> songs = songFragment.getSongList();
		if(songs != null)
			displayList(songs);
	}

	@Override
	protected void onPause() {
		super.onPause();
		songFragment.setSongList(songsList);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		songFragment.setSongList(songsList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.refresh){
			lView.setVisibility(View.GONE);
			songTask();			
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Calls the underlying methods needed to provide recommendation.<br/>
	 * It calls the methods to get a random song, get the recommendation from the web service<br/>
	 * and display the data returned.
	 */
	protected void songTask(){
		// If the device is not connected to the internet show dialog. Otherwise follow the normal course.
		if(!isConnected()){
			noConnectionDialog();
		} else{
			final Song randomSong = RandomSongRetriever.retrieve(MainActivity.this);
			Runnable runnable = null;

			// if the device haven't got any songs
			if(randomSong == null){
				noTrackDialog();
			}
			else{
				final Handler h = new Handler();
				pBar.setVisibility(View.VISIBLE);

				runnable = new Runnable(){
					@Override
					public void run() {
						ServiceHandler serv = new ServiceHandler(randomSong);
						final ArrayList<Song> songs = serv.callWebService(MainActivity.this);

						h.post(new Runnable(){
							@Override
							public void run() {
								displayList(songs);
							}
						});
					}
				};
			}
			new Thread(runnable).start();
		}
	}

	/**
	 * Display the recommended songs in the list view
	 * @param items <br/>
	 * 		  ArrayList containing the songs to be displayed
	 */
	private void displayList(ArrayList<Song> items){
		if(items != null){
			// Retain the list items
			songsList = items;

			// Display the list of recommended songs
			pBar.setVisibility(View.GONE);
			lView.setAdapter(new MediaAdapter(this, items));
			lView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Defines the contextual menu behaviour
	 */
	private Callback mActionModeCallback = new Callback() {

		// Called when the action mode is created
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.contextual, menu);
			return true;
		}

		// Called each time the action mode is started.
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.stream:
				Intent iStream = new Intent(Intent.ACTION_VIEW, Uri.parse(listItem.streamLink));
				startActivity(iStream);
				mode.finish(); // Action picked, so close the CAB
				return true;
			case R.id.buy:
				Intent iBuy = new Intent(Intent.ACTION_VIEW, Uri.parse(listItem.buyLink));
				startActivity(iBuy);
				mode.finish();
				return true;
			default:
				return false;
			}
		}

		// Called when the user exits from the action mode
		public void onDestroyActionMode(ActionMode mode) {
			actionMode = null;
		}
	};


	/*
	 * Gets the selected song and opens a contextual menu
	 */
	OnItemClickListener listener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			ListAdapter lAdapter = lView.getAdapter();
			listItem = (Song) lAdapter.getItem(position);

			if (actionMode == null) {				
				//start the CAB using the ActionMode.Callback defined above
				actionMode = MainActivity.this
						.startActionMode(mActionModeCallback);
				lView.setSelected(true);
			}
		}
	};


	/**
	 * Verifies if the user's device is connected to the internet.
	 * @return <li><b>True</b> if the device is connected to the internet, returns <b>False</b> otherwise.</li>
	 * @see 
	 * <a href="http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html">Determining and Monitoring the connectivity status from Android Developers</a>
	 */
	private boolean isConnected(){
		boolean isConnected;

		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

		return isConnected;
	}

	/**
	 * Tells the user about the connectivity status
	 * @see <a href="http://developer.android.com/guide/topics/ui/dialogs.html">Dialogs from Android Developers</a>
	 */
	private void noConnectionDialog(){
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder connectionDialogBuilder = new AlertDialog.Builder(this);

		// 2. Chain together various setter methods to set the dialog characteristics
		connectionDialogBuilder.setMessage(R.string.no_connection_message).setTitle(R.string.no_connection_title);

		// 3. Add OK Button
		connectionDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface di, int arg1) {
				di.cancel();
			}
		});

		// 3. Get the AlertDialog from create() and show
		AlertDialog dialog = connectionDialogBuilder.create();
		dialog.show();
	}

	/**
	 * Tells the user about the lack of songs in the user's device storage
	 * @see <a href="http://developer.android.com/guide/topics/ui/dialogs.html">Dialogs from Android Developers</a>
	 */
	private void noTrackDialog(){
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder trackDialogBuilder = new AlertDialog.Builder(this);

		// Chain together various setter methods to set the dialog characteristics
		trackDialogBuilder.setMessage(R.string.no_track_message).setTitle(R.string.no_track_title);

		// Add OK Button
		trackDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface di, int arg1) {
				di.cancel();
			}
		});

		// Get the AlertDialog from create()
		AlertDialog dialog = trackDialogBuilder.create();

		dialog.show();
	}
}