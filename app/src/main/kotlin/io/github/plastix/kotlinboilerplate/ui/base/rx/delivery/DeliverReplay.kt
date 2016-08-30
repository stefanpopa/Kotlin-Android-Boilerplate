package io.github.plastix.kotlinboilerplate.ui.base.rx.delivery

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.ReplaySubject

/**
 * Adapted from https://github.com/alapshin/arctor (MIT License)
 */
class DeliverReplay<T>(private val view: Observable<Boolean>) : ObservableTransformer<T, T> {

    override fun apply(observable: Observable<T>): Observable<T> {
        val subject = ReplaySubject.create<T>()

        val observer: DisposableObserver<T> = object : DisposableObserver<T>() {
            override fun onNext(value: T) {
                subject.onNext(value)
            }

            override fun onError(e: Throwable) {
                subject.onError(e)
            }

            override fun onComplete() {
                subject.onComplete()
            }
        }

        return view.switchMap {
            flag ->
            if (flag) subject else Observable.never<T>()
        }.doOnCancel {
            observer.dispose()
        }.doOnSubscribe {
            observable.subscribe(observer)
        }
    }
}