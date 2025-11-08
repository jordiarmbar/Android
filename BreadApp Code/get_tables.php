<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Configuración de la base de datos
$servername = "localhost";
$username = "root";  // Cambia si usas otro usuario
$password = "";      // Cambia por tu contraseña de MySQL
$dbname = "breadapp_db";

// Manejar preflight request para CORS
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

try {
    // Crear conexión
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Consulta para obtener todas las tablas de la base de datos
    $stmt = $conn->query("SHOW TABLES");
    $tables = $stmt->fetchAll(PDO::FETCH_NUM);
    
    $response = array();
    $tableList = array();
    
    foreach ($tables as $table) {
        $tableList[] = array("table_name" => $table[0]);
    }
    
    $response["success"] = true;
    $response["tables"] = $tableList;
    $response["count"] = count($tableList);
    
    echo json_encode($response);
    
} catch(PDOException $e) {
    $response = array(
        "success" => false,
        "message" => "Error de conexión: " . $e->getMessage()
    );
    echo json_encode($response);
}

$conn = null;
?>