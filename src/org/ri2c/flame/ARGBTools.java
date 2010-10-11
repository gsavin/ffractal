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

public class ARGBTools
{
	static final int R_MASK = Integer.parseInt( "00000000111111110000000000000000", 2 );
	static final int G_MASK = Integer.parseInt( "00000000000000001111111100000000", 2 );
	static final int B_MASK = Integer.parseInt( "00000000000000000000000011111111", 2 );
	
	public static int
	getARGBfromRGB( int alpha, int rgb )
	{
	  return ( borne(0,alpha,255) << 24 ) | rgb;
	}
	
	public static int
	getRGBAfromRGB( int alpha, int rgb )
	{
	  return ( rgb << 8 ) | ( B_MASK & borne(0,alpha,255) );
	}
	
	public static int
	getRGB( int a, int r, int g, int b )
	{
		a = borne( 0, a, 255 );
		r = borne( 0, r, 255 );
		g = borne( 0, g, 255 );
		b = borne( 0, b, 255 );
		
		return ( a << 24 ) | ( r << 16 ) | ( g << 8 ) | b;
	}
	
	public static int
	getRGBA( int r, int g, int b, int a )
	{
		a = borne( 0, a, 255 );
		r = borne( 0, r, 255 );
		g = borne( 0, g, 255 );
		b = borne( 0, b, 255 );
		
		return ( r << 24 ) | ( g << 16 ) | ( b << 8 ) | a;
	}
	
	private static int
	borne( int min, int value, int max )
	{
		return Math.max( min, Math.min( max, value ) );
	}
	
	public static int
	getAlpha( int argb )
	{
		int a = ( argb >> 24 );
		return a < 0 ? 255 + a : a;
	}
	
	public static int
	getRed( int argb )
	{
		return ( argb & R_MASK ) >> 16;
	}
	
	public static int
	getGreen( int argb )
	{
		return ( argb & G_MASK ) >> 8;
	}
	
	public static int
	getBlue( int argb )
	{
		return argb & B_MASK;
	}
	
	public static int
	getGrayscale( int argb )
	{
		return (int) ( 0.299 * getRed( argb ) + 0.587 * getGreen( argb ) + 0.114 * getBlue( argb ) );
	}
	
	public static int
	getLuminance( int argb )
	{
		return getGrayscale( argb );
	}
	
	public static int
	getChrominanceU( int argb )
	{
		return (int) ( 0.492 * ( getBlue( argb ) - getLuminance( argb ) ) );
	}
	
	public static int
	getChrominanceV( int argb )
	{
		return (int) ( 0.877 * ( getRed( argb ) - getLuminance( argb ) ) );
	}
	
	public static int
	getRGBfromYUV( int y, int u, int v )
	{
		int r = (int) ( y - 0.00004 * u + 1.14 * v );
		int g = (int) ( y - 0.395 * u - 0.581 * v );
		int b = (int) ( y + 2.032 * u - 0.0005 * v );
		
		return getRGB( 255, r, g, b );
	}
	
	public static float
	getTeinte( int argb )
	{
		float r = getRed( argb ) / 255.0f;
		float g = getGreen( argb ) / 255.0f;
		float b = getBlue( argb ) / 255.0f;
		float max = Math.max( r, Math.max( g, b ) );
		float min = Math.min( r, Math.min( g, b ) );
		
		if( max == min )
		{
			return 0;
		}
		
		if( max == r )
		{
			if( g >= b )
				return (int) ( 60 * ( g - b ) / ( max - min ) );
			else
				return (int) ( 60 * ( g - b ) / ( max - min ) ) + 360;
		}
		
		if( max == g )
		{
			return (int) ( 60 * ( b - r ) / ( max - min ) ) + 120;
		}
		
		return (int) ( 60 * ( r - g ) / ( max - min ) ) + 240;
	}
	
