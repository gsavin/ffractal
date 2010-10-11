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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ri2c.flame.Variation;

class VariationItem
	extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 349998909731496911L;

	protected static Font VARIATION_ITEM_TITLE_FONT;//: = new Font( "Arial", Font.BOLD, 14 );
	protected static Font VARIATION_ITEM_DESCRIPTION_FONT;// = new Font( "Arial", Font.PLAIN, 12 );
	
	static
	{
		URL arialBold = ClassLoader.getSystemResource("org/ri2c/flame/gui/arialbd.ttf");
		
		if( arialBold != null )
		{
			try
			{
				VARIATION_ITEM_TITLE_FONT = Font.createFont( Font.TRUETYPE_FONT, arialBold.openStream() ).deriveFont( Font.BOLD, 14 );
			}
			catch( Exception e )
			{
				VARIATION_ITEM_TITLE_FONT = new Font("serif",Font.BOLD,14);
			}
		}
		else VARIATION_ITEM_TITLE_FONT = new Font("serif",Font.BOLD,14);
		
		URL arial = ClassLoader.getSystemResource("org/ri2c/flame/gui/arial.ttf");
		
		if( arial != null )
		{
			try
			{
				VARIATION_ITEM_DESCRIPTION_FONT = Font.createFont( Font.TRUETYPE_FONT, arial.openStream() ).deriveFont(Font.PLAIN, 12);
			}
			catch( Exception e )
			{
				VARIATION_ITEM_DESCRIPTION_FONT = new Font("serif",Font.PLAIN,12);
			}
		}
		else VARIATION_ITEM_DESCRIPTION_FONT = new Font("serif",Font.PLAIN,12);
	}
	
	protected static final Color VARIATION_ITEM_BACKGROUND = new Color( 65, 137, 192, 100 );
	
	Class<? extends Variation> variation;
	
	JLabel icon;
	JLabel title;
	JLabel description;
	
	ImageIcon gridICO, thumbICO;
	String name;
	String desc;
	
	public VariationItem( Class<? extends Variation> variation )
	{
		this.variation = variation;
		this.name = variation.getSimpleName();
		
		URL gridURL = ClassLoader.getSystemResource( 
				String.format( "org/ri2c/flame/pic/variation-%s-grid.png", name.toLowerCase() )
		);

		URL thumbURL = ClassLoader.getSystemResource( 
				String.format( "org/ri2c/flame/pic/variation-%s-thumb.png", name.toLowerCase() )
		);
		
		if( gridURL != null )
		{
			try {
				BufferedImage gridIMG = ImageIO.read(gridURL);
				gridICO = new ImageIcon( gridIMG.getScaledInstance( 100, 100, BufferedImage.SCALE_SMOOTH ) );
			}
			catch (IOException e)
			{
				System.err.printf("error while loading grid icon for %s%n",variation.getClass().getSimpleName());
			}
		}
		else System.err.printf("cant find grid icon for %s%n",variation.getClass().getSimpleName());
		
		if( thumbURL != null )
		{
			try {
				BufferedImage gridIMG = ImageIO.read(thumbURL);
				thumbICO = new ImageIcon( gridIMG.getScaledInstance( 100, 100, BufferedImage.SCALE_SMOOTH ) );
			}
			catch (IOException e)
			{
				System.err.printf("error while loading thumb icon for %s%n",variation.getClass().getSimpleName());
			}
		}
		else System.err.printf("cant find thumb icon for %s%n",variation.getClass().getSimpleName());
		
		int id = -1;
		
		try {
			id = (Integer) variation.getField("ID").get(null);
		} catch(Exception e) {
		}
		
		desc = String.format( "variation n°%d", id );
		
		build();
	}
	
	protected void build()
	{
		GridBagLayout bag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(bag);
		setBorder( BorderFactory.createLineBorder( Color.BLACK ) );

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 4;
		
		icon = new JLabel( gridICO );
		bag.setConstraints( icon, c );
		add(icon);
		
		Insets insets = new Insets( 0, 0, 0, 20 );
		c.insets = insets;
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.gridheight = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		
		JPanel sep = new JPanel();
		sep.setOpaque(false);
		bag.setConstraints( sep, c );
		add(sep);
		
		title = new JLabel( name, JLabel.LEFT );
		title.setFont(VARIATION_ITEM_TITLE_FONT);
		
		c.weighty = 0;
		c.gridy++;
		
		bag.setConstraints( title, c );
		add(title);
		
		description = new JLabel( desc, JLabel.LEFT );
		description.setFont(VARIATION_ITEM_DESCRIPTION_FONT);
		c.weighty = 0;
		c.gridy++;
		
		bag.setConstraints( description, c );
		add(description);

		c.weighty = 1;
		c.gridy++;
		
		sep = new JPanel();
		sep.setOpaque(false);
		bag.setConstraints( sep, c );
		add(sep);
	}
	
	public void updateState( boolean isSelected, boolean hasFocus )
	{
		icon.setIcon( isSelected ? thumbICO : gridICO );
		
		setBackground(VARIATION_ITEM_BACKGROUND);
		setOpaque(isSelected);
	}
}