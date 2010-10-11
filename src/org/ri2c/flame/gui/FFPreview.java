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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.ri2c.flame.ARGBTools;
import org.ri2c.flame.FFunction;

public class FFPreview
	extends ImageIcon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7103628867908891856L;
	BufferedImage thumb;
	int sqrtPoints;
	
	public FFPreview( FFunction ff, int width, int height )
	{
		this( ff, width, height, 10 );
	}
	
	public FFPreview( FFunction ff, int width, int height, int points )
	{
		thumb = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		sqrtPoints = points;
		
		preview(ff);
	}
	
	public void preview( FFunction ff )
	{
		double [][][] points = new double [sqrtPoints][sqrtPoints][2];
		double [] xy = new double [3];
		double xmin, xmax;
		double ymin, ymax;
		
		xmin = ymin = Double.MAX_VALUE;
		xmax = ymax = Double.MIN_VALUE;
		
		for( int i = 0; i < sqrtPoints; i++ )
		for( int j = 0; j < sqrtPoints; j++ )
		{
			xy [0] = 4 * i / (double) ( sqrtPoints - 1 ) - 2;
			xy [1] = 4 * j / (double) ( sqrtPoints - 1 ) - 2;
			
			ff.compute(xy);
			
			points [i][j][0] = xy [0];
			points [i][j][1] = xy [1];
			
			xmin = Math.min( xmin, xy [0] );
			xmax = Math.max( xmax, xy [0] );
			ymin = Math.min( ymin, xy [1] );
			ymax = Math.max( ymax, xy [1] );
		}
		
		for( int i = 0; i < sqrtPoints; i++ )
		for( int j = 0; j < sqrtPoints; j++ )
		{
			points [i][j][0] = ( thumb.getWidth() - 1 ) * ( points [i][j][0] - xmin ) / ( xmax - xmin );
			points [i][j][1] = ( thumb.getHeight() - 1 ) * ( points [i][j][1] - ymin ) / ( ymax - ymin );
		}

		for( int x = 0; x < thumb.getWidth(); x++ )
			for( int y = 0; y < thumb.getHeight(); y++ )
				thumb.setRGB(x, y, ARGBTools.getRGB(0,255,255,255));
		
		Graphics2D g2d = thumb.createGraphics();
		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g2d.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
		g2d.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );

		//g2d.setColor( Color.WHITE );
		//g2d.fillRect( 0, 0, thumb.getWidth(), thumb.getHeight() );
		g2d.setColor( Color.GRAY );
		
		g2d.drawLine( 0, thumb.getHeight() / 2, thumb.getWidth(), thumb.getHeight() / 2 );
		g2d.drawLine( thumb.getWidth() / 2, 0, thumb.getWidth() / 2, thumb.getHeight() );
		
		g2d.setColor( Color.getHSBColor( (float) ff.color(), 1, 1) );
		
		for( int i = 0; i < sqrtPoints - 1; i++ )
		for( int j = 0; j < sqrtPoints - 1; j++ )
		{
			g2d.drawLine( (int) points [i][j][0], (int) points [i][j][1], (int) points [i+1][j][0], (int) points [i+1][j][1] );
			g2d.drawLine( (int) points [i][j][0], (int) points [i][j][1], (int) points [i][j+1][0], (int) points [i][j+1][1] );
		}
		
		thumb.flush();
		setImage(thumb);
	}
}
