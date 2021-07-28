package com.app.consultationpoint.general.model

class UserModel {
    var user_id: String? = ""
    var user_type_id: Int? = 0
    var email: String? = ""
    var password: String? = ""
    var first_name: String? = ""
    var last_name: String? = ""
    var address: String? = ""
    var mobile: String? = ""
    var profile: String? = ""
    var city: String? = ""
    var degrees = ArrayList<String>()
    var specialization: String? = ""
    var experience_yr: String? = ""
    var about_info: String? = ""
}