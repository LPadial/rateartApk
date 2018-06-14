package tfg.app.laurapadial.rateart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    //Elements layout activity_startup
    TextView tv_title;
    TextView tv_author;
    TextView tv_description;
    Button bt_upload;
    ImageView iv_image;
    RatingBar rb_ratingBar;
    Button bt_sendRate;

    //Others elements
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    RequestQueue requestQueue;

    String url, baseUrl, post, token, patientId;

    String idsMap = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.bt_sendRate = findViewById(R.id.bt_send);
        this.bt_upload = findViewById(R.id.bt_upload);
        this.tv_title = findViewById(R.id.tv_title);
        this.tv_author = findViewById(R.id.tv_author);
        this.tv_description = findViewById(R.id.tv_desc);
        this.iv_image = findViewById(R.id.imageView);
        this.rb_ratingBar = findViewById(R.id.ratingBar);

        sharedPref= getSharedPreferences("rateart", Context.MODE_PRIVATE);

        this.baseUrl = sharedPref.getString("url","51.38.237.252:3000" );

        requestQueue = Volley.newRequestQueue(this);

        getPosts();
    }

    public void getPosts(){
        this.url = "http://" + this.baseUrl + "/rateart_backend/posts";

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0){
                    try{
                        sharedPref= getSharedPreferences("rateart", Context.MODE_PRIVATE);
                        editor=sharedPref.edit();
                    }catch (JSONException je){
                        Log.e("Volley", "JSONException: " + je.toString());
                    }
                }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Empty response or not a response");
                    }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError{
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", token);
                    return headers;
                }
            };
                        requestQueue.add(arrReq);
        }

        public void onUpload(View v){
            Intent intent = new Intent(this, UploadActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
