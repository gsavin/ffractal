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

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.text.NumberFormat;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.ri2c.flame.Context;
import org.ri2c.flame.Discretiser;
import org.ri2c.flame.FFunction;
import org.ri2c.flame.RenderingContext;
import org.ri2c.flame.Space;
import org.ri2c.flame.gui.FFWizard.FFWizardListener;

public class FFGui
	extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2861824179795521368L;

	protected class FunctionPane
		extends JPanel implements ActionListener, FFWizard.FFWizardListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 6078900801372281580L;
		
		FFItem fffItem;
		
		public FunctionPane()
		{
			fffItem = new FFItem();
			fffItem.setPreferredSize( new Dimension(200,100) );
			
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			setLayout( bag );
			
			c.insets = new Insets( 5, 5, 5, 5 );
			
			JButton addFF;
			
			addFF = new JButton("new function");
			
			addFF.setActionCommand( "ff.add" );
			addFF.addActionListener(this);
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.anchor = GridBagConstraints.WEST;
			bag.setConstraints(addFF,c);
			
			JScrollPane scrollList = new JScrollPane( fflist );
			scrollList.setPreferredSize( new Dimension( 200, 400 ) );
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			bag.setConstraints(scrollList,c);
			
			add( addFF );
			add( scrollList );

			JButton select = new JButton("set final function");
			select.setActionCommand("fff.select");
			select.addActionListener(this);
			
			setLayout(bag);
			
			c.insets = new Insets( 5,5,5,5 );
			c.anchor = GridBagConstraints.WEST;
			c.weightx = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			
			bag.setConstraints(select,c);
			
			c.fill = GridBagConstraints.HORIZONTAL;
			
			bag.setConstraints(fffItem,c);
			
			add( select );
			add( fffItem );
		}

		public FFunction getFinalFFunction()
		{
			return fffItem.getFFunction();
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if( e.getActionCommand().equals("ff.add") )
			{
				FFWizard ffw = new FFWizard(frame);
				ffw.addWizardListener(this);
				ffw.setVisible(true);
			}
			else if( "fff.select".equals(e.getActionCommand()) )
			{
				FFWizard ffw = new FFWizard(frame);
				ffw.addWizardListener( new FFWizardListener()
				{
					public void ffWizardFinished(FFWizard ffw)
					{
						FFunction ff = ffw.getFlameFunction();
						
						if( ff != null )
							fffItem.updateFunction(ff);
						
						revalidate();
						frame.pack();
					}
				});
				ffw.setVisible(true);
			}
		}

		public void ffWizardFinished(FFWizard ffw)
		{
			FFunction ff = ffw.getFlameFunction();
			
			if( ff != null )
				fflist.addFFunction(ff);
		}
	}
	
	protected class SpaceSelector
		extends JPanel implements ActionListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5099749544327543086L;
		
		SpinnerNumberModel xmin, xmax, xorg, ymin, ymax, yorg;
		
		public SpaceSelector()
		{
			xmin = new SpinnerNumberModel( new Double(-1), null, null, new Double(0.0001) );
			xmax = new SpinnerNumberModel( new Double(1), null, null, new Double(0.0001) );
			ymin = new SpinnerNumberModel( new Double(-1), null, null, new Double(0.0001) );
			ymax = new SpinnerNumberModel( new Double(1), null, null, new Double(0.0001) );
			xorg = new SpinnerNumberModel( new Double(0), null, null, new Double(0.0001) );
			yorg = new SpinnerNumberModel( new Double(0), null, null, new Double(0.0001) );

			build();
		}
		
		public Discretiser createDiscretiser( int width, int height )
		{
			return new Discretiser( 
					xmin.getNumber().doubleValue(), ymin.getNumber().doubleValue(), 
					xmax.getNumber().doubleValue(), ymax.getNumber().doubleValue(), 
					width, height );
		}
		
		public double getXorg()
		{
			return xorg.getNumber().doubleValue();
		}
		
		public double getYorg()
		{
			return yorg.getNumber().doubleValue();
		}
		
		protected void build()
		{
			JButton computeBest = new JButton("best");
			computeBest.setActionCommand("best");
			computeBest.addActionListener(this);
			
			JLabel xminL = new JLabel("<html>x<sub>min</sub></html>"), xmaxL = new JLabel("<html>x<sub>max</sub></html>"), xorgL = new JLabel("<html>x<sub>org</sub></html>");
			JLabel yminL = new JLabel("<html>y<sub>min</sub></html>"), ymaxL = new JLabel("<html>y<sub>max</sub></html>"), yorgL = new JLabel("<html>y<sub>org</sub></html>");
			
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			setLayout(bag);
			
			c.insets = new Insets( 5, 5, 5 ,5 );
			
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;

			JSpinner xminS, xmaxS, xorgS, yminS, ymaxS, yorgS;
			
			xminS = new JSpinner(xmin);
			xmaxS = new JSpinner(xmax);
			yminS = new JSpinner(ymin);
			ymaxS = new JSpinner(ymax);
			xorgS = new JSpinner(xorg);
			yorgS = new JSpinner(yorg);
			
			xminS.setEditor( new JSpinner.NumberEditor(xminS,"#,##0.#####") );
			xmaxS.setEditor( new JSpinner.NumberEditor(xmaxS,"#,##0.#####") );
			yminS.setEditor( new JSpinner.NumberEditor(yminS,"#,##0.#####") );
			ymaxS.setEditor( new JSpinner.NumberEditor(ymaxS,"#,##0.#####") );
			xorgS.setEditor( new JSpinner.NumberEditor(xorgS,"#,##0.#####") );
			yorgS.setEditor( new JSpinner.NumberEditor(yorgS,"#,##0.#####") );
			
			bag.setConstraints( xminS, c );
			bag.setConstraints( xmaxS, c );
			bag.setConstraints( yminS, c );
			bag.setConstraints( ymaxS, c );
			bag.setConstraints( xorgS, c );
			bag.setConstraints( yorgS, c );
			
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.WEST;
			
			bag.setConstraints( computeBest, c );
			
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.weightx = 0;
			
			bag.setConstraints( xminL, c );
			bag.setConstraints( xmaxL, c );
			bag.setConstraints( yminL, c );
			bag.setConstraints( ymaxL, c );
			bag.setConstraints( xorgL, c );
			bag.setConstraints( yorgL, c );

			add( computeBest );
			add( xminL ); add( xminS );
			add( xmaxL ); add( xmaxS );
			add( yminL ); add( yminS );
			add( ymaxL ); add( ymaxS );
			add( xorgL ); add( xorgS );
			add( yorgL ); add( yorgS );
		}
		
		public void computeBest()
		{
			Space s = Space.computeBestSpace( fPane.getFinalFFunction(), fflist.getFunctions() );
			
			xmin.setValue( s.xmin );
			xmax.setValue( s.xmax );
			ymin.setValue( s.ymin );
			ymax.setValue( s.ymax );
			
			xorg.setValue( ( s.xmin + s.xmax ) / 2 );
			yorg.setValue( ( s.ymin + s.ymax ) / 2 );
		}
		
		public void actionPerformed( ActionEvent ae )
		{
			if( ae.getActionCommand().equals("best") )
			{
				computeBest();
			}
		}
	}
	/*
	protected class FinalFunction
		extends JPanel implements ActionListener, FFWizard.FFWizardListener
	{
		private static final long serialVersionUID = 2963296810862126600L;
		
		FFItem ffItem;
		
		public FinalFunction()
		{
			ffItem = new FFItem();
			ffItem.setPreferredSize( new Dimension(200,100) );
			build();
		}
		
		protected void build()
		{
			JButton select = new JButton("select");
			select.setActionCommand("select");
			select.addActionListener(this);
			
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			setLayout(bag);
			
			c.insets = new Insets( 5,5,5,5 );
			c.anchor = GridBagConstraints.WEST;
			c.weightx = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			
			bag.setConstraints(select,c);
			
			c.fill = GridBagConstraints.HORIZONTAL;
			
			bag.setConstraints(ffItem,c);
			
			add( select );
			add( ffItem );
		}
		
		public void actionPerformed( ActionEvent ae )
		{
			if( ae.getActionCommand().equals("select") )
			{
				FFWizard ffw = new FFWizard(frame);
				ffw.addWizardListener(this);
				ffw.setVisible(true);
			}
		}

		public void ffWizardFinished(FFWizard ffw)
		{
			FFunction ff = ffw.getFlameFunction();
			
			if( ff != null )
				ffItem.updateFunction(ff);
			
			revalidate();
			frame.pack();
		}
	}
	*/
	protected class Output
		extends JPanel implements ActionListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 6467016814488004780L;
		
		JSpinner width, height;
		JTextField path;
		JFileChooser jfc = new JFileChooser(".");
		
		public Output()
		{
			build();
		}
		
		public int getOutputWidth()
		{
			return (Integer) width.getValue();
		}
		
		public int getOutputHeight()
		{
			return (Integer) height.getValue();
		}
		
		public String getOutputFileName()
		{
			return path.getText();
		}
		
		protected void build()
		{
			width = new JSpinner( new SpinnerNumberModel( 1280, 1, 5000, 10 ) );
			height = new JSpinner( new SpinnerNumberModel( 720, 1, 5000, 10 ) );
			path = new JTextField(20);
			
			jfc.setFileFilter( new FileNameExtensionFilter( "images files ( *.png, *.jpg )", "png", "jpg" ) );
			
			JButton choose;
			
			choose = new JButton(UIManager.getIcon("FileView.directoryIcon"));
			choose.setActionCommand("choose");
			choose.addActionListener(this);
			
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			setLayout(bag);
			
			c.insets = new Insets( 5, 5, 5, 5 );
			
			JLabel size = new JLabel( "Dimensions:" );
			size.setFont( size.getFont().deriveFont( Font.BOLD ) );
			JLabel widthL = new JLabel( "Width" );
			JLabel heightL = new JLabel( "Height" );
			JLabel file = new JLabel( "File destination:" );
			file.setFont( size.getFont().deriveFont( Font.BOLD ) );
			
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.anchor = GridBagConstraints.WEST;
			
			bag.setConstraints( size, c );
			bag.setConstraints( file, c );
			
			c.gridwidth = 1;//GridBagConstraints.RELATIVE;
			c.gridy = 1;
			
			bag.setConstraints( widthL, c );
			
			c.gridy = 2;
			
			bag.setConstraints( heightL, c );
			
			c.anchor = GridBagConstraints.EAST;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;

			c.gridy = 1;
			
			bag.setConstraints( width, c );

			c.gridy = 2;
			
			bag.setConstraints( height, c );

			c.gridy = 4;
			
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0;
			c.insets = new Insets( 5, 5, 5, 0 );
			
			bag.setConstraints( choose, c );

			c.insets = new Insets( 5, 0, 5, 5 );
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			
			bag.setConstraints( path, c );
			
			add( size );
			add( widthL );
			add( width );
			add( heightL );
			add( height );
			add( file );
			add( choose );
			add( path );
		}
		
		public void actionPerformed( ActionEvent ae )
		{
			if( "choose".equals( ae.getActionCommand() ) )
			{
				if( jfc.showSaveDialog( frame ) == JFileChooser.APPROVE_OPTION )
				{
					path.setText( jfc.getSelectedFile().getAbsolutePath() );
				}
			}
		}
	}
	
	protected class Rendering
		extends JPanel implements ActionListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3011399608237796907L;
		
		DefaultListModel model;
		
		public Rendering()
		{
			model = new DefaultListModel();
			
			build();
		}
		
		public LinkedList<RenderingAction> getRenderingActions()
		{
			LinkedList<RenderingAction> actions = new LinkedList<RenderingAction>();
			
			for( int i = 0; i < model.size(); i++ )
				actions.add( (RenderingAction) model.get(i) );
			
			return actions;
		}
		
		protected void build()
		{
			JButton rotation, translate, computePoints, renderPoints, direct, pushRenderer, pushColorProvider;
			final JList list = new JList( model );
			JScrollPane listScroll = new JScrollPane(list);
			
			list.addKeyListener( new KeyAdapter()
			{
				public void keyPressed( KeyEvent ke )
				{
					switch( ke.getKeyCode() )
					{
					case KeyEvent.VK_DELETE:
						Rendering.this.removeAction( list.getSelectedIndex() );
						break;
					case KeyEvent.VK_DOWN:
						Rendering.this.downAction( list.getSelectedIndex() );
						break;
					case KeyEvent.VK_UP:
						Rendering.this.upAction( list.getSelectedIndex() );
						break;
					}
				}
			});
			
			URL url = ClassLoader.getSystemResource("org/ri2c/flame/gui/rotate.png");
			ImageIcon icon;
			
			if( url != null )
			{
				icon = new ImageIcon(url);
				rotation = new JButton(icon);
			}
			else rotation = new JButton("R");
			
			rotation.setToolTipText("rotation");
			rotation.setActionCommand("rotation");
			rotation.addActionListener(this);
			
			url = ClassLoader.getSystemResource("org/ri2c/flame/gui/translate.png");

			if( url != null )
			{
				icon = new ImageIcon(url);
				translate = new JButton(icon);
			}
			else translate = new JButton("T");
			
			translate.setToolTipText("translation");
			translate.setActionCommand("translate");
			translate.addActionListener(this);
			
			url = ClassLoader.getSystemResource("org/ri2c/flame/gui/compute.png");

			if( url != null )
			{
				icon = new ImageIcon(url);
				computePoints = new JButton(icon);
			}
			else computePoints = new JButton("P");
			
			computePoints.setToolTipText( "compute points" );
			computePoints.setActionCommand("computePoints");
			computePoints.addActionListener(this);
			
			url = ClassLoader.getSystemResource("org/ri2c/flame/gui/render-points.png");

			if( url != null )
			{
				icon = new ImageIcon(url);
				renderPoints = new JButton(icon);
			}
			else renderPoints = new JButton("RP");
			
			renderPoints.setToolTipText( "render points" );
			renderPoints.setActionCommand("renderPoints");
			renderPoints.addActionListener(this);
			
			url = ClassLoader.getSystemResource("org/ri2c/flame/gui/direct.png");

			if( url != null )
			{
				icon = new ImageIcon(url);
				direct = new JButton(icon);
			}
			else direct = new JButton("D");
			
			direct.setToolTipText( "direct rendering" );
			direct.setActionCommand("direct");
			direct.addActionListener(this);
			
			url = ClassLoader.getSystemResource("org/ri2c/flame/gui/renderer.png");

			if( url != null )
			{
				icon = new ImageIcon(url);
				pushRenderer = new JButton(icon);
			}
			else pushRenderer = new JButton("SR");
			
			pushRenderer.setToolTipText( "set renderer type" );
			pushRenderer.setActionCommand("setRenderer");
			pushRenderer.addActionListener(this);
			
			url = ClassLoader.getSystemResource("org/ri2c/flame/gui/color-provider.png");

			if( url != null )
			{
				icon = new ImageIcon(url);
				pushColorProvider = new JButton(icon);
			}
			else pushColorProvider = new JButton("SCP");
			
			pushColorProvider.setToolTipText( "set color provider type" );
			pushColorProvider.setActionCommand("setColorProvider");
			pushColorProvider.addActionListener(this);
			
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			setLayout(bag);
			
			c.insets = new Insets( 5,5,5,5 );
			
			c.gridy = 0;
			c.gridwidth = 1;
			
			bag.setConstraints( rotation, c );
			bag.setConstraints( translate, c );
			bag.setConstraints( computePoints, c );
			bag.setConstraints( renderPoints, c );
			bag.setConstraints( direct, c );
			
			c.gridy++;
			
			bag.setConstraints( pushRenderer, c );
			bag.setConstraints( pushColorProvider, c );
			
			c.gridy++;
			c.gridwidth = 6;
			c.fill = GridBagConstraints.BOTH;
			
			bag.setConstraints( listScroll, c );
			
			add( rotation );
			add( translate );
			add( computePoints );
			add( renderPoints );
			add( direct );
			add( pushRenderer );
			add( pushColorProvider );
			add( listScroll );
		}
		
		public void actionPerformed( ActionEvent ae )
		{
			if( "rotation".equals(ae.getActionCommand()) )
			{
				addNewRenderingAction( RenderingAction.createRotateAction() );
			}
			else if( "translate".equals(ae.getActionCommand()) )
			{
				addNewRenderingAction( RenderingAction.createTranslateAction() );
			}
			else if( "computePoints".equals(ae.getActionCommand()) )
			{
				addNewRenderingAction( RenderingAction.createGeneratePointsAction() );
			}
			else if( "renderPoints".equals(ae.getActionCommand()) )
			{
				addNewRenderingAction( RenderingAction.createRenderPointsAction() );
			}
			else if( "setRenderer".equals(ae.getActionCommand()) )
			{
				addNewRenderingAction( RenderingAction.createSetRendererAction() );
			}
			else if( "setColorProvider".equals(ae.getActionCommand()) )
			{
				addNewRenderingAction( RenderingAction.createSetColorProviderAction() );
			}
		}
		
		protected void addNewRenderingAction( RenderingAction ra )
		{
			if( ra != null )
				model.addElement(ra);
		}
		
		protected void removeAction( int index )
		{
			if( index >= 0 && index < model.size() )
				model.remove(index);
		}
		
		protected void downAction( int index )
		{
			if( index >= 0 && index < model.size() - 1 )
				model.add( index + 1, model.remove(index) );
		}
		
		protected void upAction( int index )
		{
			if( index > 0 && index < model.size() )
				model.add( index - 1, model.remove(index) );
		}
	}
	
	protected class Compute
		extends JPanel implements ActionListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4360414976078078569L;

		JButton fractalIt;
		GridBagConstraints c;
		JPanel pending;
		
		public Compute()
		{
			build();
		}
		
		protected void build()
		{
			fractalIt = new JButton("generate fractal");
			fractalIt.setActionCommand("generate");
			fractalIt.addActionListener(this);
			
			URL url = ClassLoader.getSystemResource("org/ri2c/flame/gui/compute-32.png");
			ImageIcon icon = null;
			
			if( url != null )
				icon = new ImageIcon(url);
			
			if( icon != null )
				fractalIt.setIcon(icon);

			GridBagLayout bag = new GridBagLayout();
			c = new GridBagConstraints();
			
			setLayout(bag);
			
			c.insets = new Insets( 5,5,5,5 );
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1;
			
			bag.setConstraints( fractalIt, c );
			
			pending = new JPanel();
			
			JScrollPane scrollPending = new JScrollPane(pending);
			scrollPending.setBorder( BorderFactory.createTitledBorder("pending...") );
			
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			
			bag.setConstraints( scrollPending, c );
		
			add( fractalIt );
			add( scrollPending );
			
			c.weighty = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
		}
		
		public void compute()
		{
			int width, height;
			Discretiser discretiser;
			RenderingContext rctx;
			Context ctx;
			LinkedList<RenderingAction> actions;
			
			width = output.getOutputWidth();
			height = output.getOutputHeight();
			
			discretiser = space.createDiscretiser(width, height);
			
			ctx = new Context();
			ctx.setInitialPoint( space.getXorg(), space.getYorg() );
			
			for( FFunction ff: fflist.getFunctions() )
				ctx.addFlameFunction(ff);
			
			rctx = new RenderingContext(ctx,output.getOutputFileName(),discretiser);
			
			actions = rendering.getRenderingActions();
			/*
			for( RenderingAction ra: rendering.getRenderingActions() )
				ra.render(rctx);
			
			rctx.output();
			*/
			ComputeThread ct = new ComputeThread(rctx,actions);
			( (GridBagLayout) getLayout() ).setConstraints( ct, c );
			pending.add(ct);
			revalidate();
			repaint();
			
			Thread t = new Thread(ct);
			t.start();
		}
		
		public void actionPerformed( ActionEvent ae )
		{
			if( "generate".equals(ae.getActionCommand()) )
			{
				compute();
			}
		}
	}
	
	protected class ComputeThread
		extends JProgressBar implements Runnable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 540561962620076377L;
		
		RenderingContext ctx;
		LinkedList<RenderingAction> actions;
		
		public ComputeThread( RenderingContext ctx, LinkedList<RenderingAction> actions )
		{
			super( 0, actions.size() + 1);
			
			setStringPainted(true);
			
			this.ctx = ctx;
			this.actions = actions;
		}
		
		public void run()
		{
			for( int i = 0; i < actions.size(); i++)
			{
				RenderingAction ra = actions.get(i);
				
				final String str = ra.toString();
				final int val = i + 1;
				
				SwingUtilities.invokeLater( new Runnable()
				{
					public void run()
					{
						setValue( val );
						setString( str );
						repaint();
					}
				});
			
				ra.render(ctx);
			}

			SwingUtilities.invokeLater( new Runnable()
			{
				public void run()
				{
					setValue( ComputeThread.this.getMaximum() );
					setString( "output fractal" );
					
					ctx.output();
					
					if( getParent() != null )
					{
						Container parent = getParent();
						parent.remove(ComputeThread.this);
						parent.repaint();
					}
				}
			});
		}
	}
	
	FFList fflist;
	JFrame frame;
	
	FunctionPane 	fPane;
	SpaceSelector 	space;
	//FinalFunction 	fff;
	Output			output;
	Rendering		rendering;
	
	Compute compute;
	
	public FFGui()
	{
		fflist = new FFList();
		
		build();
		frame.setVisible(true);
	}
	
	protected void build()
	{
		GridBagLayout bag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(bag);
		
		fPane = new FunctionPane();
		fPane.setBorder( BorderFactory.createTitledBorder( "Functions" ) );
		
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		
		bag.setConstraints(fPane,c);
		add( fPane );
		
		space = new SpaceSelector();
		space.setBorder( BorderFactory.createTitledBorder("space") );
		
		rendering = new Rendering();
		rendering.setBorder( BorderFactory.createTitledBorder( "rendering actions" ) );
		
		c.gridheight = GridBagConstraints.RELATIVE;
		c.fill = GridBagConstraints.BOTH;
		//c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 1;
		
		bag.setConstraints(space,c);
		
		c.gridheight = GridBagConstraints.REMAINDER;
		
		bag.setConstraints(rendering,c);
		
		add(space);
		add(rendering);
		
		output = new Output();
		output.setBorder( BorderFactory.createTitledBorder("output"));
		
		c.gridx = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = GridBagConstraints.RELATIVE;
		
		bag.setConstraints( output, c );
		
		add( output );
		
		compute = new Compute();
		
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		
		bag.setConstraints( compute, c );
		
		add( compute );
		
		frame = new JFrame("FFractal");
		frame.setContentPane(this);
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.pack();
	}
	
	public static void main( String ... args )
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

	    SwingUtilities.invokeLater(new Runnable() {
	      public void run() {
	    	  SubstanceLookAndFeel.setSkin("org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin");
	    	  new FFGui();
	      }
	    });
	}
}
