package ams54.recommusic;

import android.widget.ImageView;
import android.widget.TextView;
/**
* This class provides the model for the data contained in each row of a list of recommended songs.
* It is used to apply the View Holder Pattern, which improves the list performance by reducing the number of calls to findViewById method.
* A detailed discussion can be seen here in this <a href="http://www.codeofaninja.com/2013/09/android-viewholder-pattern-example.html">post by Mike Dalisay</a>
* @author Andrei Martins Silva <br/>
* @since 17/05/2014 <br/>
* @version 1.0
*/

public class ViewHolder {
	ImageView cover;
	TextView song, artist;
}
