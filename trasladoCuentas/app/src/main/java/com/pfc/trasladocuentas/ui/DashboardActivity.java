package com.pfc.trasladocuentas.ui;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.pfc.trasladocuentas.R;
import com.pfc.trasladocuentas.JsonArrayRequestWithCustomAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private LinearLayout contenedorSolicitudes;
    private TextView mensajeVacio;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = findViewById(R.id.toolbarTraslados);
        contenedorSolicitudes = findViewById(R.id.contenedorSolicitudes);
        mensajeVacio = findViewById(R.id.mensajeVacio);
        requestQueue = Volley.newRequestQueue(this);

        configurarToolbar();
        obtenerSolicitudes();
    }

    private void configurarToolbar() {
        Drawable flecha = ContextCompat.getDrawable(this, R.drawable.ic_atras);
        toolbar.setNavigationIcon(flecha);

        toolbar.setNavigationOnClickListener(v -> finish());

        toolbar.inflateMenu(R.menu.menu_traslados);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_nueva_solicitud) {
                Toast.makeText(this, "Nueva solicitud", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    public void obtenerSolicitudes() {
        String url = "http://10.0.2.2:8000/solicitudes/";

        JsonArrayRequestWithCustomAuth request = new JsonArrayRequestWithCustomAuth(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        contenedorSolicitudes.removeAllViews();

                        if (response.length() == 0) {
                            mensajeVacio.setVisibility(View.VISIBLE);
                            return;
                        }
                        mensajeVacio.setVisibility(View.GONE);

                        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject solicitud = response.getJSONObject(i);

                            View vistaSolicitud = inflater.inflate(R.layout.item_solicitud, contenedorSolicitudes, false);

                            TextView referencia = vistaSolicitud.findViewById(R.id.referenciaSolicitud);
                            TextView iban = vistaSolicitud.findViewById(R.id.ibanOrigen);
                            TextView estado = vistaSolicitud.findViewById(R.id.estadoSolicitud);
                            TextView fecha = vistaSolicitud.findViewById(R.id.fechaSolicitud);

                            String referenciaEntrada = solicitud.getString("referencia");
                            referencia.setText(referenciaEntrada);

                            String ibanCompleto = solicitud.getString("iban_origen");
                            String ibanOculto = "**** " + (ibanCompleto.length() > 4 ? ibanCompleto.substring(ibanCompleto.length() - 4) : ibanCompleto);
                            iban.setText(ibanOculto);

                            fecha.setText("Solicitado el: " + solicitud.getString("fecha_solicitud"));

                            String estadoEntrada = solicitud.getString("estado").toUpperCase();
                            estado.setText(estadoEntrada);

                            if (estadoEntrada.equals("COMPLETADO")) {
                                estado.setTextColor(Color.parseColor("#2ECC71"));
                                estado.setBackgroundColor(Color.parseColor("#EAFAF1"));
                            } else if (estadoEntrada.equals("RECHAZADO")) {
                                estado.setTextColor(Color.parseColor("#E74C3C"));
                                estado.setBackgroundColor(Color.parseColor("#FDEDEC"));
                            }

                            vistaSolicitud.setOnClickListener(v -> {
                                DesplegableDetalle df = DesplegableDetalle.newInstance(solicitud);
                                df.show(getSupportFragmentManager(), "Detalle");
                            });

                            contenedorSolicitudes.addView(vistaSolicitud);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error al cargar el dashboard", Toast.LENGTH_SHORT).show();
                },
                this
        );

        requestQueue.add(request);
    }
}