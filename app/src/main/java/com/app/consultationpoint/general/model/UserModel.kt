package com.app.consultationpoint.general.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserModel: RealmObject() {
    var id: Long = 0
    @PrimaryKey
    var doc_id: Long = 0 // DocumentId
    var username: String = ""
    var password: String = ""
    var first_name: String = ""
    var last_name: String = ""
    var email: String = ""
    var mobile: String = ""
    var gender: Int = 0 //0-Male 1-Female 2-Other
    var dob: String = ""

    //new added 1 fields
    var profile: String? = ""

    var user_type_id: Int = 0 //0-Patient 1-Doctor 2-Staff 3-Laboratory
    var user_status: String = ""
    var is_deleted: Boolean = false
    var is_verified: Boolean = false
    var created_at: Long = 0
    var updated_at: Long = 0
    var user_token: String = ""
    var about_info: String = ""
    var specialist_id: String = ""
    var experience_yr: String = ""
    var payment_id: String = ""
    var payment_detail: String = ""

    //1 - android
}