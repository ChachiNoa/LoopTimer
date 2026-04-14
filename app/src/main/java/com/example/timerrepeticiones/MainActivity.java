package com.example.timerrepeticiones;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.timerrepeticiones.data.SettingsRepository;
import com.example.timerrepeticiones.databinding.ActivityMainBinding;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private SettingsRepository settingsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settingsRepository = new SettingsRepository(this);
        if (settingsRepository.isDarkMode()) {
            setTheme(R.style.Theme_CustomTimer_Dark);
        } else {
            setTheme(R.style.Theme_CustomTimer);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                    .setOpenableLayout(binding.drawerLayout)
                    .build();
            
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            
            refreshDrawerMenu();

            binding.fab.setOnClickListener(view -> {
                navController.navigate(R.id.timerEditFragment);
            });

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.FirstFragment) {
                    binding.fab.setVisibility(View.VISIBLE);
                } else {
                    binding.fab.setVisibility(View.GONE);
                }
            });

            binding.navView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_all_groups) {
                    filterByGroup(null);
                } else if (id == 999) { // ID para añadir grupo
                    showAddGroupDialog();
                } else {
                    filterByGroup(item.getTitle().toString());
                }
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }

    private void refreshDrawerMenu() {
        Menu menu = binding.navView.getMenu();
        menu.clear();
        
        menu.add(Menu.NONE, R.id.nav_all_groups, 0, "Todos los Timers").setIcon(android.R.drawable.ic_menu_agenda);
        
        List<String> groups = settingsRepository.getGroups();
        for (int i = 0; i < groups.size(); i++) {
            menu.add(1, i, i + 1, groups.get(i)).setIcon(android.R.drawable.ic_menu_directions);
        }
        
        menu.add(2, 999, 100, "+ Añadir Grupo").setIcon(android.R.drawable.ic_input_add);
    }

    private void filterByGroup(String groupName) {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putString("filter_group", groupName);
            navHostFragment.getNavController().navigate(R.id.FirstFragment, bundle);
        }
    }

    private void showAddGroupDialog() {
        EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Nuevo Grupo")
                .setView(input)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String name = input.getText().toString();
                    if (!name.isEmpty()) {
                        List<String> groups = settingsRepository.getGroups();
                        groups.add(name);
                        settingsRepository.saveGroups(groups);
                        refreshDrawerMenu();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_groups) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.action_settings) {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_content_main);
            if (navHostFragment != null) {
                navHostFragment.getNavController().navigate(R.id.settingsFragment);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
