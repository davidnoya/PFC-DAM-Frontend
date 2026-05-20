package com.pfc.trasladocuentas.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import com.pfc.trasladocuentas.R;

public class LoginActivity extends AppCompatActivity {

    private EditText dni, password;
    private Button btnLogin;
    private TextView textoRegistro;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dni = findViewById(R.id.dni);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        textoRegistro = findViewById(R.id.textoRegistro);

        requestQueue = Volley.newRequestQueue(this);

        btnLogin.setOnClickListener(v -> hacerLogin());

        textoRegistro.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Abriendo registro", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private void hacerLogin() {
        String dniEntrada = dni.getText().toString().trim();
        String passwordEntrada = password.getText().toString().trim();

        if (dniEntrada.isEmpty() || passwordEntrada.isEmpty()) {
            Toast.makeText(this, "No has completado todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject parametros = new JSONObject();
        try {
            parametros.put("dni", dniEntrada);
            parametros.put("password", passwordEntrada);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2:8000/login/";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                parametros,
                response -> {
                    try {
                        String token = response.getString("token");
                        guardarToken(token);

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Datos de inicio incorrectos.", Toast.LENGTH_LONG).show();

                }
        );

        requestQueue.add(request);
    }

    private void guardarToken(String token) {
        SharedPreferences preferences = getSharedPreferences("SESSIONS_APP_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("VALID_TOKEN", token);
        editor.apply();
    }
}
