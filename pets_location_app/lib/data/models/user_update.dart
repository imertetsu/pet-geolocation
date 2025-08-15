class UserUpdate {
  final String id;
  final String? name;
  final String email;
  final String? password;
  final bool? isVerified;
  final String? photoUrl;
  final String? provider;

  UserUpdate({
    required this.id,
    required this.name,
    required this.email,
    this.password,
    required this.isVerified,
    this.photoUrl,
    required this.provider,
  });

  factory UserUpdate.fromJson(Map<String, dynamic> json) {
    return UserUpdate(
      id: json['id'],
      name: json['name'],
      email: json['email'],
      password: json['password'],
      isVerified: json['isVerified'],
      photoUrl: json['photoUrl'],
      provider: json['provider'],
    );
  }
}

