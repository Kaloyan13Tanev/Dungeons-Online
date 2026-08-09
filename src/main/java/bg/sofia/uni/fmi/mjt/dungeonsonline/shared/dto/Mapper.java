package bg.sofia.uni.fmi.mjt.dungeonsonline.shared.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface Mapper<D, T> {

    T toDTO(D domain);

    default List<T> toDTOs(Collection<? extends D> domains) {
        List<T> mapped = new ArrayList<>(domains.size());

        for (D domain : domains) {
            mapped.add(toDTO(domain));
        }

        return Collections.unmodifiableList(mapped);
    }

}
