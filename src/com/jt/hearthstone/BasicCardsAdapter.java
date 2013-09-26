package com.jt.hearthstone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class BasicCardsAdapter extends BaseAdapter {
	private Context mContext;
	private List<Cards> cardList;
	public static String url;
	private Cards[] cards;
	private int classNum;
	ImageLoader imageLoader = ImageLoader.getInstance();

	public BasicCardsAdapter(Context c, int classNum) {
		mContext = c;
		this.classNum = classNum;
	}

	public int getCount() {
		return cardList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// Custom ViewHolder class to make scrolling smoother
	static class ViewHolder {
		ImageView iv = null;
		TextView tv = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		getCards();
		for (Cards card : cards) {
			if (card.getClasss().intValue() == classNum
					&& card.getQuality().intValue() == 1
					&& card.getSet().intValue() == 2) {
				cardList.add(card);
			}
		}
		url = "http://55.224.222.135/"
				+ cardList.get(position).getImage() + ".png";

		// ImageLoader options to save images in Memory so we don't have to
		// re-draw them.
		// We may eventually need to disable this based on further testing
		// Not sure if real phones will have enough RAM to hold 500+ images,
		// granted they're
		// only a few KB each.
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.cards).cacheInMemory(false)
				.cacheOnDisc(true).build();
		
		if (!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		}

		// If our view (in this case, one item from the gridview) is null, then
		// inflate it.
		// We do this because re-using views makes memory happy :)
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.grid_layout, null);
			vh = new ViewHolder();

			vh.tv = (TextView) convertView.findViewById(R.id.grid_item_text);
			vh.iv = (ImageView) convertView.findViewById(R.id.grid_item_image);
			convertView.setTag(vh);
			vh.iv.setImageResource(R.drawable.cards);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		// Set the TextView color to black, even though we can do this in XML.
		// On second thought, do we even need the TextView?
		vh.tv.setTextColor(Color.WHITE);
		// Set the Text of the TextView
		vh.tv.setText(cardList.get(position).getName());
		// Load the image for the ImageView
		imageLoader.displayImage(url, vh.iv, options);
		return convertView;

	}

	private void getCards() {
		Gson gson = new Gson();
		InputStream is = mContext.getResources().openRawResource(R.raw.cards);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// The json String from the file
		String jsonString = writer.toString();

		// Set our pojo from the GSON data
		cards = gson.fromJson(jsonString, Cards[].class);
	}
}