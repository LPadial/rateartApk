package tfg.app.laurapadial.rateart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

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

class SignupActivity extends AppCompatActivity{

    private EditText name;
    private EditText surname;
    private EditText nick;
    private EditText email;
    private EditText password1;
    private EditText password2;
    private TextView loginErrorMsg;
    private Button btnRegister;
    private RadioButton rbPrivacity;
    private TextView tvError;
    private ConstraintLayout layout;
    private SharedPreferences sharedPref;
    private String baseUrl;
    private String url;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.name = (EditText) findViewById(R.id.ti_name);
        this.surname = (EditText)findViewById(R.id.ti_surname);
        this.nick = (EditText) findViewById(R.id.ti_nickname);
        this.email = (EditText) findViewById(R.id.ti_email);
        this.password1 = (EditText)findViewById(R.id.ti_password1);
        this.password2 = (EditText) findViewById(R.id.ti_password2);
        this.btnRegister = (Button) findViewById(R.id.bt_register);
        this.rbPrivacity = (RadioButton) findViewById(R.id.rb_privacidad);
        this.tvError = (TextView) findViewById(R.id.tv_error);
        this.layout = (ConstraintLayout)findViewById(R.id.layout);

        if(password1 != password2){
            tvError.setText("Las contraseñas deben ser iguales");
        } else {
            sharedPref= getSharedPreferences("rateart", Context.MODE_PRIVATE);
            this.baseUrl = sharedPref.getString("url","192.168.1.38:3000" );
            requestQueue = Volley.newRequestQueue(this);
        }


    }

    private void postSignup(String name, String surname, String nick, String email, String password1) {
        try {
            this.url = "http://" + this.baseUrl + "/rateart_backend/user";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name",name);
            jsonBody.put("surname",surname);
            jsonBody.put("nickname", nick);
            jsonBody.put("email",email);
            jsonBody.put("password", password1);
            jsonBody.put("role","user");

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("volley", response);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    Log.e("volley", error.toString());
                    onErrorSignup();
                }
            }){
                @Override
                public String getBodyContentType(){
                    return "application/json; charset=utf-8";
                }
            };
            requestQueue.add(stringRequest);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public void trySignup(View v){
        postSignup(name.getText().toString(), surname.getText().toString(), nick.getText().toString(), email.getText().toString(), password1.getText().toString());
    }

    public void onErrorSignup(){

        Snackbar.make(layout, R.string.error_signup,Snackbar.LENGTH_SHORT).show();
    }
}
