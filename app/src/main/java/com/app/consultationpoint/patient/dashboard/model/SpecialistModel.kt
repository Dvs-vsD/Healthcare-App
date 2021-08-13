package com.app.consultationpoint.patient.dashboard.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SpecialistModel: RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var image: String = ""
    var code: String = ""
    var color_code: String = ""
    var updated_at: Long = 0
    var is_deleted: Boolean = false
}