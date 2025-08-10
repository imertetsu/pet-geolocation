import 'dart:io';
import 'package:dio/dio.dart';
import '../../core/network/api_files.dart';
import 'package:mime/mime.dart';
import 'package:http_parser/http_parser.dart';

class FileRemoteDataSource {
  final Dio _dio = ApiFiles.dio;

  /// Sube una imagen o video al servidor y retorna la URL devuelta por el backend
  Future<String> uploadFile({
    required File file,
    required String userId,
  }) async {
    final fileName = file.path.split('/').last;

    // Detectar tipo MIME 
    //Este es un flujo de bytes genérico, no sé exactamente qué tipo de archivo es.
    final mimeType = lookupMimeType(file.path) ?? 'application/octet-stream';
    final mediaType = MediaType.parse(mimeType);

    final formData = FormData.fromMap({
      'file': await MultipartFile.fromFile(
        file.path,
        filename: fileName,
        contentType: mediaType, // << ahora forzamos el tipo MIME
      ),
      'userId': userId,
    });

    final response = await _dio.post(
      '/images/upload',
      data: formData,
    );

    return response.data as String; // URL devuelta por el backend
  }
}
