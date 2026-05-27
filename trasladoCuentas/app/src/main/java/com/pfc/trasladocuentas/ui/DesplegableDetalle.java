package com.pfc.trasladocuentas.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pfc.trasladocuentas.JsonObjectRequestWithCustomAuth;
import com.pfc.trasladocuentas.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class DesplegableDetalle extends BottomSheetDialogFragment {

    private String referencia;
    private TextView fecha;
    private CheckBox chkPetCancelar, chkPetBloquear, chkPetTransferirCierre, chkPetRecibirInfo, chkActHabilitar, chkActAceptar, chkActInformar;

    public static DesplegableDetalle newInstance(JSONObject solicitud) {
        DesplegableDetalle fragment = new DesplegableDetalle();
        Bundle args = new Bundle();
        args.putString("json", solicitud.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detalle, container, false);

        try {
            JSONObject s = new JSONObject(getArguments().getString("json"));
            referencia = s.getString("referencia");

            ((TextView) v.findViewById(R.id.referencia)).setText(referencia);
            ((TextView) v.findViewById(R.id.estado)).setText(s.getString("estado"));

            setCampo(v, R.id.lblEntidad, "Entidad Origen:", s.getString("entidad_origen"));
            setCampo(v, R.id.lblIbanOri, "IBAN Origen:", s.getString("iban_origen"));
            setCampo(v, R.id.lblIbanDest, "IBAN Destino:", s.getString("iban_destino"));
            setCampo(v, R.id.lblFechaSol, "Fecha Solicitud:", s.getString("fecha_solicitud"));

            fecha = v.findViewById(R.id.fecha);
            fecha.setText(s.getString("fecha_ejecucion"));

            chkPetCancelar = v.findViewById(R.id.petCancelar);
            chkPetBloquear = v.findViewById(R.id.petBloquear);
            chkPetTransferirCierre = v.findViewById(R.id.petTransferirCierre);
            chkPetRecibirInfo = v.findViewById(R.id.petRecibirInfo);
            chkActHabilitar = v.findViewById(R.id.actHabilitar);
            chkActAceptar = v.findViewById(R.id.actAceptar);
            chkActInformar = v.findViewById(R.id.actInformar);

            chkPetCancelar.setChecked(s.optBoolean("pet_cancelar_ordenes"));
            chkPetBloquear.setChecked(s.optBoolean("pet_bloquear_entrantes"));
            chkPetTransferirCierre.setChecked(s.optBoolean("pet_transferir_saldo_cierre"));
            chkPetRecibirInfo.setChecked(s.optBoolean("pet_recibir_info"));
            chkActHabilitar.setChecked(s.optBoolean("act_habilitar_ordenes"));
            chkActAceptar.setChecked(s.optBoolean("act_aceptar_adeudos"));
            chkActInformar.setChecked(s.optBoolean("act_informar_emisores"));

            if (!s.getString("estado").equalsIgnoreCase("PENDIENTE")) {
                v.findViewById(R.id.btnGuardar).setVisibility(View.GONE);
                v.findViewById(R.id.btnEliminar).setVisibility(View.GONE);
                fecha.setOnClickListener(null);
                desactivarCheckBoxes();

                if (s.getString("estado").equalsIgnoreCase("COMPLETADO")){
                    v.findViewById(R.id.btnDescargarPdf).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.btnDescargarPdf).setOnClickListener(b -> descargarPdf(referencia));
                } else {
                    v.findViewById(R.id.btnDescargarPdf).setVisibility(View.GONE);
                }
            } else {
                fecha.setOnClickListener(f -> abrirSelectorFecha());
                v.findViewById(R.id.btnGuardar).setOnClickListener(b -> enviarCambios(Request.Method.PUT));
                v.findViewById(R.id.btnEliminar).setOnClickListener(b -> enviarCambios(Request.Method.DELETE));
            }

            cargarDatosReales(referencia, s.getString("estado"), v);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

    private void cargarDatosReales(String ref, String estadoActual, View v) {
        String url = "http://10.0.2.2:8000/detalle-solicitud/" + ref + "/";

        JsonObjectRequestWithCustomAuth req = new JsonObjectRequestWithCustomAuth(
                Request.Method.GET,
                url,
                null,
                response -> {
                    chkPetCancelar.setChecked(response.optBoolean("pet_cancelar_ordenes", false));
                    chkPetBloquear.setChecked(response.optBoolean("pet_bloquear_entrantes", false));
                    chkPetTransferirCierre.setChecked(response.optBoolean("pet_transferir_saldo_cierre", false));
                    chkPetRecibirInfo.setChecked(response.optBoolean("pet_recibir_info", false));

                    chkActHabilitar.setChecked(response.optBoolean("act_habilitar_ordenes", false));
                    chkActAceptar.setChecked(response.optBoolean("act_aceptar_adeudos", false));
                    chkActInformar.setChecked(response.optBoolean("act_informar_emisores", false));

                    if (!estadoActual.equalsIgnoreCase("PENDIENTE")) {
                        v.findViewById(R.id.btnGuardar).setVisibility(View.GONE);
                        v.findViewById(R.id.btnEliminar).setVisibility(View.GONE);
                        fecha.setOnClickListener(null);
                        desactivarCheckBoxes();
                    } else {
                        fecha.setOnClickListener(f -> abrirSelectorFecha());
                        v.findViewById(R.id.btnGuardar).setOnClickListener(b -> enviarCambios(Request.Method.PUT));
                        v.findViewById(R.id.btnEliminar).setOnClickListener(b -> enviarCambios(Request.Method.DELETE));
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error al cargar los checkss.", Toast.LENGTH_SHORT).show();
                },
                requireContext()
        );

        Volley.newRequestQueue(requireContext()).add(req);
    }

    private void desactivarCheckBoxes() {
        CheckBox[] boxes = {chkPetCancelar, chkPetBloquear, chkPetTransferirCierre, chkPetRecibirInfo, chkActHabilitar, chkActAceptar, chkActInformar};
        for (CheckBox cb : boxes) cb.setClickable(false);
    }

    private void enviarCambios(int metodo) {
        String url = "http://10.0.2.2:8000/detalle-solicitud/" + referencia + "/";
        JSONObject body = (metodo == Request.Method.PUT) ? construirJson() : null;

        JsonObjectRequestWithCustomAuth req = new JsonObjectRequestWithCustomAuth(metodo, url, body, res -> {
            Toast.makeText(getContext(), "Solicitud actualizada.", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof DashboardActivity) ((DashboardActivity) getActivity()).obtenerSolicitudes();
            dismiss();
        }, err -> Toast.makeText(getContext(), "Error en servidor.", Toast.LENGTH_SHORT).show(), requireContext());

        Volley.newRequestQueue(requireContext()).add(req);
    }

    private JSONObject construirJson() {
        JSONObject j = new JSONObject();
        try {
            j.put("fecha_ejecucion", fecha.getText().toString());

            j.put("pet_cancelar_ordenes", chkPetCancelar.isChecked());
            j.put("pet_bloquear_entrantes", chkPetBloquear.isChecked());
            j.put("pet_transferir_saldo_cierre", chkPetTransferirCierre.isChecked());
            j.put("pet_recibir_info", chkPetRecibirInfo.isChecked());

            j.put("act_habilitar_ordenes", chkActHabilitar.isChecked());
            j.put("act_aceptar_adeudos", chkActAceptar.isChecked());
            j.put("act_informar_emisores", chkActInformar.isChecked());

        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error al construir el JSON de guardado: " + e.getMessage());
        }
        return j;
    }

    private void abrirSelectorFecha() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (v, y, m, d) -> fecha.setText(String.format("%04d-%02d-%02d", y, m + 1, d)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setCampo(View v, int idContenedor, String label, String valor) {
        View contenedor = v.findViewById(idContenedor);
        ((TextView) contenedor.findViewById(R.id.label)).setText(label);
        ((TextView) contenedor.findViewById(R.id.valor)).setText(valor);
    }

    private void descargarPdf(String ref) {
        String url = "http://10.0.2.2:8000/generar-pdf/" + ref + "/";

        android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(android.net.Uri.parse(url));
        request.setTitle("Solicitud " + ref);
        request.setDescription("Descargando documento PDF...");

        android.content.SharedPreferences preferences = requireContext().getSharedPreferences("SESSIONS_APP_PREFS", android.content.Context.MODE_PRIVATE);
        String token = preferences.getString("VALID_TOKEN", "");
        request.addRequestHeader("Session", token);

        request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, "solicitud_" + ref + ".pdf");

        android.app.DownloadManager manager = (android.app.DownloadManager) requireContext().getSystemService(android.content.Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
            Toast.makeText(getContext(), "Descarga iniciada...", Toast.LENGTH_SHORT).show();
        }
    }
}