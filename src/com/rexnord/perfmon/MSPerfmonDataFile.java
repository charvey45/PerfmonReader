package com.rexnord.perfmon;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.*;
import java.util.*;

public class MSPerfmonDataFile {
	private List<String> Columnlist;
	private List<float[]> RowData;
	private int LineCounter;
	private File theFile;

	
	@Override
	public String toString() {
		return this.theFile.getName();
	}
	

	/**
	 * @param InputFile
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public MSPerfmonDataFile(File InputFile) throws FileNotFoundException, ParseException {
		Columnlist = new ArrayList<String>();
		RowData = new ArrayList<float[]>();
		LineCounter = 0;
		theFile = InputFile;
		this.loadfromdisk(InputFile);
		
	}
	
	private void loadfromdisk(File InputFile)throws FileNotFoundException,	ParseException {
		
		this.theFile = InputFile;
		FileInputStream fstream = new FileInputStream(this.theFile);

		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		// Read File Line By Line
		try {

			while ((strLine = br.readLine()) != null) {
				LineCounter++;

				if (LineCounter == 1) {
					setColumnNames(strLine);
				} else
				// if (LineCounter >2)
				{
					setDataValues(strLine);
				}
			}

			fstream.close();
			in.close();
			br.close();

		} catch (IOException e) {e.printStackTrace();}

	}
	
	/**
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public boolean reload() throws FileNotFoundException, ParseException{
		if (this.theFile != null){
			Columnlist = new ArrayList<String>();
			RowData = new ArrayList<float[]>();
			LineCounter = 0;
			this.loadfromdisk(this.theFile);
			return true;
		}
		return false;
		
	}
	
	

	/**
	 * @param strLine
	 * @throws ParseException
	 */
	private void setDataValues(String strLine) throws ParseException {
		strLine = strLine.replaceAll("\"", ""); // Remove " qualifiers if they exist
		String[] Columns = strLine.split(","); // String Storage
		float[] ColumnValues = new float[Columns.length]; // Float Storage

		// Convert Date to Long for simple math and storage
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		Date date = (Date) formatter.parse(Columns[0].replaceAll("\"", ""));
		Long tmp = date.getTime();
		ColumnValues[0] = tmp.longValue();
		
		// Store columns 2 to n
		for (int i = 1; i < (Columns.length); i++) {
			ColumnValues[i] = Float.parseFloat("0" + Columns[i].trim());
		}
		RowData.add(ColumnValues);
	}
	

	/**
	 * @param strLine
	 */
	private void setColumnNames(String strLine) {
		String[] Columns = strLine.split(",");

		for (int i = 0; i < Columns.length; i++) {
			Columnlist.add(Columns[i].replaceAll("\"", ""));
		}
	}

	/**
	 * @param ColumnName
	 * @return
	 */
	private int GetColumnIndex(String ColumnName) {
		for (int i = 0; i < Columnlist.size(); i++) {
			if (ColumnName.equals(Columnlist.get(i))){
				return i;
			}
		}
		return -1;
	}

	public InvisibleNode getBuildTree() {
		InvisibleNode NewBranch = new InvisibleNode(this.theFile);
		
		// Loop the Columns to create children, then add array of values to each
		// child's user object
		for (String cname : Columnlist) { 
			NewBranch.add(new MutableTreeWithColumnDataObject(cname));
		}
		return NewBranch;
	}

	/**
	 * 
	 * @author RISP104
	 *
	 */
	private class MutableTreeWithColumnDataObject extends InvisibleNode {
		private static final long serialVersionUID = 1L;
		ColumnData Data;

		/**
		 * 
		 * @param cname
		 */
		public MutableTreeWithColumnDataObject(String cname) {
			super(cname);
			this.setAllowsChildren(false);
			Data = new ColumnData(cname,RowData.size());
			int a = GetColumnIndex(cname);
			for (int i = 0; i < RowData.size(); i++) {
				Data.setDataTimePoint(RowData.get(i)[0], RowData.get(i)[a]);
				
			}
			this.setUserObject(Data);
		}

		@Override
		public String toString() {
			return Data.getNodeText();
		}
	}
	

}
