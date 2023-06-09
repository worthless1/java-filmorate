package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class Genre implements Comparable<Genre> {
    int id;
    String name;

    @Override
    public int compareTo(Genre other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return id == genre.id &&
                Objects.equals(name, genre.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
