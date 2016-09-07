package io.github.plastix.kotlinboilerplate.util

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.functions.Function
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * This rule overrides schedulers for RxJava and RxAndroid to ensure that subscriptions
 * always subscribeOn and observeOn Schedulers.immediate().
 * Warning, this rule will reset RxAndroidPlugins and RxJavaPlugins before and after each test so
 * if the application code uses RxJava plugins this may affect the behaviour of the testing method.
 *
 *
 * This code is adapted from Ribot's Android Boilerplate (Apache 2 license)
 * https://github.com/ribot/android-boilerplate
 */
class RxSchedulersOverrideRule : TestRule {

    var testSchedulerHandler = Function<Scheduler, Scheduler> { TestScheduler() }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                RxAndroidPlugins.reset()
                RxAndroidPlugins.setMainThreadSchedulerHandler(testSchedulerHandler)

                RxJavaPlugins.reset()
                RxJavaPlugins.setIoSchedulerHandler(testSchedulerHandler)
                RxJavaPlugins.setComputationSchedulerHandler(testSchedulerHandler)
                RxJavaPlugins.setNewThreadSchedulerHandler(testSchedulerHandler)
                RxJavaPlugins.setSingleSchedulerHandler(testSchedulerHandler)

                base.evaluate()

                RxAndroidPlugins.reset()
                RxJavaPlugins.reset()
            }
        }
    }
}