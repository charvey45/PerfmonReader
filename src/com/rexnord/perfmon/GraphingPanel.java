package com.rexnord.perfmon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;



import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ZoomableChart;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterDate;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.views.ChartPanel;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

public class GraphingPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private ZoomableChart chart;
	private ColumnData Data;
	ITrace2D trace ;
	ITrace2D traceAVG;
	ITrace2D traceSTDEVUpper;
	ITrace2D traceSTDEVLower;
	ITrace2D traceMAX;
	ITrace2D traceRollingAvg;
	private JLabel CurrentMetric;
	private Collection<ITrace2D> traces;
	private JButton ZoomAllButton;
	private JButton ClipboardButton;
	
	/**
	 * 
	 */
	public GraphingPanel(){
		super();
		traces = new LinkedList<ITrace2D>();
		this.createLayout();
	}
	
	/**
	 * 
	 * @author RISP104
	 */
	private class ZoomAll implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			chart.zoomAll();
		}
	}
	
	/**
	 * Create Layout
	 */
	private void createLayout(){
		this.setLayout(new BoxLayout(	this,BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Graph"));

		JPanel subPanelZoom = new JPanel();
		subPanelZoom.setLayout(new BoxLayout(	subPanelZoom,BoxLayout.X_AXIS));
		
		CurrentMetric = new JLabel();		
		subPanelZoom.add(CurrentMetric);
		
		ClipboardButton = new JButton("Copy to Clipboard");
		ClipboardButton.addActionListener(new ClipboardImage(chart) );
		ClipboardButton.setEnabled(true);
		ClipboardButton.setFocusable(false);
		ClipboardButton.setToolTipText("Zoom Extents");
		
		
		ZoomAllButton = new JButton("Zoom Extents");
		ZoomAllButton.addActionListener(new ZoomAll());
		ZoomAllButton.setEnabled(true);
		ZoomAllButton.setFocusable(false);
		ZoomAllButton.setToolTipText("Zoom Extents");
		
		subPanelZoom.add(ZoomAllButton);
		subPanelZoom.add(ClipboardButton);
		
		
		// Main Chart object
		chart = new ZoomableChart();
		chart.setUseAntialiasing(true);
	    chart.setMinimumSize(new Dimension(	200,100));
	    chart.setPreferredSize(new Dimension(	600,400));
	    
	    ChartPanel chartPanel = new ChartPanel(chart);
	    //Add the items to the panel
		this.add(subPanelZoom);
		this.add(chartPanel);
	}

	/**
	 * @param NewData
	 */
	public void setData(ColumnData NewData) {
		System.out.println("new data being set.");
		
		RemoveAllTraces();
		traces.clear();//Empty the Collection
		
		this.Data = NewData;
		CurrentMetric.setText(this.Data.getNodeText());
		
		UpdateTraces();
		
	}

	/*
	 * Remove all traces from the gui chart object.
	 * */
	private void RemoveAllTraces() {
	    for (ITrace2D x : traces){
			this.chart.removeTrace(x);
	    }
	}
	

	public String PrintCurrentDisplay(){
		StringBuffer buffer = new StringBuffer();
				for (ITrace2D t : chart.getTraces()){
					buffer.append("Trace Range:" + t.getLabel() + "\t x:(" + new Date((long) t.getMinX()) +"-"+ new Date((long)t.getMaxX())+") y:(" + t.getMinY() +"-"+ t.getMaxY()+")");
					buffer.append("\n");
				}
				return buffer.toString();
	}
	
	
	public void UpdateTraces() {
		RemoveAllTraces();
		traces.clear();
		//Define Traces
		trace = new Trace2DSimple("Data"); 
		traceAVG= new Trace2DSimple("Average");
		traceSTDEVUpper= new Trace2DSimple("High Standard Extent");
		traceSTDEVLower= new Trace2DSimple("Low Standard Extent");
		traceMAX= new Trace2DSimple("Maximum");
		traceRollingAvg= new Trace2DSimple("Moving Average");
		
		//
		double sum = 0;
		double max = 0;
		
		MovingAverage ma = new MovingAverage((int) (Data.getLength()*.01));
		
		
		//Create temporary Data Storage for calculations
		double[][] VisibleData = new double[2][Data.getLength()];
		int a = 0;
		for(int i=0; i < (Data.getLength())  ;i++){
			VisibleData[0][a] = (long) Data.getTime(i);
			VisibleData[1][a] = Data.getData(i);
			a++;
			
			sum += Data.getData(i);
			if (Data.getData(i) > max){
				max = Data.getData(i);
			}
	    }
		

		
		
		double Average= sum/Data.getLength();
		StandardDeviation stdevCalculator = new StandardDeviation();
		double StDev = stdevCalculator.evaluate(VisibleData[1]);
		double RangeUpper = Average + (StDev *2);
		double RangeLower = Average - (StDev *2);
			if (RangeLower <0){
				RangeLower = 0;}
	
		
		// associate traces to the chart
		chart.addTrace(trace);
		chart.addTrace(traceAVG);
		chart.addTrace(traceSTDEVUpper);
		chart.addTrace(traceSTDEVLower);
		chart.addTrace(traceMAX);
		chart.addTrace(traceRollingAvg);
		
		
		IAxis axisX = chart.getAxisX();
        axisX.setPaintGrid(true);
        IAxis axisY = chart.getAxisY();
        axisY.setPaintGrid(true);
        
        
        axisX.setFormatter(new LabelFormatterDate(new SimpleDateFormat("MM-dd kk:mm:ss")));
        //axisX.setMinorTickSpacing(arg0)
		
		//Set Trace Colors
		trace.setColor(Color.GRAY);
		traceAVG.setColor(Color.ORANGE);
		traceSTDEVUpper.setColor(Color.GREEN);
		traceSTDEVLower.setColor(Color.BLUE);
		traceMAX.setColor(Color.RED);
		traceRollingAvg.setColor(Color.BLACK);
		
		//Load up the Traces with data
		for(int i=0; i < a  ;i++){
			//System.out.println("i: "+ (long)i + " X:"+VisibleData[0][i]+" Y:"+ VisibleData[1][i]);
			traceAVG.addPoint(VisibleData[0][i],Average);
			traceMAX.addPoint(VisibleData[0][i],max);
			traceSTDEVUpper.addPoint(VisibleData[0][i],RangeUpper);
			traceSTDEVLower.addPoint(VisibleData[0][i],RangeLower);
			trace.addPoint(VisibleData[0][i], VisibleData[1][i]);
			traceRollingAvg.addPoint(VisibleData[0][i],ma.getAvg(VisibleData[1][i]));
	    }

		// Add the trace to the chart: 
	    traces.add(traceAVG);
	    traces.add(traceMAX);
	    traces.add(traceSTDEVUpper);
	    traces.add(traceSTDEVLower);
	    traces.add(trace);
	    traces.add(traceRollingAvg);
		
	}
}

