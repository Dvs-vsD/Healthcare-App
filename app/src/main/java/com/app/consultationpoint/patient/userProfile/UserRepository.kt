package com.app.consultationpoint.patient.userProfile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.network.RetrofitClient
import com.app.consultationpoint.patient.dashboard.model.SpecialistModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import com.app.consultationpoint.patient.userProfile.model.PostalDataModel
import com.app.consultationpoint.patient.userProfile.model.PostalModel
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class UserRepository @Inject constructor(private var firebaseSource: FirebaseSource) {

    private var postalDataModel: MutableLiveData<PostalDataModel> = MutableLiveData()

    fun logout() {
        firebaseSource.logOut()
    }

    fun updateProfile(model: UserModel, adrModel: AddressModel) {
        firebaseSource.updateProfile(model, adrModel)
    }

    fun getProfileUptStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }

    fun uploadProfile(path: Uri) {
        firebaseSource.uploadProfile(path)
    }

    fun fetchPostalData(it: String) {
        Timber.d("api call under function")

        var model = PostalDataModel()
        val call = RetrofitClient.getClient().getPostalDetail(it)
        call.enqueue(object : Callback<PostalModel> {
            override fun onResponse(call: Call<PostalModel>, response: Response<PostalModel>) {
                if (response.body() != null) {
                    Timber.d("api call response is there")

                    if (response.body()!!.Status == "Success") {
                        Timber.d("api call success")
                        model = response.body()!!.PostOffice[0]
                        Timber.d("api call data: ${model.District}")
                        Timber.d("api call data: ${model.State}")
                        Timber.d("api call data: ${model.Country}")

                        postalDataModel.value = model
                    }
                } else {
                    Timber.d("api call No response")
                }
            }

            override fun onFailure(call: Call<PostalModel>, t: Throwable) {
                Timber.d("api call in failure")
            }
        })
    }

    fun getPostalData(): LiveData<PostalDataModel> {
        return postalDataModel
    }

    fun getSpecialistArray(): ArrayList<SpecialistModel> {
        Realm.getDefaultInstance().use { mRealm ->
            val mRealmResults = mRealm.where(SpecialistModel::class.java).findAll()
            return mRealm.copyFromRealm(mRealmResults) as ArrayList<SpecialistModel>
        }
    }

    fun getSpecializationName(id: Int): String {
        return firebaseSource.getSpecializationName(id)
    }
}