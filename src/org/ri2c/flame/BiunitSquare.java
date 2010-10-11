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

public class BiunitSquare<T>
{
  double xmin, xmax;
  double ymin, ymax;
  
  final int xsteps, ysteps;
  
  final T [][] data;
  
  public BiunitSquare( int xsteps, int ysteps )
  {
    this( -1, 1, -1, 1, xsteps, ysteps );
  }
  
  @SuppressWarnings("unchecked")
  public BiunitSquare( double xmin, double xmax, double ymin, double ymax, int xsteps, int ysteps )
  {
    if( xmin < xmax )
    {
      this.xmin = xmin;
      this.xmax = xmax;
    }
    else
    {
      this.xmin = xmax;
      this.xmax = xmin;
    }
    
    if( ymin < ymax )
    {
      this.ymin = ymin;
      this.ymax = ymax;
    }
    else
    {
      this.ymin = ymax;
      this.ymax = ymin;
    }

    if( xsteps < 0 || ysteps < 0 )
      throw new Error("steps can not be negative");

    this.xsteps = xsteps;
    this.ysteps = ysteps;
    
    this.data = (T[][]) new Object [xsteps] [ysteps];
    
    System.err.printf("biunit-square: [%f;%f]x[%f;%f] --> %dx%d%n",xmin,xmax,ymin,ymax,xsteps,ysteps);
  }
  
  public T get( double x, double y )
  {
    return data [discretX(x)] [discretY(y)];
  }
  
  public void set( double x, double y, T data )
  {
    //System.out.printf("(%f;%f) --> (%d;%d)%n",x,y,discretX(x),discretY(y));
    this.data [discretX(x)] [discretY(y)] = data;
  }
  
  public void set( int x, int y, T data )
  {
    this.data [x][y] = data;
  }
  
  public T get( int x, int y )
  {
    return data [x][y];
  }
  
  public boolean in( double x, double y )
  {
    return x >= xmin && x <= xmax && y >= ymin && y <= ymax;
  }
  
  public int discretX( double x )
  {
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
    
    return Math.max( 0, Math.min( xsteps, (int) ( ( xsteps - 1 ) * ( x - xmin ) / ( xmax - xmin ) ) ) );
  }
  
  public int discretY( double y )
  {
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
    
    return Math.max( 0, Math.min( ysteps, (int) ( ( ysteps - 1 ) * ( y - ymin ) / ( ymax - ymin ) ) ) );
  }
  
  public double realX( int x )
  {
    if( x >= xsteps )
    {
      System.err.printf("warning: x >= xsteps%n");
      x = xsteps - 1;
    }
    
    if( x < 0 )
    {
      System.err.printf("warning: x < 0%n");
      x = 0;
    }
    
    return xmin + x * ( xmax - xmin ) / (double) ( xsteps - 1 );
  }
  
  public double realY( int y )
  {
    if( y >= ysteps )
    {
      System.err.printf("warning: y >= ysteps%n");
      y = ysteps - 1;
    }
    
    if( y < 0 )
    {
      System.err.printf("warning: y < 0%n");
      y = 0;
    }
    
    return xmin + y * ( ymax - ymin ) / (double) ( ysteps - 1 );
  }
  
  public double xmin()
  {
    return xmin;
  }
  
  public double xmax()
  {
    return xmax();
  }
  
  public double ymin()
  {
    return ymin;
  }
  
  public double ymax()
  {
    return ymax();
  }
  
  public int width()
  {
    return xsteps;
  }
  
  public int height()
  {
    return ysteps;
  }
  
  public static void main( String ... args )
  {
    BiunitSquare<String> bus = new BiunitSquare<String>(5,5);
    for( int i = 0; i < 5; i++ )
    for( int j = 0; j < 5; j++ )
      bus.set( bus.realX(i), bus.realY(j), String.format( "(%d;%d)", i, j ) );
    
    System.out.printf("Real:%n");
    for( int i = 0; i < 5; i++ )
    for( int j = 0; j < 5; j++ )
      System.out.printf("%d;%d = %s%n",i,j,bus.get(bus.realX(i),bus.realY(j)));
      
    System.out.printf("Integer:%n");
    for( int i = 0; i < 5; i++ )
    for( int j = 0; j < 5; j++ )
      System.out.printf("%d;%d = %s%n",i,j,bus.get(i,j));
    
  }
}
