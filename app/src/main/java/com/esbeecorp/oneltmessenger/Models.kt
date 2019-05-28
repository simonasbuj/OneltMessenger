package com.esbeecorp.oneltmessenger

class User(val uid: String, val username: String, val profileImageUrl: String){
    // emtpy constructor,used for data desiraliztaion
    constructor() : this("", "", "")
}