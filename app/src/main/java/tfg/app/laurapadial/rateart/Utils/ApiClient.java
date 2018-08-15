package tfg.app.laurapadial.rateart.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String ROOT_URL = "http://51.38.237.252:3000/rateart_backend/";


    public static final String URL_POST_POSTS = ROOT_URL + "post";

    private static Retrofit retrofit = null;



    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ROOT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
