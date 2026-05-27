package com.pfc.trasladocuentas.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pfc.trasladocuentas.R;
import com.pfc.trasladocuentas.models.IbanFormatter;

public class Paso1Fragment extends Fragment {

    private AutoCompleteTextView entidadOrigen;
    private TextInputEditText ibanOrigen;
    private MaterialButton btnSiguiente;

    public Paso1Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paso1, container, false);

        entidadOrigen = view.findViewById(R.id.entidadOrigen);
        ibanOrigen = view.findViewById(R.id.ibanOrigen);
        ibanOrigen.addTextChangedListener(new IbanFormatter(ibanOrigen));
        btnSiguiente = view.findViewById(R.id.btnSiguiente);

        configurarDesplegable();

        btnSiguiente.setOnClickListener(v -> {
            if (validarCampos()) {
                if (getActivity() instanceof  NuevaSolicitudActivity) {
                    ((NuevaSolicitudActivity) getActivity()).cambiarAPaso(R.id.nav_paso2);
                }
            }
        });

        return view;
    }

    private void configurarDesplegable() {
        String[] bancos = new String[]{"Santander", "BBVA", "CaixaBank", "Sabadell", "Bankinter", "ING", "Unicaja", "Kutxabank"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, bancos);
        entidadOrigen.setAdapter(adapter);
    }

    private boolean validarCampos() {
        String entidad = entidadOrigen.getText().toString().trim();
        String iban = ibanOrigen.getText() != null ? ibanOrigen.getText().toString().trim() : "";

        if (entidad.isEmpty()) {
            entidadOrigen.setError("Selecciona una entidad");
            return false;
        }

        if (iban.length() != 29) {
            ibanOrigen.setError("Introduzca un IBAN válido");
            return false;
        }

        entidadOrigen.setError(null);
        ibanOrigen.setError(null);
        return true;
    }

    public String getEntidad() {
        return entidadOrigen.getText().toString();
    }
    public String getIban() {
        return ibanOrigen.getText().toString().replaceAll(" ", "");
    }
}