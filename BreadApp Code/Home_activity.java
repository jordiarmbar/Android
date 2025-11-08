package com.example.breadapp_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Home_activity extends AppCompatActivity {

    private LinearLayout btnTienda, btnCarrito, btnMapa, btnOpciones;
    private TextView textViewWelcome, textViewUserInfo;
    private Spinner spinnerUsers;
    private Button btnLoadUsers;

    private static final String PREF_NAME = "BreadAppPrefs";
    private static final String USERS_URL = "http://192.168.1.100/breadapp/get_users.php";

    private ArrayList<String> userList;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<JSONObject> usersData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        setupClickListeners();
        showWelcomeMessage();
        setupSpinner();
    }

    private void initializeViews() {
        btnTienda = findViewById(R.id.btnTienda);
        btnCarrito = findViewById(R.id.btnCarrito);
        btnMapa = findViewById(R.id.btnMapa);
        btnOpciones = findViewById(R.id.btnOpciones);
        textViewWelcome = findViewById(R.id.textView3);

        // Nuevos elementos para el Spinner
        spinnerUsers = findViewById(R.id.spinnerProducts);
        btnLoadUsers = findViewById(R.id.btnLoadProducts);
        textViewUserInfo = findViewById(R.id.textViewUserInfo);

        userList = new ArrayList<>();
        userList.add("Selecciona un usuario");
        usersData = new ArrayList<>();
    }

    private void setupSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(spinnerAdapter);

        spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedUser = userList.get(position);
                    Toast.makeText(Home_activity.this, "Usuario seleccionado: " + selectedUser, Toast.LENGTH_SHORT).show();
                    showUserInfo(position - 1);
                } else {
                    textViewUserInfo.setText("Selecciona un usuario para ver su información");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textViewUserInfo.setText("Selecciona un usuario para ver su información");
            }
        });
    }

    private void showUserInfo(int userIndex) {
        try {
            if (userIndex >= 0 && userIndex < usersData.size()) {
                JSONObject user = usersData.get(userIndex);
                String name = user.getString("name");
                String surname = user.getString("surname");
                String username = user.getString("username");
                String email = user.getString("email");
                String phone = user.getString("phone");

                String userInfo = "Nombre: " + name + " " + surname + "\n" +
                        "Usuario: " + username + "\n" +
                        "Email: " + email + "\n" +
                        "Teléfono: " + phone;

                textViewUserInfo.setText(userInfo);
            }
        } catch (JSONException e) {
            Log.e("UserInfo", "Error mostrando información: " + e.getMessage());
            textViewUserInfo.setText("Error cargando información del usuario");
        }
    }

    private void setupClickListeners() {
        btnTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home_activity.this, "Abriendo Tienda", Toast.LENGTH_SHORT).show();
            }
        });

        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home_activity.this, "Abriendo Carrito", Toast.LENGTH_SHORT).show();
            }
        });

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home_activity.this, "Abriendo Mapa", Toast.LENGTH_SHORT).show();
            }
        });

        btnOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CAMBIADO: Ahora abre la configuración en lugar de cerrar sesión
                abrirConfiguracion();
            }
        });

        btnLoadUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUsersFromDatabase();
            }
        });
    }

    // NUEVO MÉTODO: Abrir la actividad de configuración
    private void abrirConfiguracion() {
        try {
            Intent intent = new Intent(Home_activity.this, SettingsActivity.class);
            startActivity(intent);
            // No llamamos finish() para que el usuario pueda volver al home con el botón back
            Toast.makeText(this, "Abriendo configuración", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: SettingsActivity no encontrada", Toast.LENGTH_LONG).show();
            Log.e("HomeActivity", "Error abriendo SettingsActivity: " + e.getMessage());
        }
    }

    private void loadUsersFromDatabase() {
        new LoadUsersTask().execute();
    }

    private class LoadUsersTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            boolean useTestMode = true;

            if (useTestMode) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "{\"success\":true,\"users\":[" +
                        "{\"id\":1,\"name\":\"Juan\",\"surname\":\"Pérez\",\"username\":\"juanperez\",\"email\":\"juan@email.com\",\"phone\":\"123456789\"}," +
                        "{\"id\":2,\"name\":\"María\",\"surname\":\"García\",\"username\":\"mariagarcia\",\"email\":\"maria@email.com\",\"phone\":\"987654321\"}," +
                        "{\"id\":3,\"name\":\"Carlos\",\"surname\":\"López\",\"username\":\"carloslopez\",\"email\":\"carlos@email.com\",\"phone\":\"555555555\"}," +
                        "{\"id\":4,\"name\":\"Ana\",\"surname\":\"Martínez\",\"username\":\"anamartinez\",\"email\":\"ana@email.com\",\"phone\":\"111111111\"}," +
                        "{\"id\":5,\"name\":\"Pedro\",\"surname\":\"Rodríguez\",\"username\":\"pedrorodriguez\",\"email\":\"pedro@email.com\",\"phone\":\"999999999\"}" +
                        "]}";
            }

            HttpURLConnection connection = null;
            try {
                URL url = new URL(USERS_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return "{\"success\":false,\"message\":\"Error HTTP: " + responseCode + "\"}";
                }
            } catch (Exception e) {
                return "{\"success\":false,\"message\":\"Error: " + e.getMessage() + "\"}";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("LoadUsers", "Respuesta: " + result);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    if (userList.size() > 1) {
                        userList.subList(1, userList.size()).clear();
                    }
                    usersData.clear();

                    JSONArray usersArray = jsonResponse.getJSONArray("users");
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject user = usersArray.getJSONObject(i);
                        usersData.add(user);

                        String userName = user.getString("name");
                        String userSurname = user.getString("surname");
                        String username = user.getString("username");
                        String displayText = userName + " " + userSurname + " (" + username + ")";
                        userList.add(displayText);
                    }

                    spinnerAdapter.notifyDataSetChanged();
                    Toast.makeText(Home_activity.this, "Usuarios cargados: " + usersArray.length(), Toast.LENGTH_SHORT).show();

                    if (usersArray.length() > 0) {
                        spinnerUsers.setSelection(1);
                    }
                } else {
                    String message = jsonResponse.getString("message");
                    Toast.makeText(Home_activity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(Home_activity.this, "Error procesando usuarios", Toast.LENGTH_SHORT).show();
                Log.e("LoadUsers", "JSON Error: " + e.getMessage());
            }
        }
    }

    private void showWelcomeMessage() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String surname = prefs.getString("surname", "");
        String username = prefs.getString("username", "Usuario");

        String welcomeMessage;
        if (!name.isEmpty() && !surname.isEmpty()) {
            welcomeMessage = "¡Bienvenido " + name + " " + surname + "!";
        } else if (!name.isEmpty()) {
            welcomeMessage = "¡Bienvenido " + name + "!";
        } else {
            welcomeMessage = "¡Bienvenido " + username + "!";
        }

        textViewWelcome.setText(welcomeMessage);
    }

    // ELIMINADO: El método cerrarSesion() ya no se usa aquí, está en SettingsActivity

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Presiona Opciones para abrir configuración", Toast.LENGTH_SHORT).show();
    }
}