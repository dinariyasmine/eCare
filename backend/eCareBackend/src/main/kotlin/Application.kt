package com.example.com

import io.ktor.server.application.*
import com.example.com.database.SupabaseConfig

fun main(args: Array<String>) {
    SupabaseConfig.testConnection()
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}
