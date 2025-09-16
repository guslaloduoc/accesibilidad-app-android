package com.example.exp1_s2_gustavo_renjifo.utils

/**
 * Utilidades de validación usando funciones de orden superior y lambdas
 */

// Tipo alias para funciones de validación
typealias ValidationRule<T> = (T) -> ValidationResult

// Resultado de validación
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

// Clase para manejar múltiples validaciones
class ValidationChain<T> {
    private val rules = mutableListOf<ValidationRule<T>>()

    fun addRule(rule: ValidationRule<T>) = apply {
        rules.add(rule)
    }

    fun validate(value: T): ValidationResult {
        rules.forEach { rule ->
            val result = rule(value)
            if (!result.isValid) {
                return result
            }
        }
        return ValidationResult(true)
    }
}

// Funciones de orden superior para validaciones comunes
object Validators {

    // Validador genérico que toma una lambda como condición
    fun <T> createValidator(
        condition: (T) -> Boolean,
        errorMessage: String
    ): ValidationRule<T> = { value ->
        if (condition(value)) {
            ValidationResult(true)
        } else {
            ValidationResult(false, errorMessage)
        }
    }

    // Validadores específicos usando lambdas
    val emailValidator: ValidationRule<String> = createValidator(
        condition = { email ->
            email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        },
        errorMessage = "Email inválido"
    )

    val passwordValidator: ValidationRule<String> = createValidator(
        condition = { password -> password.length >= 6 },
        errorMessage = "La contraseña debe tener al menos 6 caracteres"
    )

    val nameValidator: ValidationRule<String> = createValidator(
        condition = { name -> name.isNotBlank() && name.length >= 2 },
        errorMessage = "El nombre debe tener al menos 2 caracteres"
    )

    val phoneValidator: ValidationRule<String> = createValidator(
        condition = { phone ->
            phone.isNotBlank() && phone.matches(Regex("^[+]?[0-9]{8,15}$"))
        },
        errorMessage = "Número de teléfono inválido"
    )

    // Función de orden superior para validar formularios completos
    fun <T> validateForm(
        fields: Map<String, T>,
        validators: Map<String, ValidationRule<T>>
    ): Map<String, ValidationResult> {
        return fields.mapValues { (fieldName, value) ->
            validators[fieldName]?.invoke(value) ?: ValidationResult(true)
        }
    }

    // Lambda para filtrar solo errores
    val onlyErrors: (Map<String, ValidationResult>) -> Map<String, String> = { results ->
        results.filterValues { !it.isValid }
            .mapValues { it.value.errorMessage ?: "Error desconocido" }
    }

    // Función de orden superior para crear validadores compuestos
    fun <T> combineValidators(
        vararg validators: ValidationRule<T>
    ): ValidationRule<T> = { value ->
        validators.fold(ValidationResult(true)) { acc, validator ->
            if (!acc.isValid) acc else validator(value)
        }
    }
}

// Extensiones usando lambdas
fun String.isValidEmail(): Boolean = Validators.emailValidator(this).isValid
fun String.isValidPassword(): Boolean = Validators.passwordValidator(this).isValid
fun String.isValidName(): Boolean = Validators.nameValidator(this).isValid

// Función de orden superior para transformar y validar
inline fun <T, R> T.transformAndValidate(
    transform: (T) -> R,
    validator: ValidationRule<R>
): Pair<R, ValidationResult> {
    val transformed = transform(this)
    return transformed to validator(transformed)
}