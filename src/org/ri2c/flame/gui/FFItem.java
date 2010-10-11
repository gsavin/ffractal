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

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.ri2c.flame.FFunction;

public class FFItem
	extends JLabel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4491863544716166531L;

	FFunction ff;
	
	public FFItem( FFunction ff )
	{
		super( ff.getName(), new FFPreview(ff,100,100), JLabel.LEFT );
		
		this.ff = ff;
		
		setFont( VariationItem.VARIATION_ITEM_TITLE_FONT);
		setBackground(VariationItem.VARIATION_ITEM_BACKGROUND);
		setBorder( BorderFactory.createLineBorder( Color.gray ) );
	}
	
	public FFItem()
	{
		setFont( VariationItem.VARIATION_ITEM_TITLE_FONT);
		setBackground(VariationItem.VARIATION_ITEM_BACKGROUND);
		setBorder( BorderFactory.createLineBorder( Color.gray ) );
	}
	
	public void updateFunction( FFunction ff )
	{
		setText( ff.getName() );
		setIcon( new FFPreview(ff,100,100) );
		
		this.ff = ff;
	}
	
	public void updateState( boolean isSelected, boolean hasFocus )
	{
		setOpaque(isSelected);
	}
	
	public FFunction getFFunction()
	{
		return ff;
	}
}
