### Требования

Минимальная версия SDK - 17 (Android 4.2)

Библиотека предназначена для Phone и Tablet проектов (не предназначена для Android TV или Android Wear приложений)

### Добавление в проект

Добавьте в корневой `build.gradle` в раздел `repositories`:

    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }

Затем в `build.gradle` проекта (на уровне app) в раздел `dependencies` добавьте 

    implementation 'com.github.inappstory:android-sdk:0.1.15'

Также для корректной работы в dependencies нужно добавить библиотеку GSON:

    implementation 'com.google.code.gson:gson:2.8.6'    

##### Дополнительные ограничения

Если в проекте используется proguard-обфускация кода, то в файле `proguard-rules.pro` необходимо прописать:

    keepclassmembers class fqcn.of.javascript.interface.for.webview {
        public *;
    }

    -keep public class com.inappstory.sdk.** {
        *;
    }

При разработке под Android 9 в файле манифеста в application разрешить получение трафика по http:

    android:usesCleartextTraffic="true"

##### Инициализация в проекте

Для дальнейшей работы в файле res/values/constants.xml необходимо добавить строку:

    <string name="csApiKey">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>

В файле `AndroidManifest.xml` в разделе application необходимо добавить 

    <service
        android:name=".InAppStoryService"
        android:enabled="true"
        android:exported="true" />

Для инициализации библиотеки в классе `Application`, `Activity`, `View` (или любом другом с доступом к объекту `Context`) используется класс `InAppStoryManager.Builder()`. Класс содержит следующие параметры (и одноименные сеттеры).

    Context context //контекст приложения, обязательный параметр
    boolean sandbox //обращаемся к тестовому серверу или рабочему, опциональный параметр
    String userId //уникальный текстовый идентификатор пользователя (id, login, etc...), который обращается к stories, опциональный параметр.
    String apiKey //по умолчанию ключ берется из строки csApiKey, если есть желание изменять его на лету, можно воспользоваться этим параметром. Опциональный.
    String testKey //тестовый интеграционный ключ для тестирования stories на устройстве. Опционален, по умолчанию отсутствует.
    boolean closeOnSwipe //флаг, который отвечает за закрытие stories по смахиванию вниз. По умолчанию - true.
    boolean closeOnOverscroll //флаг, который отвечает за закрытие stories по смахиванию влево на последней story или вправо на первом. По умолчанию - true.
    boolean hasLike //флаг, который отвечает за подключение функционала like/dislike. По умолчанию - false (отключен).
    boolean hasShare//флаг, который отвечает за подключение функционала шаринга. По умолчанию - false (отключен).
    boolean hasFavorite //флаг, который отвечает за подключение функционала избранных stories. По умолчанию - false (отключен).
    ArrayList<String> tags //теги для таргетирования stories, опциональный параметр

Пример инициализации `InAppStoryManager` выглядит следующим образом

    new InAppStoryManager.Builder()
            .context(context) 
            .sandbox(true)
            .closeOnSwipe(true)
            .closeOnOverscroll(true)
            .userId(userId)
            .apiKey(apiKey)
            .testKey(testKey)
            .tags(tags)
            .create();

Добавьте в layout, в котором планируется показ списка, следующий код:

    <com.inappstory.sdk.stories.ui.list.StoriesList
        android:layout_width="match_parent"
        android:id="@+id/storiesList"
        app:cs_listIsFavorite="false"
        android:layout_height="wrap_content"/>

Аттрибут cs_listIsFavorite отвечает за то, добавляем мы обычный список или список избранных сториз (true - избранные, false - полный список).

Или же добавить через код:

    StoriesList storiesList = new StoriesList(context);
    addView(storiesList);

Для загрузки элементов списка у объекта storiesList после инициализации InAppStoryManager необходимо вызвать метод loadStories(). Метод также может быть использован для перезагрузки списка.

Также класс `InAppStoryManager` содержит статический метод `destroy` для очистки:

    InAppStoryManager.destroy();

Таже InAppStoryManager содержит методы 
    
    setTags(ArrayList<String> tags);
    addTags(ArrayList<String> tags);
    removeTags(ArrayList<String> tags);

Они позволяет менять/добавлять/удалять теги без пересоздания InAppStoryManager.

Если приложение поддерживает несколько аккаунтов, то можно реализовать смену идентификатора пользователя. Для того, чтобы сменить уже заданный идентификатор, необходимо сначала вызвать функцию `InAppStoryService.getInstance().setUserId(userId)`

