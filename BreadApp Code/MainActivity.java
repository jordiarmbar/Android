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
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextSurname, editTextUsername, editTextEmail,
            editTextPhone, editTextPassword, editTextConfirmPassword;
    private Button btnRegister, btnGoToLogin, btnStayInRegister;

    private static final String REGISTER_URL = "http://10.0.2.2/breadapp/register.php";
    private static final String PREF_NAME = "BreadAppPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Log.d("MainActivity", "✅ Actividad de REGISTRO iniciada");

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnStayInRegister = findViewById(R.id.btnStayInRegister);

        Log.d("MainActivity", "btnGoToLogin: " + (btnGoToLogin != null));
        Log.d("MainActivity", "btnStayInRegister: " + (btnStayInRegister != null));
        Log.d("MainActivity", "btnRegister: " + (btnRegister != null));
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "🔵 Botón REGISTRAR clickeado");
                registerUser();
            }
        });

        if (btnGoToLogin != null) {
            btnGoToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "🔵 Botón IR A LOGIN clickeado");
                    Toast.makeText(MainActivity.this, "Cambiando a Login...", Toast.LENGTH_SHORT).show();
                    goToLoginActivity();
                }
            });
        }

        if (btnStayInRegister != null) {
            btnStayInRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "🔵 Botón MANTENER EN REGISTRO clickeado");
                    Toast.makeText(MainActivity.this, "Ya estás en registro", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void goToLoginActivity() {
        Log.d("MainActivity", "🔄 Cambiando a LoginActivity");
        Intent intent = new Intent(MainActivity.this, Log_in_activity.class);
        startActivity(intent);
        finish();
    }

    private void goToHomeActivity() {
        Log.d("MainActivity", "🔄 Redirigiendo a Home después del registro");
        Intent intent = new Intent(MainActivity.this, Home_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void registerUser() {
        if (editTextName == null || editTextSurname == null || editTextUsername == null ||
                editTextEmail == null || editTextPhone == null || editTextPassword == null ||
                editTextConfirmPassword == null) {
            Toast.makeText(this, "Error: Campos no inicializados", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (validateInputs(name, surname, username, email, phone, password, confirmPassword)) {
            new RegisterTask().execute(name, surname, username, email, phone, password);
        }
    }

    private boolean validateInputs(String name, String surname, String username, String email,
                                   String phone, String password, String confirmPassword) {

        if (name.isEmpty() || surname.isEmpty() || username.isEmpty() ||
                email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, introduce un email válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String surname = params[1];
            String username = params[2];
            String email = params[3];
            String phone = params[4];
            String password = params[5];

            HttpURLConnection connection = null;
            try {
                URL url = new URL(REGISTER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "name=" + URLEncoder.encode(name, "UTF-8") +
                        "&surname=" + URLEncoder.encode(surname, "UTF-8") +
                        "&username=" + URLEncoder.encode(username, "UTF-8") +
                        "&email=" + URLEncoder.encode(email, "UTF-8") +
                        "&phone=" + URLEncoder.encode(phone, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                Log.d("RegisterTask", "Enviando datos: " + postData);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                Log.d("RegisterTask", "Código de respuesta HTTP: " + responseCode);

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
                Log.e("RegisterTask", "Error: " + e.getMessage());
                return "{\"success\":false,\"message\":\"Error de conexión: " + e.getMessage() + "\"}";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("RegisterTask", "Respuesta: " + result);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                boolean success = jsonResponse.getBoolean("success");
                String message = jsonResponse.getString("message");

                if (success) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    clearFields();

                    // Obtener datos del registro
                    String name = editTextName.getText().toString().trim();
                    String surname = editTextSurname.getText().toString().trim();
                    String username = editTextUsername.getText().toString().trim();
                    String email = editTextEmail.getText().toString().trim();

                    // Guardar sesión automáticamente
                    saveUserSession(name, surname, username, email);

                    // Redirigir al HOME
                    goToHomeActivity();

                } else {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, "Error en la respuesta", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveUserSession(String name, String surname, String username, String email) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("isLoggedIn", true);
        editor.putString("name", name);
        editor.putString("surname", surname);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putInt("userId", (int) System.currentTimeMillis());

        editor.apply();
        Log.d("RegisterTask", "✅ Sesión guardada después del registro");
    }

    private void clearFields() {
        if (editTextName != null) editTextName.setText("");
        if (editTextSurname != null) editTextSurname.setText("");
        if (editTextUsername != null) editTextUsername.setText("");
        if (editTextEmail != null) editTextEmail.setText("");
        if (editTextPhone != null) editTextPhone.setText("");
        if (editTextPassword != null) editTextPassword.setText("");
        if (editTextConfirmPassword != null) editTextConfirmPassword.setText("");
    }
}