package com.greatinnovus.promotionapp.BasePackage

class testModel {

    lateinit var itemname:String
     var statusval:Int=0

    constructor(itemname: String, statusval: Int) {
        this.itemname = itemname
        this.statusval = statusval
    }
}