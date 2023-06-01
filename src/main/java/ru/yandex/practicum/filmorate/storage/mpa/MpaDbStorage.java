package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excepton.mpa.MpaDoesntExistException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getMpa() {
        return jdbcTemplate.query("SELECT * FROM Mpa_ratings", new MpaMapper());
    }

    public Mpa getMpaById(int id) {
        return jdbcTemplate.query("SELECT * FROM Mpa_ratings WHERE Mpa_id=?", new MpaMapper(), id)
                .stream().findAny()
                .orElseThrow(() -> new MpaDoesntExistException("Mpa with id: " + id + " does not exists"));
    }

}
