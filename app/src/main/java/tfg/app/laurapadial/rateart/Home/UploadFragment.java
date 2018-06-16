package tfg.app.laurapadial.rateart.Home;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tfg.app.laurapadial.rateart.R;

public class UploadFragment extends Fragment {
    private static final String TAG = "UploadFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        return view;
    }
}
