class RegisterRequest {
  final String name;
  final String email;
  final String password;
  final List<String> roles;
  final String code;

  RegisterRequest({
    required this.name,
    required this.email,
    required this.password,
    this.roles = const ['CUSTOMER'],
    required this.code
  });

  Map<String, dynamic> toJson() => {
        'name': name,
        'email': email,
        'password': password,
        'roles': roles,
        'code': code
      };
}
