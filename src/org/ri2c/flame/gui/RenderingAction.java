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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.ri2c.flame.ColorProvider;
import org.ri2c.flame.Renderer;
import org.ri2c.flame.RenderingContext;

public abstract class RenderingAction
{
	public abstract void render( RenderingContext ctx );
	
	public static enum Angle
	{
		PI2( 2 * Math.PI ),
		PI( Math.PI ),
		PIby2( Math.PI / 2.0 ),
		PIby3( Math.PI / 3.0 ),
		PIby4( Math.PI / 4.0 ),
		PIby5( Math.PI / 5.0 ),
		PIby6( Math.PI / 6.0 ),
		PIby7( Math.PI / 7.0 ),
		PIby8( Math.PI / 8.0 )
		
		;
		
		final double value;
		
		Angle( double a )
		{
			this.value = a;
		}
	}
	
	public static enum RendererType
	{
		Dot,
		Line,
		QuadCurve,
		CubicCurve
	}
	
	public static enum ColorProviderType
	{
		Random,
		Basic
	}
	
	public static RenderingAction createSetRendererAction()
	{
		Object [] renderers = RendererType.values();
		
		Object a = JOptionPane.showInputDialog(null, "Renderer type ?", "Set renderer", JOptionPane.QUESTION_MESSAGE, null, renderers, RendererType.Dot );
		
		if( a == null )
			return null;
		
		SetRendererAction sra = null;
		
		switch( (RendererType) a )
		{
		case Dot:
			sra = new SetRendererAction( new Renderer.Dot() );
			break;
		case Line:
			sra = new SetRendererAction( new Renderer.Line() );
			break;
		case QuadCurve:
			sra = new SetRendererAction( new Renderer.QuadCurve() );
			break;
		case CubicCurve:
			sra = new SetRendererAction( new Renderer.CubicCurve() );
			break;
		}
		
		return sra;
	}
	
	public static RenderingAction createSetColorProviderAction()
	{
		Object [] colorProviders = ColorProviderType.values();

		Object a = JOptionPane.showInputDialog(null, "Color provider type ?", "Set color provider", JOptionPane.QUESTION_MESSAGE, null, colorProviders, ColorProviderType.Basic );
		
		if( a == null )
			return null;
		
		SetColorProviderAction scpa = null;
		
		switch( (ColorProviderType) a )
		{
		case Random:
			scpa = new SetColorProviderAction( new ColorProvider.RandomColorProvider() );
			break;
		case Basic:
			scpa = new SetColorProviderAction( new ColorProvider.Basic() );
			break;
		}
		
		return scpa;
	}
	
	public static RenderingAction createRotateAction()
	{
		Object [] angles = Angle.values();
		
		Object a = JOptionPane.showInputDialog(null, "Rotation angle ?", "Create a rotation", JOptionPane.QUESTION_MESSAGE, null, angles, Angle.PIby2 );
		
		if( a == null )
			return null;
		
		RotateAction ra;
		
		int r = JOptionPane.showConfirmDialog(null, "Define rotation origin ?", "Create a rotation", JOptionPane.YES_NO_OPTION );
		
		if( r == JOptionPane.YES_OPTION )
		{
			String point = JOptionPane.showInputDialog(null, "Rotation center ? ( cx ; cy )", "Create a rotation", JOptionPane.QUESTION_MESSAGE );
			
			if( point == null )
			{
				return null;
			}
			else
			{
				Pattern p = Pattern.compile( "((?:[1-9]+\\d*|0)(?:[.]\\d+))\\s*[;\\s]\\s*((?:[1-9]+\\d*|0)(?:[.]\\d+))");
				Matcher m = p.matcher( point );
				
				if( m.find() )
				{
					double tx, ty;
					
					tx = Double.parseDouble( m.group(1) );
					ty = Double.parseDouble( m.group(2) );
					
					ra = new RotateAction( ( (Angle) a ), tx, ty );
				}
				else
				{
					JOptionPane.showMessageDialog(null,"Bad point format","Create a rotation",JOptionPane.ERROR_MESSAGE);
					return null;
				}
			}
		}
		else
		{
			ra = new RotateAction( ( (Angle) a ) );
		}
		
		return ra;
	}
	
