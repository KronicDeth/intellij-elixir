package org.elixir_lang.beam.chunk.code.operation

import org.elixir_lang.beam.chunk.code.operation.Argument.INTEGER
import org.elixir_lang.beam.chunk.code.operation.Argument.TERM

/**
 * https://github.com/erlang/otp/blob/OTP-20.2.2/lib/compiler/src/genop.tab
 */
enum class Code(val number: Int, val function: String, val arity: Int, val argumentNameToType: Array<Pair<String, Argument>>? = null) {
    // @spec label Lbl
    // @doc Specify a module local label.
    //      Label gives this code address a name (Lbl) and marks the start of
    //      a basic block.
    LABEL(1, "label", 1, arrayOf("label" to INTEGER)),

    // @spec func_info M F A
    // @doc Define a function M:F/A
    FUNC_INFO(2, "func_info", 3, arrayOf("module" to TERM, "function" to TERM, "arity" to INTEGER)),

    INT_CODE_END(3, "int_code_end", 0),

    //
    // Function and BIF calls.
    //

    // @spec call Arity Label
    // @doc Call the function at Label.
    //      Save the next instruction as the return address in the CP register.
    CALL(4, "call", 2, arrayOf("arity" to INTEGER, "label" to INTEGER)),

    // @spec call_last Arity Label Deallocate
    // @doc Deallocate and do a tail recursive call to the function at Label.
    //      Do not update the CP register.
    //      Before the call deallocate Deallocate words of stack.
    CALL_LAST(
            5,
            "call_last",
            3,
            arrayOf("arity" to INTEGER, "label" to INTEGER, "deallocate_words_of_stack" to INTEGER)
    ),

    // @spec call_only Arity Label
    // @doc Do a tail recursive call to the function at Label.
    //      Do not update the CP register.
    CALL_ONLY(6, "call_only", 2, arrayOf("arity" to INTEGER, "label" to INTEGER)),

    // @spec call_ext Arity Destination
    // @doc Call the function of arity Arity pointed to by Destination.
    //      Save the next instruction as the return address in the CP register.
    CALL_EXT(7, "call_ext", 2, arrayOf("arity" to INTEGER, "import_index" to INTEGER)),

    // @spec call_ext_last Arity Destination Deallocate
    // @doc Deallocate and do a tail call to function of arity Arity
    //      pointed to by Destination.
    //      Do not update the CP register.
    //      Deallocate Deallocate words from the stack before the call.
    CALL_EXT_LAST(
            8,
            "call_ext_last",
            3,
            arrayOf("arity" to INTEGER, "destination" to TERM, "deallocate_words_of_stack" to INTEGER)
    ),

    // @spec bif0 Bif Reg
    // @doc Call the bif Bif and store the result in Reg.
    BIF0(9, "bif0", 2, arrayOf("bif" to TERM, "destination_register" to TERM)),

    // @spec bif1 Lbl Bif Arg Reg
    // @doc Call the bif Bif with the argument Arg, and store the result in Reg.
    //      On failure jump to Lbl.
    BIF1(10, "bif1", 4, arrayOf("bif" to TERM, "label" to TERM, "argument" to TERM, "destination_register" to TERM)),

    // @spec bif2 Lbl Bif Arg1 Arg2 Reg
    // @doc Call the bif Bif with the arguments Arg1 and Arg2,
    //      and store the result in Reg.
    //      On failure jump to Lbl.
    BIF2(11, "bif2", 5, arrayOf("fail_label" to INTEGER, "import_index" to INTEGER, "argument1" to TERM, "argument2" to TERM, "destination_register" to TERM)),

    //
    // Allocating, deallocating and returning.
    //

    // @spec allocate StackNeed Live
    // @doc Allocate space for StackNeed words on the stack. If a GC is needed
    //      during allocation there are Live number of live X registers.
    //      Also save the continuation pointer (CP) on the stack.
    ALLOCATE(12, "allocate", 2, arrayOf("words_of_stack" to INTEGER, "live_x_register_count" to INTEGER)),

    // @spec allocate_heap StackNeed HeapNeed Live
    // @doc Allocate space for StackNeed words on the stack and ensure there is
    //      space for HeapNeed words on the heap. If a GC is needed
    //      save Live number of X registers.
    //      Also save the continuation pointer (CP) on the stack.
    ALLOCATE_HEAP(
            13,
            "allocate_heap",
            3,
            arrayOf("words_of_stack" to INTEGER, "words_of_heap" to INTEGER, "live_x_register_count" to INTEGER)
    ),

