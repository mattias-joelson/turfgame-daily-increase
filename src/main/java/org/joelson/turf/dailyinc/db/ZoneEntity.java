package org.joelson.turf.dailyinc.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.joelson.turf.dailyinc.model.ZoneData;
import org.joelson.turf.util.StringUtil;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "zones",
        indexes = { @Index(columnList = "id", unique = true), @Index(columnList = "name") })
public class ZoneEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(updatable = false, nullable = false)
    private int id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Instant time;

    public ZoneEntity() {
    }

    static ZoneEntity build(int id, @NotNull String name, @NotNull Instant time) {
        ZoneEntity zoneEntity = new ZoneEntity();
        zoneEntity.setId(id);
        zoneEntity.setName(name);
        zoneEntity.setTime(time);
        return zoneEntity;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = StringUtil.requireNotNullAndNotEmpty(name, "Name can not be null", "Name can not be empty");
    }

    public @NotNull Instant getTime() {
        return time;
    }

    public void setTime(@NotNull Instant time) {
        this.time = Objects.requireNonNull(time);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ZoneEntity that) {
            return id == that.id && Objects.equals(name, that.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ZoneEntity[%s]", EntityUtil.toStringPart(this));
    }

    public ZoneData toData() {
        return new ZoneData(id, name);
    }
}
