package com.rexnord.perfmon;

import info.monitorenter.gui.chart.Chart2D;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;





public class ClipboardImage implements ClipboardOwner, ActionListener {
	Chart2D Chart;
    
	public ClipboardImage(Chart2D C){
		Chart = C;
	}
	
	public ClipboardImage(BufferedImage img) {
        this.CopytoClipboard(img);
    }
	
	private void CopytoClipboard(BufferedImage img){
		TransferableImage trans = new TransferableImage( img );
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.setContents( trans, this );
	}
    

        
    public void lostOwnership( Clipboard clip, Transferable trans ) {
        System.out.println( "Lost Clipboard Ownership" );
    }
    
    private class TransferableImage implements Transferable {
        
        Image i;
        
        public TransferableImage( Image i ) {
            this.i = i;
        }
        
        public Object getTransferData( DataFlavor flavor ) 
        throws UnsupportedFlavorException, IOException {
            if ( flavor.equals( DataFlavor.imageFlavor ) && i != null ) {
                return i;
            }
            else {
                throw new UnsupportedFlavorException( flavor );
            }
        }
        
        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[ 1 ];
            flavors[ 0 ] = DataFlavor.imageFlavor;
            return flavors;
        }
        
        public boolean isDataFlavorSupported( DataFlavor flavor ) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for ( int i = 0; i < flavors.length; i++ ) {
                if ( flavor.equals( flavors[ i ] ) ) {
                    return true;
                }
            }
            
            return false;
        }
    }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		this.CopytoClipboard( this.Chart.snapShot());
		
	}
}
