package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

import com.ericsson.otp.erlang.*
import org.elixir_lang.beam.chunk.debug_info.logger
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.TypeSpecifications
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.code.Identifier

const val TYPE = "type"

sealed class TypeSpecification {
    companion object {
        private const val FORM_TYPE = "attribute"

        fun argumentsToString(arguments: OtpErlangList): String =
                arguments.elements().withIndex().joinToString(", ") { indexedTypeToString(it) }

        fun defaultString(): String = "term()"

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

        fun toString(type: OtpErlangObject): String? =
                when (type) {
                    is OtpErlangTuple -> toString(type)
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

        fun toString(type: OtpErlangTuple): String? =
                if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "ann_type") {
                    annotatedTypeToString(type.elementAt(2))
                } else if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "atom") {
                    atomToString(type.elementAt(2))
                } else if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "integer") {
                    integerToString(type.elementAt(2))
                } else if (type.arity() == 4 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "op") {
                    opToString(type.elementAt(2), type.elementAt(3))
                } else if (type.arity() == 5 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "op") {
                    opToString(type.elementAt(2), type.elementAt(3), type.elementAt(4))
                } else if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "var") {
                    varToString(type.elementAt(2))
                } else if (type.arity() == 3 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "remote_type") {
                    remoteTypeToString(type.elementAt(2))
                } else if (type.arity() == 4 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "type") {
                    builtinTypeToString(type.elementAt(2), type.elementAt(3))
                } else if (type.arity() == 4 && type.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "user_type") {
                    userTypeToString(type.elementAt(2), type.elementAt(3))
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

        private fun annotatedTypeToString(annotatedType: OtpErlangObject): String? =
                if (annotatedType is OtpErlangList && annotatedType.arity() == 2) {
                    annotatedTypeToString(annotatedType.elementAt(0), annotatedType.elementAt(1))
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

        private fun annotatedTypeToString(annotation: OtpErlangObject, type: OtpErlangObject): String? {
            val annotationString = annotationToString(annotation)
            val typeString = toString(type)

            return if (annotationString != null) {
                "$annotationString :: $typeString"
            } else {
                typeString
            }
        }

        private fun annotationToString(annotation: OtpErlangObject): String? =
                if (annotation is OtpErlangTuple && annotation.arity() == 3 && annotation.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "var") {
                    val variable = annotation.elementAt(2)

                    if (variable is OtpErlangAtom) {
                        variable.atomValue()
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
                    logger.error("""
                    Annotation did not match `{:var, _, _}`.

                    ## annotated type

                    ```
                    ${inspect(annotation)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun atomToString(atom: OtpErlangObject): String? =
                if (atom is OtpErlangAtom) {
                    inspect(atom)
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

        private fun integerToString(integer: OtpErlangObject): String? =
                if (integer is OtpErlangLong) {
                    integer.toString()
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

        private fun opToString(op: OtpErlangObject, operand: OtpErlangObject): String? =
                if (op is OtpErlangAtom) {
                    "${op.atomValue()}${toString(operand) ?: defaultString()}"
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

        private fun opToString(op: OtpErlangObject, left: OtpErlangObject, right: OtpErlangObject): String? =
                if (op is OtpErlangAtom) {
                    "${toString(left) ?: defaultString()} ${op.atomValue()} ${toString(right) ?: defaultString()}"
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

        private fun varToString(variable: OtpErlangObject): String? =
                if (variable is OtpErlangAtom) {
                    variable.atomValue()
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

        private fun remoteTypeToString(remoteType: OtpErlangObject): String? =
                if (remoteType is OtpErlangList && remoteType.arity() == 3) {
                    remoteTypeToString(remoteType.elementAt(0), remoteType.elementAt(1), remoteType.elementAt(2))
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

        private fun remoteTypeToString(
                module: OtpErlangObject,
                function: OtpErlangObject,
                arguments: OtpErlangObject
        ): String? =
                if (module is OtpErlangTuple && module.arity() == 3 && module.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "atom" &&
                        function is OtpErlangTuple && function.arity() == 3 && function.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "atom" &&
                        arguments is OtpErlangList) {
                    moduleNameFunctionNameArgumentsToString(module.elementAt(2), function.elementAt(2), arguments)
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

        private fun moduleNameFunctionNameArgumentsToString(
                moduleName: OtpErlangObject,
                functionName: OtpErlangObject,
                arguments: OtpErlangList
        ): String? =
                if (moduleName is OtpErlangAtom && functionName is OtpErlangAtom) {
                    // `:elixir` module types are built-in types in Elixir, but not Erlang
                    if (moduleName.atomValue() == "elixir") {
                        "${Identifier.inspectAsFunction(functionName)}(${argumentsToString(arguments)})"
                    } else {
                        "${inspect(moduleName)}.${Identifier.inspectAsFunction(functionName)}(${argumentsToString(arguments)})"
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


        private fun indexedTypeToString(indexedType: IndexedValue<OtpErlangObject>): String =
                toString(indexedType.value) ?: "t${indexedType.index}"

        private fun builtinTypeToString(name: OtpErlangObject, arguments: OtpErlangObject): String? =
                if (name is OtpErlangAtom) {
                    when (name.atomValue()) {
                        "constraint" -> constraintToString(arguments)
                        "fun" -> funToString(arguments)
                        "list" -> listToString(arguments)
                        "map" -> mapToString(arguments)
                        "nonempty_list" -> nonEmptyList(arguments)
                        "product" -> productToString(arguments)
                        "range" -> rangeToString(arguments)
                        "tuple" -> tupleToString(arguments)
                        "union" -> unionToString(arguments)
                        else ->
                            if (arguments is OtpErlangList) {
                                "${name.atomValue()}(${argumentsToString(arguments)})"
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

        private fun constraintToString(arguments: OtpErlangObject): String? =
                if (arguments is OtpErlangList && arguments.arity() == 2 && isIsSubtype(arguments.elementAt(0))) {
                    subtypeToString(arguments.elementAt(1))
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

        private fun subtypeToString(subtype: OtpErlangObject): String? =
                if (subtype is OtpErlangList && subtype.arity() == 2) {
                    subtypeToString(subtype.elementAt(0), subtype.elementAt(1))
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

        private fun subtypeToString(`var`: OtpErlangObject, type: OtpErlangObject): String? =
                if (`var` is OtpErlangTuple && `var`.arity() == 3 && `var`.elementAt(0).let { it as OtpErlangAtom }?.atomValue() == "var") {
                    varToString(`var`.elementAt(2))?.let { varString ->
                        val typeString = toString(type) ?: defaultString()

                        "$varString: $typeString"
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

        private fun funToString(arguments: OtpErlangObject): String? =
                if (arguments is OtpErlangList) {
                    funToString(arguments)
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

        private fun funToString(arguments: OtpErlangList): String? =
                when (arguments.arity()) {
                    0 -> "(... -> any())"
                    2 -> "(${toString(arguments.elementAt(0))} -> ${toString(arguments.elementAt(1))})"
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

        private fun listToString(arguments: OtpErlangObject): String? =
                if (arguments is OtpErlangList) {
                    if (arguments.arity() == 0) {
                        "list()"
                    } else {
                        "[${argumentsToString(arguments)}]"
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

        private fun mapToString(arguments: OtpErlangObject): String? =
                if (arguments is OtpErlangAtom && arguments.atomValue() == "any") {
                    "map()"
                } else if (arguments is OtpErlangList) {
                    mapToString(arguments)
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

        private fun mapToString(arguments: OtpErlangList): String? {
            val mapFieldList = arguments.mapNotNull { MapField.from(it) }

            return if (mapFieldList.size == arguments.arity()) {
                if (mapFieldList.any { it is MapFieldOptional || it is MapFieldRequiredNonAtom }) {
                    associationMapToString(mapFieldList)
                } else {
                    val mapFieldStruct = mapFieldList.mapNotNull { it as? MapFieldStruct }.singleOrNull()

                    if (mapFieldStruct != null) {
                        val mapFieldRequiredAtoms =
                                mapFieldList
                                        .filter { it !is MapFieldStruct }
                                        .map { it as MapFieldRequiredAtom }

                        structToString(mapFieldStruct, mapFieldRequiredAtoms)
                    } else {
                        keywordMapToString(mapFieldList.map { it as MapFieldRequiredAtom })
                    }
                }
            } else {
                null
            }
        }

        private fun associationMapToString(associations: List<MapField>): String =
                "%{${associationsToString(associations)}}"

        private fun associationsToString(associations: List<MapField>): String =
                associations.joinToString(", ") { associationToString(it) }

        private fun associationToString(association: MapField): String =
                when (association) {
                    is MapFieldRequiredAtom -> "required(${inspect(association.name)}) => ${toString(association.value)}"
                    is MapFieldStruct -> "required(:__struct__) => ${toString(association.value)}"
                    is MapFieldRequiredNonAtom -> "required(${toString(association.name)}) => ${toString(association.value)}"
                    is MapFieldOptional -> "optional(${toString(association.name)}) => ${toString(association.value)}"
                }

        private fun structToString(mapFieldStruct: MapFieldStruct, mapFieldRequiredAtoms: List<MapFieldRequiredAtom>): String =
                "%${inspect(mapFieldStruct.value)}{${keywordsToString(mapFieldRequiredAtoms)}}"

        private fun keywordsToString(keywords: List<MapFieldRequiredAtom>): String =
                keywords.joinToString(", ") { keywordToString(it) }

        private fun keywordToString(keyword: MapFieldRequiredAtom): String =
                "${Identifier.inspectAsKey(keyword.name)} ${toString(keyword.value)}"

        private fun keywordMapToString(keywords: List<MapFieldRequiredAtom>): String =
                "%{${keywordsToString(keywords)}}"

        private fun nonEmptyList(arguments: OtpErlangObject): String? =
                if (arguments is OtpErlangList) {
                    "[${arguments.joinToString(", ") { toString(it) ?: defaultString() }}, ...]"
                } else {
                    logger.error("""
                    non-empty list elements does not match `list()`.

                    ```elixir
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun productToString(arguments: OtpErlangObject): String? =
                if (arguments is OtpErlangList) {
                    arguments.joinToString(", ") { toString(it) ?: defaultString() }
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

        private fun rangeToString(arguments: OtpErlangObject): String? =
                if (arguments is OtpErlangList && arguments.arity() == 2) {
                    toString(arguments.elementAt(0))?.let { firstString ->
                        toString(arguments.elementAt(1))?.let { lastString ->
                            "$firstString..$lastString"
                        }
                    } ?: "range()"
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

        private fun tupleToString(arguments: OtpErlangObject): String? =
                if (arguments is OtpErlangAtom && arguments.atomValue() == "any") {
                    "tuple()"
                } else if (arguments is OtpErlangList) {
                    "{${argumentsToString(arguments)}}"
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

        private fun unionToString(arguments: OtpErlangObject): String? =
                if (arguments is OtpErlangList) {
                    arguments.joinToString(" | ") { TypeSpecification.toString(it) ?: defaultString() }
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

        private fun userTypeToString(name: OtpErlangObject, arguments: OtpErlangObject): String? =
                if (name is OtpErlangAtom && arguments is OtpErlangList) {
                    "${name.atomValue()}(${argumentsToString(arguments)})"
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
                    "callback" -> fromCallback(attributeValue)
                    "export_type" -> fromExportType(attributeValue)
                    "opaque" -> fromOpaque(attributeValue)
                    "optional_callbacks" -> fromOptionalCallbacks(attributeValue)
                    "spec" -> fromSpec(attributeValue)
                    "type" -> Type.from(attributeValue)
                    else -> {
                        logger.error("Don't know how to convert attribute type $attributeType to TypeSpecification")

                        null
                    }
                }

        private fun fromCallback(attributeValue: OtpErlangObject): Callback? =
                if (attributeValue is OtpErlangTuple && attributeValue.arity() == 2) {
                    fromCallback(attributeValue.elementAt(0), attributeValue.elementAt(1))
                } else {
                    logger.error("""
                    @callback does not match `{_, _}`

                    ```elixir
                    ${attributeValue}
                    ```
                    """.trimIndent())

                    null
                }

        private fun fromCallback(head: OtpErlangObject, body: OtpErlangObject): Callback? =
                if (head is OtpErlangTuple && head.arity() == 2 && body is OtpErlangList) {
                    fromCallback(head.elementAt(0), head.elementAt(1), body)
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

        private fun fromCallback(function: OtpErlangObject, arity: OtpErlangObject, body: OtpErlangList): Callback? =
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

        private fun fromExportType(attributeValue: OtpErlangObject): ExportType? {
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

        private fun fromOpaque(attributeValue: OtpErlangObject): Opaque? =
                if (attributeValue is OtpErlangTuple && attributeValue.arity() == 3) {
                    fromOpaque(attributeValue.elementAt(0), attributeValue.elementAt(1), attributeValue.elementAt(2))
                } else {
                    logger.error("""
                    @opaque argument does not match `list()`.

                    ## arguments

                    ```elixir
                    ${inspect(attributeValue)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun fromOpaque(attributeValue: OtpErlangTuple): Opaque? {
            TODO()
        }

        private fun fromOptionalCallbacks(attributeValue: OtpErlangObject): OptionalCallback? =
                if (attributeValue is OtpErlangList) {
                    fromOptionalCallbacks(attributeValue)
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

        private fun fromOptionalCallbacks(attributeValue: OtpErlangList): OptionalCallback =
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

        private fun fromSpec(attributeValue: OtpErlangObject): Specification? =
                if (attributeValue is OtpErlangTuple && attributeValue.arity() == 2) {
                    val (head, body) = attributeValue
                    fromSpec(head, body)
                } else {
                    logger.error("""
                                 Function specification attribute value is not a 2-tuple.

                                 ```elixir
                                 ${inspect(attributeValue)}
                                 ```
                                 """.trimIndent())
                    null
                }

        private fun fromSpec(head: OtpErlangObject, body: OtpErlangObject): Specification? =
                if (head is OtpErlangTuple) {
                    fromSpec(head, body)
                } else {
                    logger.error("""
                                 -spec/@spec head is not a tuple.

                                 ```elixir
                                 ${inspect(head)}
                                 ```
                                 """.trimIndent())

                    null
                }

        private fun fromSpec(head: OtpErlangTuple, body: OtpErlangObject): Specification? {
            val arity = head.arity()

            return when (arity) {
                2 -> {
                    val (function, arity) = head
                    fromLocalSpec(function, arity, body)
                }
                3 -> {
                    val (module, function, arity) = head
                    fromRemoteSpec(module, function, arity, body)
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

        private fun fromLocalSpec(
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

        private fun fromRemoteSpec(
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

object Function {
    fun toStringList(function: String, arity: Long, body: OtpErlangObject): List<String> =
            bodyToStringList(function, body) ?: listOf(arityFunctionString(function, arity))

    private fun bodyToStringList(function: String, body: OtpErlangObject): List<String>? =
            if (body is OtpErlangList) {
                val bodyStrings = body.mapNotNull { bodyElement -> bodyElementToString(function, bodyElement) }

                if (bodyStrings.isNotEmpty()) {
                    bodyStrings
                } else {
                    null
                }
            } else {
                logger.error("""
                body does not match `[_]`.

                ## body

                ```elixir
                ${inspect(body)}
                ```
                """.trimIndent())

                null
            }

    private fun bodyElementToString(function: String, bodyElement: OtpErlangObject): String? =
            if (bodyElement is OtpErlangTuple && bodyElement.arity() == 4 && bodyElement.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "type") {
                bodyElementToString(function, bodyElement.elementAt(2), bodyElement.elementAt(3))
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

    private fun bodyElementToString(function: String, typeType: OtpErlangObject, typeValue: OtpErlangObject): String? =
            if (typeType is OtpErlangAtom) {
                bodyElementTotring(function, typeType, typeValue)
            } else {
                logger.error("""
                @callback body list element form type type does not match `atom()`.

                ## body list element form type

                ```
                ${inspect(typeType)}
                ```
                """.trimIndent())

                null
            }

    private fun bodyElementTotring(function: String, typeType: OtpErlangAtom, typeValue: OtpErlangObject): String? =
            when (typeType.atomValue()) {
                "bounded_fun" -> boundedFunToString(function, typeValue)
                "fun" -> funToString(function, typeValue)
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

    private fun boundedFunToString(function: String, typeValue: OtpErlangObject): String? =
            if (typeValue is OtpErlangList && typeValue.arity() == 2) {
                boundedFunToString(function, typeValue.elementAt(0), typeValue.elementAt(1))
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

    private fun boundedFunToString(function: String, `fun`: OtpErlangObject, constraints: OtpErlangObject): String? =
            bodyElementToString(function, `fun`)?.let { bodyElementString ->
                constraintsToString(constraints)?.let { constraintsString ->
                    "$bodyElementString when $constraintsString"
                } ?: bodyElementString
            }

    private fun constraintsToString(constraints: OtpErlangObject): String? =
            if (constraints is OtpErlangList) {
                constraintsToString(constraints)
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

    private fun constraintsToString(constraints: OtpErlangList): String =
            constraints.mapNotNull { TypeSpecification.toString(it) }.joinToString(", ")

    private fun funToString(function: String, `fun`: OtpErlangObject): String? =
            if (`fun` is OtpErlangList && `fun`.arity() == 2) {
                funToString(function, `fun`.elementAt(0), `fun`.elementAt(1))
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

    private fun funToString(function: String, input: OtpErlangObject, output: OtpErlangObject): String =
            "$function(${TypeSpecification.toString(input)}) :: ${TypeSpecification.toString(output)}"

    private fun arityFunctionString(function: String, arity: Long): String =
            "$function(${arityInputString(arity)}) :: ${TypeSpecification.defaultString()}"

    private fun arityInputString(arity: Long): String =
            (0 until arity.toInt()).joinToString(", ", transform = this::parameterString)

    private fun parameterString(index: Int): String = "p${index + 1}"
}

data class Callback(val function: String, val arity: Long, val body: OtpErlangObject) : TypeSpecification() {
    override fun toString(): String = _string

    private val _string: String by lazy {
        Function.toStringList(function, arity, body).joinToString("\n") {
            "@callback $it"
        }
    }
}

data class ExportType(val name: String, val arity: Int) : TypeSpecification()

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

    fun toString(moduleAttributeName: String, name: String, inputs: OtpErlangList, val output: OtpErlangObject): String {
        val inputsString = TypeSpecification.argumentsToString(inputs)
        val outputString = TypeSpecification.toString(output)

        return "@$moduleAttributeName $name($inputsString) :: $outputString"
    }
}

data class Opaque(val name: String, val inputs: OtpErlangList, val output: OtpErlangObject) : TypeSpecification() {
    val arity = inputs.arity()

    override fun toString(): String =
            Typic.toString("@opaque", name, inputs, output)

    companion object {
        fun from(attributeValue: OtpErlangObject): Opaque? = Typic.from(attributeValue, ::Opaque)
    }
}

data class OptionalCallback(val name: String, val arity: Int) : TypeSpecification()

data class Type(val name: String, val inputs: OtpErlangList, val output: OtpErlangObject) : TypeSpecification() {
    val arity = inputs.arity()

    fun toString(typeSpecifications: TypeSpecifications): String {
        val moduleAttributeName = if (typeSpecifications.isExported(name, arity)) {
            "type"
        } else {
            "typep"
        }

        return Typic.toString(moduleAttributeName, name, inputs, output)
    }

    companion object {
        fun from(attributeValue: OtpErlangObject): Type? = Typic.from(attributeValue, ::Type)
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

    private val _string: String by lazy {
        val modulePrefixString = modulePrefixString()
        Function.toStringList(function, arity, body).joinToString("\n") {
            "@spec $modulePrefixString$it"
        }
    }

    private fun modulePrefixString(): String =
            module?.let { ":$it." }
                    ?: ""
}

operator fun OtpErlangTuple.component1(): OtpErlangObject = this.elementAt(0)
operator fun OtpErlangTuple.component2(): OtpErlangObject = this.elementAt(1)
operator fun OtpErlangTuple.component3(): OtpErlangObject = this.elementAt(2)
private operator fun OtpErlangTuple.component4(): OtpErlangObject = this.elementAt(3)
