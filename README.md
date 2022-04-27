# kotlin-functional

[![](https://jitpack.io/v/fsbarata/kotlin-functional.svg)](https://jitpack.io/#fsbarata/kotlin-functional)

## Purpose

This library provides a set of concepts and implementations of patterns commonly used in functional programming.

## Background

With the increasing of the multi-threaded/core capabilities on the devices,
it is not feasible to continue using stateful, assumption-based and side-effect styles of programming.

Kotlin provides a very limited range of functional structures and patterns.

## What to use it for

This library is an attempt to complement the existing features of kotlin,
as well as providing new types that are useful in functional programming.

## Common Data Types

### Optional
Encapsulates a value that may or may not be present.
It abstracts the nullability of a value.

Most commonly used with the RxJava framework, where nulls are forbidden.

Example 1:
```
Observable.just(1, 2, 3, 5, 6, 7)
	.map { if (it % 2 == 0) Optional.empty() else Optional.just(it) }
	.filterNotNone() // returns an Observable with items [1, 3, 5, 7]
```

Example 2:
```
Observable.just(1, 2, 3, 5, 6, 7)
	.map { if (it % 2 == 0) Optional.empty() else Optional.just(it) }
	.mapNotNone(liftOpt { it + 1 }) // returns an Observable with items [2, 4, 6, 8]
```

### Either

A data structure that can only carry one of two possibilities.

Most commonly used to represent Correct (Right) or Error (Left) result of one computation.

### NonEmpty List/Set

A list/set that must have at least one element.
As a result, multiple operations are guaranteed to yield more predictable results,
such as `first`, `last`, `max`, `reduce`, etc.

Most commonly used to represent Lists that have been tested for emptiness.

### Validation

Akin to Either, Validation can only carry a Success or a Failure result.
While Either is used to shortcut errors, Validation is used to accumulate them.

Most commonly used to accumulate/compose errors from multiple computations.


## Other uses

You will find in this library other concepts such as Monad, Monoid and Foldable.


## Dependency
### Gradle

```
repositories {
	maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "com.github.fsbarata.kotlin-functional:base:$kfVersion"
}
```

