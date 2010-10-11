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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.nio.DoubleBuffer;
import javax.imageio.ImageIO;
import java.io.File;

public class Context
{
	BiunitSquare<Point> points;
	LinkedList<FFunction> flameFunctions;
	FFunction finalFlameFunction;
	Random random = new Random();
	double [] xyc;
	double [] initialPoint;
	
	public Context()
	{
		flameFunctions = new LinkedList<FFunction>();

		random = new Random();

		xyc = new double [3];
		xyc [0] = random.nextDouble() * 2 - 1;
		xyc [1] = random.nextDouble() * 2 - 1;
		xyc [2] = random.nextDouble();
		
		initialPoint = new double [] { random.nextDouble(), random.nextDouble() };
	}
/*
	public void init( int xsteps, int ysteps )
	{
		ColorProvider colorProvider = new ColorProvider.RandomColorProvider();

		points = new BiunitSquare<Point>(-2.0,2.0,-2.0,2.0,xsteps,ysteps);

		for( int i = 0; i < xsteps; i++ )
			for( int j = 0; j < ysteps; j++ )
			{
				Point p = new Point(points.realX(i),points.realY(j),0);//colorProvider.getColor(i,j));
				points.set(i,j,p);
			}

		System.err.printf("context init done%n");
	}
*/
	public int getFFuntionCount()
	{
		return flameFunctions.size();
	}
	
	public FFunction getFFunction( int index )
	{
		return flameFunctions.get(index);
	}
	
	public void setInitialPoint( double x, double y )
	{
		initialPoint [0] = x;
		initialPoint [1] = y;
	}
	
	public double [] getInitialPoint()
	{
		return Arrays.copyOf( initialPoint, 4 );
	}
	
	public void addFlameFunction( FFunction ff )
	{
		flameFunctions.add(ff);
	}

