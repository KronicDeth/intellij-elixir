package org.elixir_lang.beam.chunk.code.operation

import org.elixir_lang.beam.chunk.Code.Options
import org.elixir_lang.beam.chunk.Code.Options.Companion.UNAMBIGUOUS
import org.elixir_lang.beam.chunk.Code.Options.Inline
import org.elixir_lang.beam.chunk.code.operation.code.*

/**
 * https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/genop.tab
 */
enum class Code(val number: Int, val function: String, val arguments: Array<Argument> = arrayOf()) {
    // @spec label Lbl
    // @doc Specify a module local label.
    //      Label gives this code address a name (Lbl) and marks the start of
    //      a basic block.
    LABEL(
            1,
            "label",
            arrayOf(
                    Argument(
                            "label",
                            Options(Inline(integers = true, literals = false), showArgumentNames = false)
                    )
            )
    ),

    // @spec func_info M F A
    // @doc Define a function M:F/A
    FUNC_INFO(
            2,
            "func_info",
            arrayOf(
                    Argument("module", Options(Inline(atoms = true, integers = false))),
                    Argument("function", Options(Inline(atoms = true, integers = false))),
                    ARITY
            )
    ),

    INT_CODE_END(3, "int_code_end"),

    //
    // Function and BIF calls.
    //

    // @spec call Arity Label
    // @doc Call the function at Label.
    //      Save the next instruction as the return address in the CP register.
    CALL(4, "call", arrayOf(ARITY, LABEL_ARGUMENT)),

    // @spec call_last Arity Label Deallocate
    // @doc Deallocate and do a tail recursive call to the function at Label.
    //      Do not update the CP register.
    //      Before the call deallocate Deallocate words of stack.
    CALL_LAST(5, "call_last", arrayOf(ARITY, LABEL_ARGUMENT, DEALLOCATE_WORDS_OF_STACK)),

    // @spec call_only Arity Label
    // @doc Do a tail recursive call to the function at Label.
    //      Do not update the CP register.
    CALL_ONLY(6, "call_only", arrayOf(ARITY, LABEL_ARGUMENT)),

    // @spec call_ext Arity Destination
    // @doc Call the function of arity Arity pointed to by Destination.
    //      Save the next instruction as the return address in the CP register.
    CALL_EXT(7, "call_ext", arrayOf(ARITY, IMPORT)),

    // @spec call_ext_last Arity Destination Deallocate
    // @doc Deallocate and do a tail call to function of arity Arity
    //      pointed to by Destination.
    //      Do not update the CP register.
    //      Deallocate Deallocate words from the stack before the call.
    CALL_EXT_LAST(
            8,
            "call_ext_last",
            arrayOf(
                    ARITY,
                    Argument("destination", Options(Inline(imports = true, integers = true, literals = false))),
                    DEALLOCATE_WORDS_OF_STACK
            )
    ),

    // @spec bif0 Bif Reg
    // @doc Call the bif Bif and store the result in Reg.
    BIF0(9, "bif0", arrayOf(IMPORT, DESTINATION)),

    // @spec bif1 Lbl Bif Arg Reg
    // @doc Call the bif Bif with the argument Arg, and store the result in Reg.
    //      On failure jump to Lbl.
    BIF1(
            10,
            "bif1",
            arrayOf(FAIL_LABEL, IMPORT, Argument("argument"), DESTINATION)
    ),

    // @spec bif2 Lbl Bif Arg1 Arg2 Reg
    // @doc Call the bif Bif with the arguments Arg1 and Arg2,
    //      and store the result in Reg.
    //      On failure jump to Lbl.
    BIF2(
            11,
            "bif2",
            arrayOf(
                    FAIL_LABEL,
                    IMPORT,
                    Argument("argument1", Options(Inline(atoms = true, literals = true))),
                    Argument("argument2", Options(Inline(atoms = true, literals = true))),
                    DESTINATION
            )
    ),

    //
    // Allocating, deallocating and returning.
    //

    // @spec allocate StackNeed Live
    // @doc Allocate space for StackNeed words on the stack. If a GC is needed
    //      during allocation there are Live number of live X registers.
    //      Also save the continuation pointer (CP) on the stack.
    ALLOCATE(12, "allocate", arrayOf(WORDS_OF_STACK, LIVE_X_REGISTER_COUNT)),

