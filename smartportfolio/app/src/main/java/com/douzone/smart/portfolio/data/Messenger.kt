package com.douzone.smart.portfolio.data

import java.io.Serializable

class Messenger (
    var id: Int,
    var name: String,
    var title: String,
    var contents: String,
    var image: String?,
    var url: String?
) : Serializable