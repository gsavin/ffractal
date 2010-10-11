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
package org.ri2c.flame;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.ri2c.flame.gui.output.ImageDisplayer;

public class RenderingContext
{
	public static enum OutputType
	{
		component,
		image
	}
	
	protected interface Output
	{
		void output();
	}
	
	protected class ImageOutput
		implements Output
	{
		BufferedImage image;
		String filename;
		
		public ImageOutput( String filename, BufferedImage image )
		{
			this.image = image;
			this.filename = filename;
		}
		
		public void output()
		{
			try
			{
				ImageIO.write( image, filename.substring( filename.lastIndexOf('.') + 1 ).toUpperCase(), new File( filename ) );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			
			ImageDisplayer id = new ImageDisplayer(image);
			id.setVisible(true);
		}
	}
	
	Context 					ctx;
	Renderer 		renderer;
	LinkedList<AffineTransform>	transformStack;
	Discretiser					discretiser;
	ColorProvider	colorProvider;
	Graphics2D 					g2d;
	Output						output;
	
	DoubleBuffer				points;
	
	protected RenderingContext( Context ctx )
	{
		this.ctx = ctx;
		
		transformStack 		= new LinkedList<AffineTransform>();
		colorProvider = new ColorProvider.RandomColorProvider();
		renderer = new Renderer.Dot();
	}
	
	public RenderingContext( Context ctx, String filename, Discretiser discretiser )
	{
		this(ctx);
		
		BufferedImage image = new BufferedImage( discretiser.width(), discretiser.height(), BufferedImage.TYPE_INT_ARGB );
		
		this.output = new ImageOutput( filename, image );
		this.g2d = image.createGraphics();
		this.discretiser = discretiser;
		
		setupGraphics();
	}
	
	public RenderingContext( Context ctx, Graphics2D g2d,  Discretiser discretiser )
	{
		this(ctx);
		
		this.g2d = g2d;
		this.discretiser = discretiser;
		
		setupGraphics();
	}
	
	public void setupGraphics()
	{
		if( g2d == null )
			return;
		
		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g2d.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
		g2d.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
		
		g2d.setColor( java.awt.Color.BLACK );
		g2d.fillRect(0,0,discretiser.width(),discretiser.height());
	}
	
	protected void pushTransform()
	{
		transformStack.addFirst( g2d.getTransform() );
	}
	
	public void popTransform()
	{
		g2d.setTransform( transformStack.poll() );
	}
	
	public void rotate( double angle )
	{
		rotate( angle, discretiser.width() / 2.0, discretiser.height() / 2.0 );
	}
	
	public void rotate( double angle, double x, double y )
	{
		pushTransform();
		g2d.rotate(angle, x, y);
	}
	
	public void translate( double tx, double ty )
	{
		pushTransform();
		g2d.translate(tx, ty);
	}
	
	public void setColorProvider( ColorProvider cp )
	{
		if( cp != null )
			colorProvider = cp;
	}
	
	public void setRenderer( Renderer r )
	{
		if( r != null )
			renderer = r;
	}
	
	public void generatePoints( int size )
	{
		points = ByteBuffer.allocateDirect( Double.SIZE * size * 4 ).asDoubleBuffer();
		int fmax;
		//String serial;
		double [] xycf = ctx.getInitialPoint();
		//Random random = new Random();
		
		fmax = 1;

		for( int i = 0; i < 100; i++ )
			ctx.compute(xycf);
		
		//Map<String,Integer> frequencies = new HashMap<String,Integer>();
		Discretiser fd = new Discretiser( discretiser.xmin, discretiser.ymin, discretiser.xmax, discretiser.ymax, discretiser.width(), discretiser.height() );
		IntBuffer frequencies = ByteBuffer.allocateDirect( Integer.SIZE * fd.width() * fd.height() ).asIntBuffer();
		
		while( frequencies.position() < frequencies.capacity() )
			frequencies.put(0);
		
		for( int i = 0; i < size; i++ )
		{
			//ctx.getFFunction( random.nextInt(ctx.getFFuntionCount()) ).compute( xycf );
			ctx.compute( xycf );
			
			points.put( xycf [0] );
			points.put( xycf [1] );
			points.put( xycf [2] );
			points.put( 0 );
			//serial = String.format("(%f;%f)", xycf [0], xycf [1]);

			int f = 1;
			
			if( fd.contains(xycf[0],xycf[1]) )
			{
				int index = fd.convertX(xycf[0]) * fd.height() + fd.convertY(xycf[1]);
				
				f = frequencies.get(index) + 1;
				frequencies.put(index,f);
				fmax = Math.max(fmax,f);
			}
			/*
			if( frequencies.containsKey(serial) )
			{
				 f = frequencies.get(serial)+1;
				frequencies.put(serial,f);
				fmax = Math.max(fmax,f);
			}
			else
			{
				frequencies.put(serial,1);
			}
			*/
			//points.put( Math.log(f) / Math.log(fmax) );
		}

		// Update frequencies of points
		
		for( int i = 0; i < size; i++ )
		{
			//serial = String.format("(%f;%f)", points.get(i*4), points.get(i*4+1));
			//points.put(i*4+3,Math.log(frequencies.get(serial))/Math.log(fmax));
			int index = fd.convertX(points.get(i*4)) * fd.height() + fd.convertY(points.get(i*4+1));
			
			if( index >= 0 && index < frequencies.capacity() )
				points.put( i*4+3, Math.log(frequencies.get(index)) / Math.log(fmax) );
		}
		
	}
	
	public void renderPoints()
	{
		renderer.rendering( points, 0, points.capacity()/4, g2d, discretiser, colorProvider );
	}
	
	public void directRendering( int ite )
	{
		double [] xycf = ctx.getInitialPoint();
		Random random = new Random();
		
		while( ite-- > 0 )
		{
			ctx.getFFunction( random.nextInt(ctx.getFFuntionCount()) ).compute( xycf );
			renderer.renderPoint( g2d, xycf[0], xycf[1], xycf[2], xycf[3], discretiser, colorProvider );
		}
	}
	
	public void output()
	{
		output.output();
	}
}
