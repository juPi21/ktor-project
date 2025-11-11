package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import com.example.model.*
import com.example.models.*


val tasks = mutableListOf(
    Task("learn new", "learn Ktor", Priority.High),
    Task("cleaning", "Clean the house", Priority.Low),
    Task("gardening", "Mow the lawn", Priority.Medium),
    Task("shopping", "Buy the groceries", Priority.High),
    Task("painting", "Paint the fence", Priority.Medium)
)

val people = mutableListOf(
    Person(1, "Alice", 25),
    Person(2, "Bob", 30)
)

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    embeddedServer(Netty, port = 8080) {

        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/") {
                call.respondText("REST API працює! Використовуйте /tasks або /people.")
            }

            get("/tasks") {
                call.respond(tasks)
            }

            get("/tasks/{title}") {
                val title = call.parameters["title"]
                val task = tasks.find { it.name.equals(title, ignoreCase = true) }
                if (task != null) call.respond(task)
                else call.respondText("Завдання не знайдено", status = HttpStatusCode.NotFound)
            }

            post("/tasks") {
                val newTask = call.receive<Task>()
                tasks.add(newTask)
                call.respondText("Завдання додано!", status = HttpStatusCode.Created)
            }

            delete("/tasks/{title}") {
                val title = call.parameters["title"]
                val removed = tasks.removeIf { it.name.equals(title, ignoreCase = true) }
                if (removed) call.respondText("Завдання видалено")
                else call.respondText("Завдання не знайдено", status = HttpStatusCode.NotFound)
            }

            get("/people") {
                call.respond(people)
            }

            get("/people/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val person = people.find { it.id == id }
                if (person != null) call.respond(person)
                else call.respondText("Користувача не знайдено", status = HttpStatusCode.NotFound)
            }

            post("/people") {
                val newPerson = call.receive<Person>()
                people.add(newPerson)
                call.respondText("Користувача додано!", status = HttpStatusCode.Created)
            }
        }
    }.start(wait = true)
}
