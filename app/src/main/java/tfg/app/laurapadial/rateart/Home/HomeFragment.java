package tfg.app.laurapadial.rateart.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tfg.app.laurapadial.rateart.R;

public class HomeFragment extends Fragment{

    private static final String TAG = "HomeFragment";
    private int fragCount = 0;
    public ArrayList<String> listFrag = new ArrayList<>();
    SharedPreferences sharedPref;
    String url, token;
    RequestQueue requestQueue;
    public String id_post;
    //public ImageLoader mImageLoader;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        requestQueue = Volley.newRequestQueue(getContext());
        getPosts();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    public void getPosts(){
        initPreferences();
        this.url = "http://51.38.237.252:3000/rateart_backend/posts";

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {

                            FragmentManager fragMan = getFragmentManager();
                            FragmentTransaction fragTransaction = fragMan.beginTransaction();
                            PostFragment myFrag;

                            for(int i = 0; i < response.length(); i++) {
                                myFrag = new PostFragment();
                                try {
                                    id_post = response.getJSONObject(i).getString("_id");
                                    myFrag.id_post = response.getJSONObject(i).getString("_id");
                                    myFrag.titulo = response.getJSONObject(i).getString("title");
                                    myFrag.description = response.getJSONObject(i).getString("description");
                                    myFrag.user = response.getJSONObject(i).getJSONObject("user").getString("name");

                                } catch (JSONException e) {
                                    Log.d(TAG, "onCreateView: "+e.toString());
                                }

                                fragTransaction.add(R.id.fragmentHome_LinearLayout, myFrag, "fragment"+ fragCount);
                                listFrag.add("fragment"+ fragCount);
                                fragCount++;
                            }
                            fragTransaction.commit();


                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "Empty response or not a response");
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        requestQueue.add(arrReq);
    }

    public void initPreferences(){
        sharedPref = this.getActivity().getSharedPreferences("rateart", Context.MODE_PRIVATE);
        this.url = sharedPref.getString("url","");
        this.token = sharedPref.getString("user_token", "");
    }

}
