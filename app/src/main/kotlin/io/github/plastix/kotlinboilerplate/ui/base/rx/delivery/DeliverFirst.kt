package io.github.plastix.kotlinboilerplate.ui.base.rx.delivery

import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

/**
 * Transformer which couples data Observable with view Observable.
 *
 * Adapted from https://github.com/alapshin/arctor (MIT License)
 */
class DeliverFirst<T>(private val view: Observable<Boolean>) : ObservableTransformer<T, T> {

    override fun apply(observable: Observable<T>): ObservableSource<T> {
        // This is nearly identical to DeliverLatest except we call take(1) on the data observable first
        return Observable.combineLatest(
                view,
                // Emit only first value from data Observable
                observable.take(1)
                        // Use materialize to propagate onError events from data Observable
                        // only after view Observable emits true
                        .materialize()
                        .delay { notification ->
                            // Delay completed notifications until the view reattaches
                            if (notification.isOnComplete) {
                                view.filter { it }.first()
                            } else {
                                // Pass all other events downstream immediately
                                // They will be "cached" by combineLatest
                                Observable.empty()
                            }
                        },
                BiFunction<Boolean, Notification<T>, Any> { isViewAttached, notification -> if (isViewAttached) notification else Unit }
        )
                .filter { it != Unit }
                .dematerialize()
    }

}
