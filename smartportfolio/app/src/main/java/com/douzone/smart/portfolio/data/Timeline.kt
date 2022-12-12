package com.douzone.smart.portfolio.data

import java.io.Serializable

class Timeline (
    var id: Int,
    var name: String,
    var title: String,
    var contents: String,
    var date: String,
    var image: ByteArray?,
    var defaultImage: Int,
    var url: String?,
    var circleColor: Int,
) : Serializable