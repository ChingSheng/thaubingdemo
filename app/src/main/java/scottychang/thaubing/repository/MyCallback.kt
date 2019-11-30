package scottychang.thaubing.repository

interface MyCallback<T> {
    fun onSuccess(data : T)
    fun onFailure(exception: Exception)
}