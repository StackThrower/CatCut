package catcut.net.listener;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import catcut.net.helpers.KeyboardHelper;

public class KeyboardEnterListener implements View.OnKeyListener,
        View.OnFocusChangeListener{


    Activity activity;

    public KeyboardEnterListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {

            KeyboardHelper.hideKeyBoard(v, activity);
            return true;
        }
        return false;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if (!hasFocus) {
            KeyboardHelper.hideKeyBoard(v, activity);
        }
    }
}

