package com.ssafy.intagral.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.ssafy.intagral.IntagralApplication
import com.ssafy.intagral.data.model.ProfileDetail
import com.ssafy.intagral.data.model.ProfileSimpleItem
import com.ssafy.intagral.data.model.ProfileType
import com.ssafy.intagral.data.service.HashtagService
import com.ssafy.intagral.data.service.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(private val hashtagService: HashtagService, private val userService: UserService): ViewModel() {
    private var profileDetail: MutableLiveData<ProfileDetail> = MutableLiveData()
    private var editStatus: MutableLiveData<EditStatus> = MutableLiveData()

    enum class EditStatus{
        DUPLICATED_NAME,
        ACTIVE,
        INAVTIVE,
        ERROR,
        LONG_NICKNAME,
    }

    fun getProfileDetail(): MutableLiveData<ProfileDetail> {
        return profileDetail
    }
    fun changeProfileDetail(profileSimple: ProfileSimpleItem) {
        viewModelScope.launch {
            var q: String = profileSimple.name
            if(profileSimple.type == ProfileType.user) {
                profileDetail.value = userService.getUserProfile(q) ?:profileDetail.value
            } else {
                profileDetail.value = hashtagService.getHashtagProfile(q) ?:profileDetail.value
            }
        }
    }

    fun getEditStatus(): MutableLiveData<EditStatus>{
        return editStatus
    }

    // TODO: profile image file upload request
    fun editProfileImage(filePath: String, imageBitmap: Bitmap){
        viewModelScope.launch {
            val file = File(filePath, "${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpeg")
            FileOutputStream(file).use { out ->
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 10, out)
            }
            var name: String? = null
            profileDetail.value?.let {
                name = it.name
            }
            if(name == null){
                cancel()
            }
            val response = userService.updateProfileImg(file)
            if(response.isSuccessful){
                Log.d("RETROFIT /api/user/profile/image", "${file.name} ????????? ??????")
                reloadProfileData(name!!)
            }else{
                Log.d("RETROFIT /api/user/profile/image", "?????? ?????? : ${response.code()}")
            }
        }
    }

    fun editProfileData(name: String, intro: String){
        viewModelScope.launch {
            // ?????? ?????? ??????
            if(name != profileDetail.value!!.name){
                // ?????? ??????
                val checkResponse = userService.checkValidName(name)
                if(checkResponse.isSuccessful){
                    checkResponse.body()?.let {
                        if(it.isAvailable == 2){
                            editStatus.value = EditStatus.DUPLICATED_NAME
                            this.cancel()
                        } else if(it.isAvailable == 1){
                            editStatus.value = EditStatus.LONG_NICKNAME
                            this.cancel()
                        } else {
                            //TODO: ?????? ????????? ?????? ????????? ????????? ??????
                        }
                    }
                }else{
                    Log.d("RETROFIT /api/user/check", "?????? ?????? : ${checkResponse.code()}")
                    editStatus.value = EditStatus.ERROR
                    this.cancel()
                }

                // ?????? ??????
                var json = JsonObject()
                json.addProperty("type", "nickname")
                json.addProperty("data", name)

                var response = userService.updateUserProfile(json)
                if(response.isSuccessful){
                    Log.d("RETROFIT /api/user/profile/info type=name", "??????")
                }else{
                    Log.d("RETROFIT /api/user/profile/info type=name", "?????? ?????? : ${response.code()}")
                    this.cancel()
                }
            }

            // ????????? ?????? ??????
            if(intro != profileDetail.value!!.intro){
                var json = JsonObject()
                json.addProperty("type", "intro")
                json.addProperty("data", intro)
                var response = userService.updateUserProfile(json)
                if(response.isSuccessful){
                    Log.d("RETROFIT /api/user/profile/info type=intro", "??????")
                }else{
                    Log.d("RETROFIT /api/user/profile/info type=intro", "?????? ?????? : ${response.code()}")
                    editStatus.value = EditStatus.ERROR
                    this.cancel()
                }
            }

            reloadProfileData(name)
            editStatus.value = EditStatus.INAVTIVE
        }
    }

    private fun reloadProfileData(name: String){
        viewModelScope.launch {
            val profile = userService.getUserProfile(name)
            profile?.let {
                profileDetail.value = it
            }
        }
        viewModelScope.launch {
           var response = userService.getMyInfo()
            if (response.isSuccessful) {
                response.body()?.let {
                    IntagralApplication.prefs.nickname = it.nickname
                }
            }
        }
    }
}