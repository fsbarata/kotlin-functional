package com.github.fsbarata.functional.data.complex

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.Traversable
import kotlin.jvm.JvmName
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Suppress("OVERRIDE_BY_INLINE")
data class Complex<A>(
	val real: A,
	val imag: A,
):
	Monad<ComplexContext, A>,
	Traversable<ComplexContext, A> {
	override val scope = Complex

	override inline fun <B> map(f: (A) -> B) = Complex(f(real), f(imag))
	@Suppress("NOTHING_TO_INLINE")
	override inline fun <B> ap(ff: Functor<ComplexContext, (A) -> B>): Complex<B> =
		ff.asComplex.let { Complex(it.real(real), it.imag(imag)) }

	override inline fun <B, R> lift2(fb: Functor<ComplexContext, B>, f: (A, B) -> R): Complex<R> {
		val other = fb.asComplex
		return Complex(
			f(real, other.real),
			f(imag, other.imag)
		)
	}

	inline fun <B> flatMap(f: (A) -> Complex<B>): Complex<B> = Complex(
		f(real).real,
		f(imag).imag
	)

	override fun <B> bind(f: (A) -> Context<ComplexContext, B>) = flatMap { f(it).asComplex }

	override fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		accumulator(accumulator(initialValue, real), imag)

	override fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		accumulator(real, accumulator(imag, initialValue))

	override fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Functor<F, B>,
	): Functor<F, Complex<B>> = appScope.lift2(f(real), f(imag), ::Complex)


	companion object: Monad.Scope<ComplexContext>, Traversable.Scope<ComplexContext> {
		override fun <A> just(a: A) = Complex(a, a)

		fun fromPolar(radius: Double = 1.0, theta: Double) = Complex(
			radius * cos(theta),
			radius * sin(theta)
		)
	}
}

typealias ComplexContext = Complex<*>

val <A> Context<ComplexContext, A>.asComplex get() = this as Complex<A>

@JvmName("conjugatei")
fun Complex<Int>.conjugate() = Complex(real, -imag)
@JvmName("conjugatel")
fun Complex<Long>.conjugate() = Complex(real, -imag)
@JvmName("conjugatef")
fun Complex<Float>.conjugate() = Complex(real, -imag)
fun Complex<Double>.conjugate() = Complex(real, -imag)

@JvmName("manituden")
fun Complex<out Number>.magnitude(): Double = map { it.toDouble() }.magnitude()
fun Complex<Double>.magnitude(): Double = hypot(real, imag)

@JvmName("phasen")
fun Complex<out Number>.phase(): Double = map { it.toDouble() }.phase()
fun Complex<Double>.phase(): Double =
	if (real == 0.0 && imag == 0.0) 0.0
	else atan2(imag, real)
