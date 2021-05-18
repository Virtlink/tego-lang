package org.spoofax.tego.ir

import org.spoofax.tego.aterm.*

/**
 * Builds the Intermediate Representation of an expression.
 */
class IrBuilder {

    /**
     * Transforms a Tego project term into an IR Tego project.
     */
    fun toProject(project: Term): Project {
        require(project is ApplTerm) { "Expected constructor application term, got: $project"}

        return Project(
            // TODO: Support multi-module
            listOf(toModule(project))
        )
    }

    /**
     * Transforms a Tego module term into an IR Tego module.
     */
    fun toModule(module: Term): Module {
        require(module is ApplTerm && module.constructor == "Module") { "Expected Module() term, got: $module"}

        val name: QName = module[0].let { moduleDecl: Term ->
            require(moduleDecl is ApplTerm && moduleDecl.constructor == "ModuleDecl") { "Expected ModuleDecl() term, got: $moduleDecl"}
            // TODO: Better way to get the QName from a module name / fix package name
            QName("tego", moduleDecl[0].toJavaString())
        }

        val allDecls = module[1].asList().mapNotNull { toDecl(it) }
        val decls = allDecls.filterIsInstance<TypeDecl>()
        val defs = allDecls.filterIsInstance<Def>()

        return Module(name, decls, defs)
    }

    /**
     * Transforms a declaration term into an IR declaration.
     */
    fun toDecl(decl: Term): Decl? {
        require(decl is ApplTerm) { "Expected constructor application term, got: $decl"}

        return when (decl.constructor) {
            "ValDef" -> /* TODO: Support this. */ null
            "ValDecl" -> /* TODO: Support this. */ null
            "ValDefNoType" -> /* TODO: Support this. */ null

            "StrategyDecl" -> toStrategyDecl(decl)
            "StrategyDefWInput" -> toStrategyDef(decl)

            "RuleDef" -> /* TODO: Support this. */ null

            "ClassDecl" -> toClassDecl(decl)

            else -> TODO("Unsupported declaration: $decl")
        }
    }

    /**
     * Transforms a class declaration term into an IR class declaration.
     */
    fun toClassDecl(decl: Term): ClassTypeDecl {
        require(decl is ApplTerm && decl.constructor == "ClassDecl") { "Expected ClassDecl() term, got: $decl"}

        // TODO: Fix package name
        return ClassTypeDecl(QName("tego", decl[1].toJavaString()), toTypeModifiers(decl[0]))
    }

    /**
     * Transforms a strategy declaration term into an IR strategy declaration.
     */
    fun toStrategyDecl(decl: Term): StrategyTypeDecl {
        require(decl is ApplTerm && decl.constructor == "StrategyDecl") { "Expected StrategyDecl() term, got: $decl"}

        val type = typeOf(decl) as StrategyType
        // TODO: Fix package name
        return StrategyTypeDecl(QName("tego", decl[1].toJavaString()), type, toTypeModifiers(decl[0]))
    }

    /**
     * Transforms a strategy definition term into an IR strategy definition.
     */
    fun toStrategyDef(def: Term): StrategyDef {
        require(def is ApplTerm && def.constructor == "StrategyDefWInput") { "Expected StrategyDefWInput() term, got: $def"}

        // TODO: Fix package name
        return StrategyDef(QName("tego", def[0].toJavaString()), def[1].asList().map { toParamDef(it) }, def[2].toJavaString(), toExp(def[3]))
    }

    /**
     * Transforms a param definition term into an IR param definition.
     */
    fun toParamDef(paramDef: Term): ParamDef {
        require(paramDef is ApplTerm) { "Expected constructor application term, got: $paramDef"}

        return when (paramDef.constructor) {
            "ParamDef" -> ParamDef(paramDef[0].toJavaString(), toType(paramDef[1]))
            "ParamDefNoType" -> ParamDef(paramDef[0].toJavaString(), null)
            else -> TODO("Unsupported expression: $paramDef")
        }
    }

    /**
     * Transforms an expression term into an IR expression.
     */
    fun toExp(exp: Term): Exp {
        require(exp is ApplTerm) { "Expected constructor application term, got: $exp"}

        val type = typeOf(exp)
        return when (exp.constructor) {
            "Let" -> Let(exp[0].toJavaString(), toExp(exp[1]), toExp(exp[2]), type)
            "Apply" -> Apply(toExp(exp[0]), exp[1].asList().map { toExp(it) }, type)
            "Eval" -> Eval(toExp(exp[0]), toExp(exp[1]), type)
            "Var" -> Var(exp[0].toJavaString(), type)

//            "Int" -> IntLit(exp[0].toJavaInt(), type)
//            "String" -> StringLit(exp[0].toJavaString(), type)
//            "Object" -> AnyInst(type)
//            "Id" -> TODO()  // Should be desugared into a val `id`
//            "Build" -> TODO()   // Should be desugared into an eval `<build> v`?
//            "Seq" -> Seq(toExp(exp[0]), toExp(exp[1]), type)
            else -> TODO("Unsupported expression: $exp")
        }
    }

    private fun typeOf(t: Term): Type {
        return toType(t.annotations["OfType", 1]?.get(0))
    }

    /**
     * Transforms a type term into an IR type.
     */
    fun toType(type: Term?): Type {
        requireNotNull(type) { "Expected a type, not nothing." }
        require(type is ApplTerm) { "Expected constructor application term, got: $type"}

        return when (type.constructor) {
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

            "STRATEGY" -> StrategyType(type[0].asList().map { toType(it) }, toType(type[1]), toType(type[2]))
            "TUPLE" -> TupleType(type[0].asList().map { toType(it) })
            // TODO: Fix package name
            "CLASS" -> ClassTypeRef(QName("tego", type[0].toJavaString()))
            "LIST" -> ListType(toType(type[0]))

            "ERROR" -> TypeError("Type error")

            else -> TODO("Unsupported type: $type")
        }
    }

    fun toTypeModifiers(mods: Term): TypeModifiers {
        require(mods is ListTerm) { "Expected a list term, got: $mods"}

        val ms = TypeModifiers.noneOf(TypeModifier::class.java)
        for (modTerm in mods.asList()) {
            check(modTerm is ApplTerm) { "Expected constructor application term, got: $modTerm"}

            val m = when (modTerm.constructor) {
                "Extern" -> TypeModifier.Extern
                else -> TODO("Unsupported type modifier: $modTerm")
            }
            ms.add(m)
        }
        return ms
    }
}