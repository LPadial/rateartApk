package tfg.app.laurapadial.rateart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import android.text.TextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import tfg.app.laurapadial.rateart.Home.PostProfileFragment;


public class SignupActivity extends AppCompatActivity{
    private static final String TAG = "SignupActivity";


    //Elements layout activity_signup
    EditText name;
    EditText surname;
    EditText nick;
    EditText email;
    EditText password1;
    EditText password2;

    Button btnRegister;
    RelativeLayout layout;
    SharedPreferences sharedPref;
    String baseUrl;
    String url;
    RequestQueue requestQueue;

    TextInputLayout ti_name;
    TextInputLayout ti_surname;
    TextInputLayout ti_nick;
    TextInputLayout ti_email;
    TextInputLayout ti_password;
    TextInputLayout ti_password2;

    ArrayList<String> nicknames;
    ArrayList<String> emails;



    //Method OnCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Declaration attributes
        //EditText
        this.name = findViewById(R.id.et_name);
        this.surname = findViewById(R.id.et_surname);
        this.nick = findViewById(R.id.et_nickname);
        this.email = findViewById(R.id.et_email);
        this.password1 = findViewById(R.id.et_password);
        this.password2 = findViewById(R.id.et_password2);

        //TextInput
        this.ti_name = findViewById(R.id.ti_name);
        this.ti_surname = findViewById(R.id.ti_surname);
        this.ti_nick = findViewById(R.id.ti_nickname);
        this.ti_email = findViewById(R.id.ti_email);
        this.ti_password = findViewById(R.id.ti_password);
        this.ti_password2 = findViewById(R.id.ti_password2);
        ti_name.setErrorEnabled(true);
        ti_surname.setErrorEnabled(true);
        ti_nick.setErrorEnabled(true);
        ti_email.setErrorEnabled(true);
        ti_password.setErrorEnabled(true);
        ti_password2.setErrorEnabled(true);

        this.btnRegister = findViewById(R.id.bt_register);
        this.layout=findViewById(R.id.layout_signup);

        //Listener name
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditName(s);
            }
        });


        //Listener surname
        surname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditSurname(s);
            }
        });

        //Listener nick
        nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditNick(s);
            }
        });

        //Listener email
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditEmail(s);
            }
        });

        //Listener password
        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditPassword1(s);
            }
        });

        //Listener password2
        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditPassword2(s);
            }
        });


        //Restrictions
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !name.isSelected()) {
                    validateEditName(((EditText) v).getText());
                }
            }
        });

        surname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !surname.isSelected()) {
                    validateEditSurname(((EditText) v).getText());
                }
            }
        });

        nick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !nick.isSelected()) {
                    validateEditNick(((EditText) v).getText());
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !email.isSelected()) {
                    validateEditEmail(((EditText) v).getText());
                }
            }
        });

        password1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !password1.isSelected()) {
                    validateEditPassword1(((EditText) v).getText());
                }
            }
        });

        password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !password2.isSelected()) {
                    validateEditPassword2(((EditText) v).getText());
                }
            }
        });

        sharedPref= getSharedPreferences("rateart", Context.MODE_PRIVATE);
        this.baseUrl = sharedPref.getString("url","51.38.237.252:3000" );
        requestQueue = Volley.newRequestQueue(this);
        getUsers();
    }

    //End OnCreate


    //Errors
    private void validateEditName(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            ti_name.setError(null);
        } else {
            ti_name.setError(getString(R.string.error_name));
        }
    }

    private void validateEditSurname(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            ti_surname.setError(null);
        } else {
            ti_surname.setError(getString(R.string.error_surname));
        }
    }

    private void validateEditNick(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            ti_nick.setError(null);
            if(nicknames.contains(nick.getText().toString())){
                ti_nick.setError(getString(R.string.error_exists_nickname));
            }
        } else {
            ti_nick.setError(getString(R.string.error_nick));
        }
    }

    private void validateEditEmail(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            ti_email.setError(null);
            if(emails.contains(email.getText().toString())){
                ti_email.setError(getString(R.string.error_exists_email));
            }
        } else {
            ti_email.setError(getString(R.string.error_email));
        }
    }

    private void validateEditPassword1(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            ti_password.setError(null);
        } else {
            ti_password.setError(getString(R.string.error_password));
        }
    }

    private void validateEditPassword2(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            ti_password2.setError(null);
        } else {
            ti_password2.setError(getString(R.string.error_password2));
        }
    }
    //End errors

    //Post user to database
    private void postSignup(String name, String surname, String nick, String email, String password1) {

        try {
            this.url = "http://" + this.baseUrl + "/rateart_backend/user";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("surname", surname);
            jsonBody.put("nickname", nick);
            jsonBody.put("email", email);
            jsonBody.put("password", password1);

            final String requestBody = jsonBody.toString();

            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG+ "volley", response);
                    onOkSignup();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG+"volley", error.toString());
                    onErrorSignup();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody(){
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    }catch (UnsupportedEncodingException uee){
                        VolleyLog.wtf("Unsuported encoding while trying to get bytes of %s using %s", requestBody, "utf8");
                        return  null;
                    }
                }
            };
            requestQueue.add(postRequest);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    //Get users
    private void getUsers() {

        this.url = "http://" + this.baseUrl + "/rateart_backend/users";
        nicknames = new ArrayList<>();
        emails = new ArrayList<>();

        JsonArrayRequest getUsers = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                nicknames.add(response.getJSONObject(i).getString("nickname"));
                                emails.add(response.getJSONObject(i).getString("email"));
                            } catch (JSONException e) {
                                Log.d(TAG, "onCreateView: " + e.toString());
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG + "volley", error.toString());
            }
        });
        requestQueue.add(getUsers);
    }

    //Button to create user
    public void trySignup(View v) {
        if(name.getText().toString().equals("") || surname.getText().toString().equals("") ||
                nick.getText().toString().equals("") || email.getText().toString().equals("") ||
                password1.getText().toString().equals("") || password2.getText().toString().equals("")){
            Snackbar.make(this.layout,R.string.error_fields_required,Snackbar.LENGTH_SHORT).show();
        }else if(emails.contains(email.getText().toString())){
            Snackbar.make(this.layout, R.string.error_exists_email,Snackbar.LENGTH_SHORT).show();
        }else if(nicknames.contains(nick.getText().toString())) {
            Snackbar.make(this.layout, R.string.error_exists_nickname, Snackbar.LENGTH_SHORT).show();
        }else if(!password1.getText().toString().equals(password2.getText().toString())) {
            Snackbar.make(this.layout, R.string.error_password_not_match,Snackbar.LENGTH_SHORT).show();
        }else if(password1.getText().toString().equals(password2.getText().toString()) && !emails.contains(email.getText().toString()) && !nicknames.contains(nick.getText().toString())) {
            postSignup(name.getText().toString(), surname.getText().toString(), nick.getText().toString(), email.getText().toString(), password1.getText().toString());
        }
    }

    public void onErrorSignup(){

        Snackbar.make(this.layout, R.string.error_signup,Snackbar.LENGTH_SHORT).show();
    }

    //Al registrarse se añade el token
    public void onOkSignup(){
        Intent intent = new Intent(this, StartupActivity.class);
        startActivity(intent);
        finish();
    }
}
