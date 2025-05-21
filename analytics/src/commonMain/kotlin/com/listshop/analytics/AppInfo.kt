package com.listshop.analytics




data class AppInfo(

    val baseUrl: String,
    val name: String?,
    val model: String?,
    val os: String?,
    val osVersion: String?,
    val clientType: String? = "Mobile",
    val clientVersion: String?,
    val buildNumber: String?,
    val deviceId: String?
) {
    constructor(
        baseUrl: String,
        name: String?,
        model: String?,
        os: String?,
        osVersion: String?,
        clientVersion: String?,
        buildNumber: String?,
        deviceId: String?
    ) : this(
        baseUrl=baseUrl,
    name=name,
    model=model,
    os=os,
    osVersion=osVersion,
        clientType="Mobile",
    clientVersion=clientVersion,
    buildNumber=buildNumber,
    deviceId=deviceId
    )


}



