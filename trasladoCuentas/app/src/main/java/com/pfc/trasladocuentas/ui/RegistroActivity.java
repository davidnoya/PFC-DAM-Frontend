package com.pfc.trasladocuentas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pfc.trasladocuentas.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistroActivity extends AppCompatActivity {

    private EditText nombre, apellidos, dni, email, telefono, password, repetirPassword;
    private Button btnCrearCuenta;
    private TextView volverLogin;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombre = findViewById(R.id.nombreRegistro);
        apellidos = findViewById(R.id.apellidosRegistro);
        dni = findViewById(R.id.dniRegistro);
        email = findViewById(R.id.emailRegistro);
        telefono = findViewById(R.id.telefonoRegistro);
        password = findViewById(R.id.passwordRegistro);
        repetirPassword = findViewById(R.id.repetirPasswordRegistro);

        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        volverLogin = findViewById(R.id.textoVolverLogin);

        requestQueue = Volley.newRequestQueue(this);

        btnCrearCuenta.setOnClickListener(v -> hacerRegistro());
        volverLogin.setOnClickListener(v -> finish());
    }

    private void hacerRegistro() {
        String nombreEntrada = nombre.getText().toString().trim();
        String apellidosEntrada = apellidos.getText().toString().trim();
        String dniEntrada = dni.getText().toString().trim();
        String emailEntrada = email.getText().toString().trim();
        String telefonoEntrada = telefono.getText().toString().trim();
        String passwordEntrada = password.getText().toString().trim();
        String repetirPasswordEntrada = repetirPassword.getText().toString().trim();

        if (nombreEntrada.isEmpty() || apellidosEntrada.isEmpty() || dniEntrada.isEmpty() ||
                emailEntrada.isEmpty() || telefonoEntrada.isEmpty() || passwordEntrada.isEmpty() || repetirPasswordEntrada.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordEntrada.equals(repetirPasswordEntrada)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject parametros = new JSONObject();
        try {
            parametros.put("nombre", nombreEntrada);
            parametros.put("apellidos", apellidosEntrada);
            parametros.put("dni", dniEntrada);
            parametros.put("email", emailEntrada);
            parametros.put("telefono", telefonoEntrada);
            parametros.put("password", passwordEntrada);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2:8000/registro/";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                parametros,
                response -> {
                    Toast.makeText(this, "Cuenta creada.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    try {
                        Toast.makeText(this, "Error al registrarse.", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        requestQueue.add(request);
    }
}
