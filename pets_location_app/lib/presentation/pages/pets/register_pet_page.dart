import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:pets_location_app/core/network/api_client.dart';
import '../../../../data/models/pet.dart';
import '../../../../data/datasources/pet_remote_datasource.dart';

class RegisterPetPage extends StatefulWidget {
  final String userId;

  const RegisterPetPage({super.key, required this.userId});

  @override
  State<RegisterPetPage> createState() => _RegisterPetPageState();
}

class _RegisterPetPageState extends State<RegisterPetPage> {
  final _formKey = GlobalKey<FormState>();

  final _nameController = TextEditingController();
  final _speciesController = TextEditingController();
  final _breedController = TextEditingController();

  DateTime? _selectedDate;
  bool _loading = false;

  final Dio _dio = ApiClient.dio;
  late final PetRemoteDataSource _petRemote;

  @override
  void initState() {
    super.initState();
    _petRemote = PetRemoteDataSource(_dio);
  }

  Future<void> _pickDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: now,
      firstDate: DateTime(2000),
      lastDate: now,
    );

    if (picked != null) {
      setState(() => _selectedDate = picked);
    }
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate() || _selectedDate == null) return;

    setState(() => _loading = true);

    final pet = Pet(
      id: 0, // Ignorado por el backend
      name: _nameController.text,
      species: _speciesController.text,
      breed: _breedController.text,
      birthDate: _selectedDate!,
      userId: widget.userId,
    );

    try {
      await _petRemote.registerPet(pet);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Mascota registrada con éxito')),
        );
        Navigator.pop(context, true); // Volver a la lista
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error: $e')),
      );
    } finally {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Registrar Mascota')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: _nameController,
                decoration: const InputDecoration(labelText: 'Nombre'),
                validator: (value) =>
                    value == null || value.isEmpty ? 'Campo requerido' : null,
              ),
              const SizedBox(height: 10),
              TextFormField(
                controller: _speciesController,
                decoration: const InputDecoration(labelText: 'Especie'),
                validator: (value) =>
                    value == null || value.isEmpty ? 'Campo requerido' : null,
              ),
              const SizedBox(height: 10),
              TextFormField(
                controller: _breedController,
                decoration: const InputDecoration(labelText: 'Raza'),
              ),
              const SizedBox(height: 10),
              ListTile(
                title: Text(_selectedDate == null
                    ? 'Fecha de nacimiento no seleccionada'
                    : 'Nacimiento: ${_selectedDate!.toLocal()}'.split(' ')[0]),
                trailing: const Icon(Icons.calendar_today),
                onTap: _pickDate,
              ),
              const SizedBox(height: 20),
              _loading
                  ? const Center(child: CircularProgressIndicator())
                  : ElevatedButton(
                      onPressed: _submit,
                      child: const Text('Registrar Mascota'),
                    ),
            ],
          ),
        ),
      ),
    );
  }
}