    // @spec allocate_heap StackNeed HeapNeed Live
    // @doc Allocate space for StackNeed words on the stack and ensure there is
    //      space for HeapNeed words on the heap. If a GC is needed
    //      save Live number of X registers.
    //      Also save the continuation pointer (CP) on the stack.
    ALLOCATE_HEAP(13, "allocate_heap", arrayOf(WORDS_OF_STACK, WORDS_OF_HEAP, LIVE_X_REGISTER_COUNT)),

    // @spec allocate_zero StackNeed Live
    // @doc Allocate space for StackNeed words on the stack. If a GC is needed
    //      during allocation there are Live number of live X registers.
    //      Clear the new stack words. (By writing NIL.)
    //      Also save the continuation pointer (CP) on the stack.
    ALLOCATE_ZERO(14, "allocate_zero", arrayOf(WORDS_OF_STACK, LIVE_X_REGISTER_COUNT)),

    // @spec allocate_heap_zero StackNeed HeapNeed Live
    // @doc Allocate space for StackNeed words on the stack and HeapNeed words
    //      on the heap. If a GC is needed
    //      during allocation there are Live number of live X registers.
    //      Clear the new stack words. (By writing NIL.)
    //      Also save the continuation pointer (CP) on the stack.
    ALLOCATE_HEAP_ZERO(
            15,
            "allocate_heap_zero",
            arrayOf(WORDS_OF_STACK, WORDS_OF_HEAP, LIVE_X_REGISTER_COUNT)
    ),

    // @spec test_heap HeapNeed Live
    // @doc Ensure there is space for HeapNeed words on the heap. If a GC is needed
    //      save Live number of X registers.
    TEST_HEAP(16, "test_heap", arrayOf(WORDS_OF_HEAP, LIVE_X_REGISTER_COUNT)),

    // @spec init N
    // @doc  Clear the Nth stack word. (By writing NIL.)
    INIT(17, "init", arrayOf(Argument("nth_stack_word"))),

    // @spec deallocate N
    // @doc  Restore the continuation pointer (CP) from the stack and deallocate
    //       N+1 words from the stack (the + 1 is for the CP).
    DEALLOCATE(18, "deallocate", arrayOf(WORDS_OF_STACK)),

    // @spec return
    // @doc  Return to the address in the continuation pointer (CP).
    RETURN(19, "return"),

    //
    // Sending & receiving.
    //
    // @spec send
    // @doc  Send argument in x(1) as a message to the destination process in x(0).
    //       The message in x(1) ends up as the result of the send in x(0).
    SEND(20, "send"),

    // @spec remove_message
    // @doc  Unlink the current message from the message queue and store a
    //       pointer to the message in x(0). Remove any timeout.
    REMOVE_MESSAGE(21, "remove_message"),

    // @spec timeout
    // @doc  Reset the save point of the mailbox and clear the timeout flag.
    TIMEOUT(22, "timeout"),

    // @spec loop_rec Label Source
    // @doc  Loop over the message queue, if it is empty jump to Label.
    LOOP_REC(23, "loop_rec", arrayOf(LABEL_ARGUMENT, SOURCE)),

    // @spec loop_rec_end Label
    // @doc  Advance the save pointer to the next message and jump back to Label.
    LOOP_REC_END(24, "loop_rec_end", arrayOf(LABEL_ARGUMENT)),

    // @spec wait Label
    // @doc  Suspend the processes and set the entry point to the beginning of the
    //       receive loop at Label.
    WAIT(25, "wait", arrayOf(LABEL_ARGUMENT)),

    // @spec wait_timeout Lable Time
    // @doc  Sets up a timeout of Time milliseconds and saves the address of the
    //       following instruction as the entry point if the timeout triggers.
    WAIT_TIMEOUT(
            26,
            "wait_timeout",
            arrayOf(
                    LABEL_ARGUMENT,
                    Argument("milliseconds", Options(Inline(integers = true)))
            )
    ),

    //
    // Arithmetic opcodes.
    //
    M_PLUS(27, "-m_plus", FOUR),
    M_MINUS(28, "-m_minus", FOUR),
    M_TIMES(29, "-m_times", FOUR),
    M_DIV(30, "-m_div", FOUR),
    INT_DIV(31, "-int_div", FOUR),
    INT_REM(32, "-int_rem", FOUR),
    INT_BAND(33, "-int_band", FOUR),
    INT_BOR(34, "-int_bor", FOUR),
    INT_BXOR(35, "-int_bxor", FOUR),
    INT_BSL(36, "-int_bsl", FOUR),
    INT_BSR(37, "-int_bsr", FOUR),
    INT_BNOT(38, "-int_bnot", THREE),

