package com.jt.hearthstone;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ChartActivity extends Fragment {
	static GraphicalView mChart;
	static GraphicalView mPieChart;
	static LinearLayout layout;
	static LinearLayout layout2;

	static XYSeries mCurrentSeries = new XYSeries("Cards");
	static DefaultRenderer mRenderer2 = new DefaultRenderer();
	static CategorySeries mSeries = new CategorySeries("Card Types");

	private List<Cards> cardList = DeckActivity.cardList;
	private XYSeriesRenderer mCurrentRenderer;
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View V = inflater.inflate(R.layout.activity_chart, container, false);
		layout = (LinearLayout) V.findViewById(R.id.barLayout);
		layout2 = (LinearLayout) V.findViewById(R.id.pieLayout);

		return V;
	}

	public void onResume() {
		super.onResume();

		if (mChart == null) {
			initChart();
			addSampleData();
			mChart = ChartFactory.getBarChartView(getActivity(), mDataset,
					mRenderer, BarChart.Type.DEFAULT);
			layout2.addView(mChart);
		} else {
			((ViewGroup) mChart.getParent()).removeView(mChart);
			mCurrentSeries.clear();
			addSampleData();
			layout2.addView(mChart);
		}

		if (mPieChart == null) {
			initPie();
			mPieChart = ChartFactory.getPieChartView(getActivity(), mSeries,
					mRenderer2);
			layout.addView(mPieChart);
		} else {
			((ViewGroup) mPieChart.getParent()).removeView(mPieChart);
			mSeries.clear();
			mRenderer2.removeAllRenderers();
			initPie();
			mPieChart = ChartFactory.getPieChartView(getActivity(), mSeries,
					mRenderer2);
			layout.addView(mPieChart);
		}
	}

	private void addSampleData() {
		int[] costs = new int[50];
		cardList = DeckActivity.cardList;
		for (Cards card : cardList) {
			if (card.getCost() != null) {
				costs[card.getCost().intValue()]++;
				Log.i("cost", "" + costs[card.getCost().intValue()]);
				mCurrentSeries.add(card.getCost().intValue(), costs[card
						.getCost().intValue()]);
			}
		}
	}

	private void initChart() {
		mDataset.addSeries(mCurrentSeries);
		mCurrentRenderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(mCurrentRenderer);
		mRenderer.setBarSpacing(1.0d);
		mRenderer.setBarWidth(15.0f);
		mRenderer.setPanEnabled(false, false);
		mRenderer.setXAxisMin(-0.5f);
		mRenderer.setXAxisMax(12.0f);
		mRenderer.setYAxisMin(0.0f);
		mRenderer.setYAxisMax(12.0f);
		mRenderer.setLegendTextSize(0.0f);
		mRenderer.setShowGridX(true);
		mRenderer.setBackgroundColor(Color.DKGRAY);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setLabelsTextSize(14.0f);
		mRenderer.setYLabelsPadding(8.0f);
		mRenderer.setXLabels(15);
		mRenderer.setYLabels(12);
		mRenderer.setXTitle("Mana Cost");
		mRenderer.setYTitle("Num. in Deck");
		mRenderer.setLegendHeight(0);
		mRenderer.setShowLegend(false);
		mRenderer.setInScroll(true);
		mCurrentRenderer.setGradientEnabled(true);
		mCurrentRenderer.setGradientStart(0, Color.rgb(252, 199, 25));
		mCurrentRenderer.setGradientStop(9, Color.rgb(255, 108, 2));
	}

	private void initPie() {
		int abilities = 0;
		int weapons = 0;
		int minions = 0;
		int series = 0;

		int[] colors = { Color.rgb(0, 171, 249), Color.rgb(245, 84, 0),
				Color.rgb(60, 242, 0) };
		int[] colors2 = { Color.rgb(0, 108, 229), Color.rgb(225, 23, 3),
				Color.rgb(8, 196, 0) };
		
		mRenderer2.setStartAngle(180);
		mRenderer2.setDisplayValues(true);
		mRenderer2.setPanEnabled(false);
		mRenderer2.setInScroll(true);
		mRenderer2.setLabelsTextSize(14.0f);
		mRenderer2.setLabelsColor(Color.WHITE);
		mRenderer2.setShowLegend(false);
		
		cardList = DeckActivity.cardList;

		for (Cards card : cardList) {
			if (card.getType() != null && card.getType().intValue() == 4) {
				minions++;
			} else if (card.getType() != null && card.getType().intValue() == 5) {
				abilities++;
			} else if (card.getType() != null && card.getType().intValue() == 7) {
				weapons++;
			}
		}
		
		if (abilities != 0) {
			mSeries.add("Spells", abilities);
			series++;
		}
		if (minions != 0) {
			mSeries.add("Minions", minions);
			series++;
		}
		if (weapons != 0) {
			mSeries.add("Weapons", weapons);
			series++;
		}

		for (int i = 0; i < series; i++) {
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setDisplayChartValues(true);
			seriesRenderer.setGradientEnabled(true);
			seriesRenderer.setGradientStart(0, colors[i]);
			seriesRenderer.setGradientStop(20, colors2[i]);
			mRenderer2.addSeriesRenderer(seriesRenderer);
		}
	}


}