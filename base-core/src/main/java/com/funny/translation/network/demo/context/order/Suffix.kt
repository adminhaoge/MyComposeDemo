package com.funny.translation.network.demo.context.order


enum class Suffix {
    EMPTY,
    APK_OR_RPK("apk"),
    HPK("hpk"),
    ZIP("zip"),
    RMVB("rmvb"),
    GBA("gba"),
    NGP("ngp"),
    NES("nes"),
    NDS("nds"),
    MP4("mp4"),
    MP3("mp3"),
    ISO("iso"),
    CSO("cso"),
    APKPATCH("patch");

    var suffix: String? = null
        private set

    constructor()
    constructor(suffix: String) {
        this.suffix = suffix
    }
}