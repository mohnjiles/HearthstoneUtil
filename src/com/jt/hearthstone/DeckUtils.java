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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DeckUtils {

	static List<String> listDecks = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	static List<Cards> getCardsList(Context context, String deckName) {
		InputStream instream = null;
		List<Cards> list = null;
		try {
			instream = context.openFileInput(deckName);
		} catch (FileNotFoundException e) {
			list = new ArrayList<Cards>();
			e.printStackTrace();
		}

		try {
			if (instream != null) {
				ObjectInputStream objStream = new ObjectInputStream(instream);
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

	@SuppressWarnings("unchecked")
	static List<String> getStringList(Context context, String deckName) {
		InputStream instream = null;
		List<String> list = null;
		try {
			instream = context.openFileInput(deckName);
		} catch (FileNotFoundException e) {
			list = new ArrayList<String>();
			e.printStackTrace();
		}

		try {
			if (instream != null) {
				ObjectInputStream objStream = new ObjectInputStream(instream);
				try {
					list = (List<String>) objStream.readObject();
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

	@SuppressWarnings("unchecked")
	static List<Integer> getIntegerDeck(Context context, String deckName) {
		InputStream instream = null;
		List<Integer> list = null;
		try {
			instream = context.openFileInput(deckName);
		} catch (FileNotFoundException e) {
			list = new ArrayList<Integer>();
			e.printStackTrace();
		}

		try {
			if (instream != null) {
				ObjectInputStream objStream = new ObjectInputStream(instream);
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

	static void renameDeck(final Context cxt, final int position,
			final FragmentActivity fragmentActivity, final List<Cards> cardList) {
		new GetStringList(cxt, "renameDeck", position, fragmentActivity, cardList).execute("decklist");
	}

	static class SaveDeck extends AsyncTask<Void, Void, Void> {

		private Context context;
		private String deckName;
		private Object object;

		public SaveDeck(Context context, String deckName, Object object) {
			this.context = context;
			this.deckName = deckName;
			this.object = object;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
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
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}
	}

	static class GetCardsList extends AsyncTask<String, Void, List<Cards>> {

		private CustomCardFragment fragment;
		private Context context;
		private int tag;

		public GetCardsList(Context context, CustomCardFragment fragment, int tag) {
			this.context = context;
			this.fragment = fragment;
			this.tag = tag;
		}

		@Override
		protected List<Cards> doInBackground(String... params) {
			InputStream instream = null;
			List<Cards> list = null;
			try {
				instream = context.openFileInput(params[0]);
			} catch (FileNotFoundException e) {
				list = new ArrayList<Cards>();
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
			Log.w("GetCardsList", "GetCardsList started");
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(List<Cards> result) {
			Log.w("GetCardsList", "GetCardsList finished" + result.toString());
			fragment.setCardList(result, tag);
			super.onPostExecute(result);
		}
	}
	
	static class GetStringList extends AsyncTask<Object, Void, List<String>> {
		
		private Context context;
		private Object tag;
		private int position;
		private FragmentActivity fragmentActivity;
		private List<Cards> cardList;
		
		public GetStringList(Context context, Object tag, int position,
				FragmentActivity fragmentActivity, List<Cards> cardList) {
			this.context = context;
			this.tag = tag;
			this.position = position;
			this.fragmentActivity = fragmentActivity;
			this.cardList = cardList;
		}
		
		@Override
		protected List<String> doInBackground(Object... params) {
			InputStream instream = null;
			List<String> list = null;
			try {
				instream = context.openFileInput(params[0].toString());
			} catch (FileNotFoundException e) {
				list = new ArrayList<String>();
				e.printStackTrace();
			}

			try {
				if (instream != null) {
					ObjectInputStream objStream = new ObjectInputStream(instream);
					try {
						list = (List<String>) objStream.readObject();
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
		protected void onPostExecute(List<String> result) {
			DeckUtils.listDecks = result;
			
			if (tag.toString().equals("renameDeck")) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.rename_dialog, null);

				final EditText nameBox = (EditText) layout
						.findViewById(R.id.etDeckName);

				AlertDialog.Builder builderz = new AlertDialog.Builder(context);
				builderz.setView(layout);

				builderz.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								dialog.dismiss();

								for (String deckName : listDecks) {
									if (deckName.equals(nameBox.getText().toString())) {
										Crouton.makeText(fragmentActivity,
												"Deck with that name already exists",
												Style.ALERT).show();
										return;
									}
								}

								((ActionBarActivity) context).getSupportActionBar()
										.setTitle(nameBox.getText().toString());

								listDecks.set(position, nameBox.getText().toString());
								new SaveDeck(context, listDecks.get(position), cardList)
										.execute();
								new SaveDeck(context, "decklist", listDecks).execute();
							}
						});
				builderz.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				AlertDialog dialogz = builderz.create();
				dialogz.show();
			}
			
			super.onPostExecute(result);
		}
	}
}
