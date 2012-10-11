package nl.anwb.hv.lib.liquibase.ext.createdomain.statement;

import liquibase.statement.AbstractSqlStatement;

public class CreateDomainStatement extends AbstractSqlStatement {

    private String schemaName;
    private String domainName;
    private String dataType;
    private Object defaultValue;
    private String check;
    private boolean ifNotExists;
    private boolean nullable;

    public CreateDomainStatement(String schemaName, String domainName, String typeName, 
                                  Object defaultValue, String check, 
                                  boolean ifNotExists, boolean nullable) {
        this.schemaName = schemaName;
        this.domainName = domainName;
        this.dataType = typeName;
        this.defaultValue = defaultValue;
        this.check = check;
        this.ifNotExists = ifNotExists;
        this.nullable = nullable;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getDataType() {
        return dataType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getCheck() {
        return check;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public boolean isNullable() {
        return nullable;
    }

}
