import 'package:pets_location_app/data/models/comment.dart';

class Post {
  final int id;
  final String title;
  final String content;
  final String category;
  final DateTime createdAt;
  final String authorId;
  final String authorName;
  final List<String> imageUrls;
  final Map<String, int> reactions;
  final List<Comment> comments;
  String? userReaction;

  Post({
    required this.id,
    required this.title,
    required this.content,
    required this.category,
    required this.createdAt,
    required this.authorId,
    required this.authorName,
    required this.imageUrls,
    required this.reactions,
    required this.comments,
    this.userReaction,
  });

  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
      id: json['id'],
      title: json['title'],
      content: json['content'],
      category: json['category'],
      createdAt: DateTime.parse(json['createdAt']),
      authorId: json['author']['id'],
      authorName: json['author']['name'],
      imageUrls: List<String>.from(json['images']),
      reactions: Map<String, int>.from(json['reactions'] ?? {}),
      comments: (json['comments'] as List<dynamic>)
          .map((c) => Comment.fromJson(c))
          .toList(),
      userReaction: json['userReaction'],
    );
  }
}
