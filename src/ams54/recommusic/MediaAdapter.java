package ams54.recommusic;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * The MediaAdapter class extends the BaseAdapter class to display the set of recommended songs in a list view.
 * It implements the ViewHolder pattern and uses view's recycling due to performance reasons. The implementation
 * of these patterns is the responsible for the smoothness while scrolling down the list.
 * @author Andrei Martins Silva
 * @since 17/05/2014
 * @version 1.0
 *
 */
public class MediaAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Song> songs;
	private int size;

	public MediaAdapter(Context cxt, ArrayList<Song> list) {
		context = cxt;
		songs = list;
		size = songs.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder;

		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row, parent, false);

			vHolder = new ViewHolder();
			vHolder.cover = (ImageView) convertView.findViewById(R.id.cover);
			vHolder.song = (TextView) convertView.findViewById(R.id.songName);
			vHolder.artist = (TextView) convertView.findViewById(R.id.artistName);

			convertView.setTag(vHolder);
		}else{
			vHolder = (ViewHolder) convertView.getTag();
		}
		
		// Setting song data
		Song S = songs.get(position);
		vHolder.cover.setImageBitmap(S.cover);					
		vHolder.song.setText(S.name);
		vHolder.artist.setText(S.artist);

		return convertView;
	}

	@Override
	public int getCount() {
		return size;
	}

	@Override
	public Object getItem(int position) {
		return songs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