Для изменения параметров `sandbox` и `apiKey` потребуется переинициализация `InAppStoryManager` (см пример инициализации). При этом старая будет очищена.
Для изменения параметра testKey можно использовать метод `InAppStoryManager.getInstance().setTestKey(String testKey)`.

### Параметры

##### StoriesList

Внешний вид списка, а таже некоторых элементов ридера настраивается через класс `AppearanceManager`. Его необходимо задать глобально для библиотеки, либо отдельно для списка перед вызовом loadStories(). 
Для глобального задания необходимо вызвать статический метод класса `setInstance(AppearanceManager manager)`

    AppearanceManager.setInstance(globalAppearanceManager);

Для задания списка вызывается метод экземляра класса StoriesList.

    storiesList.setAppearanceManager(appearanceManager);

В случае, если метод для списка не задан, то будут использоваться настройки из глобального AppearanceManager. Если не задан и он, то будет выброшен DataException.

Сам AppearanceManager содержит следующие параметры (и соответствующие им сеттеры)

    Integer csListItemWidth - ширина ячейки списка в пикселях (по умолчанию - null).

    Integer csListItemHeight - высота ячейки списка в пискелях (по умолчанию - null).

    boolean csListItemTitleVisibility - показывается ли заголовок story.

    int csListItemTitleSize - размер кегля заголовка.

    int csListItemTitleColor - цвет заголовка.

    boolean csListItemSourceVisibility - показывается ли источник story.

    int csListItemSourceSize - размер кегля источника.

    int csListItemSourceColor - цвет источника.

    int csListItemBorderColor - цвет рамки для непрочитанной ячейки.

    Typeface csCustomFont - шрифт, который используется для заголовка/источника story в ячейке.

    boolean csListItemBorderVisibility - показываем ли рамку для непрочитанной ячейки.

    boolean csListItemReadedBorderVisibility - показываем ли рамку для прочитанной ячейки.

    int csListReadedItemBorderColor - цвет рамки для прочитанной ячейки.

    int csListItemMargin - отступ между ячейками.

    boolean csShowStatusBar - отображаем ли статусбар при открытии ридера.

    int csClosePosition - где отображаем кнопку закрытия ридера.
    TOP_LEFT = 1;
    TOP_RIGHT = 2;
    BOTTOM_LEFT = 3;
    BOTTOM_RIGHT = 4;

    int csStoryReaderAnimation - анимация перелистывания stories в ридере
    ANIMATION_DEPTH = 1;
    ANIMATION_CUBE = 2;

Пример задания параметров выглядит следующим образом

    appearanceManager
        .csListItemBorderColor(Color.RED)
        .csListItemMargin(0)
        .csClosePosition(AppearanceManager.BOTTOM_RIGHT)
        .csListItemTitleColor(Color.BLUE)
        .csListItemTitleSize(Sizes.dpToPxExt(20))

Также помимо этого в AppearanceManager есть 3 интерфейса: 
1) `IStoriesListItem csListItemInterface`, используется для полной кастомизации элементов списка

    interface IStoriesListItem {
        View getView(); // здесь необходимо передать View - внешний вид ячейки.
        void setTitle(View itemView, String title, Integer titleColor); // itemView - текущая ячейка, в необходимой View используем заголовок story. Параметр titleColor может быть null.
        void setSource(View itemView, String source); // itemView - текущая ячейка, в необходимой View используем источник story.
        void setImage(View itemView, String url, int backgroundColor); // itemView - текущая ячейка, в необходимой View показываем обложку story или цвет фона в случае ее отсутствия.
        void setReaded(View itemView, boolean isReaded); // itemView - текущая ячейка, меняем ее по необходимости в случае если она прочитана.
    }

В случае задания данного интерфейса другие параметры, влияющие на внешний вид ячейки списка не используются (будут игнорироваться)
Пример использования:

    appearanceManager
        .csListItemInterface(new IStoriesListItem() {
                        @Override
                        public View getView() {
                            return LayoutInflater.from(MainActivity.this)
                                    .inflate(R.layout.custom_story_list_item, null, false);
                        }

                        @Override
                        public void setTitle(View itemView, String title, Integer titleColor) {
                            ((AppCompatTextView)itemView.findViewById(R.id.title)).setText(title);
                        }

                        @Override
                        public void setSource(View itemView, String source) {

                        }

                        @Override
                        public void setImage(View itemView, String url, int backgroundColor) {
                            //В случае, если есть сториз без изображений и с изображением, возможно потребуется предварительная очистка imageView с помощью setImageResource(0)
                            itemView.findViewById(R.id.image).setBackgroundColor(Color.RED);
                        }

                        @Override
                        public void setReaded(View itemView, boolean isReaded) {

                        }
                    });
    
