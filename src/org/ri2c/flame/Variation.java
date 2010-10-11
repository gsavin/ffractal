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

import java.util.Collections;
import java.util.Collection;
import java.util.LinkedList;

public abstract class Variation
{
  final int id;
  
  public Variation( int id )
  {
    this.id = id;
  }
  
  public int id()
  {
    return id;
  }
  
  protected double r( double x, double y )
  {
    return Math.sqrt(x*x+y*y);
  }
  
  protected double r2( double x, double y )
  {
    return x*x+y*y;
  }
  
  protected double theta( double x, double y )
  {
    return Math.atan( x/y );
  }
  
  protected double Theta( double x, double y )
  {
    return Math.atan( y/x );
  }
  
  public abstract double computeX( double x, double y );
  public abstract double computeY( double x, double y );
  
  public static class Linear
    extends Variation
  {
    public static final int ID = 0;
    
    public Linear()
    {
      super(ID);
    }
    
    public double computeX( double x, double y )
    {
      return x;
    }
    
    public double computeY( double x, double y )
    {
      return y;
    }
  }
  
  public static class Sinusoidal
    extends Variation
  {
    public static final int ID = 1;
    
    public Sinusoidal()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return Math.sin(x);
    }
    
    public double computeY(double x,double y)
    {
      return Math.sin(y);
    }
  }
  
  public static class Spherical
    extends Variation
  {
    public static final int ID = 2;
    
    public Spherical()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return x / ( x*x + y*y );
    }
    
    public double computeY(double x,double y)
    {
      return y / ( x*x + y*y );
    }
  }

  public static class Swirl
    extends Variation
  {
    public static final int ID = 3;
    
    public Swirl()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return x * Math.sin( x*x + y*y ) - y * Math.cos( x*x + y*y );
    }
    
    public double computeY(double x,double y)
    {
      return x * Math.cos( x*x + y*y ) + y * Math.sin( x*x + y*y );
    }
  }

  public static class Horseshoe
    extends Variation
  {
    public static final int ID = 4;
    
    public Horseshoe()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return ( x - y ) * ( x + y ) / r(x,y);
    }
    
    public double computeY(double x,double y)
    {
      return 2 * x * y / r(x,y);
    }
  }
  
  public static class Polar
    extends Variation
  {
    public static final int ID = 5;
    
    public Polar()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return theta(x,y) / Math.PI;
    }
    
    public double computeY(double x,double y)
    {
      return r(x,y) - 1;
    }
  }
  
  public static class Handkerchief
    extends Variation
  {
    public static final int ID = 6;
    
    public Handkerchief()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return r(x,y) * Math.sin(theta(x,y)+r(x,y));
    }
    
    public double computeY(double x,double y)
    {
      return r(x,y) * Math.cos(theta(x,y)-r(x,y));
    }
  }
  
  public static class Heart
    extends Variation
  {
    public static final int ID = 7;
    
    public Heart()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return r(x,y) * Math.sin(theta(x,y)*r(x,y));
    }
    
    public double computeY(double x,double y)
    {
      return - r(x,y) * Math.cos(theta(x,y)*r(x,y));
    }
  }
  
  public static class Disc
    extends Variation
  {
    public static final int ID = 8;
    
    public Disc()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return Math.sin(Math.PI*r(x,y)) * theta(x,y) / Math.PI;
    }
    
    public double computeY(double x,double y)
    {
      return Math.cos(Math.PI*r(x,y)) * theta(x,y) / Math.PI;
    }
  }
  
  public static class Spiral
    extends Variation
  {
    public static final int ID = 9;
    
    public Spiral()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return ( Math.cos(theta(x,y))+Math.sin(r(x,y)) ) / r(x,y);
    }
    
    public double computeY(double x,double y)
    {
      return ( Math.sin(theta(x,y))-Math.cos(r(x,y)) ) / r(x,y);
    }
  }
  
  public static class Hyperbolic
    extends Variation
  {
    public static final int ID = 10;
    
    public Hyperbolic()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return Math.sin(theta(x,y)) / r(x,y);
    }
    
    public double computeY(double x,double y)
    {
      return r(x,y) * Math.cos(theta(x,y));
    }
  }
  
  public static class Diamond
    extends Variation
  {
    public static final int ID = 11;
    
    public Diamond()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return Math.sin(theta(x,y)) * Math.cos(r(x,y));
    }
    
    public double computeY(double x,double y)
    {
      return Math.cos(theta(x,y)) * Math.sin(r(x,y));
    }
  }
  
  public static class Ex
    extends Variation
  {
    public static final int ID = 12;
    
    public Ex()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return r(x,y) * ( Math.pow( Math.sin( theta(x,y) + r(x,y) ), 3 ) + Math.pow( Math.cos( theta(x,y) - r(x,y) ), 3 ) );
    }
    
    public double computeY(double x,double y)
    {
      return r(x,y) * ( Math.pow( Math.sin( theta(x,y) + r(x,y) ), 3 ) - Math.pow( Math.cos( theta(x,y) - r(x,y) ), 3 ) );
    }
  }
  
  public static class Julia
    extends Variation
  {
    public static final int ID = 13;
    
    public Julia()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return Math.sqrt( r(x,y) ) * Math.cos( theta(x,y) / 2 + 0 );
    }
    
    public double computeY(double x,double y)
    {
      return Math.sqrt( r(x,y) ) * Math.sin( theta(x,y) / 2 + 0 );
    }
  }
  
  public static class Bent
    extends Variation
  {
    public static final int ID = 14;
    
    public Bent()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return x < 0 ? 2 * x : x;
    }
    
    public double computeY(double x,double y)
    {
      return y < 0 ? y / 2 : y;
    }
  }
  
  public static class Waves
    extends Variation
  {
    public static final int ID = 15;
    
    double b, c, e, f;
    
    public Waves()
    {
      super(ID);
      
      b = 0.2;
      c = 0.2;
      e = 0.2;
      f = 0.2;
    }
    
    public double computeX(double x,double y)
    {
      return x + b * Math.sin( y / ( c*c ) );
    }
    
    public double computeY(double x,double y)
    {
      return y + e * Math.sin( x / (f*f) );
    }
  }
  
  public static class Fisheye
    extends Variation
  {
    public static final int ID = 16;
    
    public Fisheye()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return 2 * y / ( r(x,y) + 1 );
    }
    
    public double computeY(double x,double y)
    {
      return 2 * x / ( r(x,y) + 1 );
    }
  }
  
  public static class Popcorn
    extends Variation
  {
    public static final int ID = 17;
    
    double e, f;
    
    public Popcorn()
    {
      super(ID);
      
      e = 0.2;
      f = 0.2;
    }
    
    public double computeX(double x,double y)
    {
      return x + e * Math.sin( Math.tan( 3*y ) );
    }
    
    public double computeY(double x,double y)
    {
      return y + f * Math.sin( Math.tan( 3*x ) );
    }
  }
  
  public static class Exponential
    extends Variation
  {
    public static final int ID = 18;
    
    public Exponential()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return Math.exp(x-1) * Math.cos(Math.PI*y);
    }
    
    public double computeY(double x,double y)
    {
      return Math.exp(x-1) * Math.sin(Math.PI*y);
    }
  }
  
  public static class Power
    extends Variation
  {
    public static final int ID = 19;
    
    public Power()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return Math.pow( r(x,y), Math.sin( theta(x,y) ) ) * Math.cos( theta(x,y) );
    }
    
    public double computeY(double x,double y)
    {
      return Math.pow( r(x,y), Math.sin( theta(x,y) ) ) * Math.sin( theta(x,y) );
    }
  }
  
  public static class Cosine
    extends Variation
  {
    public static final int ID = 20;
    
    public Cosine()
    {
      super(ID);
    }
    
    public double computeX(double x,double y)
    {
      return Math.cos( Math.PI*x ) * Math.cosh(y);
    }
    
    public double computeY(double x,double y)
    {
      return - Math.sin( Math.PI*x ) * Math.sinh(y);
    }
  }
  
  public static class Ring
    extends Variation
  {
    public static final int ID = 21;
    
    double c;
    
    public Ring()
    {
      super(ID);
      
      c = 2;
    }
    
    public double computeX(double x,double y)
    {
      return ( ( (r(x,y)+c*c ) % ( 2 *c*c ) ) - c*c + r(x,y) * ( 1-c*c ) ) * Math.cos(theta(x,y));
    }
    
    public double computeY(double x,double y)
    {
    	return ( ( (r(x,y)+c*c ) % ( 2 *c*c ) ) - c*c + r(x,y) * ( 1-c*c ) ) * Math.sin(theta(x,y));
    }
  }
  
  private static final LinkedList<Variation> VARIATIONS =
    new LinkedList<Variation>();
  
  static
  {
    VARIATIONS.add( new Variation.Linear() );
    VARIATIONS.add( new Variation.Spherical() );
    VARIATIONS.add( new Variation.Sinusoidal() );
    VARIATIONS.add( new Variation.Swirl() );
    VARIATIONS.add( new Variation.Polar() );
    VARIATIONS.add( new Variation.Handkerchief() );
    VARIATIONS.add( new Variation.Heart() );
    VARIATIONS.add( new Variation.Disc() );
    VARIATIONS.add( new Variation.Spiral() );
    VARIATIONS.add( new Variation.Hyperbolic() );
    VARIATIONS.add( new Variation.Diamond() );
    VARIATIONS.add( new Variation.Julia() );
    VARIATIONS.add( new Variation.Ex() );
    VARIATIONS.add( new Variation.Bent() );
    VARIATIONS.add( new Variation.Waves() );
    VARIATIONS.add( new Variation.Fisheye() );
    VARIATIONS.add( new Variation.Popcorn() );
    VARIATIONS.add( new Variation.Exponential() );
    VARIATIONS.add( new Variation.Power() );
    VARIATIONS.add( new Variation.Cosine() );
  }
  
  public static Collection<Variation> getVariations()
  {
    return Collections.unmodifiableCollection(VARIATIONS);
  }
}