    // @spec allocate_zero StackNeed Live
    // @doc Allocate space for StackNeed words on the stack. If a GC is needed
    //      during allocation there are Live number of live X registers.
    //      Clear the new stack words. (By writing NIL.)
    //      Also save the continuation pointer (CP) on the stack.
    ALLOCATE_ZERO(14, "allocate_zero", 2, arrayOf("words_of_stack" to INTEGER, "live_x_register_count" to INTEGER)),

    // @spec allocate_heap_zero StackNeed HeapNeed Live
    // @doc Allocate space for StackNeed words on the stack and HeapNeed words
    //      on the heap. If a GC is needed
    //      during allocation there are Live number of live X registers.
    //      Clear the new stack words. (By writing NIL.)
    //      Also save the continuation pointer (CP) on the stack.
    ALLOCATE_HEAP_ZERO(15, "allocate_heap_zero", 3, arrayOf("words_of_stack" to INTEGER, "words_of_heap" to INTEGER, "live_x_register_count" to INTEGER)),

    // @spec test_heap HeapNeed Live
    // @doc Ensure there is space for HeapNeed words on the heap. If a GC is needed
    //      save Live number of X registers.
    TEST_HEAP(16, "test_heap", 2, arrayOf("words_of_heap" to INTEGER, "live_x_register_count" to INTEGER)),

    // @spec init N
    // @doc  Clear the Nth stack word. (By writing NIL.)
    INIT(17, "init", 1, arrayOf("nth_stack_word" to TERM)),

    // @spec deallocate N
    // @doc  Restore the continuation pointer (CP) from the stack and deallocate
    //       N+1 words from the stack (the + 1 is for the CP).
    DEALLOCATE(18, "deallocate", 1, arrayOf("words_of_stack" to INTEGER)),

    // @spec return
    // @doc  Return to the address in the continuation pointer (CP).
    RETURN(19, "return", 0),

    //
    // Sending & receiving.
    //
    // @spec send
    // @doc  Send argument in x(1) as a message to the destination process in x(0).
    //       The message in x(1) ends up as the result of the send in x(0).
    SEND(20, "send", 0),

    // @spec remove_message
    // @doc  Unlink the current message from the message queue and store a
    //       pointer to the message in x(0). Remove any timeout.
    REMOVE_MESSAGE(21, "remove_message", 0),

    // @spec timeout
    // @doc  Reset the save point of the mailbox and clear the timeout flag.
    TIMEOUT(22, "timeout", 0),

    // @spec loop_rec Label Source
    // @doc  Loop over the message queue, if it is empty jump to Label.
    LOOP_REC(23, "loop_rec", 2, arrayOf("label" to TERM, "source" to TERM)),

    // @spec loop_rec_end Label
    // @doc  Advance the save pointer to the next message and jump back to Label.
    LOOP_REC_END(24, "loop_rec_end", 1, arrayOf("label" to TERM)),

    // @spec wait Label
    // @doc  Suspend the processes and set the entry point to the beginning of the
    //       receive loop at Label.
    WAIT(25, "wait", 1, arrayOf("label" to TERM)),

    // @spec wait_timeout Lable Time
    // @doc  Sets up a timeout of Time milliseconds and saves the address of the
    //       following instruction as the entry point if the timeout triggers.
    WAIT_TIMEOUT(26, "wait_timeout", 2, arrayOf("label" to TERM, "Milliseconds" to INTEGER)),

    //
    // Arithmetic opcodes.
    //
    M_PLUS(27, "-m_plus", 4),
    M_MINUS(28, "-m_minus", 4),
    M_TIMES(29, "-m_times", 4),
    M_DIV(30, "-m_div", 4),
    INT_DIV(31, "-int_div", 4),
    INT_REM(32, "-int_rem", 4),
    INT_BAND(33, "-int_band", 4),
    INT_BOR(34, "-int_bor", 4),
    INT_BXOR(35, "-int_bxor", 4),
    INT_BSL(36, "-int_bsl", 4),
    INT_BSR(37, "-int_bsr", 4),
    INT_BNOT(38, "-int_bnot", 3),

