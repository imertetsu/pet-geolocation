class Pet {
  final int id;
  final String name;
  final String species;
  final String breed;
  final DateTime birthDate;
  final String? userId;
  bool hasDevice;

  Pet({
    required this.id,
    required this.name,
    required this.species,
    required this.breed,
    required this.birthDate,
    required this.userId,
    this.hasDevice = false,
  });

  factory Pet.fromJson(Map<String, dynamic> json) {
    return Pet(
      id: json['id'],
      name: json['name'],
      species: json['species'],
      breed: json['breed'],
      birthDate: DateTime.parse(json['birthDate']),
      userId: json['userId'],
    );
  }
}
