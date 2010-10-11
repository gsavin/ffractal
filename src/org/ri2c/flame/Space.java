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

import java.nio.DoubleBuffer;
import java.util.Random;

public class Space
{
	public double xmin;
	public double xmax;
	public double ymin;
	public double ymax;
	
	public Space()
	{
		this( -1, -1, 1, 1 );
	}
	
	public Space( double xmin, double ymin, double xmax, double ymax )
	{
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
	}
	
	public static Space computeBestSpace( FFunction finalFunction, FFunction ... functions )
	{
		if( functions == null || functions.length == 0 )
			return new Space();
		
		Space s = new Space( Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE );
		Random random = new Random();
		
		double [] xyc = { random.nextDouble(), random.nextDouble(), 0 };
		
		for( int j = 0; j < 100; j++ )
			functions [random.nextInt(functions.length)].compute(xyc);
		
		int size = 100000;
		
		DoubleBuffer buffer = DoubleBuffer.allocate( size * 2 );
		double mx, my, dm;
		
		mx = my = 0;
		dm = 0;
		
		for( int i = 0; i < size; i++ )
		{
			functions [random.nextInt(functions.length)].compute(xyc);
			
			if( finalFunction != null )
				finalFunction.compute(xyc);
			
			if( Double.isNaN( xyc [0] ) || Double.isNaN( xyc [1] ) )
			{
				xyc [0] = random.nextDouble();
				xyc [1] = random.nextDouble();
				
				for( int j = 0; j < 20; j++ )
					functions [random.nextInt(functions.length)].compute(xyc);
			}
			else
			{
				buffer.put( xyc [0] );
				buffer.put( xyc [1] );
				
				mx += xyc [0] / size;
				my += xyc [1] / size;
			}
			
			/*
			s.xmin = Math.min( s.xmin, xyc [0] );
			s.xmax = Math.max( s.xmax, xyc [0] );
			s.ymin = Math.min( s.ymin, xyc [1] );
			s.ymax = Math.max( s.ymax, xyc [1] );
			*/
		}
		
		for( int i = 0; i < size-1; i++ )
		{
			double dt;
			dt = Math.sqrt( Math.pow( buffer.get(i*2) - mx, 2 ) + Math.pow( buffer.get(i*2+1) - my, 2 ) );
			
			if( ! Double.isNaN(dt) )
				dm += dt / size;
		}
		
		for( int i = 0; i < size-1; i++ )
		{
			double dt;
			dt = Math.sqrt( Math.pow( buffer.get(i*2) - mx, 2 ) + Math.pow( buffer.get(i*2+1) - my, 2 ) );
			
			if( Math.log(dt) <= Math.log(dm) )
			{
				s.xmin = Math.min( s.xmin, buffer.get(i*2) );
				s.xmax = Math.max( s.xmax, buffer.get(i*2) );
				s.ymin = Math.min( s.ymin, buffer.get(i*2+1) );
				s.ymax = Math.max( s.ymax, buffer.get(i*2+1) );
			}
		}
		
		double dx = ( s.xmax - s.xmin );
		double dy = ( s.ymax - s.ymin );
		
		s.xmin -= 0.1 * dx;
		s.xmax += 0.1 * dx;
		s.ymin -= 0.1 * dy;
		s.ymax += 0.1 * dy;
		
		if( Double.isNaN(s.xmin) || Double.isNaN(s.xmax) || Double.isNaN(s.ymin) || Double.isNaN(s.ymax) )
			System.out.println("not a number space");
		
		return s;
	}
}
