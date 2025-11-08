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
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
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

public class SettingsActivity extends AppCompatActivity {

    private TextView textViewVolver;
    private TextView textViewCambiarEmail;
    private TextView textViewCambiarPassword;
    private TextView textViewIdiomaRegion;
    private TextView textViewCambiarMetodoPago;
    private TextView textViewCambiarTelefono;
    private Switch switchModoOscuro;
    private Switch switchNotificaciones;
    private Button btnCerrarSesion;

    // Elementos para el Spinner de tablas
    private Spinner spinnerTables;
    private Button btnLoadTables;

    private static final String PREF_NAME = "BreadAppPrefs";
    private static final String TABLES_URL = "http://10.0.2.2/breadapp/get_tables.php";

    private ArrayList<String> tablesList;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        initializeViews();
        setupClickListeners();
        loadSettings();
        setupSpinner();
    }

    private void initializeViews() {
        // TextViews clickeables
        textViewVolver = findViewById(R.id.textView12);
        textViewCambiarEmail = findViewById(R.id.textView13);
        textViewCambiarPassword = findViewById(R.id.textView15);
        textViewIdiomaRegion = findViewById(R.id.textView20);
        textViewCambiarMetodoPago = findViewById(R.id.textView23);
        textViewCambiarTelefono = findViewById(R.id.textView24);

        // Switches
        switchModoOscuro = findViewById(R.id.switch1);
        switchNotificaciones = findViewById(R.id.switch2);

        // Botón cerrar sesión
        btnCerrarSesion = findViewById(R.id.LogOut);

        // Elementos del Spinner
        spinnerTables = findViewById(R.id.spinnerTables);
        btnLoadTables = findViewById(R.id.btnLoadTables);

        tablesList = new ArrayList<>();
        tablesList.add("Selecciona una tabla");
    }

    private void setupSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tablesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTables.setAdapter(spinnerAdapter);

        spinnerTables.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedTable = tablesList.get(position);
                    Toast.makeText(SettingsActivity.this, "Tabla seleccionada: " + selectedTable, Toast.LENGTH_SHORT).show();

                    // Aquí puedes hacer algo con la tabla seleccionada
                    Log.d("SettingsActivity", "Tabla seleccionada: " + selectedTable);

                    // Mostrar más información sobre la tabla seleccionada
                    showTableDetails(selectedTable);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se seleccionó nada
            }
        });
    }

    private void showTableDetails(String tableName) {
        // Aquí puedes implementar la lógica para mostrar detalles de la tabla
        // Por ejemplo, contar registros, mostrar estructura, etc.
        Log.i("TableDetails", "Mostrando detalles para: " + tableName);
    }

    private void setupClickListeners() {
        // Botón Volver
        textViewVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverAlHome();
            }
        });

        // Botón para cargar tablas
        btnLoadTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTablesFromDatabase();
            }
        });

        // Opción: Cambiar correo electrónico
        textViewCambiarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Funcionalidad en desarrollo: Cambiar correo", Toast.LENGTH_SHORT).show();
            }
        });

        // Opción: Cambiar contraseña
        textViewCambiarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Funcionalidad en desarrollo: Cambiar contraseña", Toast.LENGTH_SHORT).show();
            }
        });

        // Opción: Idioma y región
        textViewIdiomaRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Funcionalidad en desarrollo: Idioma y región", Toast.LENGTH_SHORT).show();
            }
        });

        // Opción: Cambiar método de pago
        textViewCambiarMetodoPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Funcionalidad en desarrollo: Método de pago", Toast.LENGTH_SHORT).show();
            }
        });

        // Opción: Cambiar número de teléfono
        textViewCambiarTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Funcionalidad en desarrollo: Cambiar teléfono", Toast.LENGTH_SHORT).show();
            }
        });

        // Switch Modo Oscuro
        switchModoOscuro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                guardarConfiguracion();
                Toast.makeText(SettingsActivity.this,
                        "Modo oscuro " + (isChecked ? "activado" : "desactivado"),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Switch Notificaciones
        switchNotificaciones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                guardarConfiguracion();
                Toast.makeText(SettingsActivity.this,
                        "Notificaciones " + (isChecked ? "activadas" : "desactivadas"),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
    }

    private void loadTablesFromDatabase() {
        // Mostrar loading
        btnLoadTables.setText("Cargando...");
        btnLoadTables.setEnabled(false);

        new LoadTablesTask().execute();
    }

    private class LoadTablesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // Cambia a false para conectar con la base de datos real
            boolean useTestMode = false; // AHORA CONECTANDO CON BD REAL

            if (useTestMode) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Datos de prueba
                return "{\"success\":true,\"tables\":[" +
                        "{\"table_name\":\"users\"}," +
                        "{\"table_name\":\"products\"}," +
                        "{\"table_name\":\"orders\"}," +
                        "{\"table_name\":\"categories\"}," +
                        "{\"table_name\":\"reviews\"}," +
                        "{\"table_name\":\"order_items\"}," +
                        "{\"table_name\":\"payments\"}" +
                        "]}";
            }

            // Código para conectar con la base de datos real
            HttpURLConnection connection = null;
            try {
                URL url = new URL(TABLES_URL);
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
                Log.e("LoadTables", "Error en conexión: " + e.getMessage());
                return "{\"success\":false,\"message\":\"Error de conexión: " + e.getMessage() + "\"}";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Restaurar botón
            btnLoadTables.setText("Cargar Tablas");
            btnLoadTables.setEnabled(true);

            Log.d("LoadTables", "Respuesta: " + result);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    // Limpiar la lista actual (excepto el primer elemento)
                    if (tablesList.size() > 1) {
                        tablesList.subList(1, tablesList.size()).clear();
                    }

                    // Procesar las tablas
                    JSONArray tablesArray = jsonResponse.getJSONArray("tables");
                    for (int i = 0; i < tablesArray.length(); i++) {
                        JSONObject table = tablesArray.getJSONObject(i);
                        String tableName = table.getString("table_name");
                        String formattedName = formatTableName(tableName);
                        tablesList.add(formattedName);
                    }

                    // Notificar al adapter que los datos cambiaron
                    spinnerAdapter.notifyDataSetChanged();

                    int tableCount = tablesArray.length();
                    Toast.makeText(SettingsActivity.this,
                            tableCount + " tablas cargadas correctamente",
                            Toast.LENGTH_SHORT).show();

                    // Seleccionar automáticamente la primera tabla si hay datos
                    if (tableCount > 0) {
                        spinnerTables.setSelection(1);
                    }
                } else {
                    String message = jsonResponse.getString("message");
                    Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    Log.e("LoadTables", "Error del servidor: " + message);

                    // En caso de error, cargar datos de prueba
                    loadTestData();
                }
            } catch (JSONException e) {
                Toast.makeText(SettingsActivity.this, "Error procesando la respuesta", Toast.LENGTH_LONG).show();
                Log.e("LoadTables", "JSON Error: " + e.getMessage());

                // En caso de error JSON, cargar datos de prueba
                loadTestData();
            }
        }
    }

    private void loadTestData() {
        // Cargar datos de prueba en caso de error
        if (tablesList.size() > 1) {
            tablesList.subList(1, tablesList.size()).clear();
        }

        String[] testTables = {"Users", "Products", "Orders", "Categories", "Reviews", "Order Items", "Payments"};
        for (String table : testTables) {
            tablesList.add(table);
        }

        spinnerAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Modo demo: Datos de prueba cargados", Toast.LENGTH_SHORT).show();
    }

    private String formatTableName(String tableName) {
        // Convertir snake_case a Title Case
        String[] words = tableName.split("_");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1)).append(" ");
            }
        }
        return formatted.toString().trim();
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean modoOscuro = prefs.getBoolean("modoOscuro", false);
        boolean notificaciones = prefs.getBoolean("notificaciones", true);

        switchModoOscuro.setChecked(modoOscuro);
        switchNotificaciones.setChecked(notificaciones);
    }

    private void guardarConfiguracion() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("modoOscuro", switchModoOscuro.isChecked());
        editor.putBoolean("notificaciones", switchNotificaciones.isChecked());

        editor.apply();
    }

    private void cerrarSesion() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void volverAlHome() {
        Intent intent = new Intent(SettingsActivity.this, Home_activity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        volverAlHome();
    }
}