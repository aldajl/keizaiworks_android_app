package keizai.works.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
 
public class LoginActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
 
        Button testLogin = (Button) findViewById(R.id.login_btn);
 
        // Listening to register new account link
        testLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), LoadingActivity.class);
                startActivity(i);
            }
        });
    }
}