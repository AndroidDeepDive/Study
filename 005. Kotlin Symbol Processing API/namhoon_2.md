## Kotlin Symbol Processing API란 무엇인가?

컴파일러 플러그인이라는 개념이 있다.

컴파일러 플러그인을 사용하면 컴파일러를 라이브러리로 직접 호출하여 프로그램을 분석하고 수정할 수 있으며, 이를 통해 다양한 용도의 아웃풋을 산출할 수 있다.

특정한 템플릿을 가진 보일러 플레이트를 생성하는 데 사용되는 것이 대표적이다.

다만, 강력한 도구이니만큼 컴파일러의 배경지식 및 구현 규칙에 대해서 어느정도 지식이 필요하다는 기술 부채는 존재한다.

KSP는 이 컴파일러 플러그인을 생성하는 데 쓰이며, KSP로 생성하는 경우 상대적으로 경량화된 플러그인을 얻을 수 있다.

또한 컴파일러의 최신 버전을 쫓아가고 이해하는 데 드는 비용을 줄일 수 있도록, 컴파일러의 변경 사항을 은닉시켜 유지보수 작업을 최소화하는 장점이 있다.

Android 개발자들도 얼마든지 활용할 수 있도록 Google의 Maven에서 다운로드를 제공한다.

#### `kotlinc` 컴파일러 플러그인과의 비교

`kotlinc` 컴파일러 플러그인의 경우 강력한 기능을 제공하는 것은 맞지만, 그만큼 컴파일러에 대한 의존성이 크기때문에 유지보수에 대한 용이성이 떨어진다.

반면 KSP는 대부분의 컴파일러 변경사항을 은닉하여 api를 통해 접근할 수 있도록 해준다.

물론 한 단계를 더 건너야하는 만큼 `kotlinc`의 모든 기능을 지원하지는 않지만, 기술 부채를 고려하였을 때 합리적인 선택이 될 것이다.

#### `kotlin.reflect`와의 비교

KSP는 `kotlin.reflect`와 유사하게 생겼지만, KSP는 타입에 대한 참조를 명시적으로 지정해주어야 한다.

#### KAPT와의 비교

앞선 포스팅에서 언급했듯 KAPT는 Kotlin 코드를 Java Annotation Processor를 수정하지 않기 위해 컴파일시 Java로 된 Stub을 생성하게 된다.

Stub을 생성하는 것은 kotlinc의 분석 비용의 3분의 1이나 차지하므로, 빌드시 많은 오버헤드가 발생하게 된다.

Glide를 기준으로 KSP로 전환시 컴파일타임이 25% 감소했다고 한다.

KAPT와 달리 KSP는 Java 관점이 아닌 Kotlin의 관점에서 접근하며, `top-level function`과 같은 Kotlin의 고유 기능에 더 적합하다.

### KSP Models

