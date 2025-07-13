import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:dio/dio.dart';
import 'package:pets_location_app/core/network/api_client.dart';
import '../../../data/models/pet.dart';
import '../../../data/datasources/pet_remote_datasource.dart';
import '../../../data/datasources/device_remote_datasource.dart';
import '../../../data/datasources/location_remote_datasource.dart';
import '../../../data/websocket/location_stomp_client.dart';

class PetMapPage extends StatefulWidget {
  final String userId;

  const PetMapPage({super.key, required this.userId});

  @override
  State<PetMapPage> createState() => _PetMapPageState();
}

class _PetMapPageState extends State<PetMapPage> {
  final Dio _dio = ApiClient.dio;
  late final PetRemoteDataSource _petRemote;
  late final DeviceRemoteDataSource _deviceRemote;
  late final LocationRemoteDataSource _locationRemote;
  Set<Marker> _markers = {};

  GoogleMapController? _mapController;
  List<Pet> petsWithDevice = [];
  Pet? selectedPet;

  // Cliente STOMP
  LocationStompClient? _stompClient;

  @override
  void initState() {
    super.initState();
    _petRemote = PetRemoteDataSource(_dio);
    _deviceRemote = DeviceRemoteDataSource(_dio);
    _locationRemote = LocationRemoteDataSource(_dio);
    _loadPetsWithDevice();
  }

  Future<void> _loadPetsWithDevice() async {
    final allPets = await _petRemote.getPetsByUserId(widget.userId);
    final filtered = <Pet>[];

    for (final pet in allPets) {
      final hasDevice = await _deviceRemote.hasDevice(pet.id);
      if (hasDevice) {
        pet.hasDevice = true;
        filtered.add(pet);
      }
    }

    setState(() {
      petsWithDevice = filtered;
    });
  }

  // Cargar ubicación + conectar STOMP
  Future<void> _loadAndShowLocation(Pet pet) async {
    final location = await _locationRemote.getLatestLocation(pet.id);
    if (location == null) return;

    final marker = Marker(
      markerId: MarkerId(pet.id.toString()),
      position: LatLng(location.latitude, location.longitude),
      infoWindow: InfoWindow(title: pet.name),
    );

    setState(() {
      selectedPet = pet;
      _markers = {marker};
    });

    _mapController?.animateCamera(
      CameraUpdate.newLatLng(
        LatLng(location.latitude, location.longitude),
      ),
    );

    // Desconectar STOMP anterior si había
    _stompClient?.disconnect();

    // Conectar STOMP para esta mascota
    _stompClient = LocationStompClient();
    _stompClient!.connect(
      petId: pet.id,
      onLocationReceived: (lat, lon) {
        final liveMarker = Marker(
          markerId: MarkerId(pet.id.toString()),
          position: LatLng(lat, lon),
          infoWindow: InfoWindow(title: pet.name),
        );

        setState(() {
          _markers = {liveMarker};
        });

        _mapController?.animateCamera(
          CameraUpdate.newLatLng(LatLng(lat, lon)),
        );
      },
    );
  }

  // Liberar recursos
  @override
  void dispose() {
    _stompClient?.disconnect();
    _mapController?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Ubicación de Mascotas'),
        automaticallyImplyLeading: false,
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: DropdownButtonFormField<Pet>(
              decoration: const InputDecoration(
                labelText: 'Selecciona una mascota',
                border: OutlineInputBorder(),
              ),
              items: petsWithDevice.map((pet) {
                return DropdownMenuItem(
                  value: pet,
                  child: Text(pet.name),
                );
              }).toList(),
              onChanged: (pet) {
                if (pet != null) {
                  _loadAndShowLocation(pet);
                }
              },
            ),
          ),
          Expanded(
            child: GoogleMap(
              initialCameraPosition: const CameraPosition(
                target: LatLng(-17.7833, -63.1833), // Coordenada por defecto
                zoom: 12,
              ),
              onMapCreated: (controller) => _mapController = controller,
              markers: _markers,
            ),
          ),
        ],
      ),
    );
  }
}
