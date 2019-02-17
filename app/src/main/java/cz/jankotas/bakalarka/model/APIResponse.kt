package cz.jankotas.bakalarka.model

class APIResponse {

    var error:Boolean=false
    var message:String?=null
    var access_token:String?=null
    var token_type:String?=null
    var expires_at:String?=null
    var user:User?=null

}