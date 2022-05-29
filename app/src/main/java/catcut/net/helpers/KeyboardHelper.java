package catcut.net.helpers;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardHelper {

    static public void hideKeyBoard(View view, Activity activity) {
        InputMethodManager imm = (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
