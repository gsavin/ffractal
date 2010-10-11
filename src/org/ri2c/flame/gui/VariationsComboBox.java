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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.ri2c.flame.Variation;

public class VariationsComboBox
	extends JComboBox
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1745822696107942875L;

	DefaultComboBoxModel model;
	
	public VariationsComboBox()
	{
		model = new DefaultComboBoxModel();
		
		for( Variation v : Variation.getVariations() )
			model.addElement( new VariationItem(v.getClass()) );
		
		setModel( model );
		setRenderer( new VariationRenderer() );
	}
	
	public Class<? extends Variation> getSelectedVariation()
	{
		return ( (VariationItem) this.getSelectedItem() ).variation;
	}
}
