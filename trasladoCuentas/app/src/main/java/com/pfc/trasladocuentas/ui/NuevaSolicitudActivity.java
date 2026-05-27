package com.pfc.trasladocuentas.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pfc.trasladocuentas.R;

public class NuevaSolicitudActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private MaterialToolbar toolbar;
    private final Fragment paso1 = new Paso1Fragment();
    private final Fragment paso2 = new Paso2Fragment();
    // private final Fragment paso3 = new Paso3Fragment();
    // private final Fragment paso4 = new Paso4Fragment();

    private Fragment fragmentActual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_solicitud);

        toolbar = findViewById(R.id.toolbarNueva);
        bottomNav = findViewById(R.id.bottomNav);

        toolbar.setNavigationOnClickListener(v -> finish());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_paso1) {
                cargarFragment(paso1);
                toolbar.setNavigationOnClickListener(v -> finish());
                return true;

            } else if (id == R.id.nav_paso2) {
                cargarFragment(paso2);
                toolbar.setNavigationOnClickListener(v -> cambiarAPaso(R.id.nav_paso1));
                return true;

            } else if (id == R.id.nav_paso3) {
                // cargarFragment(paso3);
                // toolbar.setNavigationOnClickListener(v -> cambiarAPaso(R.id.nav_paso2));
                Toast.makeText(this, "Próximamente: Paso 3", Toast.LENGTH_SHORT).show();
                return true;

            } else if (id == R.id.nav_paso4) {
                // cargarFragment(paso4);
                // toolbar.setNavigationOnClickListener(v -> cambiarAPaso(R.id.nav_paso3));
                Toast.makeText(this, "Próximamente: Paso 4", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        bottomNav.setSelectedItemId(R.id.nav_paso1);
    }

    public void cambiarAPaso(int menuItemId) {
        bottomNav.setSelectedItemId(menuItemId);
    }

    private void cargarFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (fragmentActual != null) {
            transaction.hide(fragmentActual);
        }

        if (!fragment.isAdded()) {
            transaction.add(R.id.contenedorFragments, fragment);
        } else {
            transaction.show(fragment);
        }

        fragmentActual = fragment;
        transaction.commit();
    }
}