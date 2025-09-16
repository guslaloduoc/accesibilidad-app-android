package com.example.exp1_s2_gustavo_renjifo.utils

import androidx.navigation.NavController

/**
 * Utilidades de navegación usando funciones de orden superior
 */

// Tipo alias para funciones de navegación
typealias NavigationAction = () -> Unit
typealias NavigationCallback = (String) -> Unit
typealias ConditionalNavigation = (Boolean) -> Unit

// Clase para manejar navegación con lambdas
class NavigationManager(private val navController: NavController) {

    // Función de orden superior para navegación condicional
    fun navigateIf(
        condition: () -> Boolean,
        destination: String,
        onNavigate: () -> Unit = {},
        onBlock: () -> Unit = {}
    ) {
        if (condition()) {
            navController.navigate(destination)
            onNavigate()
        } else {
            onBlock()
        }
    }

    // Función de orden superior para navegación con validación
    fun navigateWithValidation(
        destination: String,
        validator: () -> ValidationResult,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val result = validator()
        if (result.isValid) {
            navController.navigate(destination)
            onSuccess()
        } else {
            onError(result.errorMessage ?: "Error de validación")
        }
    }

    // Función de orden superior para navegación con cleanup
    fun navigateWithCleanup(
        destination: String,
        cleanup: () -> Unit
    ) {
        cleanup()
        navController.navigate(destination)
    }

    // Función de orden superior para pop con condición
    fun popIf(
        condition: () -> Boolean,
        onPop: () -> Unit = {},
        onStay: () -> Unit = {}
    ): Boolean {
        return if (condition()) {
            val result = navController.popBackStack()
            if (result) onPop() else onStay()
            result
        } else {
            onStay()
            false
        }
    }
}

// Objeto con utilidades de navegación funcional
object NavigationUtils {

    // Función de orden superior para crear callbacks de navegación
    fun createNavigationCallback(
        navController: NavController,
        destination: String,
        beforeNavigate: () -> Unit = {},
        afterNavigate: () -> Unit = {}
    ): NavigationAction = {
        beforeNavigate()
        navController.navigate(destination)
        afterNavigate()
    }

    // Función de orden superior para crear callback de retroceso
    fun createBackCallback(
        navController: NavController,
        beforePop: () -> Unit = {},
        afterPop: () -> Unit = {}
    ): NavigationAction = {
        beforePop()
        val success = navController.popBackStack()
        if (success) afterPop()
    }

    // Función de orden superior para navegación con parámetros
    fun <T> createParameterizedNavigation(
        navController: NavController,
        destinationBuilder: (T) -> String,
        parameterValidator: (T) -> Boolean = { true }
    ): (T) -> Unit = { parameter ->
        if (parameterValidator(parameter)) {
            navController.navigate(destinationBuilder(parameter))
        }
    }

    // Función de orden superior para batch de navegaciones
    fun executeBatchNavigation(
        actions: List<() -> Unit>,
        onEachAction: (Int) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        actions.forEachIndexed { index, action ->
            action()
            onEachAction(index)
        }
        onComplete()
    }

    // Lambda para navegación con animaciones personalizadas
    val createAnimatedNavigation: (NavController, String) -> NavigationAction = { navController, destination ->
        {
            navController.navigate(destination) {
                // Configuraciones de animación se pueden agregar aquí
                launchSingleTop = true
            }
        }
    }

    // Función de orden superior para manejo de deep links
    fun handleDeepLink(
        uri: String,
        handlers: Map<String, (String) -> Unit>,
        defaultHandler: (String) -> Unit = {}
    ) {
        val matchingHandler = handlers.entries.find { (pattern, _) ->
            uri.contains(pattern)
        }

        if (matchingHandler != null) {
            matchingHandler.value(uri)
        } else {
            defaultHandler(uri)
        }
    }
}

// Extensiones funcionales para NavController
fun NavController.navigateWithResult(
    destination: String,
    resultCallback: (Boolean) -> Unit
) {
    try {
        navigate(destination)
        resultCallback(true)
    } catch (e: Exception) {
        resultCallback(false)
    }
}

fun NavController.navigateAndClear(
    destination: String,
    clearBackStack: () -> Boolean = { true }
) {
    if (clearBackStack()) {
        navigate(destination) {
            popUpTo(graph.startDestinationId) { inclusive = true }
        }
    } else {
        navigate(destination)
    }
}

// Función de orden superior para crear un router
class FunctionalRouter {
    private val routes = mutableMapOf<String, NavigationAction>()

    fun addRoute(route: String, action: NavigationAction) {
        routes[route] = action
    }

    fun navigate(route: String, onNotFound: () -> Unit = {}) {
        routes[route]?.invoke() ?: onNotFound()
    }

    fun addConditionalRoute(
        route: String,
        condition: () -> Boolean,
        action: NavigationAction,
        fallback: NavigationAction = {}
    ) {
        routes[route] = {
            if (condition()) action() else fallback()
        }
    }
}