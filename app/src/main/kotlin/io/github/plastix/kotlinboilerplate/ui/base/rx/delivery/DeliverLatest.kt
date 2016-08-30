package io.github.plastix.kotlinboilerplate.ui.base.rx.delivery

import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

/**
 * {@link rx.Observable.Transformer} that couples data and view status
 *
 * If view is attached (latest emitted value from view observable is true) then values from data
 * observable propagates us usual.
 *
 * If view is detached (latest emitted value from view observable is false) then values from data
 * observable propagates using following rules:
 * <ul>
 *     <li>If data observable emits onError then it would be delivered after view is attached</li>
 *     <li>If data observable emits onCompleted then after view is attached last onNext value from
 *     data observable is delivered followed by onCompleted event</li>
 *     <li>If data observable emits multiple values then after view is attached last emitted value
 *     is delivered</li>
 * </ul>
 *
 * Adapted from https://github.com/alapshin/arctor (MIT License)
 */
class DeliverLatest<T>(private val view: Observable<Boolean>) : ObservableTransformer<T, T> {

    override fun apply(observable: Observable<T>): ObservableSource<T> {
        return Observable.combineLatest(
                view,
                // Materialize data Observable to handle onError and onCompleted events when view is detached
                observable.materialize()
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