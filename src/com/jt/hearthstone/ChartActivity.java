package com.jt.hearthstone;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract.DeletedContacts;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ChartActivity extends Fragment {
    private GraphicalView mChart;

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    private XYSeries mCurrentSeries = new XYSeries("Cards");

    private XYSeriesRenderer mCurrentRenderer;
    
    private List<Cards> cardList = DeckActivity.cardList;
    private RelativeLayout layout;
    int position;
    String name;


    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View V = inflater.inflate(R.layout.activity_chart, container, false);
		position = getActivity().getIntent().getIntExtra("position", 0);
		name = getActivity().getIntent().getStringExtra("name");
        layout = (RelativeLayout) V.findViewById(R.id.chartLayout);

        
        return V;
    }
	
    public void onResume() {
        super.onResume();
        if (mChart == null) {
            initChart();
            addSampleData();
            mChart = ChartFactory.getBarChartView(getActivity(), mDataset, mRenderer, BarChart.Type.DEFAULT);
            layout.addView(mChart);
        } else {
        	((ViewGroup) mChart.getParent()).removeView(mChart);
        	mCurrentSeries.clear();
            addSampleData();
        	layout.addView(mChart);
        }
    }
    
	private void addSampleData() {
		int[] costs = new int[50];
		cardList = DeckActivity.cardList;
	 	for (Cards card : cardList) {
	 		if (card.getCost() != null) {
				costs[card.getCost().intValue()]++;
				Log.i("cost", "" + costs[card.getCost().intValue()]);
				mCurrentSeries.add(card.getCost().intValue(), costs[card.getCost().intValue()]);
	 		}
	 	}
	}

	private void initChart() {
	    mDataset.addSeries(mCurrentSeries);
	    mCurrentRenderer = new XYSeriesRenderer();
	    mRenderer.addSeriesRenderer(mCurrentRenderer);
	    mRenderer.setBarSpacing(1.0d);
	    mRenderer.setBarWidth(10.0f);
	    mRenderer.setPanEnabled(false, false);
	    mRenderer.setXAxisMin(0.0f);
	    mRenderer.setXAxisMax(12.0f);
	    mRenderer.setYAxisMin(0.0f);
	    mRenderer.setYAxisMax(10.0f);
	    mRenderer.setLegendTextSize(0.0f);
	    mRenderer.setShowGridX(true);
	    mRenderer.setBackgroundColor(Color.DKGRAY);
	    mRenderer.setApplyBackgroundColor(true);
	    mRenderer.setLabelsTextSize(14.0f);
	    mRenderer.setYLabelsPadding(8.0f);
	    mRenderer.setXLabels(15);
	    mRenderer.setYLabels(10);
	    mRenderer.setXTitle("Mana Cost");
	    mRenderer.setYTitle("Num. in Deck");
	    mRenderer.setLegendHeight(0);
	    mCurrentRenderer.setColor(Color.CYAN);
	}

	private List<Cards> getDeck(String deckName) {
		InputStream instream = null;
		List<Cards> list = null;
		try {
			instream = getActivity().openFileInput(deckName);
		} catch (FileNotFoundException e) {
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
