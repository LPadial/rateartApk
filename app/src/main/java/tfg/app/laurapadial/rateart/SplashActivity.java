package tfg.app.laurapadial.rateart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import tfg.app.laurapadial.rateart.Home.HomeActivity;

public class SplashActivity extends Activity {
    private final int DURACION_SPLASH = 2000;

    public SharedPreferences sharedPref;
    public SharedPreferences.Editor editor;

    public String baseUrl;
    public String url;
    public String email;
    public String password;

    public RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        requestQueue = Volley.newRequestQueue(this);

        initPreferences();
    }

    public void login(String email, String password){

        try{
            this.url = baseUrl + "user/login";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email",email);
            jsonBody.put("password",password);
            jsonBody.put("gethash", "true");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    new Handler().postDelayed(new Runnable(){
                        public void run(){
                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        };
                    }, DURACION_SPLASH);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    onErrorLogin();
                }
            }){
                @Override
                public String getBodyContentType(){
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    }catch (UnsupportedEncodingException uee){
                        VolleyLog.wtf("Unsuported encoding while trying to get bytes of %s using %s", requestBody, "utf8");
                        return  null;
                    }
                }
            };
            requestQueue.add(stringRequest);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public void initPreferences(){
        sharedPref= getSharedPreferences("rateart", 0);
        this.baseUrl = "http://51.38.237.252:3000/rateart_backend/";

        if(sharedPref.getString("token","")!=null) {
            if(sharedPref.getString("email","")!=null && sharedPref.getString("password","")!=null) {
                this.email = sharedPref.getString("email","");
                this.password = sharedPref.getString("password","");
                login(this.email,this.password);
            }else {
                new Handler().postDelayed(new Runnable(){
                    public void run(){
                        Intent intent = new Intent(SplashActivity.this, StartupActivity.class);
                        startActivity(intent);
                        finish();
                    };
                }, DURACION_SPLASH);
            }
        }
    }

    public void onErrorLogin(){
        new Handler().postDelayed(new Runnable(){
            public void run(){
                Intent intent = new Intent(SplashActivity.this, StartupActivity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);
    }
}
