package com.app.consultationpoint.patient.doctor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import timber.log.Timber

class DoctorRepository {
    private var doctorList: MutableLiveData<ArrayList<DoctorModel>> = MutableLiveData(ArrayList())
    private var mRealmResult: RealmResults<DoctorModel>
    private var mRealm = Realm.getDefaultInstance()

    init {
        mRealmResult = mRealm.where(DoctorModel::class.java).findAll()
        doctorList.value?.addAll(mRealmResult)
        Timber.d("in repo %s", doctorList.value.toString())

        mRealmResult.addChangeListener { change ->
            doctorList.value?.clear()
            val list: ArrayList<DoctorModel> = ArrayList()
            list.addAll(change)
            doctorList.value = list
            Timber.d("in repo change %s", doctorList.value.toString())
        }
    }

    fun getDoctorList(): LiveData<ArrayList<DoctorModel>> {
        return doctorList
    }

    fun searchDoctor(str: String) {
        val resultsInFName = mRealm.where(DoctorModel::class.java).contains("first_name", str, Case.INSENSITIVE)
            .findAll()
        val resultsInLName = mRealm.where(DoctorModel::class.java).contains("last_name", str, Case.INSENSITIVE)
            .findAll()
        val set = HashSet<DoctorModel>()
        set.addAll(resultsInFName)
        set.addAll(resultsInLName)
        if (set.isNotEmpty()) {
            val list: ArrayList<DoctorModel> = ArrayList()
            list.addAll(set)
            doctorList.value?.clear()
            doctorList.value = list
        }
    }
}