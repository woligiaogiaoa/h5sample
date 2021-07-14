package com.jiuzhou.h5game.bean

data class H5OrderBean(val game_fs_num: String,  //gameId
                       val fs_value: String,
                       val props_name: String,
                       val role_name: String,
                       val server: String,
                       val callback_url: String,
                       val google_product_id: String,
                       val extend_data: String,
                       val sign: String,
                       val fs_number: String,
                       val rate: Int)

data class OnlineTimeEventBean(val time:String)

data class DaysLoginEventBean(val day:String,val slug:String)