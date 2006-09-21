package org.vast.stt.gui.widgets.time;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

/**
 * <p><b>Title:</b><br/>
 * TimeSpinner
 * </p>
 *
 * <p><b>Description:</b><br/>
 * GUI for holding a Text widget and two arrow buttons to allow time step/bias/lead/lag
 * selection.  Arrow buttons may be pressed and held to simulate spinner behavior.  
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Tony Cook
 * @date Dec 19, 2005
 * @version 1.0
 */
public class TimeSpinner 
	implements TraverseListener, MouseListener, KeyListener, DisposeListener, FocusListener
{
	Group mainGroup;
	StyledText text;
	String formatStr;
	Font entryFont;
	Button upBtn, downBtn;
	TimeSpinnerModel tsModel;
	boolean btnDown = false;  //  Used to indicate user is pressing and holding a spinner button
	// List timeListeners;
	final Color DARK_GRAY = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
	final Color GRAY = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GRAY);
	Color activeBackground = null;
	List<TimeListener> timeListeners;
	
	protected TimeSpinner(){
		//  Added so that CurrentTimeSpinner can extend this class
		initFont();
		timeListeners = new ArrayList<TimeListener>();
	}
	
	public TimeSpinner(Composite parent, String label) {
		this();
		formatStr = "YYYY DDD HH:mm:SS";
		tsModel = new TimeSpinnerModel("YYYY DDD HH:mm:SS");
		initGui(parent, label);
		//  Should really generate initial data from formatStr, but I don't 
		//  think it will use this anyway.  In practice, setValue(double) will
		//  be called at initialization
		text.setText("0000 000 00:00:00");
		text.setCaretOffset(13);
		tsModel.selectField(text);
	}
	
	public void setLayoutData(GridData gridData){
		mainGroup.setLayoutData(gridData);
	}
	
	protected void initFont(){
		Display display = PlatformUI.getWorkbench().getDisplay();
		entryFont = new Font(display, new FontData("arial", 9, SWT.NORMAL));
	}
	
	protected void initGui(Composite parent, String label){
		//  Create main group for text and Buttons
		mainGroup = new Group(parent, SWT.SHADOW_NONE);
		mainGroup.setText(label);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginTop = -10;
		gridLayout.marginBottom = -2;
		gridLayout.marginRight = -3;
		mainGroup.setLayout(gridLayout);
		mainGroup.addDisposeListener(this);
		//  Text widget		
		text = new StyledText(mainGroup, SWT.RIGHT | SWT.BORDER | SWT.READ_ONLY);
		GridData gridData = new GridData();
		//gridData.heightHint = 18;
		text.setLayoutData(gridData);
		text.setFont(entryFont);
		text.setToolTipText(formatStr);
		activeBackground = text.getBackground();
		
		text.addTraverseListener(this);
		text.addMouseListener(this);
		text.addKeyListener(this);

		// SpinnerGroup
		Composite spinnerGroup = new Composite(mainGroup, SWT.SHADOW_NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 1;
		gridLayout.marginTop = -2;
		gridLayout.marginBottom = -5;
		spinnerGroup.setLayout(gridLayout);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.CENTER;
		spinnerGroup.setLayoutData(gridData);
		//  Spinner buttons
		upBtn = new Button(spinnerGroup, SWT.ARROW | SWT.UP);
		gridData = new GridData();
		gridData.heightHint = 15;
    	upBtn.setLayoutData(gridData);
    	upBtn.addMouseListener(this);
    	upBtn.addFocusListener(this);
		downBtn = new Button(spinnerGroup, SWT.ARROW | SWT.DOWN);
		gridData = new GridData();
		gridData.heightHint = 15;
		downBtn.setLayoutData(gridData);
    	downBtn.addMouseListener(this);
    	downBtn.addFocusListener(this);
	}

	public void setEnabled(boolean b){
		mainGroup.setEnabled(b);
		if(b)
			text.setBackground(activeBackground);
		else 
			text.setBackground(DARK_GRAY);
			//text.setBackground(STTColorManager.getInstance().get("textBackground"));
	}

	private void timeUp(){
		int caretPos = text.getCaretOffset();
		tsModel.increment();
		text.setText(tsModel.toString());
		text.setCaretOffset(caretPos);
		tsModel.selectField(text);
		publishTimeChanged();
	}
	
	private void timeDown(){
		int caretPos = text.getCaretOffset();
		tsModel.decrement();
		text.setText(tsModel.toString());
		text.setCaretOffset(caretPos);
		tsModel.selectField(text);
		publishTimeChanged();
	}
	
	private void publishTimeChanged(){
		TimeListener tsTmp = null;
		double t = getValue();
		for(int i=0; i<timeListeners.size(); i++){
			tsTmp = timeListeners.get(i);
			tsTmp.timeChanged(this, t);
		}
	}

	public void addTimeListener(TimeListener tl){
		timeListeners.add(tl);
	}
	
	public void removeTimeListener(TimeListener tl){
		timeListeners.remove(tl);
	}
	
	Runnable spinUpThread = new Runnable(){
		public void run(){
			timeUp();
		}
	};
	
	Runnable spinDownThread = new Runnable(){
		public void run(){
			timeDown();
		}
	};

	private void startSpinUpThread(){
		if(btnDown)
			return;
		btnDown = true;
		timeUp();
		SpinThread spinThread = new SpinThread(spinUpThread);
		spinThread.start();
	}

	private void startSpinDownThread(){
		if(btnDown)
			return;
		btnDown = true;
		timeDown();
		SpinThread spinThread = new SpinThread(spinDownThread);
		spinThread.start();

	}

	class SpinThread extends Thread {
		Runnable childThread;
		
		SpinThread(Runnable child){
			childThread = child;
		}
		
		public void run(){
			try {		
				Thread.sleep(300l);  //  make this less sensitive (mouseEvents seem to be getting queued here)
				while(btnDown){
					Thread.sleep(60l);
					if(!text.isDisposed()) {
						text.getDisplay().asyncExec(childThread);
					}
				}	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.err.println("");
		}
		
	}
	
	private void stopSpinThread(){
		btnDown = false;
		text.setFocus();
		//System.err.println("Value is " + tsModel.getValue());
		//System.err.println("stopUpThread");
	}

	public void setValue(double value){
		tsModel.setValue(new Double(value));
		text.setText(tsModel.toString());
		tsModel.selectField(text);
	}
	
	public double getValue(){
		Double val = (Double)tsModel.getValue();
		return val.doubleValue();
	}
	
	public void resetCaret(){
		tsModel.resetCaret(text);
	}
	
	/**
	 * Override behavior of up-down-left-right arrow keys
	 */
	public void keyTraversed(TraverseEvent e) {
		if(e.keyCode == SWT.ARROW_DOWN) {
			timeDown();
		} else if (e.keyCode == SWT.ARROW_UP) {
			timeUp();
		} else {
			tsModel.selectField(text);
		}
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		//  do nothing...
	}

	public void mouseDown(MouseEvent e) {
		if(e.widget == upBtn) {
			startSpinUpThread();
		} else if(e.widget == downBtn) {
			startSpinDownThread();
		}
	}


	public void mouseUp(MouseEvent e) {
		btnDown = false;
		if(e.widget == upBtn || e.widget == downBtn) {
			stopSpinThread();
		} else  // e.widget == text
			tsModel.selectField(text);
	}
	
	public void keyPressed(KeyEvent e) {
		if((e.keyCode<48 || e.keyCode > 57)) { // && e.keyCode!=0) {
			//System.err.println("NOT A NUMBER");
			//e.doit = false;
			return;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	/**
	 * verifyText - only allow numeric entry for this
	 */
	public void verifyText(VerifyEvent e) {
		System.err.println("** Verified Text is " + e);
		e.doit = false;
		if((e.keyCode<48 || e.keyCode > 57) && e.keyCode!=0) {
			System.err.println("NOT A NUMBER");
			e.doit = false;
		}
	}

	/**
	 *  Added dispose listener to dispose Font I use for the text fields.  If we 
	 *  implement a FontRegistry, I can get rid of this.  TC 
	 * @param e
	 */
	public void widgetDisposed(DisposeEvent e) {
		entryFont.dispose();
	}

	public void focusGained(FocusEvent e) {
//		System.err.println(" *** Focus Gained");
	}

	public void focusLost(FocusEvent e) {
//		System.err.println(" *** Focus Lost");
		stopSpinThread();
	}
	
}
