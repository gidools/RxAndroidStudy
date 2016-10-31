package maxst.com.rxandroidstudy.util;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import maxst.com.rxandroidstudy.R;

/**
 * Created by Gidools on 2016-10-31.
 */

public class LogAdapter extends ArrayAdapter<String> {
	public LogAdapter(Context context, List<String> logList) {
		super(context, R.layout.item_log, R.id.item_log, logList);
	}
}
