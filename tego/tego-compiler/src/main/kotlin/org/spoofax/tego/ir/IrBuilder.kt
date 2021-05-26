package org.spoofax.tego.ir

import org.spoofax.tego.aterm.*

/**
 * Builds the Intermediate Representation of an expression.
 */
class IrBuilder {

    /**
     * Transforms a Tego project term into an IR Tego [Project].
     */
    fun toProject(project: Term): Project {
        // NOTE: There is not a Project() term yet
        require(project is ApplTerm) { "Expected constructor application term, got: $project"}

        return Project(
            // TODO: Support multi-file
            listOf(toFile(project))
        )
    }

    /**
     * Transforms a Tego `File` term into an IR Tego [File].
     */
    fun toFile(file: Term): File {
        require(file is ApplTerm && file.constructor == "File") { "Expected File() term, got: $file"}

        val modules = file[0].asList().map { toModule(it) }
        return File(modules)
    }

    /**
     * Transforms a Tego `Module` term into an IR Tego [Module].
     */
    fun toModule(module: Term): Module {
        require(module is ApplTerm && module.constructor == "Module") { "Expected Module() term, got: $module"}

        val modifiers = toModuleModifiers(module[0])
        // TODO: Better way to get the QName from a module name / fix package name
        val name = PackageName(module[1].toJavaString())
        // We can ignore the imports (module[2])
        val allDecls = module[3].asList().mapNotNull { toDecl(it) }
        val decls = allDecls.filterIsInstance<TypeDecl>()
        val defs = allDecls.filterIsInstance<Def>()

        return Module(name, decls, defs, modifiers)
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
        val modifiers = toTypeModifiers(decl[0])
        val name = QName(PackageName("tego"), decl[1].toJavaString())
        return ClassTypeDecl(name, modifiers)
    }

    /**
     * Transforms a strategy declaration term into an IR strategy declaration.
     */
    fun toStrategyDecl(decl: Term): StrategyTypeDecl {
        require(decl is ApplTerm && decl.constructor == "StrategyDecl") { "Expected StrategyDecl() term, got: $decl"}

        val type = typeOf(decl) as StrategyType
        // TODO: Fix package name
        val name = QName(PackageName("tego"), decl[1].toJavaString())
        val modifiers = toTypeModifiers(decl[0])
        return StrategyTypeDecl(name, type, modifiers)
    }

    /**
     * Transforms a strategy definition term into an IR strategy definition.
     */
    fun toStrategyDef(def: Term): StrategyDef {
        require(def is ApplTerm && def.constructor == "StrategyDefWInput") { "Expected StrategyDefWInput() term, got: $def"}

        // TODO: Fix package name
        val name = QName(PackageName("tego"), def[0].toJavaString())
        val params = def[1].asList().map { toParamDef(it) }
        val inputName = def[2].toJavaString()
        val body = toExp(def[3])
        return StrategyDef(name, params, inputName, body)
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
            "CLASS" -> ClassTypeRef(QName(PackageName("tego"), type[0].toJavaString()))
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
                "PublicDecl" -> TypeModifier.Public
                "ExternDecl" -> TypeModifier.Extern
                else -> TODO("Unsupported type modifier: $modTerm")
            }
            ms.add(m)
        }
        return ms
    }

    fun toModuleModifiers(mods: Term): ModuleModifiers {
        require(mods is ListTerm) { "Expected a list term, got: $mods"}

        val ms = ModuleModifiers.noneOf(ModuleModifier::class.java)
        for (modTerm in mods.asList()) {
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