package com.blossom.blossomtest.helper;

import com.blossom.blossomtest.exception.InvalidFilterException;
import com.blossom.blossomtest.model.product.Product;
import com.blossom.blossomtest.util.Constants;

import java.lang.reflect.Field;
import java.util.Map;

public class ItemFilterHelper {

    public static boolean matches(Product item, Map<String, String> filters) {
        return matchesBasicFields(item, filters) && matchesComplexFields(item, filters);
    }

    private static boolean matchesBasicFields(Product item, Map<String, String> filters) {
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (isComplexField(key)) continue;

            try {
                Field field = Product.class.getDeclaredField(key);
                field.setAccessible(true);
                Object fieldValue = field.get(item);

                if (fieldValue == null) {
                    return false;
                }

                if (Constants.FILTER_FIELDS.contains(key.toLowerCase())) {
                    if (!(fieldValue instanceof String fieldStr) ||
                            !fieldStr.toLowerCase().contains(value.toLowerCase())) {
                        return false;
                    }
                } else {
                    if (!fieldValue.toString().equalsIgnoreCase(value)) {
                        return false;
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new InvalidFilterException("Invalid filter key: " + key);
            }
        }
        return true;
    }

    private static boolean matchesComplexFields(Product item, Map<String, String> filters) {
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch (key) {
                case "price":
                    if (item.getPrice() == null || item.getPrice() != Double.parseDouble(value)) {
                        return false;
                    }
                    break;
                case "minPrice":
                    if (item.getPrice() == null || item.getPrice() < Double.parseDouble(value)) {
                        return false;
                    }
                    break;

                case "maxPrice":
                    if (item.getPrice() == null || item.getPrice() > Double.parseDouble(value)) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }


    private static boolean isComplexField(String key) {
        return key.equals("price")
                || key.equals("minPrice")
                || key.equals("maxPrice");
    }
}