    //
    // Comparision operators.
    //

    // @spec is_lt Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is not less than Arg2.
    IS_LT(39, "is_lt", COMPARISON),

    // @spec is_ge Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is less than Arg2.
    IS_GE(40, "is_ge", COMPARISON),

    // @spec is_eq Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is not (numerically) equal to Arg2.
    IS_EQ(41, "is_eq", COMPARISON),

    // @spec is_ne Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is (numerically) equal to Arg2.
    IS_NE(42, "is_ne", COMPARISON),

    // @spec is_eq_exact Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is not exactly equal to Arg2.
    IS_EQ_EXACT(43, "is_eq_exact", COMPARISON),

    // @spec is_ne_exact Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is exactly equal to Arg2.
    IS_NE_EXACT(44, "is_ne_exact", COMPARISON),

    //
    // Type tests.
    //

    // @spec is_integer Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not an integer.
    IS_INTEGER(45, "is_integer", UNARY),

    // @spec is_float Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a float.
    IS_FLOAT(46, "is_float", UNARY),

    // @spec is_number Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a number.
    IS_NUMBER(47, "is_number", UNARY),

    // @spec is_atom Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not an atom.
    IS_ATOM(48, "is_atom", UNARY),

    // @spec is_pid Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a pid.
    IS_PID(49, "is_pid", UNARY),

    // @spec is_reference Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a reference.
    IS_REFERENCE(50, "is_reference", UNARY),

    // @spec is_port Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a port.
    IS_PORT(51, "is_port", UNARY),

    // @spec is_nil Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not nil.
    IS_NIL(52, "is_nil", UNARY),

    // @spec is_binary Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a binary.
    IS_BINARY(53, "is_binary", UNARY),

    IS_CONSTANT(54, "-is_constant", UNARY),

    // @spec is_list Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a cons or nil.
    IS_LIST(55, "is_list", UNARY),

    // @spec is_nonempty_list Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a cons.
    IS_NONEMPTY_LIST(56, "is_nonempty_list", UNARY),

    // @spec is_tuple Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a tuple.
    IS_TUPLE(57, "is_tuple", UNARY),

    // @spec test_arity Lbl Arg1 Arity
    // @doc Test the arity of (the tuple in) Arg1 and jump
    // to Lbl if it is not equal to Arity.
    TEST_ARITY(58, "test_arity", arrayOf(FAIL_LABEL, TUPLE, ARITY)),

    //
    // Indexing & jumping.
    //

    // @spec select_val Arg FailLabel Destinations
    // @doc Jump to the destination label corresponding to Arg
    //      in the Destinations list, if no arity matches, jump to FailLabel.
    SELECT_VAL(
            59,
            "select_val",
            arrayOf(
                    Argument("argument", Options(Inline(atoms = true))),
                    FAIL_LABEL,
                    Argument("value_to_label", Options(Inline(atoms = true, integers = true, labels = false)))
            )
    ),

    // @spec select_tuple_arity Tuple FailLabel Destinations
    // @doc Check the arity of the tuple Tuple and jump to the corresponding
    //      destination label, if no arity matches, jump to FailLabel.
    SELECT_TUPLE_ARITY(
            60,
            "select_tuple_arity",
            arrayOf(
                    TUPLE,
                    FAIL_LABEL,
                    Argument("arity_to_label", Options(Inline(atoms = true, integers = true, labels = false)))
            )
    ),

    // @spec jump Label
    // @doc Jump to Label.
    JUMP(61, "jump", arrayOf(LABEL_ARGUMENT)),

    //
    // Catch.
    //
    CATCH(62, "catch", arguments(2)),
    CATCH_END(63, "catch_end", arguments(1)),

    //
    // Moving, extracting, modifying.
    //

    // @spec move Source Destination
    // @doc Move the source Source (a literal or a register) to
    //      the destination register Destination.
    MOVE(64, "move", arrayOf(SOURCE, DESTINATION)),

    // @spec get_list  Source Head Tail
    // @doc  Get the head and tail (or car and cdr) parts of a list
    //       (a cons cell) from Source and put them into the registers
    //       Head and Tail.
    GET_LIST(
            65,
            "get_list",
            arrayOf(SOURCE, Argument("head_register"), Argument("tail_register"))
    ),

    // @spec get_tuple_element Source Element Destination
    // @doc  Get element number Element from the tuple in Source and put
    //       it in the destination register Destination.
    GET_TUPLE_ELEMENT(
            66,
            "get_tuple_element",
            arrayOf(SOURCE, Argument("element_number"), DESTINATION)
    ),

