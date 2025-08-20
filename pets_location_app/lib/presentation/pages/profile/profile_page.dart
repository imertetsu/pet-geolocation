import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:image_picker/image_picker.dart';
import '../../../data/datasources/user_remote_datasource.dart';
import '../../../data/datasources/file_remote_datasource.dart';
import '../../../core/network/api_client.dart';

class ProfilePage extends StatefulWidget {
  final String? userId;
  const ProfilePage({super.key, required this.userId});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _passwordController = TextEditingController();
  String? _photoUrl;
  File? _pickedImage;
  String? _provider;

  final _fileRemoteDataSource = FileRemoteDataSource();
  final _userRemoteDataSource = UserRemoteDataSource(ApiClient.dio);

  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadUser();
  }

  Future<void> _loadUser() async {
    try {
      final user = await _userRemoteDataSource.getUserCompleteById(widget.userId);
      print(user);
      setState(() {
        _nameController.text = user.name ?? '';
        _photoUrl = user.photoUrl;
        _provider = user.provider ?? '';
        _isLoading = false;
      });
    } catch (e) {
      print('Error loading user: $e');
      setState(() => _isLoading = false);
    }
  }

  Future<void> _pickImage() async {
    final picked = await ImagePicker().pickImage(source: ImageSource.gallery);
    if (picked != null) {
      setState(() => _pickedImage = File(picked.path));
    }
  }

  Future<void> _saveProfile() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    try {
      String? uploadedUrl = _photoUrl;
      if (_pickedImage != null) {
        uploadedUrl = await _fileRemoteDataSource.uploadFile(
          file: _pickedImage!,
          userId: widget.userId,
        );
      }

      await _userRemoteDataSource.updateUser(
        widget.userId,
        name: _nameController.text,
        password: (_provider == 'LOCAL' && _passwordController.text.isNotEmpty)
            ? _passwordController.text
            : null,
        photoUrl: uploadedUrl,
      );
      // Guarda la nueva URL en FlutterSecureStorage
      final storage = const FlutterSecureStorage();
      if (uploadedUrl != null && uploadedUrl.isNotEmpty) {
        await storage.write(key: 'photoUrl', value: uploadedUrl);
      }
      if(_nameController.text != null && _nameController.text.isNotEmpty){
        await storage.write(key: 'userName', value: _nameController.text);
      }

      Navigator.pop(context, true);
    } catch (e) {
      print('Error saving profile: $e');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Mi Perfil')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              Center(
                child: Stack(
                  children: [
                    CircleAvatar(
                      radius: 50,
                      backgroundImage: _pickedImage != null
                          ? FileImage(_pickedImage!)
                          : (_photoUrl != null ? NetworkImage(_photoUrl!) : null) as ImageProvider?,
                      child: _photoUrl == null && _pickedImage == null
                          ? const Icon(Icons.person, size: 50)
                          : null,
                    ),
                    Positioned(
                      bottom: 0,
                      right: 0,
                      child: IconButton(
                        icon: const Icon(Icons.camera_alt),
                        onPressed: _pickImage,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 20),
              TextFormField(
                controller: _nameController,
                decoration: const InputDecoration(labelText: 'Nombre'),
                validator: (value) => value!.isEmpty ? 'Ingrese un nombre' : null,
              ),
              if (_provider == 'LOCAL') ...[
                const SizedBox(height: 12),
                TextFormField(
                  controller: _passwordController,
                  decoration: const InputDecoration(labelText: 'Contraseña (opcional)'),
                  obscureText: true,
                ),
              ],
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: _saveProfile,
                child: const Text('Guardar cambios'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
