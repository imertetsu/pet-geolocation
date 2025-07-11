import 'package:dio/dio.dart';
import '../models/pet.dart';

class PetRemoteDataSource {
  final Dio _dio;

  PetRemoteDataSource(this._dio);

  Future<List<Pet>> getPetsByUserId(String userId) async {
    final response = await _dio.get('/pets/user/$userId');
    return (response.data as List)
        .map((json) => Pet.fromJson(json))
        .toList();
  }
  Future<void> registerPet(Pet pet) async {
    final payload = {
      "name": pet.name,
      "species": pet.species,
      "breed": pet.breed,
      "birthDate": pet.birthDate.toIso8601String().split('T').first,
      "userId": pet.userId,
    };

    await _dio.post('/pets', data: payload);
  }
  Future<void> deletePet(int id) async {
    await _dio.delete('/pets/$id');
  }
}
