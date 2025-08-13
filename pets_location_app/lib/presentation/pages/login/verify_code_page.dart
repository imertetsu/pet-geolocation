import 'package:flutter/material.dart';
import '../../../data/datasources/auth_remote_datasource.dart';
import '../../../data/models/register_request.dart';

class VerifyCodePage extends StatefulWidget {
  final String name;
  final String email;
  final String password;
  final List<String> roles;

  const VerifyCodePage({
    super.key,
    required this.name,
    required this.email,
    required this.password,
    required this.roles,
  });

  @override
  State<VerifyCodePage> createState() => _VerifyCodePageState();
}

class _VerifyCodePageState extends State<VerifyCodePage> {
  final _formKey = GlobalKey<FormState>();
  final _auth = AuthRemoteDataSource();
  final _codeController = TextEditingController();

  bool _loading = false;
  String? _error;

  void _verifyAndRegister() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _loading = true;
      _error = null;
    });

    final code = _codeController.text;

    try {
      final request = RegisterRequest(
        name: widget.name,
        email: widget.email,
        password: widget.password,
        roles: widget.roles,
        code: code,
      );

      await _auth.verifyAndRegister(request);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Usuario registrado con éxito')),
        );
        Navigator.popUntil(context, (route) => route.isFirst); // Volver al login o inicio
      }
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Verificar Código')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              if (_error != null)
                Text(_error!, style: const TextStyle(color: Colors.red)),
              TextFormField(
                controller: _codeController,
                decoration: const InputDecoration(labelText: 'Código de verificación'),
                validator: (value) =>
                    value == null || value.length != 5 ? 'Código inválido' : null,
              ),
              const SizedBox(height: 20),
              _loading
                  ? const CircularProgressIndicator()
                  : ElevatedButton(
                      onPressed: _verifyAndRegister,
                      child: const Text('Verificar y Registrar'),
                    ),
            ],
          ),
        ),
      ),
    );
  }
}
