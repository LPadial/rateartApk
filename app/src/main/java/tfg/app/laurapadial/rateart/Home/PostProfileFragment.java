package tfg.app.laurapadial.rateart.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import tfg.app.laurapadial.rateart.R;

public class PostProfileFragment extends Fragment{
    private static final String TAG = "PostProfileFragment";

    public TextView tv_title, tv_description;
    public RatingBar mRatingBar;
    public ImageView iv_foto_post;
    public View view;
    public String titulo, description, id_post;

    public SharedPreferences sharedPref;
    public String token, url;
    public RequestQueue avgRatingPostQueue;
    public boolean ratingCalified;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_post_profile, container, false);

        avgRatingPostQueue = Volley.newRequestQueue(getContext());

        iv_foto_post = view.findViewById(R.id.iv_post);
        tv_title = view.findViewById(R.id.tv_titlePost);
        tv_description = view.findViewById(R.id.tv_description);
        mRatingBar = view.findViewById(R.id.ratingBar);

        mRatingBar.setIsIndicator(true);
        mRatingBar.setClickable(false);
        mRatingBar.setFocusableInTouchMode(false);

        Glide.with(this)
                .load("http://51.38.237.252:3000/rateart_backend/image/"+ id_post)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.logo_rateart)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(iv_foto_post);

        tv_title.setText(titulo);
        tv_description.setText(description);
        getAvgRating(id_post);
        return view;
    }

    public void initPreferences(){
        try {
            sharedPref = this.getActivity().getSharedPreferences("rateart", Context.MODE_PRIVATE);
            this.token = sharedPref.getString("user_token", "");
        }catch(NullPointerException e){
            Toast.makeText(this.requireContext(),"Error",Toast.LENGTH_SHORT).show();
        }
    }

    public void getAvgRating(String id_post){
        initPreferences();
        this.url = "http://51.38.237.252:3000/rateart_backend/avgRatingsPost/" + id_post;
        JsonArrayRequest objReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.i(TAG, response.getJSONObject(0).getString("average"));
                            mRatingBar.setRating(Float.parseFloat(response.getJSONObject(0).getString("average")));

                        }catch(JSONException e){
                            Log.d(TAG, "onCreateView: "+ e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ratingCalified = false;
            }
        }) {

            @Override
            public Map<String, String> getHeaders(){
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        avgRatingPostQueue.add(objReq);
    }
}
