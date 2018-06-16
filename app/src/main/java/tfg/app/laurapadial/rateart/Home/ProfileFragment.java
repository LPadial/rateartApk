package tfg.app.laurapadial.rateart.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tfg.app.laurapadial.rateart.R;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_profile, container, false);
        return view;
    }
}
