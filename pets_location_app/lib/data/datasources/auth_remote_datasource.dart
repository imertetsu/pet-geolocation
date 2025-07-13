import 'package:dio/dio.dart';
import 'package:pets_location_app/core/network/api_client.dart';
import '../models/register_request.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../models/login_request.dart';
import '../models/login_response.dart';

class AuthRemoteDataSource {
  final Dio _dio = ApiClient.dio;
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  Future<void> register(RegisterRequest request) async {
    try {
      final response = await _dio.post('/users', data: request.toJson());

      if (response.statusCode == 201) {
        // Registrado correctamente
        return;
      } else {
        throw Exception('Error en el registro');
      }
    } on DioException catch (e) {
      throw Exception(e.response?.data['message'] ?? 'Error desconocido');
    }
  }

  Future<LoginResponse> login(LoginRequest request) async {
    try {
      final response = await _dio.post('/auth/login', data: request.toJson());

      if (response.statusCode == 200) {
        final loginData = LoginResponse.fromJson(response.data);
        await _storage.write(key: 'token', value: loginData.token);
        await _storage.write(key: 'email', value: loginData.email);
        await _storage.write(key: 'role', value: loginData.role);
        return loginData;
      } else {
        throw Exception('Credenciales incorrectas');
      }
    } on DioException catch (e) {
      throw Exception(e.response?.data['message'] ?? 'Error de autenticación');
    }
  }
}