    // @spec set_tuple_element NewElement Tuple Position
    // @doc  Update the element at position Position of the tuple Tuple
    //       with the new element NewElement.
    SET_TUPLE_ELEMENT(
            67,
            "set_tuple_element",
            arrayOf(Argument("new_element"), TUPLE, Argument("position"))
    ),

    //
    // Building terms.
    //
    PUT_STRING(68, "-put_string", THREE),
    PUT_LIST(
            69,
            "put_list",
            arrayOf(
                    Argument("head", Options(Inline(integers = false, literals = true))),
                    Argument("tail", Options(Inline(integers = false, literals = true))),
                    DESTINATION
            )
    ),
    PUT_TUPLE(
            70,
            "put_tuple",
            arrayOf(
                    Argument("size", Options(Inline(integers = true, literals = false))),
                    DESTINATION
            )
    ),
    PUT(71, "put", ONE),

    //
    // Raising errors.
    //
    BADMATCH(72, "badmatch", ONE),
    IF_END(73, "if_end"),
    CASE_END(74, "case_end", ONE),

    //
    // 'fun' support.
    //
    // @spec call_fun Arity
    // @doc Call a fun of arity Arity. Assume arguments in
    //      registers x(0) to x(Arity-1) and that the fun is in x(Arity).
    //      Save the next instruction as the return address in the CP register.
    CALL_FUN(75, "call_fun", arrayOf(ARITY)),

    MAKE_FUN(76, "-make_fun", THREE),

    // @spec is_function Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a
    //      function (i.e. fun or closure).
    IS_FUNCTION(77, "is_function", UNARY),

    //
    // Late additions to R5.
    //

    // @spec call_ext_only Arity ImportIndex
    //      Do a tail recursive call to the function at ImportIndex.
    //      Do not update the CP register.
    CALL_EXT_ONLY(78, "call_ext_only", arrayOf(ARITY, IMPORT)),

    //
    // Binary matching (R7).
    //
    BS_START_MATCH(79, "-bs_start_match", TWO),
    BS_GET_INTEGER(80, "-bs_get_integer", FIVE),
    BS_GET_FLOAT(81, "-bs_get_float", FIVE),
    BS_GET_BINARY(82, "-bs_get_binary", FIVE),
    BS_SKIP_BITS(83, "-bs_skip_bits", FOUR),
    BS_TEST_TAIL(84, "-bs_test_tail", TWO),
    BS_SAVE(85, "-bs_save", ONE),
    BS_RESTORE(86, "-bs_restore", ONE),

    //
    // Binary construction (R7A).
    //
    BS_INIT(87, "-bs_init", TWO),
    BS_FINAL(88, "-bs_final", TWO),
    BS_PUT_INTEGER(89, "bs_put_integer", FIVE),
    BS_PUT_BINARY(90, "bs_put_binary", arrayOf(FAIL_LABEL, SIZE, UNIT, FLAGS, SOURCE)),
    BS_PUT_FLOAT(91, "bs_put_float", FIVE),
    BS_PUT_STRING(
            92,
            "bs_put_string",
            arrayOf(
                    Argument("length", Options(Inline(integers = true, literals = false))),
                    Argument("pool_offset", Options(Inline(integers = true, literals = false)))
            )
    ),

    //
    // Binary construction (R7B).
    //
    BS_NEED_BUF(93, "-bs_need_buf", ONE),

    //
    // Floating point arithmetic (R8).
    //
    FCLEARERROR(94, "fclearerror"),
    FCHECKERROR(95, "fcheckerror", ONE),
    FMOVE(96, "fmove", TWO),
    FCONV(97, "fconv", TWO),
    FADD(98, "fadd", FOUR),
    FSUB(99, "fsub", FOUR),
    FMUL(100, "fmul", FOUR),
    FDIV(101, "fdiv", FOUR),
    FNEGATE(102, "fnegate", THREE),

    // New fun construction (R8).
    MAKE_FUN2(
            103,
            "make_fun2",
            arrayOf(
                    Argument(
                            "function",
                            Options(Inline(functions = true, integers = true, literals = false))
                    )
            )
    ),

    // Try/catch/raise (R10B).
    TRY(104, "try", TWO),
    TRY_END(105, "try_end", ONE),
    TRY_CASE(106, "try_case", ONE),
    TRY_CASE_END(107, "try_case_end", ONE),
    RAISE(108, "raise", TWO),

