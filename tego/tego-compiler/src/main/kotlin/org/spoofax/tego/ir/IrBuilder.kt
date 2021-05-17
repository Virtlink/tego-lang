package org.spoofax.tego.ir

import org.spoofax.tego.aterm.*

/**
 * Builds the Intermediate Representation of an expression.
 */
class IrBuilder {

    /*
    StrategyDecl(
      []
    , "complete"
    , []
    , [TypeRef("Var")]
    , TypeRef("SolverState")
    , ListType(TypeRef("SolverState"))
    )
  , StrategyDefWInput(
      "complete"
    , [ParamDefNoType("v")]
    , "__input"
    , Let("x_x1", Apply(Var("expandDeterministic"), [Var("v")])
    , Let("x_x1", Apply(Var("flatMap"), [Var("x_x1")])
    , Let("x_x1", Apply(Var("expandAllQueries"), [Var("v")])
    , Let("x_x1", Apply(Var("flatMap"), [Var("x_x1")])
    , Let("x_x1", Apply(Var("expandAllInjections"), [Var("v")])
    , Let("x_x1", Apply(Var("flatMap"), [Var("x_x1")])
    , Let("x_x1", Apply(Var("expandAllPredicates"), [Var("v")])
    , Let("x_x1", Eval(Var("x_x1"), Var("__input"))
    , Let("x_x1", Eval(Var("x_x1"), Var("x_x1"))
    , Let("x_x1", Eval(Var("x_x1"), Var("x_x1"))
    , Eval(Var("x_x1"), Var("x_x1"))
    ))))))))))
    )

    ===================

    StrategyDecl(
      []
    , "complete"
    , []
    , [TypeRef("Var")]
    , TypeRef("SolverState")
    , ListType(TypeRef("SolverState"))
    ){ OfType(
         STRATEGY(
           [CLASS("Var")]
         , CLASS("SolverState")
         , LIST(CLASS("SolverState"))
         )
       )
     }
  , StrategyDefWInput(
      "complete"
    , [ParamDefNoType("v")]
    , "__input"
    , Let(
        "x22"
      , Apply(Var("expandDeterministic"){ OfType(
                                            STRATEGY(
                                              [CLASS("Var")]
                                            , CLASS("SolverState")
                                            , LIST(CLASS("SolverState"))
                                            )
                                          )
                                        }, [Var("v"){OfType(CLASS("Var"))}]){OfType(STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState"))))}
      , Let(
          "x21"
        , Apply(Var("flatMap"){ OfType(
                                  STRATEGY(
                                    [STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState")))]
                                  , LIST(CLASS("SolverState"))
                                  , LIST(CLASS("SolverState"))
                                  )
                                )
                              }, [Var("x22"){OfType(STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState"))))}]){OfType(
                                                                                                                         STRATEGY([], LIST(CLASS("SolverState")), LIST(CLASS("SolverState")))
                                                                                                                       )}
        , Let(
            "x25"
          , Apply(Var("expandAllQueries"){ OfType(
                                             STRATEGY(
                                               [CLASS("Var")]
                                             , CLASS("SolverState")
                                             , LIST(CLASS("SolverState"))
                                             )
                                           )
                                         }, [Var("v"){OfType(CLASS("Var"))}]){OfType(STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState"))))}
          , Let(
              "x24"
            , Apply(Var("flatMap"){ OfType(
                                      STRATEGY(
                                        [STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState")))]
                                      , LIST(CLASS("SolverState"))
                                      , LIST(CLASS("SolverState"))
                                      )
                                    )
                                  }, [Var("x25"){OfType(STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState"))))}]){OfType(
                                                                                                                             STRATEGY([], LIST(CLASS("SolverState")), LIST(CLASS("SolverState")))
                                                                                                                           )}
            , Let(
                "x28"
              , Apply(Var("expandAllInjections"){ OfType(
                                                    STRATEGY(
                                                      [CLASS("Var")]
                                                    , CLASS("SolverState")
                                                    , LIST(CLASS("SolverState"))
                                                    )
                                                  )
                                                }, [Var("v"){OfType(CLASS("Var"))}]){OfType(STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState"))))}
              , Let(
                  "x27"
                , Apply(Var("flatMap"){ OfType(
                                          STRATEGY(
                                            [STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState")))]
                                          , LIST(CLASS("SolverState"))
                                          , LIST(CLASS("SolverState"))
                                          )
                                        )
                                      }, [Var("x28"){OfType(STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState"))))}]){OfType(
                                                                                                                                 STRATEGY([], LIST(CLASS("SolverState")), LIST(CLASS("SolverState")))
                                                                                                                               )}
                , Let(
                    "x30"
                  , Apply(Var("expandAllPredicates"){ OfType(
                                                        STRATEGY(
                                                          [CLASS("Var")]
                                                        , CLASS("SolverState")
                                                        , LIST(CLASS("SolverState"))
                                                        )
                                                      )
                                                    }, [Var("v"){OfType(CLASS("Var"))}]){OfType(STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState"))))}
                  , Let(
                      "x29"
                    , Eval(Var("x30"){OfType(STRATEGY([], CLASS("SolverState"), LIST(CLASS("SolverState"))))}, Var("__input"){OfType(CLASS("SolverState"))}){OfType(LIST(CLASS("SolverState")))}
                    , Let(
                        "x26"
                      , Eval(Var("x27"){OfType(
                                          STRATEGY([], LIST(CLASS("SolverState")), LIST(CLASS("SolverState")))
                                        )}, Var("x29"){OfType(LIST(CLASS("SolverState")))}){OfType(LIST(CLASS("SolverState")))}
                      , Let(
                          "x23"
                        , Eval(Var("x24"){OfType(
                                            STRATEGY([], LIST(CLASS("SolverState")), LIST(CLASS("SolverState")))
                                          )}, Var("x26"){OfType(LIST(CLASS("SolverState")))}){OfType(LIST(CLASS("SolverState")))}
                        , Eval(Var("x21"){OfType(
                                            STRATEGY([], LIST(CLASS("SolverState")), LIST(CLASS("SolverState")))
                                          )}, Var("x23"){OfType(LIST(CLASS("SolverState")))}){OfType(LIST(CLASS("SolverState")))}
                        ){OfType(LIST(CLASS("SolverState")))}
                      ){OfType(LIST(CLASS("SolverState")))}
                    ){OfType(LIST(CLASS("SolverState")))}
                  ){OfType(LIST(CLASS("SolverState")))}
                ){OfType(LIST(CLASS("SolverState")))}
              ){OfType(LIST(CLASS("SolverState")))}
            ){OfType(LIST(CLASS("SolverState")))}
          ){OfType(LIST(CLASS("SolverState")))}
        ){OfType(LIST(CLASS("SolverState")))}
      ){OfType(LIST(CLASS("SolverState")))}
    ){ OfType(
         STRATEGY(
           [CLASS("Var")]
         , CLASS("SolverState")
         , LIST(CLASS("SolverState"))
         )
       )
     }
     */

