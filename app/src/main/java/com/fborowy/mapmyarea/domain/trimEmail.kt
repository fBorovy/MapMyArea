package com.fborowy.mapmyarea.domain

fun trimEmail(email: String): String {
    val index = email.indexOf('@')
    return if (index != -1) {
        email.substring(0, index)
    } else {
        email
    }
}
