package com.github.fsbarata.functional.control

/**
 * A context C of A
 *
 * This class is used to represent a generic C with argument A
 * Eg.: List<A> can be represented by Context<List<*>, A>
 */
interface Context<C, out A>
