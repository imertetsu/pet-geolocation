import 'package:dio/dio.dart';
import '../models/user.dart';
import '../models/user_update.dart';

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

  Future<UserUpdate> getUserCompleteById(String userId) async {
    try {
      final response = await _dio.get('/users/$userId');
      return UserUpdate.fromJson(response.data);
    } catch (e) {
      throw Exception('Error fetching user by id: $e');
    }
  }

  Future<void> updateUser(
    String userId, {
    required String name,
    String? password,
    String? photoUrl,
  }) async {
    final data = {
      'name': name,
      if (password != null) 'password': password,
      if (photoUrl != null) 'photoUrl': photoUrl,
    };

    await _dio.patch('/users/$userId', data: data);
  }
}
