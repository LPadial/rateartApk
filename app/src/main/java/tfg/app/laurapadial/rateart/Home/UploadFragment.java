package tfg.app.laurapadial.rateart.Home;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
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

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import tfg.app.laurapadial.rateart.R;
import tfg.app.laurapadial.rateart.SignupActivity;
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


    Button b;

    File file = null;
    RequestBody requestFile = null, requestTitle = null, requestDescription = null;
    MultipartBody.Part part = null;

    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE= 0;

    SharedPreferences sharedPref;
    String url, token;

    ViewPager viewPager;

    public SectionsPagerAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("UploadFragment", "onCreateView: ");
        View view = inflater.inflate(R.layout.activity_upload, container, false);
        viewPager = view.findViewById(R.id.viewpager_container);
        title_et = view.findViewById(R.id.et_title);
        description_et = view.findViewById(R.id.et_description);
        b = view.findViewById(R.id.bt_upload);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_STORAGE);
                } else {
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
    }

    public void uploadNewPost(){
        initPreferences();

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

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
                    refresh();
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
    }

    public void onErrorUpload(){
        Toast.makeText(this.requireActivity(), R.string.error_upload_post,Toast.LENGTH_SHORT).show();
    }

    public void onOkUpload(){
        Toast.makeText(this.requireContext(), R.string.upload_post, Toast.LENGTH_SHORT).show();
        title_et.getText().clear();
        description_et.getText().clear();

       /* Fragment newFragment = new ProfileFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.layout_upload, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();*/
    }

    private void refresh(){
        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentHome_LinearLayout);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
    }
}
