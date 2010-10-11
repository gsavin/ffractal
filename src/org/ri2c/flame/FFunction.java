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

import java.util.LinkedList;

public class FFunction
{
	public static class FFVariation
	{
		double weight;
		Variation variation;
		
		public FFVariation( double w, Variation v )
		{
			this.weight = w;
			this.variation = v;
		}
		
		public String toString()
		{
			return String.format( "%s (%f)", variation.getClass().getSimpleName(), weight );
		}
	}

	String name;
	double weight;
	double a, b, c, d, e, f;
	double color;
	LinkedList<FFVariation> variations;

	public FFunction( double color, double ... data )
	{
		this.color = color;

		a = data [0];
		b = data [1];
		c = data [2];
		d = data [3];
		e = data [4];
		f = data [5];

		variations = new LinkedList<FFVariation>();
		
		name = "";
		weight = 1;
	}
	
	public void setWeight( double w )
	{
		this.weight = w;
	}
	
	public double getWeight()
	{
		return weight;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public double color()
	{
		return color;
	}
	
	public void addVariation( double w, Variation v )
	{
		addVariation( new FFVariation(w,v) );
	}
	
	public void addVariation( FFVariation ffv )
	{
		variations.add(ffv);
	}

	public void compute( double [] xy )
	{
		double x = 0;
		double y = 0;

		if( xy == null )
			throw new Error("point is null");

		double cx = a*xy[0]+b*xy[1]+c;
		double cy = d*xy[0]+e*xy[1]+f;

		for( FFVariation ffv : variations )
		{
			x += ffv.weight * ffv.variation.computeX(cx, cy);
			y += ffv.weight * ffv.variation.computeY(cx, cy);
		}
		
		xy [0] = x;
		xy [1] = y;
		xy [2] = ( xy [2] + color ) / 2.0;
	}
}
