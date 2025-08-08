class LoginResponse {
  final String role;
  final String userId;
  final String email;
  final String token;
  final String? userName;

  LoginResponse({
    required this.role,
    required this.userId,
    required this.email,
    required this.token,
    this.userName
  });

  factory LoginResponse.fromJson(Map<String, dynamic> json) => LoginResponse(
        role: json['role'],
        userId: json['userId'],
        email: json['email'],
        token: json['token'],
      );
}
