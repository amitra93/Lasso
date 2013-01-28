package com.narabhut.lasso;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

public class TargetFinderTextView extends AutoCompleteTextView{
   

	public TargetFinderTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode==KeyEvent.KEYCODE_ENTER) 
        {
            // Just ignore the [Enter] key
            return true;
        }
        // Handle all other keys in the default way
        return super.onKeyDown(keyCode, event);
    }
}