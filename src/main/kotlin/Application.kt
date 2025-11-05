package com.example

import com.example.model.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

val tasks = mutableListOf(
    Task("learn new", "learn Ktor", Priority.High),
    Task("cleaning", "Clean the house", Priority.Low),
    Task("gardening", "Mow the lawn", Priority.Medium),
    Task("shopping", "Buy the groceries", Priority.High),
    Task("painting", "Paint the fence", Priority.Medium)
)

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}
