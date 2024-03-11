
# Тестовое задание в Тинькофф финтех весна 2024

Посмотреть как я декомпозировал задание можно -> [тут](https://github.com/users/Markamadeos/projects/1)

Там же таски, которые я не успел сделать. К каждой задаче привязан пулл-реквест, поэтому, если интересно как я делал что-то конкретное, можно провалиться из задачи в ПР и глянуть.

# Stack

 - Kotlin
 - MVVM
 - Single activity
 - Coroutines + Flow
 - Room
 - LiveData
 - Glide
 - Retrofit
 - Jetpack navigation component
 - Koin (di)
 - Bottom Navigation

# Features
 ### Популярное
 - Отображает фильмы и сериалы с параметром `/films/collections?type=TOP_POPULAR_ALL`
 - Реализован поиск, возвращает одну страницу результата, я не успел сделать пагинацию
 - Поиск происходит автоматически с задержкой в 2 секунды после окончания ввода
 - Долгий тап на элементе списка добавит фильм в избранное
 
 ### Избранное
 - Долгий тап по фильму в избранном призывает алерт для подтверждения удаления
 - Фильмы на этом экране получаются из бд и доступны при отсутствии интернета на устройстве
 
### Детали
 - Экран деталей понимает откуда к нему пришел пользователь и в зависимости от этого выбирает источник данных (сеть или бд)
 - Внизу прикольный bottomSheet😅

Еще сделал темную тему, пусть будет, подумал я. 

# Sreenshots
<img src="https://github.com/Markamadeos/Kinopoisk-test-app/blob/develop/sc/sc1.jpeg" width=30% height=30%> <img src="https://github.com/Markamadeos/Kinopoisk-test-app/blob/develop/sc/sc2.jpeg" width=30% height=30%> <img src="https://github.com/Markamadeos/Kinopoisk-test-app/blob/develop/sc/sc3.jpeg" width=30% height=30%>
<img src="https://github.com/Markamadeos/Kinopoisk-test-app/blob/develop/sc/sc4.jpeg" width=30% height=30%> <img src="https://github.com/Markamadeos/Kinopoisk-test-app/blob/develop/sc/sc5.jpeg" width=30% height=30%>
