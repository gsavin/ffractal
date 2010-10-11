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
import java.awt.Color;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.nio.DoubleBuffer;

public interface Renderer
{
	void rendering( DoubleBuffer points, int offset, int size,
			Graphics2D g2d, Discretiser d, ColorProvider colorProvider );

	
	void renderPoint( Graphics2D g2d, double x, double y, double c, double f,
			Discretiser d, ColorProvider colorProvider );
	
	public static class Dot
		implements Renderer
	{
		double sx = 10, sy = 10;
		Ellipse2D.Double e2dD = new Ellipse2D.Double();

		public Dot()
		{
			this( 6, 6 );
		}
		
		public Dot( double sx, double sy )
		{
			this.sx = sx;
			this.sy = sy;
		}
		
		public void rendering( DoubleBuffer points, int offset,
				int size, Graphics2D g2d, Discretiser d, ColorProvider colorProvider )
		{
			for( int k = 0; k < size; k++ )
				renderPoint(g2d,points.get(offset+k*4),points.get(offset+k*4+1),points.get(offset+k*4+2),points.get(offset+k*4+3),d,colorProvider);
		}
		
		public void renderPoint( Graphics2D g2d, double x, double y, double c, double f, Discretiser d, ColorProvider colorProvider )
		{
			e2dD.setFrame(
					d.convertX(x)-sx/2.0, d.convertY(y)-sy/2.0,
					sx, sy
			);

			g2d.setColor( colorProvider.getColor(x,y,c,f) );
			g2d.fill(e2dD);
		}
	}

	public static class Line
		implements Renderer
	{
		Line2D.Double l2dD = new Line2D.Double();
		
		public void rendering( DoubleBuffer points, int offset,
				int size, Graphics2D g2d, Discretiser d, ColorProvider colorProvider )
		{
			Color color;
			int [] coords = new int [6];
			double mx, my, mc, mf;

			for( int k = 0; k < size-2; k+=2 )
			{
				renderPoint(g2d,points.get(offset+k*4),points.get(offset+k*4+1),points.get(offset+k*4+2),points.get(offset+k*4+3),d,colorProvider);
				mx = my = mc = mf = 0;

				for( int i = 0; i < 2; i++ )
				{
					coords [2*i] = d.convertX(points.get(offset+(k+i)*4));
					coords [2*i+1] = d.convertY(points.get(offset+(k+i)*4+1));

					mx += coords [2*i];
					my += coords [2*i+1];
					mc += points.get(offset+(k+i)*4+2);
					mf += points.get(offset+(k+i)*4+3);
				}

				mx /= 2;
				my /= 2;
				mc /= 2;
				mf /= 2;

				l2dD.setLine(
						coords [0], coords [1],
						coords [2], coords [3]
				);


				color = colorProvider.getColor(mx,my,mc,mf);
				g2d.setColor(color);
				g2d.draw(l2dD);
			}
		}
		
		double px = Double.NaN;
		double py = Double.NaN;
		double pc = Double.NaN;
		double pf = Double.NaN;
		
		public void renderPoint( Graphics2D g2d, double x, double y, double c, double f, Discretiser d, ColorProvider colorProvider )
		{
			if( px != Double.NaN )
			{
				l2dD.setLine(
						d.convertX(px), d.convertY(py),
						d.convertX(x),  d.convertY(y)
				);

				Color color = colorProvider.getColor( ( px + x) / 2, ( py + y ) / 2, ( pc + c ) / 2, ( pf + f ) / 2 );
				g2d.setColor(color);
				g2d.draw(l2dD);
			}
			
			px = x;
			py = y;
			pc = c;
			pf = f;
		}
	}

