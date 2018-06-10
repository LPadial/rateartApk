package tfg.app.laurapadial.rateart;

import android.support.v7.app.AppCompatActivity;

class HomeActivity extends AppCompatActivity {
    private static final HomeActivity ourInstance = new HomeActivity();

    static HomeActivity getInstance() {
        return ourInstance;
    }

    private HomeActivity() {
    }
}
