# Parameterized Types (from PIE)


1.  Find the type parameters in the declaration scope.
2.  Declare the type arguments in the new instance scope.
3.  Associate the type arguments with the type parameters.
4.  Determine the instantiated type.


## Instantiation
Create instantiation scope, as a child of both the functions declaration scope and the given type arguments scope.

    new s_func_instantiated,
      s_func_instantiated -P-> s_func,
      s_func_instantiated -P-> s_type_args,

Get the generic parameters of the function (from its declaration scope):

    getGenericParams(s_func) == type_params

Implementation:

    /**
     * getGenericParams(s_data) = params
     * Get the list of generic parameters of datatype scope [s_data].
     * [s_data] can be the generic definition or an instance.
     */
    getGenericParams : scope -> list((GenericParameter * TYPE * TYPE))
    getGenericParams(s_data) = params :-
      query generic_params
        filter P*
           min $ < P and true
            in s_data |-> [(_, params)]
        | error $[BUG: Could not get generic parameters from [s_data]].

Assert that the generic arguments are ok:

    genericArgsOk(s1, s_type_args, s_func_instantiated, type_params, type_args, TRUE(), name)

Implementation:

    genericArgsOk : scope * scope * scope * list((GenericParameter * TYPE * TYPE)) * TypeArgs * BOOLEAN * string

    // No generic arguments provided
    genericArgsOk(_, _, s_data_instance, params@[_|_], NoTypeArgs(), _, error_node) :- ... .

    genericArgsOk(s, s_type_args, s_data_instance, params, type_args@TypeArgs(arg_types), resolve_in_super_types, _) :-
        genericArgsOk_1(s, s_type_args, s_data_instance, params, type_args, resolve_in_super_types, sameLength(params, arg_types)).

Implementation: Make sure type arg list and type param list have the same length:

        sameLength : list((GenericParameter * TYPE * TYPE)) * list(Type) -> (list(TYPEID) * list(Type))
        sameLength([], []) = ([], []).
        sameLength([(GenericParameter(_, name), _, _)|params], []) = ([name|names], []) :-
        sameLength(params, []) == (names, []).
        sameLength([], remaining@[_|_]) = ([], remaining).
        sameLength([_|params], [_|type_args]) = sameLength(params, type_args).

Implementation: Declare the type argument in the type arg scope, and assert it is between the bounds.

    genericArgOk : scope * scope * scope * (GenericParameter * TYPE * TYPE) * Type * BOOLEAN
    genericArgsOk_2 maps genericArgOk(*, *, *, list(*), list(*), *)
    genericArgOk(s, s_type_args, s_data_instance, (param@GenericParameter(_, name), upper_bound, lower_bound), arg, resolve_in_super_types) :-
        {arg_ty}
        typeOfWithTypeArgScope(s, s_type_args, arg, resolve_in_super_types) == arg_ty,
        declareTypeArg(s_data_instance, param, arg_ty),
        try { assignableTo(arg_ty, upper_bound, TypeArgKind()) } | error $[Type mismatch: [arg_ty] is not within upper bound [upper_bound] for type parameter [name]],
        try { assignableTo(lower_bound, arg_ty, TypeArgKind()) } | error $[Type mismatch: [arg_ty] is not within lower bound [lower_bound] for type parameter [name]].

Instantiate the type args:

    instantiateTypeArgsInAll(s_func_instantiated, param_tys) == instantiated_param_tys,  // (3)

Implementation:

    /**
    * instantiateTypeArgs(s, T) = instantiated_type
    * instantiate [T] to a type [instantiated_type].
    * This recursively and exhaustively replaces GenericParameter2TYPE(_) with
    * its type argument. If no type argument exists it will leave the generic
    * parameter as is. This is used when checking if a method overrides another.
    */
    instantiateTypeArgs : scope * TYPE -> TYPE
    instantiateTypeArgsInAll maps instantiateTypeArgs(*, list(*)) = list(*)
    instantiateTypeArgs(s, T) = instantiateTypeArgs_1(s, T, Free(), []).

        instantiateTypeArgs_1(s, GenericParameter2TYPE(param@GenericParameter(s_param, _)), _, params) =
        instantiateTypeArgs_genericParameter(s, resolved_arg, param, [param|params], seen)
        :-
        resolveTypeArg(s, param) == resolved_arg,
        containsGenericParam(params, param) == seen.


    /**
    * resolveTypeArg(s, name) = occs
    * Resolve [name] to type argument occurences [occs] in [s].
    * May return any number of type arguments. Follows P and INHERIT edges.
    */
    resolveTypeArg : scope * GenericParameter -> list((path * (GenericParameter * TYPE)))
    resolveTypeArg(s, param) = occs :-
        query generic_arg
        filter (INHERIT|P)*
            and { param' :- param == param' }
            min $ < P, $ < INHERIT
            in s |-> occs.


        containsGenericParam : list(GenericParameter) * GenericParameter -> BOOLEAN
        containsGenericParam([], _) = FALSE().
        containsGenericParam([param|_], param) = TRUE().
        containsGenericParam([param'|params], param) = containsGenericParam(params, param).

        instantiateTypeArgs_genericParameter : scope * list((path * (GenericParameter * TYPE))) *
            GenericParameter * list(GenericParameter) * BOOLEAN -> TYPE
        instantiateTypeArgs_genericParameter(_, [], param, _, _) = GenericParameter2TYPE(param).
        instantiateTypeArgs_genericParameter(s, [_], param, params, TRUE()) = GenericParameter2TYPE(param).
        instantiateTypeArgs_genericParameter(s, [(_, (_, T))], _, params, FALSE()) = instantiateTypeArgs_1(s, T, Bound(), params).
        instantiateTypeArgs_genericParameter(s, [_,_|_], GenericParameter(_, name), _, _) = DataType(emptyScope(s)) :-
            false | error $[BUG: resolved multiple type arguments named [name]].


```

      instantiateTypeArgsInAll(s_func_instantiated, param_tys) == instantiated_param_tys,  // (3)
      expectAssignableToPassScopes(s1, arg_exps, instantiated_param_tys, ExpressionKind()) == s2,   // (4)
      instantiateTypeArgs(s_func_instantiated, ret_ty) == ty,   // (5)
      typesOfWithTypeArgScope(s1, s_type_args, getTypeArgsList(type_args), FALSE()) == type_arg_tys,   // (6)
      instantiateTypeArgsInAll(s_func_instantiated, type_arg_tys) == instantiated_type_args,  // (7)
      @name.ref := name',
      @name.type := FuncRefType(instantiated_type_args, instantiated_param_tys, ty).
```