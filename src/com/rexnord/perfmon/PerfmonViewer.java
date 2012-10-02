package com.rexnord.perfmon;

//import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;


/**
 * @author RISP104
 * 
 */
public class PerfmonViewer {
	private static final String APPLICATION_NAME = "Perfmon Viewer";

	JFrame jframe;
	JFileChooser fc;
	JMenuItem refreshItem;
	JMenuItem openItem;
	JMenuItem closeItem;
	JMenuItem exitItem;
	
	JMenuItem viewFilter;
	JMenuItem viewShowAll;
	
	InvisibleNode ProfilerDataFiles;
	JTree dtree;
	InvisibleTreeModel treeModel;
	InvisibleTreeModel treeModelUnFiltered;
	InvisibleTreeModel treeModelFiltered;
	String treeFilterText;
	boolean viewFilterActive = false;
	JMenuBar bar;
	JTextArea outputValue;
	JScrollPane outputScroll;
	File LastDirectory;

	GraphingPanel graphPanel;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PerfmonViewer app = new PerfmonViewer();
		app.StartWork(args);

	}

	/**
	 * @param args
	 */
	public void StartWork(String[] args) {

		try { // Look like a Windows App
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// ignore the attempt
		}

		// Set up the application frame
		jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setTitle(APPLICATION_NAME);
		jframe.setMinimumSize(new Dimension(1200, 600));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));

		CreateMenu();

		ProfilerDataFiles = new InvisibleNode("Profiler Data Files");
		treeModelUnFiltered = new InvisibleTreeModel(ProfilerDataFiles);
		treeModelFiltered = new InvisibleTreeModel(new InvisibleNode("Filtered Data Files"));
		
		
		if (this.viewFilterActive == true) {
			treeModel = treeModelFiltered ;
		} else {
			treeModel = treeModelUnFiltered;
		}

		dtree = new JTree(treeModel);
		dtree.setExpandsSelectedPaths(true);
		dtree.addTreeSelectionListener(new SelectTreeNodeAction());

		JScrollPane dtreescroll = new JScrollPane();
		dtreescroll.setMinimumSize(new Dimension(100, 66));
		dtreescroll.setPreferredSize(new Dimension(300, 66));
		dtreescroll.getViewport().add(dtree);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				dtreescroll, panel);
		graphPanel = new GraphingPanel();

		JPanel outputpanel = new JPanel();
		outputpanel.setLayout(new BoxLayout(outputpanel, BoxLayout.Y_AXIS));
		outputpanel.setBorder(BorderFactory
				.createTitledBorder("Results and Other Ouput"));

		outputValue = new JTextArea();
		outputScroll = new JScrollPane(outputValue);
		outputScroll.setMinimumSize(new Dimension(200, 60));
		outputScroll.setPreferredSize(new Dimension(600, 60));
		outputScroll.setBorder(new LineBorder(Color.BLACK, 2));
		outputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		outputpanel.add(outputScroll);

		//
		JSplitPane Hsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				graphPanel, outputpanel);
		panel.add(Hsplit);

		jframe.add(split);
		jframe.pack();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Show
		jframe.setVisible(true);

	}

	private void CreateMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');

		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');

		/* Close File */
		closeItem = new JMenuItem("Close File");
		closeItem.setMnemonic('C');
		closeItem.addActionListener(new FileCloseAction());
		fileMenu.add(closeItem);

		/* Open File */
		openItem = new JMenuItem("Open");
		openItem.setMnemonic('O');
		openItem.addActionListener(new FileOpenAction());
		fileMenu.add(openItem);

		/* Exit Application */
		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic('x');
		exitItem.addActionListener(new ApplicationExit());
		fileMenu.add(exitItem);

		/* Filter */
		viewFilter= new JMenuItem("Filter");
		viewFilter.addActionListener(new FilterAction());
		viewFilter.setEnabled(!viewFilterActive);//Enable filter when not active
		
		/* Show All */
		viewShowAll= new JMenuItem("Show All");
		viewShowAll.addActionListener(new FilterShowAll());
		viewShowAll.setEnabled(viewFilterActive); //Enable Show all when filter True
		viewMenu.add(viewFilter);
		viewMenu.add(viewShowAll);
		
		/* Add menu to the bar*/
		bar = new JMenuBar();
		bar.add(fileMenu);
		bar.add(viewMenu);
		jframe.setJMenuBar(bar);

	}

	private class FilterAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Filter Tree Action Listener");
			
			String str = (String) JOptionPane.showInputDialog(null, "Search String(regx):",APPLICATION_NAME, JOptionPane.PLAIN_MESSAGE,null, null, treeModel.getFilterString());

			if(str != null){
				treeModel.setFilterString(str);
			}

			InvisibleNode.setVisbilityOnNodes(ProfilerDataFiles, ".*"+str+".*"); 
			treeModel.activateFilter(true);
			treeModel.reload();
			
			viewFilter.setEnabled(!treeModel.filterIsActive);
			viewShowAll.setEnabled(treeModel.filterIsActive);
		}
	}
	
	
	
	/**
	 * ActionListener for setting screen back to an unfiltered Tree.
	 * 
	 */
	private class FilterShowAll implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			System.out.println("UnFilter Tree Action Listener");
			treeModel.activateFilter(false);
			treeModel.reload();
			
			viewFilter.setEnabled(!treeModel.filterIsActive);
			viewShowAll.setEnabled(treeModel.filterIsActive);
		}
	}
	
	
	/**
	 * Exit Application
	 * 
	 */
	private class ApplicationExit implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	}

	/**
	 * ActionListener 
	 * 
	 */
	private class SelectTreeNodeAction implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			InvisibleNode node = (InvisibleNode) e.getPath()
					.getLastPathComponent();

			System.out.println("You selected " + node + "\n");

			if (node.getUserObject() instanceof ColumnData) {

				ColumnData a = (ColumnData) node.getUserObject();
				graphPanel.setData(a);
				outputValue.append("You selected " + node + "\n");
				outputValue.append(graphPanel.PrintCurrentDisplay());

				closeItem.setEnabled(false);
			} else {
				closeItem.setEnabled(true);
			}
		}
	}

	
	private class FileCloseAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			InvisibleNode node = (InvisibleNode) dtree
					.getSelectionPath().getLastPathComponent();

			// outputValue.append("You selected " + node + "\n");
			System.out.println("You selected " + node + "to be removed\n");
			treeModelUnFiltered.removeNodeFromParent(node);
		}
	}

	/**
	 * @author RISP104
	 * 
	 */
	private class FileOpenAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			fc = new JFileChooser();

			if (LastDirectory != null && LastDirectory.exists()) {
				fc.setCurrentDirectory(LastDirectory);
			}

			fc.setMultiSelectionEnabled(true);

			int returnVal = fc.showOpenDialog(jframe);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File[] FileList = fc.getSelectedFiles();

				LastDirectory = fc.getCurrentDirectory();
				for (File F : FileList) {

					try {
						addFiletoTree(F);
					} catch (FileNotFoundException e1) {
						outputValue.append(e1.getLocalizedMessage());
						e1.printStackTrace();
					} catch (ParseException e1) {
						outputValue.append(e1.getLocalizedMessage());
						e1.printStackTrace();
					}
				}
			} else {
				// System.out.println("Open command canceled by user.");
			}
		}
	}

	/**
	 * 
	 * @param file
	 *            object to parse and load as node.
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public void addFiletoTree(File f) throws FileNotFoundException,
			ParseException {
		// Parse the Data
		MSPerfmonDataFile a = new MSPerfmonDataFile(f);

		// Build Sub node structure from the Parsed Data File
		InvisibleNode newfile = a.getBuildTree();

		// Put the Parsed data object in the Tree model.
		newfile.setUserObject(a);

		// Insert the Node at the end of the list
		treeModelUnFiltered.insertNodeInto(newfile, ProfilerDataFiles,
				ProfilerDataFiles.getChildCount());

		// Set the new Node as selected
		dtree.setSelectionPath(new TreePath(newfile.getPath()));
		dtree.expandPath(dtree.getSelectionPath());
	}



}
