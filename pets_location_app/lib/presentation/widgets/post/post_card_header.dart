import 'package:flutter/material.dart';

class PostCardHeader extends StatelessWidget {
  final String authorName;
  final DateTime date;
  final Widget? categoryIcon;
  final String authorPhotoUrl;

  const PostCardHeader({
    super.key,
    required this.authorName,
    required this.date,
    required this.authorPhotoUrl,
    this.categoryIcon,
  });

  @override
  Widget build(BuildContext context) {
    final formattedDate = _formatDate(date);

    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        // Avatar + nombre + fecha
        Row(
          children: [
            CircleAvatar(
              radius: 18,
              backgroundImage: authorPhotoUrl.isNotEmpty
                  ? NetworkImage(authorPhotoUrl)
                  : null,
              child: authorPhotoUrl.isEmpty
                  ? const Icon(Icons.person) // fallback
                  : null,
            ),
            const SizedBox(width: 10),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  authorName,
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 2),
                Text(
                  formattedDate,
                  style: const TextStyle(fontSize: 12, color: Colors.grey),
                ),
              ],
            ),
          ],
        ),
        // Icono de categoría (opcional)
        if (categoryIcon != null) categoryIcon!,
      ],
    );
  }

  String _formatDate(DateTime date) {
    // Puedes personalizar esto según idioma/región
    return '${date.day.toString().padLeft(2, '0')}/'
           '${date.month.toString().padLeft(2, '0')}/'
           '${date.year}';
  }
}