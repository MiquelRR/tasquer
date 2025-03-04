package com.miquel.firebasetraining.data

import com.google.firebase.Timestamp
import java.io.Serializable


data class TaskItem(
    var id: String? = null,
    var date: Timestamp? = null,
    var description: String? = null,
    var isDone: Boolean? = false,
    var duration: Int? = null,
    var assignedUser: String? = null,
    var email: String? = null,
    var link: String? = null
) : Serializable {
    // No-argument constructor (required by Firestore)
    constructor() : this(null, null,null, null, null, null, null, null)
}