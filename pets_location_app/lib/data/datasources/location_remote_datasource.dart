import 'package:dio/dio.dart';
import '../models/location.dart';

class LocationRemoteDataSource {
  final Dio _dio;

  LocationRemoteDataSource(this._dio);

  Future<Location?> getLatestLocation(int petId) async {
    try {
      final response = await _dio.get('/locations/pet/$petId/latest');
      return Location.fromJson(response.data);
    } catch (e) {
      return null;
    }
  }
}
