package catcut.net;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PolicyPrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_privacy);

        TextView textElement = findViewById(R.id.policy_privacy);
        textElement.setText(Html.fromHtml(getString(R.string.privacy_policy_text)));
        textElement.setMovementMethod(new ScrollingMovementMethod());
    }
}
