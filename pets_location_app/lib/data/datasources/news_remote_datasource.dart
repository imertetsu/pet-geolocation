import 'package:dio/dio.dart';
import 'package:pets_location_app/data/models/detailed_post.dart';
import '../models/post.dart';
import '../models/page_result_posts.dart';

class NewsRemoteDataSource {
  final Dio _dio;

  NewsRemoteDataSource(this._dio);
    Future<PageResult<Post>> fetchNews({
    String? category,
    DateTime? fromDate,
    String? country,
    String? city,
    String? userId,
    int page = 0,
    int size = 10,
  }) async {
    try {
      final queryParams = <String, dynamic>{
        'page': page,
        'size': size,
      };

      if (category != null) queryParams['category'] = category;
      if (fromDate != null) queryParams['fromDate'] = fromDate.toIso8601String().split('T').first;
      if (country != null) queryParams['country'] = country;
      if (city != null) queryParams['city'] = city;
      if (userId != null) queryParams['userId'] = userId;

      final response = await _dio.get(
        '/news',
        queryParameters: queryParams,
      );

      return PageResult<Post>.fromJson(response.data, (json) => Post.fromJson(json));
    } catch (e) {
      throw Exception('Error fetching news: $e');
    }
  }

  
  Future<void> addReaction({
    required int newsId,
    required String userId,
    required String reaction,
  }) async {
    try {
      await _dio.post(
        '/news/$newsId/reactions',
        queryParameters: {
          'userId': userId,
          'reaction': reaction,
        },
      );
    } catch (e) {
      throw Exception('Error adding reaction: $e');
    }
  }

  Future<void> updateReaction({
    required int newsId,
    required String userId,
    required String newReaction,
  }) async {
    try {
      await _dio.put(
        '/news/$newsId/reactions',
        queryParameters: {
          'userId': userId,
          'reactionType': newReaction,
        },
      );
    } catch (e) {
      throw Exception('Error updating reaction: $e');
    }
  }

  Future<void> deleteReaction({
    required int newsId,
    required String userId,
  }) async {
    try {
      await _dio.delete(
        '/news/$newsId/reactions',
        queryParameters: {
          'userId': userId,
        },
      );
    } catch (e) {
      throw Exception('Error deleting reaction: $e');
    }
  }
  Future<void> addComment({
    required int newsId,
    required String authorId,
    required String content,
  }) async {
    try {
      await _dio.post(
        '/news/$newsId/comments',
        data: {
          'authorId': authorId,
          'content': content,
        },
      );
    } catch (e) {
      throw Exception('Error adding comment: $e');
    }
  }

  Future<void> deleteComment({
    required int newsId,
    required int commentId,
    required String requesterId,
  }) async {
    try {
      await _dio.delete(
        '/news/$newsId/comments/$commentId',
        queryParameters: {
          'requesterId': requesterId,
        },
      );
    } catch (e) {
      throw Exception('Error deleting comment: $e');
    }
  }

  Future<Post> fetchPostById(int postId) async {
    try {
      final response = await _dio.get('/news/$postId');
      return Post.fromJson(response.data);
    } catch (e) {
      throw Exception('Error fetching post by id: $e');
    }
  }
  Future<DetailedPost> fetchDetailedPostById(int postId) async {
    try {
      final response = await _dio.get('/news/$postId');
      return DetailedPost.fromJson(response.data);
    } catch (e) {
      throw Exception('Error fetching post by id: $e');
    }
  }
  Future<void> createPost({
    required String title,
    required String content,
    required String category,
    required String authorId,
    required String authorName,
    required String country,
    required String city,
    List<String> images = const [],
  }) async {
    try {
      await _dio.post(
        '/news',
        data: {
          'title': title,
          'content': content,
          'category': category,
          'authorId': authorId,
          'country': country,
          'city': city,
          'images': images,
        },
      );
    } catch (e) {
      throw Exception('Error creating post: $e');
    }
  }
  Future<Post> updatePost(
    int id,
    String title,
    String content,
    String category,
    String country,
    String city,
    List<String> images,
  ) async {
    final response = await _dio.put(
      '/news/$id',
      data: {
        "title": title,
        "content": content,
        "category": category,
        "country": country,
        "city": city,
        "images": images,
      },
    );
    return Post.fromJson(response.data);
  }

  Future<List<Post>> getPostsByUserId(String userId) async {
    final response = await _dio.get('/news/user/$userId');
    if (response.statusCode == 200) {
      return (response.data as List).map((json) => Post.fromJson(json)).toList();
    }
    throw Exception("Error al obtener posts del usuario");
  }

  Future<bool> deletePost(int postId) async {
    final response = await _dio.delete('/news/$postId');
    return response.statusCode == 204;
  }
}
