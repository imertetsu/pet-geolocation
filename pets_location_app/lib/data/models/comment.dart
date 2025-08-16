class Comment {
  final int id;
  final Author author;
  final String content;
  final DateTime createdAt;

  Comment({
    required this.id,
    required this.author,
    required this.content,
    required this.createdAt,
  });

  factory Comment.fromJson(Map<String, dynamic> json) {
    return Comment(
      id: json['id'],
      author: Author.fromJson(json['author']), // ← ahora mapeamos desde el objeto author
      content: json['content'],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }
}

class Author {
  final String id;
  final String name;

  Author({
    required this.id,
    required this.name,
  });

  factory Author.fromJson(Map<String, dynamic> json) {
    return Author(
      id: json['id'],
      name: json['name'],
    );
  }
}