    //
    // Comparision operators.
    //

    // @spec is_lt Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is not less than Arg2.
    IS_LT(39, "is_lt", 3, arrayOf("fail_label" to INTEGER, "argument1" to TERM, "argument2" to TERM)),

    // @spec is_ge Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is less than Arg2.
    IS_GE(40, "is_ge", 3, arrayOf("fail_label" to INTEGER, "argument1" to TERM, "argument2" to TERM)),

    // @spec is_eq Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is not (numerically) equal to Arg2.
    IS_EQ(41, "is_eq", 3, arrayOf("fail_label" to INTEGER, "argument1" to TERM, "argument2" to TERM)),

    // @spec is_ne Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is (numerically) equal to Arg2.
    IS_NE(42, "is_ne", 3, arrayOf("fail_label" to INTEGER, "argument1" to TERM, "argument2" to TERM)),

    // @spec is_eq_exact Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is not exactly equal to Arg2.
    IS_EQ_EXACT(43, "is_eq_exact", 3, arrayOf("fail_label" to INTEGER, "argument1" to TERM, "argument2" to TERM)),

    // @spec is_ne_exact Lbl Arg1 Arg2
    // @doc Compare two terms and jump to Lbl if Arg1 is exactly equal to Arg2.
    IS_NE_EXACT(44, "is_ne_exact", 3, arrayOf("fail_label" to INTEGER, "argument1" to TERM, "argument2" to TERM)),

    //
    // Type tests.
    //

