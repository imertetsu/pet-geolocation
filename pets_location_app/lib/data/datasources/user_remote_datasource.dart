import 'package:dio/dio.dart';
import '../models/user.dart';

class UserRemoteDataSource {
  final Dio _dio;

  UserRemoteDataSource(this._dio);

  Future<User> getUserById(String userId) async {
    try {
      final response = await _dio.get('/users/$userId');
      return User.fromJson(response.data);
    } catch (e) {
      throw Exception('Error fetching user by id: $e');
    }
  }
}
