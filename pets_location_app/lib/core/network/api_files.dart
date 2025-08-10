import 'package:dio/dio.dart';

class ApiFiles {
  static const String baseUrl = 'http://10.0.2.2:9090/api';

  static final Dio dio = Dio(
    BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 30), // tiempo para subir videos
      receiveTimeout: const Duration(seconds: 30),
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    ),
  )
    ..interceptors.add(LogInterceptor(
      requestBody: true,
      responseBody: true,
    ))
    ..interceptors.add(InterceptorsWrapper(
      onError: (DioException e, handler) {
        print("Error en petición: ${e.message}");
        handler.next(e);
      },
    ));

    static final Dio dioToGetDelete = Dio(
    BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
    ),
  );
}
