package com.example.di_jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Prioridad(val texto: String) {
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja")
}

data class Tarea(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val prioridad: Prioridad,
    val completada: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                ListaTareasApp()
            }
        }
    }
}

@Composable
fun ListaTareasApp() {

    val tareas = remember {
        mutableStateListOf(
            Tarea(
                1, "Estudiar Kotlin",
                "Repasar Jetpack Compose",
                Prioridad.ALTA
            ),
            Tarea(
                2, "Hacer ejercicios",
                "Practicar componentes de interfaz",
                Prioridad.MEDIA
            ),
            Tarea(
                3, "Ordenar apuntes",
                "Preparar los apuntes del tema",
                Prioridad.BAJA
            ),
            Tarea(
                4, "Entregar el proyecto",
                "Revisar y entregar la aplicación",
                Prioridad.ALTA
            )
        )
    }

    val pendientes = tareas.filter { !it.completada }
    val completadas = tareas.filter { it.completada }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Mi lista de tareas",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Text(
                        text = "${pendientes.size} pendientes · " +
                                "${completadas.size} completadas",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                Text(
                    text = "Tareas pendientes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (pendientes.isEmpty()) {
                item {
                    Text("No tienes tareas pendientes.")
                }
            }

            items(pendientes, key = { it.id }) { tarea ->
                TarjetaTarea(
                    tarea = tarea,
                    onCompletar = {
                        actualizarTarea(
                            tareas,
                            tarea,
                            tarea.copy(completada = true)
                        )
                    },
                    onCambiarPrioridad = { nuevaPrioridad ->
                        actualizarTarea(
                            tareas,
                            tarea,
                            tarea.copy(prioridad = nuevaPrioridad)
                        )
                    }
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 2.dp
                )
            }

            item {
                Text(
                    text = "Tareas completadas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (completadas.isEmpty()) {
                item {
                    Text("Todavía no has completado ninguna tarea.")
                }
            }

            items(completadas, key = { it.id }) { tarea ->
                TarjetaTarea(
                    tarea = tarea,
                    onCompletar = {
                        actualizarTarea(
                            tareas,
                            tarea,
                            tarea.copy(completada = false)
                        )
                    },
                    onCambiarPrioridad = { nuevaPrioridad ->
                        actualizarTarea(
                            tareas,
                            tarea,
                            tarea.copy(prioridad = nuevaPrioridad)
                        )
                    }
                )
            }
        }
    }
}

fun actualizarTarea(
    tareas: MutableList<Tarea>,
    tareaAntigua: Tarea,
    tareaNueva: Tarea
) {
    val indice = tareas.indexOfFirst { it.id == tareaAntigua.id }

    if (indice != -1) {
        tareas[indice] = tareaNueva
    }
}

@Composable
fun TarjetaTarea(
    tarea: Tarea,
    onCompletar: () -> Unit,
    onCambiarPrioridad: (Prioridad) -> Unit
) {

    var menuEstadoAbierto by remember { mutableStateOf(false) }
    var menuPrioridadAbierto by remember { mutableStateOf(false) }

    val colorPrioridad = when (tarea.prioridad) {
        Prioridad.ALTA -> Color(0xFFD32F2F)
        Prioridad.MEDIA -> Color(0xFFF9A825)
        Prioridad.BAJA -> Color(0xFF388E3C)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = tarea.completada,
                    onCheckedChange = {
                        onCompletar()
                    }
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = tarea.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textDecoration = if (tarea.completada) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )

                    Text(
                        text = tarea.descripcion,
                        fontSize = 14.sp
                    )
                }

                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = colorPrioridad,
                            contentColor = Color.White
                        ) {
                            Text(tarea.prioridad.texto)
                        }
                    }
                ) {
                    Text(
                        text = "!",
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Box {
                    OutlinedButton(
                        onClick = {
                            menuEstadoAbierto = true
                        }
                    ) {
                        Text("Estado ▼")
                    }

                    DropdownMenu(
                        expanded = menuEstadoAbierto,
                        onDismissRequest = {
                            menuEstadoAbierto = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (tarea.completada) {
                                        "Marcar como pendiente"
                                    } else {
                                        "Marcar como completada"
                                    }
                                )
                            },
                            onClick = {
                                onCompletar()
                                menuEstadoAbierto = false
                            }
                        )
                    }
                }

                Box {
                    OutlinedButton(
                        onClick = {
                            menuPrioridadAbierto = true
                        }
                    ) {
                        Text("Prioridad ▼")
                    }

                    DropdownMenu(
                        expanded = menuPrioridadAbierto,
                        onDismissRequest = {
                            menuPrioridadAbierto = false
                        }
                    ) {
                        Prioridad.values().forEach { prioridad ->
                            DropdownMenuItem(
                                text = {
                                    Text(prioridad.texto)
                                },
                                onClick = {
                                    onCambiarPrioridad(prioridad)
                                    menuPrioridadAbierto = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
