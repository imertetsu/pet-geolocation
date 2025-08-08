import 'package:pets_location_app/data/models/comment.dart';
//Made for refresh comments
class DetailedPost {
  final int id;
  final String title;
  final String content;
  final String category;
  final DateTime createdAt;
  final String authorId;
  final String authorName;
  final String country;
  final String city;
  final List<String> imageUrls;
  final List<dynamic> reactions;
  final List<Comment> comments;

  DetailedPost({
    required this.id,
    required this.title,
    required this.content,
    required this.category,
    required this.createdAt,
    required this.authorId,
    required this.authorName,
    required this.country,
    required this.city,
    required this.imageUrls,
    required this.reactions,
    required this.comments,
  });

  factory DetailedPost.fromJson(Map<String, dynamic> json) {
    return DetailedPost(
      id: json['id'],
      title: json['title'],
      content: json['content'],
      category: json['category'],
      createdAt: DateTime.parse(json['createdAt']),
      authorId: json['authorId'],
      authorName: json['authorName'],
      country: json['country'],
      city: json['city'],
      imageUrls: List<String>.from(json['images'] ?? []),
      reactions: json['reactions'] ?? [],
      comments: (json['comments'] as List<dynamic>? ?? [])
          .map((c) => Comment.fromJson(c))
          .toList(),
    );
  }
}
