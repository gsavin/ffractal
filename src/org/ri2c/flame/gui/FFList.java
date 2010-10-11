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

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.ri2c.flame.FFunction;

public class FFList
	extends JList implements ListCellRenderer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 388253893560056437L;

	DefaultListModel model;
	
	public FFList()
	{
		model = new DefaultListModel();
		setModel(model);
		setCellRenderer(this);
		
		addKeyListener( new KeyAdapter()
		{
			public void keyPressed( KeyEvent ke )
			{
				switch( ke.getKeyCode() )
				{
				case KeyEvent.VK_DELETE:
					FFList.this.removeAction( getSelectedIndex() );
					break;
				case KeyEvent.VK_DOWN:
					FFList.this.downAction( getSelectedIndex() );
					break;
				case KeyEvent.VK_UP:
					FFList.this.upAction( getSelectedIndex() );
					break;
				}
			}
		});
	}
	
	public void addFFunction( FFunction ff )
	{
		model.addElement( new FFItem(ff) );
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
	{
		FFItem item = (FFItem) value;
		
		item.updateState(isSelected, cellHasFocus);
		
		return item;
	}
	
	public FFunction [] getFunctions()
	{
		FFunction [] ffunctions = new FFunction [model.getSize()];
		
		for( int i = 0; i < model.getSize(); i++ )
			ffunctions [i] = ( (FFItem) model.get(i) ).getFFunction();
		
		return ffunctions;
	}
	
	protected void removeAction( int index )
	{
		if( index >= 0 && index < model.size() )
			model.remove(index);
	}
	
	protected void downAction( int index )
	{
		if( index >= 0 && index < model.size() - 1 )
			model.add( index + 1, model.remove(index) );
	}
	
	protected void upAction( int index )
	{
		if( index > 0 && index < model.size() )
			model.add( index - 1, model.remove(index) );
	}
}