	public static float
	getSaturation( int argb )
	{
		float r = getRed( argb ) / 255.0f;
		float g = getGreen( argb ) / 255.0f;
		float b = getBlue( argb ) / 255.0f;
		float max = Math.max( r, Math.max( g, b ) );
		float min = Math.min( r, Math.min( g, b ) );
		
		if( max - min == 0 ) 
			return 0;
		
		return 1 - min / max;
	}
	
	public static float
	getValeur( int argb )
	{
		float r = getRed( argb ) / 255.0f;
		float g = getGreen( argb ) / 255.0f;
		float b = getBlue( argb ) / 255.0f;
		float max = Math.max( r, Math.max( g, b ) );
		
		return max;
	}
	
	public static int
	getRGBfromTSV( float t, float s, float v )
	{
		return getRGBfromTSV( t, s, v, 255 );
	}
	
	public static int
	getRGBfromTSV( float t, float s, float v, int alpha )
	{
		int r, g, b;
		
		int h_i = ( (int) ( t / 60.0f ) ) % 6;
		float f = t / 60.0f - h_i;
		float p = v * ( 1 - s );
		float q = v * ( 1 - f * s );
		float u = v * ( 1 - ( 1 - f ) * s );
		
		switch( h_i )
		{
		case 0: r = (int) ( v * 255 ); g = (int) ( u * 255 ); b = (int) ( p * 255 ); break;
		case 1: r = (int) ( q * 255 ); g = (int) ( v * 255 ); b = (int) ( p * 255 ); break;
		case 2: r = (int) ( p * 255 ); g = (int) ( v * 255 ); b = (int) ( u * 255 ); break;
		case 3: r = (int) ( p * 255 ); g = (int) ( q * 255 ); b = (int) ( v * 255 ); break;
		case 4: r = (int) ( u * 255 ); g = (int) ( p * 255 ); b = (int) ( v * 255 ); break;
		default: r = (int) ( v * 255 ); g = (int) ( p * 255 ); b = (int) ( q * 255 ); break;
		}
		
		return getRGBA( r, g, b, alpha );
	}
	
	public static int
	getARGBfromTSV( int alpha, float t, float s, float v )
	{
		int r, g, b;
		
		int h_i = ( (int) ( t / 60.0f ) ) % 6;
		float f = t / 60.0f - h_i;
		float p = v * ( 1 - s );
		float q = v * ( 1 - f * s );
		float u = v * ( 1 - ( 1 - f ) * s );
		
		switch( h_i )
		{
		case 0: r = (int) ( v * 255 ); g = (int) ( u * 255 ); b = (int) ( p * 255 ); break;
		case 1: r = (int) ( q * 255 ); g = (int) ( v * 255 ); b = (int) ( p * 255 ); break;
		case 2: r = (int) ( p * 255 ); g = (int) ( v * 255 ); b = (int) ( u * 255 ); break;
		case 3: r = (int) ( p * 255 ); g = (int) ( q * 255 ); b = (int) ( v * 255 ); break;
		case 4: r = (int) ( u * 255 ); g = (int) ( p * 255 ); b = (int) ( v * 255 ); break;
		default: r = (int) ( v * 255 ); g = (int) ( p * 255 ); b = (int) ( q * 255 ); break;
		}
		
		return getRGB( alpha, r, g, b );
	}
	
	public static void
	main( String [] args )
	{
		int r = 155, g = 20, b = 50, rgb = getRGB( 255, r, g, b );
		float t = getTeinte( rgb), s = getSaturation( rgb ), v = getValeur( rgb );
		int rgbfromtsv = getRGBfromTSV( t, s, v );
		System.out.printf( "RGB: (%d;%d;%d)\tTSV: (%.3f;%.3f;%.3f)\n", r, g, b, t, s, v );
		System.out.printf( "TSV -> RGB : (%d;%d;%d)\n", getRed( rgbfromtsv ), getGreen( rgbfromtsv ), getBlue( rgbfromtsv ) );
	}
}
