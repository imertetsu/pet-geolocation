import 'package:dio/dio.dart';

class DeviceRemoteDataSource {
  final Dio _dio;

  DeviceRemoteDataSource(this._dio);

  Future<bool> hasDevice(int petId) async {
    final response = await _dio.get('/devices/exists/pet/$petId');
    return response.data == true;
  }
  // Desvincula el dispositivo por su ID
  Future<bool> unlinkDevice(String deviceId) async {
    try {
      final response = await _dio.delete('/devices/$deviceId');
      return response.statusCode == 200 || response.statusCode == 204;
    } catch (e) {
      throw Exception('Error al desvincular dispositivo: $e');
    }
  }
  // Obtiene el deviceId a partir del petId
  Future<String?> getDeviceIdByPetId(int petId) async {
    try {
      final response = await _dio.get('/devices/pet/$petId');
      return response.data['deviceId'];
    } catch (e) {
      throw Exception('Error al obtener deviceId: $e');
    }
  }
}