    // New instructions in R10B.
    BS_INIT2(
            109,
            "bs_init2",
            arrayOf(FAIL_LABEL, SIZE, WORDS_OF_STACK, LIVE_X_REGISTER_COUNT, FLAGS, DESTINATION)
    ),
    BS_BITS_TO_BYTES(110, "-bs_bits_to_bytes", THREE),
    BS_ADD(
            111,
            "bs_add",
            arrayOf(
                    FAIL_LABEL,
                    Argument("source1"),
                    Argument("source2"),
                    UNIT,
                    Argument("destination")
            )
    ),
    APPLY(112, "apply", ONE),
    APPLY_LAST(113, "apply_last", TWO),
    // @spec is_boolean Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a Boolean.
    IS_BOOLEAN(114, "is_boolean", UNARY),

    // New instructions in R10B-6.
    // @spec is_function2 Lbl Arg1 Arity
    // @doc Test the type of Arg1 and jump to Lbl if it is not a
    //      function of arity Arity.
    IS_FUNCTION2(115, "is_function2", arrayOf(*UNARY, ARITY)),

    // New bit syntax matching in R11B.

    BS_START_MATCH2(
            116,
            "bs_start_match2",
            arrayOf(FAIL_LABEL, SOURCE, LIVE_X_REGISTER_COUNT, Argument("max"), DESTINATION)
    ),
    BS_GET_INTEGER2(
            117,
            "bs_get_integer2",
            arrayOf(FAIL_LABEL, MATCH_STATE, LIVE_X_REGISTER_COUNT, SIZE, UNIT, FLAGS, DESTINATION)
    ),
    BS_GET_FLOAT2(118, "bs_get_float2", SEVEN),
    BS_GET_BINARY2(119, "bs_get_binary2", SEVEN),
    BS_SKIP_BITS2(120, "bs_skip_bits2", FIVE),
    BS_TEST_TAIL2(121, "bs_test_tail2", arrayOf(FAIL_LABEL, *TWO)),
    BS_SAVE2(122, "bs_save2", TWO),
    BS_RESTORE2(123, "bs_restore2", TWO),

    // New GC bifs introduced in R11B.

    // @spec gc_bif1 Lbl Live Bif Arg Reg
    // @doc Call the bif Bif with the argument Arg, and store the result in Reg.
    //      On failure jump to Lbl.
    //      Do a garbage collection if necessary to allocate space on the heap
    //      for the result (saving Live number of X registers).
    GC_BIF1(
            124,
            "gc_bif1",
            arrayOf(
                    FAIL_LABEL,
                    LIVE_X_REGISTER_COUNT,
                    IMPORT,
                    Argument( "argument", UNAMBIGUOUS),
                    DESTINATION
            )
    ),

    // @spec gc_bif2 Lbl Live Bif Arg1 Arg2 Reg
    // @doc Call the bif Bif with the arguments Arg1 and Arg2,
    //      and store the result in Reg.
    //      On failure jump to Lbl.
    //      Do a garbage collection if necessary to allocate space on the heap
    //      for the result (saving Live number of X registers).
    GC_BIF2(
            125,
            "gc_bif2",
            arrayOf(
                    FAIL_LABEL,
                    LIVE_X_REGISTER_COUNT,
                    IMPORT,
                    Argument("argument1", Options(Inline(atoms = true, literals = true))),
                    Argument("argument2", Options(Inline(atoms = true, literals = true))),
                    DESTINATION
            )
    ),

    // Experimental new bit_level bifs introduced in R11B.
    // NOT used in R12B.
    BS_FINAL2(126, "-bs_final2", TWO),
    BS_BITS_TO_BYTES2(127, "-bs_bits_to_bytes2", TWO),

    // R11B-4
    PUT_LITERAL(128, "-put_literal", TWO),

    // R11B-5
    // @spec is_bitstr Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a bit string.
    IS_BITSTR(129, "is_bitstr", UNARY),

    // R12B
    BS_CONTEXT_TO_BINARY(130, "bs_context_to_binary", ONE),
    BS_TEST_UNIT(131, "bs_test_unit", THREE),
    BS_MATCH_STRING(132, "bs_match_string", arrayOf(FAIL_LABEL, *THREE)),
    BS_INIT_WRITABLE(133, "bs_init_writable"),
    BS_APPEND(134, "bs_append", arguments(8)),
    BS_PRIVATE_APPEND(135, "bs_private_append", SIX),

    // @spec trim N Remaining
    // @doc Reduce the stack usage by N words,
    //      keeping the CP on the top of the stack.
    TRIM(
            136,
            "trim",
            arrayOf(
                    WORDS_OF_STACK,
                    Argument("remaining", Options(Inline(integers = true)))
            )
    ),

