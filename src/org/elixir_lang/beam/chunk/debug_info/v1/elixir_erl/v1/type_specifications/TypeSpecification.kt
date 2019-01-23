package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.type_specifications

import com.ericsson.otp.erlang.*
import org.elixir_lang.beam.chunk.debug_info.logger
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.TypeSpecifications
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.code.Identifier

const val SPEC = "spec"
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
                    "${inspect(moduleName)}.${Identifier.inspectAsFunction(functionName)}(${argumentsToString(arguments)})"
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
                if (name is OtpErlangAtom && arguments is OtpErlangList) {
                    when (name.atomValue()) {
                        "fun" -> funToString(arguments)
                        "list" -> listToString(arguments)
                        "product" -> productToString(arguments)
                        "tuple" -> tupleToString(arguments)
                        "union" -> unionToString(arguments)
                        else -> "${name.atomValue()}(${argumentsToString(arguments)})"
                    }
                } else {
                    logger.error("""
                    type does not match `{:type, _, name :: atom(), arguments :: list()}`

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

        private fun funToString(arguments: OtpErlangList): String? =
                if (arguments.arity() == 2) {
                    "(${toString(arguments.elementAt(0))} -> ${toString(arguments.elementAt(1))})"
                } else {
                    logger.error("""
                    fun does not match `[_, _]`.

                    ## arguments

                    ```
                    ${inspect(arguments)}
                    ```
                    """.trimIndent())

                    null
                }

        private fun listToString(arguments: OtpErlangList): String =
                if (arguments.arity() == 0) {
                    "list()"
                } else {
                    "[${argumentsToString(arguments)}]"
                }

        private fun productToString(arguments: OtpErlangList): String =
                arguments.joinToString(", ") { toString(it) ?: defaultString() }

        private fun tupleToString(arguments: OtpErlangList): String =
                "{${argumentsToString(arguments)}}"

        private fun unionToString(arguments: OtpErlangList): String =
                arguments.joinToString(" | ") { toString(it) ?: defaultString() }

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
                    "export_type" -> fromExportType(attributeValue)
                    "spec" -> fromSpec(attributeValue)
                    "type" -> Type.from(attributeValue)
                    else -> {
                        logger.error("Don't know how to convert attribute type $attributeType to TypeSpecification")

                        null
                    }
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
                    Specification(moduleAttributeName = SPEC, function = function.atomValue(), arity = arity.longValue(), body = body)
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
                            moduleAttributeName = SPEC,
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

data class ExportType(val name: String, val arity: Int) : TypeSpecification()

data class Type(val name: String, val inputs: OtpErlangList, val output: OtpErlangObject) : TypeSpecification() {
    val arity = inputs.arity()

    fun toString(typeSpecifications: TypeSpecifications): String {
        val moduleAttributeName = if (typeSpecifications.isExported(name, arity)) {
            "type"
        } else {
            "typep"
        }

        val inputsString = TypeSpecification.argumentsToString(inputs)
        val outputString = TypeSpecification.toString(output)

        return "@$moduleAttributeName $name($inputsString) :: $outputString"
    }

    companion object {
        fun from(attributeValue: OtpErlangObject): Type? {
            val type = if (attributeValue is OtpErlangTuple && attributeValue.arity() == 3) {
                val name = attributeValue.elementAt(0).let { it as? OtpErlangAtom }?.atomValue()
                val output = attributeValue.elementAt(1)
                val inputs = attributeValue.elementAt(2).let { it as? OtpErlangList }

                if (name != null && inputs != null) {
                    Type(name, inputs, output)
                } else {
                    null
                }
            } else {
                null
            }

            if (type == null) {
                logger.error("""
                -type does not match `{name :: atom(), output :: term(), inputs :: list()}`

                ## type

                ```
                ${inspect(attributeValue)}
                ```
                """.trimIndent())
            }

            return type
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
        val moduleAttributeName: String,
        val module: String? = null,
        val function: String,
        val arity: Long,
        val body: OtpErlangObject
) : TypeSpecification() {
    override fun toString(): String = _string

    private val _string: String by lazy {
        "@spec ${modulePrefixString()}${bodyString()}"
    }

    private fun modulePrefixString(): String =
            module?.let { ":$it." }
                    ?: ""

    private fun bodyString(): String =
            when (body) {
                is OtpErlangList -> bodyString(body)
                else -> {
                    logger.error("""
                @spec body is not a list.

                ## body

                ```
                ${inspect(body)}
                ```
                """.trimIndent())

                    arityFunctionString()
                }
            }

    private fun bodyString(body: OtpErlangList): String {
        val bodyArity = body.arity()

        return if (bodyArity == 1) {
            bodyElementToString(body.elementAt(0))
        } else {
            logger.error("""
            @spec body list arity is $bodyArity, instead of 1.

            ## body

            ```
            ${inspect(body)}
            ```
            """.trimIndent())

            arityFunctionString()
        }
    }

    private fun bodyElementToString(bodyElement: OtpErlangObject): String =
            when (bodyElement) {
                is OtpErlangTuple -> bodyElementToString(bodyElement)
                else -> {
                    logger.error("""
                @spec body list element is not a tuple.

                ## body list element

                ```
                ${inspect(bodyElement)}
                ```
                """.trimIndent())

                    arityFunctionString()
                }
            }

    private fun bodyElementToString(bodyElement: OtpErlangTuple): String {
        val arity = bodyElement.arity()

        return if (arity == 4) {
            bodyElementToString(bodyElement.elementAt(0), bodyElement.elementAt(2), bodyElement.elementAt(3))
        } else {
            logger.error("""
            @spec body list element tuple arity is not 4.

            ## body list element

            ```
            ${inspect(bodyElement)}
            ```
            """.trimIndent())

            arityFunctionString()
        }
    }

    private fun bodyElementToString(
            formType: OtpErlangObject,
            typeType: OtpErlangObject,
            typeValue: OtpErlangObject
    ): String =
            if (formType is OtpErlangAtom && formType.atomValue() == "type") {
                bodyElementToString(typeType, typeValue)
            } else {
                logger.error("""
            @spec body list element form type is not `:type`.

            ## formType

            ```
            ${inspect(formType)}
            ```
            """.trimIndent())

                arityFunctionString()
            }

    private fun bodyElementToString(typeType: OtpErlangObject, typeValue: OtpErlangObject): String =
            if (typeType is OtpErlangAtom && typeType.atomValue() == "fun") {
                typeValueToString(typeValue)
            } else {
                logger.error("""
            @spec body list element form type type is not `:fun`.

            ## body list element form type

            ```
            ${inspect(typeType)}
            ```
            """.trimIndent())

                arityFunctionString()
            }

    private fun typeValueToString(typeValue: OtpErlangObject): String =
            if (typeValue is OtpErlangList && typeValue.arity() == 2) {
                typeValueToString(typeValue.elementAt(0), typeValue.elementAt(1))
            } else {
                logger.error("""
                @spec body list element type value is not a 2-element list.

                ## type value

                ```
                ${inspect(typeValue)}
                ```
                """.trimIndent())

                arityFunctionString()
            }

    private fun typeValueToString(input: OtpErlangObject, output: OtpErlangObject): String =
            "$function(${inputToString(input)}) :: ${TypeSpecification.toString(output)}"

    private fun inputToString(input: OtpErlangObject): String =
            if (input is OtpErlangTuple && input.arity() == 4 && input.elementAt(0).let { it as? OtpErlangAtom }?.atomValue() == "type" && input.elementAt(2).let { it as? OtpErlangAtom }?.atomValue() == "product" && input.elementAt(3).let { it as? OtpErlangList } != null) {
                TypeSpecification.argumentsToString(input.elementAt(3) as OtpErlangList)
            } else {
                logger.error("""
                input type does not match `{:type, _, :product, list()}`

                ## input

                ```
                ${inspect(input)}
                ```
                """.trimIndent())

                arityInputString()
            }

    private fun arityFunctionString(): String = "$function(${arityInputString()}) :: ${TypeSpecification.defaultString()}"

    private fun arityInputString(): String =
            (0 until arity.toInt()).joinToString(", ", transform = this@Specification::parameterString)

    private fun parameterString(index: Int): String = "p${index + 1}"
}

operator fun OtpErlangTuple.component1(): OtpErlangObject = this.elementAt(0)
operator fun OtpErlangTuple.component2(): OtpErlangObject = this.elementAt(1)
operator fun OtpErlangTuple.component3(): OtpErlangObject = this.elementAt(2)
private operator fun OtpErlangTuple.component4(): OtpErlangObject = this.elementAt(3)
