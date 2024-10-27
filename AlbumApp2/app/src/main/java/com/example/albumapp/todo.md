Чтобы реализовать отображение текста с рамочкой вокруг `TextField` только в состоянии фокуса, вы
можете использовать `Box` для обертывания `TextField` и добавить условную логику для отображения
рамки в зависимости от состояния фокуса. Вот пример, как это сделать:

### Обновлённый код для `TextField` с рамкой при фокусе:

```kotlin
@Composable
fun DraggableSticker(
    elementName: String,
    pageNumber: Int = 0,
    context: Context = LocalContext.current,
    pageSize: IntSize,
    element: PageElement,
    onElementUpdate: (Int, PageElement, Int) -> Unit,
    onDelete: (IntSize) -> Unit,
    onNear: (Boolean) -> Unit
) {
    var position by remember {
        mutableStateOf(Offset(element.offsetX * pageSize.width, element.offsetY * pageSize.height))
    }
    var rotation by remember { mutableFloatStateOf(element.rotation) }
    var scale by remember { mutableFloatStateOf(element.scale * pageSize.width) }
    var elementSize by remember { mutableStateOf(IntSize.Zero) }

    var textForTextField by remember { mutableStateOf(element.resource) }
    var isFocused by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(pageSize, element.offsetX, element.offsetY) {
        position = Offset(element.offsetX * pageSize.width, element.offsetY * pageSize.height)
        rotation = element.rotation
        scale = element.scale * pageSize.width
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
            .size(scale.dp)
            .transformable(
                state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                    scale *= zoomChange
                    rotation += rotationChange
                    position += offsetChange
                    onElementUpdate(
                        pageNumber, element.copy(
                            offsetX = position.x / pageSize.width,
                            offsetY = position.y / pageSize.height,
                            scale = scale / pageSize.width,
                            rotation = rotation
                        ), element.id
                    )
                }
            )
    ) {
        when (element.type) {
            ElementType.TEXT_FIELD -> {
                Box(
                    modifier = Modifier
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                            if (!isFocused) {
                                // Обновляем глобальное состояние при потере фокуса
                                onElementUpdate(
                                    pageNumber,
                                    element.copy(resource = textForTextField),
                                    element.id
                                )
                            }
                        }
                        .graphicsLayer {
                            scaleX = 1f
                            scaleY = 1f
                            rotationZ = rotation
                        }
                ) {
                    // Рамка вокруг текста, если TextField в фокусе
                    if (isFocused) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .border(2.dp, Color.Blue) // Задайте цвет рамки
                        )
                    }

                    TextField(
                        value = textForTextField,
                        onValueChange = { textForTextField = it },
                        modifier = Modifier
                            .onSizeChanged { size -> elementSize = size }
                            .padding(8.dp) // Отступы внутри TextField
                            .background(Color.Transparent) // Прозрачный фон для TextField
                            .focusable() // Обеспечиваем, что TextField может получать фокус
                    )
                }
            }

            else -> {
                // Другие элементы (стикеры, изображения)
            }
        }
    }
}
```

### Объяснение изменений:

1. **Проверка фокуса**:
    - Добавлена переменная `isFocused`, которая обновляется в зависимости от состояния
      фокуса `TextField`.
    - В обработчике `onFocusChanged` изменяем значение `isFocused` и обновляем глобальное состояние
      при потере фокуса.

2. **Рамка вокруг текста**:
    - Используется `Box` для обертывания `TextField`. Если `isFocused` истинно, отображается
      дополнительный `Box` с рамкой (свойство `border`).
    - Для рамки задан цвет и ширина (например, `Color.Blue` и `2.dp`).

3. **Прозрачный фон**:
    - `TextField` имеет прозрачный фон, чтобы не перекрывать рамку.

4. **Отступы внутри `TextField`**:
    - Используется `padding` для добавления отступов внутри `TextField`, чтобы текст не прилипал к
      рамке.

### Результат:

Теперь, когда `TextField` получает фокус, вокруг него появляется рамка, а при потере фокуса
отображается только текст без рамки.


