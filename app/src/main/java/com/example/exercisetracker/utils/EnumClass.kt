package com.example.exercisetracker.utils

enum class Type(val rgb: Int) {
    INDOOR(0x7fe5ab),
    OUTDOOR(0xab7fe5);

    fun toHex(): String {
        return String.format("#%06X", (0xFFFFFF and rgb))
    } // returns "#FF0000"
}