![](https://github.com/google/ksp/raw/main/docs/ClassDiagram.svg)

> **참고** [KSP API definition](https://github.com/google/ksp/blob/main/api/src/main/kotlin/com/google/devtools/ksp)
> **참고** [KSP Symbol definition](https://github.com/google/ksp/blob/main/api/src/main/kotlin/com/google/devtools/ksp/symbol)

KSP 모델에 대한 딥다이브를 해보자.

KSP에서 타입에 대한 참조는 몇 가지 예외를 제외하면 명시적으로 지정하도록 되어있다.

`KSFunctionDeclaration.returnType` 혹은 `KSAnnotation.annotationType`과 같이 타입을 참조하는 경우, 타입은 항상 annotation과 modifier가 포함된 `KSReferenceElement` 기반의 `KSTypeReference`이다.

```kotlin
interface KSFunctionDeclaration : ... {
  val returnType: KSTypeReference?
  ...
}

interface KSTypeReference : KSAnnotated, KSModifierListOwner {
  val type: KSReferenceElement
}
```

`KSTypeReference`는 Kotlin의 타입 시스템의 `KSType`으로 `resolve()`할 수 있고, Kotlin 문법과 일치하는 `KSReferenceElement`를 가지고 있다.

이번엔 `KSReferenceElement`다.


```kotlin
interface KSReferenceElement : KSNode {
    val typeArguments: List<KSTypeArgument>
}
```

`KSReferenceElement`는 유용한 정보를 많이 포함하고 있는 `KSClassifierReference` 혹은 `KSCallableReference`가 될 수 있다.

```kotlin
interface KSClassifierReference : KSReferenceElement {
    val qualifier: KSClassifierReference?
    fun referencedName(): String

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        return visitor.visitClassifierReference(this, data)
    }
}
```

예를 들어 `KSClassifierReference`는 `referencedName`라는 속성을 가지고 있으며,

```kotlin
interface KSCallableReference : KSReferenceElement {
    val receiverType: KSTypeReference?
    val functionParameters: List<KSValueParameter>
    val returnType: KSTypeReference

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        return visitor.visitCallableReference(this, data)
    }
}
```

`KSCallableReference`는 `receiverType`과 `functionArguments` 그리고 `returnType`을 가지고 있다.

`KSTypeReference`에서 참조되는 타입의 선언이 필요한 경우 아래와 같은 순서로 접근한다.

```kotlin
KSTypeReference -> .resolve() -> KSType -> .declaration -> KSDeclaration
```

`resolve()`를 통해 `KSType`으로 접근하고, `declaration` 속성을 통해 `KSDeclaration` 객체를 획득한다.

### Java Annotation Processing에 대응하는 KSP 레퍼런스

너무 길어서 접어둔다.

원본은 아래 참고 링크를 타고 가면 볼 수 있다.

<details>
<summary>펼치기 / 접기</summary>

### Program elements

| **Java** | **Closest facility in KSP** | **Notes** |
| -------- | --------------------------- | --------- |
| AnnotationMirror | KSAnnotation | |
| AnnotationValue | KSValueArguments | |
| Element | KSDeclaration / KSDeclarationContainer | |
| ExecutableElement | KSFunctionDeclaration | |
| PackageElement | KSFile | KSP doesn't model packages as program elements. |
| Parameterizable | KSDeclaration | |
| QualifiedNameable | KSDeclaration | |
| TypeElement | KSClassDeclaration | |
| TypeParameterElement | KSTypeParameter | |
| VariableElement | KSValueParameter / KSPropertyDeclaration | |

### Types

Because KSP requires explicit type resolution, some functionalities in Java can
only be carried out by KSType and the corresponding elements before resolution.

| **Java** | **Closest facility in KSP** | **Notes** |
| -------- | --------------------------- | --------- |
| ArrayType | KSBuiltIns.arrayType | |
| DeclaredType | KSType / KSClassifierReference | |
| ErrorType | KSType.isError | |
| ExecutableType | KSType / KSCallableReference | |
| IntersectionType | KSType / KSTypeParameter | |
| NoType | KSType.isError | N/A in KSP |
| NullType | | N/A in KSP |
| PrimitiveType | KSBuiltIns | Not exactly same as primitive type in Java |
| ReferenceType | KSTypeReference | |
| TypeMirror | KSType | |
| TypeVariable | KSTypeParameter | |
| UnionType | N / A | Kotlin has only one type per catch block. UnionType is also not observable by even Java annotation processors. |
| WildcardType | KSType / KSTypeArgument | |

### Misc

| **Java** | **Closest facility in KSP** | **notes** |
| -------- | --------------------------- | --------- |
| Name | KSName | |
| ElementKind | ClassKind / FunctionKind | |
| Modifier | Modifier | |
| NestingKind | ClassKind / FunctionKind | |
| AnnotationValueVisitor |  | |
| ElementVisitor | KSVisitor | |
| AnnotatedConstruct | KSAnnotated | |
| TypeVisitor |  | |
| TypeKind | KSBuiltIns | Some can be found in builtins, otherwise check KSClassDeclaration for DeclaredType |
| ElementFilter | Collection.filterIsInstance | |
| ElementKindVisitor | KSVisitor | |
| ElementScanner | KSTopDownVisitor | |
| SimpleAnnotationValueVisitor |  | No needed in KSP |
| SimpleElementVisitor | KSVisitor | |
| SimpleTypeVisitor |  | |
| TypeKindVisitor |  | |
| Types | Resolver / utils | Some of the utils are also integrated into symbol interfaces |
| Elements | Resolver / utils | |

### Details

How functionalities of Java annotation processing API can be carried out by KSP.

#### AnnotationMirror

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getAnnotationType | ksAnnotation.annotationType |
| getElementValues | ksAnnotation.arguments |

### AnnotationValue

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getValue | ksValueArgument.value |

### Element

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| asType | ksClassDeclaration.asType(...) // Only available for KSClassDeclaration. Type arguments need to be supplied. |
| getAnnotation | // To be implemented. |
| getAnnotationMirrors | ksDeclaration.annotations |
| getEnclosedElements | ksDeclarationContainer.declarations |
| getEnclosingElements | ksDeclaration.parentDeclaration |
| getKind | type check & cast following ClassKind or FunctionKind |
| getModifiers | ksDeclaration.modifiers |
| getSimpleName | ksDeclaration.simpleName |

### ExecutableElement

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getDefaultValue | // To be implemented. |
| getParameters | ksFunctionDeclaration.parameters |
| getReceiverType | ksFunctionDeclaration.parentDeclaration |
| getReturnType | ksFunctionDeclaration.returnType |
| getSimpleName | ksFunctionDeclaration.simpleName |
| getThrownTypes | // Not needed in Kotlin. |
| getTypeParameters | ksFunctionDeclaration.typeParameters |
| isDefault | // Check whether parent declaration is an interface or not. |
| isVarArgs | ksFunctionDeclaration.parameters.any { it.isVarArg } |

### Parameterizable

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getTypeParameters | ksFunctionDeclaration.typeParameters |

### QualifiedNameable

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getQualifiedName | ksDeclaration.qualifiedName |

### TypeElement

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getEnclosedElements | ksClassDeclaration.declarations |
| getEnclosingElement | ksClassDeclaration.parentDeclaration |
| getInterfaces | ```ksClassDeclaration.superTypes.map { it.resolve() }.filter {(it?.declaration as? KSClassDeclaration)?.classKind == ClassKind.INTERFACE} // Should be able to do without resolution.``` |
| getNestingKind | // check KSClassDeclaration.parentDeclaration and inner modifier. |
| getQualifiedName | ksClassDeclaration.qualifiedName |
| getSimpleName | ksClassDeclaration.simpleName |
| getSuperclass | ```ksClassDeclaration.superTypes.map { it.resolve() }.filter { (it?.declaration as? KSClassDeclaration)?.classKind == ClassKind.CLASS } // Should be able to do without resolution.``` |
| getTypeParameters | ksClassDeclaration.typeParameters |

### TypeParameterElement

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getBounds | ksTypeParameter.bounds |
| getEnclosingElement | ksTypeParameter.parentDeclaration |
| getGenericElement | ksTypeParameter.parentDeclaration |

### VariableElement

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getConstantValue | // To be implemented. |
| getEnclosingElement | ksValueParameter.parentDeclaration |
| getSimpleName | ksValueParameter.simpleName |

### ArrayType

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getComponentType | ksType.arguments.first() |

### DeclaredType

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| asElement | ksType.declaration |
| getEnclosingType | ksType.declaration.parentDeclaration |
| getTypeArguments | ksType.arguments |

### ExecutableType

**Note:** A `KSType` for a function is just a signature represented by the
`FunctionN<R, T1, T2, ..., TN>` family.

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getParameterTypes | ksType.declaration.typeParameters, ksFunctionDeclaration.parameters.map { it.type } |
| getReceiverType | ksFunctionDeclaration.parentDeclaration.asType(...) |
| getReturnType | ksType.declaration.typeParameters.last() |
| getThrownTypes | // Not needed in Kotlin. |
| getTypeVariables | ksFunctionDeclaration.typeParameters |

### IntersectionType

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getBounds | ksTypeParameter.bounds |

### TypeMirror

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getKind | // Compare with types in KSBuiltIns for primitive types, Unit type, otherwise declared types |

### TypeVariable

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| asElement | ksType.declaration |
| getLowerBound | // To be decided. Only needed if capture is provided and explicit bound checking is needed. |
| getUpperBound | ksTypeParameter.bounds |

### WildcardType

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getExtendsBound | ```if (ksTypeArgument.variance == Variance.COVARIANT) { ksTypeArgument.type } else { null }``` |
| getSuperBound | ```if (ksTypeArgument.variance == Variance.CONTRAVARIANT) { ksTypeArgument.type } else { null }``` |

### Elements

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| getAllAnnotationMirrors | KSDeclarations.annotations |
| getAllMembers | getAllFunctions and getAllProperties, the latter is not there yet |
| getBinaryName | // To be decided, see [Java Spec](https://docs.oracle.com/javase/specs/jls/se13/html/jls-13.html#jls-13.1) |
| getConstantExpression | we have constant value, not expression |
| getDocComment | // To be implemented |
| getElementValuesWithDefaults | // To be implemented. |
| getName | resolver.getKSNameFromString |
| getPackageElement | Package not supported, while package information can be retrieved, operation on package is not possible for KSP |
| getPackageOf | Package not supported |
| getTypeElement | Resolver.getClassDeclarationByName |
| hides | // To be implemented |
| isDeprecated | KsDeclaration.annotations.any { it.annotationType.resolve()!!.declaration.quailifiedName!!.asString() == Deprecated::class.quailifiedName } |
| overrides | KSFunctionDeclaration/KSPropertyDeclaration.overrides // member function of respective class |
| printElements | // KSP implemented basic toString() on most classes. |

### Types

| **Java** | **KSP equivalent** |
| -------- | ------------------ |
| asElement | ksType.declaration |
| asMemberOf | resolver.asMemberOf |
| boxedClass | // Not needed |
| capture | // To be decided. |
| contains | KSType.isAssignableFrom |
| directSuperTypes | (ksType.declaration as KSClassDeclaration).superTypes |
| erasure | ksType.starProjection() |
| getArrayType | ksBuiltIns.arrayType.replace(...) |
| getDeclaredType | ksClassDeclaration.asType |
| getNoType | ksBuiltIns.nothingType / null |
| getNullType | // depends on the context, KSType.markNullable maybe useful. |
| getPrimitiveType | // Not needed, check for KSBuiltins |
| getWildcardType | // Use Variance in places expecting KSTypeArgument |
| isAssignable | ksType.isAssignableFrom |
| isSameType | ksType.equals |
| isSubsignature | functionTypeA == functionTypeB || functionTypeA == functionTypeB.starProjection() |
| isSubtype | ksType.isAssignableFrom |
| unboxedType | // Not needed. |

</details>

> **참고** [Github ksp#referencs](https://github.com/google/ksp/blob/main/docs/reference.md)

## Refrences
- https://github.com/google/ksp

