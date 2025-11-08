package com.example.breadapp_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Log_in_activity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin, btnGoToRegister, btnForgotPassword;

    private static final String LOGIN_URL = "http://10.0.2.2/breadapp/login.php";
    private static final String PREF_NAME = "BreadAppPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("LoginActivity", "✅ Actividad de LOGIN iniciada");

        initializeViews();
        setupClickListeners();

        checkExistingSession();
    }

    private void initializeViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        Log.d("LoginActivity", "btnLogin: " + (btnLogin != null));
        Log.d("LoginActivity", "btnGoToRegister: " + (btnGoToRegister != null));
        Log.d("LoginActivity", "btnForgotPassword: " + (btnForgotPassword != null));
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LoginActivity", "🔵 Botón LOGIN clickeado");
                loginUser();
            }
        });

        if (btnGoToRegister != null) {
            btnGoToRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("LoginActivity", "🔵 Botón IR A REGISTRO clickeado");
                    goToRegisterActivity();
                }
            });
        }

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Log_in_activity.this, "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToRegisterActivity() {
        Log.d("LoginActivity", "🔄 Cambiando a MainActivity (Registro)");
        Intent intent = new Intent(Log_in_activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkExistingSession() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            Log.d("LoginActivity", "✅ Sesión existente encontrada, redirigiendo al home");
            Intent intent = new Intent(Log_in_activity.this, Home_activity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d("LoginActivity", "ℹ️ No hay sesión activa");
        }
    }

    private void loginUser() {
        String username = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (validateInputs(username, password)) {
            new LoginTask().execute(username, password);
        }
    }

    private boolean validateInputs(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            HttpURLConnection connection = null;
            try {
                URL url = new URL(LOGIN_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", username);
                jsonParam.put("password", password);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                Log.d("LoginTask", "Código de respuesta HTTP: " + responseCode);

                InputStream inputStream;
                if (responseCode >= 200 && responseCode < 300) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                inputStream.close();

                return response.toString();

            } catch (Exception e) {
                Log.e("LoginTask", "Error: " + e.getMessage());
                return "{\"success\":false,\"message\":\"Error de conexión: " + e.getMessage() + "\"}";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("LoginTask", "Respuesta completa: " + result);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                boolean success = jsonResponse.getBoolean("success");
                String message = jsonResponse.getString("message");

                if (success) {
                    saveUserSession(jsonResponse);
                    Toast.makeText(Log_in_activity.this, message, Toast.LENGTH_SHORT).show();
                    clearFields();

                    Intent intent = new Intent(Log_in_activity.this, Home_activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Log_in_activity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.e("LoginTask", "JSON Error: " + e.getMessage());
                Toast.makeText(Log_in_activity.this, "Error en la respuesta: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveUserSession(JSONObject jsonResponse) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putBoolean("isLoggedIn", true);

            if (jsonResponse.has("user")) {
                JSONObject user = jsonResponse.getJSONObject("user");
                editor.putInt("userId", user.getInt("id"));
                editor.putString("username", user.getString("username"));
                editor.putString("email", user.getString("email"));
                editor.putString("name", user.getString("name"));
                editor.putString("surname", user.getString("surname"));
            }

            editor.apply();
            Log.d("LoginTask", "✅ Sesión guardada correctamente");

        } catch (JSONException e) {
            Log.e("LoginTask", "❌ Error guardando sesión: " + e.getMessage());
        }
    }

    private void clearFields() {
        editTextEmail.setText("");
        editTextPassword.setText("");
    }
}