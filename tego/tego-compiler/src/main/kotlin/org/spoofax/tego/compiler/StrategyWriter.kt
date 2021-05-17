package org.spoofax.tego.compiler

import com.virtlink.kasm.*
import com.virtlink.kasm.JvmType.Companion.asRawType
import org.spoofax.tego.ir.Exp
import org.spoofax.tego.ir.StrategyDef
import org.spoofax.tego.ir.StrategyTypeDecl
import java.util.*

/**
 * Writes a strategy to Java bytecode.
 */
class StrategyWriter(
    private val typeManager: JvmTypeManager,
    private val expWriter: ExpWriter,
) {

    /**
     * Writes the strategy to a class.
     *
     * Multiple definitions are merged into one, like in Stratego.
     *
     * @param decl the strategy's declaration
     * @param def the strategy's definition
     * @return the compiled JVM class
     */
    fun writeStrategy(decl: StrategyTypeDecl, def: StrategyDef): JvmClass {
        val declJvmType: JvmType = typeManager[decl.type]
        val strategyJvmClassSignature: JvmClassSignature = typeManager.getJvmClassSignature(decl)

        val strategy0JvmType: JvmType = JvmType.fromDescriptor("Lcom/virtlink/tego/strategies/Strategy;")
        val glcStrategyJvmType: JvmType = JvmType.fromDescriptor("Lcom/virtlink/tego/runtime/IfStrategy;")
        val idStrategyJvmType: JvmType = JvmType.fromDescriptor("Lcom/virtlink/tego/runtime/IdStrategy;")

        val evalJvmMethodSignature: JvmMethodSignature = typeManager.getEvalJvmMethodSignature(decl.type)

        val classWriter = ClassFileBuilder.`class`(
            ClassModifier.Public or ClassModifier.Final or ClassModifier.Super,
            declJvmType, strategyJvmClassSignature, strategyJvmClassSignature.superClass, strategyJvmClassSignature.superInterfaces
        ) {
            // @Nullable public T eval(@NotNull CTX ctx, .., @NotNull T input) {
            //     if (ctx == null) throw new NullPointerException("");
            //     ..
            //     if (input == null) throw new NullPointerException("");
            //
            //     ..
            // }
            method(EnumSet.of(MethodModifier.Public), "eval", evalJvmMethodSignature, head = {
                annotateMethod(JvmTypes.Nullable, false)
                annotateParameter(0, JvmTypes.NotNull, false)
                annotateParameter(1, JvmTypes.NotNull, false)
                annotateParameter(2, JvmTypes.NotNull, false)
            }) {
                val `this` = localVar("this", declJvmType)
                val ctx = localVar("ctx", JvmTypes.Object)
                val params = (def.paramNames zip decl.type.paramTypes).map { (name, type) ->
                    localVar(name, typeManager[type])
                }
                val input = localVar(def.inputName, typeManager[decl.type.inputType])

                requireNotNull(ctx, declJvmType, "eval")
                params.forEach { p: LocalVar -> requireNotNull(p, declJvmType, "eval") }
                requireNotNull(input, declJvmType, "eval")

                expWriter.apply {
                    writeExp(def.body, Environment.empty().withAll(
                        `this`, ctx, *params.toTypedArray(), input
                    ))
                }
                aReturn()
            }

            // public bridge synthetic Object eval(Object ctx, Object arg1, Object input) {
            //     return this.eval(ctx, arg1, input);
            // }
            method(
                MethodModifier.Public or MethodModifier.Bridge or MethodModifier.Synthetic,
                "eval", typeManager.getEvalJvmBridgeMethodSignature(decl.type)
            ) {
                val `this` = localVar("this", declJvmType)
                val ctx = localVar("ctx", JvmTypes.Object)
                val params = decl.type.paramTypes.mapIndexed { i, type ->
                    localVar("arg${i + 1}", typeManager[type].asRawType())
                }
                val input = localVar("input", typeManager[decl.type.inputType].asRawType())

                aLoad(`this`)
                aLoad(ctx)
                params.forEach { p: LocalVar ->
                    aLoad(p)
                }
                aLoad(input)
                invokeVirtual(declJvmType, "eval", evalJvmMethodSignature)
                aReturn()
            }

            // public String getName() {
            //     return "try";
            // }
            method(
                EnumSet.of(MethodModifier.Public),
                "getName",
                JvmMethodSignature.of(JvmTypes.String, emptyList()),
                head = {
                    annotateMethod(JvmTypes.NotNull, false)
                }) {
                ldc("try")
                aReturn()
            }

            // public String toString() {
            //     StringBuilder sb = new StringBuilder();
            //     String name = this.getName();
            //     sb.append(name);
            //     sb.append("(..)");
            //     return sb.toString();
            // }
            method(
                EnumSet.of(MethodModifier.Public),
                "toString",
                JvmMethodSignature.of(JvmTypes.String, emptyList()),
                head = {
                    annotateMethod(JvmTypes.NotNull, false)
                }) {
                val `this` = localVar("this", declJvmType)
                val stringBuilderType = JvmType.of(StringBuilder::class.java)
                new(stringBuilderType)
                dup()
                invokeConstructor(stringBuilderType, JvmMethodSignature.of(JvmType.Void, emptyList()))
                aLoad(`this`)
                invokeVirtual(declJvmType, "getName", JvmMethodSignature.of(JvmTypes.String, emptyList()))
                invokeVirtual(
                    stringBuilderType,
                    "append",
                    JvmMethodSignature.of(stringBuilderType, listOf(JvmTypes.String))
                )
                ldc("(..)")
                invokeVirtual(
                    stringBuilderType,
                    "append",
                    JvmMethodSignature.of(stringBuilderType, listOf(JvmTypes.String))
                )
                invokeVirtual(stringBuilderType, "toString", JvmMethodSignature.of(JvmTypes.String, emptyList()))
                aReturn()
            }

            // private MyStrategy() {
            //     this.super();
            // }
            constructor(EnumSet.of(MethodModifier.Private), JvmMethodSignature.of(JvmType.Void, emptyList())) {
                val `this` = localVar("this", declJvmType)
                // this.super()
                aLoad(`this`)
                invokeConstructor(JvmTypes.Object, JvmMethodSignature.of(JvmType.Void, emptyList()))
                // return
                `return`()
            }

            // MyStrategy {
            //     this.instance = new MyStrategy();
            // }
            constructorStatic {
                val l0 = lineNumber(8)
                new(declJvmType)
                dup()
                invokeConstructor(declJvmType, JvmMethodSignature.of(JvmType.Void, emptyList()))
                putStatic(declJvmType, "instance", declJvmType)
                `return`()
            }

            // private static final MyStrategy instance;
            field(
                FieldModifier.Private or FieldModifier.Final or FieldModifier.Static,
                "instance",
                declJvmType,
                JvmFieldSignature.of(declJvmType.asRawType()),
                null
            )

            // @NotNull public static final getInstance() {
            //     return instance;
            // }
            method(
                MethodModifier.Public or MethodModifier.Static or MethodModifier.Final, "getInstance",
                JvmMethodSignature.of(declJvmType, emptyList()), head = {
                    annotateMethod(JvmTypes.NotNull, false)
                }) {
                getStatic(declJvmType, "instance", declJvmType)
                aReturn()
            }
        }

        val cls = JvmClass.fromType(declJvmType, classWriter.toByteArray())
        cls.check()
        return cls
    }

    /**
     * Requires that an argument is not `null`.
     *
     * @param v the local variable for the argument
     * @param cls the class that contains the method (for debugging)
     * @param methodName the name of the method (for debugging)
     */
    private fun ScopeBuilder.requireNotNull(v: LocalVar, cls: JvmType, methodName: String) {
        // if (v == null) throw new NullPointerException("...");
        val lblNotNull = newLabel()
        aLoad(v)
        ifNonNull(lblNotNull)
        new(JvmTypes.NullPointerException)
        dup()
        ldc("Non-null parameter is null: method $cls.$methodName, parameter ${v.name ?: "<unnamed>"}")
        invokeConstructor(
            JvmTypes.NullPointerException,
            JvmMethodSignature.of(JvmType.Void, listOf(JvmTypes.String))
        )
        aThrow()
        label(lblNotNull)
    }
}