	public Point point( double x, double y )
	{
		return points.get(x,y);
	}
/*
	public BiunitSquare<Point> getPoints()
	{
		return points;
	}
*/
	public void compute( double [] xyc )
	{
		flameFunctions.get(random.nextInt(flameFunctions.size())).compute(xyc);
		
		if( finalFlameFunction != null )
			finalFlameFunction.compute(xyc);
	}
/*
	public void niterate( int ite, String filePath )
	{
		DoubleBuffer buffer = DoubleBuffer.allocate( ite * 4 );
		double xmin, xmax, ymin, ymax;
		int fmax;
		String serial;

		xmin = ymin = Double.MAX_VALUE;
		xmax = ymax = Double.MIN_VALUE;
		fmax = 1;

		Map<String,Integer> frequencies = new HashMap<String,Integer>();

		for( int i = 0; i < ite; i++ )
		{
			nextPoint();

			buffer.put( xyc [0] );
			buffer.put( xyc [1] );
			buffer.put( xyc [2] );
			buffer.put( 1 );

			xmin = Math.min( xmin, xyc [0] );
			xmax = Math.max( xmax, xyc [0] );
			ymin = Math.min( ymin, xyc [1] );
			ymax = Math.max( ymax, xyc [1] );

			serial = String.format("(%f;%f)", xyc [0], xyc [1]);

			if( frequencies.containsKey(serial) )
			{
				int f = frequencies.get(serial)+1;
				frequencies.put(serial,f);
				fmax = Math.max(fmax,f);
			}
			else
			{
				frequencies.put(serial,1);
			}
		}

		// Update frequencies of points
		for( int i = 0; i < ite; i++ )
		{
			serial = String.format("(%f;%f)", buffer.get(i*4), buffer.get(i*4+1));
			buffer.put(i*4+3,Math.log(frequencies.get(serial))/Math.log(fmax));
		}

		// Add padding
		xmax += 0.1 * ( xmax - xmin );
		xmin -= 0.1 * ( xmax - xmin );
		ymin -= 0.1 * ( ymax - ymin );
		ymax += 0.1 * ( ymax - ymin );

		int width, height;

		width = 1000;
		height = 1000;

		Discretiser d = new Discretiser(xmin,ymin,xmax,ymax,width,height);

		BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g2d = img.createGraphics();

		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g2d.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
		g2d.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
		g2d.setColor( java.awt.Color.BLACK );
		g2d.fillRect(0,0,1000,1000);
		//g2d.setStroke( new java.awt.BasicStroke(2.0f) );
		//g2d.setColor( new Color( 1.0f, 0.2f, 0.0f, 0.05f ) );

		ColorProvider colorProvider = new ColorProvider.Basic();
		Renderer renderer = new Renderer.QuadCurve();
		renderer.rendering(buffer,0,ite,g2d,d,colorProvider);
		g2d.rotate( Math.PI, width / 2.0, height / 2.0 );
		renderer.rendering(buffer,0,ite,g2d,d,colorProvider);
		/*g2d.rotate( Math.PI / 2, width / 2.0, height / 2.0 );
    renderer.rendering(buffer,0,ite,g2d,d,colorProvider);
    g2d.rotate( Math.PI / 2, width / 2.0, height / 2.0 );
    renderer.rendering(buffer,0,ite,g2d,d,colorProvider);*/
/*
		try
		{
			ImageIO.write(img,"PNG",new File(filePath));
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	HashMap<Integer,Integer> frequencies = new HashMap<Integer,Integer>();
	int frequencyMax = 0;
	
	public int incrementFrequency( double x, double y )
	{
		int hash = String.format( "(%f;%f)", x, y ).hashCode();
		int f = frequencies.containsKey(hash) ? frequencies.get(hash) + 1 : 1;
		
		frequencyMax = Math.max(frequencyMax,f);
		
		return f;
	}
	
	public void directRendering( Graphics2D g2d, Renderer renderer, Discretiser d, ColorProvider colorProvider )
	{
		nextPoint();
		
		if( d.contains( xyc [0], xyc [1] ) )
		{
			double f = incrementFrequency( xyc [0], xyc [1] );
			f = Math.log(f) / Math.log(frequencyMax);
		
			renderer.renderPoint(g2d, xyc[0], xyc[1], xyc[2], f, d, colorProvider);
		}
	}
	
	public void directRendering( int ite, String filePath, int width, int height )
	{
		Discretiser d = computeBestDiscretiser(width,height);
		
		Renderer renderer = new Renderer.Line();//Dot(4,4);
		ColorProvider colorProvider = new ColorProvider.Basic();
		
		BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g2d = img.createGraphics();

		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g2d.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
		g2d.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
		g2d.setColor( java.awt.Color.BLACK );
		g2d.fillRect(0,0,1000,1000);
		
		while( ite-- > 0 )
			directRendering(g2d,renderer,d,colorProvider);

		try
		{
			ImageIO.write(img,"PNG",new File(filePath));
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	public Discretiser computeBestDiscretiser( int width, int height )
	{
		double xorg, yorg, corg;
		
		xorg = xyc [0];
		yorg = xyc [1];
		corg = xyc [2];
		
		double xmin, ymin, xmax, ymax;
		
		xmin = ymin = Double.MAX_VALUE;
		xmax = ymax = Double.MIN_VALUE;
		
		for( int i = 0; i < 100000; i++ )
		{
			nextPoint();
			
			xmin = Math.min( xmin, xyc [0] );
			xmax = Math.max( xmax, xyc [0] );
			ymin = Math.min( ymin, xyc [1] );
			ymax = Math.max( ymax, xyc [1] );
		}
		
		xmin -= 0.1 * ( xmax - xmin );
		xmax += 0.1 * ( xmax - xmin );
		ymin -= 0.1 * ( ymax - ymin );
		ymax += 0.1 * ( ymax - ymin );
		
		xyc [0] = xorg;
		xyc [1] = yorg;
		xyc [2] = corg;
		
		System.out.printf("[%f;%f]x[%f;%f]%n",xmin,xmax,ymin,ymax);
		
		return new Discretiser( xmin, ymin, xmax, ymax, width, height );
	}*/
	
	public static void main( String ... args )
	{
		/*java.util.Locale.setDefault( java.util.Locale.ROOT );

		Context ctx = new Context();
		//ctx.init(3000,3000);

		Random r = new Random();

		int n = r.nextInt(2)+2;

		for( int i = 0; i < n; i++ )
		{
			FFunction ff = new FFunction(r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble());

			int vmax = r.nextInt(4) + 1;

			for( Variation v : Variation.getVariations() )
			{
				if( vmax > 0 && r.nextBoolean() )
				{
					ff.addVariation( r.nextDouble(), v );
					vmax--;
				}
			}

			ctx.addFlameFunction(ff);
		}

		for( int i = 0; i < 100; i++ ) ctx.nextPoint();
*/
		//ctx.niterate(100000,args[0]);
		//ctx.directRendering(100000,args[0],1000,1000);
	}
}