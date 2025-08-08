import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:dio/dio.dart';
import 'package:pets_location_app/core/network/api_client.dart';
import '../../../data/models/pet.dart';
import '../../../data/datasources/pet_remote_datasource.dart';
import '../../../data/datasources/device_remote_datasource.dart';
import '../../../data/datasources/location_remote_datasource.dart';
import '../../../data/websocket/location_stomp_client.dart';
import 'package:geolocator/geolocator.dart';
import 'dart:math';

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
  BitmapDescriptor? _petIcon;
  GoogleMapController? _mapController;
  List<Pet> petsWithDevice = [];
  Pet? selectedPet;
  LatLng? _userLocation;
  LatLng? _petLocation;

  LocationStompClient? _stompClient;

  @override
  void initState() {
    super.initState();
    _petRemote = PetRemoteDataSource(_dio);
    _deviceRemote = DeviceRemoteDataSource(_dio);
    _locationRemote = LocationRemoteDataSource(_dio);
    _loadPetsWithDevice();
    _loadCustomMarkerIcon();
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

  Future<void> _loadCustomMarkerIcon() async {
    _petIcon = await BitmapDescriptor.fromAssetImage(
      const ImageConfiguration(devicePixelRatio: 1.0),
      'assets/images/pet_location.png',
    );
  }

  Future<bool> _handleLocationPermission() async {
    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return false;
      }
    }
    if (permission == LocationPermission.deniedForever) {
      return false;
    }
    return true;
  }

  Future<void> _loadAndShowLocation(Pet pet) async {
    final location = await _locationRemote.getLatestLocation(pet.id);
    if (location == null) return;

    final petLatLng = LatLng(location.latitude, location.longitude);

    final petMarker = Marker(
      markerId: MarkerId(pet.id.toString()),
      position: petLatLng,
      infoWindow: InfoWindow(title: pet.name),
      icon: _petIcon ?? BitmapDescriptor.defaultMarker,
    );

    final hasPermission = await _handleLocationPermission();
    if (!hasPermission) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Permiso de ubicación no concedido")),
      );
      return;
    }

    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Activa la ubicación en el dispositivo")),
      );
      return;
    }

    try {
      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
      ).timeout(const Duration(seconds: 10));

      final userLatLng = LatLng(position.latitude, position.longitude);

      final userMarker = Marker(
        markerId: const MarkerId('user'),
        position: userLatLng,
        infoWindow: const InfoWindow(title: 'Tu ubicación'),
        icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueAzure),
      );

      setState(() {
        selectedPet = pet;
        _petLocation = petLatLng;
        _userLocation = userLatLng;
        _markers = {petMarker, userMarker};
      });

      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (_mapController != null) {
          final bounds = LatLngBounds(
            southwest: LatLng(
              min(userLatLng.latitude, petLatLng.latitude),
              min(userLatLng.longitude, petLatLng.longitude),
            ),
            northeast: LatLng(
              max(userLatLng.latitude, petLatLng.latitude),
              max(userLatLng.longitude, petLatLng.longitude),
            ),
          );
          _mapController!.animateCamera(
            CameraUpdate.newLatLngBounds(bounds, 100),
          );
        }
      });

    } catch (e) {
      print("Error al obtener la ubicación del usuario: $e");
    }

    _stompClient?.disconnect();

    _stompClient = LocationStompClient();
    _stompClient!.connect(
      petId: pet.id,
      onLocationReceived: (lat, lon) {
        final liveMarker = Marker(
          markerId: MarkerId(pet.id.toString()),
          position: LatLng(lat, lon),
          infoWindow: InfoWindow(title: pet.name),
          icon: _petIcon ?? BitmapDescriptor.defaultMarker,
        );

        final updatedMarkers = Set<Marker>.from(_markers)
          ..removeWhere((m) => m.markerId.value == pet.id.toString())
          ..add(liveMarker);

        setState(() {
          _markers = updatedMarkers;
        });

        _mapController?.animateCamera(
          CameraUpdate.newLatLng(LatLng(lat, lon)),
        );
      },
    );
  }

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
                target: LatLng(-17.7833, -63.1833),
                zoom: 12,
              ),
              onMapCreated: (controller) {
                _mapController = controller;
              },
              markers: _markers,
            ),
          ),
        ],
      ),
    );
  }
}
