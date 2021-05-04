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

    implementation 'com.github.inappstory:android-sdk:1.1.6'

Также для корректной работы в dependencies нужно добавить :

    implementation 'androidx.recyclerview:recyclerview:1.1.0'
    implementation 'androidx.webkit:webkit:1.4.0'  

##### Дополнительные ограничения

Если в проекте используется proguard-обфускация кода, то в файле `proguard-rules.pro` необходимо прописать:

    -keepattributes *Annotation*
    
    -keepclassmembers class * {
        @com.inappstory.sdk.eventbus.CsSubscribe <methods>;
    }
    
    -keep enum com.inappstory.sdk.eventbus.CsThreadMode { *; }

    keepclassmembers class fqcn.of.javascript.interface.for.webview {
        public *;
    }

    -keep public class com.inappstory.sdk.** {
        *;
    }

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
    String userId //уникальный текстовый идентификатор пользователя (id, login, etc...), который обращается к stories, опциональный параметр.
    String apiKey //по умолчанию ключ берется из строки csApiKey, если есть желание изменять его на лету, можно воспользоваться этим параметром. Опциональный.
    String testKey //тестовый интеграционный ключ для тестирования stories на устройстве. Опционален, по умолчанию отсутствует.
    boolean closeOnSwipe //флаг, который отвечает за закрытие stories по смахиванию вниз. По умолчанию - true.
    boolean closeOnOverscroll //флаг, который отвечает за закрытие stories по смахиванию влево на последней story или вправо на первом. По умолчанию - true.
    boolean hasLike //флаг, который отвечает за подключение функционала like/dislike. По умолчанию - false (отключен).
    boolean hasShare//флаг, который отвечает за подключение функционала шаринга. По умолчанию - false (отключен).
    boolean hasFavorite //флаг, который отвечает за подключение функционала избранных stories. По умолчанию - false (отключен).
    ArrayList<String> tags //теги для таргетирования stories, опциональный параметр
    Map<String, String> placeholders //плейсхолдеры для замены специальных переменных в текстах сториз, опциональный параметр. Плейсхолдеры задаются без спецзнаков (%).

Пример инициализации `InAppStoryManager` выглядит следующим образом

    new InAppStoryManager.Builder()
            .context(context) 
            .closeOnSwipe(true)
            .closeOnOverscroll(true)
            .userId(userId)
            .apiKey(apiKey)
            .testKey(testKey)
            .tags(tags)
            .placeholders(placeholders)
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
    setPlaceholders(@NonNull Map<String, String> placeholders);
    Map<String, String> getPlaceholders();
    setPlaceholder(String key, String value); //используется как для установки, так и для удаления плейсхолдера, если в качетсве value передан null.

Они позволяет менять/добавлять/удалять теги без пересоздания InAppStoryManager.

Если приложение поддерживает несколько аккаунтов, то можно реализовать смену идентификатора пользователя. Для того, чтобы сменить уже заданный идентификатор, необходимо сначала вызвать функцию `InAppStoryService.getInstance().setUserId(userId)`

Для изменения параметра `apiKey` потребуется переинициализация `InAppStoryManager` (см пример инициализации). При этом старая будет очищена.
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
   
    boolean csIsDraggable - флаг, отвечающий за возможность закрытия ридера сториз по drag'n'drop. По умолчанию функционал включен и значение флага - true. Данный флаг задается     только для глобального AppearanceManager.

Пример задания параметров выглядит следующим образом

    appearanceManager
        .csListItemBorderColor(Color.RED)
        .csListItemMargin(0)
        .csClosePosition(AppearanceManager.BOTTOM_RIGHT)
        .csListItemTitleColor(Color.BLUE)
        .csListItemTitleSize(Sizes.dpToPxExt(20))