	public static RenderingAction createTranslateAction()
	{
		TranslateAction ta;
		
		String point = JOptionPane.showInputDialog(null, "Translation vector ? ( tx ; ty )", "Create a translation", JOptionPane.QUESTION_MESSAGE );
		
		if( point == null )
		{
			return null;
		}
		else
		{
			Pattern p = Pattern.compile( "((?:[1-9]+\\d*|0)(?:[.]\\d+))\\s*[;\\s]\\s*((?:[1-9]+\\d*|0)(?:[.]\\d+))");
			Matcher m = p.matcher( point );
			
			if( m.find() )
			{
				double tx, ty;
				
				tx = Double.parseDouble( m.group(1) );
				ty = Double.parseDouble( m.group(2) );
				
				ta = new TranslateAction( tx, ty );
			}
			else
			{
				JOptionPane.showMessageDialog(null,"Bad vector format","Create a translation",JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		
		return ta;
	}
	
	public static RenderingAction createGeneratePointsAction()
	{
		String input = (String) JOptionPane.showInputDialog(null,"Number of points ?", "Generate points", JOptionPane.QUESTION_MESSAGE, null, null, "100000" );
		
		if( input == null )
			return null;
		
		input = input.trim();
		
		if( input.matches("^\\d+$") )
			return new GeneratePointsAction( Integer.parseInt(input) );
		
		else return null;
	}
	
	public static RenderingAction createRenderPointsAction()
	{
		return new RenderPointsAction();
	}
	
	protected static class GeneratePointsAction
		extends RenderingAction
	{
		int size;

		public GeneratePointsAction( int size )
		{
			this.size = size;
		}
		
		public void render(RenderingContext ctx)
		{
			ctx.generatePoints(size);
		}
		
		public String toString()
		{
			return String.format( "Generate %d points", size );
		}
	}
	
	protected static class SetRendererAction
		extends RenderingAction
	{
		Renderer renderer;
		
		public SetRendererAction( Renderer r )
		{
			this.renderer = r;
		}
		
		public void render(RenderingContext ctx)
		{
			ctx.setRenderer(renderer);
		}
		
		public String toString()
		{
			return String.format("Set renderer to %s",renderer.getClass().getSimpleName());
		}
	}
	
	protected static class SetColorProviderAction
		extends RenderingAction
	{
		ColorProvider colorProvider;
		
		public SetColorProviderAction( ColorProvider cp )
		{
			this.colorProvider = cp;
		}
		
		public void render(RenderingContext ctx)
		{
			ctx.setColorProvider(colorProvider);
		}
		
		public String toString()
		{
			return String.format("Set color provider to %s",colorProvider.getClass().getSimpleName());
		}
	}
	
	protected static class RenderPointsAction
		extends RenderingAction
	{
		public void render(RenderingContext ctx) 
		{
			ctx.renderPoints();
		}
		
		public String toString()
		{
			return "Render points";
		}
	}
	
	protected static class RotateAction
		extends RenderingAction
	{
		Angle angle;
		double x, y;
		
		public RotateAction( Angle angle )
		{
			this.angle = angle;
			x = Double.NaN;
			y = Double.NaN;
		}
		
		public RotateAction( Angle angle, double x, double y )
		{
			this.angle = angle;
			this.x = x;
			this.y = y;
		}
		
		public void render( RenderingContext ctx )
		{
			if( Double.isNaN(x) || Double.isNaN(y) )
				ctx.rotate( angle.value );
			else
				ctx.rotate( angle.value, x, y );
		}
		
		public String toString()
		{
			return String.format( "Rotation of %s%s", angle, ( Double.isNaN(x) || Double.isNaN(y) ) ? "" : String.format( " center on (%f;%f)", x, y ) );
		}
	}
	
	protected static class TranslateAction
		extends RenderingAction
	{
		double tx, ty;
		
		public TranslateAction( double tx, double ty )
		{
			this.tx = tx;
			this.ty = ty;
		}
		
		public void render( RenderingContext ctx )
		{
			ctx.translate(tx, ty);
		}
		
		public String toString()
		{
			return String.format("Translate with (%f;%f)",tx,ty);
		}
	}
}
