package com.example.exercisetracker.utils



import com.example.exercisetracker.db.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // Define your API endpoints and methods here
    @GET("api/endpoint/for/users")
    suspend fun getUsers(): Response<List<User>>

    @POST("api/endpoint/for/user")
    suspend fun createUser(user: User): Response<User>
}

class RemoteDataSource {


}
