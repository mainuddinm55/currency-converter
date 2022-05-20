package info.learncoding.currencyconverter.data.model

data class ApiError(val type: ErrorType, val message: String)
enum class ErrorType {
    NOT_FOUND,
    VALIDATION_ERROR,
    INTERNAL_SERVER_ERROR,
    UNAUTHORIZED,
    FORBIDDEN,
    FAILURE,
    NETWORK
}
