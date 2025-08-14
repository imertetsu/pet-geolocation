import 'package:flutter/material.dart';

class PostReactionsBar extends StatelessWidget {
  final int likeCount;
  final int loveCount;
  final int sadCount;
  final int hopeCount;

  const PostReactionsBar({
    super.key,
    required this.likeCount,
    required this.loveCount,
    required this.sadCount,
    required this.hopeCount,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [
        _buildItem(Icons.thumb_up, Colors.blue, likeCount),
        _buildItem(Icons.favorite, Colors.red, loveCount),
        _buildItem(Icons.sentiment_dissatisfied_rounded, Colors.orange, sadCount),
        _buildItem(Icons.star, Colors.green, hopeCount),
      ],
    );
  }

  Widget _buildItem(IconData icon, Color color, int count) {
    return Row(
      children: [
        Icon(icon, color: color, size: 18),
        const SizedBox(width: 4),
        Text('$count'),
      ],
    );
  }
}
