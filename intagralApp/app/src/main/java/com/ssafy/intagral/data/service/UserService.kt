package com.ssafy.intagral.data.service

import android.util.Log
import com.google.gson.JsonObject
import com.ssafy.intagral.data.model.ProfileDetail
import com.ssafy.intagral.data.model.ProfileType
import com.ssafy.intagral.data.repository.UserRepository
import com.ssafy.intagral.di.CommonRepository
import okhttp3.RequestBody
import retrofit2.Retrofit

const val imgPath = "https://intagral-file-upload-bucket.s3.ap-northeast-2.amazonaws.com/%EC%83%88+%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8.png"

class UserService {
    var commonRepository: Retrofit = CommonRepository.getCommonRepository()
    private var userRepository: UserRepository
    init{
        userRepository = commonRepository.create(UserRepository::class.java)
    }

    suspend fun login(json: JsonObject) = userRepository.login(json)
    suspend fun logout() = userRepository.logout()

    suspend fun getUserProfile(q: String): ProfileDetail? {
        var response = userRepository.getUserProfile(q)
        if(response.isSuccessful){
            var profileDetail: ProfileDetail
            response.body()?.let {
                profileDetail = ProfileDetail(ProfileType.user, it.nickname!!, it.follower ?:0, it.isFollow ?:false,
                    it.imgPath ?: imgPath, it.following ?:0, it.hashtag ?:0, it.intro ?:"")
                return profileDetail
            }
        } else {
            Log.d("RETROFIT GET /api/user/profile", "응답 에러 : ${response.code()}")
            return null
        }
        return null
    }

    suspend fun updateUserProfile(json: JsonObject) = userRepository.updateUserProfile(json)
    suspend fun updateProfileImg(data: RequestBody) = userRepository.updateProfileImg(data) //??
}