    BS_INIT_BITS(137, "bs_init_bits", SIX),

    // R12B-5
    BS_GET_UTF8(138, "bs_get_utf8", FIVE),
    BS_SKIP_UTF8(139, "bs_skip_utf8", FOUR),

    BS_GET_UTF16(140, "bs_get_utf16", FIVE),
    BS_SKIP_UTF16(141, "bs_skip_utf16", FOUR),

    BS_GET_UTF32(142, "bs_get_utf32", FIVE),
    BS_SKIP_UTF32(143, "bs_skip_utf32", FOUR),

    BS_UTF8_SIZE(144, "bs_utf8_size", THREE),
    BS_PUT_UTF8(145, "bs_put_utf8", THREE),

    BS_UTF16_SIZE(146, "bs_utf16_size", THREE),
    BS_PUT_UTF16(147, "bs_put_utf16", THREE),

    BS_PUT_UTF32(148, "bs_put_utf32", THREE),

    // R13B03

    ON_LOAD(149, "on_load"),

    // R14A

    // @spec recv_mark Label
    // @doc  Save the end of the message queue and the address of
    //       the label Label so that a recv_set instruction can start
    //       scanning the inbox from this position.
    RECV_MARK(150, "recv_mark", arrayOf(LABEL_ARGUMENT)),

    // @spec recv_set Label
    // @doc Check that the saved mark points to Label and set the
    //      save pointer in the message queue to the last position
    //      of the message queue saved by the recv_mark instruction.
    RECV_SET(151, "recv_set", arrayOf(LABEL_ARGUMENT)),

    // @spec gc_bif3 Lbl Live Bif Arg1 Arg2 Arg3 Reg
    // @doc Call the bif Bif with the arguments Arg1, Arg2 and Arg3,
    //      and store the result in Reg.
    //      On failure jump to Lbl.
    //      Do a garbage collection if necessary to allocate space on the heap
    //      for the result (saving Live number of X registers).
    GC_BIF3(
            152,
            "gc_bif3",
            arrayOf(LABEL_ARGUMENT, LIVE_X_REGISTER_COUNT, IMPORT, *THREE, DESTINATION)
    ),

    // R15A

    LINE(
            153,
            "line",
            arrayOf(
                    Argument(
                            "line",
                            Options(Inline(integers = true, literals = false), showArgumentNames = false)
                    )
            )
    ),

    // R17

    PUT_MAP_ASSOC(
            154,
            "put_map_assoc",
            arrayOf(
                    FAIL_LABEL,
                    SOURCE,
                    DESTINATION,
                    LIVE_X_REGISTER_COUNT,
                    Argument("field_from_source", Options(Inline(atoms = true, integers = false)))
            )
    ),
    PUT_MAP_EXACT(155, "put_map_exact", FIVE),
    IS_MAP(156, "is_map", UNARY),
    HAS_MAP_FIELDS(
            157,
            "has_map_fields",
            arrayOf(
                    FAIL_LABEL,
                    SOURCE,
                    Argument("fields", Options(Inline(atoms = true)))
            )
    ),
    GET_MAP_ELEMENTS(
            158,
            "get_map_elements",
            arrayOf(
                    FAIL_LABEL,
                    SOURCE,
                    Argument("key_to_destination", Options(Inline(atoms = true, integers = false)))
            )
    ),

    // OTP 20

    // @spec is_tagged_tuple Lbl Reg N Atom
    // @doc Test the type of Reg and jumps to Lbl if it is not a tuple.
    //      Test the arity of Reg and jumps to Lbl if it is not N.
    //      Test the first element of the tuple and jumps to Lbl if it is not Atom.
    IS_TAGGED_TUPLE(
            159,
            "is_tagged_tuple",
            arrayOf(
                    FAIL_LABEL,
                    SOURCE,
                    ARITY,
                    Argument("tag", Options(Inline(atoms = true)))
            )
    ),

    // OTP 21

    // @spec build_stacktrace
    // @doc  Given the raw stacktrace in x(0), build a cooked stacktrace suitable
    //       for human consumption. Store it in x(0). Destroys all other registers.
    //       Do a garbage collection if necessary to allocate space on the heap
    //       for the result.
    BUILD_STACKTRACE(160, "build_stacktrace");

    fun arity() = arguments.size
}

val codeByNumber = Code.values().associateBy(Code::number)
