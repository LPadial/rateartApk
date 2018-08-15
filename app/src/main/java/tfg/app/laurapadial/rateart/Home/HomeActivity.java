package tfg.app.laurapadial.rateart.Home;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import tfg.app.laurapadial.rateart.R;
import tfg.app.laurapadial.rateart.StartupActivity;

public class HomeActivity extends AppCompatActivity{

    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    public SectionsPagerAdapter adapter;
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mViewPager = findViewById(R.id.viewpager_container);
        mFrameLayout = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relLayoutParent);

        setupViewPager();
    }

    private void setupViewPager(){
        this.adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new UploadFragment());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new ProfileFragment());

        ViewPager viewPager = findViewById(R.id.viewpager_container);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.icon_upload);
        tabLayout.getTabAt(1).setIcon(R.drawable.icon_home);
        tabLayout.getTabAt(2).setIcon(R.drawable.icon_profile);

        viewPager.setCurrentItem(1);
    }

    public void logout(View v){
        preferences =this.getSharedPreferences("rateart", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(this, StartupActivity.class);
        startActivity(intent);
        finish();
    }
}
