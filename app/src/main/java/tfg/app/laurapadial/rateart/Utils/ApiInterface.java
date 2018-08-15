package tfg.app.laurapadial.rateart.Utils;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @Multipart
    @POST(ApiClient.URL_POST_POSTS)
    Call<Posts> uploadImage(@Header("Authorization") String token, @Part MultipartBody.Part image, @Part("title") RequestBody title, @Part("description") RequestBody description);
}