package com.pfc.trasladocuentas.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pfc.trasladocuentas.JsonObjectRequestWithCustomAuth;
import com.pfc.trasladocuentas.R;

import org.json.JSONObject;

public class NuevaSolicitudActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private MaterialToolbar toolbar;
    private final Fragment paso1 = new Paso1Fragment();
    private final Fragment paso2 = new Paso2Fragment();
    private final Fragment paso3 = new Paso3Fragment();
    private final Fragment paso4 = new Paso4Fragment();

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
                cargarFragment(paso3);
                toolbar.setNavigationOnClickListener(v -> cambiarAPaso(R.id.nav_paso2));
                return true;

            } else if (id == R.id.nav_paso4) {
                cargarFragment(paso4);
                toolbar.setNavigationOnClickListener(v -> cambiarAPaso(R.id.nav_paso3));
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

    public void enviarSolicitudFinal() {
        try {
            Paso1Fragment p1 = (Paso1Fragment) paso1;
            Paso2Fragment p2 = (Paso2Fragment) paso2;
            Paso3Fragment p3 = (Paso3Fragment) paso3;
            Paso4Fragment p4 = (Paso4Fragment) paso4;

            JSONObject json = new JSONObject();
            json.put("entidad_origen", p1.getEntidad());
            json.put("iban_origen", p1.getIban());
            json.put("iban_destino", p2.getIban());
            json.put("fecha_ejecucion", p2.getFecha());

            json.put("pet_cancelar_ordenes", p3.chkCancelar.isChecked());
            json.put("pet_bloquear_entrantes", p3.chkBloquear.isChecked());
            json.put("pet_transferir_saldo_cierre", p3.chkTransferirCierre.isChecked());
            json.put("pet_recibir_info", p3.chkRecibirInfo.isChecked());

            json.put("act_habilitar_ordenes", p4.chkHabilitar.isChecked());
            json.put("act_aceptar_adeudos", p4.chkAceptar.isChecked());
            json.put("act_informar_emisores", p4.chkInformar.isChecked());

            lanzarPeticionServidor(json);

        } catch (Exception e) {
            Toast.makeText(this, "Error al mandar los datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void lanzarPeticionServidor(JSONObject json) {
        String url = "http://10.0.2.2:8000/solicitudes/";

        JsonObjectRequestWithCustomAuth request = new JsonObjectRequestWithCustomAuth(
                Request.Method.POST, url, json,
                response -> {
                    Toast.makeText(this, "Solicitud creada!", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> Toast.makeText(this, "Error en el servidor", Toast.LENGTH_SHORT).show(),
                this
        );

        Volley.newRequestQueue(this).add(request);
    }
}