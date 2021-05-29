package org.spoofax.tego.ir

import org.spoofax.tego.aterm.*

/**
 * Builds the Intermediate Representation of an expression.
 */
class IrBuilder {

    /**
     * Transforms a Tego project term into an IR Tego [Project].
     */
    fun toProject(projectTerm: Term): Project {
        // NOTE: There is not a Project() term yet
        require(projectTerm is ApplTerm) { "Expected constructor application term, got: $projectTerm"}

        return Project().apply {
            // TODO: Support multi-file
            this.files.addAll(listOf(toFile(projectTerm)))
        }
    }

    /**
     * Transforms a Tego `File` term into an IR Tego [File].
     */
    fun toFile(fileTerm: Term): File {
        require(fileTerm is ApplTerm && fileTerm.constructor == "File") { "Expected File() term, got: $fileTerm"}

        val modules = fileTerm[0].asList().map { toModule(it) }
        return File().apply {
            this.modules.addAll(modules)
        }
    }

    /**
     * Transforms a Tego `Module` term into an IR Tego [Module].
     */
    fun toModule(moduleTerm: Term): Module {
        require(moduleTerm is ApplTerm && moduleTerm.constructor == "Module") { "Expected Module() term, got: $moduleTerm"}

        val modifiers = toModuleModifiers(moduleTerm[0])
        // TODO: Better way to get the QName from a module name / fix package name
        val name = PackageName(moduleTerm[1].toJavaString())
        val ptrs = listOf(declOf(moduleTerm[1]))
        // We can ignore the imports (module[2])
        val allDecls = moduleTerm[3].asList().mapNotNull { toDecl(it) }

        return Module(name, modifiers, ptrs).apply {
            declarations.addAll(allDecls.filterIsInstance<TypeDecl>())
            definitions.addAll(allDecls.filterIsInstance<Def>())
        }
    }

    /**
     * Transforms a declaration term into an IR declaration.
     */
    fun toDecl(declTerm: Term): Decl? {
        require(declTerm is ApplTerm) { "Expected constructor application term, got: $declTerm"}

        return when (declTerm.constructor) {
            "ValDef" -> /* TODO: Support this. */ null
            "ValDecl" -> /* TODO: Support this. */ null
            "ValDefNoType" -> /* TODO: Support this. */ null

            "StrategyDecl" -> toStrategyDecl(declTerm)
            "StrategyDefWInput" -> toStrategyDef(declTerm)

            "RuleDef" -> /* TODO: Support this. */ null

            "ClassDecl" -> toClassDecl(declTerm)

            else -> TODO("Unsupported declaration: $declTerm")
        }
    }

    /**
     * Transforms a class declaration term into an IR class declaration.
     */
    fun toClassDecl(declTerm: Term): ClassTypeDecl {
        require(declTerm is ApplTerm && declTerm.constructor == "ClassDecl") { "Expected ClassDecl() term, got: $declTerm"}

        // TODO: Fix package name
        val modifiers = toTypeModifiers(declTerm[0])
        val simpleName = declTerm[1].toJavaString()
        val ptrs = listOf(declOf(declTerm[1]))
        return ClassTypeDecl(simpleName, modifiers, ptrs)
    }

    /**
     * Transforms a strategy declaration term into an IR strategy declaration.
     */
    fun toStrategyDecl(declTerm: Term): StrategyTypeDecl {
        require(declTerm is ApplTerm && declTerm.constructor == "StrategyDecl") { "Expected StrategyDecl() term, got: $declTerm"}

        val type = typeOf(declTerm) as StrategyType
        // TODO: Fix package name
        val simpleName = declTerm[1].toJavaString()
        val modifiers = toTypeModifiers(declTerm[0])
        val ptrs = listOf(declOf(declTerm[1]))
        return StrategyTypeDecl(simpleName, type, modifiers, ptrs)
    }

    /**
     * Transforms a strategy definition term into an IR strategy definition.
     */
    fun toStrategyDef(defTerm: Term): StrategyDef {
        require(defTerm is ApplTerm && defTerm.constructor == "StrategyDefWInput") { "Expected StrategyDefWInput() term, got: $defTerm"}

        // TODO: Fix package name
        val simpleName = defTerm[0].toJavaString()
        val params = defTerm[1].asList().map { toParamDef(it) }
        val inputName = defTerm[2].toJavaString()
        val body = toExp(defTerm[3])
        val ptr = refOf(defTerm[0])
        return StrategyDef(simpleName, params, inputName, body, ptr)
    }

    /**
     * Transforms a param definition term into an IR param definition.
     */
    fun toParamDef(paramDefTerm: Term): ParamDef {
        require(paramDefTerm is ApplTerm) { "Expected constructor application term, got: $paramDefTerm"}

        return when (paramDefTerm.constructor) {
            "ParamDef" -> ParamDef(paramDefTerm[0].toJavaString(), toType(paramDefTerm[1]))
            "ParamDefNoType" -> ParamDef(paramDefTerm[0].toJavaString(), null)
            else -> TODO("Unsupported expression: $paramDefTerm")
        }
    }

