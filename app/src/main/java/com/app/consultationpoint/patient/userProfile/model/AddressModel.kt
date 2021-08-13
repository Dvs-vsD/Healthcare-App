package com.app.consultationpoint.patient.userProfile.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class AddressModel: RealmObject() {
    @PrimaryKey
    var user_id: Long = 0
    var address: String = ""
    var city: String = ""
    var state: String = ""
    var country: String = ""
    var pincode: Int = 0
    var created_at: Long = 0
    var updated_at: Long = 0
    var to_deleted: Boolean = false
}