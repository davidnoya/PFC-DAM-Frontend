package com.pfc.trasladocuentas.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pfc.trasladocuentas.R;
import com.pfc.trasladocuentas.models.IbanFormatter;

import java.util.Calendar;

public class Paso2Fragment extends Fragment {

    private TextInputEditText ibanDestino, fechaTraslado;
    private MaterialButton btnSiguiente;
    private Calendar fechaSeleccionada;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paso2, container, false);

        ibanDestino = view.findViewById(R.id.ibanDestino);
        ibanDestino.addTextChangedListener(new IbanFormatter(ibanDestino));
        fechaTraslado = view.findViewById(R.id.fechaTraslado);
        btnSiguiente = view.findViewById(R.id.btnSiguiente2);

        fechaTraslado.setOnClickListener(v -> abrirCalendario());

        btnSiguiente.setOnClickListener(v -> {
            if (validarPaso2()) {
                if (getActivity() instanceof NuevaSolicitudActivity) {
                    ((NuevaSolicitudActivity) getActivity()).cambiarAPaso(R.id.nav_paso3);
                }
            }
        });

        return view;
    }

    private void abrirCalendario() {
        Calendar hoy = Calendar.getInstance();

        Calendar minFecha = Calendar.getInstance();
        minFecha.add(Calendar.DAY_OF_YEAR, 13);

        DatePickerDialog picker = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            fechaSeleccionada = Calendar.getInstance();
            fechaSeleccionada.set(year, month, dayOfMonth);

            String fechaTexto = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            fechaTraslado.setText(fechaTexto);
        }, hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH));

        picker.getDatePicker().setMinDate(minFecha.getTimeInMillis());
        picker.show();
    }

    private boolean validarPaso2() {
        String iban = ibanDestino.getText().toString().trim();
        String fecha = fechaTraslado.getText().toString().trim();

        if (iban.length() != 29) {
            ibanDestino.setError("Introduzca un IBAN válido");
            return false;
        }

        if (fecha.isEmpty()) {
            Toast.makeText(getContext(), "Debe seleccionar una fecha", Toast.LENGTH_SHORT).show();
            return false;
        }

        ibanDestino.setError(null);
        return true;
    }

    public String getIban() {
        return ibanDestino.getText().toString().replaceAll(" ", "");
    }
    public String getFecha() {
        String[] partes = fechaTraslado.getText().toString().split("/");
        return partes[2] + "-" + partes[1] + "-" + partes[0];
    }
}