package de.mbaaba.tool.pw.detectors;

import java.awt.Point;

import junit.framework.TestCase;

import org.junit.Test;

import de.mbaaba.tool.pw.detectors.AbstractActivityDetector.Activity;


public class MouseMoveActivityDetectorTest extends TestCase {
	private Activity expectedActivity;
	private int currentStep;
	protected int activityChanges = 0;
	
	
	class MyMouseMoveActivityDetector extends MouseMoveActivityDetector {

		public MyMouseMoveActivityDetector(long aMinActivityTime, long aInactivityTime) {
			super(aMinActivityTime, aInactivityTime);
		}

		@Override
		protected void setActivity(Activity aActivity) {
			activityChanges++;
			assertEquals("Unexpected activity state after "+currentStep, expectedActivity, aActivity);
			
		}
	};

	
	@Test
	public void testDetection() throws InterruptedException {
		activityChanges = 0;
		MyMouseMoveActivityDetector mouseMoveActivityDetector = new MyMouseMoveActivityDetector(100,100);
		
		Point newMousePos = new Point(0,0);
		expectedActivity = null;
		currentStep = 0;
		mouseMoveActivityDetector.detect(newMousePos);

		Thread.sleep(50);
		newMousePos = new Point(1,0);
		expectedActivity = null;
		currentStep++;
		mouseMoveActivityDetector.detect(newMousePos);
		
		Thread.sleep(30);
		newMousePos = new Point(2,0);
		expectedActivity = null;
		currentStep++;
		mouseMoveActivityDetector.detect(newMousePos);
		
		Thread.sleep(50);
		newMousePos = new Point(3,0);
		expectedActivity = Activity.WORKING;
		currentStep++;
		mouseMoveActivityDetector.detect(newMousePos);
		
		
		
		Thread.sleep(150);
		newMousePos = new Point(3,0);
		expectedActivity = Activity.IDLE;
		currentStep++;
		mouseMoveActivityDetector.detect(newMousePos);
		
		newMousePos = new Point(4,0);
		expectedActivity = Activity.WORKING;
		currentStep++;
		mouseMoveActivityDetector.detect(newMousePos);
		
		Thread.sleep(110);
		newMousePos = new Point(4,0);
		expectedActivity = Activity.IDLE;
		currentStep++;
		mouseMoveActivityDetector.detect(newMousePos);
		
		assertEquals("Wrong number of activity changes!", 4, activityChanges);
	}

	
	@Test
	public void testStartupAfterHibernateDetection() throws InterruptedException {
		// expect no changes at all if just one short activity is detected at the beginning
		activityChanges = 0;

		MyMouseMoveActivityDetector mouseMoveActivityDetector = new MyMouseMoveActivityDetector(100,100);

		Point newMousePos = new Point(1,0);
		expectedActivity = null;
		currentStep = 0;
		mouseMoveActivityDetector.detect(newMousePos);

		Thread.sleep(50);
		newMousePos = new Point(2,0);
		expectedActivity = null;
		currentStep++;
		mouseMoveActivityDetector.detect(newMousePos);
		
		assertEquals("Wrong number of activity changes!", 0, activityChanges);
	}
	
}
