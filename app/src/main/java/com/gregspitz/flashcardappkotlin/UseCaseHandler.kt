package com.gregspitz.flashcardappkotlin

/**
 * Runs {@link UseCase}s using a {@link UseCaseScheduler}
 */
class UseCaseHandler(private val mUseCaseScheduler: UseCaseScheduler) {

    private fun runnable(f: () -> Unit): Runnable = Runnable { f() }

    fun <T : UseCase.RequestValues, R : UseCase.ResponseValue> execute(
            useCase: UseCase<T, R>, values: T, callback: UseCase.UseCaseCallback<R>) {
        useCase.setRequestValues(values)
        useCase.setUseCaseCallback(UiCallbackWrapper(callback, this))
        mUseCaseScheduler.execute( runnable { useCase.run() } )
    }

    fun <V : UseCase.ResponseValue> notifyResponse(
            response: V, useCaseCallback: UseCase.UseCaseCallback<V>) {
        mUseCaseScheduler.notifyResponse(response, useCaseCallback)
    }

    fun <V : UseCase.ResponseValue> notifyError(useCaseCallback: UseCase.UseCaseCallback<V>) {
        mUseCaseScheduler.onError(useCaseCallback)
    }

    class UiCallbackWrapper<V : UseCase.ResponseValue>(
            private val mCallback: UseCase.UseCaseCallback<V>,
            private val mUseCaseHandler: UseCaseHandler)
        : UseCase.UseCaseCallback<V> {
        override fun onSuccess(response: V) {
            mUseCaseHandler.notifyResponse(response, mCallback)
        }

        override fun onError() {
            mUseCaseHandler.notifyError(mCallback)
        }

    }
}
