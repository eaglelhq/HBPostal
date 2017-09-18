package com.ksource.hbStore.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jeesoft.widget.pickerview.CharacterPickerView;

/**
 * 地址选择器
 *
 * @version 0.1 king 2015-10
 */
public class OptionsWindowHelper {

    private static List<String> options1Items = null;
    private static List<List<String>> options2Items = null;
    private static List<List<List<String>>> options3Items = null;


    public interface OnOptionsSelectListener {
        void onOptionsSelect(String province, String city, String area);
    }

    private OptionsWindowHelper() {
    }

//    public static CharacterPickerWindow builder(Activity activity, final OnOptionsSelectListener listener) {
//        //选项选择器
//        CharacterPickerWindow mOptions = new CharacterPickerWindow(activity);
//        //初始化选项数据
//        setThreePickerData(mOptions.getPickerView(),ArrayDataDemo.getAll());
//        //设置默认选中的三级项目
//        mOptions.setSelectOptions(0, 0, 0);
//        //监听确定选择按钮
//        mOptions.setOnoptionsSelectListener(new OnOptionChangedListener() {
//            @Override
//            public void onOptionChanged(int option1, int option2, int option3) {
//                if (listener != null) {
//                    String province = options1Items.get(option1);
//                    String city = options2Items.get(option1).get(option2);
//                    String area = options3Items.get(option1).get(option2).get(option3);
//                    listener.onOptionsSelect(province, city, area);
//                }
//            }
//        });
//        return mOptions;
//    }

    /**
     * 初始化选项数据
     */
    public static void setThreePickerData(CharacterPickerView view , Map<String, Map<String, List<String>>> allCitys) {
        if (options1Items == null) {
            options1Items = new ArrayList<>();
            options2Items = new ArrayList<>();
            options3Items = new ArrayList<>();
//            final Map<String, Map<String, List<String>>>
//                    allCitys = ArrayDataDemo.getAll();
            for (Map.Entry<String, Map<String, List<String>>> entry1 : allCitys.entrySet()) {
                String key1 = entry1.getKey();
                Map<String, List<String>> value1 = entry1.getValue();

                options1Items.add(key1);

                List<String> options2Items01 = new ArrayList<>();
                List<List<String>> options3Items01 = new ArrayList<>();
                for (Map.Entry<String, List<String>> entry2 : value1.entrySet()) {
                    String key2 = entry2.getKey();
                    List<String> value2 = entry2.getValue();

                    options2Items01.add(key2);
//                    Collections.sort(value2);
                    options3Items01.add(new ArrayList<>(value2));
                }
//                Collections.sort(options2Items01);
                options2Items.add(options2Items01);
                options3Items.add(options3Items01);
            }
//            Collections.sort(options1Items);
//            for (int i = 0; i < 10; i++) {
//                options1Items.add("省"+i);
//                for (int j = 0; j < 10; j++) {
//                    List<String> list2 = new ArrayList<>();
//                    list2.add("市"+i+j);
//                    options2Items.add(new ArrayList<>(list2));
//                    for (int k = 0; k < 10; k++) {
//                        List<String> list3 = new ArrayList<>();
//                        list3.add("县"+i+j+k);
//                        ArrayList<List<String>> object = new ArrayList<>();
//                        object.add(list3);
//                        options3Items.add(object);
//                    }
//                }
//            }
        }

        //三级联动效果
        view.setPicker(options1Items, options2Items, options3Items);
//        view.setPicker(options1Items, options2Items);

    }
 /**
     * 初始化选项数据
     */
    public static void setTwoPickerData(CharacterPickerView view , Map<String, Map<String, List<String>>> allCitys) {
        if (options1Items == null) {
            options1Items = new ArrayList<>();
            options2Items = new ArrayList<>();
//            options3Items = new ArrayList<>();
//            final Map<String, Map<String, List<String>>>
//                    allCitys = ArrayDataDemo.getAll();
            for (Map.Entry<String, Map<String, List<String>>> entry1 : allCitys.entrySet()) {
                String key1 = entry1.getKey();
                Map<String, List<String>> value1 = entry1.getValue();

                options1Items.add(key1);

                List<String> options2Items01 = new ArrayList<>();
                List<List<String>> options3Items01 = new ArrayList<>();
                for (Map.Entry<String, List<String>> entry2 : value1.entrySet()) {
                    String key2 = entry2.getKey();
                    List<String> value2 = entry2.getValue();

                    options2Items01.add(key2);
//                    Collections.sort(value2);
//                    options3Items01.add(new ArrayList<>(value2));
                }
//                Collections.sort(options2Items01);
                options2Items.add(options2Items01);
//                options3Items.add(options3Items01);
            }
//            Collections.sort(options1Items);
        }

        //二级联动效果
        view.setPicker(options1Items, options2Items);

    }

}
