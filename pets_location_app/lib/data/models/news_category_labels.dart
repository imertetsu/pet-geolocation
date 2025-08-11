import '../models/news_category.dart'; 

class NewsCategoryLabels {
  static const Map<NewsCategory, String> es = {
    NewsCategory.LOST_PET: "Mascota perdida",
    NewsCategory.SEARCH_HOME: "Busca hogar",
    NewsCategory.FOUND_PET: "Mascota encontrada",
    NewsCategory.ACCESSORY_SALE: "Venta de accesorios",
    NewsCategory.PET_EVENTS: "Eventos para mascotas",
    NewsCategory.TIPS: "Consejos",
    NewsCategory.HEALTH: "Salud",
    NewsCategory.PET_AWARD: "Premio a mascota",
    NewsCategory.SERVICES: "Servicios",
    NewsCategory.SUCCESS_STORY: "Historia de éxito",
    NewsCategory.ALERT: "Alerta",
  };

  static String getEsLabel(NewsCategory category) {
    return es[category] ?? category.name;
  }
}
