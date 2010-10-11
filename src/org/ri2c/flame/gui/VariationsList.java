/*
 * This file is part of FFractal.
 * 
 * FFractal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FFractal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FFractal.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2010
 * 	Guilhelm Savin
 */
package org.ri2c.flame.gui;

import org.ri2c.flame.Variation;

import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class VariationsList
	extends JList
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2892644007337123770L;
	
	DefaultListModel model;
	
	public VariationsList()
	{
		model = new DefaultListModel();
		
		for( Variation v : Variation.getVariations() )
			model.addElement( new VariationItem(v.getClass()) );
		
		setModel( model );
		setCellRenderer( new VariationRenderer() );
	}
	
	public JScrollPane getScrollView( int count )
	{
		JScrollPane pane = new JScrollPane( this );
		
		int height = (int) ((VariationItem)model.getElementAt(0)).getPreferredSize().getHeight();
		
		pane.setPreferredSize( new Dimension( (int) pane.getPreferredSize().getWidth(), count * height ) );
		
		return pane;
	}
	
	public Class<? extends Variation> getSelectedVariation()
	{
		return ( (VariationItem) this.getSelectedValue() ).variation;
	}
	
	public static void main( String ... args )
	{
		JFrame frame = new JFrame("test");
		
		VariationsList vlist = new VariationsList();
		frame.add(vlist.getScrollView(3));
		
		frame.pack();
		
		frame.setVisible(true);
	}
}
