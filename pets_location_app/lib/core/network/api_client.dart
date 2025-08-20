import 'package:dio/dio.dart';

class ApiClient {
  static final Dio dio = Dio(
    BaseOptions(
      baseUrl: 'http://181.114.109.198:9090/api',
      connectTimeout: const Duration(seconds: 10), 
      receiveTimeout: const Duration(seconds: 10),
      headers: {'Content-Type': 'application/json'},
    ),
  )..interceptors.add(LogInterceptor(
      requestBody: true,
      responseBody: true,
    ));
}