import 'package:pets_location_app/data/datasources/user_remote_datasource.dart';

class UserHelper {
  final UserRemoteDataSource userDataSource;

  UserHelper(this.userDataSource);

  Future<String> getUserNameById(String userId) async {
    try {
      final user = await userDataSource.getUserById(userId);
      return user.name;
    } catch (e) {
      print('Error al obtener el nombre del usuario: $e');
      return 'Desconocido';
    }
  }
}
