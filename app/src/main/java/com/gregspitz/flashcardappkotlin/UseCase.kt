package com.gregspitz.flashcardappkotlin

/**
 * se cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <P> the response type
 */
abstract class UseCase<Q : UseCase.RequestValues, P : UseCase.ResponseValue> {

    private lateinit var mRequestValues: Q

    private lateinit var mUseCaseCallback: UseCaseCallback<P>

    fun getRequestValues(): Q {
        return mRequestValues
    }

    fun setRequestValues(requestValues: Q) {
        mRequestValues = requestValues
    }

    fun getUseCaseCallback(): UseCaseCallback<P> {
        return mUseCaseCallback
    }

    fun setUseCaseCallback(useCaseCallback: UseCaseCallback<P>) {
        mUseCaseCallback = useCaseCallback
    }

    fun run() {
        executeUseCase(mRequestValues)
    }

    protected abstract fun executeUseCase(requestValues: Q)

    /**
     * Data passed to a request
     */
    interface RequestValues

    /**
     * Data received from a request
     */
    interface ResponseValue

    interface UseCaseCallback<R> {
        fun onSuccess(response: R)
        fun onError()
    }
}
