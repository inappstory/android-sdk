## Stories Reader Goods Widget

In stories you can add goods widget. It can be represented as horizontal list of items (default implementation with RecyclerView) or you can fully customize it.  
If you want to use widget you should set `csCustomGoodsWidget` interface in common `AppearanceManager` instance. 
Example:
```
public class GoodsItemData {
    public GoodsItemData(String sku, 
                         String title, 
                         String description, 
                         String image, 
                         String price, 
                         String oldPrice, 
                         Object raw);
}

public interface GetGoodsDataCallback {
    void onSuccess(ArrayList<GoodsItemData> data);
    void onError();
    void onClose(); //Use if you want to close goods widget.
    void itemClick(String sku); //Use to send click statistic in custom widget
}

AppearanceManager.getCommonInstance().csCustomGoodsWidget(new ICustomGoodsWidget() {
    @Override
    public View getWidgetView() {
        ...
    }

    @Override
    public ICustomGoodsItem getItem() {
        ...
    }

    @Override
    public IGoodsWidgetAppearance getWidgetAppearance() {
        ...
    }

    @Override
    public RecyclerView.ItemDecoration getDecoration() {
        ...
    }

    @Override
    public void getSkus(ArrayList<String> skus, GetGoodsDataCallback callback) {
        //In this method you should always call 
        //callback.onSuccess(ArrayList<GoodsItemData> data)
        //or callback.onError();
        ...
    }

    @Override
    public void onItemClick(GoodsItemData sku) {
        ...
        //This action does not close stories reader and game reader. 
        //If you want to close readers, you should call `InAppStoryManager.closeStoryReader()` for closing all readers and widget
    }
});
```
If you want use default implementation (RecyclerView) than method `getWidgetView()` should return null. In that case you override other methods like `getItem()`, `getWidgetAppearance()`, `getDecoration()`, `onItemClick()` as you need. Otherwise that methods won't be called and could return null values. 
Method `getItem()` returns next interface:
```
public interface ICustomGoodsItem {
    @NonNull
    View getView();

    void bindView(View view, GoodsItemData data);
}
```
Here is an example for this method:
```
@Override
public ICustomGoodsItem getItem() {
    return new ICustomGoodsItem() {
        @NonNull
        @Override
        public View getView() {
            return LayoutInflater.from(getContext()).inflate(R.layout.custom_goods_list_item,
                   null, false);
        }

        @Override
        public void bindView(View view, GoodsItemData goodsItemData) {
            ((TextView) view.findViewById(R.id.title)).setText(goodsItemData.title);
            loadImage((ImageView)view.findViewById(R.id.image), goodsItemData.image);
        }
    };
}
```
Also you can use method `getWidgetAppearance()` if you want customize other parts of widget (background line, close button). Also for simple usage you can override GoodsWidgetAppearanceAdapter() instead of interface. Method returns next interface:
```
public interface IGoodsWidgetAppearance {
    int getBackgroundHeight();
    int getBackgroundColor();
    int getDimColor();
    Drawable getCloseButtonImage();
    int getCloseButtonColor();
}
```
Here is an example for this method:
```
@Override
public ICustomGoodsItem get() {
    return new GoodsWidgetAppearanceAdapter() {
        @Override
        public int getBackgroundColor() {
            return Color.BLUE;
        }
    };
}
```
In method `getSkus()` you get ids for goods items. When you get data for this items from your application, you should create array of GoodsItemData items and call getGoodsDataCallback.onSuccess(ArrayList<GoodsItemData> data). Also in case of any error in retreiving data for items, you should call `getGoodsDataCallback.onError().`
Here is an example for this method:
```
@Override
public void getSkus(ArrayList<String> skus, GetGoodsDataCallback callback) {
    ArrayList<GoodsItemData> goodsItemData = new ArrayList<>();
    HashMap<String, CustomGoodsItem> goods = getGoodsItemsFromServer(skus)
    for (String sku : skus) {
        CustomGoodsItem item = goods.get(sku)
        GoodsItemData data = new GoodsItemData(sku, item.title, item.description, item.imageLink, item.price, item.oldPrice, item);
        //last variable can be used in case if you want to represent any additional fields in custom cell with `ICustomGoodsItem.bindView()`
        //or get your object in `onItemClick()`
        goodsItemData.add(data);
    }
    callback.onSuccess(goodsItemData);
}
```
If you want to fully customize your widget, you should override `getWidgetView()` to return NonNull view. In that case all binding logic should be in `getSkus()` method. For example:
```
AppearanceManager.getCommonInstance().csCustomGoodsWidget(new ICustomGoodsWidget() {
                RelativeLayout container;

                @Override
                public View getWidgetView() {
                    container =
                            (RelativeLayout) View.inflate(context,
                                    R.layout.custom_goods_widget, null);
                    return container;
                }

                @Override
                public ICustomGoodsItem getItem() {
                    return null;
                }

                @Override
                public RecyclerView.ItemDecoration getDecoration() {
                    return null;
                }

                @Override
                public void getSkus(ArrayList<String> skus, GetGoodsDataCallback getGoodsDataCallback) {
                    if (container != null && skus != null) {
                        getGoodsDataCallback.onSuccess(new ArrayList<>());
                        for (String sku : skus) {
                            TextView textView = new TextView(context);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            textView.setLayoutParams(lp);
                            textView.setText(sku);
                            textView.setOnClickListener(v1 -> {
                                getGoodsDataCallback.onItemClick(sku);
                                Toast.makeText(context, textView.getText(), Toast.LENGTH_LONG).show();
                            });
                            ((LinearLayout) container.findViewById(R.id.container)).addView(textView);

                        }
                        container.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getGoodsDataCallback.onClose();
                            }
                        });
                    }
                }

                @Override
                public void onItemClick(GoodsItemData goodsItemData) {

                }
            });
```
