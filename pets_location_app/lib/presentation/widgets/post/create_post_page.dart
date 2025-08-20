import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import '../../../data/datasources/session/session_manager.dart';
import '../../../data/models/news_category.dart';
import '../../../core/network/api_client.dart';
import '../../../data/datasources/file_remote_datasource.dart';
import '../../../data/models/news_category_labels.dart';

class CreatePostPage extends StatefulWidget {
  const CreatePostPage({super.key});

  @override
  State<CreatePostPage> createState() => _CreatePostPageState();
}

class _CreatePostPageState extends State<CreatePostPage> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _contentController = TextEditingController();
  final _countryController = TextEditingController(text: 'Bolivia');

  final SessionManager _sessionManager = SessionManager();
  late final NewsRemoteDataSource _newsRemoteDataSource;

  NewsCategory? _selectedCategory;
  String? _selectedCity;
  List<XFile> _selectedImages = [];

  bool _isLoading = false;

  final List<String> _boliviaCities = [
    "La Paz",
    "Cochabamba",
    "Santa Cruz",
    "Sucre",
    "Oruro",
    "Potosí",
    "Tarija",
    "Beni",
    "Pando"
  ];

  @override
  void initState() {
    super.initState();
    _newsRemoteDataSource = NewsRemoteDataSource(ApiClient.dio);
  }

  Future<void> _pickImages() async {
    final picker = ImagePicker();
    final images = await picker.pickMultiImage();

    if (images.isNotEmpty) {
      // Limitar a máximo 3 imágenes
      if ((_selectedImages.length + images.length) > 3) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Solo puedes subir máximo 3 imágenes'),
          ),
        );
        return;
      }

      setState(() {
        _selectedImages.addAll(images);
      });
    }
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    try {
      final userId = await _sessionManager.getUserId();
      final userName = await _sessionManager.getUserName();

      if (userId == null || userName == null) {
        throw Exception("No hay sesión activa");
      }

      // 1. Subir imágenes y obtener URLs
      final fileDataSource = FileRemoteDataSource();
      List<String> uploadedUrls = [];

      for (final img in _selectedImages) {
        final url = await fileDataSource.uploadFile(
          file: File(img.path),
          userId: userId,
        );
        uploadedUrls.add(url);
      }

      // 2. Crear el post con las URLs de imágenes
      await _newsRemoteDataSource.createPost(
        title: _titleController.text.trim(),
        content: _contentController.text.trim(),
        category: _selectedCategory!.name,
        authorId: userId,
        authorName: userName,
        country: _countryController.text.trim(),
        city: _selectedCity!,
        images: uploadedUrls, // Ahora enviamos URLs, no paths
      );

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Publicación creada con éxito')),
      );

      Navigator.pop(context);
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error: $e')),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Crear Publicación')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: SingleChildScrollView(
            child: Column(
              children: [
                // Título
                TextFormField(
                  controller: _titleController,
                  decoration: const InputDecoration(labelText: 'Título'),
                  validator: (value) =>
                      value!.isEmpty ? 'El título es obligatorio' : null,
                ),
                const SizedBox(height: 16),

                // Contenido
                TextFormField(
                  controller: _contentController,
                  decoration: const InputDecoration(labelText: 'Contenido'),
                  maxLines: 4,
                  validator: (value) =>
                      value!.isEmpty ? 'El contenido es obligatorio' : null,
                ),
                const SizedBox(height: 16),

                // Categoría
                DropdownButtonFormField<NewsCategory>(
                  decoration: const InputDecoration(labelText: 'Categoría'),
                  items: NewsCategory.values.map((category) {
                    return DropdownMenuItem(
                      value: category,
                      child: Text(NewsCategoryLabels.getEsLabel(category)),
                    );
                  }).toList(),
                  value: _selectedCategory,
                  onChanged: (value) {
                    setState(() {
                      _selectedCategory = value;
                    });
                  },
                  validator: (value) =>
                      value == null ? 'Selecciona una categoría' : null,
                ),
                const SizedBox(height: 16),

                // País (fijo)
                TextFormField(
                  controller: _countryController,
                  readOnly: true,
                  decoration: const InputDecoration(labelText: 'País'),
                ),
                const SizedBox(height: 16),

                // Ciudad
                DropdownButtonFormField<String>(
                  decoration: const InputDecoration(labelText: 'Ciudad'),
                  items: _boliviaCities
                      .map((city) => DropdownMenuItem(
                            value: city,
                            child: Text(city),
                          ))
                      .toList(),
                  value: _selectedCity,
                  onChanged: (value) {
                    setState(() {
                      _selectedCity = value;
                    });
                  },
                  validator: (value) =>
                      value == null ? 'Selecciona una ciudad' : null,
                ),
                const SizedBox(height: 16),

                // Adjuntar imágenes
                Align(
                  alignment: Alignment.centerLeft,
                  child: ElevatedButton.icon(
                    onPressed: _selectedImages.length >= 3 ? null : _pickImages,
                    icon: const Icon(Icons.image),
                    label: const Text('Adjuntar imágenes'),
                  ),
                ),
                const SizedBox(height: 8),

                // Previsualización
                if (_selectedImages.isNotEmpty)
                  SizedBox(
                    height: 100,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      itemCount: _selectedImages.length,
                      itemBuilder: (context, index) {
                        return Stack(
                          children: [
                            Image.file(
                              File(_selectedImages[index].path),
                              width: 100,
                              height: 100,
                              fit: BoxFit.cover,
                            ),
                            Positioned(
                              right: 0,
                              top: 0,
                              child: IconButton(
                                icon: const Icon(Icons.close, color: Colors.red),
                                onPressed: () {
                                  setState(() {
                                    _selectedImages.removeAt(index); // quitar de la lista
                                  });
                                },
                              ),
                            ),
                          ],
                        );
                      },
                    ),
                  ),

                const SizedBox(height: 24),
                _isLoading
                    ? const CircularProgressIndicator()
                    : ElevatedButton.icon(
                        onPressed: _submit,
                        icon: const Icon(Icons.send),
                        label: const Text('Publicar'),
                      ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
