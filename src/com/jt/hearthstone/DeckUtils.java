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
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DeckUtils {

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

	static void saveDeck(Context context, String deckName, Object object) {
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
	}

	static void renameDeck(final Context cxt, final int position,
			final FragmentActivity fragmentActivity, final List<Cards> cardList) {
		final List<String> listDecks = (List<String>) getStringList(cxt,
				"decklist");

		LayoutInflater inflater = (LayoutInflater) cxt
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.rename_dialog, null);

		final EditText nameBox = (EditText) layout
				.findViewById(R.id.etDeckName);

		AlertDialog.Builder builderz = new AlertDialog.Builder(cxt);
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

						((ActionBarActivity) cxt).getSupportActionBar()
								.setTitle(nameBox.getText().toString());

						listDecks.set(position, nameBox.getText().toString());
						saveDeck(cxt, listDecks.get(position), cardList);
						saveDeck(cxt, "decklist", listDecks);
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

}
