package tfg.app.laurapadial.rateart.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tfg.app.laurapadial.rateart.R;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public TextView numPosts;
    public TextView avgRateGlobal;
    public TextView posRanking;
    public TextView name;
    public ImageButton logout;

    private int fragCount = 0;
    public ArrayList<String> listFrag = new ArrayList<>();
    SharedPreferences sharedPref;
    String url, token;
    RequestQueue requestQueue;
    public String id_post;
    PostProfileFragment myFrag;
    public RequestQueue numPostsQueue;
    public RequestQueue scoreQueue;
    public RequestQueue rankingQueue;

    //public ImageLoader mImageLoader;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        numPosts = view.findViewById(R.id.tvPosts);
        avgRateGlobal = view.findViewById(R.id.tvAvgRate);
        posRanking = view.findViewById(R.id.tvPosRanking);
        logout = view.findViewById(R.id.logout_button);
        //name = view.findViewById(R.id.display_name);

        requestQueue = Volley.newRequestQueue(getContext());
        numPostsQueue = Volley.newRequestQueue(getContext());
        scoreQueue = Volley.newRequestQueue(getContext());
        rankingQueue = Volley.newRequestQueue(getContext());

        /*mImageLoader= new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });*/

        getNumPosts();
        getScore();
        getRanking();
        getPosts();

        return view;
    }

    public void getNumPosts(){
        initPreferences();
        this.url = "http://51.38.237.252:3000/rateart_backend/countPosts";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("volley", response);
                if(response != null) {
                    numPosts.setText(response);
                }else{
                    numPosts.setText("0");
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("volley", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders(){
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        numPostsQueue.add(stringRequest);
    }

    public void getScore(){
        initPreferences();
        this.url = "http://51.38.237.252:3000/rateart_backend/avgPostsUser";

        StringRequest arrayRequest = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.i("volley", response);
                if(response!=null) {
                    avgRateGlobal.setText(response.replaceAll("\"",""));
                }else{
                    avgRateGlobal.setText("0");
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("volley", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders(){
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        scoreQueue.add(arrayRequest);
    }

    public void getRanking(){
        initPreferences();
        this.url = "http://51.38.237.252:3000/rateart_backend/ranking";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("volley", response);
                if(response != null) {
                    posRanking.setText(response);
                }else{
                    posRanking.setText("X");
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("volley", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders(){
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        rankingQueue.add(stringRequest);
    }

    public void getPosts(){
        initPreferences();
        //name.setText("");
        this.url = "http://51.38.237.252:3000/rateart_backend/postsUser";

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            FragmentManager fragMan = getFragmentManager();
                            FragmentTransaction fragTransaction = fragMan.beginTransaction();

                            for(int i = 0; i < response.length(); i++) {
                                myFrag = new PostProfileFragment();
                                try {
                                    id_post = response.getJSONObject(i).getString("_id");
                                    myFrag.id_post = response.getJSONObject(i).getString("_id");
                                    myFrag.titulo = response.getJSONObject(i).getString("title");
                                    myFrag.description = response.getJSONObject(i).getString("description");

                                    //myFrag.url_foto = "http://51.38.237.252:3000/rateart_backend/image/"+ id_post;
                                    //myFrag.mImageLoader = mImageLoader;

                                } catch (JSONException e) {
                                    Log.d(TAG, "onCreateView: "+e.toString());
                                }

                                fragTransaction.add(R.id.fragmentProfile_LinearLayout, myFrag, "fragment"+ fragCount);
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
            public Map<String, String> getHeaders(){
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
