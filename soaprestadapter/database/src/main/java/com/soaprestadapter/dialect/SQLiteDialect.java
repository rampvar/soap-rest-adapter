package com.soaprestadapter.dialect;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.BlobJdbcType;
import org.hibernate.type.descriptor.jdbc.BooleanJdbcType;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;
import org.hibernate.type.descriptor.jdbc.TimestampJdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

/**
 * Custom SqlDialect Class
 */
public class SQLiteDialect extends Dialect {

    /**
     * SQLITE_MAJOR_VERSION
     */
    private static final int SQLITE_MAJOR_VERSION = 3;
    /**
     *SQLITE_MINOR_VERSION
     */
    private static final int SQLITE_MINOR_VERSION = 45;

    /**
     *  Constructor to initialize SQLiteDialect class.
     */
    public SQLiteDialect() {
        super(DatabaseVersion.make(SQLITE_MAJOR_VERSION, SQLITE_MINOR_VERSION));
    }

    @Override
    public void contributeTypes(final TypeContributions typeContributions,
                                final ServiceRegistry serviceRegistry) {
        typeContributions.getTypeConfiguration().getJdbcTypeRegistry().
                addDescriptor(SqlTypes.VARCHAR, VarcharJdbcType.INSTANCE);
        typeContributions.getTypeConfiguration().getJdbcTypeRegistry().
                addDescriptor(SqlTypes.INTEGER, IntegerJdbcType.INSTANCE);
        typeContributions.getTypeConfiguration().getJdbcTypeRegistry().
                addDescriptor(SqlTypes.BOOLEAN, BooleanJdbcType.INSTANCE);
        typeContributions.getTypeConfiguration().getJdbcTypeRegistry().
                addDescriptor(SqlTypes.TIMESTAMP, TimestampJdbcType.INSTANCE);
        typeContributions.getTypeConfiguration().getJdbcTypeRegistry().
                addDescriptor(SqlTypes.BLOB, BlobJdbcType.DEFAULT);
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new IdentityColumnSupportImpl();
    }


    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public boolean supportsCascadeDelete() {
        return false;
    }
}