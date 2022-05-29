package catcut.net.helpers;

import android.content.Context;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import catcut.net.R;


public class NetworkExceptionUI {

    public static void showMessageNoInternetConnection(final Context context, final String description) {

        showMessage(context, description);
    }


    public static void showMessageInternalError(final Context context) {
        showMessage(context, context.getString(R.string.message_internal_error));
    }


    private static void showMessage(final Context context, String message) {
        try {
            if (context != null) {
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(context, message,
                                Toast.LENGTH_LONG).show();
                    }
                });

            }

        } catch (Exception e) {

        }
    }

}
