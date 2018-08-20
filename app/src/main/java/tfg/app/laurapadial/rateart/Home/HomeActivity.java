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

        View view1 = getLayoutInflater().inflate(R.layout.customtab, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.icon_upload);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));

        View view2 = getLayoutInflater().inflate(R.layout.customtab, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.icon_home);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        View view3 = getLayoutInflater().inflate(R.layout.customtab, null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.icon_profile);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.mipmap.ic_upload);
        tabLayout.getTabAt(1).setIcon(R.mipmap.ic_rateart);
        tabLayout.getTabAt(2).setIcon(R.mipmap.ic_profile);

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
