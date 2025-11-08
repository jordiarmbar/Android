<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit();
}

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "breadapp_db";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Conexión fallida: " . $conn->connect_error]);
    exit();
}

if ($_SERVER['CONTENT_TYPE'] == 'application/json') {
    $input = json_decode(file_get_contents('php://input'), true);
    $name = $input['name'] ?? '';
    $surname = $input['surname'] ?? '';
    $username = $input['username'] ?? '';
    $email = $input['email'] ?? '';
    $phone = $input['phone'] ?? '';
    $password = $input['password'] ?? '';
} else {
    $name = $_POST['name'] ?? '';
    $surname = $_POST['surname'] ?? '';
    $username = $_POST['username'] ?? '';
    $email = $_POST['email'] ?? '';
    $phone = $_POST['phone'] ?? '';
    $password = $_POST['password'] ?? '';
}

if (empty($name) || empty($surname) || empty($username) || empty($email) || empty($phone) || empty($password)) {
    echo json_encode(["success" => false, "message" => "Todos los campos son requeridos"]);
    exit;
}

$check_sql = "SELECT id FROM users WHERE username = ? OR email = ?";
$check_stmt = $conn->prepare($check_sql);
$check_stmt->bind_param("ss", $username, $email);
$check_stmt->execute();
$check_result = $check_stmt->get_result();

if ($check_result->num_rows > 0) {
    echo json_encode(["success" => false, "message" => "El usuario o email ya existen"]);
    exit;
}

$hashed_password = password_hash($password, PASSWORD_DEFAULT);

$sql = "INSERT INTO users (name, surname, username, email, phone, password, created_at) 
        VALUES (?, ?, ?, ?, ?, ?, NOW())";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssssss", $name, $surname, $username, $email, $phone, $hashed_password);

if ($stmt->execute()) {
    $user_id = $stmt->insert_id;
    
    $user_data = [
        "id" => $user_id,
        "name" => $name,
        "surname" => $surname,
        "username" => $username,
        "email" => $email,
        "phone" => $phone
    ];
    
    echo json_encode([
        "success" => true, 
        "message" => "Usuario registrado exitosamente",
        "user" => $user_data
    ]);
} else {
    echo json_encode(["success" => false, "message" => "Error al registrar usuario: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>