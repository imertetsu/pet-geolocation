import 'package:flutter/material.dart';
import 'package:dio/dio.dart';

class AssociateDevicePage extends StatefulWidget {
  final int petId;

  const AssociateDevicePage({super.key, required this.petId});

  @override
  State<AssociateDevicePage> createState() => _AssociateDevicePageState();
}

class _AssociateDevicePageState extends State<AssociateDevicePage> {
  final _deviceIdController = TextEditingController();
  final Dio _dio = Dio(BaseOptions(baseUrl: 'http://localhost:8080/api'));
  bool _loading = false;
  String? _error;

  Future<void> _associateDevice() async {
    setState(() {
      _loading = true;
      _error = null;
    });

    try {
      await _dio.post('/devices', data: {
        "deviceId": _deviceIdController.text.trim(),
        "petId": widget.petId,
      });

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Dispositivo asociado correctamente')),
        );
        Navigator.pop(context, true); // Volvemos con un valor para recargar
      }
    } catch (e) {
      setState(() {
        _error = 'Error al asociar dispositivo: ${e.toString()}';
      });
    } finally {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Asociar Dispositivo')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            if (_error != null)
              Text(_error!, style: const TextStyle(color: Colors.red)),
            TextField(
              controller: _deviceIdController,
              decoration: const InputDecoration(
                labelText: 'ID del Dispositivo (ej: A9G-334517)',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 20),
            _loading
                ? const CircularProgressIndicator()
                : ElevatedButton.icon(
                    icon: const Icon(Icons.link),
                    label: const Text('Asociar'),
                    onPressed: _associateDevice,
                  ),
          ],
        ),
      ),
    );
  }
}