2) `IGetFavoriteListItem csFavoriteListItemInterface`, используется для полной кастомизации элемента favorite в списке.

        public interface IGetFavoriteListItem {
            View getFavoriteItem(List<FavoriteImage> favoriteImages, int count);
            void bindFavoriteItem(View favCell, List<FavoriteImage> favoriteImages, int count);
        }
    
`View favCell` в методе `bindFavoriteItem` - RelativeLayout, который в себе содержит ту View, которую возвращает метод `getFavoriteItem`. В случае, если необходимо обращаться непосредтвенно ко внутренней View - предварительно у нее необходимо задать id или обращаться как `favCell.getChildAt(0)`.

Класс FavoriteImage содержит следующие геттеры:

    int getId() // идентификатор story
    List<Image> getImage() // обложка story (список объектов Image - картинок разного качества)
    String backgroundColor // цвет фона (в случае отсутствия обложки) в HEX

Пример использования:

    appearanceManager
        .csFavoriteListItemInterface(new IGetFavoriteListItem() {
                    @Override
                    public View getFavoriteItem(List<FavoriteImage> favImages, int count) {
                        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_story_list_item_favorite, null, false);
                        bindFavoriteItem(v, favImages, count);
                        return v;
                    }

                    @Override
                    public void bindFavoriteItem(View v, List<FavoriteImage> favImages, int count) {
                        AppCompatTextView title = v.findViewById(R.id.title);
                        title.setText("My favorites");
                        RelativeLayout container = v.findViewById(R.id.container);
                        container.removeAllViews();
                        AppCompatImageView image1 = new AppCompatImageView(MainActivity.this);
                        if (!favImages.isEmpty()) {
                            image1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT));
                            ImageLoader.getInstance().displayImage(favImages.get(0).getImage().get(0).getUrl(), -1, image1);
                        } else {
                            container.setBackgroundColor(Color.RED);
                        }
                    }
                });
 
Так же, для взаимодействия с ячейкой избранного (например, открытия нового окна со списком избранных сториз) необходимо добавить обработчик 
    
    storiesList.setOnFavoriteItemClick(new StoriesList.OnFavoriteItemClick() {
                @Override
                public void onClick() {
                    doAction();
                }
    });

3) `ILoaderView iLoaderView` - используется для подстановки собственного лоадера вместо дефолтного

        public interface ILoaderView {
            View getView();
        }

Этот интерфейс необходимо задавать для глобального AppearanceManager.
Пример использования:

    globalAppearanceManager.csLoaderView(new ILoaderView() {
            @Override
            public View getView() {
                RelativeLayout v = new RelativeLayout(MainActivity.this);
                v.addView(new View(MainActivity.this) {{
                    setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(48), Sizes.dpToPxExt(48)));
                    setBackgroundColor(Color.GREEN);
                }});
                return v;
            }
        });

##### Размеры

Размеры диалогового окна отображения stories на планшете

    <dimen name="cs_tablet_width">400dp</dimen>
    <dimen name="cs_tablet_height">600dp</dimen>

##### Иконки

Библиотека использует 6 кнопок с иконками: refresh, close, like, dislike, share и favorite Для кнопок refresh и close используются файлы `ic_refresh.xml`, `ic_stories_close.xml`. Иконки могут быть заданы как в векторном виде (для устройств от 5.0 и выше), так и в виде png/webp файлов для основных разрешений (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi).
Кнопоки like, dislike, share и favorite заданы файлами 
`ic_stories_status_like.xml`, `ic_stories_status_dislike.xml`, `ic_share_status.xml` и `ic_stories_status_favorite.xml`.
Разметка like, dislike и favorite представлена в виде selector. Для отображения статуса используется state_activated (true/false).

### Интеграция с SDK

##### События

Библиотека взаимодействует с помощью событийной модели, которая обеспечивается внутренним модулем CsEventBus (урезанная версия библиотеки [EventBus](http://greenrobot.org/ru-eventbus/))
На события, которые генерирует библиотека, можно подписать собственные объекты. 
Делается это следующим образом:

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
    }
     
    @CsSubscribe
    public void handleSomethingElse(SomeOtherEvent event) {
        doSomethingWith(event);
    }

Все подписчики на события должны в соответствии со своим lifecycle регистрироваться и по возможности отключаться от CsEventBus. Например:

    @Override
    public void onStart() {
        super.onStart();
        CsEventBus.getDefault().register(this);
    }
     
    @Override
    public void onStop() {
        CsEventBus.getDefault().unregister(this);
        super.onStop();
    }

Для отправки событий в SDK используется метод post(Event event). Например:

    CsEventBus.getDefault().post(new MessageEvent("Hello everyone!"));

Ниже перечислены 10 событий, на которые можно подписаться:

1) StoriesLoaded - список сториз загрузился, виджет готов к работе (срабатывает каждый раз при загрузке списка, в том числе и при refresh). Событие содержит метод getCount() - количество сториз.

