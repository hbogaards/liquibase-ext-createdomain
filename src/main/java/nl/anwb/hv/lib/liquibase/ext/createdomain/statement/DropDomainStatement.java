package nl.anwb.hv.lib.liquibase.ext.createdomain.statement;

import liquibase.statement.AbstractSqlStatement;

public class DropDomainStatement extends AbstractSqlStatement {

    private String schemaName;
    private String domainName;

    public DropDomainStatement(String schemaName, String domainName) {
        this.schemaName = schemaName;
        this.domainName = domainName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getDomainName() {
        return domainName;
    }
}
