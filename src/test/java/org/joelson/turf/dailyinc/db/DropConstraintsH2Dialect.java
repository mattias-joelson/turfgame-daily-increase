package org.joelson.turf.dailyinc.db;

import org.hibernate.dialect.H2Dialect;

public class DropConstraintsH2Dialect extends H2Dialect {

    @Override
    public boolean dropConstraints() {
        return true;
    }
}