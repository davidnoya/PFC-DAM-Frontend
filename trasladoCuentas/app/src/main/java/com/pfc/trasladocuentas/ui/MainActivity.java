package com.pfc.trasladocuentas.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import com.pfc.trasladocuentas.R;
import com.pfc.trasladocuentas.JsonArrayRequestWithCustomAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView saldoTotal;
    private MaterialToolbar toolbar;
    private RequestQueue requestQueue;

    private LinearLayout contenedorCuentas, contenedorTarjetas, contenedorMovimientos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saldoTotal = findViewById(R.id.textoSaldoTotal);
        toolbar = findViewById(R.id.toolbar);
        requestQueue = Volley.newRequestQueue(this);

        contenedorCuentas = findViewById(R.id.contenedorCuentas);
        contenedorTarjetas = findViewById(R.id.contenedorTarjetas);
        contenedorMovimientos = findViewById(R.id.contenedorMovimientos);

        setupToolbarMenu();
        obtenerResumen();
    }

    private void setupToolbarMenu() {
        toolbar.inflateMenu(R.menu.menu_resumen);

        android.graphics.drawable.Drawable iconoMenu = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.ic_menu);
        if (iconoMenu != null) {
            iconoMenu.setTint(android.graphics.Color.BLACK);
            toolbar.setOverflowIcon(iconoMenu);
        }

        MenuItem itemSession = toolbar.getMenu().findItem(R.id.menu_logout);
        if (itemSession != null) {
            SpannableString s = new SpannableString(itemSession.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
            itemSession.setTitle(s);
        }

        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_traslado) {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.menu_logout) {
                mostrarPopupConfirmacionLogout();
                return true;
            }
            return false;
        });
    }

    private void obtenerResumen() {
        String url = "http://10.0.2.2:8000/resumen-banca/";

        JsonArrayRequestWithCustomAuth request = new JsonArrayRequestWithCustomAuth(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        double saldoTotalEntrada = 0.0;

                        contenedorCuentas.removeAllViews();
                        contenedorTarjetas.removeAllViews();
                        contenedorMovimientos.removeAllViews();

                        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject cuenta = response.getJSONObject(i);

                            double saldoCuenta = cuenta.getDouble("saldo");
                            saldoTotalEntrada += saldoCuenta;

                            android.view.View vistaCuenta = inflater.inflate(R.layout.item_cuenta, contenedorCuentas, false);
                            TextView alias = vistaCuenta.findViewById(R.id.aliasCuenta);
                            TextView iban = vistaCuenta.findViewById(R.id.ibanCuenta);
                            TextView saldo = vistaCuenta.findViewById(R.id.saldoCuenta);

                            alias.setText(cuenta.getString("alias"));
                            String ibanCompleto = cuenta.getString("iban");
                            String ibanOculto = "**** " + (ibanCompleto.length() > 4 ? ibanCompleto.substring(ibanCompleto.length() - 4) : ibanCompleto);
                            iban.setText(ibanOculto);
                            saldo.setText(String.format("%.2f €", saldoCuenta));

                            contenedorCuentas.addView(vistaCuenta);

                            JSONArray tarjetas = cuenta.getJSONArray("tarjetas");
                            for (int j = 0; j < tarjetas.length(); j++) {
                                JSONObject tarjeta = tarjetas.getJSONObject(j);

                                android.view.View vistaTarjeta = inflater.inflate(R.layout.item_tarjeta, contenedorTarjetas, false);
                                TextView tipo = vistaTarjeta.findViewById(R.id.tipoTarjeta);
                                TextView pan = vistaTarjeta.findViewById(R.id.panTarjeta);
                                TextView estado = vistaTarjeta.findViewById(R.id.estadoTarjeta);

                                tipo.setText(tarjeta.getString("tipo"));

                                String panCompleto = tarjeta.getString("pan");
                                String panOculto = "**** " + (panCompleto.length() > 4 ? panCompleto.substring(panCompleto.length() - 4) : panCompleto);
                                pan.setText(panOculto);

                                boolean activa = tarjeta.getBoolean("activa");
                                estado.setText(activa ? "ACTIVA" : "INACTIVA");
                                estado.setTextColor(android.graphics.Color.parseColor(activa ? "#2ECC71" : "#E74C3C"));

                                contenedorTarjetas.addView(vistaTarjeta);
                            }

                            JSONArray movimientos = cuenta.getJSONArray("ultimos_movimientos");
                            for (int k = 0; k < movimientos.length(); k++) {
                                JSONObject mov = movimientos.getJSONObject(k);

                                android.view.View vistaMovimiento = inflater.inflate(R.layout.item_movimiento, contenedorMovimientos, false);
                                TextView concepto = vistaMovimiento.findViewById(R.id.conceptoMovimiento);
                                TextView fecha = vistaMovimiento.findViewById(R.id.fechaMovimiento);
                                TextView importe = vistaMovimiento.findViewById(R.id.importeMovimiento);

                                concepto.setText(mov.getString("concepto"));
                                fecha.setText(mov.getString("fecha"));

                                double importeEntrada = mov.getDouble("importe");
                                importe.setText(String.format("%.2f €", importeEntrada));
                                if (importeEntrada < 0) {
                                    importe.setTextColor(android.graphics.Color.parseColor("#FF0000"));
                                } else {
                                    importe.setText(String.format("+%.2f €", importeEntrada));
                                    importe.setTextColor(android.graphics.Color.parseColor("#2ECC71"));
                                }

                                contenedorMovimientos.addView(vistaMovimiento);
                            }
                        }

                        saldoTotal.setText(String.format("%.2f €", saldoTotalEntrada));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    android.widget.Toast.makeText(this, "Token de sesión no válido", android.widget.Toast.LENGTH_SHORT).show();
                    cerrarSesion();
                },
                this
        );

        requestQueue.add(request);
    }

    private void mostrarPopupConfirmacionLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar sesión");
        builder.setMessage("Estás seguro de que quieres salir de la aplicación?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            SharedPreferences preferences = getSharedPreferences("SESSIONS_APP_PREFS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("VALID_TOKEN");
            editor.apply();

            Toast.makeText(MainActivity.this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
            cerrarSesion();
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cerrarSesion() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}