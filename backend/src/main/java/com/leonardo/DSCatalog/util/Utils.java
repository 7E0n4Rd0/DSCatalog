package com.leonardo.DSCatalog.util;

import com.leonardo.DSCatalog.projections.IdProjection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    /**
     * A method responsible to order a generic list that
     * extends {@link IdProjection} interface.
     * @implNote the return of this method is warned as {@code unchecked} by the compiler
     * due to not predict what would return, cast the value to type properly
     * @param <ID> id type of the list element or object
     * @param ordered to get sort reference based on elements position
     * @param unordered to order based in ordered list model
     * @return unordered list with ordered data
     */
    public static <ID> List<? extends IdProjection<ID>> replace(List<? extends IdProjection<ID>> ordered, List<? extends IdProjection<ID>> unordered) {
        Map<ID, IdProjection<ID>> map = new HashMap<>();
        for (IdProjection<ID> obj : unordered){
            map.put(obj.getId(), obj);
        }

        List<IdProjection<ID>> result = new ArrayList<>();
        for (IdProjection<ID> obj : ordered){
            result.add(map.get(obj.getId()));
        }

        return result;
    }
}