	public static class QuadCurve
	implements Renderer
	{
		QuadCurve2D.Double q2dD = new QuadCurve2D.Double();
		
		public void rendering( DoubleBuffer points, int offset,
				int size, Graphics2D g2d, Discretiser d, ColorProvider colorProvider )
		{
			Color color;
			int [] coords = new int [6];
			double mx, my, mc, mf;

			for( int k = 0; k < size-2; k+=2 )
			{
				mx = my = mc = mf = 0;

				for( int i = 0; i < 3; i++ )
				{
					coords [2*i] = d.convertX(points.get(offset+(k+i)*4));
					coords [2*i+1] = d.convertY(points.get(offset+(k+i)*4+1));

					mx += coords [2*i];
					my += coords [2*i+1];
					mc += points.get(offset+(k+i)*4+2);
					mf += points.get(offset+(k+i)*4+3);
				}

				mx /= 3;
				my /= 3;
				mc /= 3;
				mf /= 3;

				q2dD.setCurve(
						coords [0], coords [1],
						coords [2], coords [3],
						coords [4], coords [5]
				);

				color = colorProvider.getColor(mx,my,mc,mf);
				g2d.setColor(color);
				g2d.draw(q2dD);
			}
		}
		
		double px1 = Double.NaN;
		double py1 = Double.NaN;
		double pc1 = Double.NaN;
		double pf1 = Double.NaN;
		double px2 = Double.NaN;
		double py2 = Double.NaN;
		double pc2 = Double.NaN;
		double pf2 = Double.NaN;
		
		public void renderPoint( Graphics2D g2d, double x, double y, double c, double f, Discretiser d, ColorProvider colorProvider )
		{
			if( px1 != Double.NaN )
			{
				if( px2 != Double.NaN )
				{
					q2dD.setCurve(
							d.convertX(px1), d.convertY(py1),
							d.convertX(px2), d.convertY(py2),
						    d.convertX(x)  , d.convertY(y)
					);

					Color color = colorProvider.getColor( ( px1 + px2 + x ) / 3, ( py1 + py2 + y ) / 3, ( pc1 + pc2 + c ) / 3, ( pf1 + pf2 + f ) / 3 );
					g2d.setColor(color);
					g2d.draw(q2dD);
					
					px1 = x;
					py1 = y;
					pc1 = c;
					pf1 = f;

					px2 = Double.NaN;
					py2 = Double.NaN;
					pc2 = Double.NaN;
					pf2 = Double.NaN;
				}
				else
				{
					px2 = x;
					py2 = y;
					pc2 = c;
					pf2 = f;
				}
			}
			else
			{
				px1 = x;
				py1 = y;
				pc1 = c;
				pf1 = f;
			}
		}
	}

	public static class CubicCurve
	implements Renderer
	{
		public void rendering( DoubleBuffer points, int offset,
				int size, Graphics2D g2d, Discretiser d, ColorProvider colorProvider )
		{
			CubicCurve2D.Double c2dD = new CubicCurve2D.Double();
			Color color;
			int [] coords = new int [8];
			double mx, my, mc, mf;

			for( int k = 0; k < size-3; k+=3 )
			{
				mx = my = mc = mf = 0;

				for( int i = 0; i < 4; i++ )
				{
					coords [2*i] = d.convertX(points.get(offset+(k+i)*4));
					coords [2*i+1] = d.convertY(points.get(offset+(k+i)*4+1));

					mx += coords [2*i];
					my += coords [2*i+1];
					mc += points.get(offset+(k+i)*4+2);
					mf += points.get(offset+(k+i)*4+3);
				}

				mx /= 4;
				my /= 4;
				mc /= 4;
				mf /= 4;

				c2dD.setCurve(
						coords [0], coords [1],
						coords [2], coords [3],
						coords [4], coords [5],
						coords [6], coords [7]
				);

				color = colorProvider.getColor(mx,my,mc,mf);
				g2d.setColor(color);
				g2d.draw(c2dD);
			}
		}
		
		public void renderPoint( Graphics2D g2d, double x, double y, double c, double f, Discretiser d, ColorProvider colorProvider )
		{
			
		}
	}
}