Также помимо этого в AppearanceManager есть несколько интерфейсов.
`IStoriesListItem csListItemInterface`, используется для полной кастомизации элементов списка.

    interface IStoriesListItem {
        View getView(); // здесь необходимо передать View - внешний вид ячейки.
        View getVideoView(); // здесь необходимо передать View - внешний вид ячейки на случай, если в ячейках используется видео на обложке.
        void setTitle(View itemView, String title, Integer titleColor); // itemView - текущая ячейка, в необходимой View используем заголовок story. Параметр titleColor может быть null.
        void setSource(View itemView, String source); // itemView - текущая ячейка, в необходимой View используем источник story.
        void setImage(View itemView, String url, int backgroundColor); // itemView - текущая ячейка, в необходимой View показываем обложку story или цвет фона в случае ее отсутствия.
        void setReaded(View itemView, boolean isReaded); // itemView - текущая ячейка, меняем ее по необходимости в случае если она прочитана.
        void setHasAudio(View itemView, boolean isReaded); // itemView - текущая ячейка, меняем ее по необходимости в случае если у данной сториз есть аудио внутри.
        void setHasVideo(View itemView, String videoUrl, String url, int backgroundColor); // itemView - текущая ячейка, в необходимой View показываем видеообложку story (videoUrl), постер видео (url) или цвет фона в случае его отсутствия. Для работы с ячейками видео рекомендуется использовать класс из библиотеки VideoPlayer в качестве контейнера для отображения видео и метод loadVideo(String videoUrl) для запуска. Данный класс предусматривает кэширование видеообложек. Класс VideoPlayer наследуется от TextureView
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
                        public View getVideoView() {
                            return LayoutInflater.from(MainActivity.this)
                                    .inflate(R.layout.custom_story_list_video_item, null, false);
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
                            itemView.findViewById(R.id.image).setBackgroundColor(backgroundColor);
                        }

                        @Override
                        public void setHasVideo(View itemView, String videoUrl, String url, int backgroundColor) {
                            itemView.findViewById(R.id.image).setBackgroundColor(backgroundColor);
                            ((VideoPlayer)itemView.findViewById(R.id.video)).loadVideo(videoUrl);
                        }

                        @Override
                        public void setReaded(View itemView, boolean isReaded) {

                        }

                        @Override
                        public void setHasAudio(View itemView, boolean hasAudio) {

                        }
                    });
    
`IGetFavoriteListItem csFavoriteListItemInterface`, используется для полной кастомизации элемента favorite в списке.

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

`ILoaderView iLoaderView` - используется для подстановки собственного лоадера вместо дефолтного. Задается в глобальном AppearanceManager.

        public interface ILoaderView {
            View getView();
        }

`IGameLoaderView iGameLoaderView` - используется для подстановки собственного лоадера вместо дефолтного на экране игр. Задается в глобальном AppearanceManager.

        public interface IGameLoaderView {
            View getView(); //При наследовании от интерфейса View должна возвращать сама себя.
            void setProgress(int progress, int max); //Значения прогресса - от 0 до 100, в качестве max передается 100. 
        }
        
`StoryTouchListener csStoryTouchListener` - используется для добавления обработки клика на ячейки списков сториз (например, для анимации)

    public interface StoryTouchListener {
        void touchDown(View view, int position); //View - ячейка списка, position - позиция в списке

        void touchUp(View view, int position);
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

Библиотека использует 7 кнопок с иконками: sound, refresh, close, like, dislike, share и favorite Для кнопок refresh и close используются файлы `ic_refresh.xml`, `ic_close.xml`. Иконки могут быть заданы как в векторном виде (для устройств от 5.0 и выше), так и в виде png/webp файлов для основных разрешений (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi).
Кнопоки sound, like, dislike, share и favorite заданы файлами 
`ic_stories_status_sound.xml`, `ic_stories_status_like.xml`, `ic_stories_status_dislike.xml`, `ic_share_status.xml` и `ic_stories_status_favorite.xml`.
Разметка sound, like, dislike и favorite представлена в виде selector. Для отображения статуса используется state_activated (true/false).

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
    
На данный момент в SDK можно отправить 2 события

    CloseStoryReaderEvent - используется для закрытия ридера сториз (например при перегрузке клика на кнопки, шаринг и прочее)
    SoundOnOffEvent - вызывается после изменения флага включений/выключения звука (InAppStoryManager.getInstance().soundOn). В случае, если ридер закрыт, вызывать событие не требуется.

Ниже перечислены события, на которые можно подписаться:

1) StoriesLoaded - список сториз загрузился, виджет готов к работе (срабатывает каждый раз при загрузке списка, в том числе и при refresh). Событие содержит метод getCount() - количество сториз.

Все события со 2 по 10 содержат 4 метода (некоторые события помимо этих 4 - содержат еще и дополнительные) 
 
    int getId() - получение id сториз
    String getTitle() - получение заголовка сториз
    ArrayList<String> getTags() - получение тегов сториз
    int getSlidesCount() - количество слайдов

