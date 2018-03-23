package com.gregspitz.flashcardappkotlin

import android.os.Handler
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Executes asynchronous tasks using a {@link ThreadPoolExecutor}.
 * <p>
 * See also {@link Executors} for a list of factory methods to create common
 * {@link java.util.concurrent.ExecutorService}s for different scenarios.
 */
class UseCaseThreadPoolScheduler : UseCaseScheduler {

    val poolSize = 2
    val maxPoolSize = 4
    val timeout = 30L

    private var threadPoolExecutor = ThreadPoolExecutor(poolSize, maxPoolSize, timeout,
            TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(poolSize))

    private val handler = Handler()

    override fun execute(runnable: Runnable) {
        threadPoolExecutor.execute(runnable)
    }

    override fun <V : UseCase.ResponseValue> notifyResponse(
            response: V, useCaseCallback: UseCase.UseCaseCallback<V>) {
        handler.post({useCaseCallback.onSuccess(response)})
    }

    override fun <V : UseCase.ResponseValue> onError(useCaseCallback: UseCase.UseCaseCallback<V>) {
        handler.post({useCaseCallback.onError()})
    }

}
