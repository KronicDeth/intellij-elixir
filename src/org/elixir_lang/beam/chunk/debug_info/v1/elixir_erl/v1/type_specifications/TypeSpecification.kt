package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

import com.ericsson.otp.erlang.*
import org.elixir_lang.Macro
import org.elixir_lang.Macro.expr
import org.elixir_lang.Macro.variable
import org.elixir_lang.beam.chunk.debug_info.logger
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.TypeSpecifications
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications.TypeSpecification.Companion.defaultQuoted
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.otpErlangList
import org.elixir_lang.otpErlangTuple

const val TYPE = "type"

sealed class TypeSpecification {
    companion object {
        private const val FORM_TYPE = "attribute"

        fun argumentsToQuoted(arguments: OtpErlangList): List<OtpErlangObject> =
                arguments
                        .elements()
                        .withIndex()
                        .map { indexedTypeToQuoted(it) }

        fun defaultQuoted(): OtpErlangObject = expr("term")

        fun from(term: OtpErlangObject): TypeSpecification? =
                if (term is OtpErlangTuple && term.arity() == 4 && term.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == FORM_TYPE) {
                    fromAttribute(term.elementAt(2), term.elementAt(3))
                } else {
                    logger.error("""
                    TypeSpecification does not match `{:$FORM_TYPE, _, _, _}`

                    ## type

                    ```elixir
                    ${inspect(term)}
                    ```
                    """.trimIndent())

                    null
                }

        fun toQuoted(type: OtpErlangObject): OtpErlangObject? =
                when (type) {
                    is OtpErlangTuple -> toQuoted(type)
                    else -> {
                        logger.error("""
                        Don't know how to convert type to string.
                        ## type

                        ```
                        ${inspect(type)}
                        ```
                        """.trimIndent())

                        null
                    }
                }

        private fun toQuoted(type: OtpErlangTuple): OtpErlangObject? =
                if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "ann_type") {
                    annotatedTypeToQuoted(type.elementAt(2))
                } else if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "atom") {
                    atomToQuoted(type.elementAt(2))
                } else if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "integer") {
                    integerToQuoted(type.elementAt(2))
                } else if (type.arity() == 4 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "op") {
                    opToQuoted(type.elementAt(2), type.elementAt(3))
                } else if (type.arity() == 5 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "op") {
                    opToQuoted(type.elementAt(2), type.elementAt(3), type.elementAt(4))
                } else if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "var") {
                    varToQuoted(type.elementAt(2))
                } else if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "remote_type") {
                    remoteTypeToQuoted(type.elementAt(2))
                } else if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "type") {
                    builtinTypeToQuoted(type.elementAt(2));
                } else if (type.arity() == 4 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "type") {
                    builtinTypeToQuoted(type.elementAt(2), type.elementAt(3))
                } else if (type.arity() == 4 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "user_type") {
                    userTypeToQuoted(type.elementAt(2), type.elementAt(3))
                } else {
                    logger.error("""
                    Don't know how to convert type to string.

                    ## type

                    ```
                    ${inspect(type)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun annotatedTypeToQuoted(annotatedType: OtpErlangObject): OtpErlangObject? =
                if (annotatedType is OtpErlangList && annotatedType.arity() == 2) {
                    annotatedTypeToQuoted(annotatedType.elementAt(0), annotatedType.elementAt(1))
                } else {
                    logger.error("""
                    Annotated type does not match `[_, _]`

                    ## annotate type

                    ```
                    ${inspect(annotatedType)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun annotatedTypeToQuoted(annotation: OtpErlangObject, type: OtpErlangObject): OtpErlangObject? {
            val annotationQuoted = annotationToQuoted(annotation)
            val typeQuoted = toQuoted(type)

            return if (annotationQuoted != null) {
                expr(
                        "::",
                        annotationQuoted,
                        typeQuoted ?: defaultQuoted()
                )
            } else {
                typeQuoted
            }
        }

        private fun annotationToQuoted(annotation: OtpErlangObject): OtpErlangObject? =
                if (annotation is OtpErlangTuple && annotation.arity() == 3 &&
                        annotation.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "var") {
                    val variable = annotation.elementAt(2)

                    if (variable is OtpErlangAtom) {
                        Macro.variable(variable)
                    } else {
                        logger.error("""
                        Annotation did not match `{:var, _, variable :: atom()}`.

                        ## variable

                        ```
                        ${inspect(variable)}
                        ```
                        """.trimIndent())

                        null
                    }
                } else {
                    // work-around for code before https://github.com/elixir-lang/elixir/pull/8161 was merged
                    toQuoted(annotation)
                }

        private fun atomToQuoted(atom: OtpErlangObject): OtpErlangObject? =
                if (atom is OtpErlangAtom) {
                    atom
                } else {
                    logger.error("""
                    Atom type does not match `atom()`

                    ## atom

                    ```
                    ${inspect(atom)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun integerToQuoted(integer: OtpErlangObject): OtpErlangObject? =
                if (integer is OtpErlangLong) {
                    integer
                } else {
                    logger.error("""
                    Integer type does not match `integer()`.

                    ## integer

                    ```
                    ${inspect(integer)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun opToQuoted(op: OtpErlangObject, operand: OtpErlangObject): OtpErlangObject? =
                if (op is OtpErlangAtom) {
                    expr(
                            op,
                            toQuoted(operand) ?: defaultQuoted()
                    )
                } else {
                    logger.error("""
                    Op name doesn't match `atom()`.

                    ## op

                    ```elixir
                    ${inspect(op)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun opToQuoted(op: OtpErlangObject, left: OtpErlangObject, right: OtpErlangObject): OtpErlangObject? =
                if (op is OtpErlangAtom) {
                    expr(
                            op,
                            toQuoted(left) ?: defaultQuoted(),
                            toQuoted(right) ?: defaultQuoted()
                    )
                } else {
                    logger.error("""
                    Op name doesn't match `atom()`.

                    ## op

                    ```elixir
                    ${inspect(op)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun varToAtom(variable: OtpErlangObject): OtpErlangAtom? =
                if (variable is OtpErlangAtom) {
                    variable
                } else {
                    logger.error("""
                    Variable name does not match `atom()`.

                    ## variable

                    ```
                    ${inspect(variable)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun varToQuoted(variable: OtpErlangObject): OtpErlangObject? =
                if (variable is OtpErlangAtom) {
                    Macro.variable(variable)
                } else {
                    logger.error("""
                    Variable name does not match `atom()`.

                    ## variable

                    ```
                    ${inspect(variable)}
                    ```
                    """.trimIndent())

                    null
                }


        private fun remoteTypeToQuoted(remoteType: OtpErlangObject): OtpErlangObject? =
                if (remoteType is OtpErlangList && remoteType.arity() == 3) {
                    remoteTypeToQuoted(remoteType.elementAt(0), remoteType.elementAt(1), remoteType.elementAt(2))
                } else {
                    logger.error("""
                    remote type did not match `[_, _, _]`

                    ## remote type

                    ```
                    ${inspect(remoteType)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun remoteTypeToQuoted(
                module: OtpErlangObject,
                function: OtpErlangObject,
                arguments: OtpErlangObject
        ): OtpErlangObject? =
                if (module is OtpErlangTuple && module.arity() == 3 && module.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "atom" &&
                        function is OtpErlangTuple && function.arity() == 3 && function.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "atom" &&
                        arguments is OtpErlangList) {
                    moduleNameFunctionNameArgumentsToQuoted(module.elementAt(2), function.elementAt(2), arguments)
                } else {
                    logger.error("""
                    remote type does not match `[{:atom, _, _}, {:atom, _, _}, list()]`

                    ## module

                    ```
                    ${inspect(module)}
                    ```

                    ## function

                    ```
                    ${inspect(function)}
                    ```

                    ## arguments

                    ```
                    ${inspect(function)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun moduleNameFunctionNameArgumentsToQuoted(
                moduleName: OtpErlangObject,
                functionName: OtpErlangObject,
                arguments: OtpErlangList
        ): OtpErlangObject? =
                if (moduleName is OtpErlangAtom && functionName is OtpErlangAtom) {
                    // `:elixir` module types are built-in types in Elixir, but not Erlang
                    if (moduleName.atomValue() == "elixir") {
                        expr(function = functionName, argumentList = argumentsToQuoted(arguments))
                    } else {
                        expr(
                                function = expr(".", otpErlangList(moduleName, functionName)),
                                argumentList = argumentsToQuoted(arguments)
                        )
                    }
                } else {
                    logger.error("""
                    remote type does not match `[{:atom, _, module_name :: atom()}, {:atom, _, function_name :: atom()}, list()]`

                    ## module_name

                    ```
                    ${inspect(moduleName)}
                    ```

                    ## function_name

                    ```
                    ${inspect(functionName)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun indexedTypeToQuoted(indexedType: IndexedValue<OtpErlangObject>): OtpErlangObject =
                toQuoted(indexedType.value) ?: variable("t${indexedType.index}")

        private fun builtinTypeToQuoted(name: OtpErlangObject): OtpErlangObject? =
                if (name is OtpErlangAtom && name.atomValue() == "any") {
                    variable("...")
                } else {
                    logger.error("""
                    type does not match `{:type, _, name :: :any}`.

                    ## name

                    ```
                    ${inspect(name)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun builtinTypeToQuoted(name: OtpErlangObject, arguments: OtpErlangObject): OtpErlangObject? =
                if (name is OtpErlangAtom) {
                    when (name.atomValue()) {
                        "constraint" -> constraintToQuoted(arguments)
                        "fun" -> funToQuoted(arguments)
                        "list" -> listToQuoted(arguments)
                        "map" -> mapToQuoted(arguments)
                        "nil" -> nilToQuoted(arguments)
                        "nonempty_list" -> nonEmptyListToQuoted(arguments)
                        "product" -> productToQuoted(arguments)
                        "range" -> rangeToQuoted(arguments)
                        "tuple" -> tupleToQuoted(arguments)
                        "union" -> unionToQuoted(arguments)
                        else ->
                            if (arguments is OtpErlangList) {
                                expr(
                                        function = name,
                                        argumentList = argumentsToQuoted(arguments)
                                )
                            } else {
                                logger.error("""
                                $name does not match `{:type, _, :$name, arguments :: list()}`.

                                ## arguments

                                ```elixir
                                ${inspect(arguments)}
                                ```
                                """.trimIndent())

                                null
                            }
                    }
                } else {
                    logger.error("""
                    type does not match `{:type, _, name :: atom(), arguments :: term()}`.

                    ## name

                    ```
                    ${inspect(name)}
                    ```

                    ## arguments

                    ```
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun constraintToQuoted(arguments: OtpErlangObject): OtpErlangObject? =
                if (arguments is OtpErlangList && arguments.arity() == 2 && isIsSubtype(arguments.elementAt(0))) {
                    subtypeToQuoted(arguments.elementAt(1))
                } else {
                    logger.error("""
                    constraint arguments do not match `[{:atom, _, :is_subtype}, _]`

                    ## arguments

                    ```elixir
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun isIsSubtype(term: OtpErlangObject): Boolean =
                term is OtpErlangTuple && term.arity() == 3 &&
                        term.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "atom" &&
                        term.elementAt(2).let { it as? OtpErlangAtom }?.atomValue() == "is_subtype"

        private fun subtypeToQuoted(subtype: OtpErlangObject): OtpErlangObject? =
                if (subtype is OtpErlangList && subtype.arity() == 2) {
                    subtypeToQuoted(subtype.elementAt(0), subtype.elementAt(1))
                } else {
                    logger.error("""
                    subtype does not match `[_, _]`.

                    ## subtype

                    ```elixir
                    ${inspect(subtype)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun subtypeToQuoted(`var`: OtpErlangObject, type: OtpErlangObject): OtpErlangObject? =
                if (`var` is OtpErlangTuple && `var`.arity() == 3 && `var`.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "var") {
                    varToAtom(`var`.elementAt(2))?.let { varAtom ->
                        val typeQuoted = toQuoted(type) ?: defaultQuoted()

                        // 2-tuples are literals in `Macro.t()`, so this doesn't use `expr`.
                        otpErlangTuple(varAtom, typeQuoted)
                    }
                } else {
                    logger.error("""
                    subtype variable does not match `{:var, _, _}`

                    ```elixir
                    ${inspect(`var`)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun funToQuoted(arguments: OtpErlangObject): OtpErlangObject? =
                if (arguments is OtpErlangList) {
                    funToQuoted(arguments)
                } else {
                    logger.error("""
                    fun does not match `list()`.

                    ## arguments

                    ```
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun funToQuoted(arguments: OtpErlangList): OtpErlangObject? =
                when (arguments.arity()) {
                    0 ->
                        expr(
                                "->",
                                variable("..."),
                                expr("any")
                        )
                    2 ->
                        expr(
                                "->",
                                toQuoted(arguments.elementAt(0)) ?: defaultQuoted(),
                                toQuoted(arguments.elementAt(1)) ?: defaultQuoted()
                        )
                    else -> {
                        logger.error("""
                            fun arguments does not match `[]` or `[inputs :: term(), output :: term()]`.

                            ## arguments

                            ```
                            ${inspect(arguments)}
                            ```
                            """.trimIndent())

                        null
                    }
                }

        private fun listToQuoted(arguments: OtpErlangObject): OtpErlangObject? =
                if (arguments is OtpErlangList) {
                    if (arguments.arity() == 0) {
                        expr("list")
                    } else {
                        otpErlangList(argumentsToQuoted(arguments))
                    }
                } else {
                    logger.error("""
                    list does not match `list()`.

                    ## arguments

                    ```elixir
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun mapToQuoted(arguments: OtpErlangObject): OtpErlangObject? =
                if (arguments is OtpErlangAtom && arguments.atomValue() == "any") {
                    expr("map")
                } else if (arguments is OtpErlangList) {
                    mapToQuoted(arguments)
                } else {
                    logger.error("""
                    map does not match `:any` or `list()`.

                    ## arguments

                    ```elixir
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun mapToQuoted(arguments: OtpErlangList): OtpErlangObject? {
            val mapFieldList = arguments.mapNotNull { MapField.from(it) }

            return if (mapFieldList.size == arguments.arity()) {
                if (mapFieldList.any { it is MapFieldOptional || it is MapFieldRequiredNonAtom }) {
                    associationMapToQuoted(mapFieldList)
                } else {
                    val mapFieldStruct = mapFieldList.mapNotNull { it as? MapFieldStruct }.singleOrNull()

                    if (mapFieldStruct != null) {
                        val mapFieldRequiredAtoms =
                                mapFieldList
                                        .filter { it !is MapFieldStruct }
                                        .map { it as MapFieldRequiredAtom }

                        structToQuoted(mapFieldStruct, mapFieldRequiredAtoms)
                    } else {
                        keywordMapToQuoted(mapFieldList.map { it as MapFieldRequiredAtom })
                    }
                }
            } else {
                null
            }
        }

        private fun associationMapToQuoted(associations: List<MapField>): OtpErlangObject =
                expr("%{}", associationsToQuoted(associations))

        private fun associationsToQuoted(associations: List<MapField>): OtpErlangList =
                associations
                        .map { associationToQuoted(it) }
                        .let { otpErlangList(it) }

        private fun associationToQuoted(association: MapField): OtpErlangTuple =
                when (association) {
                    is MapFieldRequiredAtom ->
                        otpErlangTuple(
                                expr("required", association.name),
                                toQuoted(association.value) ?: defaultQuoted()
                        )
                    is MapFieldStruct ->
                        otpErlangTuple(
                                expr("required", OtpErlangAtom("__struct__")),
                                toQuoted(association.value) ?: defaultQuoted()
                        )
                    is MapFieldRequiredNonAtom ->
                        otpErlangTuple(
                                expr("required", toQuoted(association.name) ?: defaultQuoted()),
                                toQuoted(association.value) ?: defaultQuoted()
                        )
                    is MapFieldOptional ->
                        otpErlangTuple(
                                expr("optional", toQuoted(association.name) ?: defaultQuoted()),
                                toQuoted(association.value) ?: defaultQuoted()
                        )
                }

        private fun structToQuoted(mapFieldStruct: MapFieldStruct, mapFieldRequiredAtoms: List<MapFieldRequiredAtom>): OtpErlangObject =
                expr(
                        "%",
                        mapFieldStruct.value,
                        expr("%{}", arguments = keywordsToQuoted(mapFieldRequiredAtoms))
                )

        private fun keywordsToQuoted(keywords: List<MapFieldRequiredAtom>): OtpErlangList =
                keywords
                        .map { keywordToQuoted(it) }
                        .let { otpErlangList(it) }

        private fun keywordToQuoted(keyword: MapFieldRequiredAtom): OtpErlangObject =
                otpErlangTuple(
                        keyword.name,
                        toQuoted(keyword.value) ?: defaultQuoted()
                )

        private fun keywordMapToQuoted(keywords: List<MapFieldRequiredAtom>): OtpErlangObject =
                expr(
                        "%{}",
                        arguments = keywordsToQuoted(keywords)
                )

        private fun nilToQuoted(arguments: OtpErlangObject): OtpErlangList? =
                if (arguments is OtpErlangList && arguments.arity() == 0) {
                    OtpErlangList()
                } else {
                    logger.error("""
                    empty list arguments do not match `[]`.

                    ```elixir
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun nonEmptyListToQuoted(arguments: OtpErlangObject): OtpErlangList? =
                if (arguments is OtpErlangList) {
                    val argumentsQuote = arguments.map { toQuoted(it) ?: defaultQuoted() }

                    val nonEmptyList = mutableListOf<OtpErlangObject>()
                    nonEmptyList.addAll(argumentsQuote)
                    nonEmptyList.add(variable("..."))

                    otpErlangList(nonEmptyList)
                } else {
                    logger.error("""
                    non-empty list elements does not match `list()`.

                    ```elixir
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun productToQuoted(arguments: OtpErlangObject): OtpErlangObject? =
                if (arguments is OtpErlangList) {
                    arguments
                            .map { toQuoted(it) ?: defaultQuoted() }
                            .let { otpErlangList(it) }
                } else {
                    logger.error("""
                    product does not match `list()`.

                    ## arguments

                    ```elixir
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun rangeToQuoted(arguments: OtpErlangObject): OtpErlangObject? =
                if (arguments is OtpErlangList && arguments.arity() == 2) {
                    toQuoted(arguments.elementAt(0))?.let { firstQuoted ->
                        toQuoted(arguments.elementAt(1))?.let { lastQuoted ->
                            expr(
                                    "..",
                                    firstQuoted,
                                    lastQuoted
                            )
                        }
                    } ?: expr("range")
                } else {
                    logger.error("""
                    range arguments do not match `[_, _]`.

                    ## arguments

                    ```elixir
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun tupleToQuoted(arguments: OtpErlangObject): OtpErlangObject? =
                if (arguments is OtpErlangAtom && arguments.atomValue() == "any") {
                    expr("tuple")
                } else if (arguments is OtpErlangList) {
                    if (arguments.arity() == 2) {
                        otpErlangTuple(argumentsToQuoted(arguments))
                    } else {
                        expr(function = "{}", argumentList = argumentsToQuoted(arguments))
                    }
                } else {
                    logger.error("""
                    tuple does not match `:any` or `list()`.

                    ## arguments

                    ```elixir
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun unionToQuoted(arguments: OtpErlangObject): OtpErlangObject? =
                if (arguments is OtpErlangList) {
                    arguments
                            .map { TypeSpecification.toQuoted(it) ?: defaultQuoted() }
                            .reduceRight { argumentQuoted, acc ->
                                expr("|", argumentQuoted, acc)
                            }
                } else {
                    logger.error("""
                    union does not match `list()`.

                    ## arguments

                    ```elixir
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun userTypeToQuoted(name: OtpErlangObject, arguments: OtpErlangObject): OtpErlangObject? =
                if (name is OtpErlangAtom && arguments is OtpErlangList) {
                    expr(
                            function = name,
                            argumentList = argumentsToQuoted(arguments)
                    )
                } else {
                    logger.error("""
                    user type does not match `{:user_type, _, name :: atom(), arguments :: list()}`

                    ## name

                    ```
                    ${inspect(name)}
                    ```

                    ## arguments

                    ```
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun fromAttribute(
                attributeType: OtpErlangObject,
                attributeValue: OtpErlangObject
        ): TypeSpecification? =
                if (attributeType is OtpErlangAtom) {
                    fromAttribute(attributeType.atomValue(), attributeValue)
                } else {
                    logger.error("""
                    Specification attribute type is not an atom.

                    ```elixir
                    ${inspect(attributeType)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun fromAttribute(
                attributeType: String,
                attributeValue: OtpErlangObject
        ): TypeSpecification? =
                when (attributeType) {
                    "callback" -> Callback.from(attributeValue)
                    "export_type" -> ExportType.from(attributeValue)
                    "opaque" -> Opaque.from(attributeValue)
                    "optional_callbacks" -> OptionalCallback.from(attributeValue)
                    "spec" -> Specification.from(attributeValue)
                    "type" -> Type.from(attributeValue)
                    else -> {
                        logger.error("Don't know how to convert attribute type $attributeType to TypeSpecification")

                        null
                    }
                }
    }
}

object Function {
    fun toQuotedList(function: String, arity: Long, body: OtpErlangObject): List<OtpErlangObject> =
            bodyToQuotedList(function, body) ?: listOf(arityFunctionQuoted(function, arity))

    private fun bodyToQuotedList(function: String, body: OtpErlangObject): List<OtpErlangObject>? =
            if (body is OtpErlangList) {
                val bodyQuotedList = body.mapNotNull { bodyElement -> bodyElementToQuoted(function, bodyElement) }

                if (bodyQuotedList.isNotEmpty()) {
                    bodyQuotedList
                } else {
                    null
                }
            } else {
                logger.error("""
                body does not match `list()`.

                ## body

                ```elixir
                ${inspect(body)}
                ```
                """.trimIndent())

                null
            }

    private fun bodyElementToQuoted(function: String, bodyElement: OtpErlangObject): OtpErlangObject? =
            if (bodyElement is OtpErlangTuple && bodyElement.arity() == 4 && bodyElement.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "type") {
                bodyElementToQuoted(function, bodyElement.elementAt(2), bodyElement.elementAt(3))
            } else {
                logger.error("""
                body element does not match `{:type, _, _, _}`

                ## body element

                ```elixir
                ${inspect(bodyElement)}
                ```
                """.trimIndent())

                null
            }

    private fun bodyElementToQuoted(function: String, typeType: OtpErlangObject, typeValue: OtpErlangObject): OtpErlangObject? =
            if (typeType is OtpErlangAtom) {
                bodyElementToQuoted(function, typeType, typeValue)
            } else {
                logger.error("""
                body list element form type type does not match `atom()`.

                ## body list element form type

                ```elixir
                ${inspect(typeType)}
                ```
                """.trimIndent())

                null
            }

    private fun bodyElementToQuoted(function: String, typeType: OtpErlangAtom, typeValue: OtpErlangObject): OtpErlangObject? =
            when (typeType.atomValue()) {
                "bounded_fun" -> boundedFunToQuoted(function, typeValue)
                "fun" -> funToQuoted(function, typeValue)
                else -> {
                    logger.error("""
                    @callback body list element form type type is not `:bound_fun` or `:fun`.

                    ```elixir
                    ${inspect(typeType)}
                    ```
                    """.trimIndent())

                    null
                }
            }

    private fun boundedFunToQuoted(function: String, typeValue: OtpErlangObject): OtpErlangObject? =
            if (typeValue is OtpErlangList && typeValue.arity() == 2) {
                boundedFunToQuoted(function, typeValue.elementAt(0), typeValue.elementAt(1))
            } else {
                logger.error("""
                bounded_fun does not match `[_, _]`.

                ## type value

                ```elixir
                ${inspect(typeValue)}
                ```
                """.trimIndent())

                null
            }

    private fun boundedFunToQuoted(function: String, `fun`: OtpErlangObject, constraints: OtpErlangObject): OtpErlangObject? =
            bodyElementToQuoted(function, `fun`)?.let { bodyElementQuoted ->
                constraintsToQuoted(constraints)?.let { constraintsQuoted ->
                    expr("when", bodyElementQuoted, constraintsQuoted)
                } ?: bodyElementQuoted
            }

    private fun constraintsToQuoted(constraints: OtpErlangObject): OtpErlangObject? =
            if (constraints is OtpErlangList) {
                constraintsToQuoted(constraints)
            } else {
                logger.error("""
                constraints do not match `list()`.

                ## constraints

                ```elixir
                ${inspect(constraints)}
                ```
                """.trimIndent())

                null
            }

    private fun constraintsToQuoted(constraints: OtpErlangList): OtpErlangObject =
            constraints
                    .mapNotNull { TypeSpecification.toQuoted(it) }
                    .let { otpErlangList(it) }

    private fun funToQuoted(function: String, `fun`: OtpErlangObject): OtpErlangObject? =
            if (`fun` is OtpErlangList && `fun`.arity() == 2) {
                funToQuoted(function, `fun`.elementAt(0), `fun`.elementAt(1))
            } else {
                logger.error("""
                fun does not match `[_, _]`.

                ## fun

                ```elixir
                ${inspect(`fun`)}
                ```
                """.trimIndent())

                null
            }

    private fun funToQuoted(function: String, input: OtpErlangObject, output: OtpErlangObject): OtpErlangObject =
            expr(
                    "::",
                    expr(
                            function,
                            TypeSpecification.toQuoted(input) as OtpErlangList
                    ),
                    TypeSpecification.toQuoted(output) ?: defaultQuoted()
            )

    private fun arityFunctionQuoted(function: String, arity: Long): OtpErlangObject =
            expr(
                    "::",
                    expr(
                            function,
                            arityInputQuoted(arity)
                    ),
                    TypeSpecification.defaultQuoted()
            )

    private fun arityInputQuoted(arity: Long): OtpErlangList =
            (0 until arity.toInt())
                    .map { variable(parameterString(it)) }
                    .let { otpErlangList(it) }

    private fun parameterString(index: Int): String = "p${index + 1}"
}

data class Callback(val function: String, val arity: Long, val body: OtpErlangObject) : TypeSpecification() {
    override fun toString(): String = _string

    private val quotedList by lazy {
        Function
                .toQuotedList(function, arity, body)
                .map {
                    expr(
                            "@",
                            expr(
                                    "callback",
                                    it
                            )
                    )
                }
    }

    private val _string: String by lazy {
        quotedList.joinToString("\n") {
            Macro.toString(it)
        }
    }

    companion object {
        fun from(attributeValue: OtpErlangObject): Callback? =
                if (attributeValue is OtpErlangTuple && attributeValue.arity() == 2) {
                    from(attributeValue.elementAt(0), attributeValue.elementAt(1))
                } else {
                    logger.error("""
                    @callback does not match `{_, _}`

                    ```elixir
                    $attributeValue
                    ```
                    """.trimIndent())

                    null
                }

        private fun from(head: OtpErlangObject, body: OtpErlangObject): Callback? =
                if (head is OtpErlangTuple && head.arity() == 2 && body is OtpErlangList) {
                    from(head.elementAt(0), head.elementAt(1), body)
                } else {
                    logger.error("""
                    Does not match `{head :: {_, _}, body :: list()}`

                    ## head

                    ```elixir
                    ${inspect(head)}
                    ```

                    ## body

                    ```elixir
                    ${inspect(body)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun from(function: OtpErlangObject, arity: OtpErlangObject, body: OtpErlangList): Callback? =
                if (function is OtpErlangAtom && arity is OtpErlangLong) {
                    Callback(function = function.atomValue(), arity = arity.longValue(), body = body)
                } else {
                    logger.error("""
                    head does not match `{function :: atom(), arity :: non_neg_integer()}`

                    ## function

                    ```elixir
                    ${inspect(function)}
                    ```

                    ## arity

                    ```elixir
                    ${inspect(arity)}
                    ```
                    """.trimIndent())

                    null
                }
    }
}

data class ExportType(val name: String, val arity: Int) : TypeSpecification() {
    companion object {
        fun from(attributeValue: OtpErlangObject): ExportType? {
            val exportType = if (attributeValue is OtpErlangList && attributeValue.arity() == 1) {
                attributeValue.elementAt(0).let { it as? OtpErlangTuple }?.let { prop ->
                    val (key, value) = prop

                    if (key is OtpErlangAtom && value is OtpErlangLong) {
                        ExportType(key.atomValue(), value.intValue())
                    } else {
                        null
                    }
                }
            } else {
                null
            }

            if (exportType == null) {
                logger.error("""
                -export_type does no match `[{name :: atom(), arity :: non_neg_integer()}]`

                ## export type

                ```
                ${inspect(attributeValue)}
                ```
                """.trimIndent())
            }

            return exportType
        }
    }
}

object Typic {
    fun <T> from(attributeValue: OtpErlangObject, constructor: (name: String, inputs: OtpErlangList, output: OtpErlangObject) -> T): T? {
        val type = if (attributeValue is OtpErlangTuple && attributeValue.arity() == 3) {
            val name = attributeValue.elementAt(0).let { it as? OtpErlangAtom }?.atomValue()
            val output = attributeValue.elementAt(1)
            val inputs = attributeValue.elementAt(2).let { it as? OtpErlangList }

            if (name != null && inputs != null) {
                constructor(name, inputs, output)
            } else {
                null
            }
        } else {
            null
        }

        if (type == null) {
            logger.error("""
            typic Does not match `{name :: atom(), output :: term(), inputs :: list()}`

            ## typic

            ```
            ${inspect(attributeValue)}
            ```
            """.trimIndent())
        }

        return type
    }

    fun toQuoted(moduleAttributeName: String, name: String, inputs: OtpErlangList, output: OtpErlangObject): OtpErlangObject {
        val inputsQuoted = TypeSpecification.argumentsToQuoted(inputs)
        val outputQuoted = TypeSpecification.toQuoted(output) ?: defaultQuoted()

        return expr(
                "@",
                expr(
                        moduleAttributeName,
                        expr(
                                "::",
                                expr(
                                        name,
                                        inputsQuoted
                                ),
                                outputQuoted
                        )
                )
        )
    }
}

data class Opaque(val name: String, val inputs: OtpErlangList, val output: OtpErlangObject) : TypeSpecification() {
    val arity = inputs.arity()

    override fun toString(): String = _string

    private val quoted by lazy {
        Typic.toQuoted("opaque", name, inputs, output)
    }

    private val _string: String by lazy {
        Macro.toString(quoted)
    }

    companion object {
        fun from(attributeValue: OtpErlangObject): Opaque? = Typic.from(attributeValue, ::Opaque)
    }
}

data class OptionalCallback(val name: String, val arity: Int) : TypeSpecification() {
    companion object {
        fun from(attributeValue: OtpErlangObject): OptionalCallback? =
                if (attributeValue is OtpErlangList) {
                    from(attributeValue)
                } else {
                    logger.error("""
                    @optional_callbacks argument does not match `list()`.

                    ## arguments

                    ```
                    ${inspect(attributeValue)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun from(attributeValue: OtpErlangList): OptionalCallback =
                optionalCallbackList(attributeValue).single()

        private fun optionalCallbackList(list: OtpErlangList): List<OptionalCallback> = list.mapNotNull { optionalCallback(it) }

        private fun optionalCallback(entry: OtpErlangObject): OptionalCallback? =
                if (entry is OtpErlangTuple && entry.arity() == 2) {
                    optionalCallback(entry.elementAt(0), entry.elementAt(1))
                } else {
                    logger.error("""
                name_arity does not match `{_, _}`.

                ## name_arity

                ```elixir
                ${inspect(entry)}
                ```
                """.trimIndent())

                    null
                }

        private fun optionalCallback(name: OtpErlangObject, arity: OtpErlangObject): OptionalCallback? =
                if (name is OtpErlangAtom && arity is OtpErlangLong) {
                    OptionalCallback(name.atomValue(), arity.intValue())
                } else {
                    logger.error("""
                    name does not match `atom()` and arity does not match `integer()`.

                    ## name

                    ```elixir
                    ${inspect(name)}
                    ```

                    ## arity

                    ```elixir
                    ${inspect(arity)}
                    ```
                    """.trimIndent())

                    null
                }
    }
}

/**
 * > If F is a function specification -Spec Name Ft_1; ...; Ft_k, where Spec is either the atom spec or the atom
 * > callback, and each Ft_i is a possibly constrained function type with an argument sequence of the same length
 * > Arity, then Rep(F) = {attribute,Line,Spec,{{Name,Arity},[Rep(Ft_1), ..., Rep(Ft_k)]}}.
 *
 * > If F is a function specification -spec Mod:Name Ft_1; ...; Ft_k, where each Ft_i is a possibly constrained function
 * > type with an argument sequence of the same length Arity,
 * > then Rep(F) = {attribute,Line,spec,{{Mod,Name,Arity},[Rep(Ft_1), ..., Rep(Ft_k)]}}.
 */
data class Specification(
        val module: String? = null,
        val function: String,
        val arity: Long,
        val body: OtpErlangObject
) : TypeSpecification() {
    override fun toString(): String = _string

    private val quotedList by lazy {
        Function
                .toQuotedList(function, arity, body)
                .map {
                    expr(
                            "@",
                            expr(
                                    "spec",
                                    modulePrefix(it as OtpErlangTuple)
                            )
                    )
                }
    }

    private val _string: String by lazy {
        quotedList.joinToString("\n") {
            Macro.toString(it)
        }
    }

    private fun modulePrefix(functionQuoted: OtpErlangTuple): OtpErlangObject {
        assert(functionQuoted.arity() == 3)

        return if (module != null) {
            val (function, metadata, arguments) = functionQuoted

            expr(
                    function = expr(
                            function = ".",
                            arguments = otpErlangList(
                                    OtpErlangAtom(module),
                                    function
                            )
                    ),
                    arguments = otpErlangList(
                            metadata,
                            arguments
                    )
            )
        } else {
            functionQuoted
        }
    }

    companion object {
        fun from(attributeValue: OtpErlangObject): Specification? =
                if (attributeValue is OtpErlangTuple && attributeValue.arity() == 2) {
                    val (head, body) = attributeValue
                    from(head, body)
                } else {
                    logger.error("""
                    Function specification attribute value is not a 2-tuple.

                    ```elixir
                    ${inspect(attributeValue)}
                    ```
                    """.trimIndent())
                    null
                }

        private fun from(head: OtpErlangObject, body: OtpErlangObject): Specification? =
                if (head is OtpErlangTuple) {
                    from(head, body)
                } else {
                    logger.error("""
                    -spec/@spec head is not a tuple.

                    ```elixir
                    ${inspect(head)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun from(head: OtpErlangTuple, body: OtpErlangObject): Specification? {
            val headArity = head.arity()

            return when (headArity) {
                2 -> {
                    val (function, arity) = head
                    fromLocal(function, arity, body)
                }
                3 -> {
                    val (module, function, arity) = head
                    fromRemote(module, function, arity, body)
                }
                else -> {
                    logger.error("""
                    -spec/@spec head is not a 2-tuple for a local function or a 3-tuple for remote function.

                    ```elixir
                    ${inspect(head)}
                    ```
                    """.trimIndent())

                    null
                }
            }
        }

        private fun fromLocal(
                function: OtpErlangObject,
                arity: OtpErlangObject,
                body: OtpErlangObject
        ): Specification? =
                if (function is OtpErlangAtom && arity is OtpErlangLong) {
                    Specification(function = function.atomValue(), arity = arity.longValue(), body = body)
                } else {
                    logger.error("""
                    -spec/@spec head 2-tuple is not `{function :: atom(), arity :: integer()}`:

                    ### Function

                    ```elixir
                    ${inspect(function)}
                    ```

                    ### Arity

                    ```elixir
                    ${inspect(arity)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun fromRemote(
                module: OtpErlangObject,
                function: OtpErlangObject,
                arity: OtpErlangObject,
                body: OtpErlangObject
        ): Specification? =
                if (module is OtpErlangAtom && function is OtpErlangAtom && arity is OtpErlangLong) {
                    Specification(
                            module = module.atomValue(),
                            function = function.atomValue(),
                            arity = arity.longValue(),
                            body = body
                    )
                } else {
                    logger.error("""
                    -spec/@spec head 3-tuple is not `{module :: atom(), function :: atom(), arity :: integer()}`:

                    ### Module

                    ```elixir
                    ${inspect(module)}
                    ```

                    ### Function

                    ```elixir
                    ${inspect(function)}
                    ```

                    ### Arity

                    ```elixir
                    ${inspect(arity)}
                    ```
                    """.trimIndent())

                    null
                }
    }
}

data class Type(val name: String, val inputs: OtpErlangList, val output: OtpErlangObject) : TypeSpecification() {
    val arity = inputs.arity()

    fun toString(typeSpecifications: TypeSpecifications): String {
        val moduleAttributeName = if (typeSpecifications.isExported(name, arity)) {
            "type"
        } else {
            "typep"
        }

        return Typic.toQuoted(moduleAttributeName, name, inputs, output).let { Macro.toString(it) }
    }

    companion object {
        fun from(attributeValue: OtpErlangObject): Type? = Typic.from(attributeValue, ::Type)
    }
}

operator fun OtpErlangTuple.component1(): OtpErlangObject = this.elementAt(0)
operator fun OtpErlangTuple.component2(): OtpErlangObject = this.elementAt(1)
operator fun OtpErlangTuple.component3(): OtpErlangObject = this.elementAt(2)
private operator fun OtpErlangTuple.component4(): OtpErlangObject = this.elementAt(3)
