package org.example.model.Friendhsip;

import org.example.model.Users.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship {
    private final Long idUser1;
    private final Long idUser2;
    private final LocalDateTime createdAt;

    public Friendship(Long idUser1, Long idUser2, LocalDateTime createdAt) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.createdAt = createdAt;
    }

    public Long getIdUser1() {
        return idUser1;
    }

    public Long getIdUser2() {
        return idUser2;
    }

    public LocalDateTime getcreatedAt() {
        return createdAt;
    }
    public boolean involves(User user) {
        return idUser1.equals(user) || idUser2.equals(user);
    }

    public Long getOther(User user) {
        if (user.getId() == idUser1) return idUser2;
        else if (user.getId() == idUser2) return idUser1;
        else return null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Friendship f)) return false;
        return (idUser1.equals(f.idUser1) && idUser2.equals(f.idUser2)) ||
                (idUser1.equals(f.idUser2) && idUser2.equals(f.idUser1));
    }

    @Override
    public int hashCode() {
        Long min = Math.min(idUser1, idUser2);
        Long max = Math.max(idUser1, idUser2);
        return Objects.hash(min, max);
    }
}
