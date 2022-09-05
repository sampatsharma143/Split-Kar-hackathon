package com.shunyank.split_kar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.shunyank.split_kar.databinding.ActivityMainBinding;
import com.shunyank.split_kar.utils.Helper;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        setupSmoothBottomMenu(navController);

    }
    void setupSmoothBottomMenu(NavController navController){
        PopupMenu popupMenu = new PopupMenu(this,null);
        popupMenu.inflate(R.menu.bottom_nav_menu);
        Menu menu =  popupMenu.getMenu();

        binding.bottomBar.setupWithNavController(menu,navController);
    }

}