import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SessionManager {
  final _storage = const FlutterSecureStorage();

  Future<String?> getUserId() async => await _storage.read(key: 'userId');
  Future<String?> getUserName() async => await _storage.read(key: 'userName');
  Future<String?> getUserRole() async => await _storage.read(key: 'role');
  Future<String?> getToken() async => await _storage.read(key: 'token');

  Future<void> clear() async => await _storage.deleteAll();
}
