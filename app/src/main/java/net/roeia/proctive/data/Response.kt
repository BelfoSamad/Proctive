package net.roeia.proctive.data

data class Response<out T>(val status: Status, val data: T?, val errorCode: Int?) {
    companion object {
        fun <T> success(data: T?): Response<T> {
            return Response(Status.SUCCESS, data, null)
        }

        fun <T> error(errorCode: Int, data: T?): Response<T> {
            return Response(Status.ERROR, data, errorCode)
        }

        fun <T> loading(data: T?): Response<T> {
            return Response(Status.LOADING, data, null)
        }
    }
}