    // @spec is_integer Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not an integer.
    IS_INTEGER(45, "is_integer", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_float Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a float.
    IS_FLOAT(46, "is_float", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_number Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a number.
    IS_NUMBER(47, "is_number", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_atom Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not an atom.
    IS_ATOM(48, "is_atom", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_pid Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a pid.
    IS_PID(49, "is_pid", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_reference Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a reference.
    IS_REFERENCE(50, "is_reference", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_port Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a port.
    IS_PORT(51, "is_port", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_nil Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not nil.
    IS_NIL(52, "is_nil", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_binary Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a binary.
    IS_BINARY(53, "is_binary", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    IS_CONSTANT(54, "-is_constant", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_list Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a cons or nil.
    IS_LIST(55, "is_list", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_nonempty_list Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a cons.
    IS_NONEMPTY_LIST(56, "is_nonempty_list", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec is_tuple Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a tuple.
    IS_TUPLE(57, "is_tuple", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // @spec test_arity Lbl Arg1 Arity
    // @doc Test the arity of (the tuple in) Arg1 and jump
    // to Lbl if it is not equal to Arity.
    TEST_ARITY(58, "test_arity", 3, arrayOf("fail_label" to INTEGER, "argument" to TERM, "arity" to INTEGER)),

    //
    // Indexing & jumping.
    //

    // @spec select_val Arg FailLabel Destinations
    // @doc Jump to the destination label corresponding to Arg
    //      in the Destinations list, if no arity matches, jump to FailLabel.
    SELECT_VAL(59, "select_val", 3, arrayOf("arity" to TERM, "fail_label" to INTEGER, "arity_label_list" to TERM)),

    // @spec select_tuple_arity Tuple FailLabel Destinations
    // @doc Check the arity of the tuple Tuple and jump to the corresponding
    //      destination label, if no arity matches, jump to FailLabel.
    SELECT_TUPLE_ARITY(60, "select_tuple_arity", 3, arrayOf("tuple" to TERM, "fail_label" to INTEGER, "arity_label_list" to TERM)),

    // @spec jump Label
    // @doc Jump to Label.
    JUMP(61, "jump", 1, arrayOf("label" to TERM)),

    //
    // Catch.
    //
    CATCH(62, "catch", 2),
    CATCH_END(63, "catch_end", 1),

    //
    // Moving, extracting, modifying.
    //

    // @spec move Source Destination
    // @doc Move the source Source (a literal or a register) to
    //      the destination register Destination.
    MOVE(64, "move", 2, arrayOf("source" to TERM, "destination_register" to TERM)),

    // @spec get_list  Source Head Tail
    // @doc  Get the head and tail (or car and cdr) parts of a list
    //       (a cons cell) from Source and put them into the registers
    //       Head and Tail.
    GET_LIST(65, "get_list", 3, arrayOf("source" to TERM, "head_register" to TERM, "tail_register" to TERM)),

    // @spec get_tuple_element Source Element Destination
    // @doc  Get element number Element from the tuple in Source and put
    //       it in the destination register Destination.
    GET_TUPLE_ELEMENT(
            66,
            "get_tuple_element",
            3,
            arrayOf("source" to TERM, "element_number" to INTEGER, "destination_register" to TERM)
    ),

    // @spec set_tuple_element NewElement Tuple Position
    // @doc  Update the element at position Position of the tuple Tuple
    //       with the new element NewElement.
    SET_TUPLE_ELEMENT(
            67,
            "set_tuple_element",
            3,
            arrayOf("new_element" to TERM, "tuple" to TERM, "position" to INTEGER)
    ),

    //
    // Building terms.
    //
    PUT_STRING(68, "-put_string", 3),
    PUT_LIST(69, "put_list", 3),
    PUT_TUPLE(70, "put_tuple", 2, arrayOf("size" to INTEGER, "register" to TERM)),
    PUT(71, "put", 1),

    //
    // Raising errors.
    //
    BADMATCH(72, "badmatch", 1),
    IF_END(73, "if_end", 0),
    CASE_END(74, "case_end", 1),

    //
    // 'fun' support.
    //
    // @spec call_fun Arity
    // @doc Call a fun of arity Arity. Assume arguments in
    //      registers x(0) to x(Arity-1) and that the fun is in x(Arity).
    //      Save the next instruction as the return address in the CP register.
    CALL_FUN(75, "call_fun", 1, arrayOf("arity" to INTEGER)),

    MAKE_FUN(76, "-make_fun", 3),

    // @spec is_function Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a
    //      function (i.e. fun or closure).
    IS_FUNCTION(77, "is_function", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    //
    // Late additions to R5.
    //

    // @spec call_ext_only Arity ImportIndex
    //      Do a tail recursive call to the function at ImportIndex.
    //      Do not update the CP register.
    CALL_EXT_ONLY(78, "call_ext_only", 2, arrayOf("arity" to INTEGER, "import_index" to INTEGER)),

    //
    // Binary matching (R7).
    //
    BS_START_MATCH(79, "-bs_start_match", 2),
    BS_GET_INTEGER(80, "-bs_get_integer", 5),
    BS_GET_FLOAT(81, "-bs_get_float", 5),
    BS_GET_BINARY(82, "-bs_get_binary", 5),
    BS_SKIP_BITS(83, "-bs_skip_bits", 4),
    BS_TEST_TAIL(84, "-bs_test_tail", 2),
    BS_SAVE(85, "-bs_save", 1),
    BS_RESTORE(86, "-bs_restore", 1),

    //
    // Binary construction (R7A).
    //
    BS_INIT(87, "-bs_init", 2),
    BS_FINAL(88, "-bs_final", 2),
    BS_PUT_INTEGER(89, "bs_put_integer", 5),
    BS_PUT_BINARY(90, "bs_put_binary", 5, arrayOf("fail_label" to INTEGER, "size" to TERM, "unit" to INTEGER, "flags" to TERM, "source" to TERM)),
    BS_PUT_FLOAT(91, "bs_put_float", 5),
    BS_PUT_STRING(92, "bs_put_string", 2, arrayOf("length" to INTEGER, "pool_offset" to INTEGER)),

    //
    // Binary construction (R7B).
    //
    BS_NEED_BUF(93, "-bs_need_buf", 1),

    //
    // Floating point arithmetic (R8).
    //
    FCLEARERROR(94, "fclearerror", 0),
    FCHECKERROR(95, "fcheckerror", 1),
    FMOVE(96, "fmove", 2),
    FCONV(97, "fconv", 2),
    FADD(98, "fadd", 4),
    FSUB(99, "fsub", 4),
    FMUL(100, "fmul", 4),
    FDIV(101, "fdiv", 4),
    FNEGATE(102, "fnegate", 3),

    // New fun construction (R8).
    MAKE_FUN2(103, "make_fun2", 1, arrayOf("function_index" to INTEGER)),

    // Try/catch/raise (R10B).
    TRY(104, "try", 2),
    TRY_END(105, "try_end", 1),
    TRY_CASE(106, "try_case", 1),
    TRY_CASE_END(107, "try_case_end", 1),
    RAISE(108, "raise", 2),

    // New instructions in R10B.
    BS_INIT2(109, "bs_init2", 6, arrayOf("fail_label" to INTEGER, "size" to INTEGER, "words_of_stacK" to INTEGER, "live_x_register_count" to INTEGER, "flags" to TERM, "destination_register" to TERM)),
    BS_BITS_TO_BYTES(110, "-bs_bits_to_bytes", 3),
    BS_ADD(111, "bs_add",5, arrayOf("fail_label" to INTEGER, "source1" to TERM, "source2" to TERM, "unit" to INTEGER, "destination" to TERM)),
    APPLY(112, "apply", 1),
    APPLY_LAST(113, "apply_last", 2),
    // @spec is_boolean Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a Boolean.
    IS_BOOLEAN(114, "is_boolean", 2),

    // New instructions in R10B-6.
    // @spec is_function2 Lbl Arg1 Arity
    // @doc Test the type of Arg1 and jump to Lbl if it is not a
    //      function of arity Arity.
    IS_FUNCTION2(115, "is_function2", 3, arrayOf("fail_label" to INTEGER, "argument" to TERM, "arity" to INTEGER)),

    // New bit syntax matching in R11B.

    BS_START_MATCH2(116, "bs_start_match2", 5),
    BS_GET_INTEGER2(117, "bs_get_integer2", 7),
    BS_GET_FLOAT2(118, "bs_get_float2", 7),
    BS_GET_BINARY2(119, "bs_get_binary2", 7),
    BS_SKIP_BITS2(120, "bs_skip_bits2", 5),
    BS_TEST_TAIL2(121, "bs_test_tail2", 3),
    BS_SAVE2(122, "bs_save2", 2),
    BS_RESTORE2(123, "bs_restore2", 2),

    // New GC bifs introduced in R11B.

    // @spec gc_bif1 Lbl Live Bif Arg Reg
    // @doc Call the bif Bif with the argument Arg, and store the result in Reg.
    //      On failure jump to Lbl.
    //      Do a garbage collection if necessary to allocate space on the heap
    //      for the result (saving Live number of X registers).
    GC_BIF1(124, "gc_bif1", 5, arrayOf("fail_label" to INTEGER, "live_x_register_count" to INTEGER, "import_index" to INTEGER, "argument" to TERM, "destination_register" to TERM)),

    // @spec gc_bif2 Lbl Live Bif Arg1 Arg2 Reg
    // @doc Call the bif Bif with the arguments Arg1 and Arg2,
    //      and store the result in Reg.
    //      On failure jump to Lbl.
    //      Do a garbage collection if necessary to allocate space on the heap
    //      for the result (saving Live number of X registers).
    GC_BIF2(125, "gc_bif2", 6, arrayOf("fail_label" to INTEGER, "live_x_register_count" to INTEGER, "import_index" to INTEGER, "argument1" to TERM, "argument2" to TERM, "destination_register" to TERM)),

    // Experimental new bit_level bifs introduced in R11B.
    // NOT used in R12B.
    BS_FINAL2(126, "-bs_final2", 2),
    BS_BITS_TO_BYTES2(127, "-bs_bits_to_bytes2", 2),

    // R11B-4
    PUT_LITERAL(128, "-put_literal", 2),

    // R11B-5
    // @spec is_bitstr Lbl Arg1
    // @doc Test the type of Arg1 and jump to Lbl if it is not a bit string.
    IS_BITSTR(129, "is_bitstr", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),

    // R12B
    BS_CONTEXT_TO_BINARY(130, "bs_context_to_binary", 1),
    BS_TEST_UNIT(131, "bs_test_unit", 3),
    BS_MATCH_STRING(132, "bs_match_string", 4),
    BS_INIT_WRITABLE(133, "bs_init_writable", 0),
    BS_APPEND(134, "bs_append", 8),
    BS_PRIVATE_APPEND(135, "bs_private_append", 6),

    // @spec trim N Remaining
    // @doc Reduce the stack usage by N words,
    //      keeping the CP on the top of the stack.
    TRIM(136, "trim", 2, arrayOf("words_of_stack" to INTEGER, "remaining" to INTEGER)),

    BS_INIT_BITS(137, "bs_init_bits", 6),

    // R12B-5
    BS_GET_UTF8(138, "bs_get_utf8", 5),
    BS_SKIP_UTF8(139, "bs_skip_utf8", 4),

    BS_GET_UTF16(140, "bs_get_utf16", 5),
    BS_SKIP_UTF16(141, "bs_skip_utf16", 4),

    BS_GET_UTF32(142, "bs_get_utf32", 5),
    BS_SKIP_UTF32(143, "bs_skip_utf32", 4),

    BS_UTF8_SIZE(144, "bs_utf8_size", 3),
    BS_PUT_UTF8(145, "bs_put_utf8", 3),

    BS_UTF16_SIZE(146, "bs_utf16_size", 3),
    BS_PUT_UTF16(147, "bs_put_utf16", 3),

    BS_PUT_UTF32(148, "bs_put_utf32", 3),

    // R13B03

    ON_LOAD(149, "on_load", 0),

    // R14A

    // @spec recv_mark Label
    // @doc  Save the end of the message queue and the address of
    //       the label Label so that a recv_set instruction can start
    //       scanning the inbox from this position.
    RECV_MARK(150, "recv_mark", 1, arrayOf("label" to TERM)),

    // @spec recv_set Label
    // @doc Check that the saved mark points to Label and set the
    //      save pointer in the message queue to the last position
    //      of the message queue saved by the recv_mark instruction.
    RECV_SET(151, "recv_set", 1, arrayOf("label" to TERM)),

    // @spec gc_bif3 Lbl Live Bif Arg1 Arg2 Arg3 Reg
    // @doc Call the bif Bif with the arguments Arg1, Arg2 and Arg3,
    //      and store the result in Reg.
    //      On failure jump to Lbl.
    //      Do a garbage collection if necessary to allocate space on the heap
    //      for the result (saving Live number of X registers).
    GC_BIF3(
            152,
            "gc_bif3",
            7,
            arrayOf(
                    "label" to TERM,
                    "live_x_register_count" to INTEGER,
                    "bif" to TERM,
                    "argument1" to TERM,
                    "argument2" to TERM,
                    "argument3" to TERM,
                    "destination_register" to TERM
            )
    ),

    // R15A

    LINE(153, "line", 1),

    // R17

    PUT_MAP_ASSOC(
            154,
            "put_map_assoc",
            5,
            arrayOf(
                    "fail_label" to INTEGER,
                    "source" to TERM,
                    "destination" to TERM,
                    "live_x_register_count" to TERM,
                    "field_from_source" to TERM
            )
    ),
    PUT_MAP_EXACT(155, "put_map_exact", 5),
    IS_MAP(156, "is_map", 2, arrayOf("fail_label" to INTEGER, "argument" to TERM)),
    HAS_MAP_FIELDS(157, "has_map_fields", 3, arrayOf("fail_label" to INTEGER, "source" to TERM, "fields" to TERM)),
    GET_MAP_ELEMENTS(158, "get_map_elements", 3, arrayOf("fail_label" to INTEGER, "source" to TERM, "key_to_destination" to TERM)),

    // OTP 20

    // @spec is_tagged_tuple Lbl Reg N Atom
    // @doc Test the type of Reg and jumps to Lbl if it is not a tuple.
    //      Test the arity of Reg and jumps to Lbl if it is not N.
    //      Test the first element of the tuple and jumps to Lbl if it is not Atom.
    IS_TAGGED_TUPLE(159, "is_tagged_tuple", 4, arrayOf("fail_label" to INTEGER, "register" to TERM, "arity" to INTEGER, "tag" to TERM)),

    // OTP 21

    // @spec build_stacktrace
    // @doc  Given the raw stacktrace in x(0), build a cooked stacktrace suitable
    //       for human consumption. Store it in x(0). Destroys all other registers.
    //       Do a garbage collection if necessary to allocate space on the heap
    //       for the result.
    BUILD_STACKTRACE(160, "build_stacktrace", 0);

    init {
        if (argumentNameToType != null) {
            val argumentNameCount = argumentNameToType.size
            assert(argumentNameCount == arity) {
                "Number of argument names ($argumentNameCount) differs from arity ($arity) for function ($function)"
            }
        }
    }
}

val codeByNumber = Code.values().associateBy(Code::number)
