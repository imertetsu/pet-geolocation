class LoginResponse {
  final String role;
  final String userId;
  final String email;
  final String token;

  LoginResponse({
    required this.role,
    required this.userId,
    required this.email,
    required this.token,
  });

  factory LoginResponse.fromJson(Map<String, dynamic> json) => LoginResponse(
        role: json['role'],
        userId: json['userId'],
        email: json['email'],
        token: json['token'],
      );
}
