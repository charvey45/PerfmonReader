package com.rexnord.perfmon;

/**
 * @author RISP104
 *
 */
public class ColumnData {
	
	private double[] data;
	private double[] time;
	private int length=0;
	private int counter=-1;
	private String NodeText;
	
	 
	double maxdata;
	double mindata;
	double maxtime;
	double mintime;
	

	/** COnstructor is built with the number of data elements it will hold.
	 * 
	 * @param size
	 */
	public ColumnData(String cname,int size) {
		this.NodeText = cname;
		this.data= new double[size];
		this.time = new double[size];
		this.length = size-1;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString (){
		return this.NodeText;
	}

	/**
	 * @param data the data to set
	 */
	public void setDataTimeCollection(double[] Settime,double[] Setdata) {
		this.data = Setdata;
		this.time = Settime;
		
		
		for (double u: Settime){
			if (u  > maxtime) {maxtime = u;}
			if (u  < mintime) {mintime = u;}
		}

		for (double g: Setdata){
			if (g  > maxdata) {maxdata = g;}
			if (g  < mindata) {mindata = g;}
		}
		
		
		counter = Setdata.length -1;
		
		
	}

	public void setDataTimePoint(double Settime,double Setdata) {
		counter++;
		
		this.data[counter] = Setdata;
		this.time[counter] = Settime;
		
		if (Settime  > maxtime) {maxtime = Settime;}
		if (Settime  < mintime) {mintime = Settime;}
		if (Setdata  > maxdata) {maxdata = Setdata;}
		if (Setdata  < mindata) {mindata = Setdata;}
	}

	/**
	 * @return the data
	 */
	public double[] getData() {
		return data;
	}
	public double getData(int a) {
		return data[a];
	}
	public double[] getTime() {
		return time;
	}
	public double getTime(int a) {
		return time[a];
	}
	
	
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}


	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}


	/**
	 * @return the nodeText
	 */
	public String getNodeText() {
		return NodeText;
	}


	/**
	 * @param nodeText the nodeText to set
	 */
	public void setNodeText(String nodeText) {
		NodeText = nodeText;
	}


	/**
	 * @return the maxdata
	 */
	public double getMaxdata() {
		return maxdata;
	}


	/**
	 * @param maxdata the maxdata to set
	 */
	public void setMaxdata(double maxdata) {
		this.maxdata = maxdata;
	}


	/**
	 * @return the mindata
	 */
	public double getMindata() {
		return mindata;
	}


	/**
	 * @param mindata the mindata to set
	 */
	public void setMindata(double mindata) {
		this.mindata = mindata;
	}


	/**
	 * @return the maxtime
	 */
	public double getMaxtime() {
		return maxtime;
	}


	/**
	 * @param maxtime the maxtime to set
	 */
	public void setMaxtime(double maxtime) {
		this.maxtime = maxtime;
	}


	/**
	 * @return the mintime
	 */
	public double getMintime() {
		return mintime;
	}


	/**
	 * @param mintime the mintime to set
	 */
	public void setMintime(double mintime) {
		this.mintime = mintime;
	}


	/**
	 * @param data the data to set
	 */
	public void setData(double[] data) {
		this.data = data;
	}


	/**
	 * @param time the time to set
	 */
	public void setTime(double[] time) {
		this.time = time;
	}
}
