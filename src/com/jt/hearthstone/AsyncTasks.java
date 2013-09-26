package com.jt.hearthstone;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.View;

public class AsyncTasks {
	public class SaveDeck extends AsyncTask<Void, Void, Void> {
		private Context context;
		private String deckName;
		private Object object;
		private int position;

		public SaveDeck(Context cxt, String deckName, Object object,
				int position) {
			context = cxt;
			this.deckName = deckName;
			this.object = object;
			this.position = position;
		}

		@Override
		protected Void doInBackground(Void... params) {
			FileOutputStream fos = null;
			try {
				fos = context.openFileOutput(deckName, Context.MODE_PRIVATE);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(fos);
				oos.writeObject(object);
				oos.close();
				fos.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			new GetDeckActivityDeck(context, deckName, position).execute();
		}

	}

	public class GetClassesDeck extends AsyncTask<Void, Void, List<Integer>> {
		private Context context;
		private ProgressDialog dialog;
		private int position;

		public GetClassesDeck(Context cxt, int position) {
			context = cxt;
			this.position = position;
		}

		@Override
		protected List<Integer> doInBackground(Void... params) {
			InputStream instream = null;
			List<Integer> list = null;
			try {
				instream = context.openFileInput("deckclasses");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			try {
				if (instream != null) {
					ObjectInputStream objStream = new ObjectInputStream(
							instream);
					try {
						list = (List<Integer>) objStream.readObject();
						if (instream != null) {
							instream.close();
						}
						if (objStream != null) {
							objStream.close();
						}

					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					list = new ArrayList<Integer>();
				}
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(context, "", "Loading...");
		}

		@Override
		protected void onPostExecute(List<Integer> result) {
			DeckFragmentHolder.myPager.setOffscreenPageLimit(3);
			DeckFragmentHolder.myPager.setAdapter(DeckFragmentHolder.adapter);
			DeckFragmentHolder.myPager.setCurrentItem(1);

			DeckFragmentHolder.aBar.setTitle(((Activity) context).getIntent()
					.getStringExtra("name"));
			switch (result.get(position)) {
			case 0:
				DeckFragmentHolder.aBar.setIcon(R.drawable.druid);
				break;
			case 1:
				DeckFragmentHolder.aBar.setIcon(R.drawable.hunter);
				break;
			case 2:
				DeckFragmentHolder.aBar.setIcon(R.drawable.mage);
				break;
			case 3:
				DeckFragmentHolder.aBar.setIcon(R.drawable.paladin);
				break;
			case 4:
				DeckFragmentHolder.aBar.setIcon(R.drawable.priest);
				break;
			case 5:
				DeckFragmentHolder.aBar.setIcon(R.drawable.rogue);
				break;
			case 6:
				DeckFragmentHolder.aBar.setIcon(R.drawable.shaman);
				break;
			case 7:
				DeckFragmentHolder.aBar.setIcon(R.drawable.warlock);
				break;
			case 8:
				DeckFragmentHolder.aBar.setIcon(R.drawable.warrior);
				break;
			}
			dialog.dismiss();
		}

	}

	public class GetDeckActivityDeck extends AsyncTask<Void, Void, List<Cards>> {
		private Context context;
		private String deckName;
		private int position;

		// gotta love context
		public GetDeckActivityDeck(Context cxt, String deckName, int position) {
			context = cxt;
			this.deckName = deckName;
			this.position = position;
		}

		@Override
		protected List<Cards> doInBackground(Void... params) {
			InputStream instream = null;
			List<Cards> list = new ArrayList<Cards>();
			try {
				instream = context.openFileInput(deckName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			try {
				if (instream != null) {
					ObjectInputStream objStream = new ObjectInputStream(
							instream);
					try {
						list = (List<Cards>) objStream.readObject();
						if (instream != null) {
							instream.close();
						}
						if (objStream != null) {
							objStream.close();
						}

					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					list = new ArrayList<Cards>();
				}
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<Cards> result) {
			int sp = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_SP, 10, context.getResources()
							.getDisplayMetrics());
			int bigSp = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_SP, 14, context.getResources()
							.getDisplayMetrics());
			
			int screenSize = context.getResources().getConfiguration().screenLayout
					& Configuration.SCREENLAYOUT_SIZE_MASK;
			if (result == null) {
				result = getDeck(deckName);
			}
			DeckActivity.cardList = result;
			if (result.size() == 0
					&& screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				DeckActivity.tvNumCards.setTextSize(bigSp);
				DeckActivity.tvNumCards
						.setText("Looks like there's nothing here. Swipe right to get started!");
				DeckActivity.ivSwipe.setVisibility(View.VISIBLE);
			} else if (result.size() == 0
					&& screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				DeckActivity.tvNumCards.setTextSize(bigSp);
				DeckActivity.tvNumCards
						.setText("Looks like there's nothing here. Add cards from the left to get started!");
				DeckActivity.ivSwipe.setVisibility(View.VISIBLE);
			} else {
				DeckActivity.tvNumCards.setTextSize(sp);
				DeckActivity.tvNumCards.setText("" + result.size() + " / 30");
				DeckActivity.ivSwipe.setVisibility(View.GONE);
			}
			DeckActivity.adapter2 = new ImageAdapter(context, result);
			DeckActivity.gvDeck.setAdapter(DeckActivity.adapter2);
			DeckActivity.adapter = new DeckListAdapter(context, position,
					result);
			DeckActivity.lvDeck.setAdapter(DeckActivity.adapter);
		}

		private List<Cards> getDeck(String deckName) {
			InputStream instream = null;
			List<Cards> list = null;
			try {
				instream = context.openFileInput(deckName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			try {
				if (instream != null) {
					ObjectInputStream objStream = new ObjectInputStream(
							instream);
					try {
						list = (List<Cards>) objStream.readObject();
						if (instream != null) {
							instream.close();
						}
						if (objStream != null) {
							objStream.close();
						}

					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					list = new ArrayList<Cards>();
				}
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return list;
		}

	}

	public class LoadActivity extends AsyncTask<Void, Void, Void> {

		private int arg2;
		private List<String> listDecks;
		private Context cxt;
		private ProgressDialog dialog;

		public LoadActivity(int arg2, List<String> listDeck, Context cxt) {
			this.arg2 = arg2;
			this.listDecks = listDeck;
			this.cxt = cxt;
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(cxt, "",
					"Loading deck " + listDecks.get(arg2) + "...", true);
			dialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Intent intent = new Intent(cxt, DeckFragmentHolder.class);
			intent.putExtra("position", arg2);
			intent.putExtra("name", listDecks.get(arg2));
			cxt.startActivity(intent);
			dialog.dismiss();
		}
	}
}
