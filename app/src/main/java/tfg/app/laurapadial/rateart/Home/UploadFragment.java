package tfg.app.laurapadial.rateart.Home;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import tfg.app.laurapadial.rateart.R;
import tfg.app.laurapadial.rateart.Utils.ApiClient;
import tfg.app.laurapadial.rateart.Utils.ApiInterface;
import retrofit2.Call;
import tfg.app.laurapadial.rateart.Utils.Posts;

public class UploadFragment extends Fragment{

    final int ACTIVITY_SELECT_IMAGE = 1234;
    private ImageButton btn_choose_photo;
    Uri selectedImage;
    String filePath;
    EditText title_et, description_et;
    CoordinatorLayout layout;


    Button b;

    File file = null;
    RequestBody requestFile = null, requestTitle = null, requestDescription = null;
    MultipartBody.Part part = null;

    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE= 0;
    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE= 0;

    SharedPreferences sharedPref;
    String url, token;

    ViewPager viewPager;


    private String encryptedFileName = "encrypted_Image.jpg";
    private static String algorithm = "AES";
    static SecretKey yourKey = null;
    public String baseUrl;

    public RequestQueue requestQueue;

    public String keyToDecode = null;
    public String filename;
    public String decodedFileName = "decoded_Image.jpg";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("UploadFragment", "onCreateView: ");
        View view = inflater.inflate(R.layout.activity_upload, container, false);
        requestQueue = Volley.newRequestQueue(getContext());
        viewPager = view.findViewById(R.id.viewpager_container);
        title_et = view.findViewById(R.id.et_title);
        description_et = view.findViewById(R.id.et_description);
        layout = view.findViewById(R.id.layout_upload);
        b = view.findViewById(R.id.bt_upload);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_STORAGE);
                } else if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }else{
                    uploadNewPost();
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_STORAGE) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                uploadNewPost();
            }
        }
    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btn_choose_photo = view.findViewById(R.id.upload_photo);
        btn_choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();


            filePath = getPath(selectedImage);
            filename = filePath.substring(filePath.lastIndexOf("/")+1);
            encryptedFileName = "encrypted_" + filename;

            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

            if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                btn_choose_photo.setImageURI(selectedImage);
            } else {
                getExitTransition();
            }

        }

    }

    public String getPath(Uri uri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver().query(uri, proj, null, null, null);
        assert cursor != null;
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void initPreferences(){
        sharedPref = this.getActivity().getSharedPreferences("rateart", Context.MODE_PRIVATE);
        this.url = sharedPref.getString("url","");
        this.token = sharedPref.getString("user_token", "");
        this.baseUrl = sharedPref.getString("baseUrl","");
    }

    public void uploadNewPost(){
        initPreferences();

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        if(filePath!=null && title_et.getText().toString()!="" && description_et.getText().toString()!=""){
            file = new File(filePath);
            requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            requestTitle = RequestBody.create(MediaType.parse("text/plain"), title_et.getText().toString());
            requestDescription = RequestBody.create(MediaType.parse("text/plain"), description_et.getText().toString());
            part = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


            try {
                Call<Posts> call = apiService.uploadImage(token, part, requestTitle, requestDescription);

                call.enqueue(new Callback<Posts>() {
                    @Override
                    public void onResponse(Call<Posts> call, retrofit2.Response<Posts> response) {
                        onOkUpload();
                        try {
                            saveFile(getImageFile());
                            //decodeFile();
                            deleteOriginalImage();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<Posts> call, Throwable t) {
                        Log.d("incorrect", t.getMessage());
                        onErrorUpload();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else{
            Snackbar.make(layout,R.string.errorFields,Snackbar.LENGTH_SHORT).show();
        }
    }

    public void onErrorUpload(){
        Toast.makeText(this.requireActivity(), R.string.error_upload_post,Toast.LENGTH_SHORT).show();
    }

    public void onOkUpload(){
        Toast.makeText(this.requireContext(), R.string.upload_post, Toast.LENGTH_SHORT).show();
        title_et.getText().clear();
        description_et.getText().clear();
    }



    //Encriptar

    public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        yourKey = keyGenerator.generateKey();
        return yourKey;
    }

    public static byte[] encodeFile(SecretKey yourKey, byte[] fileData) throws Exception {
        byte[] encrypted = null;
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        encrypted = cipher.doFinal(fileData);
        return encrypted;
    }

    public static byte[] decodeFile(SecretKey yourKey, byte[] fileData) throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }

    void saveFile(byte[] stringToSave) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator, encryptedFileName);

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            yourKey = generateKey();
            postKey(yourKey,filePath);

            byte[] filesBytes = encodeFile(yourKey, stringToSave);
            bos.write(filesBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postKey(SecretKey password, String filePath) {
        try {
            initPreferences();
            this.url = "http://51.38.237.252:3000/rateart_backend/key";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("key",password);
            jsonBody.put("pathFile", filePath);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Clave","La clave ha sido guardada "+ response);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    Log.d("Clave","NOOO "+ error);
                }
            }){
                @Override
                public String getBodyContentType(){
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

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", token);
                    return headers;
                }
            };
            requestQueue.add(stringRequest);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public void getKey(String id_post){
        initPreferences();
        this.url = "http://51.38.237.252:3000/rateart_backend/key";
        JsonObjectRequest objReq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response != null) {
                                keyToDecode=response.getString("key");
                            }
                        }catch(JSONException e){
                            Log.d("Error al hacer get", "onCreateView: "+e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("500","Server not fix");
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
        requestQueue.add(objReq);
    }


    void decodeFile() {

        try {
            //File file = new File(Environment.getExternalStorageDirectory() + File.separator, encryptedFileName);
            byte[] decodedData = decodeFile(yourKey, readFile());
            // String str = new String(decodedData);
            //System.out.println("DECODED FILE CONTENTS : " + str);
            showImage(decodedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] readFile() {
        byte[] contents = null;

        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator, encryptedFileName);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

    public byte[] getImageFile() throws FileNotFoundException {
        byte[] image_data = null;
        byte[] inarry = null;

        //AssetManager am = getActivity().getAssets();
        try {
            //InputStream is = am.open(filePath); // use recorded file instead of getting file from assets folder.
            InputStream is = new FileInputStream(filePath);
            int length = is.available();
            Log.d("is",String.valueOf(length));
            Log.d("filePath", filePath);
            image_data = new byte[length];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = is.read(image_data)) != -1) {
                output.write(image_data, 0, bytesRead);
            }
            inarry = output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inarry;

    }

    private void showImage(byte[] imageByteArray) {

        try {
            // create temp file that will hold byte array
            //File tempImage = File.createTempFile("image", "jpg", getContext().getCodeCacheDir());
            File tempImage = new File(Environment.getExternalStorageDirectory()
                    + File.separator, decodedFileName);
            Log.d("tempImage", Environment.getExternalStorageDirectory() + File.separator + decodedFileName);
            tempImage.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempImage);
            fos.write(imageByteArray);
            fos.close();
            // Tried reusing instance of media player
            // but that resulted in system crashes...
            /*MediaPlayer mediaPlayer = new MediaPlayer();
            FileInputStream fis = new FileInputStream(tempImage);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();*/
        } catch (IOException ex) {
            ex.printStackTrace();

        }

    }

    private void deleteOriginalImage(){
        File file = new File(filePath).getAbsoluteFile();
        Log.d("filePath", filePath);
        if(file.exists()){
            if (file.delete()) {
                Log.d("file Deleted :",filePath);
            } else {
                Log.e("file not Deleted :",filePath);
            }
        }
    }
}

