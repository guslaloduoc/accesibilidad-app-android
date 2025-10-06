package com.example.exp1_s2_gustavo_renjifo.utils

import androidx.compose.runtime.*

/**
 * Utilidades de estado usando funciones de orden superior y lambdas
 */

// Tipo alias para manejadores de estado
typealias StateUpdater<T> = (T) -> T
typealias StateValidator<T> = (T) -> Boolean
typealias StateListener<T> = (T) -> Unit

// Estado reactivo con validación
@Composable
fun <T> rememberValidatedState(
    initialValue: T,
    validator: StateValidator<T> = { true },
    onValidationFailed: (T) -> Unit = {}
): MutableState<T> {
    val state = remember { mutableStateOf(initialValue) }

    return object : MutableState<T> {
        override var value: T
            get() = state.value
            set(newValue) {
                if (validator(newValue)) {
                    state.value = newValue
                } else {
                    onValidationFailed(newValue)
                }
            }

        override fun component1(): T = value
        override fun component2(): (T) -> Unit = { value = it }
    }
}

// Estado con historial usando lambdas
class StateHistory<T>(initialValue: T) {
    private val history = mutableListOf(initialValue)
    private var currentIndex = 0

    val current: T get() = history[currentIndex]
    val canUndo: Boolean get() = currentIndex > 0
    val canRedo: Boolean get() = currentIndex < history.size - 1

    fun update(updater: StateUpdater<T>) {
        val newValue = updater(current)
        if (newValue != current) {
            // Eliminar historial futuro si estamos en el medio
            if (canRedo) {
                history.subList(currentIndex + 1, history.size).clear()
            }
            history.add(newValue)
            currentIndex = history.size - 1
        }
    }

    fun undo(): T? = if (canUndo) {
        currentIndex--
        current
    } else null

    fun redo(): T? = if (canRedo) {
        currentIndex++
        current
    } else null

    fun reset(value: T) {
        history.clear()
        history.add(value)
        currentIndex = 0
    }
}

// Composable para estado con historial
@Composable
fun <T> rememberStateHistory(initialValue: T): StateHistory<T> {
    return remember { StateHistory(initialValue) }
}

// Estado observable con listeners
class ObservableState<T>(initialValue: T) {
    private var _value: T = initialValue
    private val listeners = mutableListOf<StateListener<T>>()

    var value: T
        get() = _value
        set(newValue) {
            if (_value != newValue) {
                _value = newValue
                listeners.forEach { it(newValue) }
            }
        }

    fun addListener(listener: StateListener<T>) {
        listeners.add(listener)
    }

    fun removeListener(listener: StateListener<T>) {
        listeners.remove(listener)
    }

    fun update(updater: StateUpdater<T>) {
        value = updater(_value)
    }

    fun updateIf(condition: (T) -> Boolean, updater: StateUpdater<T>) {
        if (condition(_value)) {
            update(updater)
        }
    }
}

// Estado derivado usando lambdas
@Composable
fun <T, R> State<T>.derivedState(transform: (T) -> R): State<R> {
    return remember { derivedStateOf { transform(value) } }
}

// Estado compuesto para múltiples valores
@Composable
fun <T1, T2, R> combineStates(
    state1: State<T1>,
    state2: State<T2>,
    combiner: (T1, T2) -> R
): State<R> {
    return remember { derivedStateOf { combiner(state1.value, state2.value) } }
}

@Composable
fun <T1, T2, T3, R> combineStates(
    state1: State<T1>,
    state2: State<T2>,
    state3: State<T3>,
    combiner: (T1, T2, T3) -> R
): State<R> {
    return remember { derivedStateOf { combiner(state1.value, state2.value, state3.value) } }
}

// Estado de formulario con validación
data class FormField<T>(
    val value: T,
    val error: String? = null,
    val isValid: Boolean = error == null
)

class FormState {
    private val fields = mutableMapOf<String, MutableState<FormField<Any>>>()
    private val validators = mutableMapOf<String, ValidationRule<Any>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getField(name: String): State<FormField<T>>? {
        return fields[name] as? State<FormField<T>>
    }

    fun <T> setField(
        name: String,
        value: T,
        validator: ValidationRule<T>? = null
    ) {
        val field = FormField(value)
        fields[name] = mutableStateOf(field as FormField<Any>)
        validator?.let { validators[name] = it as ValidationRule<Any> }
        validateField(name)
    }

    fun <T> updateField(name: String, updater: StateUpdater<T>) {
        val currentField = fields[name]?.value as? FormField<T> ?: return
        val newValue = updater(currentField.value)
        val newField = currentField.copy(value = newValue)
        fields[name]?.value = newField as FormField<Any>
        validateField(name)
    }

    private fun validateField(name: String) {
        val field = fields[name]?.value ?: return
        val validator = validators[name]

        if (validator != null) {
            val result = validator(field.value)
            val updatedField = field.copy(
                error = if (result.isValid) null else result.errorMessage,
                isValid = result.isValid
            )
            fields[name]?.value = updatedField
        }
    }

    fun validateAll(): Boolean {
        fields.keys.forEach { validateField(it) }
        return fields.values.all { it.value.isValid }
    }

    val isValid: Boolean
        get() = fields.values.all { it.value.isValid }
}

// Composable para formulario reactivo
@Composable
fun rememberFormState(): FormState {
    return remember { FormState() }
}

// Utilidades para efectos con lambdas
object EffectUtils {

    // LaunchedEffect con condicional
    @Composable
    fun ConditionalEffect(
        condition: Boolean,
        key: Any? = null,
        effect: suspend () -> Unit
    ) {
        if (condition) {
            LaunchedEffect(key) {
                effect()
            }
        }
    }

    // DisposableEffect con cleanup personalizado
    @Composable
    fun DisposableEffectWithCleanup(
        key: Any?,
        onCreate: () -> Unit = {},
        onDispose: () -> Unit = {}
    ) {
        DisposableEffect(key) {
            onCreate()
            onDispose { onDispose() }
        }
    }
}

// Estado de carga con resultado
sealed class LoadingState<out T> {
    object Idle : LoadingState<Nothing>()
    object Loading : LoadingState<Nothing>()
    data class Success<T>(val data: T) : LoadingState<T>()
    data class Error(val message: String) : LoadingState<Nothing>()
}

// Composable para manejo de estado de carga
@Composable
fun <T> rememberAsyncState(
    operation: suspend () -> T
): MutableState<LoadingState<T>> {
    val state = remember { mutableStateOf<LoadingState<T>>(LoadingState.Idle) }

    LaunchedEffect(Unit) {
        state.value = LoadingState.Loading
        try {
            val result = operation()
            state.value = LoadingState.Success(result)
        } catch (e: Exception) {
            state.value = LoadingState.Error(e.message ?: "Error desconocido")
        }
    }

    return state
}

// Extensiones para LoadingState
inline fun <T> LoadingState<T>.onSuccess(action: (T) -> Unit): LoadingState<T> {
    if (this is LoadingState.Success) action(data)
    return this
}

inline fun <T> LoadingState<T>.onError(action: (String) -> Unit): LoadingState<T> {
    if (this is LoadingState.Error) action(message)
    return this
}

inline fun <T> LoadingState<T>.onLoading(action: () -> Unit): LoadingState<T> {
    if (this is LoadingState.Loading) action()
    return this
}