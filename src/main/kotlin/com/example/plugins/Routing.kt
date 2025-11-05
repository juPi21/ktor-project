package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.http.*

import com.example.model.*

fun Application.configureRouting() {
    routing {

        staticResources("/task-ui", "task-ui")

        get("/tasks") {
            call.respondText(
                contentType = ContentType.Text.Html,
                text = tasks.tasksAsTable()
            )
        }

        get("/tasks/byPriority/{priority?}") {
            val priorityParam = call.parameters["priority"]

            if (priorityParam == null) {
                call.respondText(
                    text = tasks.tasksAsTable(),
                    contentType = ContentType.Text.Html
                )
                return@get
            }

            val parsedPriority = try {
                Priority.valueOf(priorityParam)
            } catch (e: IllegalArgumentException) {
                call.respondText(
                    "Invalid priority value",
                    status = HttpStatusCode.BadRequest
                )
                return@get
            }

            val filteredTasks = tasks.filter { it.priority == parsedPriority }
            call.respondText(
                text = filteredTasks.tasksAsTable(),
                contentType = ContentType.Text.Html
            )
        }

        post("/tasks") {
            try {
                val params = call.receiveParameters()

                val name = params["name"] ?: return@post call.respondText(
                    "Missing name", status = HttpStatusCode.BadRequest
                )

                val description = params["description"] ?: "No description"
                val priorityString = params["priority"] ?: "Low"

                val priority = try {
                    Priority.valueOf(priorityString.replaceFirstChar { it.uppercase() })
                } catch (e: IllegalArgumentException) {
                    return@post call.respondText(
                        "Invalid priority value. Use Low, Medium, High, or Vital.",
                        status = HttpStatusCode.BadRequest
                    )
                }

                val newTask = Task(name, description, priority)
                tasks.add(newTask)

                call.respondText(
                    "Task added successfully: $name",
                    status = HttpStatusCode.Created
                )

            } catch (e: Exception) {
                call.respondText(
                    "Error processing request: ${e.message}",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
    }
}

