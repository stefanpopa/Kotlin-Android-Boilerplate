package io.github.plastix.kotlinboilerplate.ui.base.rx.delivery

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class DeliverReplayTest {

    companion object {
        val TIME_DELAY_MS = 5000L
    }

    lateinit var testScheduler: TestScheduler
    lateinit var testSubscriber: TestObserver<Int>

    @Before
    fun setUp() {
        testScheduler = TestScheduler()
        testSubscriber = TestObserver.create<Int>()
    }

    @Test
    fun emitsSingleItemWhenViewIsAttached() {
        val view = Observable.just(true)
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0)
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValue(0)
        testSubscriber.assertComplete()
    }

    @Test
    fun emitsTwoItemsWhenViewIsAttached() {
        val view = Observable.just(true)
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0, 1)
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValues(0, 1)
        testSubscriber.assertComplete()
    }

    @Test
    fun emitsThreeItemsWhenViewIsAttached() {
        val view = Observable.just(true)
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0, 1, 2)
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValues(0, 1, 2)
        testSubscriber.assertComplete()
    }

    @Test
    fun noEmissionSingleItemWhenViewIsDetached() {
        val view = Observable.just(false)
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0)
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.assertNotComplete()
        testSubscriber.assertNoValues()
    }

    @Test
    fun noEmissionTwoItemsWhenViewIsDetached() {
        val view = Observable.just(false)
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0, 1)
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.assertNotComplete()
        testSubscriber.assertNoValues()
    }

    @Test
    fun noEmissionThreeItemsWhenViewIsDetached() {
        val view = Observable.just(false)
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0, 1, 2)
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.assertNotComplete()
        testSubscriber.assertNoValues()
    }

    @Test
    fun noEmissionSingleItemWhenViewIsNeverAttached() {
        val view = Observable.never<Boolean>()
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0)
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.assertNotComplete()
        testSubscriber.assertNoValues()
    }

    @Test
    fun noEmissionTwoItemsWhenViewIsNeverAttached() {
        val view = Observable.never<Boolean>()
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0, 1)
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.assertNotComplete()
        testSubscriber.assertNoValues()
    }

    @Test
    fun noEmissionThreeItemsWhenViewIsNeverAttached() {
        val view = Observable.never<Boolean>()
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0, 1, 2)
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.assertNotComplete()
        testSubscriber.assertNoValues()
    }

    @Test
    fun emitsSingleItemWhenViewReattaches() {
        val view = Observable.just(true)
                .delay(TIME_DELAY_MS, TimeUnit.MILLISECONDS, testScheduler)
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0)
                .compose(transformer)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)

        testSubscriber.assertNoValues()
        testSubscriber.assertNotComplete()
        testScheduler.advanceTimeBy(TIME_DELAY_MS, TimeUnit.MILLISECONDS)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValue(0)
        testSubscriber.assertComplete()
    }

    @Test
    fun emitsTwoItemsWhenViewReattaches() {
        val view = Observable.just(true)
                .delay(TIME_DELAY_MS, TimeUnit.MILLISECONDS, testScheduler)
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0, 1)
                .compose(transformer)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)

        testSubscriber.assertNoValues()
        testSubscriber.assertNotComplete()
        testScheduler.advanceTimeBy(TIME_DELAY_MS, TimeUnit.MILLISECONDS)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValues(0, 1)
        testSubscriber.assertComplete()
    }

    @Test
    fun emitsThreeItemsWhenViewReattaches() {
        val view = Observable.just(true)
                .delay(TIME_DELAY_MS, TimeUnit.MILLISECONDS, testScheduler)
        val transformer = DeliverReplay<Int>(view)

        Observable.just(0, 1, 2)
                .compose(transformer)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)

        testSubscriber.assertNoValues()
        testSubscriber.assertNotComplete()
        testScheduler.advanceTimeBy(TIME_DELAY_MS, TimeUnit.MILLISECONDS)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValues(0, 1, 2)
        testSubscriber.assertComplete()
    }

    @Test
    fun emitsErrorWhenViewIsAttached() {
        val view = Observable.just(true)
        val transformer = DeliverReplay<Int>(view)

        Observable.error<Int>(Throwable())
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertError(Throwable::class.java)
    }

    @Test
    fun noErrorEmittedWhenViewIsDetached() {
        val view = Observable.just(false)
        val transformer = DeliverReplay<Int>(view)

        Observable.error<Int>(Throwable())
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
    }

    @Test
    fun noErrorEmittedWhenViewNeverAttached() {
        val view = Observable.never<Boolean>()
        val transformer = DeliverReplay<Int>(view)

        Observable.error<Int>(Throwable())
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
    }

    @Test
    fun emitErrorWhenViewReattaches() {
        val view = Observable.just(true)
                .delay(TIME_DELAY_MS, TimeUnit.MILLISECONDS, testScheduler)
        val transformer = DeliverLatest<Int>(view)

        Observable.error<Int>(Throwable())
                .compose(transformer)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testScheduler.advanceTimeBy(TIME_DELAY_MS, TimeUnit.MILLISECONDS)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertError(Throwable::class.java)
    }

    @Test
    fun emitErrorAfterItemsWhenViewReattaches() {
        val view = Observable.just(true)
                .delay(TIME_DELAY_MS, TimeUnit.MILLISECONDS, testScheduler)
        val transformer = DeliverReplay<Int>(view)

        Observable.just("0", "1", "2", "error")
                .map { it.toInt() }
                .compose(transformer)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertNoValues()
        testScheduler.advanceTimeBy(TIME_DELAY_MS, TimeUnit.MILLISECONDS)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValues(0, 1, 2)
        testSubscriber.assertError(Throwable::class.java)
    }

    @Test
    fun emitErrorBeforeItemsWhenViewReattaches() {
        val view = Observable.just(true)
                .delay(TIME_DELAY_MS, TimeUnit.MILLISECONDS, testScheduler)
        val transformer = DeliverReplay<Int>(view)

        Observable.just("error", "0", "1", "2")
                .map { it.toInt() }
                .compose(transformer)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertNoValues()
        testScheduler.advanceTimeBy(TIME_DELAY_MS, TimeUnit.MILLISECONDS)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoValues()
        testSubscriber.assertError(Throwable::class.java)
    }

    @Test
    fun emitsErrorInBetweenItemsWhenViewReattaches() {
        val view = Observable.just(true)
                .delay(TIME_DELAY_MS, TimeUnit.MILLISECONDS, testScheduler)
        val transformer = DeliverReplay<Int>(view)

        Observable.just("0", "error", "1", "2")
                .map { it.toInt() }
                .compose(transformer)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testScheduler.advanceTimeBy(TIME_DELAY_MS, TimeUnit.MILLISECONDS)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValue(0)
        testSubscriber.assertError(Throwable::class.java)
    }

    @Test
    fun emitsCompleteWhenViewIsAttached() {
        val view = Observable.just(true)
        val transformer = DeliverReplay<Int>(view)

        Observable.empty<Int>()
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertNoValues()
        testSubscriber.assertComplete()
    }

    @Test
    fun noCompleteEmissionWhenViewIsDetached() {
        val view = Observable.just(false)
        val transformer = DeliverReplay<Int>(view)

        Observable.empty<Int>()
                .compose(transformer)
                .subscribe(testSubscriber)

        testSubscriber.assertNotComplete()
        testSubscriber.assertNoValues()
    }

    @Test
    fun emitsCompleteWhenViewReattaches() {
        val view = Observable.just(true)
                .delay(TIME_DELAY_MS, TimeUnit.MILLISECONDS, testScheduler)
        val transformer = DeliverReplay<Int>(view)

        Observable.empty<Int>()
                .compose(transformer)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)

        testSubscriber.assertNotComplete()
        testScheduler.advanceTimeBy(TIME_DELAY_MS, TimeUnit.MILLISECONDS)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertComplete()
    }
}