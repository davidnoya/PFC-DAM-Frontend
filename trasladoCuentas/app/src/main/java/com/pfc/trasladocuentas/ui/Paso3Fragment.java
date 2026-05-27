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

public class Paso3Fragment extends Fragment {

    public CheckBox chkCancelar, chkBloquear, chkTransferirCierre, chkRecibirInfo;
    private MaterialButton btnSiguiente;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paso3, container, false);

        chkCancelar = view.findViewById(R.id.chkCancelar);
        chkBloquear = view.findViewById(R.id.chkBloquear);
        chkTransferirCierre = view.findViewById(R.id.chkTransferirCierre);
        chkRecibirInfo = view.findViewById(R.id.chkRecibirInfo);
        btnSiguiente = view.findViewById(R.id.btnSiguiente3);

        btnSiguiente.setOnClickListener(v -> {
            if (getActivity() instanceof NuevaSolicitudActivity) {
                ((NuevaSolicitudActivity) getActivity()).cambiarAPaso(R.id.nav_paso4);
            }
        });

        return view;
    }
}