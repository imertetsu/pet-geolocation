import 'package:flutter/material.dart';
import '../../data/models/pet.dart';
import '../pages/devices/associate_device_page.dart';

class PetCard extends StatelessWidget {
  final Pet pet;
  final VoidCallback onDelete;
  final VoidCallback onAssociateDevice;

  const PetCard({
    super.key,
    required this.pet,
    required this.onDelete,
    required this.onAssociateDevice,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      child: ListTile(
        leading: Tooltip(
          message: pet.hasDevice
              ? 'Dispositivo asociado'
              : 'Sin dispositivo',
          child: Icon(
            Icons.location_on,
            color: pet.hasDevice ? Colors.green : Colors.grey,
          ),
        ),
        title: Text(pet.name),
        subtitle: Text('${pet.species} - ${pet.breed}'),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Botón de asociar dispositivo si no tiene uno
            IconButton(
              icon: Icon(
                pet.hasDevice ? Icons.link_off : Icons.link,
                color: pet.hasDevice ? Colors.orange : Colors.blue,
              ),
              tooltip: pet.hasDevice ? 'Desvincular dispositivo' : 'Asociar dispositivo',
              onPressed: onAssociateDevice,
            ),
            // Botón de eliminar
            IconButton(
              icon: const Icon(Icons.delete, color: Colors.red),
              tooltip: 'Eliminar mascota',
              onPressed: onDelete,
            ),
          ],
        ),
      ),
    );
  }
}
