import 'dart:developer';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:pets_location_app/data/models/post.dart';
import 'package:pets_location_app/data/datasources/news_remote_datasource.dart';
import 'package:pets_location_app/data/datasources/file_remote_datasource.dart';
import '../../../core/network/api_client.dart';
import '../../../data/models/news_category.dart';
import '../../../data/models/news_category_labels.dart';

class EditPostPage extends StatefulWidget {
  final Post post;

  const EditPostPage({Key? key, required this.post}) : super(key: key);

  @override
  State<EditPostPage> createState() => _EditPostPageState();
}

class _EditPostPageState extends State<EditPostPage> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _titleController;
  late TextEditingController _contentController;
  late TextEditingController _countryController;
  late TextEditingController _cityController;

  late List<String> _images; // URLs originales
  List<String> _filesToDelete = [];
  List<File> _filesToUpload = [];
  late NewsCategory _selectedCategory;

  final _newsDataSource = NewsRemoteDataSource(ApiClient.dio);
  final _fileRemoteDataSource = FileRemoteDataSource();

  bool _isSaving = false;

  @override
  void initState() {
    super.initState();
    _titleController = TextEditingController(text: widget.post.title);
    _contentController = TextEditingController(text: widget.post.content);
    _countryController = TextEditingController(text: widget.post.country ?? "");
    _cityController = TextEditingController(text: widget.post.city ?? "");
    _images = List.from(widget.post.imageUrls);
    _selectedCategory = NewsCategory.values.firstWhere(
      (e) => e.name == widget.post.category,
      orElse: () => NewsCategory.LOST_PET, // Valor por defecto si no coincide
    );
  }

  Future<void> _pickAndUploadImage() async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);

    if (pickedFile != null) {
      setState(() {
        _filesToUpload.add(File(pickedFile.path));
      });
    }
  }

  void _removeImage(String url) {
    setState(() {
      _images.remove(url);
      _filesToDelete.add(url);
    });
  }

  void _removeLocalFile(File file) {
    setState(() {
      _filesToUpload.remove(file);
    });
  }

  Future<void> _saveChanges() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isSaving = true;
    });

    try {
      // 1️⃣ Subir imágenes nuevas
      for (final file in _filesToUpload) {
        final uploadedUrl = await _fileRemoteDataSource.uploadFile(
          file: file,
          userId: widget.post.authorId,
        );
        _images.add(uploadedUrl);
      }

      // 2️⃣ Eliminar imágenes viejas
      for (final url in _filesToDelete) {
        await _fileRemoteDataSource.deleteFile(url);
      }

      // 3️⃣ Actualizar en backend
      final updatedPost = await _newsDataSource.updatePost(
        widget.post.id,
        _titleController.text,
        _contentController.text,
        _selectedCategory.name,
        _countryController.text,
        _cityController.text,
        _images,
      );

      if (!mounted) return;
      Navigator.pop(context, updatedPost);
    } catch (e) {
      log("Error al actualizar post: $e");
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Error al actualizar el post")),
      );
    } finally {
      if (mounted) {
        setState(() {
          _isSaving = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Editar publicación")),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: _titleController,
                decoration: const InputDecoration(labelText: "Título"),
                validator: (value) =>
                    value == null || value.isEmpty ? "Ingrese un título" : null,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _contentController,
                decoration: const InputDecoration(labelText: "Contenido"),
                maxLines: 5,
                validator: (value) => value == null || value.isEmpty
                    ? "Ingrese el contenido"
                    : null,
              ),
              const SizedBox(height: 12),

              // Selector de categoría
              DropdownButtonFormField<NewsCategory>(
                value: _selectedCategory,
                decoration: const InputDecoration(labelText: "Categoría"),
                items: NewsCategory.values.map((cat) {
                  return DropdownMenuItem(
                    value: cat,
                    child: Text(NewsCategoryLabels.getEsLabel(cat)),
                  );
                }).toList(),
                onChanged: (value) {
                  setState(() {
                    _selectedCategory = value!;
                  });
                },
              ),
              const SizedBox(height: 12),

              TextFormField(
                controller: _countryController,
                decoration: const InputDecoration(labelText: "País"),
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _cityController,
                decoration: const InputDecoration(labelText: "Ciudad"),
              ),
              const SizedBox(height: 12),

              // Lista de imágenes actuales
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: [
                  for (final img in _images)
                    Stack(
                      children: [
                        Image.network(img, width: 100, height: 100, fit: BoxFit.cover),
                        Positioned(
                          right: 0,
                          top: 0,
                          child: IconButton(
                            icon: const Icon(Icons.close, color: Colors.red),
                            onPressed: () => _removeImage(img),
                          ),
                        ),
                      ],
                    ),

                  // Archivos nuevos que aún no se subieron
                  for (final file in _filesToUpload)
                    Stack(
                      children: [
                        Image.file(file, width: 100, height: 100, fit: BoxFit.cover),
                        Positioned(
                          right: 0,
                          top: 0,
                          child: IconButton(
                            icon: const Icon(Icons.close, color: Colors.red),
                            onPressed: () => _removeLocalFile(file),
                          ),
                        ),
                      ],
                    ),

                  GestureDetector(
                    onTap: _pickAndUploadImage,
                    child: Container(
                      width: 100,
                      height: 100,
                      color: Colors.grey[300],
                      child: const Icon(Icons.add),
                    ),
                  )
                ],
              ),

              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: _isSaving ? null : _saveChanges,
                child: _isSaving
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text("Guardar cambios"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
