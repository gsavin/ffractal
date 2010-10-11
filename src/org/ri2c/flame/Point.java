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

public class Point
{
  final double x, y;
  double frequency;
  double color;
  
  public Point( double x, double y, double color )
  {
    this.x = x;
    this.y = y;
    this.color = color;
    
    this.frequency = 0;
  }
  
  public double x()
  {
    return x;
  }
  
  public double y()
  {
    return y;
  }
  
  public double color()
  {
    return color;
  }
  
  public double frequency()
  {
    return frequency;
  }
  
  public void blend( double color )
  {
    this.color = ( this.color + color ) / 2.0;
  }
  
  public void incrementFrequency()
  {
    this.frequency++;
  }
}
