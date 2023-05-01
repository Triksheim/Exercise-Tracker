package com.example.exercisetracker.utils



import com.example.exercisetracker.db.User
import com.example.exercisetracker.db.UserJSON
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import kotlin.Result
import retrofit2.HttpException
import retrofit2.Response


private const val BASE_URL = "https://wfa-media.com/exercise23/v3/api.php/"
private const val API_KEY = "004E06B1-E02"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(ApiKeyInterceptor())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

private class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val requestWithApiKey = originalRequest.newBuilder()
            .header("Authorization", API_KEY)
            .build()
        return chain.proceed(requestWithApiKey)
    }
}


interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): UserJSON

    @POST("users")
    suspend fun createUser(@Body user: User): Response<UserJSON>

}


class RemoteDataSource(private val apiService: ApiService) {

    suspend fun getUser(id: Int) = apiService.getUser(id)

    suspend fun createUser(user: User): Result<UserJSON> {
        return try {
            val response = apiService.createUser(user)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP error ${response.code()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

val apiService = retrofit.create(ApiService::class.java)
val remoteDataSource = RemoteDataSource(apiService)