    /**
     * Transforms an expression term into an IR expression.
     */
    fun toExp(expTerm: Term): Exp {
        require(expTerm is ApplTerm) { "Expected constructor application term, got: $expTerm"}

        val type = typeOf(expTerm)
        return when (expTerm.constructor) {
            "Let" -> Let(expTerm[0].toJavaString(), toExp(expTerm[1]), toExp(expTerm[2]), type)
            "Apply" -> Apply(toExp(expTerm[0]), expTerm[1].asList().map { toExp(it) }, type)
            "Eval" -> Eval(toExp(expTerm[0]), toExp(expTerm[1]), type)
            "Var" -> Var(expTerm[0].toJavaString(), type, refOf(expTerm[0]))

//            "Int" -> IntLit(exp[0].toJavaInt(), type)
//            "String" -> StringLit(exp[0].toJavaString(), type)
//            "Object" -> AnyInst(type)
//            "Id" -> TODO()  // Should be desugared into a val `id`
//            "Build" -> TODO()   // Should be desugared into an eval `<build> v`?
//            "Seq" -> Seq(toExp(exp[0]), toExp(exp[1]), type)
            else -> TODO("Unsupported expression: $expTerm")
        }
    }

    /**
     * Gets the type of a term (its `OfType(_)` annotation)
     */
    private fun typeOf(t: Term): Type {
        val typeTerm = t.annotations["OfType", 1]?.get(0)
            ?: throw IllegalStateException("Expected term to have OfType(), found nothing: $t")
        return toType(typeTerm)
    }

    /**
     * Gets the pointer of a declaration term (its `OfDecl(x{TermIndex(_, _)})` annotation)
     */
    private fun declOf(t: Term): TermIndex {
        val pointerTerm = t.annotations["OfDecl", 1]?.get(0)?.annotations?.get("TermIndex", 2)
            ?: throw IllegalStateException("Expected term to have OfDecl(x{TermIndex(_, _)}) annotation, found nothing: $t")
        return toTermIndex(pointerTerm)
    }

    /**
     * Gets the pointer of a reference term (its `OfRef(x@{TermIndex(_, _)})` annotation)
     */
    private fun refOf(t: Term): TermIndex {
        val pointerTerm = t.annotations["OfRef", 1]?.get(0)?.annotations?.get("TermIndex", 2)
            ?: throw IllegalStateException("Expected term to have OfRef(x@{TermIndex(_, _)}) annotation, found nothing: $t")
        return toTermIndex(pointerTerm)
    }

    /**
     * Gets the pointer of a resolved type (its `TermIndex(_, _)` annotation).
     */
    private fun pointerOf(t: Term): TermIndex {
        val termIndexTerm = t.annotations["TermIndex", 2]
            ?: throw IllegalStateException("Expected term to have TermIndex() annotation, found nothing: $t")
        return toTermIndex(termIndexTerm)
    }

    private fun toTermIndex(termIndexTerm: Term): TermIndex {
        require(termIndexTerm is ApplTerm && termIndexTerm.constructor == "TermIndex") { "Expected TermIndex() term, got: $termIndexTerm"}

        val resource = termIndexTerm[0].toJavaString()
        val index = termIndexTerm[1].toJavaInt()
        return TermIndex(resource, index)
    }

//    private fun termIndicesOf(t: Term): List<TermIndex> {
//        val termIndicesTerm = t.annotations["TermIndices", 1] ?: return emptyList()
//        return termIndicesTerm[0].asList().map { toTermIndex(it) }
//    }

    /**
     * Transforms a type term into an IR type.
     */
    fun toType(typeTerm: Term): Type {
        require(typeTerm is ApplTerm) { "Expected constructor application term, got: $typeTerm"}

        return when (typeTerm.constructor) {
            "BOOL" -> BoolType
            "CHAR" -> CharType

            "BYTE" -> ByteType
            "SHORT" -> ShortType
            "INT" -> IntType
            "LONG" -> LongType

            "UBYTE" -> UByteType
            "USHORT" -> UShortType
            "UINT" -> UIntType
            "ULONG" -> ULongType

            "FLOAT" -> FloatType
            "DOUBLE" -> DoubleType

            "ANY" -> AnyType
            "NOTHING" -> NothingType

            "UNIT" -> UnitType
            "STRING" -> StringType

            "STRATEGY" -> StrategyType(typeTerm[0].asList().map { toType(it) }, toType(typeTerm[1]), toType(typeTerm[2]))
            "TUPLE" -> TupleType(typeTerm[0].asList().map { toType(it) })
            // TODO: Fix package name
            "CLASS" -> ClassTypeRef(typeTerm[0].toJavaString(), pointerOf(typeTerm[0]))
            "LIST" -> ListType(toType(typeTerm[0]))

            "ERROR" -> TypeError("Type error")

            else -> TODO("Unsupported type: $typeTerm")
        }
    }

    fun toTypeModifiers(modsTerm: Term): TypeModifiers {
        require(modsTerm is ListTerm) { "Expected a list term, got: $modsTerm"}

        val ms = TypeModifiers.noneOf(TypeModifier::class.java)
        for (modTerm in modsTerm.asList()) {
            check(modTerm is ApplTerm) { "Expected constructor application term, got: $modTerm"}

            val m = when (modTerm.constructor) {
                "PublicDecl" -> TypeModifier.Public
                "ExternDecl" -> TypeModifier.Extern
                else -> TODO("Unsupported type modifier: $modTerm")
            }
            ms.add(m)
        }
        return ms
    }

    fun toModuleModifiers(modsTerm: Term): ModuleModifiers {
        require(modsTerm is ListTerm) { "Expected a list term, got: $modsTerm"}

        val ms = ModuleModifiers.noneOf(ModuleModifier::class.java)
        for (modTerm in modsTerm.asList()) {
            check(modTerm is ApplTerm) { "Expected constructor application term, got: $modTerm"}

            val m = when (modTerm.constructor) {
                "ExternModule" -> ModuleModifier.Extern
                else -> TODO("Unsupported type modifier: $modTerm")
            }
            ms.add(m)
        }
        return ms
    }
}