import 'package:dio/dio.dart';
import '../models/post.dart';

class NewsRemoteDataSource {
  final Dio _dio;

  NewsRemoteDataSource(this._dio);

  Future<List<Post>> fetchNews({required String userId}) async {
    try {
      final response = await _dio.get(
        '/news',
        queryParameters: {'userId': userId},
      );

      final List data = response.data;
      return data.map((json) => Post.fromJson(json)).toList();
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
    required String authorName,
    required String content,
  }) async {
    try {
      await _dio.post(
        '/news/$newsId/comments',
        data: {
          'authorId': authorId,
          'authorName': authorName,
          'content': content,
        },
      );
    } catch (e) {
      throw Exception('Error adding comment: $e');
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
}