Все события со 2 по 10 содержат 4 метода (некоторые события помимо этих 4 - содержат еще и дополнительные) 
 
    int getId() - получение id сториз
    String getTitle() - получение заголовка сториз
    ArrayList<String> getTags() - получение тегов сториз
    int getSlidesCount() - количество слайдов

2) ClickOnStory - клик по сториз в списке (и в обычном списке и в избранном). Дополнительный метод int getSource(), может возвращать значения ClickOnStory.LIST, ClickOnStory.FAVORITE.

3) ShowStory - показ ридера со сториз (после клика или перелистывания в обычном списке, в избранном, одиночной сториз или онбордингов). Дополнительный метод int getSource(), может возвращать значения ShowStory.SINGLE, ShowStory.ONBOARDING, ShowStory.LIST, ShowStory.FAVORITE

Все события со 4 по 10 содержат метод int getIndex() - с какого слайда было вызвано событие

4) CloseStory - закрытие сториз. Дополнительные методы: 
- int getAction(), может возвращать значения CloseStory.AUTO, CloseStory.CLICK, CloseStory.SWIPE, CloseStory.CUSTOM
- int getSource(), может возвращать значения ShowStory.SINGLE, ShowStory.ONBOARDING, ShowStory.LIST, ShowStory.FAVORITE

5) ClickOnButton - клик по кнопке в сториз. Дополнительный метож getLink(), возвращает ссылку, передаваемую в кнопке.

6) ShowSlide - показ слайда.

7) ClickOnShareStory - нажатие на кнопку поделиться.

Все события со 8 по 10 содержат метод boolean getValue() - в каком состоянии находится кнопка (true - активирована)

8) LikeStory - клик на кнопку лайка
9) DislikeStory - клик на кнопку дизлайка
10) FavoriteStory - клик на кнопку добавления сториз в избранное

Также есть 2 события для отслеживания ошибок:

1) `StoriesErrorEvent` - возникает, если с сервера приходит какая-то ошибка. Содержит 7 разных типов в зависимости от места возникновения. Имеет метод getType для получения типа ошибки. Ниже перечислены типы ошибок:

    OPEN_SESSION = 0;
    LOAD_LIST = 1;
    LOAD_SINGLE = 2;
    LOAD_ONBOARD = 3;
    READER = 4;
    EMPTY_LINK = 5;
    CACHE = 6;

2)`NoConnectionEvent` - при попытке загрузить без интернета. Имеет метод getType для получения типа ошибки.

Помимо этого есть событие на закрытие ридера `CloseStoryReaderEvent`, которое можно вызывать через 

    CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CUSTOM));
    

##### Onboarding сториз и одиночные сториз

Библиотека поддерживает работу с onboarding сториз. 
Функция загрузки онбордингов следующая:

    InAppStoryManager.getInstance().showOnboardingStories(List<String> tags, Context context, AppearanceManager manager);

В функцию передается список тегов (в случае, если пустой - берется из изначального билдера), контекст и менеджер отображения (используется для определения положения кнопки закрытия и анимации в ридере).

Также может быть необходимость производить какое-то действие в приложении сразу после загрузки онбордингов (или в случае, если они не смогли показаться, так как уже все были отображены ранее или произошла какая-то ошибка). В этом случае необходимо использовать перегрузку

    InAppStoryManager.getInstance().onboardLoadedListener = new InAppStoryManager.OnboardingLoadedListener() {
            @Override
            public void onLoad() {

                doActionLoad(); // данное событие вызывается, когда ридер еще открыт. Если необходимо сделать какое-то действие по закрытию ридера, то можно подписаться на событие CloseStory.
            }

            @Override
            public void onEmpty() {

                doActionEmpty();
            }

            @Override
            public void onError() {

                doActionError();
            }
        };

Помимо этого есть возможность открытия одной сториз по ее id|slug.

    InAppStoryManager.getInstance().showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback);

    public interface IShowStoryCallback {
        void onShow();

        void onError();
    }

