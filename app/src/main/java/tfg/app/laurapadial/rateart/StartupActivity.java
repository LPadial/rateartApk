package tfg.app.laurapadial.rateart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class StartupActivity extends AppCompatActivity {

    ConstraintLayout layout;

    //Elements layout activity_startup
    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    Button btnSignup;
    Button btFacebook;
    TextView tvError;

    //Others elements
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    RequestQueue requestQueue;

    String baseUrl;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        this.btnLogin = (Button) findViewById(R.id.bt_login);
        this.etEmail = (EditText) findViewById(R.id.et_email);
        this.etPassword = (EditText) findViewById(R.id.et_password);
        layout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        sharedPref= getSharedPreferences("rateart", Context.MODE_PRIVATE);

        this.baseUrl = sharedPref.getString("url","192.168.1.38:3000" );

        requestQueue = Volley.newRequestQueue(this);
    }

    private void postLogin(String email, String password) {
        try {
            this.url = "http://" + this.baseUrl + "/rateart_backend/user/login";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email",email);
            jsonBody.put("password",password);
            jsonBody.put("gethash", "true");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("volley", response);
                    onOkLogin(response);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    Log.e("volley", error.toString());
                    onErrorLogin();
                }
            }){
                @Override
                public String getBodyContentType(){
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() throws AuthFailureError{
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

    public void tryLogin(View v){
        postLogin(etEmail.getText().toString(), etPassword.getText().toString());
    }

    public void onErrorLogin(){

        Snackbar.make(layout, R.id.login_error,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    public void onOkLogin(String response){
        try {
            JSONObject obj = new JSONObject((response));
            Log.i("TOKEN_ADQUIRED",obj.get("token").toString());

            sharedPref= getSharedPreferences("rateart", Context.MODE_PRIVATE);
            editor=sharedPref.edit();

            editor.putString("user_token", obj.get("token").toString());
            editor.putString("url", this.baseUrl);
            editor.commit();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }catch (JSONException ex){
            Log.e("JSONParser", "Can't parse de string to a JSON");
        }
    }
}