2) ClickOnStory - клик по сториз в списке (и в обычном списке и в избранном). Дополнительный метод int getSource(), может возвращать значения ClickOnStory.LIST, ClickOnStory.FAVORITE.

3) ShowStory - показ ридера со сториз (после клика или перелистывания в обычном списке, в избранном, одиночной сториз или онбордингов). Дополнительный метод int getSource(), может возвращать значения ShowStory.SINGLE, ShowStory.ONBOARDING, ShowStory.LIST, ShowStory.FAVORITE

Все события со 4 по 13 содержат метод int getIndex() - с какого слайда было вызвано событие

4) CloseStory - закрытие сториз. Дополнительные методы: 
- int getAction(), может возвращать значения CloseStory.AUTO, CloseStory.CLICK, CloseStory.SWIPE, CloseStory.CUSTOM
- int getSource(), может возвращать значения ShowStory.SINGLE, ShowStory.ONBOARDING, ShowStory.LIST, ShowStory.FAVORITE

5) ClickOnButton - клик по кнопке в сториз. Дополнительный метож getLink(), возвращает ссылку, передаваемую в кнопке.

6) ShowSlide - показ слайда.

7) ClickOnShareStory - нажатие на кнопку поделиться.

8) StartGame - клик на кнопку с открытием игры
9) CloseGame - при закрытии игры вручную (по крестику, back и т.д.).
10) FinishGame - по окончании игры (при автоматическом закрытии). Также содержит метод getResult(), который возвращает json строку с результатом игры.

Все события со 8 по 10 содержат метод boolean getValue() - в каком состоянии находится кнопка (true - активирована)

11) LikeStory - клик на кнопку лайка
12) DislikeStory - клик на кнопку дизлайка
13) FavoriteStory - клик на кнопку добавления сториз в избранное

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
    
Помимо этого для работы с онбордингами и одиночными сториз добавлены события:
`OnboardingLoad` - отправляется при подгрузке списка онбордингов. Содержит метод getCount, который возвращает количество онбординг сториз и isEmpty - флаг того, пустой списко вернулся по запросу или нет.
`OnboardingLoadError` - отправляется при подгрузке списка онбордингов в случае возникновения какой-то ошибки. 
`SingleLoad` - отправляется при загрузке единичной сториз по id (методом `InAppStoryManager.getInstance().showStory`). 
`SingleLoadError` - отправляется при загрузке единичной сториз по id в случае возникновения какой-то ошибки. 
    
##### Работа со звуком

За включение/выключение воспроизведения звука в сториз отвечает флаг `InAppStoryManager.getInstance().soundOn` (true - звук включен, false - выключен). Значение флага по умолчанию прописано в файле `constants.xml` в переменной `defaultMuted` (по умолчанию true - звук выключен) и может быть перегружено. Необходимо учитывать, что значение `soundOn` выставляется как `!soundMuted` (то есть по умолчанию будет false). 
Также флаг `InAppStoryManager.getInstance().soundOn` является публичным, потому можно (например после инициализации InAppStoryManager) задать его значение напрямую, например:

    InAppStoryManager.getInstance().soundOn = true;

В случае изменения значения при открытом ридере необходимо так же отправить событие `SoundOnOffEvent`. 

    CsEventBus.getDefault().post(new SoundOnOffEvent());    

Если ридер закрыт - отправка события не требуется.

##### Onboarding сториз и одиночные сториз

Библиотека поддерживает работу с onboarding сториз. 
Функция загрузки онбордингов следующая:

    InAppStoryManager.getInstance().showOnboardingStories(List<String> tags, Context context, AppearanceManager manager);

В функцию передается список тегов (в случае, если пустой - берется из изначального билдера), контекст и менеджер отображения (используется для определения положения кнопки закрытия и анимации в ридере).

Также может быть необходимость производить какое-то действие в приложении сразу после загрузки онбордингов (или в случае, если они не смогли показаться, так как уже все были отображены ранее или произошла какая-то ошибка). В этом случае необходимо подписаться на следующие события CsEventBus:
    
    OnboardingLoad - отправляется при подгрузке списка онбордингов.
    OnboardingLoadError - отправляется при подгрузке списка онбордингов в случае возникновения какой-то ошибки. 

Помимо этого есть возможность открытия одной сториз по ее id|slug.

    InAppStoryManager.getInstance().showStory(String storyId, Context context, AppearanceManager manager);

