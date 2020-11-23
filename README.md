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

### Either

A data structure that can only carry one of two possibilities.

Most commonly used to represent Correct (Right) or Error (Left) result of one computation.

### NonEmpty List

A list that must have at least one element.
As a result, multiple operations are guaranteed to yield more predictable results,
such as `first()` or `last()`.

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