Методы интерфейса вызывается в случае успешной/неуспешной попытки загрузки сториз и могут быть, например, использованы разработчиком для изменения состояний каких либо внешних элементов в приложении. 

Функция позволяет загружать все сториз, в том числе и те, которые отсутствуют в списке, возвращенном пользователю. 

##### Обработчики

Обработчик кликов на кнопки в сториз задается в InAppStoryManager через метод

    InAppStoryManager.getInstance().setUrlClickCallback(UrlClickCallback callback);

Интерфейс `UrlClickCallback` содержит метод `onUrlClick(String url)`, который и необходимо перегрузить.

Например:

    InAppStoryManager.getInstance().setUrlClickCallback(new InAppStoryManager.UrlClickCallback() {
            @Override
            public void onUrlClick(String link) {
                Toast.makeText(context, link, Toast.LENGTH_LONG).show();
            }
        });
Если нужно закрывать ридер при срабатывании обработчика, то необходимо в `onUrlClick` добавить вызов события `CloseStoryReaderEvent`

В SDK заложен дефолтный обработчик ссылок.
    
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(object.getLink().getTarget()));
    startActivity(i);
    
При перегрузке он не используется, поэтому если хочется сохранить обработку ссылок, которые не востребованы приложением в дефолтном виде, то необходимо их учесть при перегрузке.

Также можно перегрузить обработчик нажатия на кнопку шаринга следующим образом

    InAppStoryManager.getInstance().shareCallback = new InAppStoryManager.ShareCallback() {
            @Override
            public void onShare(String url, String title, String description, String shareId) {
                doAction(url, title, description);
            }
        };
        


FAQ
1) Изменение формы ячейки: прямоугольник, круг
Для того, чтобы задать прямоугольную ячейку - в AppearanceManager можно использовать `csListItemWidth(int width)`, `csListItemHeight(int height)`. В случае, если необходима круглая ячейка - необходимо использовать кастомизацию через `csListItemInterface`.

2) Кастомный шрифт
Для кастомизации шрифта ячейки - в AppearanceManager используем `csCustomFont(Typeface font)`. Кастомизации шрифта в ридере нету, шрифт для сториз автоматически подгружается с сервера.

3) Смена положения таймера/крестика
В AppearanceManager используется `csClosePosition`.

4) Изменение лоадера
В глобальном AppearanceManager используется кастомизация через csLoaderView.

5) Задание обработчика для кнопок
Используем метод `InAppStoryManager.getInstance().setUrlClickCallback(InAppStoryManager.UrlClickCallback callback)`. Также в callback возможно необходимо будет добавить закрытие ридера через

    CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CUSTOM)).

6) Смена аккаунта в приложении
Используем метод `InAppStoryManager.getInstance().setUserId(String userId)`.

7) Добавление PTR
В колбеке PTR метода необходимо добавить `storiesList.loadStories()`.

8) Изменение тегов у пользователя
Используем методы 

    InAppStoryManager.getInstance().setTags(ArrayList<String> tags);
    InAppStoryManager.getInstance().addTags(ArrayList<String> tags);
    InAppStoryManager.getInstance().removeTags(ArrayList<String> tags);

9) Добавление избранного
При инициализации `InAppStoryManager.Builder()` используем свойство `hasFavorite(true)`. В случае кастомизации внешнего вида ячеек списка через `IStoriesListItem csListItemInterface`, необходимо также кастомизировать внешний вид ячейки для избранного с помощью интерфейса `IGetFavoriteListItem csFavoriteListItemInterface`. Помимо этого для взаимодействия с ячейкой избранного добавляем обработчик `storiesList.setOnFavoriteItemClick(StoriesList.OnFavoriteItemClick callback)`. При отображении списка избранных в xml-разметку со списком необходимо добавить аттрибут `cs_listIsFavorite`.

10) Открытие сториз из push-уведомления
В функции-обработчике push-уведомления добавляем вызов единичной сториз с помощью функции `InAppStoryManager.getInstance().showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback)`.

11) Онбоардинг
Используем вызов `InAppStoryManager.getInstance().showOnboardingStories(List<String> tags, Context context, AppearanceManager manager)`.

12) Лайки/дизлайки
При инициализации `InAppStoryManager.Builder()` используем свойство `hasLike(true)`.

13) Шаринг
При инициализации `InAppStoryManager.Builder()` используем свойство `hasShare(true)`. Так же возможна кастомизация обработчика `InAppStoryManager.getInstance().shareCallback`.
