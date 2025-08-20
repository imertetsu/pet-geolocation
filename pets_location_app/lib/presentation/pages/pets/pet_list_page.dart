import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:pets_location_app/core/network/api_client.dart';
import '../../../data/models/pet.dart';
import '../../../data/datasources/pet_remote_datasource.dart';
import '../../../data/datasources/device_remote_datasource.dart';
import '../../widgets/pet/pet_card.dart';
import 'register_pet_page.dart';
import '../devices/associate_device_page.dart';

class PetListPage  extends StatefulWidget {
  final String? userId;

  const PetListPage ({super.key, required this.userId});

  @override
  State<PetListPage > createState() => _PetListPageState();
}

class _PetListPageState extends State<PetListPage> {
  final Dio _dio = ApiClient.dio;
  late final PetRemoteDataSource _petRemote;
  late final DeviceRemoteDataSource _deviceRemote;

  List<Pet> pets = [];
  bool loading = true;

  @override
  void initState() {
    super.initState();
    _petRemote = PetRemoteDataSource(_dio);
    _deviceRemote = DeviceRemoteDataSource(_dio);
    _loadPets();
  }

  Future<void> _loadPets() async {
    final loadedPets = await _petRemote.getPetsByUserId(widget.userId);

    for (var pet in loadedPets) {
      pet.hasDevice = await _deviceRemote.hasDevice(pet.id);
    }

    setState(() {
      pets = loadedPets;
      loading = false;
    });
  }
  Future<void> _confirmDeletePet(Pet pet) async {
    if (pet.hasDevice) {
      showDialog(
        context: context,
        builder: (_) => AlertDialog(
          title: const Text('No se puede eliminar'),
          content: const Text(
              'Esta mascota tiene un dispositivo asociado. Debes eliminar el dispositivo antes.'),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Entendido'),
            ),
          ],
        ),
      );
      return;
    }

    final confirm = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Eliminar Mascota'),
        content: Text('¿Estás seguro que quieres eliminar a "${pet.name}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancelar'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Eliminar', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );

    if (confirm == true) {
      await _deletePet(pet.id);
    }
  }
  Future<void> _deletePet(int petId) async {
    try {
      await _petRemote.deletePet(petId);
      await _loadPets(); // Recargar la lista
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error al eliminar: $e')),
      );
    }
  }
  Future<void> _unlinkDevice(Pet pet) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Desvincular dispositivo'),
        content: Text('¿Estás seguro de desvincular el dispositivo de "${pet.name}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancelar'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Desvincular', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );

    if (confirm == true) {
      try {
        final deviceId = await _deviceRemote.getDeviceIdByPetId(pet.id);
        if (deviceId != null) {
          final success = await _deviceRemote.unlinkDevice(deviceId);
          if (success) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Dispositivo desvinculado correctamente')),
            );
            await _loadPets(); // Recargar lista
          }
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('No se encontró ningún dispositivo')),
          );
        }
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: ${e.toString()}')),
        );
      }
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mis Mascotas'),
        automaticallyImplyLeading: false,),
      body: loading
          ? const Center(child: CircularProgressIndicator())
          : pets.isEmpty
              ? const Center(child: Text('No tienes mascotas registradas.'))
              : ListView.builder(
                  itemCount: pets.length,
                  itemBuilder: (context, index) {
                    final pet = pets[index];
                    return PetCard(
                      pet: pet,
                      onDelete: () => _confirmDeletePet(pet),
                      onAssociateDevice: () async {
                        if (!pet.hasDevice) {
                          final result = await Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (_) => AssociateDevicePage(petId: pet.id),
                            ),
                          );
                          if (result == true) _loadPets();
                        } else {
                          _unlinkDevice(pet); // Nueva función
                        }
                      },
                    );
                  }
                ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          final result = await Navigator.push(
            context,
            MaterialPageRoute(
              builder: (_) => RegisterPetPage(userId: widget.userId),
            ),
          );
          if (result == true) {
            _loadPets(); 
          }
        },
        child: const Icon(Icons.add),
        tooltip: 'Registrar Mascota',
      ),
    );
  }
}
