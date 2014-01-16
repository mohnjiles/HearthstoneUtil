package com.jt.hearthstone;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.integer;
import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static butterknife.Views.findById;

public class NewsListAdapter extends BaseAdapter {
	private Context mContext;
	private int count;
	private List<String> newsTitles;
	private List<String> urls;
	private ImageLoader loader = ImageLoader.getInstance();

	public NewsListAdapter(Context c, int count, List<String> newsTitles,
			List<String> urls) {
		mContext = c;
		this.count = count;
		this.newsTitles = newsTitles;
		this.urls = urls;

	}

	public int getCount() {
		return count;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// Custom ViewHolder class to make scrolling smoother
	static class ViewHolder {
		TextView tvNewsTitle = null;
		ImageView ivClass = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		Typeface font = TypefaceCache.get(mContext.getAssets(),
				"fonts/belwebd.ttf");

		// If our view (in this case, one item from the gridview) is null, then
		// inflate it.
		// We do this because re-using views makes memory happy :)
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.news_list_view, null);
			vh = new ViewHolder();
			vh.tvNewsTitle = findById(convertView, R.id.tvDeckName);
			vh.ivClass = findById(convertView, R.id.ivClassImage);

			convertView.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		vh.tvNewsTitle.setTypeface(font);
		vh.tvNewsTitle.setText(newsTitles.get(position));

		if (!loader.isInited()) {
			loader.init(Utils.config(mContext));
		}

		loader.displayImage(urls.get(position), vh.ivClass,
				Utils.defaultOptions);

		return convertView;

	}
}
