package tfg.app.laurapadial.rateart.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tfg.app.laurapadial.rateart.R;

public class PostFragment extends Fragment{
    private static final String TAG = "PostFragment";

    public TextView tv_title, tv_description, tv_user;
    public RatingBar mRatingBar;
    public ImageView iv_foto_post;
    public View view;
    public String titulo, description, user;

    public SharedPreferences sharedPref;
    public String token, url, id_post;
    public RequestQueue ratingQueue;
    public RequestQueue ratingPostQueue;
    public boolean ratingCalified;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_post, container, false);

        ratingQueue = Volley.newRequestQueue(getContext());
        ratingPostQueue = Volley.newRequestQueue(getContext());
        tv_title = view.findViewById(R.id.tv_titlePost);
        tv_description = view.findViewById(R.id.tv_description);
        tv_user = view.findViewById(R.id.tv_user_post);
        iv_foto_post = view.findViewById(R.id.iv_post);

        tv_title.setText(titulo);
        tv_description.setText(description);
        tv_user.setText(user);

        Glide.with(this)
                .load("http://51.38.237.252:3000/rateart_backend/image/"+ id_post)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.logo_rateart)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(iv_foto_post);

        mRatingBar = view.findViewById(R.id.ratingBar);
        getRating(id_post);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                postRating(id_post, ratingBar.getRating());
                Log.d(TAG, "onRatingChanged: " + ratingBar.getRating());

                mRatingBar.setIsIndicator(true);
                mRatingBar.setClickable(false);
                mRatingBar.setFocusableInTouchMode(false);
            }
        });

        return view;
    }

    public void postRating(String id_post, float value){
        try{
            initPreferences();
            this.url = "http://51.38.237.252:3000/rateart_backend/rating/" + id_post;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("value", value);
            Log.d(TAG, "postRating: value"+value);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("postRating","Response" + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("volley", error.toString());
                    onErrorPost();
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
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", token);
                    return headers;
                }
            };
            ratingQueue.add(stringRequest);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public void onErrorPost(){
        Toast.makeText(getActivity(),"No se ha subido el rating", Toast.LENGTH_SHORT).show();
    }

    public void initPreferences(){
        sharedPref = this.getActivity().getSharedPreferences("rateart", Context.MODE_PRIVATE);
        this.token = sharedPref.getString("user_token", "");
    }

    public void getRating(String id_post){
        initPreferences();
        this.url = "http://51.38.237.252:3000/rateart_backend/userRatingPost/" + id_post;
        JsonObjectRequest objReq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response != null) {
                                mRatingBar.setIsIndicator(true);
                                mRatingBar.setClickable(false);
                                mRatingBar.setFocusableInTouchMode(false);
                                mRatingBar.setRating(Float.parseFloat(response.getString("value")));
                            }
                        }catch(JSONException e){
                            Log.d(TAG, "onCreateView: "+e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ratingCalified = false;
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        ratingPostQueue.add(objReq);
    }
}