    /**
     * Compiles a strategy declaration term into an IR strategy declaration.
     */
    fun toStrategyDecl(decl: Term): StrategyTypeDecl {
        require(decl is ApplTerm) { "Expected constructor application term, got: $decl"}

        return when (decl.constructor) {
            "StrategyDecl" -> StrategyTypeDecl(QName("", decl[1].toJavaString()), StrategyType(decl[3].toList().map { toType(it) }, toType(decl[4]), toType(decl[5])))
            "StrategyDeclNoParams" -> throw UnsupportedOperationException("Unsupported declaration, should have been desugared: $decl")
            "StrategyDeclNoTypeParams" -> throw UnsupportedOperationException("Unsupported declaration, should have been desugared: $decl")
            "StrategyDeclNoParamsOrTypeParams" -> throw UnsupportedOperationException("Unsupported declaration, should have been desugared: $decl")
            else -> TODO("Unsupported declaration: $decl")
        }
    }

    /**
     * Compiles a strategy definition term into an IR strategy definition.
     */
    fun toStrategyDef(def: Term): StrategyDef {
        require(def is ApplTerm) { "Expected constructor application term, got: $def"}

        return when (def.constructor) {
            "StrategyDefWInput" -> StrategyDef(QName("", def[0].toJavaString()), def[1].toList().map { it.toJavaString() }, def[2].toJavaString(), toExp(def[3]))
            "StrategyDefWInputNoParams" -> throw UnsupportedOperationException("Unsupported declaration, should have been desugared: $def")
            "StrategyDef" -> throw UnsupportedOperationException("Unsupported declaration, should have been desugared: $def")
            "StrategyDefNoParams" -> throw UnsupportedOperationException("Unsupported declaration, should have been desugared: $def")
            else -> TODO("Unsupported declaration: $def")
        }
    }

    /**
     * Compiles an expression term into an IR expression.
     */
    fun toExp(exp: Term): Exp {
        require(exp is ApplTerm) { "Expected constructor application term, got: $exp"}

        val type = typeOf(exp)
        return when (exp.constructor) {
            "Let" -> Let(exp[0].toJavaString(), toExp(exp[1]), toExp(exp[2]), type)
            "Apply" -> Apply(toExp(exp[0]), exp[1].toList().map { toExp(it) }, type)
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
        return toType(t.annotations["OfType", 1])
    }

    /**
     * Compiles a type term into an IR type.
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

            "STRATEGY" -> StrategyType(type[0].toList().map { toType(it) }, toType(type[1]), toType(type[2]))
            "TUPLE" -> TupleType(type[0].toList().map { toType(it) })
            "CLASS" -> ClassTypeRef(QName("", type[0].toJavaString()))
            "LIST" -> ListType(toType(type[0]))

            "ERROR" -> TypeError("Type error")

            else -> TODO("Unsupported type: $type")
        }
    }
}