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

import org.ri2c.flame.FFunction;
import org.ri2c.flame.FFunction.FFVariation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FFWizard
	extends JDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2990462791621588331L;

	protected static final int MODE_RANDOM = 1;
	protected static final int MODE_MANUAL = 2;

	public static interface FFWizardListener
	{
		void ffWizardFinished( FFWizard ffw );
	}
	
	protected abstract class Step
	{
		String message;
		
		public Step( String message )
		{
			this.message = message;
		}

		public void install()
		{
			FFWizard.this.message.setText( message );
			
			FFWizard.this.content.removeAll();
			FFWizard.this.content.repaint();
			buildContent();
			
			restore();
		}
		
		public abstract void buildContent();
		public abstract void save();
		public abstract void restore();
	}

	static int count = 0;
	
	protected class InitialStep
		extends Step implements ChangeListener
	{
		JComboBox box;
		JTextField name;
		JSlider color;
		
		public InitialStep()
		{
			super( "This is the new flame function creation wizard.\nChoose the creation way:" );
			
			name = new JTextField(20);
			name.setBorder( BorderFactory.createTitledBorder("function name") );
			name.setOpaque(false);
			name.setText( String.format( "function#%d", count++ ) );
			
			String [] mode = { "random", "manual" };
			box = new JComboBox(mode);
			
			color = new JSlider(0,360,random.nextInt(360));
			color.addChangeListener(this);
			color.setBorder( BorderFactory.createTitledBorder("function color") );
		}

		public void restore()
		{
			if( FFWizard.this.ff_name != null )
				name.setText( FFWizard.this.ff_name );
			
			if( FFWizard.this.ff_random )
				box.setSelectedItem("random");
			else
				box.setSelectedItem("manual");
		}
		
		public void buildContent()
		{
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			FFWizard.this.content.setLayout(bag);
			
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weighty = 1;
			
			bag.setConstraints( name, c );
			content.add( name );
			
			bag.setConstraints( box, c );
			content.add( box );
			
			bag.setConstraints( color, c );
			content.add( color );
			
			stateChanged(null);
		}
		
		public void save()
		{
			FFWizard.this.setTitle( String.format( "\"%s\" creation", name.getText() ) );
			FFWizard.this.ff_name = name.getText();
			FFWizard.this.ff_random = box.getSelectedItem().equals("random");
			FFWizard.this.ff_color = color.getValue() / 360.0;
		}

		public void stateChanged(ChangeEvent e)
		{
			color.setBackground( Color.getHSBColor( color.getValue() / 360.0f, 1, 1 ) );
		}
	}
	
	protected class ConfigureFunctionStep
		extends Step
	{
		JSpinner [] wabcdef;
		
		public ConfigureFunctionStep( )
		{
			super( "Enter function parameters.\nF(x,y) = w.sum( Vi( ax+by+c, dx+ey+f ) )" );
			
			wabcdef = new JSpinner [7];
			
			double [] def = { 1, 1, 0, 0, 0, 1, 0 };
			
			for( int i = 0; i < 7; i++ )
				wabcdef [i] = new JSpinner( new SpinnerNumberModel(def[i],-10,10,0.001) );
		}
		
		public void restore()
		{
			if( FFWizard.this.ff_random )
				randomize();
			else if( FFWizard.this.ff_data != null )
			{
				wabcdef [0] .setValue( FFWizard.this.ff_w );
				
				for( int i = 0; i < 6; i++ )
					wabcdef [i+1] .setValue( FFWizard.this.ff_data [i] );
			}
		}
		
		public void buildContent()
		{
			//JPanel panel = new JPanel();
			
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			FFWizard.this.content.setLayout(bag);
			
			JLabel label;
			
			String labels = "wabcdef";
			
			c.ipadx = 10;
			c.ipady = 10;
			
			for( int i = 0; i < 7; i++ )
			{
				label = new JLabel( labels.substring(i,i+1) );
				c.weightx = 0;
				c.fill = GridBagConstraints.NONE;
				c.gridwidth = GridBagConstraints.RELATIVE;
				bag.setConstraints(label,c);
				FFWizard.this.content.add(label);
				
				c.weightx = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridwidth = GridBagConstraints.REMAINDER;
				bag.setConstraints(wabcdef[i],c);
				FFWizard.this.content.add(wabcdef[i]);
			}
		}
		
		public void save()
		{
			FFWizard.this.ff_w = (Double) wabcdef [0] .getValue();
			FFWizard.this.ff_data = new double [6];

			for( int i = 0; i < 6; i++ )
				FFWizard.this.ff_data [i] = (Double) wabcdef [i+1] .getValue();
		}
		
		public void randomize()
		{
			Random r = new Random();
			
			wabcdef [0] .setValue( r.nextDouble() * 2 );
			wabcdef [1] .setValue( r.nextDouble() * 4 - 2 );
			wabcdef [2] .setValue( r.nextDouble() * 4 - 2 );
			wabcdef [3] .setValue( r.nextDouble() * 4 - 2 );
			wabcdef [4] .setValue( r.nextDouble() * 4 - 2 );
			wabcdef [5] .setValue( r.nextDouble() * 4 - 2 );
			wabcdef [6] .setValue( r.nextDouble() * 4 - 2 );
		}
	}
	
	protected class ChooseVariationsStep
		extends Step implements ActionListener
	{
		JSpinner weight;
		VariationsComboBox vlist;
		DefaultListModel vchoose;
		
		public ChooseVariationsStep( )
		{
			super( "Choose variations used in the flame function." );
		}

		public void buildContent()
		{
			vlist = new VariationsComboBox();
			weight = new JSpinner( new SpinnerNumberModel(1,0,1,0.001) );
			
			JButton add = new JButton("add");
			add.setActionCommand("add");
			add.addActionListener(this);
			
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			
			FFWizard.this.content.setLayout(bag);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weighty = 0;
			c.weightx = 1;
			//c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			bag.setConstraints( vlist, c );
			
			c.insets = new Insets( 5, 0, 0, 5 );
			c.fill = GridBagConstraints.NONE;
			c.weightx = 1;
			c.gridwidth = 1;
			//c.gridy = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			
			weight.setBorder( BorderFactory.createTitledBorder( "weight" ) );
			
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.ipadx = 30;
			bag.setConstraints( weight, c );
			c.ipadx = 0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			bag.setConstraints( add, c );
			
			vchoose = new DefaultListModel();
			JList list = new JList(vchoose);
			JScrollPane scrollList = new JScrollPane(list);
			
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			bag.setConstraints(scrollList,c);
			
			FFWizard.this.content.add( vlist );
			FFWizard.this.content.add( weight );
			FFWizard.this.content.add( add );
			FFWizard.this.content.add( scrollList );
		}
		
		public void save()
		{
			ff_variations.clear();
			
			for( int i = 0; i < vchoose.getSize(); i++ )
				ff_variations.add( (FFVariation) vchoose.getElementAt(i) );
		}
		
		public void restore()
		{
			for( FFVariation ffv: ff_variations )
				vchoose.addElement(ffv);
		}

		public void actionPerformed(ActionEvent e)
		{
			if( e.getActionCommand().equals("add") )
			{
				try
				{
					FFVariation ffv = new FFVariation( (Double) weight.getValue(), vlist.getSelectedVariation().newInstance() );
					vchoose.addElement( ffv );
				}
				catch (InstantiationException e1)
				{
					e1.printStackTrace();
				}
				catch (IllegalAccessException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}
	
	protected class PreviewStep
		extends Step
	{
		public PreviewStep()
		{
			super( "Preview of your function" );
		}

		public void buildContent()
		{
			FFunction ff = new FFunction(ff_color,ff_data);
			
			for( FFVariation ffv: ff_variations )
				ff.addVariation(ffv);
			
			FFPreview ffp = new FFPreview( ff, 300, 300, 20 );
			JLabel preview = new JLabel(ffp);
			
			FFWizard.this.content.add( preview );
		}

		public void restore()
		{
			
		}

		public void save() {
			
		}
	}

	public static final int CANCELLED = 1;
	public static final int FINISHED = 2;
	
	String 					ff_name;
	boolean					ff_random;
	double					ff_color;
	double 					ff_w;
	double [] 				ff_data;
	LinkedList<FFVariation>	ff_variations;
	
	JPanel buttons;
	JPanel content;
	JTextPane message;
	LinkedList<Step>	steps;
	int currentStep;
	int mode;
	JButton cancel, previous, next, finish;
	int state;
	Random random = new Random();
	
	LinkedList<FFWizardListener> listeners;

	public FFWizard()
	{
		this(null);
	}
	
	public FFWizard( Frame f )
	{
		super( f, f != null );
		
		setTitle( "New Flame Function" );
		setResizable(false);

		steps = new LinkedList<Step>();
		steps.add( new InitialStep() );
		steps.add( new ConfigureFunctionStep() );
		steps.add( new ChooseVariationsStep() );
		steps.add( new PreviewStep() );

		buildGlobal();
		
		currentStep = -1;
		nextStep();
		
		ff_variations = new LinkedList<FFVariation>();
		
		listeners = new LinkedList<FFWizardListener>();
	}

	protected void nextStep()
	{
		if( currentStep >= 0 )
			steps.get(currentStep).save();
		
		currentStep++;
		
		if( currentStep < steps.size() )
			steps.get(currentStep).install();
		
		previous.setEnabled(currentStep > 0);
		next.setEnabled(currentStep < steps.size() - 1 );
		finish.setEnabled(currentStep == steps.size() - 1 );
		
		pack();
	}
	
	protected void previousStep()
	{
		if( currentStep < steps.size() )
			steps.get(currentStep).save();
		
		currentStep--;
		
		if( currentStep >= 0 )
			steps.get(currentStep).install();
		
		previous.setEnabled(currentStep > 0);
		next.setEnabled(currentStep < steps.size() - 1 );
		finish.setEnabled(currentStep == steps.size() - 1 );
		
		pack();
	}
	
	protected void buildGlobal()
	{
		GridBagLayout bag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		setLayout( bag );//new BorderLayout() );

		URL leftImg = ClassLoader.getSystemResource("org/ri2c/flame/gui/wizard-left.png");

		if( leftImg != null )
		{
			c.gridheight = 3;
			//c.fill = GridBagConstraints.BOTH;

			ImageIcon iiLeft = new ImageIcon(leftImg);
			JLabel iiLeftLabel = new JLabel(iiLeft);

			iiLeftLabel.setBorder( BorderFactory.createMatteBorder(0,0,0,1,Color.GRAY) );

			bag.setConstraints( iiLeftLabel, c );
			add( iiLeftLabel );
		}
		else System.err.printf("cant find resource\n");

		Insets insets = new Insets( 10, 20, 10, 20 );

		c.insets = insets;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;

		message = new JTextPane();
		message.setOpaque(false);
		message.setEditable(false);

		bag.setConstraints(message,c);
		add(message);

		content = new JPanel();
		content.setPreferredSize( new Dimension(300,300) );
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		bag.setConstraints(content,c);
		add(content);

		buttons = new JPanel();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		bag.setConstraints(buttons,c);
		add(buttons);		
		
		cancel = new JButton( "cancel" );
		previous = new JButton("previous");
		next = new JButton("next");
		finish = new JButton("finish");
		
		cancel.addActionListener(this);
		previous.addActionListener(this);
		next.addActionListener(this);
		finish.addActionListener(this);
		
		cancel.setActionCommand("cancel");
		previous.setActionCommand("previous");
		next.setActionCommand("next");
		finish.setActionCommand("finish");
		
		buttons.add( cancel );
		buttons.add( previous );
		buttons.add( next );
		buttons.add( finish );
	}
	
	public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand().equals("cancel") )
		{
			cancel();
		}
		else if( e.getActionCommand().equals("next") )
		{
			nextStep();
		}
		else if( e.getActionCommand().equals("previous") )
		{
			previousStep();
		}
		else if( e.getActionCommand().equals("finish") )
		{
			finish();
		}
	}
	
	public void cancel()
	{
		state = CANCELLED;
		
		setVisible(false);
		
		synchronized(this)
		{
			notifyAll();
		}
	}
	
	public void finish()
	{
		state = FINISHED;
		
		setVisible(false);
		
		for( FFWizardListener l: listeners )
			l.ffWizardFinished(this);
	}

	public FFunction getFlameFunction()
	{
		FFunction ff = new FFunction(ff_color,ff_data);
		
		for( FFVariation ffv: ff_variations )
			ff.addVariation(ffv);
		
		ff.setName(ff_name);
		ff.setWeight(ff_w);
		
		return ff;
	}
	
	public void addWizardListener( FFWizardListener listener )
	{
		listeners.add(listener);
	}
	
	public void removeWizardListener( FFWizardListener listener )
	{
		listeners.remove(listener);
	}
	
	public static FFunction createNewFlameFunction()
	{
		FFWizard ffw = new FFWizard();
		
		ffw.setVisible(true);
		
		synchronized(ffw)
		{
			try {
				ffw.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		if( ffw.state == FINISHED )
			return ffw.getFlameFunction();
		else return null;
	}
	
	public static void main( String ... args )
	{
		//FFWizard ffw = new FFWizard();
		//ffw.setVisible(true);
		
		createNewFlameFunction();
	}
}
