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

public class Discretiser
{
	double xmin, xmax, ymin, ymax;
	int width, height;

	public Discretiser( double xmin, double ymin, double xmax, double ymax, int width, int height )
	{
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.width = width;
		this.height = height;
	}

	public int width()
	{
		return width;
	}

	public int height()
	{
		return height;
	}

	public boolean contains( double x, double y )
	{
		return x >= xmin && x <= xmax && y >= ymin && y <= ymax;
	}

	public int convertX( double x )
	{
		/*
		if( x > xmax )
		{
			System.err.printf("warning: x > xmax%n");
			x = xmax;
		}

		if( x < xmin )
		{
			System.err.printf("warning: x < xmin (%.3f)%n",x);
			x = xmin;
		}
		*/
		return (int) ( ( width - 1 ) * ( x - xmin ) / ( xmax - xmin ) );
	}

	public int convertY( double y )
	{
		/*
		if( y > ymax )
		{
			System.err.printf("warning: y > ymax%n");
			y = ymax;
		}

		if( y < ymin )
		{
			System.err.printf("warning: y < ymin%n");
			y = ymin;
		}
		*/
		return (int) ( ( height - 1 ) * ( y - ymin ) / ( ymax - ymin ) );
	}

	public double convertX( int x )
	{
		if( x >= width )
		{
			System.err.printf("warning: x >= width%n");
			x = width - 1;
		}

		if( x < 0 )
		{
			System.err.printf("warning: x < 0%n");
			x = 0;
		}

		return xmin + x * ( xmax - xmin ) / (double) ( width - 1 );
	}

	public double convertY( int y )
	{
		if( y >= height )
		{
			System.err.printf("warning: y >= height%n");
			y = height - 1;
		}

		if( y < 0 )
		{
			System.err.printf("warning: y < 0%n");
			y = 0;
		}

		return xmin + y * ( ymax - ymin ) / (double) ( height - 1 );
	}
}