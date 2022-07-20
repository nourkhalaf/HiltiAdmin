package com.hiltiapp.hiltiadmin.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.hiltiapp.hiltiadmin.Devices.DeviceCategoriesActivity;
import com.hiltiapp.hiltiadmin.LoginActivity;
import com.hiltiapp.hiltiadmin.MainActivity;
import com.hiltiapp.hiltiadmin.Prevalent.Prevalent;
import com.hiltiapp.hiltiadmin.R;
import com.hiltiapp.hiltiadmin.Screens.ScreensProductsActivity;
import com.hiltiapp.hiltiadmin.Skins.SkinsProductsActivity;
import com.hiltiapp.hiltiadmin.SliderImagesActivity;
import com.hiltiapp.hiltiadmin.WoodDesigns.WoodCategoriesActivity;

import java.util.Locale;

import io.paperdb.Paper;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ImageView mobileScreens, mobileSkins, devicesScreen, woodDesigns;
    private Button sliderImagesBtn, logoutBtn;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mobileScreens = root.findViewById(R.id.mobile_screens);
        mobileSkins = root.findViewById(R.id.mobile_skin);
        devicesScreen = root.findViewById(R.id.devices_screens);
        woodDesigns = root.findViewById(R.id.wood_designs);

        sliderImagesBtn = root.findViewById(R.id.slider_images_btn);
        sliderImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SliderImagesActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn = root.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]
                        {
                                getString(R.string.logout),
                                getString(R.string.cancel)
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(  getString(R.string.logout_alert));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0)
                        {
                            Toast.makeText(getActivity(),  getString(R.string.toast_logout),Toast.LENGTH_SHORT).show();

                            Paper.book().write(Prevalent.AdminNameKey,"");
                            Paper.book().write(Prevalent.AdminPasswordKey,"");
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();

                        }
                        if (i == 1)
                        {


                        }


                    }
                });
                builder.show();


            }
        });
        mobileScreens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ScreensProductsActivity.class);
                startActivity(intent);
            }
        });

        mobileSkins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SkinsProductsActivity.class);
                startActivity(intent);
            }
        });

        devicesScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceCategoriesActivity.class);
                startActivity(intent);
            }
        });

        woodDesigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WoodCategoriesActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.language, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.language_english)
        {
            setApplicationLocale("en");
        }
        if (id == R.id.language_arabic)
        {
            setApplicationLocale("ar");
        }
        return super.onOptionsItemSelected(item);
    }


    private void setApplicationLocale(String locale) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(locale.toLowerCase()));
        } else {
            config.locale = new Locale(locale.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
        Intent refresh = new Intent(getActivity(), MainActivity.class);
        startActivity(refresh);
        getActivity().finish();
    }

}