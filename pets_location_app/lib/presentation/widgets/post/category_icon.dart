import 'package:flutter/material.dart';

class CategoryIcon extends StatelessWidget {
  final String category;
  final double size;

  const CategoryIcon({
    super.key,
    required this.category,
    this.size = 54,
  });

  @override
  Widget build(BuildContext context) {
    final String? asset = _getAssetPath(category);

    if (asset == null) return const SizedBox();

    return Tooltip(
      message: _getCategoryLabel(category),
      child: Image.asset(
        asset,
        width: size,
        height: size,
        fit: BoxFit.contain,
      ),
    );
  }

  String? _getAssetPath(String category) {
    switch (category.toUpperCase()) {
      case 'LOST_PET':
        return 'assets/icons/missing_pet.png';
      case 'SEARCH_HOME':
        return 'assets/icons/searching_home.png';
      case 'FOUND_PET':
        return 'assets/icons/pet_found.png';
      case 'ACCESSORY_SALE':
        return 'assets/icons/pet_store.png';
      case 'PET_EVENTS':
        return 'assets/icons/pet_events.png';
      case 'TIPS':
        return 'assets/icons/pet_advices.png';
      case 'HEALTH':
        return 'assets/icons/pet_health.png';
      case 'PET_AWARD':
        return 'assets/icons/award_pet.png';
      case 'SERVICES':
        return 'assets/icons/pet_service.png';
      case 'SUCCESS_STORY':
        return 'assets/icons/pet_happy_stories.png';
      case 'ALERT':
        return 'assets/icons/pet_alert.png';
      default:
        return null;
    }
  }

  String _getCategoryLabel(String category) {
    switch (category.toUpperCase()) {
      case 'LOST_PET':
        return 'Mascota perdida';
      case 'SEARCH_HOME':
        return 'Busca un hogar';
      case 'FOUND_PET':
        return 'Mascota encontrada';
      case 'ACCESSORY_SALE':
        return 'Venta de accesorios';
      case 'PET_EVENTS':
        return 'Eventos para mascotas';
      case 'TIPS':
        return 'Consejos';
      case 'HEALTH':
        return 'Salud animal';
      case 'PET_AWARD':
        return 'Premio a mascota';
      case 'SERVICES':
        return 'Servicios';
      case 'SUCCESS_STORY':
        return 'Historia exitosa';
      case 'ALERT':
        return 'Alerta';
      default:
        return 'Categoría desconocida';
    }
  }
}
