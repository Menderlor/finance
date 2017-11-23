package com.cedarhd.control;

import android.view.View;

import com.cedarhd.control.ScrollDetectors.ScrollDetector;

public interface ScrollDetectorFactory {
	/**
	 * Create new instance of {@link ScrollDetector} based on the parameter v
	 * 
	 * @param v
	 * @return
	 */
	public ScrollDetector newScrollDetector(View v);
}
