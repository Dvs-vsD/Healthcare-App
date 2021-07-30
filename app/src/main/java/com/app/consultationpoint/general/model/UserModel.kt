package com.app.consultationpoint.general.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserModel: RealmObject() {
    var id: Long? = 0
    @PrimaryKey
    var doc_id: Long = 0 // document id
    var user_type_id: Int? = 0
    var email: String? = ""
    var password: String? = ""
    var first_name: String? = ""
    var last_name: String? = ""
    var address: String? = ""
    var mobile: String? = ""
    var profile: String? = ""
    var city: String? = ""
    var specialization: String? = ""
    var experience_yr: String? = ""
    var about_info: String? = ""
    var created_at: Long = 0
    var updated_at: Long = 0
    var user_token: String = ""
}