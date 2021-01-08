package org.elixir_lang.beam.term

import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import org.elixir_lang.beam.ByteSubarray
import org.elixir_lang.beam.chunk.Chunk.unsignedByte

typealias UnsignedByte = Int
typealias ByteCount = Int

fun unsignedIntToInt(unsignedInt: Long): Int =
        if (unsignedInt > Int.MAX_VALUE) {
            throw IllegalArgumentException("Unsigned int ($unsignedInt) exceeds max int (${Int.MAX_VALUE})")
        } else {
            unsignedInt.toInt()
        }

/**
 * BEAM file uses a special encoding to store simple terms in BEAM file in a space-efficient way. It is different from
 * memory term layout, used by BEAM VM.
 */
sealed class Term {
    companion object {
        fun from(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<Term, ByteCount> {
            var internalOffset = offset

            val (fullTag, fullTagByteCount) = unsignedByte(data[offset])
            internalOffset += fullTagByteCount

            val tag = fullTag.and(0b111)

            // http://beam-wisdoms.clau.se/en/latest/indepth-beam-file.html#beam-term-format
            val (term, termByteCount) = when (tag) {
                0b000 ->
                    indexed(fullTag, data, internalOffset, ::Literal)
                0b001 ->
                    Integer.from(fullTag, data, internalOffset)
                0b010 ->
                    indexed(fullTag, data, internalOffset, ::Atom)
                0b011 ->
                    indexed(fullTag, data, internalOffset, ::XRegister)
                0b100 ->
                    indexed(fullTag, data, internalOffset, ::YRegister)
                0b101 ->
                    indexed(fullTag, data, internalOffset, ::Label)
                0b110 ->
                    Character.from(fullTag, data, internalOffset)
                0b111 -> {
                    val extendedTag = fullTag.ushr(3).and(0b1_1111)

                    /* In OTP 20 the Floats are encoded as literals, and every other extended code is shifted, i.e. List
                       becomes 1 (0b10111), Float register becomes 2 (0b100111), alloc list becomes 3 (0b110111) and
                       literal becomes 4 (0b1000111). */
                    if (literalFloat) {
                        when (extendedTag) {
                            0b0010 ->
                                List.from(data, internalOffset, literalFloat)
                            0b0100 ->
                                FloatingPointRegister.from(data, internalOffset, literalFloat)
                            0b0110 ->
                                AllocationList.from(data, internalOffset, literalFloat)
                            0b1000 ->
                                ExtendedLiteral.from(data, internalOffset, literalFloat)
                            else ->
                                throw IllegalArgumentException(
                                        "Extended tag ($extendedTag) is not properly shifted and masked"
                                )
                        }
                    } else {
                        when (extendedTag) {
                            0b0010 ->
                                Float.from(data, internalOffset)
                            0b0100 ->
                                List.from(data, internalOffset, literalFloat)
                            0b0110 ->
                                FloatingPointRegister.from(data, internalOffset, literalFloat)
                            0b1000 ->
                                AllocationList.from(data, internalOffset, literalFloat)
                            0b1010 ->
                                ExtendedLiteral.from(data, internalOffset, literalFloat)
                            else ->
                                throw IllegalArgumentException(
                                        "Extended tag ($extendedTag) is not properly shifted and masked"
                                )
                        }
                    }
                }
                else -> throw IllegalArgumentException("tag ($tag) is not properly shifted and masked")
            }
            internalOffset += termByteCount

            return Pair(term, internalOffset - offset)
        }
    }
}

typealias Index = Int

inline fun <reified T> indexed(
        fullTag: UnsignedByte,
        data: ByteArray,
        offset: Int,
        transformer: (index: Index) -> T
): Pair<T, ByteCount> {
    val (index, byteCount) = index(fullTag, data, offset)
    return Pair(transformer.invoke(index), byteCount)
}

fun index(fullTag: UnsignedByte, data: ByteArray, offset: Int): Pair<Index, ByteCount> =
        toIndex(value(fullTag, data, offset))

private fun toIndex(valueByteCount: Pair<Any, ByteCount>): Pair<Index, ByteCount> {
    val (value, byteCount) = valueByteCount

    val index = when (value) {
        is Int -> value
        is ByteSubarray -> unsignedIntToInt(value.toUnsignedInt())
        else -> throw IllegalArgumentException("Only Int and ByteSubarray expected")
    }

    return Pair(index, byteCount)
}

fun value(fullTag: UnsignedByte, data: ByteArray, offset: Int): Pair<Any, ByteCount> {
    var internalOffset = offset

    val bit3 = fullTag.ushr(3).and(0b1)

    return if (bit3 == 0) {
        val value = fullTag.ushr(4)

        Pair(value, internalOffset - offset)
    } else {
        val bit4 = fullTag.ushr(4).and(0b1)
        val bits7to5 = fullTag.and(0b1110_0000).shr(5)

        val value: Any = when {
            bit4 == 0 -> {
                val (lowByte, lowByteCount) = unsignedByte(data[internalOffset])
                internalOffset += lowByteCount

                bits7to5.shl(8).or(lowByte)
            }
            bits7to5 == 0b111 -> {
                val (byteCount, byteCountByteCount) = unsignedByte(data[internalOffset])
                internalOffset += byteCountByteCount

                val value = ByteSubarray(data, internalOffset, byteCount)
                internalOffset += byteCount

                value
            }
            else -> {
                val byteCount = bits7to5 + 2

                val value = ByteSubarray(data, internalOffset, byteCount)
                internalOffset += byteCount

                value
            }
        }

        Pair(value, internalOffset - offset)
    }
}

class Literal(val index: Index) : Term()

class Integer(val long: Long) : Term() {
    companion object {
        fun from(fullTag: UnsignedByte, data: ByteArray, offset: Int): Pair<Integer, ByteCount> {
            val (value, byteCount) = value(fullTag, data, offset)

            val long: Long = when (value) {
                is Int -> value.toLong()
                is ByteSubarray -> value.toLong()
                else -> throw IllegalArgumentException("Don't know how to convert value (${value.javaClass} $value)")
            }

            return Pair(Integer(long), byteCount)
        }
    }
}

class Atom(val index: Index) : Term()
class Label(val index: Index) : Term()
class XRegister(val index: Index) : Term()
class YRegister(val index: Index) : Term()

class Character(val codePoint: Int) : Term() {
    companion object {
        fun codePoint(byteSubarray: ByteSubarray): Int = unsignedIntToInt(byteSubarray.toUnsignedInt())

        fun from(fullTag: UnsignedByte, data: ByteArray, offset: Int): Pair<Character, ByteCount> {
            val (value, byteCount) = value(fullTag, data, offset)
            val codePoint = when (value) {
                is Int -> value
                is ByteSubarray -> codePoint(value)
                else -> throw IllegalArgumentException("Don't know how to convert value (${value.javaClass} $value)")
            }

            return Pair(Character(codePoint), byteCount)
        }
    }
}

class Float : Term() {
    companion object {
        fun from(data: ByteArray, offset: Int): Pair<Float, ByteCount> {
            TODO("decode $data at offset $offset as Float")
        }
    }
}

private fun size(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<Int, Int> {
    var internalOffset = offset

    val (sizeTerm, sizeTermByteCount) = Term.from(data, internalOffset, literalFloat)
    internalOffset += sizeTermByteCount

    val size = when (sizeTerm) {
        is Literal -> sizeTerm.index
        else -> throw IllegalArgumentException(
                "Don't know how to convert sizeTerm (${sizeTerm.javaClass} $sizeTerm)"
        )
    }
    return Pair(size, internalOffset - offset)
}

class List(val elements: kotlin.collections.List<Term>) : Term() {
    companion object {
        fun from(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<List, ByteCount> {
            var internalOffset = offset

            val (size, sizeByteCount) = size(data, internalOffset, literalFloat)
            internalOffset += sizeByteCount

            val elements = mutableListOf<Term>()

            repeat(size) {
                val (element, elementByteCount) = Term.from(data, internalOffset, literalFloat)
                internalOffset += elementByteCount

                elements.add(element)
            }

            return Pair(List(elements), internalOffset - offset)
        }
    }
}

class FloatingPointRegister(val index: Index) : Term() {
    companion object {
        fun from(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<FloatingPointRegister, ByteCount> {
            var internalOffset = offset

            val (indexTerm, indexTermByteCount) = Term.from(data, offset, literalFloat)
            internalOffset += indexTermByteCount

            val index = when (indexTerm) {
                is Literal -> indexTerm.index
                else ->
                    throw IllegalArgumentException("Expecting indexTerm (${indexTerm.javaClass} $indexTerm) to be a Literal")
            }

            return Pair(FloatingPointRegister(index), internalOffset - offset)
        }
    }
}

class AllocationList(val allocationList: kotlin.collections.List<Allocation>) : Term() {
    data class Allocation(val type: Type, val value: Int) {
        override fun toString(): String =
                when (type) {
                    Type.WORDS -> "words($value)"
                    Type.FLOATS -> "floats($value)"
                    Type.LITERAL -> "literal($value)"
                }

        companion object {
            fun from(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<Allocation, ByteCount> {
                var internalOffset = offset

                // https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/beam_disasm.erl#L566
                val (type, typeByteCount) = type(data, internalOffset, literalFloat)
                internalOffset += typeByteCount

                val (value, valueByteCount) = value(data, internalOffset, literalFloat)
                internalOffset += valueByteCount

                return Pair(Allocation(type, value), internalOffset - offset)
            }

            private fun type(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<Type, ByteCount> {
                var internalOffset = offset

                // https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/beam_disasm.erl#L566
                val (typeTerm, typeTermByteCount) = Term.from(data, internalOffset, literalFloat)
                internalOffset += typeTermByteCount

                val type = when (typeTerm) {
                    is Literal ->
                        // https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/beam_disasm.erl#L568-L572
                        typeByNumber[typeTerm.index] ?: throw IllegalArgumentException(
                                "typeTerm Literal index (${typeTerm.index}) is not a recognized Type number"
                        )
                    else -> throw IllegalArgumentException(
                            "Expecting typeTerm (${typeTerm.javaClass} $typeTerm) to be a Literal"
                    )
                }

                return Pair(type, internalOffset - offset)
            }

            // https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/beam_disasm.erl#L567
            private fun value(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<Int, ByteCount> {
                var internalOffset = offset

                val (valueTerm, valueByteCount) = Term.from(data, internalOffset, literalFloat)
                internalOffset += valueByteCount

                val value = when (valueTerm) {
                    is Literal -> valueTerm.index
                    else ->
                        /* https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/beam_disasm.erl#L567
                           `{{u,_},Bs}` */
                        throw IllegalArgumentException(
                                "Expecting valueTerm (${valueTerm.javaClass} $valueTerm) to be a Literal"
                        )
                }

                return Pair(value, internalOffset - offset)
            }
        }
    }

    // https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/beam_disasm.erl#L569-L571
    enum class Type(val number: Int) {
        WORDS(0),
        FLOATS(1),
        LITERAL(2)
    }

    companion object {
        private val typeByNumber = Type.values().associateBy(Type::number)

        // https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/beam_disasm.erl#L559-L573
        fun from(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<AllocationList, ByteCount> {
            var internalOffset = offset

            // https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/beam_disasm.erl#L560
            val (size, sizeByteCount) = size(data, internalOffset, literalFloat)
            internalOffset += sizeByteCount

            val allocations = mutableListOf<Allocation>()

            repeat(size) {
                val (allocation, allocationByteCount) = Allocation.from(data, internalOffset, literalFloat)
                allocations.add(allocation)
                internalOffset += allocationByteCount
            }

            return Pair(AllocationList(allocations), internalOffset - offset)
        }
    }
}

class ExtendedLiteral: Term() {
    companion object {
        fun from(data: ByteArray, offset: Int, literalFloat: Boolean): Pair<Literal, ByteCount> {
            var internalOffset = offset

            val (term, termByteCount) = Term.from(data, internalOffset, literalFloat)
            internalOffset += termByteCount

            val index = when (term) {
                is Literal -> term.index
                else -> throw IllegalArgumentException(
                        "Extended literals are expected to use normal literals as argument (${term.javaClass} $term)"
                )
            }

            return Pair(Literal(index), internalOffset - offset)
        }
    }
}