В случае успешной/неуспешной попытки загрузки сториз вызываются события, на которые может подписаться разработчик для изменения состояний каких либо внешних элементов в приложении.  

    SingleLoad - отправляется при загрузке единичной сториз по id (методом `InAppStoryManager.getInstance().showStory`). 
    SingleLoadError - отправляется при загрузке единичной сториз по id в случае возникновения какой-то ошибки. 

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
        
##### Виджет

При создании виджета есть возможность добавления списка сториз. При этом будут отображены первые 4 элемента списка.
Для этого необходимо задать свойства списка с помощью метода: 

    AppearanceManager.csWidgetAppearance(Context context, //контекст, лучше всего передавать контекст виджета, обязательный параметр 
                                        Class widgetClass //класс виджета (WidgetName.class), обязательный параметр 
                                        Integer itemCornerRadius, //радиус углов ячеек списка, опционально 
                                        Boolean sandbox) //тестовый или продуктовый сервер для подключения, опционально

Список представляет из себя GridView, поэтому при разметке виджета необходимо добавить соответствующий элемент, например

    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_horizontal"
        android:orientation="vertical">
        ...
        <GridView
            android:id="@+id/storiesGrid"
            android:layout_width="320dp"
            android:layout_height="90dp"
            android:layout_margin="8dp"
            android:horizontalSpacing="6dp"
            android:numColumns="4"
            android:verticalSpacing="6dp" />
        ...
    </LinearLayout>

В файле манифеста виджет необходимо задать фильтр на события

    <receiver
        android:name=".MyWidget"
        android:label="MyWidget">
        <intent-filter>
            <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            <action android:name="ias_w.UPDATE_WIDGETS"/> //приходит, когда возникает необходимость подгрузить список с сервера
            <action android:name="ias_w.UPDATE_SUCCESS_WIDGETS"/> //приходит в случае успешного получения непустого списка сториз с сервера
            <action android:name="ias_w.UPDATE_NO_CONNECTION"/> //приходит в случае если при попытке получить список с сервера не удалось соединиться с интернетом
            <action android:name="ias_w.UPDATE_EMPTY_WIDGETS"/> //приходит в случае получения пустого списка сториз с сервера
            <action android:name="ias_w.UPDATE_AUTH"/> //приходит в случае если пользователь не авторизован в InAppStorySDK
            <action android:name="ias_w.CLICK_ITEM"/> //приходит при нажатии на элемент списка сториз виджета
        </intent-filter>
        <meta-data
            android:name="android.appwidget.provider"
            android:resource="@xml/widget_metadata"/>                 
    </receiver>

Соответствующие событиям константы заданы следующим образом:

    public static final String UPDATE = "ias_w.UPDATE_WIDGETS";
    public static final String CLICK_ITEM = "ias_w.CLICK_ITEM";
    public static final String POSITION = "item_position";
    public static final String ID = "item_id";
    public static final String UPDATE_SUCCESS = "ias_w.UPDATE_SUCCESS_WIDGETS";
    public static final String UPDATE_EMPTY = "ias_w.UPDATE_EMPTY_WIDGETS";
    public static final String UPDATE_NO_CONNECTION = "ias_w.UPDATE_NO_CONNECTION";
    public static final String UPDATE_AUTH = "ias_w.UPDATE_AUTH";

Таким образом в методе onReceive виджета можно подписаться на них, например

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(UPDATE_SUCCESS)) {
            createSuccessData(context);
        } else if (intent.getAction().equalsIgnoreCase(UPDATE)) {
            try {
                StoriesWidgetService.loadData(context);
            } catch (DataException e) {
                e.printStackTrace();
            }
        } else if (intent.getAction().equalsIgnoreCase(UPDATE_EMPTY)) {
            createEmptyWidget();
        } else if (intent.getAction().equalsIgnoreCase(UPDATE_AUTH)) {
            createAuthWidget();
        } else if (intent.getAction().equalsIgnoreCase(UPDATE_NO_CONNECTION)) {
            createNoConnectionWidget();
        } else if (intent.getAction().equalsIgnoreCase(CLICK_ITEM)) {
            int itemId = intent.getIntExtra(StoriesWidgetService.ID, -1);
            int itemPos = intent.getIntExtra(StoriesWidgetService.POSITION, -1);
            if (itemPos != -1) {
                Toast.makeText(context, "Clicked on item " + itemPos + ", id " + itemId,
                        Toast.LENGTH_LONG).show();
            }
        }
        super.onReceive(context, intent);
    }

