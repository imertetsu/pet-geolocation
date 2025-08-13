import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:pets_location_app/core/network/api_client.dart';
import 'package:pets_location_app/presentation/pages/home_page.dart';
import '../models/register_request.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../models/login_request.dart';
import '../models/login_response.dart';
import 'package:google_sign_in/google_sign_in.dart';
import '../datasources/user_remote_datasource.dart';

class AuthRemoteDataSource {
  final Dio _dio = ApiClient.dio;
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  Future<void> requestVerificationCode(String email) async {
    try {
      final response = await _dio.post(
        '/users/register/request-code',
        data: {'email': email},
      );
      if (response.statusCode != 200) {
        throw Exception('Error al solicitar código de verificación');
      }
    } on DioException catch (e) {
      throw Exception(e.response?.data['message'] ?? 'Error desconocido');
    }
  }

  Future<void> verifyAndRegister(RegisterRequest request) async {
    try {
      final response = await _dio.post(
        '/users/register/verify',
        data: request.toJson(),
      );
      if (response.statusCode != 201) {
        throw Exception('Error en la verificación o registro');
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
        await _storage.write(key: 'userId', value: loginData.userId);
        
        // AHORA hacemos un GET para obtener el nombre del usuario
        final userDataSource = UserRemoteDataSource(_dio);
        final user = await userDataSource.getUserById(loginData.userId);

        // Guardamos el nombre recuperado
        await _storage.write(key: 'userName', value: user.name);
        return LoginResponse(
          role: loginData.role,
          userId: loginData.userId,
          email: loginData.email,
          token: loginData.token,
          userName: user.name,
        );
      } else {
        throw Exception('Credenciales incorrectas');
      }
    } on DioException catch (e) {
      throw Exception(e.response?.data['message'] ?? 'Error de autenticación');
    }
  }
  Future<void> handleGoogleLogin(BuildContext context) async {
    try {
      final googleSignIn = GoogleSignIn(scopes: ['email', 'profile']);
      final googleUser = await googleSignIn.signIn();
      if (googleUser == null) return;

      await _storage.write(key: 'userName', value: googleUser.displayName);
      final googleAuth = await googleUser.authentication;

      final credential = GoogleAuthProvider.credential(
        accessToken: googleAuth.accessToken,
        idToken: googleAuth.idToken,
      );
      final userCredential =
          await FirebaseAuth.instance.signInWithCredential(credential);

      String? firebaseToken = await userCredential.user?.getIdToken(true);

      final response = await ApiClient.dio.post('/auth/firebase', data: {
        'idToken': firebaseToken,
      });

      if (response.statusCode == 200) {
        final data = response.data;

        // Guardamos los datos en Secure Storage
        await _storage.write(key: 'token', value: data['token']);
        await _storage.write(key: 'email', value: data['email']);
        await _storage.write(key: 'role', value: data['role']);
        await _storage.write(key: 'userId', value: data['userId']);
        await _storage.write(key: 'photoUrl', value: data['photoUrl']);

        print('Inicio de sesión con Google exitoso');

        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (_) => HomePage(userId: data['userId']),
          ),
        );
      } else {
        throw Exception('Error al iniciar sesión con Firebase');
      }

    } on DioException catch (dioError) {
      String errorMsg = 'Error de red al conectar con el servidor';

      if (dioError.response != null) {
        print('Dio error - status: ${dioError.response?.statusCode}');
        print('Body: ${dioError.response?.data}');
        errorMsg = dioError.response?.data['message'] ?? 'Error del servidor';
      } else {
        print('Dio error: ${dioError.message}');
      }

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(errorMsg)),
      );
    } catch (e) {
      print('Error en login con Google: $e');
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Error de autenticación con Google')),
      );
    }
  }
  Future<void> logoutGoogle() async {
    try {
      final googleSignIn = GoogleSignIn();
      await googleSignIn.signOut();
      // Si quieres forzar que olvide la cuenta por completo:
      // await googleSignIn.disconnect();
      print("Sesión de Google cerrada correctamente");
    } catch (e) {
      print("Error al cerrar sesión de Google: $e");
    }
  }
}
