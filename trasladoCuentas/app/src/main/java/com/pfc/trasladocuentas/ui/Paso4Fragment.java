package com.pfc.trasladocuentas.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.pfc.trasladocuentas.R;

public class Paso4Fragment extends Fragment {

    public CheckBox chkHabilitar, chkAceptar, chkInformar;
    private MaterialButton btnFinalizar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paso4, container, false);

        chkHabilitar = view.findViewById(R.id.chkHabilitar);
        chkAceptar = view.findViewById(R.id.chkAceptar);
        chkInformar = view.findViewById(R.id.chkInformar);
        btnFinalizar = view.findViewById(R.id.btnFinalizar);

        btnFinalizar.setOnClickListener(v -> {
            if (getActivity() instanceof NuevaSolicitudActivity) {
                ((NuevaSolicitudActivity) getActivity()).enviarSolicitudFinal();
            }
        });

        return view;
    }
}