Пример функции createSuccessData():

    void createSuccessData(final Context context) {
        ComponentName thisAppWidget = new ComponentName(
                context.getPackageName(), getClass().getName());
        final AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        final int appWidgetIds[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int i = 0; i < appWidgetIds.length; ++i) {

            Intent intent = new Intent(context, StoriesWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.cs_widget_stories_list);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            rv.setRemoteAdapter(appWidgetIds[i], R.id.storiesGrid, intent);
            setClick(rv, context, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.storiesGrid);
        }
    }

    void setClick(RemoteViews rv, Context context, int appWidgetId) {
        Intent listClickIntent = new Intent(context, MyWidget.class);
        listClickIntent.setAction(CLICK_ITEM);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0,
                listClickIntent, 0);
        rv.setPendingIntentTemplate(R.id.storiesGrid, listClickPIntent);
    }

Метод `StoriesWidgetService.loadData(Context context)` используется непосредственно для загрузки списка. Его можно вызывать, например из метода onUpdate или onEnabled виджета. Например:

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    StoriesWidgetService.loadData(context);
                } catch (DataException e) {
                    e.printStackTrace();
                }
            }
        }, 500);
        updateData(appWidgetManager, context, appWidgetIds);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

По умолчанию ячейки списка виджета квадратные, 70x70. Она задана в файле `cs_widget_grid_item.xml`. Для изменения необходимо перегрузить данный файл, сохранив при этом идентификаторы и тип элеметов container, title, image. Элемент container задает пропорции ячеек, поэтому размеры ячейки необходимо определять в нем. Разметка ячейки выглядит следующим образом:

    <?xml version="1.0" encoding="utf-8"?>
    <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:gravity="center_horizontal"
        android:orientation="vertical">

        <RelativeLayout
            android:id="@+id/container"
            android:layout_width="70dp"
            android:clickable="true"
            android:layout_height="70dp">

            <ImageView
                android:id="@+id/image"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:clickable="false"
                android:gravity="center"
                android:scaleType="fitCenter" />
            <TextView
                android:id="@+id/title"
                android:layout_width="match_parent"
                android:maxWidth="55dp"
                android:clickable="false"
                android:padding="8dp"
                android:textSize="10sp"
                android:layout_height="wrap_content"
                android:layout_centerInParent="true"
                android:layout_alignParentBottom="true"
                android:maxLines="3"
                android:textColor="@color/white" />
        </RelativeLayout>
    </FrameLayout>

FAQ
1) Изменение формы ячейки: прямоугольник, круг
Для того, чтобы задать прямоугольную ячейку - в AppearanceManager можно использовать `csListItemWidth(int width)`, `csListItemHeight(int height)`. В случае, если необходима круглая ячейка - необходимо использовать кастомизацию через `csListItemInterface`.

2) Кастомный шрифт
Для кастомизации шрифта ячейки - в AppearanceManager используем `csCustomFont(Typeface font)`. Кастомизации шрифта в ридере нету, шрифт для сториз автоматически подгружается с сервера.

3) Смена положения таймера/крестика
В AppearanceManager используется `csClosePosition`.

4) Изменение лоадера в ридере сториз
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
В функции-обработчике push-уведомления добавляем вызов единичной сториз с помощью функции `InAppStoryManager.getInstance().showStory(String storyId, Context context, AppearanceManager manager)`.

11) Онбоардинг
Используем вызов `InAppStoryManager.getInstance().showOnboardingStories(List<String> tags, Context context, AppearanceManager manager)`.

12) Лайки/дизлайки
При инициализации `InAppStoryManager.Builder()` используем свойство `hasLike(true)`.

13) Шаринг
При инициализации `InAppStoryManager.Builder()` используем свойство `hasShare(true)`. Так же возможна кастомизация обработчика `InAppStoryManager.getInstance().shareCallback`.

14) Включение/выключение звука по умолчанию
В файле `constants.xml` в переменной `defaultMuted` задачем значение. Если true, то по умолчанию звук будет выключен, если false - включен.

15) Включение/выключение звука в runtime
Меняем значение флага `InAppStoryManager.getInstance().soundOn`. Например:
    
    InAppStoryManager.getInstance().soundOn = true;

В случае изменения значения при открытом ридере необходимо так же отправить событие `SoundOnOffEvent`. 

    CsEventBus.getDefault().post(new SoundOnOffEvent()); 
