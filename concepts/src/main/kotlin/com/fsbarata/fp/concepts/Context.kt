package com.fsbarata.fp.concepts

/**
 * A context C of A
 */
interface Context<C: Context<C, *>, out A>
